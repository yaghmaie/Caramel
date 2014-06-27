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
    /**
     * Calculates parse tree from token list using a parse table.
     * @param tokenList to make parse tree of.
     * @param parseTable to do derivations with.
     * @return Parse tree.
     * @throws ParserException
     */
    public ParseTree getParseTree( ArrayList<Token> tokenList, ParseTable parseTable ) throws ParserException {
        ParseTreeNode topOfStack = new ParseTreeNode( null, parseTable.contextFreeGrammar.startSymbol ); // Create top of stack and bind start symbol with it
        ParseTree parseTree = new ParseTree( topOfStack ); // Create new parse tree

        Stack<ParseTreeNode> stack = new Stack<ParseTreeNode>(); // Create stack to do calculation
        stack.push( topOfStack ); // Push topOfStack into stack

        Token token; // Current token
        Terminal next; // Next Terminal
        RightHandSide rhs; // Right hand side

        int tokenIter = 0;
        while ( tokenIter < tokenList.size() ) { // While there are un processed tokens in list
            token = tokenList.get( tokenIter ); // Take a token
            next = parseTable.contextFreeGrammar.terminals.get( token.tokenClass ); // Get token terminal
            topOfStack = stack.peek(); // Load topOfStack

            if( topOfStack.symbol.getClass().equals( Terminal.class ) ) { // If top of stack is a Terminal
                if( topOfStack.symbol.equals( next ) ) { // If Terminal equals next
                    stack.pop(); // Pop stack
                    tokenIter++; // Move one token forward
                    topOfStack.setValue( token.lexeme ); // Set lexeme of token as value of current tree node
                } else { // If not, throw exception
                    throw new ParserException( "Unexpected input at line " + token.line + " near \"" + token.lexeme + "\"." );
                }
            } else { // If top of stack is a non-Terminal
                stack.pop(); // Pop stack
                try {
                    rhs = parseTable.getEntry( (NonTerminal)topOfStack.symbol, next ); // Take right hand side from parse table
                } catch ( Exception e ) {
                    throw new ParserException( "Invalid input at line " + token.line + " near \"" + token.lexeme + "\"." );
                }
                if( rhs.size() == 0 ) { // If right hand side is not empty then set it's symbols as children for current node
                    topOfStack.children.add( 0, new ParseTreeNode( topOfStack, parseTable.contextFreeGrammar.terminals.get( "\0" ), "\0" ) );
                }
                for( int i = rhs.size() - 1; i >= 0; i-- ) { // Push symbols into the stack
                    topOfStack.children.add( 0, new ParseTreeNode( topOfStack, rhs.get( i ) ) );
                    stack.push( topOfStack.children.get(0) );
                }
            }
        }
        while( !stack.isEmpty() ) { // Tokens are finished here, try to derive remaining non-Terminals to epsilon
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
