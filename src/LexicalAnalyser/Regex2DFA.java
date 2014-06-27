package LexicalAnalyser;

import java.util.*;

/**
 * Created by pejman on 5/23/14.
 */
public class Regex2DFA {
    /**
     * List of NFAs built from rules
     */
    private ArrayList<NFA> nfas = new ArrayList<NFA>();
    /**
     * Maps NFAs name to their index in NFA list
     */
    private HashMap<String, Integer> stringToNFAIndex = new HashMap<String, Integer>();

    /**
     * Receives a string of rules and transforms them to DFA.
     * Rules are regular expression definitions separated by new line. Each regex must be defined like "label=%%regex%%".
     * A label can be a string of letters and digits. Regex can be defined using new characters put into apostrophes (``) and other regex definitions which defined above, put into tilde (~~) with concatenation (.), or (+), closure (*) operators.
     * Example:
     *      letter=%%`a`+`b`%% // An input containing character 'a' or 'b' can be matched with 'letter'
     *      digit=%%`1`+`2`%% // An input containing character '1' or '2' can be matched with 'digit'
     *      digitClosure=%%~digit~*%% // An input containing zero or more 'digits' will be matched with 'digitClosure'
     *      naturalNumber=%%~digit~.~digitClosure~%% // An input with one or more digits will be matched with 'naturalNumber'
     *
     * @param rules String of regular expression definitions.
     * @return DFA equivalent to rules.
     */
    public DFA rules2DFA( String rules ) {

        String[] rulesArray = rules.split( "\n" ); // Splits each rule in 'rules' string to an array

        int i = 0; // Counts number of NFAs
        for( String rule : rulesArray ) { // For each rule in 'rulesArray'
            String label = rule.substring( 0, rule.indexOf( '=' ) ); // Extracts rule's label
            String regex = rule.substring( rule.indexOf( '%' ) + 2, rule.lastIndexOf( '%' ) - 1 ).replaceAll( "\\\\n", "\n" ).replaceAll( "\\\\t", "\t" ); // Extracts regex from rule

            NFA nfa = regex2NFA( regex ); // Transforms regex to NFA
            nfa.getFinalState().setName( label ); // Sets name of final state of NFA to label
            stringToNFAIndex.put( label, i++ ); // Sets an index for label
            nfas.add( nfa ); // Puts nfa into NFA list
        }

        return nfaMap2DFA(); // Transforms all the NFAs in list to a DFA and returns it
    }

