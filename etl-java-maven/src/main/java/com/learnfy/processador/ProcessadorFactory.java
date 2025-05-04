package com.learnfy.processador;

import com.learnfy.logs.LogService;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

public class ProcessadorFactory {
    public static Processador getProcessador(String key, JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName) {
        if (key.startsWith("planilhas/dados_cursos/")) {
            return new ProcessadorCursoOfertado(jdbcTemplate, s3Client);
        } else if (key.startsWith("planilhas/dados_empregabilidade/")) {
            return new ProcessadorEmpregabilidade(jdbcTemplate, s3Client, bucketName);
        } else {
            throw new IllegalArgumentException("Tipo de arquivo desconhecido para a key: " + key);
        }
    }

    public static void inserirDadosEscolaridade(JdbcTemplate jdbcTemplate, S3Client s3Client, String bucket, LogService logService) {
        Processador processadorUf = new ProcessadorUf(jdbcTemplate, s3Client);
        try {
            processadorUf.processar(bucket, "planilhas/dados_cursos/estados.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Estados, erro: %s", e.getMessage()));
        }

        Processador processadorMunicipio = new ProcessadorMunicipio(jdbcTemplate, s3Client);
        try {
            processadorMunicipio.processar(bucket, "planilhas/dados_cursos/municipios.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Municípios, erro: %s", e.getMessage()));
        }

        Processador processadorIes = new ProcessadorIes(jdbcTemplate, s3Client);
        try {
            processadorIes.processar(bucket, "planilhas/dados_cursos/ies.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados das Instituições de Ensino, erro: %s", e.getMessage()));
        }

        Processador processadorCurso = new ProcessadorCursoArea(jdbcTemplate, s3Client, logService);
        try {
            processadorCurso.processar(bucket, "planilhas/dados_cursos/cursos_areas.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Cursos e das Áreas, erro: %s", e.getMessage()));
        }

        Processador processadorCursoOfertado = new ProcessadorCursoOfertado(jdbcTemplate, s3Client);
        try {
            processadorCursoOfertado.processar(bucket, "planilhas/dados_cursos/cursos_ofertados.xlsx");
        } catch (Exception e) {
            System.out.println("Não foi possível processar os dados dos Cursos Ofertados");
        }
    }
}
