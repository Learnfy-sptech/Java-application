package com.learnfy;

import com.learnfy.entity.CursoOfertado;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, OpenXML4JException, SAXException {

        LeitorDeArquivos leitorDeArquivos = new LeitorDeArquivos();
        List<CursoOfertado> cursos = leitorDeArquivos.obterDadosCurso("/home/ubuntu/base-dados-cursos-ensino-superior.xlsx");
        leitorDeArquivos.inserirCursos(cursos);

    }
}