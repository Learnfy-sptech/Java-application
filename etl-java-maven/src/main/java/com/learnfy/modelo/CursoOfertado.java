package com.learnfy.modelo;

public class CursoOfertado {

    private Integer fkIes;
    private Integer fkCurso;
    private String nomeIes; // Apenas para realizar a busca da fk
    private String nomeCurso; // Apenas para realizar a busca da fk
    private Integer ano;
    private Integer modalidadeEnsino;
    private Integer qtdVagas;
    private Integer qtdVagasDiurno;
    private Integer qtdVagasNoturno;
    private Integer qtdVagasEad;
    private Integer qtdIncritos;
    private Integer qtdIncritosDiurno;
    private Integer qtdIncritosNoturno;
    private Integer qtdIncritosEad;
    private Integer qtdConcluintes;
    private Integer qtdConcluintesDiurno;
    private Integer qtdConcluintesNoturno;
    private Integer qtdIngressantesRedePublica;
    private Integer qtdIngressantesRedePrivada;
    private Integer qtdConcluintesRedePublica;
    private Integer qtdConcluintesRedePrivada;

    public CursoOfertado() {
    }

    public Integer getFkIes() {
        return fkIes;
    }

    public Integer getQtdConcluintes() {
        return qtdConcluintes;
    }

    public void setQtdConcluintes(Integer qtdConcluintes) {
        this.qtdConcluintes = qtdConcluintes;
    }

    public void setFkIes(Integer fkIes) {
        this.fkIes = fkIes;
    }

    public Integer getFkCurso() {
        return fkCurso;
    }

    public void setFkCurso(Integer fkCurso) {
        this.fkCurso = fkCurso;
    }

    public String getNomeIes() {
        return nomeIes;
    }

    public void setNomeIes(String nomeIes) {
        this.nomeIes = nomeIes;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
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

    @Override
    public String toString() {
        return "Curso{" +
                "ano=" + ano +
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
                '}';
    }
}