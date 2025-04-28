package com.learnfy;

import com.learnfy.entity.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class ConexaoBanco {

    private final JdbcTemplate jdbcTemplate;
    private final BasicDataSource basicDataSource;

    public ConexaoBanco() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://34.207.167.8/learnfy_db");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("learnfy123");

        this.basicDataSource = basicDataSource;
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void insertUf(Uf uf) {
        List<Uf> ufValidation = jdbcTemplate.query(
                "SELECT * FROM uf_tb WHERE sigla = ?",
                new BeanPropertyRowMapper<>(Uf.class),
                uf.getSigla()
        );

        if (ufValidation.isEmpty()) {
            try {
                jdbcTemplate.update(
                        "INSERT INTO uf_tb (sigla, nome, região)" +
                                "VALUES (?,?,?)", uf.getSigla(), uf.getNome(), uf.getRegiao()
                );
            } catch (Exception e) {
                System.out.println(String.format("Erro ao inserir o Estado %s | Erro: %s", uf.getSigla(), e.getMessage()));
            }
        } else {
            System.out.println(String.format("Estado %s já cadastrado!", uf.getNome()));
        }

    }

    public void insertMunicipio(Municipio municipio) {
        List<Municipio> municipioValidation = jdbcTemplate.query(
                "SELECT * FROM municipio_tb WHERE nome = ?",
                new BeanPropertyRowMapper<>(Municipio.class),
                municipio.getNome()
        );

        if (municipioValidation.isEmpty()) {
            try {
                List<Uf> uf = jdbcTemplate.query(
                        "SELECT * FROM uf_tb WHERE sigla = ?",
                        new BeanPropertyRowMapper<>(Uf.class),
                        municipio.getSiglaUf()
                );
                jdbcTemplate.update(
                        "INSERT INTO municipio_tb (nome, fk_uf)" +
                                "VALUES (?,?)", municipio.getNome(), uf.get(0).getId()
                );
            } catch (Exception e) {
                System.out.println(String.format("Erro ao inserir o Município %s | Erro: %s", municipio.getNome(), e.getMessage()));
            }
        } else {
            System.out.println(String.format("Município %s já cadastrado!", municipio.getNome()));
        }
    }

    public void insertIes(Ies ies) {
        List<Ies> iesValidation = jdbcTemplate.query(
                "SELECT * FROM ies_tb WHERE nome = ?",
                new BeanPropertyRowMapper<>(Ies.class),
                ies.getNome()
        );

        if (iesValidation.isEmpty()) {
            try {
                List<Municipio> municipio = jdbcTemplate.query(
                        "SELECT * FROM municipio_tb WHERE nome = ?",
                        new BeanPropertyRowMapper<>(Municipio.class),
                        ies.getNomeMunicipio()
                );
                jdbcTemplate.update(
                        "INSERT INTO ies_tb (fk_municipio, rede_publica, nome)" +
                                "VALUES (?,?,?)", municipio.get(0).getId(), ies.getRedePublica(), ies.getNomeMunicipio()
                );
            } catch (Exception e) {
                System.out.printf("Erro ao inserir a Instituição de Ensino %s | Erro: %s", ies.getNome(), e.getMessage());
            }
        } else {
            System.out.println(String.format("Instituição de Ensino %s já cadastrada!", ies.getNome()));
        }
    }

    public void insertArea(Area area) {
        List<Area> areaValidation = jdbcTemplate.query(
                "SELECT * FROM area_tb WHERE sigla = ?",
                new BeanPropertyRowMapper<>(Area.class),
                area.getNome()
        );

        if (areaValidation.isEmpty()) {
            try {
                jdbcTemplate.update(
                        "INSERT INTO area_tb (nome)" +
                                "VALUES (?,?,?)", area.getNome()
                );
            } catch (Exception e) {
                System.out.println(String.format("Erro ao inserir a Area %s | Erro: %s", area.getNome(), e.getMessage()));
            }
        } else {
            System.out.println(String.format("Area %s já cadastrada!", area.getNome()));
        }

    }

//    public void insertCurso(Curso curso) {
//        List<Curso> cursoValidation = jdbcTemplate.query(
//                "SELECT id FROM curso_tb WHERE nome = ?",
//                new BeanPropertyRowMapper<>(Curso.class),
//                curso.getNomeCurso()
//        );
//
//        if (cursoValidation.isEmpty()) {
//            try {
//                Long cursoId = jdbcTemplate.queryForObject(
//                        "SELECT id FROM curso_tb WHERE nome = ?",
//                        Long.class,
//                        curso.getNomeArea()
//                );
//
//                List<Curso> curso = jdbcTemplate.queryForObject(
//                        "SELECT * FROM curso_tb WHERE nome = ?",
//                        new BeanPropertyRowMapper<>(Curso.class),
//                        curso.getNomeArea()
//                );
//
//                jdbcTemplate.update(
//                        "INSERT INTO curso_tb (fk_area, nome_curso, grau_academico)" +
//                                "VALUES (?,?,?)", area.get(0).getId(), curso.getNomeCurso(), curso.getGrau_academico()
//                );
//            } catch (Exception e) {
//                System.out.printf("Erro ao inserir o Curso %s | Erro: %s", curso.getNomeCurso(), e.getMessage());
//            }
//        } else {
//            System.out.println(String.format("Curso %s já cadastrado!", curso.getNomeCurso()));
//        }
//    }
//
//    public void insertCursoOfertado(CursoOfertado cursoOfertado) {
//        try {
//            List<Curso> curso = jdbcTemplate.query(
//                    "SELECT * FROM curso_tb WHERE nome = ?",
//                    cursoOfertado.
//            )
//            jdbcTemplate.update(
//                    "INSERT INTO curso_tb (fk_area, nome_curso, grau_academico)" +
//                            "VALUES (?,?,?)", area.get(0).getId(), curso.getNomeCurso(), curso.getGrau_academico()
//            );
//
//        } catch (Exception e) {
//            System.out.printf("Erro ao inserir o Curso %s | Erro: %s", curso.getNomeCurso(), e.getMessage());
//        }
//    }

}
