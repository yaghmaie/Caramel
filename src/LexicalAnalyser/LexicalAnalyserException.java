package LexicalAnalyser;

/**
 * Created by pejman on 6/10/14.
 */
public class LexicalAnalyserException extends Exception {
    LexicalAnalyserException( String message ) {
        super( "LexicalAnalyser Exception occurred: "  + message );
    }
}
