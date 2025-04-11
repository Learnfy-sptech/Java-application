//package com.learnfy;
//
//public class DadosCurso {
//
//    private Integer ano;
//    private String sigla_uf;
//    private Integer id_municipio;
//    private Integer tipo_dimensao;
//    private Integer tipo_organizacao_academica;
//    private Integer tipo_organizacao_administrativa;
//    private Integer rede;
//    private String nome_curso;
//
//    public DadosCurso() {
//    }
//
//    public DadosCurso(Integer ano, String sigla_uf, Integer id_municipio, Integer tipo_dimensao, Integer tipo_organizacao_academica, Integer tipo_organizacao_administrativa, Integer rede, Integer id_ies, String nome_curso) {
//        this.ano = ano;
//        this.sigla_uf = sigla_uf;
//        this.id_municipio = id_municipio;
//        this.tipo_dimensao = tipo_dimensao;
//        this.tipo_organizacao_academica = tipo_organizacao_academica;
//        this.tipo_organizacao_administrativa = tipo_organizacao_administrativa;
//        this.rede = rede;
//        this.nome_curso = nome_curso;
//    }
//
//    public Integer getAno() {
//        return ano;
//    }
//
//    public void setAno(Integer ano) {
//        this.ano = ano;
//    }
//
//    public String getSigla_uf() {
//        return sigla_uf;
//    }
//
//    public void setSigla_uf(String sigla_uf) {
//        this.sigla_uf = sigla_uf;
//    }
//
//    public Integer getId_municipio() {
//        return id_municipio;
//    }
//
//    public void setId_municipio(Integer id_municipio) {
//        this.id_municipio = id_municipio;
//    }
//
//    public Integer getTipo_dimensao() {
//        return tipo_dimensao;
//    }
//
//    public void setTipo_dimensao(Integer tipo_dimensao) {
//        this.tipo_dimensao = tipo_dimensao;
//    }
//
//    public Integer getTipo_organizacao_academica() {
//        return tipo_organizacao_academica;
//    }
//
//    public void setTipo_organizacao_academica(Integer tipo_organizacao_academica) {
//        this.tipo_organizacao_academica = tipo_organizacao_academica;
//    }
//
//    public Integer getTipo_organizacao_administrativa() {
//        return tipo_organizacao_administrativa;
//    }
//
//    public void setTipo_organizacao_administrativa(Integer tipo_organizacao_administrativa) {
//        this.tipo_organizacao_administrativa = tipo_organizacao_administrativa;
//    }
//
//    public Integer getRede() {
//        return rede;
//    }
//
//    public void setRede(Integer rede) {
//        this.rede = rede;
//    }
//
//    public String getNome_curso() {
//        return nome_curso;
//    }
//
//    public void setNome_curso(String nome_curso) {
//        this.nome_curso = nome_curso;
//    }
//
//}


// ------------------------------------------ //
// TESTE SEGUNDA PLANILHA, COMENTEI O MÉTODO ACIMA POIS OS CAMPOS NÃO BATERAM COM A PLANILHA QUE EXPORTEI
// ------------------------------------------ //

package com.learnfy;

public class DadosCurso {

    private Integer ano;
    private String sigla_uf;
    private String nome_curso_cine;
    private String nome_area_geral;
    private Integer quantidade_vagas_processos_seletivos;
    private Integer quantidade_inscritos;
    private Integer quantidade_inscritos_ead;
    private Integer quantidade_ingressantes_60_mais;
    private Integer quantidade_matriculas;
    private Integer quantidade_concluintes;

    public DadosCurso() {
    }

    public DadosCurso(Integer ano, String sigla_uf, String nome_curso_cine, String nome_area_geral,
                      Integer quantidade_vagas_processos_seletivos, Integer quantidade_inscritos,
                      Integer quantidade_inscritos_ead, Integer quantidade_ingressantes_60_mais,
                      Integer quantidade_matriculas, Integer quantidade_concluintes) {
        this.ano = ano;
        this.sigla_uf = sigla_uf;
        this.nome_curso_cine = nome_curso_cine;
        this.nome_area_geral = nome_area_geral;
        this.quantidade_vagas_processos_seletivos = quantidade_vagas_processos_seletivos;
        this.quantidade_inscritos = quantidade_inscritos;
        this.quantidade_inscritos_ead = quantidade_inscritos_ead;
        this.quantidade_ingressantes_60_mais = quantidade_ingressantes_60_mais;
        this.quantidade_matriculas = quantidade_matriculas;
        this.quantidade_concluintes = quantidade_concluintes;
    }

    // Getters e Setters
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getSigla_uf() { return sigla_uf; }
    public void setSigla_uf(String sigla_uf) { this.sigla_uf = sigla_uf; }

    public String getNome_curso_cine() { return nome_curso_cine; }
    public void setNome_curso_cine(String nome_curso_cine) { this.nome_curso_cine = nome_curso_cine; }

    public String getNome_area_geral() { return nome_area_geral; }
    public void setNome_area_geral(String nome_area_geral) { this.nome_area_geral = nome_area_geral; }

    public Integer getQuantidade_vagas_processos_seletivos() { return quantidade_vagas_processos_seletivos; }
    public void setQuantidade_vagas_processos_seletivos(Integer quantidade_vagas_processos_seletivos) {
        this.quantidade_vagas_processos_seletivos = quantidade_vagas_processos_seletivos;
    }

    public Integer getQuantidade_inscritos() { return quantidade_inscritos; }
    public void setQuantidade_inscritos(Integer quantidade_inscritos) {
        this.quantidade_inscritos = quantidade_inscritos;
    }

    public Integer getQuantidade_inscritos_ead() { return quantidade_inscritos_ead; }
    public void setQuantidade_inscritos_ead(Integer quantidade_inscritos_ead) {
        this.quantidade_inscritos_ead = quantidade_inscritos_ead;
    }

    public Integer getQuantidade_ingressantes_60_mais() { return quantidade_ingressantes_60_mais; }
    public void setQuantidade_ingressantes_60_mais(Integer quantidade_ingressantes_60_mais) {
        this.quantidade_ingressantes_60_mais = quantidade_ingressantes_60_mais;
    }

    public Integer getQuantidade_matriculas() { return quantidade_matriculas; }
    public void setQuantidade_matriculas(Integer quantidade_matriculas) {
        this.quantidade_matriculas = quantidade_matriculas;
    }

    public Integer getQuantidade_concluintes() { return quantidade_concluintes; }
    public void setQuantidade_concluintes(Integer quantidade_concluintes) {
        this.quantidade_concluintes = quantidade_concluintes;
    }
}

