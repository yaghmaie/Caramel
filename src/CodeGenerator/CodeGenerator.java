package CodeGenerator;

import Parser.ParseTree;
import Parser.ParseTreeNode;

/**
 * Created by pejman on 6/25/14.
 */
public class CodeGenerator {
    public void cgen( ParseTree tree, ParseTreeNode node ) {
        if( node.parent == null ) {
            System.out.println("\t.text\n.globl main");
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
            System.out.println("addiu\n$sp, $sp, 4");
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
            System.out.println("add\t$a0, $t1, $a0");
            System.out.println("addiu\n$sp, $sp, 4");
            cgen( tree, node.children.get( 2 ) );
        }
    }
}
