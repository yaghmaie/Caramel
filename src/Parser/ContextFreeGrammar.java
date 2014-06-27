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

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by pejman on 6/19/14.
 */
public class ContextFreeGrammar {
    /**
     * Productions for each non-Terminal
     */
    final HashMap<NonTerminal, HashSet<RightHandSide>> productions = new HashMap<NonTerminal, HashSet<RightHandSide>>();
    /**
     * List of non-Terminals
     */
    final HashMap<String, NonTerminal> nonTerminals = new HashMap<String, NonTerminal>();
    /**
     * List of Terminals
     */
    final HashMap<String, Terminal> terminals = new HashMap<String, Terminal>();
    /**
     * Start symbol
     */
    final NonTerminal startSymbol;
    /**
     * End symbol
     */
    final Terminal endSymbol = new Terminal( "$" );

    /**
     * Constructor sets start symbol for CFG
     * @param startSymbol
     */
    ContextFreeGrammar( String startSymbol ) {
        this.startSymbol = getNonTerminal( startSymbol );
    }

    /**
     * Returns non-Terminal matching string.
     * @param str String to find non-Terminal
     * @return non-Terminal.
     */
    private NonTerminal getNonTerminal( String str ) {
        if( !nonTerminals.containsKey( str ) ) {
            nonTerminals.put( str, new NonTerminal( str ) );
        }
        return nonTerminals.get( str );
    }

    /**
     * Returns Terminal matching string.
     * @param str String to find Terminal.
     * @return Terminal.
     */
    private Terminal getTerminal( String str ) {
        if( !terminals.containsKey( str ) ) {
            terminals.put( str, new Terminal( str ) );
        }
        return terminals.get( str );
    }

    /**
     * Adds a right hand side for a non-Terminal.
     * @param nonTerminal non-Terminal.
     * @param rightHandSide right hand side.
     */
    public void addProduction( String nonTerminal, String rightHandSide ) {
        RightHandSide rightHandSideArray = new RightHandSide(); // Create new right hand side
        for( int i = 0; i < rightHandSide.length(); i++ ) { // Read the string
            int strStart = 0;
            switch ( rightHandSide.charAt( i ) ) {
                case '`': // If there is apostrophe in string
                    strStart = i;
                    i++;
                    while ( rightHandSide.charAt( i ) != '`' || rightHandSide.charAt( i - 1 ) == '\\' ) { // Extract what's between apostrophes
                        i++;
                    }
                    rightHandSideArray.add( getTerminal( rightHandSide.substring( strStart + 1, i ) ) ); // Add extracted to right hand side
                    break;
                case '~':
                    strStart = i;
                    i++;
                    while ( rightHandSide.charAt( i ) != '~' ) { // Extract what's between tildes
                        i++;
                    }
                    rightHandSideArray.add( getNonTerminal( rightHandSide.substring( strStart + 1, i ) ) ); // Add extracted to right hand side
                    break;
            }
        }
        if( !productions.containsKey( getNonTerminal( nonTerminal ) ) ) { // Add right hand side to non-Terminal rhs set
            HashSet<RightHandSide> tempSet = new HashSet<RightHandSide>();
            tempSet.add( rightHandSideArray );
            productions.put( getNonTerminal( nonTerminal ), tempSet );
        } else {
            productions.get( getNonTerminal( nonTerminal ) ).add( rightHandSideArray );
        }
    }
}
