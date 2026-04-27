java
package parseBoolean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseBooleanOriginalTest {

    @Test
    void testProcess_simpleTrue() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("1");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_simpleFalse() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("0");
        assertEquals(0, parser.process());
    }

    @Test
    void testProcess_notTrue() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("!1");
        assertEquals(0, parser.process());
    }

    @Test
    void testProcess_notFalse() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("!0");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_orTrue() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("1+0");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_orFalse() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("0+0");
        parser._input = "0+0";
        assertEquals(0, parser.process());
    }

    @Test
    void testProcess_andTrue() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("1*1");
        parser._input = "1*1";
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_andFalse() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("1*0");
        parser._input = "1*0";
        assertEquals(0, parser.process());
    }

    @Test
    void testProcess_complexExpression() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("(1+0)*1");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_complexExpression2() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("1*(0+1)");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_whitespace() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal(" 1 + 0 ");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_nestedParentheses() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("((1))");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_doubleNot() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("!!1");
        assertEquals(1, parser.process());
    }

    @Test
    void testProcess_doubleNot2() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("!!0");
        assertEquals(0, parser.process());
    }

    @Test
    void testProcess_withNotAndOr() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("!1+0");
        assertEquals(0, parser.process());
    }

    @Test
    void testProcess_withNotAndOr2() {
        ParseBooleanOriginal parser = new ParseBooleanOriginal("0+!1");
        assertEquals(0, parser.process());
    }
}