package compiler.analise_sintatica;

import java.util.ArrayList;

import compiler.ShowError;
import compiler.Token;
import compiler.analise_de_contexto.Type;
import compiler.visitor.*;

public class Parser {
    private int currentTokenId;

    private int currentIndex;
    private ArrayList<Token> arrayOfTokens;

    public Parser(ArrayList<Token> arrayList) {
        this.currentIndex = 0;
        this.arrayOfTokens = arrayList;

        if (this.arrayOfTokens.size() > 0) {
            this.currentTokenId = this.arrayOfTokens.get(currentIndex).kind;
        } else {
            this.currentTokenId = Token.EOT;
        }
    }

    private Token currentToken() {
        if (currentIndex >= 0 && currentIndex < arrayOfTokens.size()) {
            return arrayOfTokens.get(currentIndex);
        }
        return new Token(Token.EOT, "<eot>", -1, -1);
    }

    private void accept(int tokenId) {
        if (tokenId == currentTokenId) {
            currentIndex++;
            if (currentIndex < arrayOfTokens.size()) {
                currentTokenId = arrayOfTokens.get(currentIndex).kind;
            } else {
                currentTokenId = Token.EOT;
            }
        } else {
            Token token = currentToken();
            new ShowError(
                "Símbolo não aceito: " + token.spelling + "\n"
                + "Linha: " + token.line
                + " Coluna: " + token.column
            );
        }
    }

    private void acceptIt() {
        currentIndex++;
        if (currentIndex < arrayOfTokens.size()) {
            currentTokenId = arrayOfTokens.get(currentIndex).kind;
        } else {
            currentTokenId = Token.EOT;
        }
    }

    private nodeComandoAtribuicao parse_atribuicao() {
        nodeComandoAtribuicao comandoAtribuicao = new nodeComandoAtribuicao(currentToken());

        comandoAtribuicao.variavel = parse_variavel();
        accept(Token.BECOMES);
        comandoAtribuicao.expressao = parse_expressao();

        return comandoAtribuicao;
    }

    private nodeComando parse_comando() {
        nodeComando comando;

        switch (currentTokenId) {
            case Token.IDENTIFIER:
                comando = parse_atribuicao();
                break;
            case Token.IF:
                comando = parse_condicional();
                break;
            case Token.WHILE:
                comando = parse_iterativo();
                break;
            case Token.BEGIN:
                comando = parse_comandoComposto();
                break;
            default:
                Token token = currentToken();
                new ShowError(
                    "Comando que começa com \"" + token.spelling + "\" não identificado\n"
                    + "Linha: " + token.line
                    + " Coluna: " + token.column
                );
                comando = null;
        }

        return comando;
    }

    private nodeComandoComposto parse_comandoComposto() {
        nodeComandoComposto comandoComposto = new nodeComandoComposto(currentToken());

        accept(Token.BEGIN);
        comandoComposto.comandos = parse_listaDeComandos();
        accept(Token.END);

        return comandoComposto;
    }

    private nodeComandoCondicional parse_condicional() {
        nodeComandoCondicional condicional = new nodeComandoCondicional(currentToken());

        accept(Token.IF);
        condicional.expressao = parse_expressao();
        accept(Token.THEN);
        condicional.comando1 = parse_comando();

        condicional.comando2 = null;

        if (currentTokenId == Token.ELSE) {
            accept(Token.ELSE);
            condicional.comando2 = parse_comando();
        }

        return condicional;
    }

    private nodeCorpo parse_corpo() {
        nodeCorpo corpo = new nodeCorpo();

        corpo.declaracoes = parse_declaracoes();
        corpo.comandoComposto = parse_comandoComposto();

        return corpo;
    }

    private nodeDeclaracao parse_declaracao() {
        nodeDeclaracao declaracao = new nodeDeclaracao();
        declaracao.declaracaoDeVariavel = parse_declaracaoDeVariavel();

        return declaracao;
    }

    private nodeDeclaracaoDeVariavel parse_declaracaoDeVariavel() {
    nodeDeclaracaoDeVariavel declaracaoDeVariavel = new nodeDeclaracaoDeVariavel();

    accept(Token.VAR);

    nodeID id = new nodeID(
        currentToken().spelling,
        currentToken()
    );
    id.valor = currentToken().spelling;
    declaracaoDeVariavel.IDs.add(id);

    accept(Token.IDENTIFIER);

    while (currentTokenId == Token.COMMA) {
        accept(Token.COMMA);

        nodeID outroId = new nodeID(
            currentToken().spelling,
            currentToken()
        );
        outroId.valor = currentToken().spelling;
        declaracaoDeVariavel.IDs.add(outroId);

        accept(Token.IDENTIFIER);
    }

    accept(Token.COLON);
    declaracaoDeVariavel.tipo = parse_tipo();

    return declaracaoDeVariavel;
}

    private nodeDeclaracoes parse_declaracoes() {
        nodeDeclaracoes declaracoes = new nodeDeclaracoes();
        declaracoes.declaracoes = new ArrayList<>();

        while (currentTokenId == Token.VAR) {
            declaracoes.declaracoes.add(parse_declaracao());
            accept(Token.SEMICOLON);
        }

        return declaracoes;
    }

    private nodeExpressao parse_expressao() {
        nodeExpressao expressao = new nodeExpressao(currentToken());

        expressao.expressaoSimples1 = parse_expressaoSimples();
        expressao.operadorRelacional = null;
        expressao.expressaoSimples2 = null;

        if (currentTokenId == Token.RELATIONALOPERATOR) {
            nodeOperadorRelacional operadorRelacional = new nodeOperadorRelacional(
                currentToken().spelling,
                Token.RELATIONALOPERATOR
            );
            expressao.operadorRelacional = operadorRelacional;

            acceptIt();

            expressao.expressaoSimples2 = parse_expressaoSimples();
        }

        return expressao;
    }

