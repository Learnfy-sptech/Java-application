package com.learnfy.processador;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

public class ProcessadorFactory {
    public static Processador getProcessador(String key, JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName) {
        if (key.startsWith("planilhas/dados_cursos/")) {
            return new ProcessadorCurso(jdbcTemplate, s3Client, bucketName);
        } else if (key.startsWith("planilhas/dados_empregabilidade/")) {
            return new ProcessadorEmpregabilidade(jdbcTemplate, s3Client, bucketName);
        } else {
            throw new IllegalArgumentException("Tipo de arquivo desconhecido para a key: " + key);
        }
    }
}
