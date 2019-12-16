package org.msrg.research;

import org.junit.Test;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.msrg.research.parser.ParserMain;
import org.msrg.research.util.MethodsSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FabricUnitRunner extends BlockJUnit4ClassRunner {

    /** Logger instance */
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private ParserMain parser;

    public FabricUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.firstBeforeExecution = true;
        this.firstNonSafeExecuted = false;
    }

    //control variable from @Before, that needs to run once for @Safe methods
    boolean firstBeforeExecution;

    //control variable for first non @Safe, that can use the same deploy instance from safe executions
    boolean firstNonSafeExecuted;


    //***************************************************************
    //
    //  Fixture rules
    //
    //***************************************************************
    /**
     * Verify if can reuse a @Before fixture or not <br>
     * If the method is @Safe AND is NOT a first execution, then can reuse <br>
     * Otherwise, do a new @Before execution
     * @param actualMethod object representing the actual test method
     * @return true if needs to run @Before, false if can skip
     */
    private boolean needsToRunBeforeFixture(FrameworkMethod actualMethod) {

        //Case 1: Is safe and not first execution
        if (isSafeAndNotFirstBeforeExecution(actualMethod)) {
            return true;
        }

        //Case 2: First non safe
        if (fistNonSafeWasExecuted(actualMethod)) {
            return true;
        }

        //Case 3: first method before execution
        if ( this.firstBeforeExecution ) {
            return true;
        }

        return false;
    }

    private boolean isSafeAndNotFirstBeforeExecution(FrameworkMethod actualMethod) {
        return (this.parser.isSafe(actualMethod.getName()) && this.firstBeforeExecution );
    }

    private boolean fistNonSafeWasExecuted(FrameworkMethod actualMethod) {
        return (!this.parser.isSafe(actualMethod.getName()) && this.firstNonSafeExecuted );
    }

    //***************************************************************
    //
    //  JUnit overrides
    //
    //***************************************************************

    /**
     * Returns the methods that run tests. Default implementation returns all
     * methods annotated with {@code @Test}
     */
    @Override
    protected List<FrameworkMethod> computeTestMethods() {

        parser = ParserMain.getParserMain();
        System.out.println("I am done my with parsing");
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(Test.class);

        List<FrameworkMethod> updatedMethods = new ArrayList<>();
        updatedMethods.addAll(methods);

        Collections.sort(updatedMethods, new MethodsSorter(parser));

        System.out.println("UPDATED METHODS ARE AS FOLLOWS ");
        for(int i=0;i<updatedMethods.size();i++){
            System.out.println(updatedMethods.get(i).getMethod().getName());
        }

        return updatedMethods;
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Object test;
        try {
            test = new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest( method );
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        Statement statement = methodInvoker(method, test);
        statement = possiblyExpectingExceptions(method, test, statement);

        //verify the need to run @Before fixture
        if ( this.needsToRunBeforeFixture(method) ) {
            statement = withBefores(method, test, statement);
            //if run once, check it
            this.firstBeforeExecution = false;
        }

        //mark first non safe execution when happens
        if ( !this.parser.isSafe(method.getName()) ) {
            this.firstNonSafeExecuted = true;
        }

        statement = withAfters(method, test, statement);
        return statement;
    }

    /**
     * Creates a test to be excecuted
     * @param method method annotated with @Test
     * @return Object that will be execute the test method
     * @throws Exception for some error
     */
    public Object createTest(FrameworkMethod method) throws Exception {
        //standard creation from JUnit
        Object obj = super.createTest();
        return obj;
    }


}
