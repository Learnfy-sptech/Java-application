package com.learnfy.modelo;

public class Curso {

    private Integer id;
    private Integer fkArea;
    private String nomeCurso;
    private String grauAcademico;

    public Curso() {
        id = null;
        fkArea = null;
    }

    public Curso(String nomeCurso, String grauAcademico) {
        this();
        this.nomeCurso = nomeCurso;
        this.grauAcademico = grauAcademico;
    }

    public Curso(Integer id, Integer fkArea, String nomeCurso, String grauAcademico) {
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

    public String getGrauAcademico() {
        return grauAcademico;
    }

    public void setGrauAcademico(String grauAcademico) {
        this.grauAcademico = grauAcademico;
    }
}
