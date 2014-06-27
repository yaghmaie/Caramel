package LexicalAnalyser;

import Fundamentals.BigTable;

import java.util.HashSet;

/**
 * Created by pejman on 5/23/14.
 */
public class DFA extends BigTable<String, State, State> implements FA {
    /**
     * Start state of the DFA
     */
    private State startState;

    /**
     * Dumps DFA table. Useful for debugging purposes.
     */
    public void dump() {
        if( this.isEmpty() ) {
            System.out.println("NFA is empty");
            return;
        }
        for( String str : theTable.keySet() ) {
            System.out.println("Transitions with \"" + str + "\".");
            for( State state : theTable.get( str ).keySet() ) {
                if( state.equals( startState ) )
                    System.out.print( "From state : &" + state.getName() + "& to " + theTable.get( str ).get( state ).getName() );
                else
                    System.out.print( "From state : " + state.getName() + " to " + theTable.get( str ).get( state ).getName() );

                System.out.println();
            }
        }
    }

    /**
     * Checks if DFA is empty.
     * @return boolean.
     */
    public boolean isEmpty() {
        return theTable.isEmpty();
    }

    /**
     * Returns DFA start state.
     * @return Start state.
     */
    public State getStartState() {
        return startState;
    }

    /**
     * Sets DFA start state.
     * @param state Set as start state.
     */
    public void setStartState( State state ) {
        this.startState = state;
    }

    /**
     * Adds a transition from a state to another state with a string.
     * @param source State to have transition from.
     * @param input String to have transition with.
     * @param destination State to have transition to.
     * @throws TransitionInputConflictException
     * @throws StateNotBelongsToFAException
     */
    public void addTransition( State source, String input, State destination ) throws TransitionInputConflictException, StateNotBelongsToFAException{
        if( source.fa.equals( this ) && destination.fa.equals( this ) ) { // If this DFA owns the source and destination
            if ( theTable.containsKey( input ) ) {
                if ( theTable.get( input ).containsKey( source ) ) { // If there is an entry with source and input string throw exception
                    throw new TransitionInputConflictException( "State has already transition with \"" + input + "\"" );
                } else {
                    theTable.get( input ).put( source, destination );
                }
            } else {
                addSecondaryKey( input, source, destination );
            }
            source.transitionSet.add( input );
        } else { // If this DFA does not owns the source or destination throw exception
            throw new StateNotBelongsToFAException( "source or destination state not belongs to this DFA." );
        }
    }

    /**
     * Return state which source goes to with input.
     * @param source State.
     * @param input String.
     * @return Destination state.
     * @throws TransitionNotFoundException
     */
    public State getNextState( State source, String input ) throws TransitionNotFoundException {
        if( theTable.containsKey( input ) && theTable.get( input ).containsKey( source ) ) {
            return theTable.get( input ).get( source );
        } // If there is no transition with source and input throw exception
        throw new TransitionNotFoundException( "No transition with \"" + input + "\" and given State." );
    }
}
