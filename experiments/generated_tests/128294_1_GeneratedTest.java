java
package org.mobicents.media.server.impl.naming;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class NumericRangeTest {

    public NumericRangeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testHasMore() {
        NumericRange instance = new NumericRange(1, 5);
        boolean expResult = true;
        boolean result = instance.hasMore();
        assertEquals(expResult, result);
    }

    @Test
    public void testHasMoreAtEnd() {
        NumericRange instance = new NumericRange(5, 5);
        boolean expResult = false;
        boolean result = instance.hasMore();
        assertEquals(expResult, result);
    }

    @Test
    public void testHasMoreAtStart() {
        NumericRange instance = new NumericRange(1, 1);
        boolean expResult = false;
        boolean result = instance.hasMore();
        assertEquals(expResult, result);
    }
}