    /**
     * Receives a regex and transforms it into a NFA.
     * @param regex String of a regular expression definition.
     * @return NFA equivalent to given regex.
     */
    private NFA regex2NFA( String regex ) {
        // Given regex is in infix form. To calculate NFA for regex, we convert regex into postfix form and start calculating.
        ArrayList<String> postfix = infix2Postfix( regex ); // Converts infix regex definition into postfix. The postfix form is an array of regex elements
        Stack<NFA> stack = new Stack<NFA>(); // Stack for NFA calculation

        for ( String element : postfix ) { // For each element in postfix form
            NFA nfa1, nfa2;
            if ( element.equals( "*" ) ) { // If element is "*" then closure top of stack
                nfa1 = stack.pop(); // Pop the stack and put it into nfa1
                State start = new State( nfa1 ); // Create start state for nfa
                State fin = new State( nfa1 ); // Create final state for nfa
                try {
                    nfa1.addTransition( start, "\0", nfa1.getStartState() ); // Add epsilon transition from start to nfa1 start state
                    nfa1.addTransition( start, "\0", fin ); // Add epsilon transition from start to final state
                    nfa1.addTransition( nfa1.getFinalState(), "\0", start ); // Add epsilon transition from nfa1 final to start state
                    nfa1.addTransition( nfa1.getFinalState(), "\0", fin ); // Add epsilon transition from nfa1 final to final state
                }catch( StateNotBelongsToFAException e ) {
                    System.err.println( "In * regex2NFA NFA: " + e.getMessage() );
                }
                nfa1.setStartState( start ); // Change start state for nfa1
                nfa1.getFinalState().setFinal( false ); // Set finality of final state of nfa1 to false
                fin.setFinal( true ); // Set finality of final state to true
                nfa1.setFinalState( fin ); // Set final as final state for nfa1
                stack.push( nfa1 ); // Push nfa1 into the stack

            } else if ( element.equals( "." ) ) { // If element is "." then concatenate two NFAs above the stack
                nfa2 = stack.pop(); // Pop the stack and put it into nfa1
                nfa1 = stack.pop(); // Pop the stack and put it into nfa2

                nfa1.mergeNFA( nfa2 ); // Merge nfa2 into nfa1
                try {
                    nfa1.addTransition( nfa1.getFinalState(), "\0", nfa2.getStartState() ); // Add transition from nfa1 final state to nfa2 start state
                } catch ( StateNotBelongsToFAException e ) {
                    System.err.println( "In . regex2NFA NFA: " + e.getMessage() );
                }
                nfa1.setStartState( nfa1.getStartState() ); // Change start state for nfa1
                nfa1.getFinalState().setFinal( false ); // Set finality of final state of nfa1 to false
                nfa1.setFinalState( nfa2.getFinalState() ); // Set nfa2 final state as final state for nfa1
                stack.push( nfa1 ); // Push nfa1 into the stack
            } else if ( element.equals( "+" ) ) { // If element is "+" then
                nfa2 = stack.pop(); // Pop the stack and put it into nfa1
                nfa1 = stack.pop(); // Pop the stack and put it into nfa2

                State start = new State( nfa1 ); // Create start state for nfa
                State fin = new State( nfa1 ); // Create final state for nfa

                nfa1.mergeNFA( nfa2 ); // Merge nfa2 into nfa1
                try {
                    nfa1.addTransition( start, "\0", nfa1.getStartState() ); // Add epsilon transition from start to nfa1 start state
                    nfa1.addTransition( start, "\0", nfa2.getStartState() ); // Add epsilon transition from start to nfa2 start state

                    nfa1.addTransition( nfa1.getFinalState(), "\0", fin ); // Add epsilon transition from nfa1 final to final state
                    nfa1.addTransition( nfa2.getFinalState(), "\0", fin ); // Add epsilon transition from nfa2 final to final state
                } catch ( StateNotBelongsToFAException e ) {
                    System.err.println( "In + regex2NFA NFA: " + e.getMessage() );
                }
                nfa1.getFinalState().setFinal( false ); // Set finality of final state of nfa1 to false
                nfa2.getFinalState().setFinal( false ); // Set finality of final state of nfa2 to false
                fin.setFinal( true ); // Set finality of final state to true
                nfa1.setStartState( start ); // Change start state for nfa1
                nfa1.setFinalState( fin ); // Change final state for nfa1
                stack.push( nfa1 ); // Push nfa1 into the stack
            } else { // If element is a character then make it's NFA and push it into the stack. If the element is a NFA label then make a clone of that NFA from NFA list and push it into the stack
                stack.push( element.charAt( 0 ) == '~' ? nfas.get( stringToNFAIndex.get( element.substring( 1, element.length() - 1 ) ) ).clone() : string2Nfa( element.substring( 1, element.length() - 1 ) ) );
            }

        }
        NFA nfa = stack.pop(); // The nfa at top of stack is the result
        nfa.getFinalState().setFinal( true ); // Make sure that it's final state is true
        return nfa; // Return that NFA
    }

    /**
     * Turns an string into a NFA. The NFA will have a start state with one transition to final state with given string.
     * @param str String to turn into NFA.
     * @return NFA which accepts that string.
     */
    private NFA string2Nfa( String str ) {
        NFA nfa = new NFA(); // First create a new NFA
        State s1 = new State( nfa ); // Create start state and final state for that
        State s2 = new State( nfa );
        try {
            nfa.addTransition( s1, str, s2 ); // Set transition from start state to final state with given string.
        } catch ( StateNotBelongsToFAException e ) {
            System.out.println( e.getMessage() );
        }
        s2.setFinal( true );
        nfa.setStartState( s1 );
        nfa.setFinalState( s2 );
        return nfa;
    }

