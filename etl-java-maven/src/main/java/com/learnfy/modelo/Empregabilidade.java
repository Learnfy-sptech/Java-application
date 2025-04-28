package com.learnfy.modelo;

public class Empregabilidade {
    private Integer ano;
    private String sigla_uf;
    private Integer cbo_2002;
    private String cbo_2002_descricao;
    private String cbo_2002_descricao_familia;
    private String categoria;
    private String grau_instrucao;
    private Double salario_mensal;

    public Empregabilidade() {
    }

    public Empregabilidade(Integer ano, String sigla_uf, Integer cbo_2002, String cbo_2002_descricao,
                                String cbo_2002_descricao_familia, String categoria, String grau_instrucao, Double salario_mensal) {
        this.ano = ano;
        this.sigla_uf = sigla_uf;
        this.cbo_2002 = cbo_2002;
        this.cbo_2002_descricao = cbo_2002_descricao;
        this.cbo_2002_descricao_familia = cbo_2002_descricao_familia;
        this.categoria = categoria;
        this.grau_instrucao = grau_instrucao;
        this.salario_mensal = salario_mensal;
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

    public Integer getCbo_2002() {
        return cbo_2002;
    }

    public void setCbo_2002(Integer cbo_2002) {
        this.cbo_2002 = cbo_2002;
    }

    public String getCbo_2002_descricao() {
        return cbo_2002_descricao;
    }

    public void setCbo_2002_descricao(String cbo_2002_descricao) {
        this.cbo_2002_descricao = cbo_2002_descricao;
    }

    public String getCbo_2002_descricao_familia() {
        return cbo_2002_descricao_familia;
    }

    public void setCbo_2002_descricao_familia(String cbo_2002_descricao_familia) {
        this.cbo_2002_descricao_familia = cbo_2002_descricao_familia;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGrau_instrucao() {
        return grau_instrucao;
    }

    public void setGrau_instrucao(String grau_instrucao) {
        this.grau_instrucao = grau_instrucao;
    }

    public Double getSalario_mensal() {
        return salario_mensal;
    }

    public void setSalario_mensal(Double salario_mensal) {
        this.salario_mensal = salario_mensal;
    }
}