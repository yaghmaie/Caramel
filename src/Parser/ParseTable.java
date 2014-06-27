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

import Fundamentals.BigTable;

/**
 * Created by pejman on 6/10/14.
 */
public class ParseTable extends BigTable<NonTerminal, Terminal, RightHandSide> {

    final RightHandSide emptyRightHandSide = new RightHandSide();
    final ContextFreeGrammar contextFreeGrammar;

    public ParseTable( ContextFreeGrammar cfg ) {
        contextFreeGrammar = cfg;
    }

    public void addEntry( NonTerminal nonTerminal, Terminal terminal, RightHandSide rightHandSide ) throws ConflictingParseTableEntryException {
        if( theTable.containsKey( nonTerminal ) ) {
            if( theTable.get( nonTerminal ).containsKey( terminal ) ) {
                throw new ConflictingParseTableEntryException( "non-Terminal \"" + nonTerminal.getContent() + "\" has already entry with terminal \"" + terminal.getContent() + "\"" );
            }
        }
        addSecondaryKey( nonTerminal, terminal, rightHandSide );
    }

    public RightHandSide getEntry( NonTerminal nonTerminal, Terminal terminal ) throws Exception {
        if( !( theTable.containsKey( nonTerminal ) && theTable.get( nonTerminal ).containsKey( terminal ) ) )
            throw new ParseTableEntryNotFoundException( "non-Terminal \"" + nonTerminal.getContent() + "\" and Terminal \"" + terminal.getContent() + "\"" );
        return theTable.get( nonTerminal ).get( terminal );
    }

    public void dump() {
        for( NonTerminal nonT : theTable.keySet() ) {
            System.out.println("Entries for non-Terminal \"" + nonT.getContent() + "\"" );
            for( Terminal t : theTable.get( nonT ).keySet() ) {
                System.out.println("\tTerminal \"" + t.getContent() + "\" RightHandSide:" );
                System.out.print( "\t\t" );
                theTable.get( nonT ).get( t ).dump();
            }
        }
    }
}
