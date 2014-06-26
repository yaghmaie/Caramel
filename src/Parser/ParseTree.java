package Parser;

/**
 * Created by pejman on 6/21/14.
 */
public class ParseTree {
    public final ParseTreeNode root;

    ParseTree( ParseTreeNode node ) {
        root = node;
    }

    public void dump() {
        dump( root, 0 );
    }

    private void dump( ParseTreeNode node, int n ) {
        for( int i = 0; i++ < n; System.out.print("\t") );
        System.out.printf( "Class/Value: %s/%s\n", node.symbol.getContent(), node.getValue() );
        for( ParseTreeNode p : node.children ) {
            dump( p, n + 1 );
        }
    }
}
