package com.learnfy.entity;

public class Area {
    private Long id;
    private String nome;

    public Area() {
        id = null;
    }

    public Area(String nome) {
        this();
        this.nome = nome;
    }

    public Area(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
