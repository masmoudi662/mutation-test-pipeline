java
package org.red5.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.red5.server.api.IConnection;

public class ConversionUtilsTest {

    @Test
    public void testConvertNullTarget() {
        try {
            ConversionUtils.convert("test", null);
            fail("Expected ConversionException");
        } catch (ConversionException e) {
            assertEquals("Unable to perform conversion, target was null", e.getMessage());
        }
    }

    @Test
    public void testConvertNullSourcePrimitiveTarget() {
        try {
            ConversionUtils.convert(null, int.class);
            fail("Expected ConversionException");
        } catch (ConversionException e) {
            assertTrue(e.getMessage().contains("Unable to convert null to primitive value of"));
        }
    }

    @Test
    public void testConvertNullSource() throws ConversionException {
        assertNull(ConversionUtils.convert(null, String.class));
    }

    @Test
    public void testConvertSameType() throws ConversionException {
        String source = "test";
        assertEquals(source, ConversionUtils.convert(source, String.class));
    }

    @Test
    public void testConvertStringToInteger() throws ConversionException {
        Integer result = (Integer) ConversionUtils.convert("123", Integer.class);
        assertEquals(123, result.intValue());
    }

    @Test
    public void testConvertIntegerToString() throws ConversionException {
        String result = (String) ConversionUtils.convert(123, String.class);
        assertEquals("123", result);
    }

    @Test
    public void testConvertMapToList() throws ConversionException {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);

        List<?> result = (List<?>) ConversionUtils.convert(map, List.class);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0));
        assertEquals(2, result.get(1));
    }

    @Test
    public void testConvertArrayToList() throws ConversionException {
        String[] array = {"one", "two"};
        List<?> result = (List<?>) ConversionUtils.convert(array, List.class);
        assertEquals(2, result.size());
        assertEquals("one", result.get(0));
        assertEquals("two", result.get(1));
    }
    
    @Test
    public void testConvertArrayToSet() throws ConversionException {
    	String[] array = {"one", "two", "one"};
    	Set<?> result = (Set<?>) ConversionUtils.convert(array, Set.class);
    	assertEquals(2, result.size());
    	assertTrue(result.contains("one"));
    	assertTrue(result.contains("two"));
    }

    @Test(expected = ConversionException.class)
    public void testConvertUnsupportedConversion() throws ConversionException {
        ConversionUtils.convert(new Object(), Integer.class);
    }
}