package com.learnfy.processador;

import com.learnfy.logs.LogService;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.List;

public class ProcessadorFactory {
    public static Processador getProcessador(String key, JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName, LogService logService) {
         return switch (key) {
            case "planilhas/dados_empregabilidade/empregabilidade.xlsx" -> new ProcessadorEmpregabilidade(jdbcTemplate, s3Client, bucketName, logService);
            case "planilhas/dados_cursos/estados.xlsx" -> new ProcessadorUf(jdbcTemplate, s3Client, logService);
            case "planilhas/dados_cursos/municipios.xlsx" -> new ProcessadorMunicipio(jdbcTemplate, s3Client, logService);
            case "planilhas/dados_cursos/instituicoes_ensino.xlsx" -> new ProcessadorIes(jdbcTemplate, s3Client, logService);
            case "planilhas/dados_cursos/cursos_areas.xlsx" -> new ProcessadorCursoArea(jdbcTemplate, s3Client, logService);
            case "planilhas/dados_cursos/cursos_ofertados.xlsx" -> new ProcessadorCursoOfertado(jdbcTemplate, s3Client, logService);
             default -> {
                 logService.registrarLog("ProcessadorFactory.java", "ProcessadorFactory", "CRÍTICO", "Nome do arquivo inválido");
                 yield null;
             }
         };
    }
}
