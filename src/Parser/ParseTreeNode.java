package Parser;

import Fundamentals.Token;

import java.util.ArrayList;

/**
 * Created by pejman on 6/21/14.
 */
public class ParseTreeNode {
    public final ParseTreeNode parent;
    public final ArrayList<ParseTreeNode> children = new ArrayList<ParseTreeNode>();
    public final Symbol symbol;
    private String value;

    public ParseTreeNode( ParseTreeNode parent, Symbol symbol, String value ) {
        this.symbol = symbol;
        this.parent = parent;
        this.value = value;
    }

    public ParseTreeNode( ParseTreeNode parent, Symbol symbol ) {
        this.symbol = symbol;
        this.parent = parent;
    }


    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }
}
