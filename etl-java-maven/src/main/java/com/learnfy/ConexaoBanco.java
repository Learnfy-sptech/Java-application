package com.learnfy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ConexaoBanco {

    private final JdbcTemplate jdbcTemplate;

    public ConexaoBanco() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(ConfigLoader.get("db.url"));
        ds.setUsername(ConfigLoader.get("db.username"));
        ds.setPassword(ConfigLoader.get("db.password"));
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
