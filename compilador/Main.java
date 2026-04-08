package compilador;

import java.io.FileReader;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.List;

// Coração do projeto! Inicializa as rotinas de compilação em lotes de arquivos ".txt".
public class Main {
    public static void main(String[] args) {
        // Encontra o diretório base onde você realizou a execução do `java Main` pelo terminal.
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String subPath = "/compilador/"; // Pasta relativa aos textos a se localizar

        // Trazemos pelo escopo do [Parte 2] : Leitura dos nossos lotes de testes (3 certos, 3 falsos).
        String[] arquivosTeste = {
            "teste_correto_1.txt", "teste_correto_2.txt", "teste_correto_3.txt",
            "teste_erro_semantico_1.txt", "teste_erro_semantico_2.txt", "teste_erro_semantico_3.txt"
        };

        // For para passar em cada um dos seis arquivos listados em cima:
        for (String arquivo : arquivosTeste) {
            System.out.println("-----------------------------------------");    
            String caminhoCompleto = rootPath + subPath + arquivo; // Ex: .../compilador/teste_correto_1.txt
            System.out.println("Lendo arquivo: " + caminhoCompleto);

            try {
                // Checa se o teste não sumiu ou não foi rodado local errado
                File f = new File(caminhoCompleto);
                if (!f.exists()) {
                    System.out.println("ERRO: Arquivo não encontrado neste local!");
                    continue; // Pula automaticamente para o próximo se não achou esse arq
                }

                // Ativa a [Parte 1] - Dispara Análise Léxica pela engine original
                Lexer lexer = new Lexer(new FileReader(caminhoCompleto));       
                // Ativa a [Parte 1 e Parte 2 Mista] - Dispara o Analisador Sintático via varredura do Léxico
                parser p = new parser(lexer);
                // Como anexamos ações de SemanticAnalyzer junto do Analisador Sintático "p", 
                // dar the parse() abaixo ativará recursivamente todos os nossos scripts paralelos ao analisar os Tokens! 
                p.parse();

                // Após fechar o parser da árvore daquele `.txt` completamente:
                // Invocamos a análise de "p" e vemos se alguma regra estourou algo nos relatórios anexos!
                if (p.semAnal.hasErrors()) {
                    // [Situação DE ERRO DE CONTEXTO SEMÂNTICO]
                    System.out.println("Erros Semanticos encontrados:");        
                    // Soltamos todos os prints explicativos daquele arquivo na tela do usuário.
                    for(String err : p.semAnal.getErrors()) {
                        System.out.println(err);
                    }
                } else {
                    // [Situação CORRETA - Não contêm infrições léxicas, sintáticas e nem semânticas]
                    System.out.println("Sucesso! Sintaxe e Semântica corretas.");
                    
                    // Modifica o sufixo do arquivo original (Substituíndo seu exato percusso de .txt para .asm)
                    String outputName = caminhoCompleto.replace(".txt", ".asm");

                    // Abre o Writer em disco invocando o fluxo com código puro extraído de CodeGenerator.getCode()
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputName))) {
                        List<String> code = p.codeGen.getCode(); // Pega aquele List de "Strings de JUMP e Temp"
                        for (String instr : code) { // Escreve linha a linha num forEach
                            writer.write(instr);    
                            writer.newLine(); // Adiciona linha de baixo \n
                        }
                        System.out.println("Código gerado e salvo em: " + outputName);
                    } catch (Exception ioE) {
                        System.out.println("Erro ao salvar o arquivo ASM.");    
                        ioE.printStackTrace();
                    }
                }

            } catch (Exception e) { // Se deu catch aqui fora, não fechou o sintático (Syntax Error Fatal) explodindo o terminal do projeto original do cara.
                System.out.println("ERRO NA COMPILAÇÃO:");
                e.printStackTrace();
            }
        }
    }
}
