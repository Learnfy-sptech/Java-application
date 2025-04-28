package com.learnfy.modelo;

public class Curso {

    private Integer ano;
    private String siglaUf;
    private Integer idMunicipio;
    private String rede;
    private Integer idIes;
    private String nomeCurso;
    private String nomeArea;
    private Integer grauAcademico;
    private Integer modalidadeEnsino;
    private Integer qtdVagas;
    private Integer qtdVagasDiurno;
    private Integer qtdVagasNoturno;
    private Integer qtdVagasEad;
    private Integer qtdIncritos;
    private Integer qtdIncritosDiurno;
    private Integer qtdIncritosNoturno;
    private Integer qtdIncritosEad;
    private Integer qtdConcluintesDiurno;
    private Integer qtdConcluintesNoturno;
    private Integer qtdIngressantesRedePublica;
    private Integer qtdIngressantesRedePrivada;
    private Integer qtdConcluintesRedePublica;
    private Integer qtdConcluintesRedePrivada;
    private Integer qtdIngressantesAtividadeExtra;
    private Integer qtdConcluintesAtividadeExtra;

    public Curso() {

    }

    public Curso(Integer ano, String siglaUf, Integer idMunicipio, String rede, Integer idIes, String nomeCurso, String nomeArea, Integer grauAcademico, Integer modalidadeEnsino, Integer qtdVagas, Integer qtdVagasDiurno, Integer qtdVagasNoturno, Integer qtdVagasEad, Integer qtdIncritos, Integer qtdIncritosDiurno, Integer qtdIncritosNoturno, Integer qtdIncritosEad, Integer qtdConcluintesDiurno, Integer qtdConcluintesNoturno, Integer qtdIngressantesRedePublica, Integer qtdIngressantesRedePrivada, Integer qtdConcluintesRedePublica, Integer qtdConcluintesRedePrivada, Integer qtdIngressantesAtividadeExtra, Integer qtdConcluintesAtividadeExtra) {
        this.ano = ano;
        this.siglaUf = siglaUf;
        this.idMunicipio = idMunicipio;
        this.rede = rede;
        this.idIes = idIes;
        this.nomeCurso = nomeCurso;
        this.nomeArea = nomeArea;
        this.grauAcademico = grauAcademico;
        this.modalidadeEnsino = modalidadeEnsino;
        this.qtdVagas = qtdVagas;
        this.qtdVagasDiurno = qtdVagasDiurno;
        this.qtdVagasNoturno = qtdVagasNoturno;
        this.qtdVagasEad = qtdVagasEad;
        this.qtdIncritos = qtdIncritos;
        this.qtdIncritosDiurno = qtdIncritosDiurno;
        this.qtdIncritosNoturno = qtdIncritosNoturno;
        this.qtdIncritosEad = qtdIncritosEad;
        this.qtdConcluintesDiurno = qtdConcluintesDiurno;
        this.qtdConcluintesNoturno = qtdConcluintesNoturno;
        this.qtdIngressantesRedePublica = qtdIngressantesRedePublica;
        this.qtdIngressantesRedePrivada = qtdIngressantesRedePrivada;
        this.qtdConcluintesRedePublica = qtdConcluintesRedePublica;
        this.qtdConcluintesRedePrivada = qtdConcluintesRedePrivada;
        this.qtdIngressantesAtividadeExtra = qtdIngressantesAtividadeExtra;
        this.qtdConcluintesAtividadeExtra = qtdConcluintesAtividadeExtra;
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

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getRede() {
        return rede;
    }

    public void setRede(String rede) {
        this.rede = rede;
    }

    public Integer getIdIes() {
        return idIes;
    }

    public void setIdIes(Integer idIes) {
        this.idIes = idIes;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public String getNomeArea() {
        return nomeArea;
    }

    public void setNomeArea(String nomeArea) {
        this.nomeArea = nomeArea;
    }

    public Integer getGrauAcademico() {
        return grauAcademico;
    }

    public void setGrauAcademico(Integer grauAcademico) {
        this.grauAcademico = grauAcademico;
    }

    public Integer getModalidadeEnsino() {
        return modalidadeEnsino;
    }

    public void setModalidadeEnsino(Integer modalidadeEnsino) {
        this.modalidadeEnsino = modalidadeEnsino;
    }

    public Integer getQtdVagas() {
        return qtdVagas;
    }

    public void setQtdVagas(Integer qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public Integer getQtdVagasDiurno() {
        return qtdVagasDiurno;
    }

    public void setQtdVagasDiurno(Integer qtdVagasDiurno) {
        this.qtdVagasDiurno = qtdVagasDiurno;
    }

    public Integer getQtdVagasNoturno() {
        return qtdVagasNoturno;
    }

    public void setQtdVagasNoturno(Integer qtdVagasNoturno) {
        this.qtdVagasNoturno = qtdVagasNoturno;
    }

    public Integer getQtdVagasEad() {
        return qtdVagasEad;
    }

    public void setQtdVagasEad(Integer qtdVagasEad) {
        this.qtdVagasEad = qtdVagasEad;
    }

    public Integer getQtdIncritos() {
        return qtdIncritos;
    }

    public void setQtdIncritos(Integer qtdIncritos) {
        this.qtdIncritos = qtdIncritos;
    }

    public Integer getQtdIncritosDiurno() {
        return qtdIncritosDiurno;
    }

    public void setQtdIncritosDiurno(Integer qtdIncritosDiurno) {
        this.qtdIncritosDiurno = qtdIncritosDiurno;
    }

    public Integer getQtdIncritosNoturno() {
        return qtdIncritosNoturno;
    }

    public void setQtdIncritosNoturno(Integer qtdIncritosNoturno) {
        this.qtdIncritosNoturno = qtdIncritosNoturno;
    }

    public Integer getQtdIncritosEad() {
        return qtdIncritosEad;
    }

    public void setQtdIncritosEad(Integer qtdIncritosEad) {
        this.qtdIncritosEad = qtdIncritosEad;
    }

    public Integer getQtdConcluintesDiurno() {
        return qtdConcluintesDiurno;
    }

    public void setQtdConcluintesDiurno(Integer qtdConcluintesDiurno) {
        this.qtdConcluintesDiurno = qtdConcluintesDiurno;
    }

    public Integer getQtdConcluintesNoturno() {
        return qtdConcluintesNoturno;
    }

    public void setQtdConcluintesNoturno(Integer qtdConcluintesNoturno) {
        this.qtdConcluintesNoturno = qtdConcluintesNoturno;
    }

    public Integer getQtdIngressantesRedePublica() {
        return qtdIngressantesRedePublica;
    }

    public void setQtdIngressantesRedePublica(Integer qtdIngressantesRedePublica) {
        this.qtdIngressantesRedePublica = qtdIngressantesRedePublica;
    }

    public Integer getQtdIngressantesRedePrivada() {
        return qtdIngressantesRedePrivada;
    }

    public void setQtdIngressantesRedePrivada(Integer qtdIngressantesRedePrivada) {
        this.qtdIngressantesRedePrivada = qtdIngressantesRedePrivada;
    }

    public Integer getQtdConcluintesRedePublica() {
        return qtdConcluintesRedePublica;
    }

    public void setQtdConcluintesRedePublica(Integer qtdConcluintesRedePublica) {
        this.qtdConcluintesRedePublica = qtdConcluintesRedePublica;
    }

    public Integer getQtdConcluintesRedePrivada() {
        return qtdConcluintesRedePrivada;
    }

    public void setQtdConcluintesRedePrivada(Integer qtdConcluintesRedePrivada) {
        this.qtdConcluintesRedePrivada = qtdConcluintesRedePrivada;
    }

    public Integer getQtdIngressantesAtividadeExtra() {
        return qtdIngressantesAtividadeExtra;
    }

    public void setQtdIngressantesAtividadeExtra(Integer qtdIngressantesAtividadeExtra) {
        this.qtdIngressantesAtividadeExtra = qtdIngressantesAtividadeExtra;
    }

    public Integer getQtdConcluintesAtividadeExtra() {
        return qtdConcluintesAtividadeExtra;
    }

    public void setQtdConcluintesAtividadeExtra(Integer qtdConcluintesAtividadeExtra) {
        this.qtdConcluintesAtividadeExtra = qtdConcluintesAtividadeExtra;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "ano=" + ano +
                ", siglaUf='" + siglaUf + '\'' +
                ", idMunicipio=" + idMunicipio +
                ", rede='" + rede + '\'' +
                ", idIes=" + idIes +
                ", nomeCurso='" + nomeCurso + '\'' +
                ", nomeArea='" + nomeArea + '\'' +
                ", grauAcademico=" + grauAcademico +
                ", modalidadeEnsino=" + modalidadeEnsino +
                ", qtdVagas=" + qtdVagas +
                ", qtdVagasDiurno=" + qtdVagasDiurno +
                ", qtdVagasNoturno=" + qtdVagasNoturno +
                ", qtdVagasEad=" + qtdVagasEad +
                ", qtdIncritos=" + qtdIncritos +
                ", qtdIncritosDiurno=" + qtdIncritosDiurno +
                ", qtdIncritosNoturno=" + qtdIncritosNoturno +
                ", qtdIncritosEad=" + qtdIncritosEad +
                ", qtdConcluintesDiurno=" + qtdConcluintesDiurno +
                ", qtdConcluintesNoturno=" + qtdConcluintesNoturno +
                ", qtdIngressantesRedePublica=" + qtdIngressantesRedePublica +
                ", qtdIngressantesRedePrivada=" + qtdIngressantesRedePrivada +
                ", qtdConcluintesRedePublica=" + qtdConcluintesRedePublica +
                ", qtdConcluintesRedePrivada=" + qtdConcluintesRedePrivada +
                ", qtdIngressantesAtividadeExtra=" + qtdIngressantesAtividadeExtra +
                ", qtdConcluintesAtividadeExtra=" + qtdConcluintesAtividadeExtra +
                '}';
    }
}