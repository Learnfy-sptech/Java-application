package com.learnfy;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoBanco {

    private final JdbcTemplate jdbcTemplate;
    private final BasicDataSource basicDataSource;

    public ConexaoBanco() {
        BasicDataSource basicDataSource = new BasicDataSource();
        String urlString = "jdbc:mysql://" + System.getenv("EC2_IP") + "/db_cursos";
        basicDataSource.setUrl(urlString);
        String usernameString = System.getenv("USER_DB");
        basicDataSource.setUsername(usernameString);
        String passwordString = System.getenv("PASS_DB");
        basicDataSource.setPassword(passwordString);

        this.basicDataSource = basicDataSource;
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
