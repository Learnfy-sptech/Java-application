package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.logs.LogService;
import com.learnfy.modelo.Ies;
import com.learnfy.s3.S3Service;
import org.apache.commons.math3.analysis.function.Log;
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
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.*;

public class ProcessadorIes implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final LogService logService;

    public ProcessadorIes(JdbcTemplate jdbcTemplate, S3Client s3Client, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorIes", "START", "Iniciando processamento do arquivo.");

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            if (key.endsWith(".xls")) {
                throw new UnsupportedOperationException("Arquivos .xls não são suportados no modo SAX.");
            }

            Map<String, Integer> cacheMunicipios = carregarMunicipios();

            OPCPackage pkg = OPCPackage.open(inputStream);
            XSSFReader reader = new XSSFReader(pkg);
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);

            DataFormatter formatter = new DataFormatter();
            final int BATCH_SIZE = 100;
            List<Ies> batchIes = new ArrayList<>(BATCH_SIZE);

            SheetContentsHandler handler = new SheetContentsHandler() {
                private Ies ies;

                @Override
                public void startRow(int rowNum) {
                    if (rowNum == 0) {
                        ies = null; // Ignora cabeçalho
                        return;
                    }
                    ies = new Ies();
                }

                @Override
                public void endRow(int rowNum) {
                    if (ies != null) {
                        Integer fkMunicipio = cacheMunicipios.get(ies.getNomeMunicipio());
                        if (fkMunicipio != null) {
                            ies.setFkMunicipio(fkMunicipio);
                        } else {
                            System.out.println(String.format("Não foi possível obter a chave estrangeira do município %s", ies.getNomeMunicipio()));
                        }

                        batchIes.add(ies);
                        if (batchIes.size() == BATCH_SIZE) {
                            enviarBatch(batchIes);
                            batchIes.clear();
                        }
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (ies == null) return;
                    String col = cellReference.replaceAll("\\d", "");
                    int currentCol = colunaParaIndice(col);

                    formattedValue = formattedValue.trim();

                    switch (currentCol) {
                        case 0 -> ies.setNomeMunicipio(tratarTexto(formattedValue));
                        case 1 -> ies.setOrganizacaoAcademica(tratarTexto(formattedValue));
                        case 2 -> ies.setNome(tratarTexto(formattedValue));
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

            if (!batchIes.isEmpty()) {
                enviarBatch(batchIes);
                batchIes.clear();
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorIes", "CRITICO", "Erro ao processar a planilha");
        }
    }

    private String tratarTexto(String valor) {
        return valor != null ? valor.trim().toUpperCase() : "";
    }

    private int parseInt(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
    }

    private int colunaParaIndice(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private void enviarBatch(List<Ies> iesList) {
        System.out.println("Inserindo " + iesList.size() + " registros no banco.");

        String sql = "INSERT INTO ies_tb (fk_municipio, organizacao_academica, nome) VALUES (?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, iesList, iesList.size(), (ps, ies) -> {
                ps.setInt(1, ies.getFkMunicipio());
                ps.setString(2, ies.getOrganizacaoAcademica());
                ps.setString(3, ies.getNome());
            });
            logService.registrarLog("BatchIES","ProcessadorIes", "SUCESSO", "Sucesso na inserção de Batch");
        } catch (Exception e) {
            System.out.println("Erro ao inserir batch: " + e.getMessage());
            logService.registrarLog("BatchIES","ProcessadorIes", "CRITICO", "Erro ao inserir batch: " + e.getMessage());
        }
    }

    private Map<String, Integer> carregarMunicipios() {
        System.out.println("Carregando cache de municípios...");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id_municipio, nome FROM municipio_tb");

        Map<String, Integer> cache = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String nome = (String) row.get("nome");
            Integer id = (Integer) row.get("id_municipio");
            cache.put(nome, id);
        }

        System.out.println("✔ Cache de municípios carregado com " + cache.size() + " entradas.");
        return cache;
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        Processador processadorIes = new ProcessadorIes(jdbcTemplate, s3Client, logService);
        try {
            processadorIes.processar(bucket, "planilhas/dados_cursos/instituicoes_ensino.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados das Instituições de Ensino, erro: %s", e.getMessage()));
        }
    }
}
