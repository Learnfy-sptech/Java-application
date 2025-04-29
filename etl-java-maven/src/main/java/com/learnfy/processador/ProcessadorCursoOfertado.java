package com.learnfy.processador;

import com.learnfy.modelo.CursoOfertado;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ProcessadorCursoOfertado implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final String bucketName;

    public ProcessadorCursoOfertado(JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
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
            List<CursoOfertado> batchCursoOfertados = new ArrayList<>(BATCH_SIZE);

            SheetContentsHandler handler = new SheetContentsHandler() {
                private CursoOfertado curso;
                private int currentCol = -1;

                @Override
                public void startRow(int rowNum) {
                    if (rowNum == 0) {
                        // Ignora o cabeçalho
                        curso = null;
                        return;
                    }
                    curso = new CursoOfertado();
                }

                @Override
                public void endRow(int rowNum) {
                    if (curso != null) {
                        batchCursoOfertados.add(curso);
                        if (batchCursoOfertados.size() == BATCH_SIZE) {
                            enviarBatch(batchCursoOfertados);
                            batchCursoOfertados.clear();
                        }
                    }
                }

                @Override
                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                    if (curso == null) return;
                    String col = cellReference.replaceAll("\\d", "");
                    currentCol = colunaParaIndice(col);

                    switch (currentCol) {
                        case 0 -> curso.setAno(parseInt(formattedValue));
                        case 1 -> curso.setSiglaUf(formattedValue);
                        case 2 -> curso.setIdMunicipio(parseInt(formattedValue));
                        case 3 -> curso.setRede(formattedValue);
                        case 4 -> curso.setIdIes(parseInt(formattedValue));
                        case 5 -> curso.setNomeCurso(formattedValue);
                        case 6 -> curso.setNomeArea(formattedValue);
                        case 7 -> curso.setGrauAcademico(parseInt(formattedValue));
                        case 8 -> curso.setModalidadeEnsino(parseInt(formattedValue));
                        case 9 -> curso.setQtdVagas(parseInt(formattedValue));
                        case 10 -> curso.setQtdVagasDiurno(parseInt(formattedValue));
                        case 11 -> curso.setQtdVagasNoturno(parseInt(formattedValue));
                        case 12 -> curso.setQtdVagasEad(parseInt(formattedValue));
                        case 13 -> curso.setQtdIncritos(parseInt(formattedValue));
                        case 14 -> curso.setQtdIncritosDiurno(parseInt(formattedValue));
                        case 15 -> curso.setQtdIncritosNoturno(parseInt(formattedValue));
                        case 16 -> curso.setQtdIncritosEad(parseInt(formattedValue));
                        case 17 -> curso.setQtdConcluintesDiurno(parseInt(formattedValue));
                        case 18 -> curso.setQtdConcluintesNoturno(parseInt(formattedValue));
                        case 19 -> curso.setQtdIngressantesRedePublica(parseInt(formattedValue));
                        case 20 -> curso.setQtdIngressantesRedePrivada(parseInt(formattedValue));
                        case 21 -> curso.setQtdConcluintesRedePublica(parseInt(formattedValue));
                        case 22 -> curso.setQtdConcluintesRedePrivada(parseInt(formattedValue));
                    }
                }

                @Override
                public void headerFooter(String text, boolean isHeader, String tagName) {
                    // Ignorar cabeçalho e rodapé
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

            if (!batchCursoOfertados.isEmpty()) {
                enviarBatch(batchCursoOfertados);
                batchCursoOfertados.clear();
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

    private void enviarBatch(List<CursoOfertado> cursoOfertados) {
        System.out.println("Inserindo " + cursoOfertados.size() + " registros no banco.");

        String sql = "INSERT INTO curso (\n" +
                "    ano, sigla_uf, id_municipio, rede, id_ies, nome_curso, nome_area, grau_academico,\n" +
                "    modalidade_ensino, qtd_vagas, qtd_vagas_diurno, qtd_vagas_noturno, qtd_vagas_ead,\n" +
                "    qtd_inscritos, qtd_inscritos_diurno, qtd_inscritos_noturno, qtd_inscritos_ead,\n" +
                "    qtd_concluintes_diurno, qtd_concluintes_noturno, qtd_ingressantes_rede_publica,\n" +
                "    qtd_ingressantes_rede_privada, qtd_concluintes_rede_publica, qtd_concluintes_rede_privada\n" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, cursoOfertados, cursoOfertados.size(), (ps, cursoOfertado) -> {
                ps.setInt(1, cursoOfertado.getAno() != null ? cursoOfertado.getAno() : 0);
                ps.setString(2, cursoOfertado.getSiglaUf() != null ? cursoOfertado.getSiglaUf() : "");
                ps.setInt(3, cursoOfertado.getIdMunicipio() != null ? cursoOfertado.getIdMunicipio() : 0);
                ps.setString(4, cursoOfertado.getRede() != null ? cursoOfertado.getRede() : "");
                ps.setInt(5, cursoOfertado.getIdIes() != null ? cursoOfertado.getIdIes() : 0);
                ps.setString(6, cursoOfertado.getNomeCurso() != null ? cursoOfertado.getNomeCurso() : "");
                ps.setString(7, cursoOfertado.getNomeArea() != null ? cursoOfertado.getNomeArea() : "");
                ps.setInt(8, cursoOfertado.getGrauAcademico() != null ? cursoOfertado.getGrauAcademico() : 0);
                ps.setInt(9, cursoOfertado.getModalidadeEnsino() != null ? cursoOfertado.getModalidadeEnsino() : 0);
                ps.setInt(10, cursoOfertado.getQtdVagas() != null ? cursoOfertado.getQtdVagas() : 0);
                ps.setInt(11, cursoOfertado.getQtdVagasDiurno() != null ? cursoOfertado.getQtdVagasDiurno() : 0);
                ps.setInt(12, cursoOfertado.getQtdVagasNoturno() != null ? cursoOfertado.getQtdVagasNoturno() : 0);
                ps.setInt(13, cursoOfertado.getQtdVagasEad() != null ? cursoOfertado.getQtdVagasEad() : 0);
                ps.setInt(14, cursoOfertado.getQtdIncritos() != null ? cursoOfertado.getQtdIncritos() : 0);
                ps.setInt(15, cursoOfertado.getQtdIncritosDiurno() != null ? cursoOfertado.getQtdIncritosDiurno() : 0);
                ps.setInt(16, cursoOfertado.getQtdIncritosNoturno() != null ? cursoOfertado.getQtdIncritosNoturno() : 0);
                ps.setInt(17, cursoOfertado.getQtdIncritosEad() != null ? cursoOfertado.getQtdIncritosEad() : 0);
                ps.setInt(18, cursoOfertado.getQtdConcluintesDiurno() != null ? cursoOfertado.getQtdConcluintesDiurno() : 0);
                ps.setInt(19, cursoOfertado.getQtdConcluintesNoturno() != null ? cursoOfertado.getQtdConcluintesNoturno() : 0);
                ps.setInt(20, cursoOfertado.getQtdIngressantesRedePublica() != null ? cursoOfertado.getQtdIngressantesRedePublica() : 0);
                ps.setInt(21, cursoOfertado.getQtdIngressantesRedePrivada() != null ? cursoOfertado.getQtdIngressantesRedePrivada() : 0);
                ps.setInt(22, cursoOfertado.getQtdConcluintesRedePublica() != null ? cursoOfertado.getQtdConcluintesRedePublica() : 0);
                ps.setInt(23, cursoOfertado.getQtdConcluintesRedePrivada() != null ? cursoOfertado.getQtdConcluintesRedePrivada() : 0);
            });
        } catch (Exception e) {
            System.err.println("Erro ao inserir batch: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
