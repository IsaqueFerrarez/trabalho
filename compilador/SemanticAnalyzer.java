package compilador;

import java.util.ArrayList;
import java.util.List;

// Classe responsável por coordenar a Análise Semântica, delegando registros estáticos
// para a Tabela de Símbolos, e acumulando lógicas de erros semânticos detectados
// durante a varredura sintática no CUP.
public class SemanticAnalyzer {
    // Instância da Tabela de Símbolos dedicada deste analisador
    private SymbolTable symbolTable;
    // Lista de Erros acumulados em tela contendo as mensagens finais formatadas
    private List<String> errorList;

    // Inicializamos tudo no construtor
    public SemanticAnalyzer() {
        symbolTable = new SymbolTable();
        errorList = new ArrayList<>();
    }

    // Repassador para "entrar em novas chaves {", avisando à Tabela criar um local novo
    public void enterScope() {
        symbolTable.enterScope();
    }

    // Repassador para "fechar chaves }", avisando a apagar o escopo local das variáveis contidas nele
    public void exitScope() {
        symbolTable.exitScope();
    }

    // [Regra a] e [Regra do Trabalho]: Verificar se uma variável está sendo declarada novamente (Ex: int a; int a;)
    // É invocado quando a gramática do CUP enxerga algo como "tipo ID;"
    public void checkDeclaration(String varName, String type, int line, int column) {
        // Tenta registrar na SymbolTable a declaração recém-sintetizada
        // Se ela retornar falso (o nome já existir nela naquele âmbito), a gente dispara o erro:
        if (!symbolTable.addSymbol(varName, type)) {
            errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Variavel '" + varName + "' ja declarada neste escopo.");
        }
    }

    // [Regra a]: Tentar checar se a variável declarada está sendo utilizada corretamente na memória sintética.
    // É invocado nas regras "Termo ::= ID" ou ao tentar dar input numa variável "input(a)".
    public String checkUsage(String varName, int line, int column) {
        // Busca na SymbolTable pelo varName
        String type = symbolTable.lookup(varName);
        
        // Se a lookup retornar null, quer dizer que aquela variável nem estava declarada
        if (type == null) {
            errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Variavel '" + varName + "' nao declarada.");
            return "UNKNOWN"; // Marcador temporário para evitar crashes em cadeia adiante
        }
        
        // Se a lookup achar legal, retorna o tipo verdadeiro cadastrado ("int", "real", etc.)
        return type;
    }

    // [Regra b]: Verificar a compatibilidade de valores com os tipos de variáveis durante Atribuição (ex: a = 2.5)
    // É acionado ao escanear a regrinha gramatical da atribuição "ID = expressao" no CUP.
    public void checkAssignment(String idType, String exprType, int line, int column) {
        // Se um dos lados estiver envolvido na variável não declarada de antes (com um "UNKNOWN" caindo em cascata)
        // abortamos a checagem silenciando este novo erro adjacente.
        if (idType == null || idType.equals("UNKNOWN") || exprType == null || exprType.equals("UNKNOWN")) return;
        
        // Comparamos o lado esquerdo (variável) com o lado direito (expressão atribuída)
        // Cenários permitidos pelo trabalho: int recebe int; real recebe real; E real recebendo int (conversão implícita)
        if (!idType.equals(exprType)) {
            // "Se não for o caso do (real consumindo um número int de um literal)", avisar ao usuário!
            if (!(idType.equals("real") && exprType.equals("int"))) {
                errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Incompatibilidade de tipos. Impossivel atribuir '" + exprType + "' a uma variavel do tipo '" + idType + "'.");
            }
        }
    }

    // [Regra d]: Identificar erros nos operandos de expressões matemáticas (ex: String soma com Número)
    // Acionado na regra "expressao ::= expressao [SINAL] expressao".
    public String checkBinaryOperation(String type1, String type2, int line, int column) {
        // Silenciamento temporário caso de problema encadeado anterior do código
        if (type1.equals("UNKNOWN") || type2.equals("UNKNOWN")) return "UNKNOWN";
        
        // Regras puramente int geram int
        if (type1.equals("int") && type2.equals("int")) return "int";
        
        // Regras puramente reais geram real
        if (type1.equals("real") && type2.equals("real")) return "real";
        
        // Se houver uma conta misturando um "real" e um "int", o resultado se eleva a um "real" (Promoção/Casting)
        if ((type1.equals("int") && type2.equals("real")) || (type1.equals("real") && type2.equals("int"))) return "real";
        
        // Se chegou até aqui, foi um absurdo como operar uma "str", "chr" num sinal de "+" ou "-"
        errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Operacao invalida entre os tipos '" + type1 + "' e '" + type2 + "'.");
        return "UNKNOWN";
    }

    // Apenas devolvemos True se não há nenhum erro guardado para alertar sobre o desfecho.
    public boolean hasErrors() {
        return !errorList.isEmpty();
    }

    // Retorna a List do banco inteiro
    public List<String> getErrors() {
        return errorList;
    }

    // Escreve brutalmente tudo de que deu errado no std_err caso o main chame.
    public void printErrors() {
        for (String err : errorList) {
            System.err.println(err);
        }
    }
}
