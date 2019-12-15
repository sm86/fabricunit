package org.msrg.research.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.msrg.research.antlr4.java9.Java9BaseVisitor;
import org.msrg.research.antlr4.java9.Java9Parser;

import java.util.HashMap;
import java.util.Set;

public class JavaTestParser {

    private static final String Client = "client";

    private String fileName;
    private Set<String> clientUnsafeMethods;
    private HashMap<String, Boolean> testUnsafeMethods;

    public JavaTestParser(String fileName, Set<String> clientUnsafe) {
        this.fileName = fileName;
        this.clientUnsafeMethods = clientUnsafe;
        parse();
        System.out.println("Test unsafe Methods are "+ testUnsafeMethods.toString());
    }

    public HashMap<String, Boolean> getTestUnsafeMethods() {
        return testUnsafeMethods;
    }

    private void setTestUnsafeMethods(HashMap<String, Boolean> testUnsafeMethods) {
        this.testUnsafeMethods = testUnsafeMethods;
    }

    private void parse(){
        ParseTree tree = JavaParserUtil.getParseTree(fileName);
        final HashMap<String, Boolean> testUnsafeMethods = new HashMap<String, Boolean>();

        Java9BaseVisitor visitor = new Java9BaseVisitor(){
            boolean isTest = false;
            String func = null;

            @Override
            public Object visitMethodDeclaration(Java9Parser.MethodDeclarationContext ctx) {
                func = ctx.methodHeader().methodDeclarator().identifier().getText();
                if("@Test".equals(ctx.methodModifier().get(0).getText())){
                    isTest = true;
                    testUnsafeMethods.put(func,false);
                }else{
                    isTest = false;
                }
                return visitChildren(ctx);
            }

            @Override
            public Object visitMethodInvocation(Java9Parser.MethodInvocationContext ctx) {
                if(isTest && ctx.getText().contains("client.")) {
                    //                 System.out.println(ctx.getText());
                }
                return visitChildren(ctx);
            }
            @Override
            public Object visitMethodInvocation_lfno_primary(Java9Parser.MethodInvocation_lfno_primaryContext ctx) {
                if(isTest && ctx.typeName().getText().equals(Client)){
                    String funcName  = ctx.identifier().getText();
                    if(isUnsafeMethod(funcName))
                        testUnsafeMethods.put(func,true);
                }
                return visitChildren(ctx);
            }

        };
        visitor.visit(tree);
        this.testUnsafeMethods = testUnsafeMethods;
    }

    private boolean isUnsafeMethod(String funcName){
        if(clientUnsafeMethods.contains(funcName))
            return true;
        return false;
    }

}
