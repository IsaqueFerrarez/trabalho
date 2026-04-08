package compilador;

public class ExprData {
    public String type;
    public String temp; // For Code Gen (e.g., "t0")
    public String code; // Sub-expression generated code, optional depending on generation strategy

    public ExprData(String type, String temp) {
        this.type = type;
        this.temp = temp;
        this.code = "";
    }
}
