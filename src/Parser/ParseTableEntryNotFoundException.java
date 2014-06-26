package Parser;

/**
 * Created by pejman on 6/21/14.
 */
public class ParseTableEntryNotFoundException extends Exception {
    ParseTableEntryNotFoundException( String message ) {
        super( "Parse table entry not found: " + message );
    }
}
