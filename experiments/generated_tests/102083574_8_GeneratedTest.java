java
package org.netbeans.html.json.impl;

import org.junit.Test;
import static org.junit.Assert.*;

public class JSONTest {

    @Test
    public void testNumberValueWithString() {
        assertEquals(Double.valueOf("123.45"), JSON.numberValue("123.45"));
    }

    @Test
    public void testNumberValueWithBooleanTrue() {
        assertEquals(1, JSON.numberValue(true));
    }

    @Test
    public void testNumberValueWithBooleanFalse() {
        assertEquals(0, JSON.numberValue(false));
    }

    @Test
    public void testNumberValueWithInteger() {
        assertEquals(10, JSON.numberValue(10));
    }

    @Test
    public void testNumberValueWithDouble() {
        assertEquals(10.5, JSON.numberValue(10.5));
    }

    @Test
    public void testNumberValueWithInvalidString() {
        assertEquals(Double.NaN, JSON.numberValue("abc"));
    }

    @Test
    public void testNumberValueWithNull() {
        assertNull(JSON.numberValue(null));
    }
}