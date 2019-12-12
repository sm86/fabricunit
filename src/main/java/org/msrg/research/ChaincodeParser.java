package org.msrg.research;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.msrg.research.antlr4.golang.GoLexer;
import org.msrg.research.antlr4.golang.GoParser;
import org.msrg.research.antlr4.golang.GoParserBaseVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Parses the chaincode and identifies the safe or unsafe methods in it.

public class ChaincodeParser {
    private List<String> safeMethods;
    private List<String> unsafeMethods;

    public ChaincodeParser(String fileName) {
        CharStream charStream = null;
        try {
            charStream = CharStreams.fromFileName(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GoLexer goLexer = new GoLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(goLexer);
        commonTokenStream.fill();
        GoParser goParser = new GoParser(commonTokenStream);

        final List<String> unsafeMethods = new ArrayList<>();
        final List<String> safeMethods = new ArrayList<>();

        GoParser.SourceFileContext fileContext = goParser.sourceFile();
        GoParserBaseVisitor visitor = new GoParserBaseVisitor(){
            @Override
            public Object visitMethodDecl(GoParser.MethodDeclContext ctx){
                String methodName = ctx.IDENTIFIER().getText();
                if(ctx.block().getText().contains("DelState") || ctx.block().getText().contains("PutState")){
                    unsafeMethods.add(methodName);
                }else{
                    safeMethods.add(methodName);
                }
                return visitChildren(ctx);
            }
        };
        visitor.visit(fileContext);

        System.out.println("Safe methods are " +safeMethods.toString());
        System.out.println("Unsafe methods are " + unsafeMethods.toString());

    }

    public List<String> getSafeMethods() {
        return safeMethods;
    }

    public void setSafeMethods(List<String> safeMethods) {
        this.safeMethods = safeMethods;
    }

    public List<String> getUnsafeMethods() {
        return unsafeMethods;
    }

    public void setUnsafeMethods(List<String> unsafeMethods) {
        this.unsafeMethods = unsafeMethods;
    }
}
