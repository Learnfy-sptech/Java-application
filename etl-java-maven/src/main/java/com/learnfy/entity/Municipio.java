package com.learnfy.entity;

public class Municipio {

    private Long id;
    private String nome;
    private String siglaUf;
    private Long fkUf;


    public Municipio() {
        id = null;
        fkUf = null;
    }

    public Municipio(String nome, String siglaUf) {
        this();
        this.nome = nome;
        this.siglaUf = siglaUf;
    }

    public Municipio(Long id, String nome, String siglaUf, Long fkUf) {
        this.id = id;
        this.nome = nome;
        this.siglaUf = siglaUf;
        this.fkUf = fkUf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSiglaUf() {
        return siglaUf;
    }

    public void setSiglaUf(String siglaUf) {
        this.siglaUf = siglaUf;
    }

    public Long getFkUf() {
        return fkUf;
    }

    public void setFkUf(Long fkUf) {
        this.fkUf = fkUf;
    }
}
