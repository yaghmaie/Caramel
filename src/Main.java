import CodeGenerator.CodeGenerator;
import Fundamentals.Token;
import LexicalAnalyser.DFA;
import LexicalAnalyser.LexicalAnalyser;
import LexicalAnalyser.LexicalAnalyserException;
import LexicalAnalyser.Regex2DFA;
import Parser.*;

import java.util.ArrayList;

/**
 * Created by pejman on 3/17/14.
 */


public class Main {

    public static void main( String[] args ) {

        String code = "((3*2)+((12*5)+96*1127+458))*(43+2*1+45*12+123)";
        //String code = "3*7";
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
            LexicalAnalyser lexa = new LexicalAnalyser();
            ArrayList<Token> tokenList = null;
            tokenList = lexa.getTokenList( code, dfa );

            /*for( Token tk : tokenList ) {
                tk.dump();
            }*/

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
            new CodeGenerator().cgen( parseTree, parseTree.root );
        } catch ( Exception e ) {
            System.err.println( e.getMessage() );
        }

    }
}
