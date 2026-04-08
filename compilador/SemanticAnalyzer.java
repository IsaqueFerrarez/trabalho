package compilador;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private List<String> errorList;

    public SemanticAnalyzer() {
        symbolTable = new SymbolTable();
        errorList = new ArrayList<>();
    }

    public void enterScope() {
        symbolTable.enterScope();
    }

    public void exitScope() {
        symbolTable.exitScope();
    }

    public void checkDeclaration(String varName, String type, int line, int column) {
        if (!symbolTable.addSymbol(varName, type)) {
            errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Variavel '" + varName + "' ja declarada neste escopo.");
        }
    }

    public String checkUsage(String varName, int line, int column) {
        String type = symbolTable.lookup(varName);
        if (type == null) {
            errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Variavel '" + varName + "' nao declarada.");
            return "UNKNOWN";
        }
        return type;
    }

    public void checkAssignment(String idType, String exprType, int line, int column) {
        if (idType == null || idType.equals("UNKNOWN") || exprType == null || exprType.equals("UNKNOWN")) return;
        
        // Allowed: int=int, real=real, real=int. Not allowed: int=real, str=int, etc.
        if (!idType.equals(exprType)) {
            if (!(idType.equals("real") && exprType.equals("int"))) {
                errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Incompatibilidade de tipos. Impossivel atribuir '" + exprType + "' a uma variavel do tipo '" + idType + "'.");
            }
        }
    }

    public String checkBinaryOperation(String type1, String type2, int line, int column) {
        if (type1.equals("UNKNOWN") || type2.equals("UNKNOWN")) return "UNKNOWN";
        
        if (type1.equals("int") && type2.equals("int")) return "int";
        if (type1.equals("real") && type2.equals("real")) return "real";
        if ((type1.equals("int") && type2.equals("real")) || (type1.equals("real") && type2.equals("int"))) return "real";
        
        errorList.add("Erro Semantico na linha " + line + ", coluna " + column + ": Operacao invalida entre os tipos '" + type1 + "' e '" + type2 + "'.");
        return "UNKNOWN";
    }

    public boolean hasErrors() {
        return !errorList.isEmpty();
    }

    public List<String> getErrors() {
        return errorList;
    }

    public void printErrors() {
        for (String err : errorList) {
            System.err.println(err);
        }
    }
}
