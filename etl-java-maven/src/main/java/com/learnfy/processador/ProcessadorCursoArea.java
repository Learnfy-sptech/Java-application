package com.learnfy.processador;

import com.learnfy.modelo.Area;
import com.learnfy.modelo.Curso;
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

public class ProcessadorCursoArea implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;

    public ProcessadorCursoArea(JdbcTemplate jdbcTemplate, S3Client s3Client) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
    }

    @Override
    public void processar(String bucket, String key) throws Exception {
        System.out.println("Iniciando processamento do arquivo: " + key);

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
            List<Curso> batchCurso = new ArrayList<>(BATCH_SIZE);

            // Carrega todas as áreas existentes no banco e as coloca em um Map
            Map<String, Integer> areasCadastradas = new HashMap<>();
            jdbcTemplate.query("SELECT id_area, nome FROM area_tb", rs -> {
                areasCadastradas.put(rs.getString("nome").trim().toLowerCase(), rs.getInt("id_area"));
            });

            SheetContentsHandler handler = new SheetContentsHandler() {
                private Curso curso;
                private Area area;

                @Override
                public void startRow(int rowNum) {
                    if (rowNum == 0) {
                        curso = null;
                        area = null;
                        return;
                    }
                    curso = new Curso();
                    area = new Area();
                }

                @Override
                public void endRow(int rowNum) {
                    if (curso != null && area != null) {
                        String nomeArea = area.getNome().trim().toLowerCase();

                        if (!areasCadastradas.containsKey(nomeArea)) {
                            jdbcTemplate.update("INSERT INTO area_tb (nome) VALUES (?)", area.getNome());
                            Integer idArea = jdbcTemplate.queryForObject("SELECT id_area FROM area_tb WHERE nome = ?", Integer.class, area.getNome());
                            areasCadastradas.put(nomeArea, idArea);
                            curso.setFkArea(idArea);
                        } else {
                            curso.setFkArea(areasCadastradas.get(nomeArea));
                        }

                        batchCurso.add(curso);
                        if (batchCurso.size() == BATCH_SIZE) {
                            enviarBatchCurso(batchCurso);
                            batchCurso.clear();
                        }
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (curso == null && area == null) return;
                    String col = cellReference.replaceAll("\\d", "");
                    int currentCol = colunaParaIndice(col);

                    formattedValue = formattedValue.trim();

                    switch (currentCol) {
                        case 0 -> curso.setNomeCurso(formattedValue);
                        case 1 -> curso.setGrauAcademico(parseInt(formattedValue));
                        case 2 -> area.setNome(formattedValue);
                    }
                }

                @Override
                public void headerFooter(String text, boolean isHeader, String tagName) {
                    // Ignora cabeçalho e rodapé
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

            if (!batchCurso.isEmpty()) {
                enviarBatchCurso(batchCurso);
                batchCurso.clear();
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
        }
    }

    private int parseInt(String value) {
        try {
            return value != null && !value.isEmpty() ? (int) Double.parseDouble(value) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int colunaParaIndice(String col) {
        int index = 0;
        for (char c : col.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private void enviarBatchCurso(List<Curso> cursoList) {
        System.out.println("Inserindo " + cursoList.size() + " registros no banco.");

        String sqlCurso = "INSERT INTO curso_tb (fk_area, nome, grau_academico) VALUES (?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sqlCurso, cursoList, cursoList.size(), (ps, curso) -> {
                ps.setInt(1, curso.getFkArea());
                ps.setString(2, curso.getNomeCurso() != null ? curso.getNomeCurso() : "");
                ps.setInt(3, curso.getGrauAcademico() != null ? curso.getGrauAcademico() : 0);
            });
        } catch (Exception e) {
            System.err.println("Erro ao inserir batch: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
