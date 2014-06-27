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

package CodeGenerator;

import Parser.ParseTree;
import Parser.ParseTreeNode;

/**
 * Created by pejman on 6/25/14.
 */
public class CodeGenerator {
    public void cgen( ParseTree tree, ParseTreeNode node ) {
        if( node.parent == null ) {
            System.out.println("\t.text\n\t.globl main\nmain:");
            cgen( tree, node.children.get(0) );
            cgen( tree, node.children.get(1) );
            System.out.println( "li\t$v0, 1" );
            System.out.println( "syscall" );
        }else
        if( node.symbol.getContent().equals( "E" ) ) {
            cgen( tree, node.children.get(0) );
            cgen( tree, node.children.get(1) );
        }else
        if( node.symbol.getContent().equals( "Ep" ) && node.children.get( 0 ).symbol.getContent().equals( "plus" ) ) {
            System.out.println("sw\t$a0, 0($sp)");
            System.out.println("addiu\t$sp, $sp, -4");
            cgen( tree, node.children.get( 1 ) );
            System.out.println( "lw\t$t1, 4($sp)" );
            System.out.println("add\t$a0, $t1, $a0");
            System.out.println("addiu\t$sp, $sp, 4");
            cgen( tree, node.children.get( 2 ) );
        }else
        if( node.symbol.getContent().equals( "Ep" ) ) {
            return;
        }else
        if( node.symbol.getContent().equals( "T" ) ) {
            cgen( tree, node.children.get( 0 ) );
            cgen( tree, node.children.get( 1 ) );
        }else
        if( node.symbol.getContent().equals( "Tp" ) && node.children.get( 0 ).symbol.getContent().equals( "times" ) ) {
            System.out.println("sw\t$a0, 0($sp)");
            System.out.println("addiu\t$sp, $sp, -4");
            cgen( tree, node.children.get( 1 ) );
            System.out.println( "lw\t$t1, 4($sp)" );
            System.out.println("mult\t$a0, $t1, $a0");
            System.out.println("addiu\t$sp, $sp, 4");
            cgen( tree, node.children.get( 2 ) );
        }else
        if( node.symbol.getContent().equals( "Tp" ) ) {
            return;
        }else
        if( node.symbol.getContent().equals( "F" ) && node.children.get( 0 ).symbol.getContent().equals( "openpar" ) ) {
            cgen( tree, node.children.get(1) );
        }else {
            System.out.println("li\t$a0, " + node.children.get( 0 ).getValue());
        }
    }
}
