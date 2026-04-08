package compilador;

import java.io.FileReader;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String rootPath = Paths.get("").toAbsolutePath().toString();
        String subPath = "/compilador/"; 

        String[] arquivosTeste = {
            "teste_correto_1.txt", "teste_correto_2.txt", "teste_correto_3.txt",
            "teste_erro_semantico_1.txt", "teste_erro_semantico_2.txt", "teste_erro_semantico_3.txt"
        };

        for (String arquivo : arquivosTeste) {
            System.out.println("-----------------------------------------");
            String caminhoCompleto = rootPath + subPath + arquivo;
            System.out.println("Lendo arquivo: " + caminhoCompleto);

            try {
                File f = new File(caminhoCompleto);
                if (!f.exists()) {
                    System.out.println("ERRO: Arquivo não encontrado neste local!");
                    continue;
                }

                Lexer lexer = new Lexer(new FileReader(caminhoCompleto));
                parser p = new parser(lexer);
                p.parse();
                
                if (p.semAnal.hasErrors()) {
                    System.out.println("Erros Semanticos encontrados:");
                    for(String err : p.semAnal.getErrors()) {
                        System.out.println(err);
                    }
                } else {
                    System.out.println("Sucesso! Sintaxe e Semântica corretas.");
                    String outputName = caminhoCompleto.replace(".txt", ".asm");
                    
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputName))) {
                        List<String> code = p.codeGen.getCode();
                        for (String instr : code) {
                            writer.write(instr);
                            writer.newLine();
                        }
                        System.out.println("Código gerado e salvo em: " + outputName);
                    } catch (Exception ioE) {
                        System.out.println("Erro ao salvar o arquivo ASM.");
                        ioE.printStackTrace();
                    }
                }

            } catch (Exception e) {
                System.out.println("ERRO NA COMPILAÇÃO:");
                e.printStackTrace(); 
            }
        }
    }
}
