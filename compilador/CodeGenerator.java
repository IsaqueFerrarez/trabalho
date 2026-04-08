package compilador;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private List<String> instructions;
    private int tempCounter;
    private int labelCounter;

    public CodeGenerator() {
        instructions = new ArrayList<>();
        tempCounter = 0;
        labelCounter = 0;
    }

    public void emit(String instruction) {
        instructions.add(instruction);
    }

    public String newTemp() {
        return "t" + (tempCounter++);
    }

    public String newLabel() {
        return "L" + (labelCounter++);
    }

    public void printCode() {
        for (String instr : instructions) {
            System.out.println(instr);
        }
    }

    public List<String> getCode() {
        return instructions;
    }
}
