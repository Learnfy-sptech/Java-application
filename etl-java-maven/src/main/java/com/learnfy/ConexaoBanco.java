package com.learnfy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class ConexaoBanco {
    public static JdbcTemplate getJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(ConfigLoader.get("DB_URL"));
        dataSource.setUsername(ConfigLoader.get("DB_USERNAME"));
        dataSource.setPassword(ConfigLoader.get("DB_PASSWORD"));
        return new JdbcTemplate(dataSource);
    }
}

