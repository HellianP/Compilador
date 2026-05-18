package compiler.analise_lexica;
import compiler.ShowError;
import compiler.Token;
import compiler.analise_de_contexto.IdentificationTable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 *
 * @author
 */
public class Scanner {
    public BufferedReader fileReader;


    private int counterToMarkCharacter;
    private char currentChar;


    private byte currentKind;
    private StringBuffer currentSpelling;
        
    private int currentLine = 1;
    private int currentColumn;


    public IdentificationTable table;


    public Scanner(String pathToFile) {
        counterToMarkCharacter = 0;
        try {
            String currentDirectory = System.getProperty("user.dir");


            FileReader poorFileReader = new FileReader(
                currentDirectory + pathToFile
            );
            BufferedReader fileReader = new BufferedReader(poorFileReader);
            
            int character;
            if((character = fileReader.read()) != -1) {
                this.currentChar = (char)character;
            }
            this.fileReader = fileReader;


        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public boolean isEOF() throws IOException { 
        fileReader.mark(counterToMarkCharacter);
        counterToMarkCharacter++;


        if(fileReader.read() != -1) {
            fileReader.reset();
            return false;
        }
        return true;
    }


    public void getNextCaracter() {
        int character;
                
        switch (currentChar) {
        case '\n':
            currentLine++;
            currentColumn = 1;
            break;
        case '\t':
            currentColumn += 2;
            break;
        default:
            currentColumn++;
            break;
        }
                
        try {
            if((character = fileReader.read()) != -1) {
                this.currentChar = (char)character;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void readFile(String pathToFile) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("O diretório atual é: " + currentDirectory);


            FileReader fileReader = new FileReader(
                currentDirectory + pathToFile
            );
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
        if(currentChar == expectedChar) {
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


    protected boolean isBasicRelationalOperator(char character) {
        switch(character) {
            case '<':
            case '>':
            case '=':
                return true;
            default:
                return false;
        }
    }


    protected boolean isBasicAditionalOperator(char character) {
        switch(character) {
            case '+':
            case '-':
                return true;
            default:
                return false;
        }
    }


    protected boolean isBasicMultiplicationalOperator(char character) {
        switch(character) {
            case '*':
            case '/':
                return true;
            default:
                return false;
        }
    }


    protected boolean isSimpleType(String word) {
        switch(word) {
            case "integer":
            case "boolean":
                return true;
            default:
                return false;
        }
    } 


    protected boolean isGraphicCaracter(char caracter) {
        int minimumValues[] = { 32, 58, 91, 123, 128 };
        int maximumValues[] = { 47, 64, 96, 126, 159 };
        
        for(int i = 0; i < 5; i++) {
            if(caracter >= minimumValues[i] && caracter <= maximumValues[i]) {
                return true;
            }
        }
        return false;
    }


    private byte scanToken() {


        if(isLetter(currentChar)) {
            takeIt();
            while(isLetter(currentChar) || isDigit(currentChar)) {
                takeIt();
            }
            if(currentSpelling.toString().equals("program")) {
                return Token.PROGRAM;
            }
            if(
                currentSpelling.toString().equals("true") || 
                currentSpelling.toString().equals("false")) {
                    return Token.BOOLLITERAL;
            }
            if(currentSpelling.toString().equals("or")) {
                return Token.ADITIONALOPERATOR;
            }
            if(currentSpelling.toString().equals("and")) {
                return Token.MULTIPLICATIONALOPERATOR;
            }
            if(isSimpleType(currentSpelling.toString())) {
                return Token.TIPOSIMPLES;
            }
            return Token.IDENTIFIER;
        }


        if(isDigit(currentChar)) {
            takeIt();
            while(isDigit(currentChar)) {
                takeIt();
            }
            if(isLetter(currentChar)) {
                return Token.ERROR;
            } else {
                return Token.INTLITERAL;
            }
        }


        if(currentChar == ':') {
            takeIt();
            if(currentChar == '=') {
                takeIt();
                return Token.BECOMES;
            } else {
                return Token.COLON;
            }
        }


        if(currentChar == ';') {
            takeIt();
            return Token.SEMICOLON;
        }


        if(currentChar == '(') {
            takeIt();
            return Token.LPAREN;
        }


        if(currentChar == ')') {
            takeIt();
            return Token.RPAREN;
        }


        if(currentChar == '.') {
            takeIt();
            return Token.PERIOD;
        }


        if(currentChar == '!') {
            takeIt();
            return Token.EXCLAMATION;
        }


        if(currentChar == '@') {
            takeIt();
            return Token.ARROBA;
        }


        if(currentChar == '#') {
            takeIt();
            return Token.HASHTAG;
        }


        if(isBasicRelationalOperator(currentChar)) {
            takeIt();
            return Token.RELATIONALOPERATOR;
        }


        if(isBasicAditionalOperator(currentChar)) {
            takeIt();
            return Token.ADITIONALOPERATOR;
        }


        if(isBasicMultiplicationalOperator(currentChar)) {
            takeIt();
            return Token.MULTIPLICATIONALOPERATOR;
        }


        if(currentChar == '\000') {
            takeIt();
            return Token.EOT;
        }


        takeIt();
        return Token.ERROR;
    }


    private void scanSeparator() {
        switch(currentChar) {
            case '!':
                takeIt();
                while(currentChar != '\n') {
                    takeIt();
                }
                break;


            case ' ':
            case '\n':
            case '\t':
                takeIt();
                break;
        }
    }


    public Token scan() {
        while(currentChar == '!' || currentChar == ' ' || currentChar == '\n' || currentChar == '\t') {
            scanSeparator();
        }
        currentSpelling = new StringBuffer("");
        currentKind = scanToken();


        return new Token(
            currentKind, 
            currentSpelling.toString(),
            currentLine, 
            currentColumn
        );
    }
}