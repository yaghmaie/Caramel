package Parser;

import java.util.ArrayList;

/**
 * Created by pejman on 6/10/14.
 */
public class RightHandSide extends ArrayList<Symbol> {
    public void dump() {
        for( Symbol sym : this ) {
            System.out.print( sym.getContent() + " " );
        }
        System.out.println();
    }
}
