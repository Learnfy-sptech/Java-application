package com.learnfy.modelo;

public class Empregabilidade {
    private Integer ano;
    private String siglaUf;
    private Integer cbo2002;
    private String cbo2002Descricao;
    private String cbo2002DescricaoFamilia;
    private String categoria;
    private String grauInstrucao;
    private Double salarioMensal;

    public Empregabilidade() {
    }

    public Empregabilidade(Integer ano, String siglaUf, Integer cbo2002, String cbo_2002_descricao,
                           String cbo2002DescricaoFamilia, String categoria, String grauInstrucao, Double salarioMensal) {
        this.ano = ano;
        this.siglaUf = siglaUf;
        this.cbo2002 = cbo2002;
        this.cbo2002Descricao = cbo_2002_descricao;
        this.cbo2002DescricaoFamilia = cbo2002DescricaoFamilia;
        this.categoria = categoria;
        this.grauInstrucao = grauInstrucao;
        this.salarioMensal = salarioMensal;
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

    public Integer getCbo2002() {
        return cbo2002;
    }

    public void setCbo2002(Integer cbo2002) {
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

    @Override
    public String toString() {
        return "Empregabilidade{" +
                "ano=" + ano +
                ", siglaUf='" + siglaUf + '\'' +
                ", cbo2002=" + cbo2002 +
                ", cbo2002Descricao='" + cbo2002Descricao + '\'' +
                ", cbo2002DescricaoFamilia='" + cbo2002DescricaoFamilia + '\'' +
                ", categoria='" + categoria + '\'' +
                ", grauInstrucao='" + grauInstrucao + '\'' +
                ", salarioMensal=" + salarioMensal +
                '}';
    }
}