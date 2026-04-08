package compilador;

// Classe auxiliar de transporte (DTO).
// Utilizada pelas regras de expressão no Parser.cup para transportar duas informações agrupadas
// de baixo para cima na árvore sintática enquanto as regras vão se resolvendo.
public class ExprData {
    // Armazena o tipo associado a esta expressão sintática (ex: "int", "real", "str").
    public String type;
    
    // Armazena a representação transitória dessa variável gerada para o formato do Código Assembly Fictício (ex: "t0", "15").
    public String temp;

    // Construtor principal para receber o dado associado à geração de código.
    public ExprData(String type, String temp) {
        this.type = type;
        this.temp = temp;
    }
}
