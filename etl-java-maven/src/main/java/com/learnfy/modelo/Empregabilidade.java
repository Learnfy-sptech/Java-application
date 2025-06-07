package com.learnfy.modelo;

public class Empregabilidade {
    private Integer ano;
    private String siglaUf;
    private String cbo2002;
    private String cbo2002Descricao;
    private String cbo2002DescricaoFamilia;
    private String categoria;
    private String grauInstrucao;
    private Double salarioMensal;
    private Integer fk_uf;
    private Integer fk_area;

    public Empregabilidade(){}

    public Empregabilidade(Integer ano, String siglaUf, String cbo2002, String cbo2002Descricao, String cbo2002DescricaoFamilia, String categoria, String grauInstrucao, Double salarioMensal, Integer fk_uf, Integer fk_area) {
        this.ano = ano;
        this.siglaUf = siglaUf;
        this.cbo2002 = cbo2002;
        this.cbo2002Descricao = cbo2002Descricao;
        this.cbo2002DescricaoFamilia = cbo2002DescricaoFamilia;
        this.categoria = categoria;
        this.grauInstrucao = grauInstrucao;
        this.salarioMensal = salarioMensal;
        this.fk_uf = fk_uf;
        this.fk_area = fk_area;
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

    public Integer getFkUf() {
        return fk_uf;
    }

    public void setFkUf(Integer fk_uf) {
        this.fk_uf = fk_uf;
    }

    public Integer getFkArea() {
        return fk_area;
    }

    public void setFkArea(Integer fk_area) {
        this.fk_area = fk_area;
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
                ", fk_uf=" + fk_uf +
                ", fk_area=" + fk_area +
                '}';
    }
}