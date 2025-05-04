CREATE DATABASE db_escolaridade;

USE db_escolaridade;

CREATE TABLE uf_tb (
	id_uf INT PRIMARY KEY AUTO_INCREMENT,
	sigla CHAR(2),
	nome VARCHAR(80),
	regiao VARCHAR(20)
);

CREATE TABLE municipio_tb (
	id_municipio INT PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(80),
	fk_uf INT,
	CONSTRAINT fk_municipio_uf FOREIGN KEY (fk_uf) REFERENCES uf_tb(id_uf)
);

CREATE TABLE ies_tb (
	id_ies INT PRIMARY KEY AUTO_INCREMENT,
	organizacao_academica VARCHAR(50),
	nome VARCHAR(120),
	fk_municipio INT,
	CONSTRAINT fk_ies_municipio FOREIGN KEY (fk_municipio) REFERENCES municipio_tb(id_municipio)
);

CREATE TABLE area_tb (
	id_area INT PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(100)
);

CREATE TABLE curso_tb (
	id_curso INT PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(100),
	grau_academico VARCHAR(40),
	fk_area INT,
	CONSTRAINT fk_curso_area FOREIGN KEY (fk_area) REFERENCES area_tb(id_area)
);

CREATE TABLE curso_ofertado_tb (
    id_curso_ofertado INT AUTO_INCREMENT PRIMARY KEY,
    fk_ies INT NOT NULL,
    fk_curso INT NOT NULL,
    ano INT,
    modalidade_ensino INT,
    qtd_vagas INT,
    qtd_vagas_diurno INT,
    qtd_vagas_noturno INT,
    qtd_vagas_ead INT,
    qtd_incritos INT,
    qtd_incritos_diurno INT,
    qtd_incritos_noturno INT,
    qtd_incritos_ead INT,
    qtd_concluintes INT,
    qtd_concluintes_diurno INT,
    qtd_concluintes_noturno INT,
    qtd_ingressantes_rede_publica INT,
    qtd_ingressantes_rede_privada INT,
    qtd_concluintes_rede_publica INT,
    qtd_concluintes_rede_privada INT,

    CONSTRAINT fk_curso_ofertado_ies FOREIGN KEY (fk_ies) REFERENCES ies_tb(id_ies),
    CONSTRAINT fk_curso_ofertado_curso FOREIGN KEY (fk_curso) REFERENCES curso_tb(id_curso)
);
