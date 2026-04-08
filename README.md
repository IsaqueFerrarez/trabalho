# Compilador Front-End JFlex/CUP
Este projeto implementa um compilador simplificado para uma linguagem acadêmica/fictícia, dividida nas seguintes partes: Análise Léxica (JFlex), Análise Sintática (CUP), **Análise Semântica** e **Geração de Código Intermediário (Assembly Fictício)**.

## Comando para rodar o Analisador Sintático

java -cp .:/usr/share/java/cup.jar compilador.Main

## Caso seja necessário recompilar tudo, rode esses comandos:

**Apaga tudo para evitar cache**

rm compilador/*.class compilador/Lexer.java compilador/parser.java compilador/sym.java

**Gera o Parser**

java -jar /usr/share/java/cup.jar -destdir compilador -parser parser -symbols sym compilador/Parser.cup

**Gera o Lexer**

jflex compilador/Lexer.flex

**Compila tudo**

javac -cp .:/usr/share/java/cup.jar compilador/*.java

**Roda**

java -cp .:/usr/share/java/cup.jar compilador.Main

---

## Estrutura das Novas Entregas (Parte 2 do Trabalho - Semântica e Geração de Código)

Neste ciclo de entrega, o projeto foi estendido adicionando o processo de tradução e semântica por acoplamento nos eventos léxicos gerados. Foram criados os seguintes pilares listados no escopo do trabalho:

* **Tabela de Símbolos (`SymbolTable.java`)**: Cuida do mapeamento de variáveis de forma hierárquica usando Pílhas (Stacks/Escopos). Garante o ciclo de vida dinâmico entre chaves `{ ... }` permitindo recuar nomes.
* **Analisador Semântico (`SemanticAnalyzer.java`)**: Uma classe despachante para tratar erros de regras de uso. É responsável por deduzir promoções (int / real), detectar operações com strings indevidas, proibir re-declaração (`int x; int x;`) ou prever atribuição incompatível ou uso de variável ausente.
* **Gerador de Código Intermediário (`CodeGenerator.java`)**: Um motor linear fictício de string assembly com contador de Temporárias (`t0, t1, tX..`) e construtor de Labels de pulo (`L0, L1, LX..`). Através do Parser, ele escaneia e escreve comandos como `goto L0`, `ifFalse` e avaliadores de `input/output`.
* **Acoplador de Retornos (`ExprData.java`)**: Componente vital para *Tradução Dirigida pela Sintaxe* que consegue trafegar Tipo Semântico e Comando Temporary dentro do JFlex pelo nó Terminal.
* **Casos de Teste (`.txt` e `.asm`):** 
    - `teste_correto_X.txt` testam semânticas puras de tipos. Caso cheguem no fim válidos, geram em disco seus arquivos binários fictícios de compilação em `teste_correto_X.asm`.
    - `teste_erro_semantico_X.txt` quebram e disparam as mensagens padronizadas dos problemas descritos.
* Foram adicionados extensos comentários de documentação em cima de todos os métodos dos novos arquivos implementados.

### O que acontece na Execução dos Lotes de Teste (Main)?
Ao disparar o Java pelo `Main`, a nossa classe de inicialização puxa e varre consecutivamente todos os 6 testes (`teste_correto_X` e `teste_erro_semantico_X`) descritos internamente.
1. Nos casos corretos, ele imprimirá mensagens de "Sucesso!" na tela e lançará um arquivo físico no final usando a extensão convertida do Assembly fictício para evidenciar a geração de código.
2. Nos casos propositais de erro Semântico do trabalho, os alertas acusando o nome da Classe Culpada, tipo incompatível e **Linha/Coluna** exata das re-declarações e invocações proíbidas serão revelados via console.
