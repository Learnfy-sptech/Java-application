package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.logs.LogService;
import com.learnfy.modelo.Municipio;
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
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.*;

public class ProcessadorMunicipio implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final LogService logService;

    public ProcessadorMunicipio(JdbcTemplate jdbcTemplate, S3Client s3Client, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) throws Exception {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorMunicipio", "START", "Iniciando processamento do arquivo.");

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            if (key.endsWith(".xls")) {
                throw new UnsupportedOperationException("Arquivos .xls não são suportados no modo SAX.");
            }

            Map<String, Integer> cacheUf = carregarUfs();

            OPCPackage pkg = OPCPackage.open(inputStream);
            XSSFReader reader = new XSSFReader(pkg);
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);

            DataFormatter formatter = new DataFormatter();
            final int BATCH_SIZE = 100;
            List<Municipio> batchMunicipio = new ArrayList<>(BATCH_SIZE);

            SheetContentsHandler handler = new SheetContentsHandler() {
                private Municipio municipio;

                @Override
                public void startRow(int rowNum) {
                    if (rowNum == 0) {
                        municipio = null; // Ignora cabeçalho
                        return;
                    }
                    municipio = new Municipio();
                }

                @Override
                public void endRow(int rowNum) {
                    if (municipio != null) {
                        Integer fkUf = cacheUf.get(municipio.getSiglaUf());
                        if (fkUf != null) {
                            municipio.setFkUf(fkUf);
                            batchMunicipio.add(municipio);
                            if (batchMunicipio.size() == BATCH_SIZE) {
                                enviarBatch(batchMunicipio);
                                batchMunicipio.clear();
                            }
                        } else {
                            System.out.printf("Uf não encontrada para sigla: %s (linha %d)%n", municipio.getSiglaUf(), rowNum);
                            logService.registrarLog(key, "ProcessadorMunicipio", "ALERTA", "Uf não encontrada para sigla: " + municipio.getSiglaUf());
                        }
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (municipio == null) return;
                    String col = cellReference.replaceAll("\\d", "");
                    int currentCol = colunaParaIndice(col);

                    switch (currentCol) {
                        case 0 -> municipio.setNome(tratarTexto(formattedValue));
                        case 1 -> municipio.setSiglaUf(tratarTexto(formattedValue));
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

            if (!batchMunicipio.isEmpty()) {
                enviarBatch(batchMunicipio);
                batchMunicipio.clear();
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
            logService.registrarLog(key, "ProcessadorMunicipio", "SUCESSO", "Leitura da planilha finalizada com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorMunicipio", "CRITICO", "Erro ao processar a planilha: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int colunaParaIndice(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private void enviarBatch(List<Municipio> municipios) {
        System.out.println("Inserindo " + municipios.size() + " registros no banco.");

        String sql = "INSERT INTO municipio_tb (nome, fk_uf) VALUES (?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, municipios, municipios.size(), (ps, municipio) -> {
                ps.setString(1, municipio.getNome());
                ps.setInt(2, municipio.getFkUf());
            });
            logService.registrarLog("BatchInsert", "ProcessadorMunicipio", "SUCESSO", "Batch inserido com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao inserir batch: " + e.getMessage());
            logService.registrarLog("BatchInsert", "ProcessadorMunicipio", "CRITICO", "Erro ao inserir batch: " + e.getMessage());
        }
    }

    private int parseInt(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
    }

    private String tratarTexto(String valor) {
        return valor != null ? valor.trim().toUpperCase() : "";
    }

    private Map<String, Integer> carregarUfs() {
        System.out.println("Carregando cache de Ufs...");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id_uf, sigla FROM uf_tb");

        Map<String, Integer> cache = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String sigla = (String) row.get("sigla");
            Integer id = (Integer) row.get("id_uf");
            cache.put(sigla, id);
        }
        System.out.println("✔ Cache de UFs carregado com " + cache.size() + " entradas.");
        return cache;
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        Processador processadorMunicipio = new ProcessadorMunicipio(jdbcTemplate, s3Client, logService);
        try {
            processadorMunicipio.processar(bucket, "planilhas/dados_cursos/municipios.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Municípios, erro: %s", e.getMessage()));
        }
    }
}
