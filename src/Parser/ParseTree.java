/*
 * Simple solutions for creating lexical analyzer, syntax analyzer, code generator.
 *     Copyright (C) 2014  Pejman Yaghmaie.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package Parser;

/**
 * Created by pejman on 6/21/14.
 */
public class ParseTree {
    /**
     * Root of tree
     */
    public final ParseTreeNode root;

    /**
     * Constructor sets root.
     * @param node
     */
    ParseTree( ParseTreeNode node ) {
        root = node;
    }

    /**
     * Dumps tree containment.
     */
    public void dump() {
        dump( root, 0 );
    }

    /**
     * Dumps tree containment from a specific node.
     * @param node Node to dump tree from.
     * @param n Number of parent nodes from root.
     */
    private void dump( ParseTreeNode node, int n ) {
        for( int i = 0; i++ < n; System.out.print("\t") );
        System.out.printf( "Class/Value: %s/%s\n", node.symbol.getContent(), node.getValue() );
        for( ParseTreeNode p : node.children ) {
            dump( p, n + 1 );
        }
    }
}
