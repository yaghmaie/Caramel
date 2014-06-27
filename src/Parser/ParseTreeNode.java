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
