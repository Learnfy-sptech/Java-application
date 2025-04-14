package com.learnfy;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LeitorDeArquivos {

    ConexaoBanco conexaoBanco = new ConexaoBanco();

    public List<Curso> obterDadosCurso(String pathFile) throws IOException, SAXException, OpenXML4JException {

        List<Curso> cursosArquivo = new ArrayList<>();

        IOUtils.setByteArrayMaxOverride(1_000_000_000);
        OPCPackage arquivo = OPCPackage.open(pathFile);
        XSSFReader leitor = new XSSFReader(arquivo);
        ReadOnlySharedStringsTable texto = new ReadOnlySharedStringsTable(arquivo);
        InputStream folha = leitor.getSheetsData().next();

        XSSFSheetXMLHandler.SheetContentsHandler manipulador = new XSSFSheetXMLHandler.SheetContentsHandler() {
            List<String> valoresLinha;
            Curso curso;
            ConexaoBanco conexaoBanco;

            // Método que será executado a cada início de linha
            @Override
            public void startRow(int rowNum) {
                curso = new Curso();
                valoresLinha = new ArrayList<>();
            }

            Integer colunaAtual = -1;

            // Método que será executado a cada célula lida
            @Override
            public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                // Verifica a coluna atual para saber se foi pulada alguma célula vazia
                int coluna = converterLetraParaIndice(cellReference.replaceAll("\\d", ""));

                /*
                    Se a coluna da célula for diferente da coluna que era para ser a atual, serão
                    adicionados valores nulos para garantir a compatibilidade do vetor com a quantidade
                    de atributos da classe
                */
                while (++colunaAtual < coluna) {
                    valoresLinha.add(null);
                }

                valoresLinha.add(formattedValue.trim());
                colunaAtual = coluna;
            }

            // Método que será executado no final de cada linha
            @Override
            public void endRow(int rowNum) {
                if (rowNum == 0) return;
                try {
                    // Atribuindo os valores da lista aos atributos do objeto
                    curso.setAno(customParseInteger(valoresLinha.get(0)));
                    curso.setSiglaUf(valoresLinha.get(1));
                    curso.setIdMunicipio(customParseInteger(valoresLinha.get(2)));
                    curso.setRede(valoresLinha.get(3));
                    curso.setIdIes(customParseInteger(valoresLinha.get(4)));
                    curso.setNomeCurso(valoresLinha.get(5));
                    curso.setNomeArea(valoresLinha.get(6));
                    curso.setGrauAcademico(customParseInteger(valoresLinha.get(7)));
                    curso.setModalidadeEnsino(customParseInteger(valoresLinha.get(8)));
                    curso.setQtdVagas(customParseInteger(valoresLinha.get(9)));
                    curso.setQtdVagasDiurno(customParseInteger(valoresLinha.get(10)));
                    curso.setQtdVagasNoturno(customParseInteger(valoresLinha.get(11)));
                    curso.setQtdVagasEad(customParseInteger(valoresLinha.get(12)));
                    curso.setQtdIncritos(customParseInteger(valoresLinha.get(13)));
                    curso.setQtdIncritosDiurno(customParseInteger(valoresLinha.get(14)));
                    curso.setQtdIncritosNoturno(customParseInteger(valoresLinha.get(15)));
                    curso.setQtdIncritosEad(customParseInteger(valoresLinha.get(16)));
                    curso.setQtdConcluintesDiurno(customParseInteger(valoresLinha.get(17)));
                    curso.setQtdConcluintesNoturno(customParseInteger(valoresLinha.get(18)));
                    curso.setQtdIngressantesRedePublica(customParseInteger(valoresLinha.get(19)));
                    curso.setQtdIngressantesRedePrivada(customParseInteger(valoresLinha.get(20)));
                    curso.setQtdConcluintesRedePublica(customParseInteger(valoresLinha.get(21)));
                    curso.setQtdConcluintesRedePrivada(customParseInteger(valoresLinha.get(22)));
                    curso.setQtdIngressantesAtividadeExtra(customParseInteger(valoresLinha.get(23)));
                    curso.setQtdConcluintesAtividadeExtra(customParseInteger(valoresLinha.get(24)));
                    cursosArquivo.add(curso);
                } catch (Exception e) {
                    System.out.printf("Erro ao inserir linha %d: %s", rowNum, e.getMessage());
                }
            }
        };

        XMLReader parser = XMLReaderFactory.createXMLReader();
        XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(null, null, texto, manipulador, null, false);
        parser.setContentHandler(xmlHandler);
        parser.parse(new InputSource(folha));
        folha.close();

        return cursosArquivo;
    }

    private static Integer converterLetraParaIndice(String coluna) {
        Integer indice = 0;
        for (int i = 0; i < coluna.length(); i++) {
            // Quantidade de colunas existente na tabela
            indice *= 26;
            indice += coluna.charAt(i) - 'A' + 1;
        }
        return indice - 1;
    }

    private Integer customParseInteger(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return Integer.parseInt(valor);
    }

    public void inserirCursos(List<Curso> cursos) {
        for (Curso curso : cursos) {
            conexaoBanco.insertCurso(curso);
        }
    }

}

