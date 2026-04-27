java
package com.heatonresearch.aifh.general.fns.link;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.heatonresearch.aifh.AIFHError;

public class TestInverseSquaredLinkFunction {

    @Test
    public void testEvaluateValid() {
        InverseSquaredLinkFunction fn = new InverseSquaredLinkFunction();
        double[] x = {2.0};
        double result = fn.evaluate(x);
        assertEquals(-0.25, result, 1e-6);
    }

    @Test(expected = AIFHError.class)
    public void testEvaluateInvalidLength() {
        InverseSquaredLinkFunction fn = new InverseSquaredLinkFunction();
        double[] x = {2.0, 3.0};
        fn.evaluate(x);
    }

    @Test
    public void testEvaluatePositive() {
        InverseSquaredLinkFunction fn = new InverseSquaredLinkFunction();
        double[] x = {5.0};
        double result = fn.evaluate(x);
        assertEquals(-0.04, result, 1e-6);
    }

    @Test
    public void testEvaluateNegative() {
        InverseSquaredLinkFunction fn = new InverseSquaredLinkFunction();
        double[] x = {-2.0};
        double result = fn.evaluate(x);
        assertEquals(-0.25, result, 1e-6);
    }

    @Test
    public void testEvaluateSmallValue() {
        InverseSquaredLinkFunction fn = new InverseSquaredLinkFunction();
        double[] x = {0.5};
        double result = fn.evaluate(x);
        assertEquals(-4.0, result, 1e-6);
    }

    @Test
    public void testEvaluateLargeValue() {
        InverseSquaredLinkFunction fn = new InverseSquaredLinkFunction();
        double[] x = {10.0};
        double result = fn.evaluate(x);
        assertEquals(-0.01, result, 1e-6);
    }
}