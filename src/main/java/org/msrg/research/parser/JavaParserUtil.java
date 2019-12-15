package org.msrg.research.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.msrg.research.antlr4.java9.Java9Lexer;
import org.msrg.research.antlr4.java9.Java9Parser;

import java.io.IOException;

public class JavaParserUtil {

    public static ParseTree getParseTree(String fileName){
        CharStream charStream = null;
        try {
            charStream = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Java9Lexer java9Lexer = new Java9Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(java9Lexer);
        commonTokenStream.fill();
        Java9Parser java9Parser = new Java9Parser(commonTokenStream);

        ParseTree tree = java9Parser.compilationUnit();
        return tree;
    }
}
