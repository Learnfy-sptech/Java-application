package com.learnfy.modelo;

public class Uf {

    private Integer id;
    private String sigla;
    private String nome;
    private String regiao;

    public Uf() {
        id = null;
    }

    public Uf(String sigla, String nome, String regiao) {
        this();
        this.sigla = sigla;
        this.nome = nome;
        this.regiao = regiao;
    }

    public Uf(Integer id, String sigla, String nome, String regiao) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
        this.regiao = regiao;
    }

    public Integer getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }
}