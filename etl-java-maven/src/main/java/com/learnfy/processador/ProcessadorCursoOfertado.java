package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.modelo.CursoOfertado;
import com.learnfy.s3.S3Service;
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

    public ProcessadorCursoOfertado(JdbcTemplate jdbcTemplate, S3Client s3Client) {
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

                        try {
                            curso.setFkIes(jdbcTemplate.queryForObject(
                                    "SELECT id_ies FROM ies_tb WHERE nome = ?",
                                    Integer.class,
                                    curso.getNomeIes()
                            ));
                            curso.setFkCurso(jdbcTemplate.queryForObject(
                                    "SELECT id_curso FROM curso_tb WHERE nome = ?",
                                    Integer.class,
                                    curso.getNomeCurso()
                            ));
                        } catch (Exception e) {
                            System.out.println(String.format("Não foi possível buscar as chave estrangeiras para a linha %d", rowNum));
                        }

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

                    formattedValue = formattedValue.trim();

                    switch (currentCol) {
                        case 0 -> curso.setAno(parseInt(formattedValue));
                        case 1 -> curso.setNomeIes(formattedValue); // Para depois buscar a fkIes
                        case 2 -> curso.setNomeCurso(formattedValue); // Para depois buscar a fkCurso
                        case 3 -> curso.setModalidadeEnsino(parseInt(formattedValue));
                        case 4 -> curso.setQtdVagas(parseInt(formattedValue));
                        case 5 -> curso.setQtdVagasDiurno(parseInt(formattedValue));
                        case 6 -> curso.setQtdVagasNoturno(parseInt(formattedValue));
                        case 7 -> curso.setQtdVagasEad(parseInt(formattedValue));
                        case 8 -> curso.setQtdIncritos(parseInt(formattedValue));
                        case 9 -> curso.setQtdIncritosDiurno(parseInt(formattedValue));
                        case 10 -> curso.setQtdIncritosNoturno(parseInt(formattedValue));
                        case 11 -> curso.setQtdIncritosEad(parseInt(formattedValue));
                        case 12 -> curso.setQtdConcluintes(parseInt(formattedValue));
                        case 13 -> curso.setQtdConcluintesDiurno(parseInt(formattedValue));
                        case 14 -> curso.setQtdConcluintesNoturno(parseInt(formattedValue));
                        case 15 -> curso.setQtdIngressantesRedePublica(parseInt(formattedValue));
                        case 16 -> curso.setQtdIngressantesRedePrivada(parseInt(formattedValue));
                        case 17 -> curso.setQtdConcluintesRedePublica(parseInt(formattedValue));
                        case 18 -> curso.setQtdConcluintesRedePrivada(parseInt(formattedValue));
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

        String sql = "INSERT INTO curso_ies (\n" +
                "    ano, fk_ies, fk_curso, modalidade_ensino,\n" +
                "    qtd_vagas, qtd_vagas_diurno, qtd_vagas_noturno, qtd_vagas_ead,\n" +
                "    qtd_inscritos, qtd_inscritos_diurno, qtd_inscritos_noturno, qtd_inscritos_ead,\n" +
                "    qtd_concluintes, qtd_concluintes_diurno, qtd_concluintes_noturno,\n" +
                "    qtd_ingressantes_rede_publica, qtd_ingressantes_rede_privada,\n" +
                "    qtd_concluintes_rede_publica, qtd_concluintes_rede_privada\n" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n";


        try {
            jdbcTemplate.batchUpdate(sql, cursoOfertados, cursoOfertados.size(), (ps, cursoOfertado) -> {
                ps.setInt(2, cursoOfertado.getFkIes() != null ? cursoOfertado.getFkIes() : 0);
                ps.setInt(1, cursoOfertado.getAno() != null ? cursoOfertado.getAno() : 0);
                ps.setInt(3, cursoOfertado.getFkCurso() != null ? cursoOfertado.getFkCurso() : 0);
                ps.setInt(4, cursoOfertado.getModalidadeEnsino() != null ? cursoOfertado.getModalidadeEnsino() : 0);
                ps.setInt(5, cursoOfertado.getQtdVagas() != null ? cursoOfertado.getQtdVagas() : 0);
                ps.setInt(6, cursoOfertado.getQtdVagasDiurno() != null ? cursoOfertado.getQtdVagasDiurno() : 0);
                ps.setInt(7, cursoOfertado.getQtdVagasNoturno() != null ? cursoOfertado.getQtdVagasNoturno() : 0);
                ps.setInt(8, cursoOfertado.getQtdVagasEad() != null ? cursoOfertado.getQtdVagasEad() : 0);
                ps.setInt(9, cursoOfertado.getQtdIncritos() != null ? cursoOfertado.getQtdIncritos() : 0);
                ps.setInt(10, cursoOfertado.getQtdIncritosDiurno() != null ? cursoOfertado.getQtdIncritosDiurno() : 0);
                ps.setInt(11, cursoOfertado.getQtdIncritosNoturno() != null ? cursoOfertado.getQtdIncritosNoturno() : 0);
                ps.setInt(12, cursoOfertado.getQtdIncritosEad() != null ? cursoOfertado.getQtdIncritosEad() : 0);
                ps.setInt(13, cursoOfertado.getQtdConcluintes() != null ? cursoOfertado.getQtdConcluintes() : 0);
                ps.setInt(14, cursoOfertado.getQtdConcluintesDiurno() != null ? cursoOfertado.getQtdConcluintesDiurno() : 0);
                ps.setInt(15, cursoOfertado.getQtdConcluintesNoturno() != null ? cursoOfertado.getQtdConcluintesNoturno() : 0);
                ps.setInt(16, cursoOfertado.getQtdIngressantesRedePublica() != null ? cursoOfertado.getQtdIngressantesRedePublica() : 0);
                ps.setInt(17, cursoOfertado.getQtdIngressantesRedePrivada() != null ? cursoOfertado.getQtdIngressantesRedePrivada() : 0);
                ps.setInt(18, cursoOfertado.getQtdConcluintesRedePublica() != null ? cursoOfertado.getQtdConcluintesRedePublica() : 0);
                ps.setInt(19, cursoOfertado.getQtdConcluintesRedePrivada() != null ? cursoOfertado.getQtdConcluintesRedePrivada() : 0);
            });
        } catch (Exception e) {
            System.err.println("Erro ao inserir batch: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();

        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        Processador processadorCursoOfertado = new ProcessadorCursoOfertado(jdbcTemplate, s3Client);
        try {
            processadorCursoOfertado.processar(bucket, "planilhas/dados_cursos/cursos_ofertados.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Cursos Ofertados, erro: %s", e.getMessage()));
        }
    }
}
