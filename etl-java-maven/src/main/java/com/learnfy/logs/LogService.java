package com.learnfy.logs;

import org.springframework.jdbc.core.JdbcTemplate;

public class LogService {
    private final JdbcTemplate jdbcTemplate;

    public LogService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void registrarLog(String nomeArquivo, String tipoProcessador, String status, String mensagem) {
        String sql = "INSERT INTO logs_processamento (nome_arquivo, tipo_processador, status, mensagem) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, nomeArquivo, tipoProcessador, status, mensagem);
    }
}
