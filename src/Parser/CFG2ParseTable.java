package Parser;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by pejman on 6/19/14.
 */
public class CFG2ParseTable {
    private HashMap<NonTerminal, HashSet<Terminal>> firstSets = new HashMap<NonTerminal, HashSet<Terminal>>();
    private HashMap<NonTerminal, HashSet<Terminal>> followSets = new HashMap<NonTerminal, HashSet<Terminal>>();
    private ContextFreeGrammar cfg;

    public ParseTable rules2ParseTable( String rules ) {
        String[] rulesArray = rules.split( "\n" );
        cfg = new ContextFreeGrammar( rulesArray[0].substring( 0, rulesArray[0].indexOf( '=' ) ) );
        ParseTable parseTable = new ParseTable( cfg );

        for( String rule : rulesArray ) {
            String nonTerminal = rule.substring( 0, rule.indexOf( '=' ) );
            String rightHandSide = rule.substring( rule.indexOf( '%' ) + 2, rule.lastIndexOf( '%' ) - 1 );
            cfg.addProduction( nonTerminal, rightHandSide );
        }

        for( NonTerminal nonT : cfg.productions.keySet() ) {
            first( nonT );
        }
        for( NonTerminal nonT : cfg.productions.keySet() ) {
            follow( nonT );
        }
        /*for( Terminal t : followSets.get( cfg.nonTerminals.get( "Tp" ) ) ) {
            System.out.println( t.getContent() );
        }*/
        /*for( Terminal t : followSets.get( cfg.nonTerminals.get( "X" ) ) ) {
            System.out.println( t.getContent() );
        }
        System.out.println("/////////");
        for( Terminal t : followSets.get( cfg.nonTerminals.get( "E" ) ) ) {
            System.out.println( t.getContent() );
        }*/
        for( NonTerminal nonT : cfg.productions.keySet() ) {
            for( RightHandSide rhs : cfg.productions.get( nonT ) ) {
                HashSet<Terminal> firs = first(rhs);
                for( Terminal t : firs ) {
                    if( t.equals( cfg.terminals.get("\0") ) )
                        continue;
                    try {
                        parseTable.addEntry( nonT, t, rhs );
                    } catch ( ConflictingParseTableEntryException e ) {
                        System.err.println( e.getMessage() );
                    }
                }
            }
            if( first( nonT ).contains( cfg.terminals.get( "\0" ) ) ) {
                for( Terminal t : follow(nonT) ) {
                    try {
                        parseTable.addEntry( nonT, t, parseTable.emptyRightHandSide );
                    } catch ( ConflictingParseTableEntryException e ) {
                        System.err.println( e.getMessage() );
                    }
                }
            }
        }

        return parseTable;
    }

    private HashSet<Terminal> first( Symbol symbol ) {
        if( symbol.getClass().equals( NonTerminal.class ) ) {
            return first( (NonTerminal)symbol );
        } else {
            return first( (Terminal)symbol );
        }
    }

    private HashSet<Terminal> first( NonTerminal nonTerminal ) {
        if( !firstSets.containsKey( nonTerminal ) ) {
            firstSets.put( nonTerminal, new HashSet<Terminal>() );
        } else {
            return firstSets.get( nonTerminal );
        }
        for( RightHandSide r : cfg.productions.get( nonTerminal ) ) {
            firstSets.get( nonTerminal ).addAll( first( r ) );
        }
        return firstSets.get( nonTerminal );
    }

    private HashSet<Terminal> first( RightHandSide rightHandSide) {

        HashSet<Terminal> set = new HashSet<Terminal>();
        HashSet<Terminal> right;

        for( int i = 0; i < rightHandSide.size(); i++ ) {
            right = first( rightHandSide.get( i ) );
            set.addAll( right );
            if( !right.contains( cfg.terminals.get( "\0" ) ) ) {
                set.remove( cfg.terminals.get( "\0" ) );
                break;
            }
        }

        return set;
    }

    private HashSet<Terminal> first( Terminal terminal ) {
        HashSet<Terminal> temp = new HashSet<Terminal>();
        temp.add( terminal );
        return temp;
    }

    private HashSet<Terminal> follow1( NonTerminal nonTerminal ) {
        if( !followSets.containsKey( nonTerminal ) ) {
            followSets.put( nonTerminal, new HashSet<Terminal>() );
            if( nonTerminal.equals( cfg.startSymbol ) ) {
                followSets.get( nonTerminal ).add( cfg.endSymbol );
            }
        } else {
            return followSets.get( nonTerminal );
        }
        for( NonTerminal nonT : cfg.productions.keySet() ) {
            for( RightHandSide rhs : cfg.productions.get( nonT ) ) {
                if( rhs.contains( nonTerminal ) ) {
                    int idx = rhs.indexOf( nonTerminal );
                    if( idx == rhs.size() - 1 ) {
                        followSets.get( nonTerminal ).addAll( follow( nonT ) );
                        break;
                    }
                    RightHandSide tmpRhs = new RightHandSide();
                    tmpRhs.addAll( rhs.subList( idx + 1, rhs.size() ) );
                    HashSet<Terminal> tmpFirst = first( tmpRhs );
                    followSets.get( nonTerminal ).addAll( tmpFirst );
                    if( tmpFirst.contains( cfg.terminals.get( "\0" ) ) ) {
                        tmpFirst.remove( cfg.terminals.get( "\0" ) );
                        followSets.get( nonTerminal ).remove( cfg.terminals.get( "\0" ) );
                        followSets.get( nonTerminal ).addAll( follow( nonT ) );
                    }
                }
            }
        }
        return followSets.get( nonTerminal );
    }

    private HashSet<Terminal> follow( NonTerminal nonTerminal ) {
        if( !followSets.containsKey( nonTerminal ) ) {
            followSets.put( nonTerminal, new HashSet<Terminal>() );
            if( nonTerminal.equals( cfg.startSymbol ) ) {
                followSets.get( nonTerminal ).add( cfg.endSymbol );
            }
        } /*else {
            return followSets.get( nonTerminal );
        }*/
        for( NonTerminal nonT : cfg.productions.keySet() ) {
            for( RightHandSide rhs : cfg.productions.get( nonT ) ) {
                if( rhs.contains( nonTerminal ) ) {
                    int idx = rhs.indexOf( nonTerminal );
                    if( idx == rhs.size() - 1 ) {
                        followSets.get( nonTerminal ).addAll( follow1( nonT ) );
                        break;
                    }
                    RightHandSide tmpRhs = new RightHandSide();
                    tmpRhs.addAll( rhs.subList( idx + 1, rhs.size() ) );
                    HashSet<Terminal> tmpFirst = first( tmpRhs );
                    followSets.get( nonTerminal ).addAll( tmpFirst );
                    if( tmpFirst.contains( cfg.terminals.get( "\0" ) ) ) {
                        tmpFirst.remove( cfg.terminals.get( "\0" ) );
                        followSets.get( nonTerminal ).remove( cfg.terminals.get( "\0" ) );
                        followSets.get( nonTerminal ).addAll( follow1( nonT ) );
                    }
                }
            }
        }
        return followSets.get( nonTerminal );
    }
}
