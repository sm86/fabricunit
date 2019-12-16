package org.msrg.research.util;

import org.junit.runners.model.FrameworkMethod;
import org.msrg.research.parser.ParserMain;

import java.util.Comparator;

public class MethodsSorter implements Comparator<FrameworkMethod> {

    ParserMain parser;

    public MethodsSorter(ParserMain parser) {
        this.parser = parser;
    }

    public int compare(FrameworkMethod m1, FrameworkMethod m2) {
        boolean i1Safe = parser.isSafe(m1.getMethod().getName());
        boolean i2Safe = parser.isSafe(m2.getMethod().getName());
        if (i1Safe && !i2Safe) {
            return -1;
        }
        else if (!i1Safe && i2Safe) {
            return 1;
        }
        return 0;
    }

}