package compiler;


/**
 *
 * @author
 */
public class Token {
  
  public byte kind;
  public String spelling;
  public int line;
  public int column;


  public final static byte 
    IDENTIFIER = 0,
    BOOLLITERAL = 1, INTLITERAL = 2,


    BEGIN = 3, IF = 4, THEN = 5, ELSE = 6, VAR = 7, WHILE = 8, DO = 9, PROGRAM = 10, END = 11,
    BECOMES = 12, LPAREN = 13, RPAREN = 14, COLON = 15, SEMICOLON = 16,
    PERIOD = 17,


    EXCLAMATION = 18, ARROBA = 19, HASHTAG = 20, ELLIPSIS = 21,


    RELATIONALOPERATOR = 22, ADITIONALOPERATOR = 23, MULTIPLICATIONALOPERATOR = 24, TIPOSIMPLES = 25,
    EOT = 26, ERROR = 27;


  private final static String[] spellings = {
    "<identifier>",
    "<bool-literal>", "<int-literal>",


    "begin", "if", "then", "else", "var", "while", "do", "program", "end",
    ":=", "(", ")", ":", ";",
    ".",


    "!", "@", "#", "...",


    "<op-rel>", "<op-ad>", "<op-mul>", "<tipo-simples>",
    "<eot>", "<error>"
  };


  public Token(byte kind, String spelling, int line, int column) {
    this.kind = kind;
    this.spelling = spelling;
    this.line = line;
    this.column = column;


    // Se kind for reconhecido como IDENTIFIER,
    // devemos verificar se é uma das palavras reservadas.
    // Se for, o kind deve ser alterado apropriadamente. 
    if(kind == IDENTIFIER) {
      for(byte i = BEGIN; i <= END; i++) {
        if(spelling.equals(spellings[i])) {
          this.kind = i;
          break;
        }
      }
    }
  }
}