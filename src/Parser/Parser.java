/*
 * Simple solutions for creating lexical analyzer, syntax analyzer, code generator.
 *     Copyright (C) 2014  Pejman Yaghmaie.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package Parser;

import Fundamentals.Token;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by pejman on 6/10/14.
 */
public class Parser {

    public ParseTree getParseTree( ArrayList<Token> tokenList, ParseTable parseTable ) throws ParserException {
        ParseTreeNode topOfStack = new ParseTreeNode( null, parseTable.contextFreeGrammar.startSymbol );
        ParseTree parseTree = new ParseTree( topOfStack );

        Stack<ParseTreeNode> stack = new Stack<ParseTreeNode>();
        stack.push( topOfStack );

        Token token;
        Terminal next;
        RightHandSide rhs;

        for( int tokenIter = 0; tokenIter < tokenList.size();  ) {
            token = tokenList.get( tokenIter );
            next = parseTable.contextFreeGrammar.terminals.get( token.tokenClass );
            topOfStack = stack.peek();

            if( topOfStack.symbol.getClass().equals( Terminal.class ) ) {
                if( topOfStack.symbol.equals( next ) ) {
                    stack.pop();
                    tokenIter++;
                    topOfStack.setValue( token.lexeme );
                } else {
                    throw new ParserException( "Invalid input at line " + token.line + " near \"" + token.lexeme + "\"." );
                }
            } else {
                stack.pop();
                try {
                    rhs = parseTable.getEntry( (NonTerminal)topOfStack.symbol, next );
                } catch ( Exception e ) {
                    throw new ParserException( "Invalid input at line " + token.line + " near \"" + token.lexeme + "\"." );
                }
                if( rhs.size() == 0 ) {
                    topOfStack.children.add( 0, new ParseTreeNode( topOfStack, parseTable.contextFreeGrammar.terminals.get( "\0" ), "\0" ) );
                }
                for( int i = rhs.size() - 1; i >= 0; i-- ) {
                    topOfStack.children.add( 0, new ParseTreeNode( topOfStack, rhs.get( i ) ) );
                    stack.push( topOfStack.children.get(0) );
                }
            }
        }
        while( !stack.isEmpty() ) {
            try {
                if( stack.peek().symbol.getClass().equals( NonTerminal.class ) && parseTable.getEntry( (NonTerminal)(stack.peek().symbol), parseTable.contextFreeGrammar.endSymbol ).equals( parseTable.emptyRightHandSide ) ){
                    stack.pop().children.add( 0, new ParseTreeNode( topOfStack, parseTable.contextFreeGrammar.terminals.get( "\0" ), "\0" ) );
                }
                else throw new ParserException( "Run out of input tokens while parser stack not empty." );
            } catch ( Exception e ) {
                throw new ParserException( "Run out of input tokens while parser stack not empty." );
            }
        }

        return parseTree;
    }

}
