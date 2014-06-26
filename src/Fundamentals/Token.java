package Fundamentals;

/**
 * Created by pejman on 6/10/14.
 */
public class Token {
    public final String tokenClass;
    public final String lexeme;
    public final int line;
    public Token( String tokenClass, String lexeme, int line ) {
        this.tokenClass = tokenClass;
        this.lexeme = lexeme;
        this.line = line;
    }
    public void dump() {
        System.out.printf( "Class: %20s\t\t\tlexeme: %30s\t\tline #%04d\n", tokenClass, lexeme, line );
    }
}
