package com.learnfy.processador;

import com.learnfy.modelo.Curso;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessadorCurso implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final String bucketName;

    public ProcessadorCurso(JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName) {
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

            Workbook workbook = WorkbookFactory.create(inputStream);
            List<Curso> cursos = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Planilha lida com sucesso. Processando linhas...");

            boolean primeiraLinha = true;

            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                try {
                    Curso curso = extrairDados(row);
                    cursos.add(curso);
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            final int BATCH_SIZE = 500;
            for (int i = 0; i < cursos.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, cursos.size());
                List<Curso> subList = cursos.subList(i, end);
                enviarBatch(subList);
            }

            workbook.close();
            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
        }
    }

    private Curso extrairDados(Row row) {
        Curso curso = new Curso();
        try {
            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 0 -> curso.setAno((int) getNumericValue(cell));
                    case 1 -> curso.setSiglaUf(getStringValue(cell));
                    case 2 -> curso.setIdMunicipio((int) getNumericValue(cell));
                    case 3 -> curso.setRede(getStringValue(cell));
                    case 4 -> curso.setIdIes((int) getNumericValue(cell));
                    case 5 -> curso.setNomeCurso(getStringValue(cell));
                    case 6 -> curso.setNomeArea(getStringValue(cell));
                    case 7 -> curso.setGrauAcademico((int) getNumericValue(cell));
                    case 8 -> curso.setModalidadeEnsino((int) getNumericValue(cell));
                    case 9 -> curso.setQtdVagas((int) getNumericValue(cell));
                    case 10 -> curso.setQtdVagasDiurno((int) getNumericValue(cell));
                    case 11 -> curso.setQtdVagasNoturno((int) getNumericValue(cell));
                    case 12 -> curso.setQtdVagasEad((int) getNumericValue(cell));
                    case 13 -> curso.setQtdIncritos((int) getNumericValue(cell));
                    case 14 -> curso.setQtdIncritosDiurno((int) getNumericValue(cell));
                    case 15 -> curso.setQtdIncritosNoturno((int) getNumericValue(cell));
                    case 16 -> curso.setQtdIncritosEad((int) getNumericValue(cell));
                    case 17 -> curso.setQtdConcluintesDiurno((int) getNumericValue(cell));
                    case 18 -> curso.setQtdConcluintesNoturno((int) getNumericValue(cell));
                    case 19 -> curso.setQtdIngressantesRedePublica((int) getNumericValue(cell));
                    case 20 -> curso.setQtdIngressantesRedePrivada((int) getNumericValue(cell));
                    case 21 -> curso.setQtdConcluintesRedePublica((int) getNumericValue(cell));
                    case 22 -> curso.setQtdConcluintesRedePrivada((int) getNumericValue(cell));
                    case 23 -> curso.setQtdIngressantesAtividadeExtra((int) getNumericValue(cell));
                    case 24 -> curso.setQtdConcluintesAtividadeExtra((int) getNumericValue(cell));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair dados da linha: " + e.getMessage());
        }
        return curso;
    }

    private double getNumericValue(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0;
    }

    private String getStringValue(Cell cell) {
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : "";
    }

    private void enviarBatch(List<Curso> cursos) {
        System.out.println("Inserindo " + cursos.size() + " registros no banco.");

        String sql = "INSERT INTO curso (\n" +
                "    ano, sigla_uf, id_municipio, rede, id_ies, nome_curso, nome_area, grau_academico,\n" +
                "    modalidade_ensino, qtd_vagas, qtd_vagas_diurno, qtd_vagas_noturno, qtd_vagas_ead,\n" +
                "    qtd_inscritos, qtd_inscritos_diurno, qtd_inscritos_noturno, qtd_inscritos_ead,\n" +
                "    qtd_concluintes_diurno, qtd_concluintes_noturno, qtd_ingressantes_rede_publica,\n" +
                "    qtd_ingressantes_rede_privada, qtd_concluintes_rede_publica, qtd_concluintes_rede_privada\n" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";

        jdbcTemplate.batchUpdate(sql, cursos, cursos.size(), (ps, curso) -> {
            ps.setInt(1, curso.getAno() != null ? curso.getAno() : 0);
            ps.setString(2, curso.getSiglaUf() != null ? curso.getSiglaUf() : "");
            ps.setInt(3, curso.getIdMunicipio() != null ? curso.getIdMunicipio() : 0);
            ps.setString(4, curso.getRede() != null ? curso.getRede() : "");
            ps.setInt(5, curso.getIdIes() != null ? curso.getIdIes() : 0);
            ps.setString(6, curso.getNomeCurso() != null ? curso.getNomeCurso() : "");
            ps.setString(7, curso.getNomeArea() != null ? curso.getNomeArea() : "");

            Integer grauAcademico = curso.getGrauAcademico();
            ps.setInt(8, grauAcademico != null ? grauAcademico : 0);

            Integer modalidadeEnsino = curso.getModalidadeEnsino();
            ps.setInt(9, modalidadeEnsino != null ? modalidadeEnsino : 0);

            Integer qtdVagas = curso.getQtdVagas();
            ps.setInt(10, qtdVagas != null ? qtdVagas : 0);

            Integer qtdVagasDiurno = curso.getQtdVagasDiurno();
            ps.setInt(11, qtdVagasDiurno != null ? qtdVagasDiurno : 0);

            Integer qtdVagasNoturno = curso.getQtdVagasNoturno();
            ps.setInt(12, qtdVagasNoturno != null ? qtdVagasNoturno : 0);

            Integer qtdVagasEad = curso.getQtdVagasEad();
            ps.setInt(13, qtdVagasEad != null ? qtdVagasEad : 0);

            Integer qtdIncritos = curso.getQtdIncritos();
            ps.setInt(14, qtdIncritos != null ? qtdIncritos : 0);

            Integer qtdIncritosDiurno = curso.getQtdIncritosDiurno();
            ps.setInt(15, qtdIncritosDiurno != null ? qtdIncritosDiurno : 0);

            Integer qtdIncritosNoturno = curso.getQtdIncritosNoturno();
            ps.setInt(16, qtdIncritosNoturno != null ? qtdIncritosNoturno : 0);

            Integer qtdIncritosEad = curso.getQtdIncritosEad();
            ps.setInt(17, qtdIncritosEad != null ? qtdIncritosEad : 0);

            Integer qtdConcluintesDiurno = curso.getQtdConcluintesDiurno();
            ps.setInt(18, qtdConcluintesDiurno != null ? qtdConcluintesDiurno : 0);

            Integer qtdConcluintesNoturno = curso.getQtdConcluintesNoturno();
            ps.setInt(19, qtdConcluintesNoturno != null ? qtdConcluintesNoturno : 0);

            Integer qtdIngressantesRedePublica = curso.getQtdIngressantesRedePublica();
            ps.setInt(20, qtdIngressantesRedePublica != null ? qtdIngressantesRedePublica : 0);

            Integer qtdIngressantesRedePrivada = curso.getQtdIngressantesRedePrivada();
            ps.setInt(21, qtdIngressantesRedePrivada != null ? qtdIngressantesRedePrivada : 0);

            Integer qtdConcluintesRedePublica = curso.getQtdConcluintesRedePublica();
            ps.setInt(22, qtdConcluintesRedePublica != null ? qtdConcluintesRedePublica : 0);

            Integer qtdConcluintesRedePrivada = curso.getQtdConcluintesRedePrivada();
            ps.setInt(23, qtdConcluintesRedePrivada != null ? qtdConcluintesRedePrivada : 0);
        });
    }
}
