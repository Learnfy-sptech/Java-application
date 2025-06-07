package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.logs.LogService;
import com.learnfy.modelo.Empregabilidade;
import com.learnfy.s3.S3Service;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessadorEmpregabilidade extends Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final String bucketName;
    private final LogService logService;

    public ProcessadorEmpregabilidade(JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) throws Exception {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorEmpregabilidade", "START", "Iniciando processamento do arquivo.");

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

                if (key.endsWith(".xls")) {
                    throw new UnsupportedOperationException("Arquivos .xls não são suportados no modo SAX.");
                }

                Map<String, Integer> siglaUfToId = jdbcTemplate.query(
                        "SELECT sigla, id_uf FROM uf_tb",
                        rs -> {
                            Map<String, Integer> map = new HashMap<>();
                            while (rs.next()) {
                                map.put(rs.getString("sigla").trim(), rs.getInt("id_uf"));
                            }
                            return map;
                        });

                IOUtils.setByteArrayMaxOverride(1_000_000_000);
                OPCPackage pkg = OPCPackage.open(inputStream);
                XSSFReader reader = new XSSFReader(pkg);
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);

                DataFormatter formatter = new DataFormatter();
                final int BATCH_SIZE = 100;
                List<Empregabilidade> batchEmpregabilidade = new ArrayList<>(BATCH_SIZE);

                XSSFSheetXMLHandler.SheetContentsHandler handler = new XSSFSheetXMLHandler.SheetContentsHandler() {
                    private Empregabilidade empregabilidade;
                    private int currentCol = -1;

                    @Override
                    public void startRow(int rowNum) {
                        empregabilidade = (rowNum == 0) ? null : new Empregabilidade();
                    }

                    @Override
                    public void endRow(int rowNum) {

                        if (empregabilidade != null) {
                            Integer fkUf = siglaUfToId.get(empregabilidade.getSiglaUf());
                            Integer fkArea = coletarFkArea(empregabilidade.getCbo2002());

                            if (fkUf == null) {
                                logService.registrarLog(key, "ProcessadorEmpregabilidade", "ALERTA",
                                        String.format("Linha ignorada: Estado não encontrado: '%s'",
                                                empregabilidade.getSiglaUf()));
                                return;
                            } else if (fkArea == null) {
                                logService.registrarLog(key, "ProcessadorEmpregabilidade", "ALERTA",
                                        String.format("Linha ignorada: Área não encontrado: '%s'",
                                                empregabilidade.getSiglaUf()));
                                return;
                            }

                            try {
                                empregabilidade.setFkUf(fkUf);
                                empregabilidade.setFkArea(fkArea);
                                batchEmpregabilidade.add(empregabilidade);
                                if (batchEmpregabilidade.size() == BATCH_SIZE) {
                                    enviarBatch(batchEmpregabilidade);
                                    batchEmpregabilidade.clear();
                                }
                            } catch (Exception e) {
                                logService.registrarLog(key, "ProcessadorCursoOfertado", "ERRO",
                                        "Erro ao adicionar curso no batch: " + e.getMessage());
                            }
                        }


                    }

                    @Override
                    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                        if (empregabilidade == null) return;
                        String col = cellReference.replaceAll("\\d", "");
                        currentCol = colunaParaIndice(col);

                        formattedValue = formattedValue.trim();

                        switch (currentCol) {
                            case 0 -> empregabilidade.setAno(parseInt(formattedValue));
                            case 1 -> empregabilidade.setSiglaUf(tratarTexto(formattedValue));
                            case 2 -> empregabilidade.setCbo2002(tratarTexto(formattedValue));
                            case 3 -> empregabilidade.setCbo2002Descricao(tratarTexto(formattedValue));
                            case 4 -> empregabilidade.setCbo2002DescricaoFamilia(tratarTexto(formattedValue));
                            case 5 -> empregabilidade.setCategoria(tratarTexto(formattedValue));
                            case 6 -> empregabilidade.setGrauInstrucao(tratarTexto(formattedValue));
                            case 7 -> empregabilidade.setSalarioMensal(parseDouble(formattedValue));
                        }
                    }

                    @Override
                    public void headerFooter(String text, boolean isHeader, String tagName) {
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

            if (!batchEmpregabilidade.isEmpty()) {
                enviarBatch(batchEmpregabilidade);
                batchEmpregabilidade.clear();
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
            logService.registrarLog(key, "ProcessadorEmpregabilidade", "SUCESSO", "Processamento finalizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorEmpregabilidade", "CRITICO", "Erro ao processar planilha: " + e.getMessage());
        }
    }

    private int parseInt(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
    }

    private double parseDouble(String value) {
        return value != null && !value.isEmpty() ? Double.parseDouble(value) : 0.0;
    }

    private String tratarTexto(String valor) {
        return valor != null ? valor.trim().toUpperCase() : "";
    }

    private void enviarBatch(List<Empregabilidade> dadosEmpregabilidadeList) {
        System.out.println("Inserindo " + dadosEmpregabilidadeList.size() + " registros no banco.");

        String sql = "INSERT INTO dados_empregabilidade_tb (ano, sigla_uf, cbo_2002, cbo_2002_descricao, cbo_2002_descricao_familia," +
                "categoria, grau_instrucao, salario_mensal, fk_area, fk_uf)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, dadosEmpregabilidadeList, dadosEmpregabilidadeList.size(), (ps, dados) -> {
            ps.setInt(1, dados.getAno() != null ? dados.getAno() : 0);
            ps.setString(2, dados.getSiglaUf() != null ? dados.getSiglaUf() : "");
            ps.setString(3, dados.getCbo2002() != null ? dados.getCbo2002() : "");
            ps.setString(4, dados.getCbo2002Descricao() != null ? dados.getCbo2002Descricao() : "");
            ps.setString(5, dados.getCbo2002DescricaoFamilia() != null ? dados.getCbo2002DescricaoFamilia() : "");
            ps.setString(6, dados.getCategoria() != null ? dados.getCategoria() : "");
            ps.setString(7, dados.getGrauInstrucao() != null ? dados.getGrauInstrucao() : "");
            ps.setDouble(8, dados.getSalarioMensal() != null ? dados.getSalarioMensal() : 0.0);
            ps.setInt(9, dados.getFkArea());
            ps.setInt(10, dados.getFkUf());
        });
        logService.registrarLog("BatchEmpregabilidade", "ProcessadorEmpregabilidade", "SUCESSO", "Sucesso na inserção de Batch");
    }

    private int colunaParaIndice(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private Integer coletarFkArea(String cbo) {
        switch (cbo.charAt(0)) {
            case '0':
                return 1;
            case '1':
                return 8;
            case '2':
                return 3;
            case '3':
                return 4;
            case '4':
                return 9;
            case '5':
                return 2;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 5;
            case '9':
                return 2;
        }
        return null;
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        Processador processadorEmpregabilidade = new ProcessadorEmpregabilidade(jdbcTemplate, s3Client, bucket, logService);
        try {
            processadorEmpregabilidade.processar(bucket, "planilhas/dados_empregabilidade/empregabilidade.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados de Empregabilidade, erro: %s", e.getMessage()));
        }
    }
}
