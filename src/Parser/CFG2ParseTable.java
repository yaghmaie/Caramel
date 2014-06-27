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
public class CFG2ParseTable {
    /**
     * First set for each non-Terminal symbol
     */
    private HashMap<NonTerminal, HashSet<Terminal>> firstSets = new HashMap<NonTerminal, HashSet<Terminal>>();
    /**
     * Follow set for each non-Terminal symbol
     */
    private HashMap<NonTerminal, HashSet<Terminal>> followSets = new HashMap<NonTerminal, HashSet<Terminal>>();
    /**
     * Using context-free grammar
     */
    private ContextFreeGrammar cfg;

    /**
     * Receives a string of rules and transforms them into a parse table.
     * Rules must be defined like "non-Terminal=%%RightHandSide%%" where RightHandSide is an array of Terminals and defined non-Terminals.
     * Terminals must be between two apostrophes and non-Terminals must be between two tildes.
     * NOTICE: The grammar must be LL(1).
     * Example:
     *      S=%%~T~~F~%%
     *      T=%%`plus`~F~%%
     *      F=%%`number`%%
     * @param rules String of rules to be transformed to parse table.
     * @return Parse table.
     */
    public ParseTable rules2ParseTable( String rules ) {
        String[] rulesArray = rules.split( "\n" ); // Splits rules and puts each rule in array
        cfg = new ContextFreeGrammar( rulesArray[0].substring( 0, rulesArray[0].indexOf( '=' ) ) ); // Creates a new CFG and sets the label of first rule as start symbol
        ParseTable parseTable = new ParseTable( cfg ); // Creates a new parse table and sets it's CFG to cfg

        for( String rule : rulesArray ) { // For each rule in rulesArray
            String nonTerminal = rule.substring( 0, rule.indexOf( '=' ) ); // Extract non-Terminal
            String rightHandSide = rule.substring( rule.indexOf( '%' ) + 2, rule.lastIndexOf( '%' ) - 1 ); // Extract right hand side
            cfg.addProduction( nonTerminal, rightHandSide ); // Add right hand side for non-Terminal into cfg
        }

        for( NonTerminal nonT : cfg.productions.keySet() ) { // For each non-Terminal in cfg calculate first set
            first( nonT );
        }
        for( NonTerminal nonT : cfg.productions.keySet() ) { // For each non-Terminal in cfg calculate follow set
            follow( nonT );
        }
        // Fills parse table using first set and follow set
        for( NonTerminal nonT : cfg.productions.keySet() ) { // For each non-Terminal in cfg
            for( RightHandSide rhs : cfg.productions.get( nonT ) ) { // For each right hand side for that non-Terminal
                HashSet<Terminal> firs = first(rhs); // Get first set for that right hand side
                for( Terminal t : firs ) { // For each terminal in first set
                    if( t.equals( cfg.terminals.get("\0") ) ) // If Terminal equals epsilon do noting
                        continue;
                    try {
                        parseTable.addEntry( nonT, t, rhs ); // Add right hand side to parse table at non-Terminal row and Terminal column
                    } catch ( ConflictingParseTableEntryException e ) { // Throw exception if conflict occurred in filling parse table
                        System.err.println( e.getMessage() );
                    }
                }
            }
            if( first( nonT ).contains( cfg.terminals.get( "\0" ) ) ) { // If in first set of non-Terminal, there is epsilon
                for( Terminal t : follow(nonT) ) { // For each Terminal at follow set of non-Terminal
                    try {
                        parseTable.addEntry( nonT, t, parseTable.emptyRightHandSide ); // Add empty right hand side (non-Terminal to epsilon) to parse table at non-Terminal row and Terminal column
                    } catch ( ConflictingParseTableEntryException e ) {
                        System.err.println( e.getMessage() );
                    }
                }
            }
        }

        return parseTable;
    }

    /**
     * Calculates first set for a symbol.
     * @param symbol To calculate first set for.
     * @return First set of that symbol.
     */
    private HashSet<Terminal> first( Symbol symbol ) {
        if( symbol.getClass().equals( NonTerminal.class ) ) { // If symbol is a non-Terminal call first with non-Terminal else call with Terminal
            return first( (NonTerminal)symbol );
        } else {
            return first( (Terminal)symbol );
        }
    }

    /**
     * Calculates first set for a non-Terminal.
     * @param nonTerminal To calculate first set for.
     * @return First set of non-Terminal.
     */
    private HashSet<Terminal> first( NonTerminal nonTerminal ) {
        if( !firstSets.containsKey( nonTerminal ) ) { // If first not calculated for non-Terminal yet
            firstSets.put( nonTerminal, new HashSet<Terminal>() ); // Create a new set for that non-Terminal and fill it
        } else {
            return firstSets.get( nonTerminal ); // If calculated before return the set
        }
        for( RightHandSide r : cfg.productions.get( nonTerminal ) ) { // For each right hand side of non-Terminal
            firstSets.get( nonTerminal ).addAll( first( r ) ); // Calculate first set for that right hand side and add it's Terminals into first set
        }
        return firstSets.get( nonTerminal );
    }

