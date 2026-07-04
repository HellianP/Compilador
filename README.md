Compilador completo, escrito em Java, para uma linguagem imperativa simples (estilo Pascal), desenvolvido para a disciplina de Compiladores (Prof. Marcus Ramos).

O compilador implementa as cinco fases clássicas de um compilador, usando o padrão de projeto **Visitor** para percorrer a Árvore Sintática Abstrata (AST) em cada etapa:

1. **Análise Léxica** — `Scanner`
2. **Análise Sintática** — `Parser` (descida recursiva, gramática LL(1))
3. **Impressão da AST** — `Printer`
4. **Análise de Contexto** (escopo e tipos) — `Checker`
5. **Geração de Código** — `CodeGenerator` (código-objeto para a máquina TAM)

Documentação completa: [`Documentos/Documentação do Compilador.pdf`](Documentos/) (gramática, tokens, exemplos de entrada/saída, manuais de compilação e utilização).

## Estrutura do projeto
src/compiler/
├── Compiler.java                  # orquestrador principal (main)
├── Token.java                     # definição dos tokens da linguagem
├── ShowError.java                 # tratamento de erros
├── analise_lexica/Scanner.java
├── analise_sintatica/Parser.java
├── analise_de_contexto/           # Checker, IdentificationTable, Type, Attribute
├── geracao_de_codigo/CodeGenerator.java
└── visitor/                       # classes de nó da AST + Printer + interface Visitor
test/            # arquivos-fonte de exemplo (code0.txt, code1.txt, ...)
Documentos/      # documentação completa do projeto
Gramática LL1.txt, tokens e separadores.txt, Escopo da linguagem.txt   # especificação da linguagem

## Requisitos

- JDK 8 ou superior (testado também com JDK 17)

## Como compilar

A partir da raiz do repositório:

```bash
javac -encoding UTF-8 -d bin src/compiler/*.java \
  src/compiler/analise_lexica/*.java \
  src/compiler/analise_sintatica/*.java \
  src/compiler/analise_de_contexto/*.java \
  src/compiler/geracao_de_codigo/*.java \
  src/compiler/visitor/*.java
```

> O parâmetro `-encoding UTF-8` é necessário: as classes usam acentuação em português nas mensagens de erro.

Alternativamente, o projeto pode ser aberto diretamente no **NetBeans** (`build.xml`/`nbproject/` já incluídos) ou no **VS Code** com o "Extension Pack for Java".

## Como executar

```bash
java -cp bin compiler.Compiler [caminho-do-arquivo-fonte]
```

- Se nenhum argumento for passado, o compilador usa o arquivo definido em `Compiler.fileName` (por padrão, `test/code1.txt`).
- O nível de compilação é controlado pela variável `Compiler.steps`:

| `steps` | Etapa executada |
|---|---|
| 1 | Análise léxica |
| 2 | + Análise sintática |
| 3 | + Impressão da AST |
| 4 | + Análise de contexto |
| 5 | + Geração de código (padrão) |

Se a compilação for bem-sucedida, o código-objeto é gerado em `Object-code.txt` na raiz do projeto. Erros léxicos, sintáticos ou semânticos são reportados no console com linha e coluna, interrompendo a execução no primeiro erro encontrado.

## Exemplo

Entrada (`test/code1.txt`):

```pascal
program codigo;
var v1, v2, v3 : integer;

begin
  v1 := 0;
  v2 := 500;
  while v1 < v2 do
  begin
    v1 := v1 + 1;
  end
end
.
```