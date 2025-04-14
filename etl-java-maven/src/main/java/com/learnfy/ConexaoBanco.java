package com.learnfy;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoBanco {

    private final JdbcTemplate jdbcTemplate;
    private final BasicDataSource basicDataSource;

    public ConexaoBanco() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://35.172.194.187/db_cursos");
        basicDataSource.setUsername("localuser");
        basicDataSource.setPassword("lucas1234");

        this.basicDataSource = basicDataSource;
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void insertCurso(Curso curso) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        jdbcTemplate.update(
                "INSERT INTO cursos (" +
                        "ano, sigla_uf, id_municipio, rede, id_ies, nome_curso, nome_area, grau_academico, modalidade_ensino, " +
                        "qtd_vagas, qtd_vagas_diurno, qtd_vagas_noturno, qtd_vagas_ead, " +
                        "qtd_incritos, qtd_incritos_diurno, qtd_incritos_noturno, qtd_incritos_ead, " +
                        "qtd_concluintes_diurno, qtd_concluintes_noturno, qtd_ingressantes_rede_publica, qtd_ingressantes_rede_privada, " +
                        "qtd_concluintes_rede_publica, qtd_concluintes_rede_privada, qtd_ingressantes_atividade_extra, qtd_concluintes_atividade_extra" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                curso.getAno(),
                curso.getSiglaUf(),
                curso.getIdMunicipio(),
                curso.getRede(),
                curso.getIdIes(),
                curso.getNomeCurso(),
                curso.getNomeArea(),
                curso.getGrauAcademico(),
                curso.getModalidadeEnsino(),
                curso.getQtdVagas(),
                curso.getQtdVagasDiurno(),
                curso.getQtdVagasNoturno(),
                curso.getQtdVagasEad(),
                curso.getQtdIncritos(),
                curso.getQtdIncritosDiurno(),
                curso.getQtdIncritosNoturno(),
                curso.getQtdIncritosEad(),
                curso.getQtdConcluintesDiurno(),
                curso.getQtdConcluintesNoturno(),
                curso.getQtdIngressantesRedePublica(),
                curso.getQtdIngressantesRedePrivada(),
                curso.getQtdConcluintesRedePublica(),
                curso.getQtdConcluintesRedePrivada(),
                curso.getQtdIngressantesAtividadeExtra(),
                curso.getQtdConcluintesAtividadeExtra()
        );
    }

}
