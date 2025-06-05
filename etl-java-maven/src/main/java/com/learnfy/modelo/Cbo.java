package com.learnfy.modelo;

public class Cbo {

    private Integer id_cbo;
    private String descricao;
    private String codigo_tipo_emprego;
    private Integer fk_area;

    public Cbo(){
        id_cbo = null;
        fk_area = null;
    }

    public Cbo(Integer id_cbo, String descricao, String codigo_tipo_emprego, Integer fk_area) {
        this.id_cbo = id_cbo;
        this.descricao = descricao;
        this.codigo_tipo_emprego = codigo_tipo_emprego;
        this.fk_area = fk_area;
    }

    public Integer getId_cbo() {
        return id_cbo;
    }

    public void setId_cbo(Integer id_cbo) {
        this.id_cbo = id_cbo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo_tipo_emprego() {
        return codigo_tipo_emprego;
    }

    public void setCodigo_tipo_emprego(String codigo_tipo_emprego) {
        this.codigo_tipo_emprego = codigo_tipo_emprego;
    }

    public Integer getFk_area() {
        return fk_area;
    }

    public void setFk_area(Integer fk_area) {
        this.fk_area = fk_area;
    }
}
