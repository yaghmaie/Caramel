package Parser;

/**
 * Created by pejman on 6/20/14.
 */
public abstract class Symbol {
    private String content;
    Symbol( String symbol ) {
        content = symbol;
    }
    public String getContent() {
        return content;
    }
}
