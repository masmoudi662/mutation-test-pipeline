java
package org.kie.soup.commons.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class ListSplitterTest {

    @Test
    public void testSplitEmpty() {
        String[] result = ListSplitter.split("");
        assertArrayEquals(new String[]{""}, result);
    }

    @Test
    public void testSplitSingleValue() {
        String[] result = ListSplitter.split("value");
        assertArrayEquals(new String[]{"value"}, result);
    }

    @Test
    public void testSplitMultipleValues() {
        String[] result = ListSplitter.split("'value1,value2,value3'");
        assertArrayEquals(new String[]{"value1,value2,value3"}, result);
    }

    @Test
    public void testSplitWithDifferentDelimiter() {
        String[] result = ListSplitter.split(";", false, "value1;value2;value3");
        assertArrayEquals(new String[]{"value1", "value2", "value3"}, result);
    }

    @Test
    public void testSplitWithQuotesAndDifferentDelimiter() {
        String[] result = ListSplitter.split(";", true, "'value1;value2;value3'");
        assertArrayEquals(new String[]{"value1", "value2", "value3"}, result);
    }

    @Test
    public void testSplitWithQuotesAndDelimiter() {
        String[] result = ListSplitter.split("'", true, "'value1','value2','value3'");
        assertArrayEquals(new String[]{"value1", "value2", "value3"}, result);
    }

    @Test
    public void testSplitWithNullValue() {
        String[] result = ListSplitter.split(null);
        assertArrayEquals(new String[]{null}, result);
    }

    @Test
    public void testSplitWithEmptyValues() {
        String[] result = ListSplitter.split("''");
        assertArrayEquals(new String[]{""}, result);
    }

    @Test
    public void testSplitWithCommas() {
        String[] result = ListSplitter.split("a,b,c");
        assertArrayEquals(new String[]{"a,b,c"}, result);
    }
}