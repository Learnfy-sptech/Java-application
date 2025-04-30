package com.learnfy.modelo;

public class Ies {

    private Integer id;
    private Integer fkMunicipio;
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

    public Ies(Integer id, Integer fkMunicipio, String nomeMunicipio, Boolean redePublica, String nome) {
        this.id = id;
        this.fkMunicipio = fkMunicipio;
        this.nomeMunicipio = nomeMunicipio;
        this.redePublica = redePublica;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkMunicipio() {
        return fkMunicipio;
    }

    public void setFkMunicipio(Integer fkMunicipio) {
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