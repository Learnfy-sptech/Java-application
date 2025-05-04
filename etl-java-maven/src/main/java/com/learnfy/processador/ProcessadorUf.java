package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.logs.LogService;
import com.learnfy.modelo.Uf;
import com.learnfy.s3.S3Service;
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
import java.util.ArrayList;
import java.util.List;

public class ProcessadorUf implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final LogService logService;

    public ProcessadorUf(JdbcTemplate jdbcTemplate, S3Client s3Client, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) throws Exception {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorUf", "START", "Iniciando processamento do arquivo");

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
            List<Uf> batchUfs = new ArrayList<>(BATCH_SIZE);

            SheetContentsHandler handler = new SheetContentsHandler() {
                private Uf uf;

                @Override
                public void startRow(int rowNum) {
                    if (rowNum == 0) {
                        uf = null; // Ignora cabeçalho
                        return;
                    }
                    uf = new Uf();
                }

                @Override
                public void endRow(int rowNum) {
                    if (uf != null) {
                        batchUfs.add(uf);
                        if (batchUfs.size() == BATCH_SIZE) {
                            enviarBatch(batchUfs);
                            batchUfs.clear();
                        }
                    } else {
                        logService.registrarLog(key, "ProcessadorUF", "ALERTA", "Erro ao processar linha: " + rowNum);
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (uf == null) return;
                    String col = cellReference.replaceAll("\\d", "");
                    int currentCol = colunaParaIndice(col);

                    switch (currentCol) {
                        case 0 -> uf.setSigla(tratarTexto(formattedValue));
                        case 1 -> uf.setNome(tratarTexto(formattedValue));
                        case 2 -> uf.setRegiao(tratarTexto(formattedValue));
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

            if (!batchUfs.isEmpty()) {
                enviarBatch(batchUfs);
                batchUfs.clear();
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
            logService.registrarLog(key, "ProcessadorUF", "SUCESSO", "Leitura da planilha finalizada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorUf", "CRITICO", "Erro ao processar a planilha: " + e.getMessage());
        }
    }

    private int parseInt(String value) {
        return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
    }

    private String tratarTexto(String valor) {
        return valor != null ? valor.trim().toUpperCase() : "";
    }

    private int colunaParaIndice(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private void enviarBatch(List<Uf> ufs) {
        System.out.println("Inserindo " + ufs.size() + " registros no banco.");

        String sql = "INSERT INTO uf_tb (sigla, nome, regiao) VALUES (?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, ufs, ufs.size(), (ps, uf) -> {
                ps.setString(1, uf.getSigla());
                ps.setString(2, uf.getNome());
                ps.setString(3, uf.getRegiao());
            });
            logService.registrarLog("BatchInsert", "ProcessadorUf", "SUCESSO", "Batch inserido com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao inserir batch: " + e.getMessage());
            logService.registrarLog("BatchInsert", "ProcessadorUf", "CRITICO", "Erro ao inserir batch: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        Processador processadorUf = new ProcessadorUf(jdbcTemplate, s3Client, logService);
        try {
            processadorUf.processar(bucket, "planilhas/dados_cursos/estados.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Estados, erro: %s", e.getMessage()));
        }
    }
}