    /**
     * Receives a regex string and converts it into a postfix array of elements.
     * @param regex String containing the regex.
     * @return Array of postfix elements.
     */
    private ArrayList<String> infix2Postfix( String regex ) {
        Stack<Character> stack = new Stack<Character>(); // Create stack for postfix calculation
        ArrayList<String> postfix = new ArrayList<String>(); // Array to hold postfix elements

        for ( int i = 0; i < regex.length(); i++ ) { // For each character in regex string
            int strStart;
            switch ( regex.charAt( i ) ) {
                case '`': // If character is apostrophe
                    strStart = i;
                    i++;
                    while ( regex.charAt( i ) != '`' || regex.charAt( i - 1 ) == '\\' ) { // Extract string between apostrophes
                        i++;
                    }
                    postfix.add( regex.substring( strStart , i + 1 ) ); // Put extracted into postfix array
                    break;
                case '~': // If character is tilde
                    strStart = i;
                    i++;
                    while ( regex.charAt( i ) != '~' ) { // Extract string between tildes
                        i++;
                    }
                    postfix.add( regex.substring( strStart, i + 1 ) ); // Put extracted into postfix array
                    break;
                case '+': // If character is plus
                    while ( !stack.isEmpty() && getSymbolPriority( stack.peek() ) > getSymbolPriority( '+' ) ) { // Pop all the elements having higher priority than plus and put them into postfix array
                        postfix.add( stack.pop().toString() );
                    }
                    stack.push( '+' ); // Push plus into the stack
                    break;
                case '.': // If character is concatenation
                    while ( !stack.isEmpty() && getSymbolPriority( stack.peek() ) > getSymbolPriority( '.' ) ) { // Pop all the elements having higher priority than concatenation and put them into postfix array
                        postfix.add( stack.pop().toString() );
                    }
                    stack.push( '.' ); // Push concatenation into the stack
                    break;
                case '*': // If character is star then push it into the stack
                    stack.push( '*' );
                    break;
                case '(': // If character is open paren then push it into the stack
                    stack.push( '(' );
                    break;
                case ')': // If character is close paren
                    while ( !stack.isEmpty() && stack.peek() != '(' ) { // Pop all elements in the stack and put them into postfix array until open paren seen
                        postfix.add( stack.pop().toString() );
                    }
                    stack.pop(); // Pop open paren from stack
                    break;
            }
        }
        while ( !stack.isEmpty() ) { // If there are elements in the stack pop them all and put them into postfix array
            postfix.add( stack.pop().toString() );
        }

        return postfix; // Return postfix array
    }

    /**
     * Receives a NFA and a state then calculates epsilon closure for that state.
     * @param nfa NFA containing the state.
     * @param state State to calculate epsilon closure for.
     * @return Set of states in epsilon closure.
     */
    private HashSet<State> eClosure( NFA nfa, State state ) {
        HashSet<State> set = new HashSet<State>(); // Create a set to put epsilon closure states into.
        Queue<State> queue = new LinkedList<State>(  ); // A queue to track epsilon transactions

        set.add( state ); // Add given state into set
        queue.add( state ); // Add given state in queue

        while( !queue.isEmpty() ) { // While there is state to calculate epsilon closure for
            try {
                for ( State st : nfa.getNextState( queue.poll(), "\0" ) ) { // For each state in epsilon transition set for the state in head of queue
                    if ( !set.contains( st ) ) { // If set not contains that state
                        set.add( st ); // Add the state into set
                        queue.add( st ); // Add the state into queue
                    }
                }
            } catch ( TransitionNotFoundException e ) {
                // Do noting
            }
        }

        return set; // Return epsilon closure set
    }

    /**
     * Receives a set of states and calculates hash code for them with doing XOR operation for all the states hash codes.
     * @param set State set to calculate hash code for it.
     * @return Hash code for set.
     */
    private int getSetHashCode( HashSet set ) {
        int hash = 0;
        for( Object item : set ) {
            hash ^= item.hashCode();
        }
        return hash;
    }

