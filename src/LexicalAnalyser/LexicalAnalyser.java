package LexicalAnalyser;

import java.util.ArrayList;
import Fundamentals.Token;
/**
 * Created by pejman on 5/23/14.
 */
public class LexicalAnalyser {
    /**
     * Receives code and a DFA and extracts tokens from code using that DFA.
     * @param code Input string to extract tokens from.
     * @param dfa For matching code pieces with token classes.
     * @return Array of tokens extracted from code.
     * @throws LexicalAnalyserException
     */
    public ArrayList<Token> getTokenList( String code, DFA dfa ) throws LexicalAnalyserException {
        ArrayList<Token> list = new ArrayList<Token>(); // Creates list of the tokens

        code = code.concat( "\n" ); // Inserts a new line character at the end of the code

        State curState = dfa.getStartState(); // Current state of DFA
        String curInput = null; // Creates current input equals to null
        String curLexeme = ""; // Creates current lexeme equals to null string
        String lastClass = null; // Last token class matched
        String lastLexeme = null; // Last lexeme matched

        int line = 1; // Counter for line each token appeared at.
        int lastCharPos = 0;
        for( int i = 0; i < code.length(); i++ ) { // Process the whole code
            curInput = String.valueOf( code.charAt( i ) ); // Takes next character form code
            try {
                curState = dfa.getNextState( curState, curInput ); // Does a transition with current input and current DFA state
                curLexeme = curLexeme.concat(curInput); // Concatenates current input with current lexeme
                if( curState.isFinal() ) { // If current state is final then the current lexeme is matched
                    lastClass = curState.getName(); // Set the matched token class
                    lastLexeme = curLexeme; // Set the matched lexeme
                    lastCharPos = i; // Set the location where last valid lexeme seen
                }
            } catch( TransitionNotFoundException e ) { // If transition not found in DFA then extract the last seen token
                if( lastClass == null ) { // If class of the input is unknown then input is not valid, throw exception
                    throw new LexicalAnalyserException( "Invalid input at line: " + line );
                } else
                if( lastClass.equals( "ws" ) ) { // If class is white space then remove the matched token
                    for( int it = 0; it < lastLexeme.length(); it++ ) {
                        if( lastLexeme.charAt( it ) == '\n' ) {
                            line++;
                        }
                    }
                    i = lastCharPos;
                    lastClass = null;
                    lastLexeme = null;
                    curLexeme = "";
                    curState = dfa.getStartState();
                } else { // Add the token to list
                    i = lastCharPos;
                    list.add( new Token( lastClass, lastLexeme, line ) );
                    lastClass = null;
                    lastLexeme = null;
                    curLexeme = "";
                    curState = dfa.getStartState();
                }

            }

        }

        return list;
    }
}
