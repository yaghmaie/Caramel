package LexicalAnalyser;

import java.util.*;

/**
 * Created by pejman on 5/23/14.
 */
public class Regex2DFA {

    private HashMap<Integer, NFA> nfas = new HashMap<Integer, NFA>(  );
    private HashMap<String, Integer> str2Int = new HashMap<String, Integer>();

    public HashMap<Integer, NFA> getNFAMap() {
        return nfas;
    }

    public DFA rules2DFA( String rules ) {

        String[] rulesArray = rules.split( "\n" );

        int i = 0;
        for( String rule : rulesArray ) {
            String label = rule.substring( 0, rule.indexOf( '=' ) );
            String regex = rule.substring( rule.indexOf( '%' ) + 2, rule.lastIndexOf( '%' ) - 1 ).replaceAll( "\\\\n", "\n" ).replaceAll( "\\\\t", "\t" );

            NFA nfa = regex2NFA( regex );
            nfa.getFinalState().setName( label );
            str2Int.put( label, i );
            nfas.put( i++, nfa );
        }

        return nfaMap2DFA();
    }

    private NFA regex2NFA( String regex ) {

        ArrayList<String> postfix = infix2Postfix( regex );
        Stack<NFA> stack = new Stack<NFA>();

        for ( String aPostfix : postfix ) {
            NFA nfa1, nfa2;
            if ( aPostfix.equals( "*" ) ) {
                nfa1 = stack.pop();
                State start = new State( nfa1 );
                State fin = new State( nfa1 );
                try {
                    nfa1.addTransition( start, "\0", nfa1.getStartState() );
                    nfa1.addTransition( start, "\0", fin );
                    nfa1.addTransition( nfa1.getFinalState(), "\0", start );
                    nfa1.addTransition( nfa1.getFinalState(), "\0", fin );
                }catch( StateNotBelongsToFAException e ) {
                    System.err.println( "In * regex2NFA NFA: " + e.getMessage() );
                }
                nfa1.setStartState( start );
                nfa1.getFinalState().setFinal( false );
                fin.setFinal( true );
                nfa1.setFinalState( fin );
                stack.push( nfa1 );

            } else if ( aPostfix.equals( "." ) ) {
                nfa2 = stack.pop();
                nfa1 = stack.pop();

                nfa1.mergeNFA( nfa2 );
                try {
                    nfa1.addTransition( nfa1.getFinalState(), "\0", nfa2.getStartState() );
                } catch ( StateNotBelongsToFAException e ) {
                    System.err.println( "In . regex2NFA NFA: " + e.getMessage() );
                }
                nfa1.setStartState( nfa1.getStartState() );
                nfa1.getFinalState().setFinal( false );
                nfa1.setFinalState( nfa2.getFinalState() );
                stack.push( nfa1 );
            } else if ( aPostfix.equals( "+" ) ) {
                nfa2 = stack.pop();
                nfa1 = stack.pop();

                State start = new State( nfa1 );
                State fin = new State( nfa1 );

                nfa1.mergeNFA( nfa2 );
                try {
                    nfa1.addTransition( start, "\0", nfa1.getStartState() );
                    nfa1.addTransition( start, "\0", nfa2.getStartState() );

                    nfa1.addTransition( nfa1.getFinalState(), "\0", fin );
                    nfa1.addTransition( nfa2.getFinalState(), "\0", fin );
                } catch ( StateNotBelongsToFAException e ) {
                    System.err.println( "In + regex2NFA NFA: " + e.getMessage() );
                }
                nfa1.getFinalState().setFinal( false );
                nfa2.getFinalState().setFinal( false );
                fin.setFinal( true );
                nfa1.setStartState( start );
                nfa1.setFinalState( fin );
                stack.push( nfa1 );
            } else {
                stack.push( aPostfix.charAt( 0 ) == '~' ? nfas.get( str2Int.get( aPostfix.substring( 1, aPostfix.length() - 1 ) ) ).clone() : string2Nfa( aPostfix.substring( 1, aPostfix.length() - 1 ) ) );
            }

        }
        NFA nfa = stack.pop();
        nfa.getFinalState().setFinal( true );
        return nfa;
    }

    private NFA string2Nfa( String str ) {
        NFA nfa = new NFA();
        State s1 = new State( nfa );
        State s2 = new State( nfa );
        try {
            nfa.addTransition( s1, str, s2 );
        } catch ( StateNotBelongsToFAException e ) {
            e.printStackTrace();
        }
        s2.setFinal( true );
        nfa.setStartState( s1 );
        nfa.setFinalState( s2 );
        return nfa;
    }

