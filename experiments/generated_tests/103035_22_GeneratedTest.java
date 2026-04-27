java
package org.red5.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

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
            assertEquals("Unable to convert null to primitive value of int", e.getMessage());
        }
    }

    @Test
    public void testConvertNullSourceNonPrimitiveTarget() throws ConversionException {
        assertNull(ConversionUtils.convert(null, String.class));
    }

    @Test
    public void testConvertSameInstance() throws ConversionException {
        String source = "test";
        assertEquals(source, ConversionUtils.convert(source, String.class));
    }

    @Test
    public void testConvertAssignable() throws ConversionException {
        Integer source = 1;
        Number result = (Number) ConversionUtils.convert(source, Number.class);
        assertEquals(source, result);
    }

    @Test
    public void testConvertToString() throws ConversionException {
        Integer source = 1;
        String result = (String) ConversionUtils.convert(source, String.class);
        assertEquals("1", result);
    }

    @Test
    public void testConvertPrimitive() throws ConversionException {
        Integer source = 1;
        int result = (Integer) ConversionUtils.convert(source, Integer.class);
        assertEquals(1, result);
    }

    @Test
    public void testConvertWrappedPrimitive() throws ConversionException {
        Integer source = 1;
        Integer result = (Integer) ConversionUtils.convert(source, Integer.class);
        assertEquals(source, result);
    }

    @Test
    public void testConvertBeanToMap() throws ConversionException {
        TestBean source = new TestBean();
        source.setName("test");
        Map<?, ?> result = (Map<?, ?>) ConversionUtils.convert(source, Map.class);
        assertEquals("test", result.get("name"));
    }

    @Test
    public void testConvertMapToList() throws ConversionException {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) ConversionUtils.convert(TestBean.getMap(), List.class);
		assertEquals(TestBean.getMap().values().size(), list.size());
    }
    
    @Test
    public void testConnection() {
    	try {
    		ConversionUtils.convert(new TestConnection(), String.class);
    		fail("Expected exception");
		} catch (ConversionException e) {
			assertTrue(true);
		}
    }
    
    private static class TestBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
        public static Map<String, Object> getMap() {
        	Map<String, Object> map = new java.util.LinkedHashMap<String, Object>();
        	map.put("key1", 1);
        	map.put("key2", "val");
        	return map;
        }
    }
    
    private static class TestConnection implements IConnection {

		@Override
		public int getId() {
			return 0;
		}

		@Override
		public int getGeneration() {
			return 0;
		}

		@Override
		public boolean isClosed() {
			return false;
		}

		@Override
		public long getReadBytes() {
			return 0;
		}

		@Override
		public long getWrittenBytes() {
			return 0;
		}    	
    }
}