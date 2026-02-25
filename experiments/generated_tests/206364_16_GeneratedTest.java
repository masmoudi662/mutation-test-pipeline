java
package org.apache.openjpa.lib.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testReplaceWithSameFromAndTo() {
        String str = "test string";
        String from = "test";
        String to = "test";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals(str, result);
    }

    @Test
    public void testReplaceSimple() {
        String str = "test string";
        String from = "test";
        String to = "best";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals("best string", result);
    }

    @Test
    public void testReplaceMultipleOccurrences() {
        String str = "test test test string";
        String from = "test";
        String to = "best";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals("best best best string", result);
    }

    @Test
    public void testReplaceEmptyFrom() {
        String str = "test string";
        String from = "";
        String to = "best";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals(str, result);
    }

     @Test
    public void testReplaceEmptyTo() {
        String str = "test string";
        String from = "test";
        String to = "";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals(" string", result);
    }

    @Test
    public void testReplaceFromLongerThanTo() {
        String str = "testing string";
        String from = "testing";
        String to = "test";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals("test string", result);
    }

    @Test
    public void testReplaceToLongerThanFrom() {
        String str = "test string";
        String from = "test";
        String to = "testing";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals("testing string", result);
    }

    @Test
    public void testReplaceNoOccurrence() {
        String str = "test string";
        String from = "wrong";
        String to = "best";
        String result = StringUtil.replace(str, from, to);
        Assert.assertEquals("test string", result);
    }

    @Test
    public void testReplaceNullString() {
        String str = null;
        String from = "test";
        String to = "best";
        String result = StringUtil.replace(str, from, to);
        Assert.assertNull(result);
    }
}