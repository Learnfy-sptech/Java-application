package com.learnfy.modelo;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public class Empregabilidade {
    private Integer ano;
    private String siglaUf;
    private String cbo2002;
    private String cbo2002Descricao;
    private String cbo2002DescricaoFamilia;
    private String categoria;
    private String grauInstrucao;
    private Double salarioMensal;
    private Integer fk_municipio;

    public Empregabilidade(){}
    public Empregabilidade(Integer ano, String siglaUf, String cbo2002, String cbo2002Descricao, String cbo2002DescricaoFamilia, String categoria, String grauInstrucao, Double salarioMensal, Integer fk_municipio) {
        this.ano = ano;
        this.siglaUf = siglaUf;
        this.cbo2002 = cbo2002;
        this.cbo2002Descricao = cbo2002Descricao;
        this.cbo2002DescricaoFamilia = cbo2002DescricaoFamilia;
        this.categoria = categoria;
        this.grauInstrucao = grauInstrucao;
        this.salarioMensal = salarioMensal;
        this.fk_municipio = fk_municipio;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSiglaUf() {
        return siglaUf;
    }

    public void setSiglaUf(String siglaUf) {
        this.siglaUf = siglaUf;
    }

    public String getCbo2002() {
        return cbo2002;
    }

    public void setCbo2002(String cbo2002) {
        this.cbo2002 = cbo2002;
    }

    public String getCbo2002Descricao() {
        return cbo2002Descricao;
    }

    public void setCbo2002Descricao(String cbo2002Descricao) {
        this.cbo2002Descricao = cbo2002Descricao;
    }

    public String getCbo2002DescricaoFamilia() {
        return cbo2002DescricaoFamilia;
    }

    public void setCbo2002DescricaoFamilia(String cbo2002DescricaoFamilia) {
        this.cbo2002DescricaoFamilia = cbo2002DescricaoFamilia;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGrauInstrucao() {
        return grauInstrucao;
    }

    public void setGrauInstrucao(String grauInstrucao) {
        this.grauInstrucao = grauInstrucao;
    }

    public Double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(Double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    public Integer getFk_municipio() {
        return fk_municipio;
    }

    public void setFk_municipio(Integer fk_municipio) {
        this.fk_municipio = fk_municipio;
    }
}