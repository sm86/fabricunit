package org.msrg.research;

import org.msrg.research.parser.ChaincodeParser;
import org.msrg.research.parser.JavaClientParser;
import org.msrg.research.parser.JavaTestParser;

import java.util.List;
import java.util.Set;

public class App
{

    public static void main( String[] args )
    {
        String baseDir = "/Users/shashank/Documents/research/blockchain-application-using-fabric-java-sdk/";

        String chaincodeFileName = baseDir +"network_resources/chaincode/src/github.com/fabcar/fabcar.go";
        ChaincodeParser chaincodeParser = new ChaincodeParser(chaincodeFileName);
        final List<String> chaincodeSafe = chaincodeParser.getSafeMethods();

        String clientFileName = baseDir+ "java/src/main/java/org/example/chaincode/FabCarInvocation.java";
        JavaClientParser clientParser = new JavaClientParser(clientFileName, chaincodeSafe);
        final Set<String> clientUnsafe = clientParser.getUnsafeMethods();

        String testFileName = baseDir + "java/src/test/java/org.example/ClientTest.java";
        JavaTestParser testParser = new JavaTestParser(testFileName, clientUnsafe);
    }
}