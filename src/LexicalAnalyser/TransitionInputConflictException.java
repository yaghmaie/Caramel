package LexicalAnalyser;

/**
 * Created by pejman on 6/6/14.
 */
public class TransitionInputConflictException extends Exception {
    TransitionInputConflictException( String message ) {
        super( "Input Conflict : " + message );
    }
}
