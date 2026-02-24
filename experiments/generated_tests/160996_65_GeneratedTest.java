java
package org.apache.etch.util.core.nio;

import org.junit.Test;

import static org.junit.Assert.*;

public class HistoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_minNegative() {
        new History(-1, 10, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_limitLessThanMin() {
        new History(10, 5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_lenNegative() {
        new History(5, 10, -1);
    }

    @Test
    public void testUsed_positive() {
        History history = new History(5, 10, 5);
        assertTrue(history.used(1));
        assertEquals(1, history.getUsed());
    }

    @Test
    public void testUsed_negative() {
        History history = new History(5, 10, 5);
        history.used(5);
        assertTrue(history.used(-1));
        assertEquals(4, history.getUsed());
    }

    @Test(expected = IllegalStateException.class)
    public void testUsed_belowZero() {
        History history = new History(5, 10, 5);
        history.used(5);
        history.used(-6);
    }

    @Test
    public void testAlloc_positive() {
        History history = new History(5, 10, 5);
        history.alloc(1);
        assertEquals(1, history.getAlloc());
    }

    @Test
    public void testAlloc_negative() {
        History history = new History(5, 10, 5);
        history.alloc(5);
        history.alloc(-1);
        assertEquals(4, history.getAlloc());
    }

    @Test(expected = IllegalStateException.class)
    public void testAlloc_aboveLimit() {
        History history = new History(5, 10, 5);
        history.alloc(11);
    }

    @Test(expected = IllegalStateException.class)
    public void testAlloc_belowZero() {
        History history = new History(5, 10, 5);
        history.alloc(-6);
    }

    @Test
    public void testInit() {
        History history = new History(5, 10, 5);
        history.used(7);
        history.init();
        assertEquals(7, history.suggested());
    }
}