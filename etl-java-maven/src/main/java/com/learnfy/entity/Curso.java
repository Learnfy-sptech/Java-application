package com.learnfy.entity;

public class Curso {

    private Long id;
    private Long fkArea;
    private String nomeArea;
    private String nomeCurso;
    private Integer grau_academico;

    public Curso() {
        id = null;
        fkArea = null;
    }

    public Curso(String nomeCurso, Integer grau_academico) {
        this();
        this.nomeCurso = nomeCurso;
        this.grau_academico = grau_academico;
    }

    public Curso(Long id, Long fkArea, String nomeCurso, Integer grau_academico) {
        this.id = id;
        this.fkArea = fkArea;
        this.nomeCurso = nomeCurso;
        this.grau_academico = grau_academico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkArea() {
        return fkArea;
    }

    public void setFkArea(Long fkArea) {
        this.fkArea = fkArea;
    }

    public String getNomeArea() {
        return nomeArea;
    }

    public void setNomeArea(String nomeArea) {
        this.nomeArea = nomeArea;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public Integer getGrau_academico() {
        return grau_academico;
    }

    public void setGrau_academico(Integer grau_academico) {
        this.grau_academico = grau_academico;
    }
}
