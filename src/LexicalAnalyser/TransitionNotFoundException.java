package LexicalAnalyser;

/**
 * Created by pejman on 5/27/14.
 */
public class TransitionNotFoundException extends Exception {
    TransitionNotFoundException( String message ) {
        super( "Transition not found : " + message );
    }
}
