java
package org.hildan.livedoc.core.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultsTest {

    @Test
    public void defaultValueFor_nullType() {
        assertNull(Defaults.defaultValueFor(null));
    }

    @Test
    public void defaultValueFor_boolean() {
        assertEquals(false, Defaults.defaultValueFor(boolean.class));
    }

    @Test
    public void defaultValueFor_byte() {
        assertEquals((byte) 0, Defaults.defaultValueFor(byte.class));
    }

    @Test
    public void defaultValueFor_char() {
        assertEquals('\u0000', Defaults.defaultValueFor(char.class));
    }

    @Test
    public void defaultValueFor_short() {
        assertEquals((short) 0, Defaults.defaultValueFor(short.class));
    }

    @Test
    public void defaultValueFor_int() {
        assertEquals(0, Defaults.defaultValueFor(int.class));
    }

    @Test
    public void defaultValueFor_long() {
        assertEquals(0L, Defaults.defaultValueFor(long.class));
    }

    @Test
    public void defaultValueFor_float() {
        assertEquals(0.0f, Defaults.defaultValueFor(float.class));
    }

    @Test
    public void defaultValueFor_double() {
        assertEquals(0.0d, Defaults.defaultValueFor(double.class));
    }

    @Test
    public void defaultValueFor_otherClass() {
        assertNull(Defaults.defaultValueFor(String.class));
    }
}