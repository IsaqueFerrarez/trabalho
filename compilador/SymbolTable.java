package compilador;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {
    private Stack<Map<String, String>> scopes;

    public SymbolTable() {
        scopes = new Stack<>();
        // Global scope
        scopes.push(new HashMap<>());
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.pop();
        }
    }

    public boolean addSymbol(String name, String type) {
        Map<String, String> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            return false; // Already declared in current scope
        }
        currentScope.put(name, type);
        return true;
    }

    public String lookup(String name) {
        // Search from inner-most to outer-most scope
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopes.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // Not found
    }
}
