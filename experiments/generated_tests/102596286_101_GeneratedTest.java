java
package com.emc.ocopea.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HashCodeUtilTest {

    @Test
    public void testHashBooleanTrue() {
        assertEquals(32, HashCodeUtil.hash(31, true));
    }

    @Test
    public void testHashBooleanFalse() {
        assertEquals(31, HashCodeUtil.hash(31, false));
    }

    @Test
    public void testHashInt() {
        assertEquals(63, HashCodeUtil.hash(31, 32));
    }

    @Test
    public void testHashLong() {
        assertEquals(63, HashCodeUtil.hash(31, 32L));
    }

    @Test
    public void testHashFloat() {
        assertEquals(32, HashCodeUtil.hash(31, 1.0f));
    }

    @Test
    public void testHashDouble() {
        assertEquals(32, HashCodeUtil.hash(31, 1.0d));
    }

    @Test
    public void testHashObject() {
        assertEquals(63, HashCodeUtil.hash(31, "32"));
    }

    @Test
    public void testHashObjectNull() {
        assertEquals(31, HashCodeUtil.hash(31, null));
    }

    @Test
    public void testHashArray() {
        int[] array = {1, 2, 3};
        assertEquals(1281, HashCodeUtil.hash(31, array));
    }

}