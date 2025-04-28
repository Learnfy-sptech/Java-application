package com.learnfy;

import com.learnfy.entity.CursoOfertado;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeitorDeArquivos {

    JdbcTemplate jdbcTemplate = new ConexaoBanco().getJdbcTemplate();
    private final List<Object[]> batchArgs = new ArrayList<>();
    private final int batchSize = 1000;

    public void importarCursos(String pathFile) throws IOException, SAXException, OpenXML4JException {
        IOUtils.setByteArrayMaxOverride(1_000_000_000);
        OPCPackage arquivo = OPCPackage.open(pathFile);
        XSSFReader leitor = new XSSFReader(arquivo);
        ReadOnlySharedStringsTable texto = new ReadOnlySharedStringsTable(arquivo);
        InputStream folha = leitor.getSheetsData().next();

        XSSFSheetXMLHandler.SheetContentsHandler manipulador = new XSSFSheetXMLHandler.SheetContentsHandler() {
            List<String> valoresLinha;
            Integer colunaAtual = -1;

            @Override
            public void startRow(int rowNum) {
                valoresLinha = new ArrayList<>();
                colunaAtual = -1;
            }

            @Override
            public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                int coluna = converterLetraParaIndice(cellReference.replaceAll("\\d", ""));
                while (++colunaAtual < coluna) {
                    valoresLinha.add(null);
                }
                valoresLinha.add(formattedValue != null ? formattedValue.trim() : null);
                colunaAtual = coluna;
            }

            @Override
            public void endRow(int rowNum) {
                if (rowNum == 0) return; // Pular cabeçalho
                try {
                    Object[] params = new Object[]{
                            // Arrumar essa parte
                            customParseLong(valoresLinha.size() > 0 ? valoresLinha.get(0) : null),
                            customParseLong(valoresLinha.size() > 1 ? valoresLinha.get(1) : null),
                            customParseInteger(valoresLinha.size() > 2 ? valoresLinha.get(2) : null),
                            customParseInteger(valoresLinha.size() > 3 ? valoresLinha.get(3) : null),
                            customParseInteger(valoresLinha.size() > 4 ? valoresLinha.get(4) : null),
                            customParseInteger(valoresLinha.size() > 5 ? valoresLinha.get(5) : null),
                            customParseInteger(valoresLinha.size() > 6 ? valoresLinha.get(6) : null),
                            customParseInteger(valoresLinha.size() > 7 ? valoresLinha.get(7) : null),
                            customParseInteger(valoresLinha.size() > 8 ? valoresLinha.get(8) : null),
                            customParseInteger(valoresLinha.size() > 9 ? valoresLinha.get(9) : null),
                            customParseInteger(valoresLinha.size() > 10 ? valoresLinha.get(10) : null),
                            customParseInteger(valoresLinha.size() > 11 ? valoresLinha.get(11) : null),
                            customParseInteger(valoresLinha.size() > 12 ? valoresLinha.get(12) : null),
                            customParseInteger(valoresLinha.size() > 13 ? valoresLinha.get(13) : null),
                            customParseInteger(valoresLinha.size() > 14 ? valoresLinha.get(14) : null),
                            customParseInteger(valoresLinha.size() > 15 ? valoresLinha.get(15) : null),
                            customParseInteger(valoresLinha.size() > 16 ? valoresLinha.get(16) : null),
                            customParseInteger(valoresLinha.size() > 17 ? valoresLinha.get(17) : null)
                    };

                    batchArgs.add(params);

                    if (batchArgs.size() >= batchSize) {
                        inserirCursosOfertados();
                    }

                } catch (Exception e) {
                    System.out.printf("Erro ao capturar os dados da linha %d: %s\n", rowNum, e.getMessage());
                }
            }
        };

        XMLReader parser = XMLReaderFactory.createXMLReader();
        XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(null, null, texto, manipulador, null, false);
        parser.setContentHandler(xmlHandler);
        parser.parse(new InputSource(folha));
        folha.close();

        if (!batchArgs.isEmpty()) {
            inserirCursosOfertados();
        }
    }

    private void inserirCursosOfertados() {

        String sql = "INSERT INTO curso_ofertado (" +
                "fk_ies, fk_curso, ano, modalidade_ensino, qtd_vagas, qtd_vagas_diurno, qtd_vagas_noturno, qtd_vagas_ead, " +
                "qtd_incritos, qtd_incritos_diurno, qtd_incritos_noturno, qtd_incritos_ead, " +
                "qtd_concluintes_diurno, qtd_concluintes_noturno, qtd_ingressantes_rede_publica, qtd_ingressantes_rede_privada, " +
                "qtd_concluintes_rede_publica, qtd_concluintes_rede_privada" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] params = batchArgs.get(i);
                for (int j = 0; j < params.length; j++) {
                    ps.setObject(j + 1, params[j]);
                }
            }

            @Override
            public int getBatchSize() {
                return batchArgs.size();
            }
        });

        batchArgs.clear();
    }

    private static Integer converterLetraParaIndice(String coluna) {
        int indice = 0;
        for (int i = 0; i < coluna.length(); i++) {
            indice *= 26;
            indice += coluna.charAt(i) - 'A' + 1;
        }
        return indice - 1;
    }

    private static Integer customParseInteger(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return Integer.parseInt(valor);
    }

    private static Long customParseLong(String valor) {
        if (valor == null || valor.isBlank()) return null;
        return Long.parseLong(valor);
    }
}
