java
package sma.smython;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {

    private Parser parser;

    @Before
    public void setUp() {
        // You'll need to provide a Scanner implementation for testing
        // For example, a simple one that returns tokens from a list:
        Scanner scanner = new Scanner() {
            private List<String> tokens;
            private List<Object> values;
            private int index = 0;

            public Scanner init(List<String> tokens, List<Object> values) {
                this.tokens = tokens;
                this.values = values;
                this.index = 0;
                return this;
            }

            @Override
            public String next() {
                if (index < tokens.size()) {
                    return tokens.get(index++);
                } else {
                    return "END";
                }
            }

            @Override
            public Object value() {
                if (index <= values.size()) {
                    return values.get(index - 1);
                }
                return null;
            }

            @Override
            public int line() {
                return 1;
            }
        };
        parser = new Parser(scanner);
    }

    @Test
    public void testParseYieldExpr() {
        Scanner scanner = new Scanner() {
            private final List<String> tokens = Arrays.asList("yield", "None", "END");
            private final List<Object> values = Arrays.asList(null, null);
            private int index = 0;

            @Override
            public String next() {
                if (index < tokens.size()) {
                    return tokens.get(index++);
                } else {
                    return "END";
                }
            }

            @Override
            public Object value() {
                 if (index <= values.size()) {
                    return values.get(index - 1);
                }
                return null;
            }

            @Override
            public int line() {
                return 1;
            }
        };
        parser = new Parser(scanner);
        Expr expr = parser.parseYieldExpr();
        assertNotNull(expr);
        assertTrue(expr instanceof Expr.Yield);
    }

    @Test
    public void testParseFileInput_empty() {
        Scanner scanner = new Scanner() {
            private final List<String> tokens = Arrays.asList("END");

            private int index = 0;

            @Override
            public String next() {
                if (index < tokens.size()) {
                    return tokens.get(index++);
                } else {
                    return "END";
                }
            }

            @Override
            public Object value() {
                return null;
            }

            @Override
            public int line() {
                return 1;
            }
        };
        parser = new Parser(scanner);
        Suite suite = parser.parseFileInput();
        assertNotNull(suite);
    }

    @Test
    public void testParseFileInput_newline_endmarker() {
        Scanner scanner = new Scanner() {
            private final List<String> tokens = Arrays.asList("NEWLINE", "END");
            private int index = 0;
            @Override
            public String next() {
                if (index < tokens.size()) {
                    return tokens.get(index++);
                } else {
                    return "END";
                }
            }
            @Override
            public Object value() {
                return null;
            }

            @Override
            public int line() {
                return 1;
            }
        };
        parser = new Parser(scanner);
        Suite suite = parser.parseFileInput();
        assertNotNull(suite);
    }

    @Test
    public void testParseSimpleStmt_pass() {
        Scanner scanner = new Scanner() {
            private final List<String> tokens = Arrays.asList("pass", "NEWLINE", "END");
            private final List<Object> values = Arrays.asList(null, null);

            private int index = 0;

            @Override
            public String next() {
                if (index < tokens.size()) {
                    return tokens.get(index++);
                } else {
                    return "END";
                }
            }

            @Override
            public Object value() {
                if (index <= values.size()) {
                    return values.get(index - 1);
                }
                return null;
            }

            @Override
            public int line() {
                return 1;
            }
        };
        parser = new Parser(scanner);
        Suite suite = new Suite();
        parser.parseSimpleStmt(suite);
        assertEquals(1, suite.size());
        assertTrue(suite.get(0) instanceof Stmt.Pass);
    }
}