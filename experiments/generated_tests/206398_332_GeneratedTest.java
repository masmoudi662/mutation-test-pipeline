java
package org.apache.hc.core5.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TimeoutValueExceptionTest {

    @Test
    public void testFromMilliseconds() {
        long deadline = 1000;
        long actual = 500;
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(deadline, actual);
        assertNotNull(exception);
        assertEquals(deadline, exception.getDeadline().getDuration().toMillis());
        assertEquals(actual, exception.getActual().getDuration().toMillis());
    }

    @Test
    public void testFromMillisecondsWithNegativeValues() {
        long deadline = -1000;
        long actual = -500;
        TimeoutValueException exception = TimeoutValueException.fromMilliseconds(deadline, actual);
        assertNotNull(exception);
        assertEquals(0, exception.getDeadline().getDuration().toMillis());
        assertEquals(0, exception.getActual().getDuration().toMillis());
    }

    @Test
    public void testConstructor() {
        Timeout deadline = Timeout.ofMilliseconds(2000);
        Timeout actual = Timeout.ofMilliseconds(1500);
        TimeoutValueException exception = new TimeoutValueException(deadline, actual);
        assertNotNull(exception);
        assertEquals(deadline, exception.getDeadline());
        assertEquals(actual, exception.getActual());
    }

    @Test
    public void testGetters() {
        Timeout deadline = Timeout.ofMilliseconds(3000);
        Timeout actual = Timeout.ofMilliseconds(2500);
        TimeoutValueException exception = new TimeoutValueException(deadline, actual);
        assertEquals(deadline, exception.getDeadline());
        assertEquals(actual, exception.getActual());
    }
}