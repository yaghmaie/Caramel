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

import Fundamentals.Token;
import LexicalAnalyser.DFA;
import LexicalAnalyser.LexicalAnalyser;
import LexicalAnalyser.Regex2DFA;
import Parser.CFG2ParseTable;
import Parser.ParseTable;
import Parser.ParseTree;
import Parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pejman on 3/17/14.
 */


public class Main {

    public static void main( String[] args ) {

        String code = "((3*2)+((12*5)+96*1127+458))*(43+2*1+45*12+123)";
        //String code = "4*7";
        //String code = "(3+1)(5+1)";


        DFA dfa = new Regex2DFA().rules2DFA(
                "letter=%%`a`+`b`+`c`+`d`+`e`+`f`+`g`+`h`+`i`+`j`+`k`+`l`+`m`+`n`+`o`+`p`+`q`+`r`+`s`+`t`+`u`+`v`+`w`+`x`+`y`+`z`%%\n" +
                        "digit=%%`1`+`2`+`3`+`4`+`5`+`6`+`7`+`8`+`9`+`0`%%\n" +
                        "id=%%~letter~.(~letter~+~digit~)*%%\n" +
                        "number=%%~digit~.~digit~*%%\n" +
                        //"decimal=%%~number~.((`.`.~number~)+`\0`)%%\n" +
                        "ws=%%(` `+`\\t`+`\\n`)*%%\n" +
                        //"eq=%%`=`%%\n" +
                        "plus=%%`+`%%\n" +
                        //"minus=%%`-`%%\n" +
                        "times=%%`*`%%\n" +
                        "openpar=%%`(`%%\n" +
                        "closepar=%%`)`%%\n"
                        //"div=%%`/`%%\n" +
                        //"term=%%`;`%%\n" +
                        //"if=%%`i`.`f`%%\n" +
                        //"gt=%%`>`%%\n"
        );

        try {
            ArrayList<Token> tokenList = new LexicalAnalyser().getTokenList( code, dfa );

            for( Token tk : tokenList ) {
                tk.dump();
            }

            ParseTable parseTable = new CFG2ParseTable().rules2ParseTable(
                    "E=%%~T~~Ep~%%\n" +
                            //"E=%%~id~`eq`~E~%%" +
                            "Ep=%%`plus`~T~~Ep~%%\n" +
                            "Ep=%%`\0`%%\n" +
                            "T=%%~F~~Tp~%%\n" +
                            "Tp=%%`times`~F~~Tp~%%\n" +
                            "Tp=%%`\0`%%\n" +
                            "F=%%`openpar`~E~`closepar`%%\n" +
                            "F=%%`number`%%" );
            /*ParseTable parseTable = new CFG2ParseTable().rules2ParseTable(
                    "E=%%~T~~X~%%\n" +
                            "X=%%`plus`~E~%%\n" +
                            "X=%%`\0`%%\n" +
                            "Y=%%`times`~T~%%\n" +
                            "Y=%%`\0`%%\n" +
                            "T=%%`openpar`~E~`closepar`%%\n" +
                            "T=%%`number`~Y~%%"
            );
*/
            //parseTable.dump();

            ParseTree parseTree = new Parser().getParseTree( tokenList, parseTable );
            //parseTree.dump();
            //new CodeGenerator().cgen( parseTree, parseTree.root );

        } catch ( Exception e ) {
            System.err.println( e.getMessage() );
        }

    }
}
