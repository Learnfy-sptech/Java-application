package com.learnfy.entity;

public class Ies {

    private Long id;
    private Long fkMunicipio;
    private String nomeMunicipio;
    private Boolean redePublica;
    private String nome;

    public Ies() {
        id = null;
        fkMunicipio = null;
    }

    public Ies(String nomeMunicipio, Boolean redePublica, String nome) {
        this();
        this.nomeMunicipio = nomeMunicipio;
        this.redePublica = redePublica;
        this.nome = nome;
    }

    public Ies(Long id, Long fkMunicipio, String nomeMunicipio, Boolean redePublica, String nome) {
        this.id = id;
        this.fkMunicipio = fkMunicipio;
        this.nomeMunicipio = nomeMunicipio;
        this.redePublica = redePublica;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFkMunicipio() {
        return fkMunicipio;
    }

    public void setFkMunicipio(Long fkMunicipio) {
        this.fkMunicipio = fkMunicipio;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public Boolean getRedePublica() {
        return redePublica;
    }

    public void setRedePublica(Boolean redePublica) {
        this.redePublica = redePublica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
