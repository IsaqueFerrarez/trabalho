package compilador;

import java.util.ArrayList;
import java.util.List;

// Classe responsável pela [Geração de Código Intermediário] fictício
// Baseado na Tradução Dirigida pela Sintaxe, ela vai sendo "notificada" pelas regras do Parser.
public class CodeGenerator {
    // Array interno que guarda sequencialmente todas as linhas geradas pelo compilador em memória.
    private List<String> instructions;

    // Dois rastreadores numéricos que não se repetem:
    // Para variáveis temporárias de operações (ex t0, t1, t2...)
    private int tempCounter;
    // Para a geração de rótulos dos laços condicionais e ciclos (Goto e Jumps, ex L0, L1, L2...)
    private int labelCounter;

    // Inicialização ao rodar o arquivo Main.
    public CodeGenerator() {
        instructions = new ArrayList<>();
        tempCounter = 0;
        labelCounter = 0;
    }

    // Ação principal! Chamada pelo Parser.cup `codeGen.emit("t0 = a + b")` 
    // É o mecanismo que anexa a instrução traduzida na fila "quente".
    public void emit(String instruction) {
        instructions.add(instruction);
    }

    // Fornece um nome inédito para guardar o resultado temporário de uma operação em árvore
    // Exemplo: newTemp() = "t0", então se a pessoa fizer "X + 2", a saída fictícia será "t0 = X + 2". 
    public String newTemp() {
        return "t" + (tempCounter++); // Retorna o texto concatenado e soma +1 ao contador na sequência.
    }

    // Fornece um nome para endereço inédito em fluxos JUMP de assembly.
    // Usado nos "if (...) goto LX".
    public String newLabel() {
        return "L" + (labelCounter++); // Ex: Retorna L0, depois L1, logo depois L2.
    }

    // Facilita visualização do array printando por tela toda a tradução completa
    public void printCode() {
        for (String instr : instructions) {
            System.out.println(instr);
        }
    }

    // Apenas cospe todas as regras armazenas de forma não-formatada em array caso o método pai precise.
    public List<String> getCode() {
        return instructions;
    }
}
