package com.learnfy.modelo;

public class Curso {

    private Integer id;
    private Integer fkArea;
    private String nomeCurso;
    private Integer grauAcademico;

    public Curso() {
        id = null;
        fkArea = null;
    }

    public Curso(String nomeCurso, Integer grauAcademico) {
        this();
        this.nomeCurso = nomeCurso;
        this.grauAcademico = grauAcademico;
    }

    public Curso(Integer id, Integer fkArea, String nomeCurso, Integer grauAcademico) {
        this.id = id;
        this.fkArea = fkArea;
        this.nomeCurso = nomeCurso;
        this.grauAcademico = grauAcademico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkArea() {
        return fkArea;
    }

    public void setFkArea(Integer fkArea) {
        this.fkArea = fkArea;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public Integer getGrauAcademico() {
        return grauAcademico;
    }

    public void setGrauAcademico(Integer grauAcademico) {
        this.grauAcademico = grauAcademico;
    }
}
