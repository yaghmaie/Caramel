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
