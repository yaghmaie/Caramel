package Parser;

/**
 * Created by pejman on 6/21/14.
 */
public class ParserException extends Exception {
    ParserException( String message ) {
        super( "An error occurred during parsing operation: " + message );
    }
}
