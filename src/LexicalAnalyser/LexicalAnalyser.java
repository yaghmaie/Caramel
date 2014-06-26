package LexicalAnalyser;

import java.util.ArrayList;
import Fundamentals.Token;
/**
 * Created by pejman on 5/23/14.
 */
public class LexicalAnalyser {

    public ArrayList<Token> getTokenList( String code, DFA dfa ) throws LexicalAnalyserException {
        ArrayList<Token> list = new ArrayList<Token>();

        code = code.concat( "\n" );

        State curState = dfa.getStartState();
        String curInput = null;
        String curLexeme = "";
        String lastClass = null;
        String lastLexeme = null;

        int line = 1;
        int lastCharPos = 0;
        for( int i = 0; i < code.length(); i++ ) {
            curInput = String.valueOf( code.charAt( i ) );
            try {
                curState = dfa.getNextState( curState, curInput );
                curLexeme = curLexeme.concat(curInput);
                if( curState.isFinal() ) {
                    lastClass = curState.getName();
                    lastLexeme = curLexeme;
                    lastCharPos = i;
                }
            } catch( TransitionNotFoundException e ) {
                if( lastClass == null ) {
                    throw new LexicalAnalyserException( "Invalid input at line: " + line );
                } else
                if( lastClass.equals( "ws" ) ) {
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
                } else {
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