    private nodeExpressaoSimples parse_expressaoSimples() {
        nodeExpressaoSimples expressaoSimples = new nodeExpressaoSimples();
        expressaoSimples.termo = parse_termo();
        expressaoSimples.operadoresAditivos = new ArrayList<nodeOperadorAditivo>();
        expressaoSimples.termos = new ArrayList<nodeTermo>();

        while (currentTokenId == Token.ADITIONALOPERATOR) {
            nodeOperadorAditivo operadorAditivo = new nodeOperadorAditivo(
                currentToken().spelling,
                Token.ADITIONALOPERATOR
            );
            expressaoSimples.operadoresAditivos.add(operadorAditivo);

            acceptIt();

            expressaoSimples.termos.add(parse_termo());
        }

        return expressaoSimples;
    }

    private nodeFator parse_fator() {
        nodeFator fator = null;
        Type tipo;
        nodeLiteral aux2;

        switch (currentTokenId) {
            case Token.IDENTIFIER:
                nodeVariavel aux1 = new nodeVariavel(
                    new nodeID(
                        currentToken().spelling,
                        currentToken()
                    ),
                    currentToken()
                );
                fator = aux1;
                acceptIt();
                break;

            case Token.INTLITERAL:
                tipo = new Type(Type.INT);
                aux2 = new nodeLiteral(currentToken().spelling, tipo);
                fator = aux2;
                acceptIt();
                break;

            case Token.FLOATLITERAL:
                tipo = new Type(Type.REAL);
                aux2 = new nodeLiteral(currentToken().spelling, tipo);
                fator = aux2;
                acceptIt();
                break;

            case Token.BOOLLITERAL:
                tipo = new Type(Type.BOOL);
                aux2 = new nodeLiteral(currentToken().spelling, tipo);
                fator = aux2;
                acceptIt();
                break;

            case Token.LPAREN:
                accept(Token.LPAREN);
                nodeExpressao expressao = parse_expressao();
                accept(Token.RPAREN);
                fator = expressao;
                break;

            default:
                Token token = currentToken();
                new ShowError(
                    "Símbolo \"" + token.spelling + "\" não identificado\n" +
                    "Linha: " + token.line + " " +
                    "Coluna: " + token.column
                );
        }

        return fator;
    }

    private nodeComandoIterativo parse_iterativo() {
        nodeComandoIterativo comandoIterativo = new nodeComandoIterativo(currentToken());

        accept(Token.WHILE);
        comandoIterativo.expressao = parse_expressao();
        accept(Token.DO);
        comandoIterativo.comando = parse_comando();

        return comandoIterativo;
    }

    private ArrayList<nodeComando> parse_listaDeComandos() {
        ArrayList<nodeComando> comandos = new ArrayList<>();

        while (
            currentTokenId == Token.IDENTIFIER ||
            currentTokenId == Token.IF ||
            currentTokenId == Token.WHILE ||
            currentTokenId == Token.BEGIN
        ) {
            comandos.add(parse_comando());
            accept(Token.SEMICOLON);
        }

        return comandos;
    }

    private void parse_outros() {
        switch (currentTokenId) {
            case Token.EXCLAMATION:
            case Token.ARROBA:
            case Token.HASHTAG:
            case Token.ELLIPSIS:
                acceptIt();
                break;
            default:
                Token token = currentToken();
                new ShowError(
                    "Símbolo não aceito: " + token.spelling
                    + " Linha: " + token.line
                    + " Coluna: " + token.column
                );
        }
    }

    private nodePrograma parse_programa() {
        nodePrograma programaAST = new nodePrograma();

        accept(Token.PROGRAM);

        programaAST.id = new nodeID(
            currentToken().spelling,
            currentToken()
        );
        programaAST.id.valor = currentToken().spelling;

        accept(Token.IDENTIFIER);
        accept(Token.SEMICOLON);

        programaAST.corpo = parse_corpo();

        accept(Token.PERIOD);

        return programaAST;
    }

    private nodeTermo parse_termo() {
        nodeTermo termo = new nodeTermo();
        termo.fatores = new ArrayList<nodeFator>();
        termo.operadoresMultiplicativos = new ArrayList<nodeOperadorMultiplicativo>();

        termo.fator = parse_fator();

        while (currentTokenId == Token.MULTIPLICATIONALOPERATOR) {
            nodeOperadorMultiplicativo operadorMultiplicativo =
                new nodeOperadorMultiplicativo(
                    currentToken().spelling,
                    Token.MULTIPLICATIONALOPERATOR
                );
            termo.operadoresMultiplicativos.add(operadorMultiplicativo);

            acceptIt();

            termo.fatores.add(parse_fator());
        }

        return termo;
    }

    private nodeTipo parse_tipo() {
        nodeTipo tipo = new nodeTipo();

        String tipoString = currentToken().spelling;
        Type tipoType = Type.evaluateString(tipoString);
        tipo.tipoSimples = new nodeTipoSimples(tipoString, tipoType);

        accept(Token.TIPOSIMPLES);
        return tipo;
    }

    private nodeVariavel parse_variavel() {
        nodeID ID = new nodeID(
            currentToken().spelling,
            currentToken()
        );
        ID.valor = currentToken().spelling;

        nodeVariavel variavel = new nodeVariavel(ID, currentToken());

        accept(Token.IDENTIFIER);

        return variavel;
    }

    public nodePrograma parse() {
        return parse_programa();
    }
}