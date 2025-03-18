# Learnfy - Data Logger

Nosso Java utiliza a biblioteca SLF4J para coletar e registrar dados brutos de arquivos, "fazendo" a leitura de planilhas e colunas de dados. O objetivo é fornecer um sistema de log que rastreia o processo de coleta de dados, identifica possíveis problemas (como células vazias) e fornece informações sobre o progresso da coleta.

## 💻 Tecnologias

<div align="center">
  <img src="https://skillicons.dev/icons?i=java" alt="Skills" />
  <br />
</div>

## Funcionalidades

* **Inicialização do Log:**
    * Exibe um título estilizado no console para identificar o início da coleta de dados.
    * Informa o início da coleta de dados para o projeto LEARNIFY.
* **Registro de Acesso a Arquivos e Planilhas:**
    * Registra o acesso a arquivos específicos.
    * Registra a leitura de arquivos de planilha (workbooks).
    * Registra a leitura de planilhas individuais (worksheets).
* **Coleta de Dados de Colunas:**
    * Simula a coleta de dados de colunas, suportando diferentes tipos de dados (inteiros, decimais e strings).
    * Gera dados aleatórios dentro de um intervalo especificado.
    * Simula a presença de células vazias (valores nulos) em dados coletados.
    * Registra cada dado coletado com informações sobre a coluna e o valor.
    * Fornece um resumo da coleta de dados, incluindo o número de dados coletados e células vazias.
    * Emite um aviso (warn log) se o número de células vazias exceder um limite predefinido (15% dos dados coletados).
* **Mensagem Final:**
    * Registra uma mensagem final indicando o término da transformação e leitura dos dados, incluindo o nome do arquivo e as planilhas processadas.

## Como Usar

1.  **Dependências:**
    * Certifique-se de ter a biblioteca SLF4J adicionada ao seu projeto.
2.  **Instanciação:**
    * Crie uma instância da classe `Log`, passando um objeto `Logger` como parâmetro.
3.  **Coleta de Dados:**
    * Use os métodos `newFileLog`, `newWorkbook`, `newWorksheet` e `dataColumn` para registrar o progresso da coleta de dados.
    * Use os metodos `dataColumn` com os tipos de dados que desejar, int, double, ou String[]
4.  **Mensagem Final:**
    * Use o metodo `finalMessage` para registrar o fim da coleta de dados.

## Exemplo de Uso

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        Log log = new Log(logger);

        log.titleLog();
        log.newFileLog("dados.xlsx");
        log.newWorkbook("dados.xlsx");
        log.newWorksheet("Planilha1");
        log.dataColumn("ColunaA", 100, 1, 10);
        log.dataColumn("ColunaB", 100, 1.0, 10.0);
        String[] valores = {"valor1", "valor2", "valor3"};
        log.dataColumn("ColunaC", 100, valores);
        String[] worksheets = {"Planilha1"};
        log.finalMessage("dados.xlsx", worksheets);
    }
}