    /**
     * Transforms all the NFAs in the NFA list into a single DFA and returns it.
     * @return DFA equivalent to all NFAs in NFA list
     */
    private DFA nfaMap2DFA () {
        // dfaStates are states of result DFA
        // dStates are sets of NFA states that are mapped to actual DFA states
        DFA dfa = new DFA(); // result DFA
        Queue<HashSet<State>> dStateQueue = new LinkedList<HashSet<State>>(); // Queue to put dStates in and calculate their transitions with other dStates
        HashMap<Integer, State> dState2dfaStateMap = new HashMap<Integer, State>(); // Mapping dStates to dfaStates using their hash code
        State startState = new State( dfa ); // Start state of result DFA
        startState.setName( "StartState" ); // Setting name of result DFA start state to "StartState"
        dfa.setStartState( startState ); // Putting start state to result DFA

        HashSet<State> dStateStart = new HashSet<State>(); // dState for DFA start state
        for( NFA nfa : nfas ) { // Calculating dState for DFA start state
            dStateStart.addAll( eClosure( nfa, nfa.getStartState() ) );
        }
        dState2dfaStateMap.put( getSetHashCode( dStateStart ), startState ); // Creating hash code for dStateStart and mapping to startState
        dStateQueue.add( dStateStart ); // Adding dStateStart to queue for its transition

        while( !dStateQueue.isEmpty() ) { // Calculate transitions for dStates in queue until no dState exists.
            HashSet<State> dStateFromQueue = dStateQueue.poll(); // Retrieved dState from queue
            HashSet<String> checkedInput = new HashSet<String>(); // Set of input characters checked for transition from this dState
            checkedInput.add( "\0" ); // Adding epsilon to checked input
            int dStateFromQueueHash = getSetHashCode( dStateFromQueue ); // Hash code for dStateFromQueue

            for( State item : dStateFromQueue ) { // For each state 'item' in dStateFromQueue
                for( String transitionString : item.transitionSet ) { // For each string 'transitionString' item can have transition with
                    if( ! checkedInput.contains( transitionString ) ) { // If transitionString is not checked before
                        HashSet<State> newdState = new HashSet<State>(); // Calculate the dState that item goes to with transitionString
                        for( State fromState : dStateFromQueue ) { // For each state 'fromState' in dStateFromQueue
                            if( fromState.transitionSet.contains( transitionString ) ) { // If the state can have transition with transitionString
                                try {
                                    HashSet<State> destinationSet = ( ( NFA ) fromState.fa ).getNextState( fromState, transitionString ); // Get set of states that fromState can have transitions with transitionString
                                    for( State state : destinationSet ) { // Add epsilon closure of all states in destinationSet to new generating dState
                                        newdState.addAll( eClosure( ( ( NFA ) state.fa ), state ) );
                                    }
                                } catch ( TransitionNotFoundException e ) {
                                    System.err.println( e.getMessage() + "\nIn nfaMap2DFA method. Odd exception, critical error." );
                                }
                            }
                        } // dState which item goes to with transitionString is generated now.
                        int newdStateHashCode = getSetHashCode( newdState ); // Calculate hash code for new dState
                        if ( !dState2dfaStateMap.containsKey( newdStateHashCode ) ) { // If the new dState not generated before, then map it a new actual DFA state and add it to queue for calculating its transitions to other dStates
                            dState2dfaStateMap.put( newdStateHashCode, new State( dfa ) );
                            dStateQueue.add( newdState );
                        }
                        // Checking newdState for final states
                        for( State state : newdState ) { // For each state in newdState
                            if( state.isFinal() ) { // If state is final state
                                dState2dfaStateMap.get( newdStateHashCode ).setFinal( true ); // Set finality flag in actual DFA state mapped with this dState to true
                                if( stringToNFAIndex.containsKey( dState2dfaStateMap.get( newdStateHashCode ).getName() ) ) { // If there is a NFA where it's name is same as DFA state name
                                    if( stringToNFAIndex.get( dState2dfaStateMap.get( newdStateHashCode ).getName() ) < stringToNFAIndex.get( state.getName() ) ) { // If the state's name has higher priority than DFA state in NFA list
                                        dState2dfaStateMap.get( newdStateHashCode ).setName( state.getName() ); // Set name of DFA state to state's name
                                    }
                                } else {
                                    dState2dfaStateMap.get( newdStateHashCode ).setName( state.getName() ); // If there is no NFA which name is matched to DFA state then set the DFA state's name to that NFA state'd name
                                }
                            }
                        } // DFA state finality check is completed here
                        try { // Update result DFA with transition found for dState with transitionString
                            dfa.addTransition( dState2dfaStateMap.get( dStateFromQueueHash ), transitionString, dState2dfaStateMap.get( newdStateHashCode ) );
                        } catch ( TransitionInputConflictException e ) {
                            e.printStackTrace();
                        } catch ( StateNotBelongsToFAException ex ) {
                            System.err.println( ex.getMessage() );
                        }
                        checkedInput.add( transitionString ); // Adding transitionString as checked in checkedInput
                    }
                } // Transition with transitionString is found here
            } // All transitions for dState is found here
        }

        return dfa; // Return result DFA
    }

    /**
     * Returns priority of a character. Useful for infix to postfix calculation.
     * @param sym
     * @return
     */
    private int getSymbolPriority( Character sym ) {
        switch ( sym ) {
            case '(':
                return 0;
            case ')':
                return 0;
            case '+':
                return 1;
            case '.':
                return 2;
            case '*':
                return 3;
            default:
                return 0;

        }
    }

}