package compilador;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// Classe responsável por representar a Tabela de Símbolos.
// Ela rastreia as variáveis declaradas e seus respectivos tipos em memória durante a fase de análise.
public class SymbolTable {
    // Uma pilha (Stack) de mapas (HashMap). Cada mapa representa o escopo local atual.
    // Usar uma pilha nos permite suportar escopos isolados (ex: variáveis isoladas dentro de um for ou if).
    private Stack<Map<String, String>> scopes;

    // O Construtor é executado ao iniciar a Tabela.
    public SymbolTable() {
        scopes = new Stack<>();
        // Empurramos (push) um mapa em branco que será considerado o escopo global.
        scopes.push(new HashMap<>());
    }

    // Método chamado pelas regras do CUP toda vez que a gramática detecta abertura de um bloco '{'
    public void enterScope() {
        // Empilha (push) um HashMap vazio representando um novo contexto isolado de chaves (local)
        scopes.push(new HashMap<>());
    }

    // Método chamado quando um bloco fecha '}' descartando as variáveis do escopo local
    public void exitScope() {
        if (scopes.size() > 1) { // Só remove se não for o escopo global (index 0)
            scopes.pop(); // Remove do topo
        }
    }

    // Tenta registrar uma variável (nome e tipo), vinculando-a obrigatoriamente no topo da pilha (escopo mais local)
    public boolean addSymbol(String name, String type) {
        // 'peek' olha para o topo da lista (o último {} aberto) sem retirá-lo da memória.
        Map<String, String> currentScope = scopes.peek();
        
        // Regra Semântica: se a gente já registrou essa variável na mesma profundidade (mesmo HashMap local), é erro!
        if (currentScope.containsKey(name)) {
            return false; // False avisa ao analisador que já foi declarada
        }
        
        // Salva a chave (ex "x") apontando para o conteúdo (ex "int")
        currentScope.put(name, type);
        return true;
    }

    // Procura o tipo vinculado a uma variável declarada, verificando em qual escopo ela mora.
    public String lookup(String name) {
        // Looping de trás pra frente: começa a procurar no local (fim da pilha) até chegar na base (global)
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, String> scope = scopes.get(i);
            
            // Se encontrar a variável em algum desses mapas pelo caminho:
            if (scope.containsKey(name)) {
                return scope.get(name); // Devolve o escopo encontrado, como "int" ou "real"
            }
        }
        // Retorna silenciosamente nulo (null) para indicar que subiu todos os níveis e a variável nunca existiu!
        return null;
    }
}
