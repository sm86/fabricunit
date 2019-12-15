package org.msrg.research.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.msrg.research.antlr4.java9.Java9BaseVisitor;
import org.msrg.research.antlr4.java9.Java9Parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaClientParser {
    private Set<String> unsafeMethods;
    private String clientFileName;
    private List<String> chaincodeSafe;

    public JavaClientParser(String fileName, List<String> chaincodeSafe) {
        this.clientFileName = fileName;
        this.chaincodeSafe = chaincodeSafe;
        parseClient();
        System.out.println("Client safe methods are "+ getUnsafeMethods().toString());
    }

    private void setUnsafeMethods(Set<String> unsafeMethods) {
        this.unsafeMethods = unsafeMethods;
    }

    public Set<String> getUnsafeMethods() {
        return unsafeMethods;
    }

    private void parseClient(){

        ParseTree tree = JavaParserUtil.getParseTree(clientFileName);
        final Set<String> unsafeTestMethods= new HashSet<>();

        Java9BaseVisitor visitor = new Java9BaseVisitor(){
            String methodName = null;
            @Override
            public Object visitMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {
                methodName = ctx.methodHeader().methodDeclarator().identifier().getText();
                return visitChildren(ctx);
            }
            @Override
            public Object visitMethodInvocation(Java9Parser.MethodInvocationContext ctx) {
                if(ctx.identifier().getText().contains("setFcn")){
                    String funcName = ctx.argumentList().getText().replace("\"", "");
                    if(!isSafe(funcName))
                        unsafeTestMethods.add(methodName);
                }
                return visitChildren(ctx);
            }
        };
        visitor.visit(tree);
        this.unsafeMethods = unsafeTestMethods;
    }

    private boolean isSafe(String func){
        if(chaincodeSafe.contains(func))
            return true;
        return false;
    }
}