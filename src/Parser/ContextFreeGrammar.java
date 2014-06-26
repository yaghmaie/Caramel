package Parser;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by pejman on 6/19/14.
 */
public class ContextFreeGrammar {
    final HashMap<NonTerminal, HashSet<RightHandSide>> productions = new HashMap<NonTerminal, HashSet<RightHandSide>>();
    final HashMap<String, NonTerminal> nonTerminals = new HashMap<String, NonTerminal>();
    final HashMap<String, Terminal> terminals = new HashMap<String, Terminal>();
    final NonTerminal startSymbol;
    final Terminal endSymbol = new Terminal( "$" );

    ContextFreeGrammar( String startSymbol ) {
        this.startSymbol = getNonTerminal( startSymbol );
    }

    private NonTerminal getNonTerminal( String str ) {
        if( !nonTerminals.containsKey( str ) ) {
            nonTerminals.put( str, new NonTerminal( str ) );
        }
        return nonTerminals.get( str );
    }
    private Terminal getTerminal( String str ) {
        if( !terminals.containsKey( str ) ) {
            terminals.put( str, new Terminal( str ) );
        }
        return terminals.get( str );
    }

    public void addProduction( String nonTerminal, String rightHandSide ) {
        RightHandSide rightHandSideArray = new RightHandSide();
        for( int i = 0; i < rightHandSide.length(); i++ ) {
            int strStart = 0;
            switch ( rightHandSide.charAt( i ) ) {
                case '`':
                    strStart = i;
                    i++;
                    while ( rightHandSide.charAt( i ) != '`' || rightHandSide.charAt( i - 1 ) == '\\' ) {
                        i++;
                    }
                    rightHandSideArray.add( getTerminal( rightHandSide.substring( strStart + 1, i ) ) );
                    break;
                case '~':
                    strStart = i;
                    i++;
                    while ( rightHandSide.charAt( i ) != '~' ) {
                        i++;
                    }
                    rightHandSideArray.add( getNonTerminal( rightHandSide.substring( strStart + 1, i ) ) );
                    break;
            }
        }
        if( !productions.containsKey( getNonTerminal( nonTerminal ) ) ) {
            HashSet<RightHandSide> tempSet = new HashSet<RightHandSide>();
            tempSet.add( rightHandSideArray );
            productions.put( getNonTerminal( nonTerminal ), tempSet );
        } else {
            productions.get( getNonTerminal( nonTerminal ) ).add( rightHandSideArray );
        }
    }
}
