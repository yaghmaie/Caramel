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

import Fundamentals.Token;

import java.util.ArrayList;

/**
 * Created by pejman on 6/21/14.
 */
public class ParseTreeNode {
    /**
     * Parent of this node
     */
    public final ParseTreeNode parent;
    /**
     * children of this node
     */
    public final ArrayList<ParseTreeNode> children = new ArrayList<ParseTreeNode>();
    /**
     * Symbol this node is bounded with.
     */
    public final Symbol symbol;
    /**
     * Value this node carrying
     */
    private String value;

    /**
     * Constructor of node. Sets parent, symbol and value of node.
     * @param parent node.
     * @param symbol Terminal or non-Terminal.
     * @param value String.
     */
    public ParseTreeNode( ParseTreeNode parent, Symbol symbol, String value ) {
        this.symbol = symbol;
        this.parent = parent;
        this.value = value;
    }

    /**
     * Constructor of node. Sets parent and symbol of node.
     * @param parent node.
     * @param symbol Terminal or non-Terminal.
     */
    public ParseTreeNode( ParseTreeNode parent, Symbol symbol ) {
        this.symbol = symbol;
        this.parent = parent;
    }

    /**
     * Get value.
     * @return value of node.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value of node.
     * @param value of node.
     */
    public void setValue( String value ) {
        this.value = value;
    }
}
