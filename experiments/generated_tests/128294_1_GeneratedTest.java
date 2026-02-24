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
        NumericRange instance = new NumericRange("[1..3]");
        assertTrue(instance.hasMore());
        instance.next();
        assertTrue(instance.hasMore());
        instance.next();
        assertTrue(instance.hasMore());
        instance.next();
        assertFalse(instance.hasMore());
    }

    @Test
    public void testNext() {
        NumericRange instance = new NumericRange("[1..3]");
        assertEquals("1", instance.next());
        assertEquals("2", instance.next());
        assertEquals("3", instance.next());
    }

    @Test
    public void testSingleValueRange() {
        NumericRange instance = new NumericRange("[1..1]");
        assertTrue(instance.hasMore());
        assertEquals("1", instance.next());
        assertFalse(instance.hasMore());
    }

    @Test
    public void testZeroBasedRange() {
        NumericRange instance = new NumericRange("[0..2]");
        assertTrue(instance.hasMore());
        assertEquals("0", instance.next());
        assertEquals("1", instance.next());
        assertEquals("2", instance.next());
        assertFalse(instance.hasMore());
    }

    @Test
    public void testLargeRange() {
        NumericRange instance = new NumericRange("[1000..1002]");
        assertEquals("1000", instance.next());
        assertEquals("1001", instance.next());
        assertEquals("1002", instance.next());
    }

    @Test
    public void testConstructor() {
        NumericRange instance = new NumericRange("[5..7]");
        assertTrue(instance.hasMore());
        assertEquals("5", instance.next());
        assertEquals("6", instance.next());
        assertEquals("7", instance.next());
        assertFalse(instance.hasMore());
    }
}