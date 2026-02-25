java
package org.apache.etch.util.core.nio;

import org.junit.Test;

import static org.junit.Assert.fail;

public class HistoryTest {

    @Test
    public void testAlloc_valid() {
        History history = new History();
        history.limit(10);
        history.alloc(5);
        history.alloc(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlloc_zero() {
        History history = new History();
        history.alloc(0);
    }

    @Test
    public void testAlloc_limit() {
        History history = new History();
        history.limit(10);
        try {
            history.alloc(11);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    @Test
    public void testAlloc_negative() {
        History history = new History();
        history.limit(10);
        history.alloc(5);
        try {
            history.alloc(-6);
        } catch (Exception e) {

        }
    }

    @Test(expected = IllegalStateException.class)
    public void testAlloc_overflow() {
        History history = new History();
        history.limit(Integer.MAX_VALUE);
        history.alloc(Integer.MAX_VALUE);
        history.alloc(1);
    }

    @Test
    public void testAlloc_max_int_limit() {
        History history = new History();
        history.limit(Integer.MAX_VALUE);
        history.alloc(Integer.MAX_VALUE - 1);
    }

}