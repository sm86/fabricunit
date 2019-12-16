package org.msrg.research.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ParserMain {

    private HashMap<String, Boolean> testMethods;
    private String baseDir = "/Users/shashank/Documents/research/blockchain-application-using-fabric-java-sdk/";
    private static ParserMain parserMain = null;

    private ParserMain() {
        completeParse();
        System.out.println("PARSER PROCESS HAS ENDED");
    }

    public static ParserMain getParserMain(){
        if(parserMain==null)
            parserMain = new ParserMain();
        return parserMain;
    }

    public HashMap<String, Boolean> getTestMethods() {
        return testMethods;
    }

    public void setTestMethods(HashMap<String, Boolean> testMethods) {
        this.testMethods = testMethods;
    }

    private void completeParse(){

        String chaincodeFileName = baseDir +"network_resources/chaincode/src/github.com/fabcar/fabcar.go";
        ChaincodeParser chaincodeParser = new ChaincodeParser(chaincodeFileName);
        final List<String> chaincodeSafe = chaincodeParser.getSafeMethods();

        String clientFileName = baseDir+ "java/src/main/java/org/example/chaincode/FabCarInvocation.java";
        JavaClientParser clientParser = new JavaClientParser(clientFileName, chaincodeSafe);
        final Set<String> clientUnsafe = clientParser.getUnsafeMethods();

        String testFileName = baseDir + "java/src/test/java/org.example/ClientTest.java";
        JavaTestParser testParser = new JavaTestParser(testFileName, clientUnsafe);
        setTestMethods(testParser.getTestUnsafeMethods());
    }

    public boolean isSafe(String method){
        return !(testMethods.get(method));
    }

}