    /**
     * Calculates first set for right hand side.
     * @param rightHandSide To calculate first set for.
     * @return First set of right hand side.
     */
    private HashSet<Terminal> first( RightHandSide rightHandSide) {

        HashSet<Terminal> set = new HashSet<Terminal>(); // Creates a new set
        HashSet<Terminal> right; // Defines a temporary set

        for ( Symbol aRightHandSide : rightHandSide ) { // For each symbol in right hand side
            right = first( aRightHandSide ); // Get first set for that symbol
            set.addAll( right ); // Add all of it's terminal into set
            if ( !right.contains( cfg.terminals.get( "\0" ) ) ) { // If temporary set not contains epsilon
                set.remove( cfg.terminals.get( "\0" ) ); // Remove epsilon from set and quit the loop
                break;
            }
        }

        return set;
    }

    /**
     * Returns first set for a Terminal
     * @param terminal To calculate first set for.
     * @return First set for Terminal.
     */
    private HashSet<Terminal> first( Terminal terminal ) {
        HashSet<Terminal> temp = new HashSet<Terminal>(); // Creates a set
        temp.add( terminal ); // Adds Terminal into it
        return temp; // Returns the set
    }

    /**
     * Calculates follow set for non-Terminal. If follow set calculated before not stops calculating.
     * @param nonTerminal To calculate follow set for.
     * @return Follow set for non-terminal
     */
    private HashSet<Terminal> follow( NonTerminal nonTerminal ) {
        if( !followSets.containsKey( nonTerminal ) ) { // If follow set not calculated before
            followSets.put( nonTerminal, new HashSet<Terminal>() ); // Create a set for non-Terminal
            if( nonTerminal.equals( cfg.startSymbol ) ) { // If non-Terminal is start symbol
                followSets.get( nonTerminal ).add( cfg.endSymbol ); // Add end symbol to it's follow set
            }
        }
        for( NonTerminal nonT : cfg.productions.keySet() ) { // For each non-Terminal
            for( RightHandSide rhs : cfg.productions.get( nonT ) ) { // For each right hand side for that non-Terminal
                if( rhs.contains( nonTerminal ) ) { // If right hand side contains non-Terminal then go on, if not skip
                    int idx = rhs.indexOf( nonTerminal ); // Get index of non-Terminal
                    if( idx == rhs.size() - 1 ) { // If this is the last symbol in right hand side
                        followSets.get( nonTerminal ).addAll( follow1( nonT ) ); // Add follow set of nonT to follow set of non-Terminal
                        break;
                    }
                    RightHandSide tmpRhs = new RightHandSide(); // Creates a temporary right hand side
                    tmpRhs.addAll( rhs.subList( idx + 1, rhs.size() ) ); // Puts all symbols after index into temp right hand side
                    HashSet<Terminal> tmpFirst = first( tmpRhs ); // Get first set for temp right hand side
                    followSets.get( nonTerminal ).addAll( tmpFirst ); // Add all Terminals in first set to follow set
                    if( tmpFirst.contains( cfg.terminals.get( "\0" ) ) ) { // If first set contains epsilon
                        tmpFirst.remove( cfg.terminals.get( "\0" ) ); // Remove it from first set
                        followSets.get( nonTerminal ).remove( cfg.terminals.get( "\0" ) ); // Either from follow set
                        followSets.get( nonTerminal ).addAll( follow1( nonT ) ); // Add follow set of nonT to follow set of non-Terminal
                    }
                }
            }
        }
        return followSets.get( nonTerminal );
    }

    /**
     * Calculates follow set for non-Terminal. If follow set calculated before stops calculating.
     * @param nonTerminal
     * @return
     */
    private HashSet<Terminal> follow1( NonTerminal nonTerminal ) {
        if( !followSets.containsKey( nonTerminal ) ) { // If follow set not calculated before
            followSets.put( nonTerminal, new HashSet<Terminal>() ); // Create a set for non-Terminal
            if( nonTerminal.equals( cfg.startSymbol ) ) { // If non-Terminal is start symbol
                followSets.get( nonTerminal ).add( cfg.endSymbol ); // Add end symbol to it's follow set
            }
        } else {
            return followSets.get( nonTerminal ); // Return already calculated follow set for non-Terminal
        }
        for( NonTerminal nonT : cfg.productions.keySet() ) { // For each non-Terminal
            for( RightHandSide rhs : cfg.productions.get( nonT ) ) { // For each right hand side for that non-Terminal
                if( rhs.contains( nonTerminal ) ) { // If right hand side contains non-Terminal then go on, if not skip
                    int idx = rhs.indexOf( nonTerminal ); // Get index of non-Terminal
                    if( idx == rhs.size() - 1 ) { // If this is the last symbol in right hand side
                        followSets.get( nonTerminal ).addAll( follow1( nonT ) ); // Add follow set of nonT to follow set of non-Terminal
                        break;
                    }
                    RightHandSide tmpRhs = new RightHandSide(); // Creates a temporary right hand side
                    tmpRhs.addAll( rhs.subList( idx + 1, rhs.size() ) ); // Puts all symbols after index into temp right hand side
                    HashSet<Terminal> tmpFirst = first( tmpRhs ); // Get first set for temp right hand side
                    followSets.get( nonTerminal ).addAll( tmpFirst ); // Add all Terminals in first set to follow set
                    if( tmpFirst.contains( cfg.terminals.get( "\0" ) ) ) { // If first set contains epsilon
                        tmpFirst.remove( cfg.terminals.get( "\0" ) ); // Remove it from first set
                        followSets.get( nonTerminal ).remove( cfg.terminals.get( "\0" ) ); // Either from follow set
                        followSets.get( nonTerminal ).addAll( follow1( nonT ) ); // Add follow set of nonT to follow set of non-Terminal
                    }
                }
            }
        }
        return followSets.get( nonTerminal );
    }
}
