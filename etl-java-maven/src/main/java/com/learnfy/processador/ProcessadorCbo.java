package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.logs.LogService;
import com.learnfy.modelo.*;
import com.learnfy.s3.S3Service;
import com.mysql.cj.log.Log;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.*;
public class ProcessadorCbo extends Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final LogService logService;

    public ProcessadorCbo(JdbcTemplate jdbcTemplate, S3Client s3Client, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorCbo", "START", "Iniciando processamento do arquivo.");

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            if (key.endsWith(".xls")) {
                throw new UnsupportedOperationException("Arquivos .xls não são suportados no modo SAX.");
            }

            OPCPackage pkg = OPCPackage.open(inputStream);
            XSSFReader reader = new XSSFReader(pkg);
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            DataFormatter formatter = new DataFormatter();

            final int BATCH_SIZE = 100;
            List<Cbo> batchCbo = new ArrayList<>(BATCH_SIZE);

            // Carrega todas as áreas existentes no banco e as coloca em um Map
            Map<String, Integer> areasCadastradas = new HashMap<>();
            jdbcTemplate.query("SELECT id_area FROM area_tn", rs -> {
                areasCadastradas.put(rs.getString("cbo2002").trim().toLowerCase(), rs.getInt("id_area"));
            });

            Map<String, String> cboGrandesGrupos = new HashMap<>();
            cboGrandesGrupos.put("0", "Militares, Policiais e Bombeiros");
            cboGrandesGrupos.put("1", "Funcionários Públicos");
            cboGrandesGrupos.put("2", "Profissionais das Ciências e Artes");
            cboGrandesGrupos.put("3", "Técnicos de Nível Médio");
            cboGrandesGrupos.put("4", "Trabalhadores de Serviços Administrativos");
            cboGrandesGrupos.put("5", "Vendedores de Comércio em Lojas e Mercados");
            cboGrandesGrupos.put("6", "Agropecuários, Florestais e da Pesca");
            cboGrandesGrupos.put("7", "Industria e Serviços Industriais");
            cboGrandesGrupos.put("8", "Produção de Bens e Serviços Industriais");
            cboGrandesGrupos.put("9", "Trabalhadores de Reparação e Manutenção");


//            Map<String, String> cboListasAreas = new HashMap<>();
//            cboListasAreas.put("0", "Agricultura e Veterinária");
//            cboListasAreas.put("1", "Ciências Sociais, Negócios e Direitos");
//            cboListasAreas.put("2", "Ciências, Matemática e Computação");
//            cboListasAreas.put("3", "Educação");
//            cboListasAreas.put("4", "Engenharia, Produção e Construção");
//            cboListasAreas.put("5", "Humanidade e Artes");
//            cboListasAreas.put("6", "Outros");
//            cboListasAreas.put("7", "Saúde e Bem Estar Social");
//            cboListasAreas.put("8", "Serviços");


            //TODO Insere os dados mokados mesmo, e criar a lógica para colocar a fk_area na tabela de cbo,
            // Além disso, criar a lógica para fazer o depara entre as áreas que estão na tabela e os grupos do cbo
            // Relaxe cara, só precisa fazer essa lógica de depara do select, essa part é na dataviz.

            SheetContentsHandler handler = new SheetContentsHandler() {
                private Cbo cbo;
                private Area area;
                private Empregabilidade empregabilidade;

                @Override
                public void startRow(int rowNum) {
                    if (rowNum == 0) {
                        cbo = null;
                        area = null;
                        return;
                    }
                    cbo = new Cbo();
                    area = new Area();
                    empregabilidade = new Empregabilidade();
                }

                @Override
                public void endRow(int rowNum) {
                    if (cbo != null && area != null) {
                        try {
                            for (Map.Entry<String, String> entry : cboGrandesGrupos.entrySet()) {
                                jdbcTemplate.update("INSERT INTO cbo_tb (codigo_tipo_emprego, descricao) VALUES (?)",
                                        entry.getKey(), entry.getValue());
                            }
                            batchCbo.add(cbo);
                            if (batchCbo.size() == BATCH_SIZE) {
                                enviarBatchCbo(batchCbo);
                                batchCbo.clear();
                            }
                        } catch (Exception e) {
                            logService.registrarLog("CursoCbo", "ProcessadorCbo", "ALERTA",
                                    String.format("Erro na linha %d, não foi possivel inserir os valores de cbo", rowNum + 1));
                        }
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (cbo == null && (empregabilidade == null || area == null)) return;
                    String col = cellReference.replaceAll("\\d", "");
                    int currentCol = colunaParaIndice(col);

                    formattedValue = formattedValue.trim();

                    switch (currentCol) {
                        case 0 -> cbo.setDescricao(tratarTexto(formattedValue));
                        case 1 -> empregabilidade.setCbo2002(tratarTexto(formattedValue));
                        case 2 -> area.setId(parseInt(formattedValue));
                        case 3 -> empregabilidade.setCategoria(tratarTexto(formattedValue).substring(0, 2));
                    }
                }

            };

            XMLReader parser = XMLReaderFactory.createXMLReader();
            XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(
                    reader.getStylesTable(), null, strings, handler, formatter, false);
            parser.setContentHandler(xmlHandler);

            try (InputStream sheet = reader.getSheetsData().next()) {
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            }

            if (!batchCbo.isEmpty()) {
                enviarBatchCbo(batchCbo);
                batchCbo.clear();
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
            logService.registrarLog(key, "ProcessadorCbo", "SUCESSO", "Processamento finalizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorCbo", "CRITICO", "Erro crítico no processamento: " + e.getMessage());
        }
    }

    private int parseInt(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
    }

    public String tratarTexto(String valor) {
        if (valor != null) {

            if (valor.matches(".* - .*")) {
                valor = valor.substring(0, valor.indexOf("-") - 1);
            }
            return valor.toUpperCase();
        }

        return "";
    }

    private int colunaParaIndice(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private void enviarBatchCbo(List<Cbo> cboList) {
        System.out.println("Inserindo " + cboList.size() + " registros no banco.");

        String sqlCbo= "INSERT INTO cbo_tb (fkArea, codigo_tipo_emprego, codigo_divisao_emprego) VALUES (?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sqlCbo, cboList, cboList.size(), (ps, cbo) -> {
                ps.setInt(1, cbo.getFk_area());
                ps.setString(2, cbo.getCodigo_tipo_emprego());
            });
        } catch (Exception e) {
            System.out.println(String.format("Erro ao inserir batch: " + e.getMessage()));
        }
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);
        Processador ProcessadorCbo = new ProcessadorCbo(jdbcTemplate, s3Client, logService);
        try {
            ProcessadorCbo.processar(bucket, "planilhas/dados_cursos/cursos_areas.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Cursos e Áreas, erro: %s", e.getMessage()));
        }
    }
}   