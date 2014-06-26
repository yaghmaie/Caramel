package Parser;

/**
 * Created by pejman on 6/21/14.
 */
public class ConflictingParseTableEntryException extends Exception {
    ConflictingParseTableEntryException( String message ) {
        super( "Conflict occurred in parse table: " + message );
    }
}
