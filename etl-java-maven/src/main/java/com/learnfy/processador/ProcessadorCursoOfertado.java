package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.logs.LogService;
import com.learnfy.modelo.CursoOfertado;
import com.learnfy.s3.S3Service;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ProcessadorCursoOfertado extends Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final LogService logService;

    public ProcessadorCursoOfertado(JdbcTemplate jdbcTemplate, S3Client s3Client, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorCursoOfertado", "START", "Iniciando processamento do arquivo.");

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            if (key.endsWith(".xls")) {
                throw new UnsupportedOperationException("Arquivos .xls não são suportados no modo SAX.");
            }

            // Pré-carrega as FKs em Mapas
            Map<String, Integer> nomeIesToId = jdbcTemplate.query(
                    "SELECT nome, id_ies FROM ies_tb",
                    rs -> {
                        Map<String, Integer> map = new HashMap<>();
                        while (rs.next()) {
                            map.put(rs.getString("nome").trim(), rs.getInt("id_ies"));
                        }
                        return map;
                    });

            Map<String, Integer> nomeCursoToId = jdbcTemplate.query(
                    "SELECT nome, id_curso FROM curso_tb",
                    rs -> {
                        Map<String, Integer> map = new HashMap<>();
                        while (rs.next()) {
                            map.put(rs.getString("nome").trim(), rs.getInt("id_curso"));
                        }
                        return map;
                    });

            IOUtils.setByteArrayMaxOverride(1_000_000_000);
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
                    curso = (rowNum == 0) ? null : new CursoOfertado();
                }

                @Override
                public void endRow(int rowNum) {

                    if (curso != null) {
                        Integer fkIes = nomeIesToId.get(curso.getNomeIes());
                        Integer fkCurso = nomeCursoToId.get(curso.getNomeCurso());

                        if (fkIes == null || fkCurso == null) {
                            logService.registrarLog(key, "ProcessadorCursoOfertado", "ALERTA",
                                    String.format("Linha ignorada: IES ou Curso não encontrado: '%s' | '%s'",
                                            curso.getNomeIes(), curso.getNomeCurso()));
                            return;
                        }

                        try {
                            curso.setFkIes(fkIes);
                            curso.setFkCurso(fkCurso);
                            batchCursoOfertados.add(curso);
                            if (batchCursoOfertados.size() == BATCH_SIZE) {
                                enviarBatch(batchCursoOfertados);
                                batchCursoOfertados.clear();
                            }
                        } catch (Exception e) {
                            logService.registrarLog(key, "ProcessadorCursoOfertado", "ERRO",
                                    "Erro ao adicionar curso no batch: " + e.getMessage());
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
                        case 1 -> curso.setNomeIes(tratarTexto(formattedValue));
                        case 2 -> curso.setNomeCurso(tratarTexto(formattedValue));
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
            }

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorCursoOfertado", "CRITICO", "Erro ao processar planilha: " + e.getMessage());
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

    private void enviarBatch(List<CursoOfertado> cursoOfertados) {
        System.out.println("Inserindo " + cursoOfertados.size() + " registros no banco.");

        String sql = "INSERT INTO curso_ofertado_tb (\n" +
                "    ano, fk_ies, fk_curso, modalidade_ensino,\n" +
                "    qtd_vagas, qtd_vagas_diurno, qtd_vagas_noturno, qtd_vagas_ead,\n" +
                "    qtd_incritos, qtd_incritos_diurno, qtd_incritos_noturno, qtd_incritos_ead,\n" +
                "    qtd_concluintes, qtd_concluintes_diurno, qtd_concluintes_noturno,\n" +
                "    qtd_ingressantes_rede_publica, qtd_ingressantes_rede_privada,\n" +
                "    qtd_concluintes_rede_publica, qtd_concluintes_rede_privada\n" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n";

        try {
            jdbcTemplate.batchUpdate(sql, cursoOfertados, cursoOfertados.size(), (ps, cursoOfertado) -> {
                ps.setInt(1, cursoOfertado.getAno());
                ps.setInt(2, cursoOfertado.getFkIes());
                ps.setInt(3, cursoOfertado.getFkCurso());
                ps.setInt(4, cursoOfertado.getModalidadeEnsino());
                ps.setInt(5, cursoOfertado.getQtdVagas());
                ps.setInt(6, cursoOfertado.getQtdVagasDiurno());
                ps.setInt(7, cursoOfertado.getQtdVagasNoturno());
                ps.setInt(8, cursoOfertado.getQtdVagasEad());
                ps.setInt(9, cursoOfertado.getQtdIncritos());
                ps.setInt(10, cursoOfertado.getQtdIncritosDiurno());
                ps.setInt(11, cursoOfertado.getQtdIncritosNoturno());
                ps.setInt(12, cursoOfertado.getQtdIncritosEad());
                ps.setInt(13, cursoOfertado.getQtdConcluintes());
                ps.setInt(14, cursoOfertado.getQtdConcluintesDiurno());
                ps.setInt(15, cursoOfertado.getQtdConcluintesNoturno());
                ps.setInt(16, cursoOfertado.getQtdIngressantesRedePublica());
                ps.setInt(17, cursoOfertado.getQtdIngressantesRedePrivada());
                ps.setInt(18, cursoOfertado.getQtdConcluintesRedePublica());
                ps.setInt(19, cursoOfertado.getQtdConcluintesRedePrivada());
            });
            logService.registrarLog("BatchCursoOfertado", "ProcessadorCursoOfertado", "INFO",
                    "Batch de " + cursoOfertados.size() + " cursos ofertados inserido com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao inserir batch: " + e.getMessage());
            logService.registrarLog("BatchCursoOfertado", "ProcessadorCursoOfertado", "ERRO",
                    "Erro ao inserir batch: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        Processador processadorCursoOfertado = new ProcessadorCursoOfertado(jdbcTemplate, s3Client, logService);
        try {
            processadorCursoOfertado.processar(bucket, "planilhas/dados_cursos/cursos_ofertados.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Cursos Ofertados, erro: %s", e.getMessage()));
        }
    }
}
