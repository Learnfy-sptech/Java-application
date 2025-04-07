package com.learnfy;

public class DadosCurso {

    private Integer ano;
    private String sigla_uf;
    private Integer id_municipio;
    private Integer tipo_dimensao;
    private Integer tipo_organizacao_academica;
    private Integer tipo_organizacao_administrativa;
    private Integer rede;
    private Integer id_ies;
    private String nome_curso;

    public DadosCurso() {
    }

    public DadosCurso(Integer ano, String sigla_uf, Integer id_municipio, Integer tipo_dimensao, Integer tipo_organizacao_academica, Integer tipo_organizacao_administrativa, Integer rede, Integer id_ies, String nome_curso) {
        this.ano = ano;
        this.sigla_uf = sigla_uf;
        this.id_municipio = id_municipio;
        this.tipo_dimensao = tipo_dimensao;
        this.tipo_organizacao_academica = tipo_organizacao_academica;
        this.tipo_organizacao_administrativa = tipo_organizacao_administrativa;
        this.rede = rede;
        this.id_ies = id_ies;
        this.nome_curso = nome_curso;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSigla_uf() {
        return sigla_uf;
    }

    public void setSigla_uf(String sigla_uf) {
        this.sigla_uf = sigla_uf;
    }

    public Integer getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(Integer id_municipio) {
        this.id_municipio = id_municipio;
    }

    public Integer getTipo_dimensao() {
        return tipo_dimensao;
    }

    public void setTipo_dimensao(Integer tipo_dimensao) {
        this.tipo_dimensao = tipo_dimensao;
    }

    public Integer getTipo_organizacao_academica() {
        return tipo_organizacao_academica;
    }

    public void setTipo_organizacao_academica(Integer tipo_organizacao_academica) {
        this.tipo_organizacao_academica = tipo_organizacao_academica;
    }

    public Integer getTipo_organizacao_administrativa() {
        return tipo_organizacao_administrativa;
    }

    public void setTipo_organizacao_administrativa(Integer tipo_organizacao_administrativa) {
        this.tipo_organizacao_administrativa = tipo_organizacao_administrativa;
    }

    public Integer getRede() {
        return rede;
    }

    public void setRede(Integer rede) {
        this.rede = rede;
    }

    public Integer getId_ies() {
        return id_ies;
    }

    public void setId_ies(Integer id_ies) {
        this.id_ies = id_ies;
    }

    public String getNome_curso() {
        return nome_curso;
    }

    public void setNome_curso(String nome_curso) {
        this.nome_curso = nome_curso;
    }

}
