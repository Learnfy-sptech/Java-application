package com.learnfy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ConexaoBanco {

    private final JdbcTemplate jdbcTemplate;

    public ConexaoBanco() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(ConfigLoader.get("DB_URL"));
        ds.setUsername(ConfigLoader.get("DB_USERNAME"));
        ds.setPassword(ConfigLoader.get("DB_PASSWORD"));
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
