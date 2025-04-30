package com.learnfy.modelo;

public class Municipio {

    private Integer id;
    private String nome;
    private String siglaUf;
    private Integer fkUf;


    public Municipio() {
        id = null;
        fkUf = null;
    }

    public Municipio(String nome, String siglaUf) {
        this();
        this.nome = nome;
        this.siglaUf = siglaUf;
    }

    public Municipio(Integer id, String nome, String siglaUf, Integer fkUf) {
        this.id = id;
        this.nome = nome;
        this.siglaUf = siglaUf;
        this.fkUf = fkUf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getFkUf() {
        return fkUf;
    }

    public void setFkUf(Integer fkUf) {
        this.fkUf = fkUf;
    }
}
