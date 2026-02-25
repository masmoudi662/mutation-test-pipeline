java
package org.apache.hc.core5.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TimeoutValueExceptionTest {

    @Test
    public void testFromMilliseconds() {
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(1000L, 500L);
        assertNotNull(exception);
        assertEquals("Request terminated after 1000 milliseconds; actual timeout 500 milliseconds", exception.getMessage());
    }

    @Test
    public void testFromMillisecondsZeroTimeout() {
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(0L, 0L);
        assertNotNull(exception);
        assertEquals("Request terminated after 0 milliseconds; actual timeout 0 milliseconds", exception.getMessage());
    }

    @Test
    public void testFromMillisecondsLargeTimeout() {
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(Long.MAX_VALUE, Long.MAX_VALUE);
        assertNotNull(exception);
        assertEquals("Request terminated after 9223372036854775807 milliseconds; actual timeout 9223372036854775807 milliseconds", exception.getMessage());
    }

    @Test
    public void testFromMillisecondsNegativeTimeout() {
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(-1000L, -500L);
        assertNotNull(exception);
        assertEquals("Request terminated after 0 milliseconds; actual timeout 0 milliseconds", exception.getMessage());
    }

    @Test
    public void testGetTimeout1() {
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(1000L, 500L);
        assertEquals(Timeout.ofMilliseconds(1000L), exception.getTimeout());
    }

    @Test
    public void testGetTimeout2() {
        TimeoutValueException exception = new TimeoutValueException(Timeout.ofMilliseconds(2000), Timeout.ofMilliseconds(1000));
        assertEquals(Timeout.ofMilliseconds(2000), exception.getTimeout());
    }

    @Test
    public void testGetActualTimeout1() {
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(1000L, 500L);
        assertEquals(Timeout.ofMilliseconds(500L), exception.getActualTimeout());
    }

    @Test
    public void testGetActualTimeout2() {
        TimeoutValueException exception = new TimeoutValueException(Timeout.ofMilliseconds(2000), Timeout.ofMilliseconds(1000));
        assertEquals(Timeout.ofMilliseconds(1000), exception.getActualTimeout());
    }
}