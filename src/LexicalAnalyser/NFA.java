package LexicalAnalyser;

import Fundamentals.BigTable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by pejman on 5/23/14.
 */
public class NFA extends BigTable<String, State, HashSet > implements FA {
    /**
     * Start state of NFA
     */
    private State startState;
    /**
     * Final state of NFA
     */
    private State finalState;

    /**
     * Returns start state.
     * @return Start state.
     */
    public State getStartState() {
        return startState;
    }

    /**
     * Sets start state.
     * @param state State.
     */
    public void setStartState( State state ) {
        this.startState = state;
    }

    /**
     * Adds a transition from source to destination with input string to NFA.
     * @param source State to have transition from.
     * @param input String to have transition with.
     * @param destination State to have transition to.
     * @throws StateNotBelongsToFAException
     */
    public void addTransition( State source, String input, State destination ) throws StateNotBelongsToFAException {
        if( source.fa.equals( this ) && destination.fa.equals( this ) ) { // If this NFA owns the source and destination
            if ( theTable.containsKey( input ) ) {
                if ( theTable.get( input ).containsKey( source ) ) {
                    theTable.get( input ).get( source ).add( destination );
                } else {
                    HashSet<State> tempSet = new HashSet<State>();
                    tempSet.add( destination );
                    theTable.get( input ).put( source, tempSet );
                }
            } else {
                HashSet<State> tempSet = new HashSet<State>();
                tempSet.add( destination );
                addSecondaryKey( input, source, tempSet );
            }
            source.transitionSet.add( input );
        } else { // If this NFA does not owns the source or destination throw exception
            throw new StateNotBelongsToFAException( "source or destination state not blongs to this NFA." );
        }
    }

    /**
     * Returns final state of NFA.
     * @return State.
     */
    public State getFinalState() {
        return finalState;
    }

    /**
     * Sets final state of the NFA.
     * @param state State.
     */
    public void setFinalState( State state ) {
        this.finalState = state;
    }

    /**
     * Return state which source goes to with input.
     * @param source State.
     * @param input String.
     * @return Destination state.
     * @throws TransitionNotFoundException
     */
    public HashSet<State> getNextState( State source, String input ) throws TransitionNotFoundException {
        if( theTable.containsKey( input ) && theTable.get( input ).containsKey( source ) ) {
            return theTable.get( input ).get( source );
        } // If there is no transition with source and input throw exception
        throw new TransitionNotFoundException( "No transition with \"" + input + "\" and given State." );
    }

    /**
     * Merges another NFA into this NFA. All the states of the other NFA will be owned by this NFA.
     * @param nfa
     */
    public void mergeNFA( NFA nfa ) {
        for( String priKey : nfa.theTable.keySet() ) {
            for( State secKey : nfa.theTable.get( priKey ).keySet() ) {
                secKey.setFinal( false );
                secKey.fa = this;
                for( State state : (HashSet<State>)nfa.theTable.get( priKey ).get( secKey ) ) {
                    state.fa = this;
                    state.setFinal( false );
                    try {
                        this.addTransition( secKey, priKey, state );
                    } catch ( StateNotBelongsToFAException e ) {
                        System.err.println( "In Merge NFA: " + e.getMessage() );
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public NFA clone() {
        NFA res = new NFA();
        HashMap<State, State> tempMap = new HashMap<State, State>();
        for( String priKey : this.theTable.keySet() ) {
            for( State secKey : this.theTable.get( priKey ).keySet() ) {
                if( !tempMap.containsKey( secKey ) )
                    tempMap.put( secKey, new State(res) );
                for( State state : (HashSet<State>)this.theTable.get( priKey ).get( secKey ) ) {
                    if( !tempMap.containsKey( state ) )
                        tempMap.put( state, new State(res) );
                    try {
                        res.addTransition( tempMap.get( secKey ), priKey, tempMap.get( state ) );
                    } catch ( StateNotBelongsToFAException e ) {
                        System.err.println( "In Clone NFA: " + e.getMessage() );
                    }
                }
            }
        }
        res.setStartState( tempMap.get( this.startState ) );
        res.setFinalState( tempMap.get( this.finalState ) );
        return res;
    }

    /**
     * Dumps NFA table. Useful for debugging purposes.
     */
    public void dump() {
        if( this.isEmpty() ) {
            System.out.println("NFA is empty");
            return;
        }
        for( String str : theTable.keySet() ) {
            System.out.println("Transitions with \"" + (str.equals("\0") ? "epsilon" : str) + "\".");
            for( State state : theTable.get( str ).keySet() ) {
                if( state.equals( startState ) )
                    System.out.print( "From state : &" + state.getName() + "& to " );
                else
                    System.out.print( "From state : " + state.getName() + " to " );
                for( State st : (HashSet<State>)theTable.get( str ).get( state ) ) {
                    System.out.print( st.getName().equals( finalState.getName() ) ? "*" + st.getName() + "*" : st.getName() + "," );
                }
                System.out.println();
            }
        }
    }
    /**
     * Checks if NFA is empty.
     * @return boolean.
     */
    public boolean isEmpty() {
        return theTable.isEmpty();
    }

}