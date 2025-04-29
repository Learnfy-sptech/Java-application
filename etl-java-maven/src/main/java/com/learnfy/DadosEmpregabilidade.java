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

public class DadosEmpregabilidade {

    private Integer ano;
    private String sigla_uf;
    private Integer cbo_2002;
    private String cbo_2002_descricao;
    private String cbo_2002_descricao_familia;
    private String categoria;
    private String grau_instrucao;
    private Double salario_mensal;

    public DadosEmpregabilidade() {
    }

    public DadosEmpregabilidade(Integer ano, String sigla_uf, Integer cbo_2002, String cbo_2002_descricao,
                                String cbo_2002_descricao_familia, String categoria, String grau_instrucao, Double salario_mensal) {
        this.ano = ano;
        this.sigla_uf = sigla_uf;
        this.cbo_2002 = cbo_2002;
        this.cbo_2002_descricao = cbo_2002_descricao;
        this.cbo_2002_descricao_familia = cbo_2002_descricao_familia;
        this.categoria = categoria;
        this.grau_instrucao = grau_instrucao;
        this.salario_mensal = salario_mensal;
    }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getSigla_uf() { return sigla_uf; }
    public void setSigla_uf(String sigla_uf) { this.sigla_uf = sigla_uf; }

    public Integer getCbo_2002() { return cbo_2002; }

    public void setCbo_2002(Integer cbo_2002) { this.cbo_2002 = cbo_2002;}

    public String getcbo_2002_descricao() { return cbo_2002_descricao; }
    public void setcbo_2002_descricao(String cbo_2002_descricao) { this.cbo_2002_descricao = cbo_2002_descricao; }

    public String getcbo_2002_descricao_familia() { return cbo_2002_descricao_familia; }
    public void setcbo_2002_descricao_familia(String cbo_2002_descricao_familia)
    { this.cbo_2002_descricao_familia = cbo_2002_descricao_familia; }

    public String getCategoria() {return categoria;}

    public void setCategoria(String categoria) {this.categoria = categoria;}

    public String getgrau_instrucao() { return grau_instrucao; }
    public void setgrau_instrucao(String grau_instrucao) {
        this.grau_instrucao = grau_instrucao;
    }

    public Double getSalario_mensal() { return salario_mensal; }
    public void setSalario_mensal(Double salario_mensal) { this.salario_mensal = salario_mensal; }
}

