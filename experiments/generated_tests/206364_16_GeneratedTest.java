java
package org.apache.openjpa.lib.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("test"));
    }

    @Test
    public void testIsBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertFalse(StringUtil.isBlank("test"));
        assertFalse(StringUtil.isBlank("  test  "));
    }

    @Test
    public void testContains() {
        assertTrue(StringUtil.contains("test", 't'));
        assertFalse(StringUtil.contains("test", 'x'));
        assertFalse(StringUtil.contains(null, 't'));
    }

    @Test
    public void testEqualsIgnoreCase() {
        assertTrue(StringUtil.equalsIgnoreCase("test", "TEST"));
        assertTrue(StringUtil.equalsIgnoreCase("test", "test"));
        assertFalse(StringUtil.equalsIgnoreCase("test", "tes"));
        assertFalse(StringUtil.equalsIgnoreCase(null, "test"));
        assertFalse(StringUtil.equalsIgnoreCase("test", null));
        assertTrue(StringUtil.equalsIgnoreCase(null, null));
    }

    @Test
    public void testSplit() {
        String[] result = StringUtil.split("test,test,test", ",", 2);
        assertEquals(2, result.length);
        assertEquals("test", result[0]);
        assertEquals("test,test", result[1]);

        result = StringUtil.split("test", ",", 2);
        assertEquals(1, result.length);
        assertEquals("test", result[0]);

        result = StringUtil.split("test,test,test", ",", 0);
        assertEquals(3, result.length);
    }

    @Test
    public void testReplace() {
        assertEquals("teXXt", StringUtil.replace("test", "s", "XX"));
        assertEquals("test", StringUtil.replace("test", "s", "s"));
    }

    @Test
    public void testTrimToNull() {
        assertNull(StringUtil.trimToNull(null));
        assertNull(StringUtil.trimToNull(""));
        assertNull(StringUtil.trimToNull("   "));
        assertEquals("test", StringUtil.trimToNull("  test  "));
    }

    @Test
    public void testJoin() {
        Object[] values = {"test", "test2", "test3"};
        assertEquals("testnulltest2nulltest3", StringUtil.join(values, null));
        assertEquals("test,test2,test3", StringUtil.join(values, ","));
        values = new Object[] {"test"};
        assertEquals("test", StringUtil.join(values, ","));
        values = new Object[] {};
        assertEquals("", StringUtil.join(values, ","));
        assertNull(StringUtil.join(null, ","));
    }

    @Test
    public void testParse() {
        assertEquals(Integer.valueOf(10), StringUtil.parse("10", Integer.class));
        assertEquals(10, (int) StringUtil.parse("10", int.class));
        assertEquals(Character.valueOf('a'), StringUtil.parse("a", Character.class));
        assertEquals('a', (char) StringUtil.parse("a", char.class));
    }

    @Test
    public void testStripEnd() {
        assertEquals("abc", StringUtil.stripEnd("abc  ", null));
        assertEquals("  abc", StringUtil.stripEnd("  abcyx", "xyz"));
    }
}