    private ArrayList<String> infix2Postfix( String regex ) {
        Stack<Character> stack = new Stack<Character>();
        ArrayList<String> postfix = new ArrayList<String>();

        for ( int i = 0; i < regex.length(); i++ ) {
            int strStart;
            switch ( regex.charAt( i ) ) {
                case '`':
                    strStart = i;
                    i++;
                    while ( regex.charAt( i ) != '`' || regex.charAt( i - 1 ) == '\\' ) {
                        i++;
                    }
                    postfix.add( regex.substring( strStart , i + 1 ) );
                    break;
                case '~':
                    strStart = i;
                    i++;
                    while ( regex.charAt( i ) != '~' ) {
                        i++;
                    }
                    postfix.add( regex.substring( strStart, i + 1 ) );
                    break;
                case '+':
                    while ( !stack.isEmpty() && getSymbolPriority( stack.peek() ) > getSymbolPriority( '+' ) ) {
                        postfix.add( stack.pop().toString() );
                    }
                    stack.push( '+' );
                    break;
                case '.':
                    while ( !stack.isEmpty() && getSymbolPriority( stack.peek() ) > getSymbolPriority( '.' ) ) {
                        postfix.add( stack.pop().toString() );
                    }
                    stack.push( '.' );
                    break;
                case '*':
                    stack.push( '*' );
                    break;
                case '(':
                    stack.push( '(' );
                    break;
                case ')':
                    while ( !stack.isEmpty() && stack.peek() != '(' ) {
                        postfix.add( stack.pop().toString() );
                    }
                    stack.pop();
                    break;
            }
        }
        while ( !stack.isEmpty() ) {
            postfix.add( stack.pop().toString() );
        }

        return postfix;
    }

    private HashSet<State> eClosure( NFA nfa, State state ) {
        HashSet<State> set = new HashSet<State>();
        Queue<State> queue = new LinkedList<State>(  );

        set.add( state );
        queue.add( state );

        while( !queue.isEmpty() ) {
            try {
                for ( State st : nfa.getNextState( queue.poll(), "\0" ) ) {
                    if ( !set.contains( st ) ) {
                        set.add( st );
                        queue.add( st );
                    }
                }
            } catch ( TransitionNotFoundException e ) {

            }
        }

        return set;
    }

    private int getSetHashCode( HashSet set ) {
        int hash = 0;
        for( Object item : set ) {
            hash ^= item.hashCode();
        }
        return hash;
    }

    private DFA nfaMap2DFA () {
        DFA dfa = new DFA();
        Queue<HashSet<State>> queue = new LinkedList<HashSet<State>>();
        HashMap<Integer, State> stateMap = new HashMap<Integer, State>();
        State startState = new State( dfa );
        startState.setName( "StartSt" );
        dfa.setStartState( startState );

        HashSet<State> dState1 = new HashSet<State>();
        for( NFA nfa : nfas.values() ) {
            dState1.addAll( eClosure( nfa, nfa.getStartState() ) );
        }
        stateMap.put( getSetHashCode( dState1 ), startState );
        queue.add( dState1 );

        while( !queue.isEmpty() ) {
            HashSet<State> tempSet = queue.poll();
            HashSet<String> checkedInput = new HashSet<String>();
            checkedInput.add( "\0" );
            int tempHash = getSetHashCode( tempSet );

            for( State item : tempSet ) {
                for( String str : item.transitionSet ) {
                    if( ! checkedInput.contains( str ) ) {

                        HashSet<State> newdState = new HashSet<State>();
                        for( State itemIter : tempSet ) {
                            if( itemIter.transitionSet.contains( str ) ) {
                                try {
                                    HashSet<State> resultSet = ( ( NFA ) itemIter.fa ).getNextState( itemIter, str );
                                    for( State dState : resultSet ) {
                                        newdState.addAll( eClosure( ( ( NFA ) dState.fa ), dState ) );
                                    }
                                } catch ( TransitionNotFoundException e ) {
                                    System.err.println( e.getMessage() + "\nIn nfaMap2DFA method. Odd exception, critical error." );
                                }
                            }
                        }
                        int newdHash = getSetHashCode( newdState );
                        if ( !stateMap.containsKey( newdHash ) ) {
                            stateMap.put( newdHash, new State( dfa ) );
                            queue.add( newdState );
                        }

                        for( State iterState : newdState ) {
                            if( iterState.isFinal() ) {
                                stateMap.get( newdHash ).setFinal( true );
                                if( str2Int.containsKey( stateMap.get( newdHash ).getName() ) ) {
                                    if( str2Int.get( stateMap.get( newdHash ).getName() ) < str2Int.get( iterState.getName() ) ) {
                                        stateMap.get( newdHash ).setName( iterState.getName() );
                                    }
                                } else {
                                    stateMap.get( newdHash ).setName( iterState.getName() );
                                }
                            }
                        }
                        try {
                            dfa.addTransition( stateMap.get( tempHash ), str, stateMap.get( newdHash ) );
                        } catch ( TransitionInputConflictException e ) {
                            e.printStackTrace();
                        } catch ( StateNotBelongsToFAException ex ) {
                            System.err.println( ex.getMessage() );
                        }
                        checkedInput.add( str );
                    }
                }
            }
        }

        return dfa;
    }

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