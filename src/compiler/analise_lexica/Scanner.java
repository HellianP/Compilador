package compiler.analise_lexica;

import compiler.ShowError;
import compiler.Token;
import compiler.analise_de_contexto.IdentificationTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author
 */
public class Scanner {
    public BufferedReader fileReader;

    private char currentChar;

    private byte currentKind;
    private StringBuffer currentSpelling;

    private int currentLine = 1;
    private int currentColumn = 1;

    public IdentificationTable table;

    public Scanner(String pathToFile) {
        try {
            File file = new File(pathToFile);

            if (!file.isAbsolute()) {
                file = new File(System.getProperty("user.dir"), pathToFile);
            }

            this.fileReader = new BufferedReader(new FileReader(file));

            int character = this.fileReader.read();
            if (character != -1) {
                this.currentChar = (char) character;
            } else {
                this.currentChar = '\000';
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.fileReader = null;
            this.currentChar = '\000';
        }
    }

    public boolean isEOF() throws IOException {
        if (fileReader == null) {
            return true;
        }

        if (currentChar == '\000') {
            return true;
        }

        return false;
    }

    public void getNextCaracter() {
        int character;

        switch (currentChar) {
            case '\n':
                currentLine++;
                currentColumn = 1;
                break;
            case '\t':
                currentColumn += 4;
                break;
            case '\r':
                break;
            default:
                currentColumn++;
                break;
        }

        try {
            if (fileReader != null && (character = fileReader.read()) != -1) {
                this.currentChar = (char) character;
            } else {
                this.currentChar = '\000';
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.currentChar = '\000';
        }
    }

    public void readFile(String pathToFile) {
        try {
            File file = new File(pathToFile);

            if (!file.isAbsolute()) {
                file = new File(System.getProperty("user.dir"), pathToFile);
            }

            System.out.println("Arquivo: " + file.getAbsolutePath());

            FileReader fileReader = new FileReader(file);
            int character;
            while ((character = fileReader.read()) != -1) {
                System.out.print((char) character);
            }
            System.out.println();

            fileReader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void take(char expectedChar) {
        if (currentChar == expectedChar) {
            currentSpelling.append(currentChar);
            getNextCaracter();
        } else {
            new ShowError("Caracter inválido: " + currentSpelling.append(currentChar));
        }
    }

    private void takeIt() {
        currentSpelling.append(currentChar);
        getNextCaracter();
    }

    protected boolean isDigit(char caracter) {
        return (caracter >= '0' && caracter <= '9');
    }

    protected boolean isLetter(char caracter) {
        return (caracter >= 'a' && caracter <= 'z');
    }

    protected boolean isBasicAditionalOperator(char character) {
        switch (character) {
            case '+':
            case '-':
                return true;
            default:
                return false;
        }
    }

    protected boolean isBasicMultiplicationalOperator(char character) {
        switch (character) {
            case '*':
            case '/':
                return true;
            default:
                return false;
        }
    }

    protected boolean isSimpleType(String word) {
        switch (word) {
            case "integer":
            case "real":
            case "boolean":
                return true;
            default:
                return false;
        }
    }

    protected boolean isGraphicCaracter(char caracter) {
        int minimumValues[] = {32, 58, 91, 123, 128};
        int maximumValues[] = {47, 64, 96, 126, 159};

        for (int i = 0; i < 5; i++) {
            if (caracter >= minimumValues[i] && caracter <= maximumValues[i]) {
                return true;
            }
        }
        return false;
    }

    private byte scanIdentifierOrReservedWord() {
        takeIt();
        while (isLetter(currentChar) || isDigit(currentChar)) {
            takeIt();
        }

        String word = currentSpelling.toString();

        if (word.equals("program")) return Token.PROGRAM;
        if (word.equals("begin")) return Token.BEGIN;
        if (word.equals("end")) return Token.END;
        if (word.equals("if")) return Token.IF;
        if (word.equals("then")) return Token.THEN;
        if (word.equals("else")) return Token.ELSE;
        if (word.equals("var")) return Token.VAR;
        if (word.equals("while")) return Token.WHILE;
        if (word.equals("do")) return Token.DO;
        if (word.equals("true") || word.equals("false")) return Token.BOOLLITERAL;
        if (word.equals("or")) return Token.ADITIONALOPERATOR;
        if (word.equals("and")) return Token.MULTIPLICATIONALOPERATOR;
        if (isSimpleType(word)) return Token.TIPOSIMPLES;

        return Token.IDENTIFIER;
    }

    private byte scanNumber() {
        boolean hasDot = false;

        if (currentChar == '.') {
            hasDot = true;
            takeIt();

            if (!isDigit(currentChar)) {
                return Token.PERIOD;
            }

            while (isDigit(currentChar)) {
                takeIt();
            }

            return Token.FLOATLITERAL;
        }

        while (isDigit(currentChar)) {
            takeIt();
        }

        if (currentChar == '.') {
            hasDot = true;
            takeIt();

            while (isDigit(currentChar)) {
                takeIt();
            }
        }

        if (isLetter(currentChar)) {
            while (isLetter(currentChar) || isDigit(currentChar)) {
                takeIt();
            }
            return Token.ERROR;
        }

        if (hasDot) {
            return Token.FLOATLITERAL;
        }

        return Token.INTLITERAL;
    }

    private byte scanRelationalOperator() {
        if (currentChar == '<') {
            takeIt();
            if (currentChar == '=' || currentChar == '>') {
                takeIt();
            }
            return Token.RELATIONALOPERATOR;
        }

        if (currentChar == '>') {
            takeIt();
            if (currentChar == '=') {
                takeIt();
            }
            return Token.RELATIONALOPERATOR;
        }

        if (currentChar == '=') {
            takeIt();
            return Token.RELATIONALOPERATOR;
        }

        return Token.ERROR;
    }

    private byte scanToken() {
        if (isLetter(currentChar)) {
            return scanIdentifierOrReservedWord();
        }

        if (isDigit(currentChar) || currentChar == '.') {
            return scanNumber();
        }

        if (currentChar == ':') {
            takeIt();
            if (currentChar == '=') {
                takeIt();
                return Token.BECOMES;
            } else {
                return Token.COLON;
            }
        }

        if (currentChar == ';') {
            takeIt();
            return Token.SEMICOLON;
        }

        if (currentChar == ',') {
            takeIt();
            return Token.COMMA;
        }

        if (currentChar == '(') {
            takeIt();
            return Token.LPAREN;
        }

        if (currentChar == ')') {
            takeIt();
            return Token.RPAREN;
        }

        if (currentChar == '<' || currentChar == '>' || currentChar == '=') {
            return scanRelationalOperator();
        }

        if (isBasicAditionalOperator(currentChar)) {
            takeIt();
            return Token.ADITIONALOPERATOR;
        }

        if (isBasicMultiplicationalOperator(currentChar)) {
            takeIt();
            return Token.MULTIPLICATIONALOPERATOR;
        }

        if (currentChar == '!') {
            takeIt();
            return Token.EXCLAMATION;
        }

        if (currentChar == '@') {
            takeIt();
            return Token.ARROBA;
        }

        if (currentChar == '#') {
            takeIt();
            return Token.HASHTAG;
        }

        if (currentChar == '\000') {
            return Token.EOT;
        }

        takeIt();
        return Token.ERROR;
    }

    private void scanSeparator() {
        switch (currentChar) {
            case '!':
                while (currentChar != '\n' && currentChar != '\000') {
                    getNextCaracter();
                }
                break;

            case ' ':
            case '\n':
            case '\t':
            case '\r':
                getNextCaracter();
                break;
        }
    }

    public Token scan() {
        while (
            currentChar == ' ' ||
            currentChar == '\n' ||
            currentChar == '\t' ||
            currentChar == '\r' ||
            currentChar == '!'
        ) {
            scanSeparator();
        }

        int tokenLine = currentLine;
        int tokenColumn = currentColumn;

        currentSpelling = new StringBuffer("");
        currentKind = scanToken();

        return new Token(
            currentKind,
            currentSpelling.toString(),
            tokenLine,
            tokenColumn
        );
    }
}