package LexicalAnalyser;

/**
 * Created by pejman on 6/10/14.
 */
public class StateNotBelongsToFAException extends Exception {
    StateNotBelongsToFAException( String message ) {
        super( "State not belongs to FA: " + message );
    }
}
