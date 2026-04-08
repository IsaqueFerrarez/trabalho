# Compilador Front-End JFlex/CUP
Este projeto implementa um compilador simplificado para uma linguagem acadêmica/fictícia, dividida nas seguintes partes: Análise Léxica (JFlex), Análise Sintática (CUP), **Análise Semântica** e **Geração de Código Intermediário (Assembly Fictício)**.

## Estrutura das Novas Entregas (Parte 2 do Trabalho)
Neste ciclo de entrega, o projeto foi estendido adicionando o processo de tradução e semântica por acoplamento nos eventos léxicos gerados. Foram criados os seguintes pilares listados no escopo do trabalho:

* **Tabela de Símbolos (`SymbolTable.java`)**: Cuida do mapeamento de variáveis de forma hierárquica usando Pílhas (Stacks/Escopos). Garante o ciclo de vida dinâmico entre chaves `{ ... }` permitindo recuar nomes.
* **Analisador Semântico (`SemanticAnalyzer.java`)**: Uma classe despachante para tratar erros de regras de uso. É responsável por deduzir promoções (int / real), detectar operações com strings indevidas, proibir re-declaração (`int x; int x;`) ou prever atribuição incompatível ou uso de variável ausente.
* **Gerador de Código Intermediário (`CodeGenerator.java`)**: Um motor linear fictício de string assembly com contador de Temporárias (`t0, t1, tX..`) e construtor de Labels de pulo (`L0, L1, LX..`). Através do Parser, ele escaneia e escreve comandos como `goto L0`, `ifFalse` e avaliadores de `input/output`.
* **Acoplador de Retornos (`ExprData.java`)**: Componente vital para *Tradução Dirigida pela Sintaxe* que consegue trafegar Tipo Semântico e Comando Temporary dentro do JFlex pelo nó Terminal.
* **Casos de Teste (`.txt` e `.asm`):** 
    - `teste_correto_X.txt` testam semânticas puras de tipos. Caso cheguem no fim válidos, geram em disco seus arquivos binários fictícios de compilação em `teste_correto_X.asm`.
    - `teste_erro_semantico_X.txt` quebram e disparam as mensagens padronizadas dos problemas descritos.
* Foram adicionados extensos comentários de documentação em cima de todos os métodos dos novos arquivos implementados.

## Comando para rodar o Compilador Completo

Assumindo que você possui o Java habilitado ou importado dentro da interface de terminal do projeto.

### 1- Recompilar toda a Especificação JFlex/CUP modificada:

*Você deve executar os comandos estando DENTRO da pasta `compilador/`:*

```bash
# Apagando caches prévios caso recompile
rm *.class Lexer.java parser.java sym.java

# 1. Regenerar Léxico a partir do .flex
jflex Lexer.flex

# 2. Regenerar Parser a partir do .cup modificado (Injeta o Semântico)
# Use o -cp se o java-cup estiver local, ou use o jar isolado apontado ex: -jar /usr/share/...
java -cp .. java_cup.Main -parser parser -symbols sym Parser.cup

# 3. Recompilar o Bytecode do projeto inteiro (Classes novas implementadas)
javac -cp .. *.java

# 4. Executar os Lotes (Injeta e traduz os 6 testes novos)
java -cp .. compilador.Main
```

### O que acontece na Execução?
Ao disparar o Java pelo `Main`, a nossa classe de inicialização puxa e varre consecutivamente todos os 6 testes descritos no root do projeto.
1. Nos casos corretos, ele imprimirá mensagens de "Sucesso!" na tela e lançara um arquivo físico no final usando a extensão convertida do Assembly para visualizar os Gotos.
2. Nos casos propositais de erro Semântico do trabalho, os alertas acusando o nome da Classe Culpada, tipo incompatível e **Linha/Coluna** exata das re-declarações e invocações proíbidas serão revelados via StdOut pelo rastreador de *SemanticAnalyzer*.
