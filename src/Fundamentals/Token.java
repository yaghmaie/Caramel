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

package Fundamentals;

/**
 * Created by pejman on 6/10/14.
 */
public class Token {
    public final String tokenClass;
    public final String lexeme;
    public final int line;
    public Token( String tokenClass, String lexeme, int line ) {
        this.tokenClass = tokenClass;
        this.lexeme = lexeme;
        this.line = line;
    }
    public void dump() {
        System.out.printf( "Class: %20s\t\t\tlexeme: %30s\t\tline #%04d\n", tokenClass, lexeme, line );
    }
}
