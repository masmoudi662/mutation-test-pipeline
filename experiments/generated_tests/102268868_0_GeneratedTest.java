java
package com.steinbacher.storj_hoststats_app.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class VersionTest {

    @Test
    public void testToString() {
        Version version = new Version(1, 2, 3);
        assertEquals("1.2.3", version.toString());
    }

    @Test
    public void testToStringWithZeroes() {
        Version version = new Version(0, 0, 0);
        assertEquals("0.0.0", version.toString());
    }

    @Test
    public void testToStringWithLargeNumbers() {
        Version version = new Version(100, 200, 300);
        assertEquals("100.200.300", version.toString());
    }

    @Test
    public void testMajorVersion() {
        Version version = new Version(5, 0, 0);
        assertEquals("5.0.0", version.toString());
    }

    @Test
    public void testMinorVersion() {
        Version version = new Version(0, 7, 0);
        assertEquals("0.7.0", version.toString());
    }

    @Test
    public void testBuildVersion() {
        Version version = new Version(0, 0, 9);
        assertEquals("0.0.9", version.toString());
    }

    @Test
    public void testAnotherCombination() {
        Version version = new Version(2, 5, 8);
        assertEquals("2.5.8", version.toString());
    }
}