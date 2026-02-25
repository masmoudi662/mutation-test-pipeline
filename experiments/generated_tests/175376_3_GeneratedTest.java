java
package sma.smython;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class ParserTest {

    @Test
    public void testParseYieldExprEmpty() {
        Parser parser = new Parser("yield");
        Expr expr = parser.parseYieldExpr();
        assertNotNull(expr);
        assertTrue(expr instanceof Expr.Yield);
        Expr.Yield yieldExpr = (Expr.Yield) expr;
        assertTrue(yieldExpr.values.isEmpty());
    }

    @Test
    public void testParseYieldExprSingle() {
        Parser parser = new Parser("yield 1");
        Expr expr = parser.parseYieldExpr();
        assertNotNull(expr);
        assertTrue(expr instanceof Expr.Yield);
        Expr.Yield yieldExpr = (Expr.Yield) expr;
        assertEquals(1, yieldExpr.values.size());
    }

    @Test
    public void testParseYieldExprMultiple() {
        Parser parser = new Parser("yield 1, 2, 3");
        Expr expr = parser.parseYieldExpr();
        assertNotNull(expr);
        assertTrue(expr instanceof Expr.Yield);
        Expr.Yield yieldExpr = (Expr.Yield) expr;
        assertEquals(3, yieldExpr.values.size());
    }
    
    @Test
    public void testParseYieldExprWithParens() {
        Parser parser = new Parser("yield (1, 2, 3)");
        Expr expr = parser.parseYieldExpr();
        assertNotNull(expr);
        assertTrue(expr instanceof Expr.Yield);
        Expr.Yield yieldExpr = (Expr.Yield) expr;
        assertEquals(1, yieldExpr.values.size());
    }
}