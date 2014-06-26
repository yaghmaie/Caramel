package LexicalAnalyser;

import Fundamentals.BigTable;

import java.util.HashSet;

/**
 * Created by pejman on 5/23/14.
 */
public class DFA extends BigTable<String, State, State> implements FA {

    private State startState;
    final HashSet<State> finalStates = new HashSet<State>();

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
    public boolean isEmpty() {
        return theTable.isEmpty();
    }

    public State getStartState() {
        return startState;
    }

    public void setStartState( State state ) {
        this.startState = state;
    }

    public void addTransition( State source, String input, State destination ) throws TransitionInputConflictException, StateNotBelongsToFAException{
        if( source.fa.equals( this ) && destination.fa.equals( this ) ) {
            if ( theTable.containsKey( input ) ) {
                if ( theTable.get( input ).containsKey( source ) ) {
                    throw new TransitionInputConflictException( "State has already transition with \"" + input + "\"" );
                } else {
                    theTable.get( input ).put( source, destination );
                }
            } else {
                addSecondaryKey( input, source, destination );
            }
            source.transitionSet.add( input );
        } else {
            throw new StateNotBelongsToFAException( "source or destination state not blongs to this DFA." );
        }
    }

    public State getNextState( State source, String input ) throws TransitionNotFoundException {
        if( theTable.containsKey( input ) && theTable.get( input ).containsKey( source ) ) {
            return theTable.get( input ).get( source );
        }
        throw new TransitionNotFoundException( "No transition with \"" + input + "\" and given State." );
    }
}
