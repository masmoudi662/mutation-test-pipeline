java
package org.red5.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConversionException;
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
            assertTrue(e.getMessage().startsWith("Unable to convert null to primitive value of "));
        }
    }

    @Test
    public void testConvertNullSourceNonPrimitiveTarget() {
        assertNull(ConversionUtils.convert(null, String.class));
    }

    @Test
    public void testConvertNaNFloat() {
        Float nan = Float.NaN;
        assertEquals(nan, ConversionUtils.convert(nan, Float.class));
    }

    @Test
    public void testConvertNaNDouble() {
        Double nan = Double.NaN;
        assertEquals(nan, ConversionUtils.convert(nan, Double.class));
    }

    @Test(expected = ConversionException.class)
    public void testConvertIConnectionMismatch() {
        ConversionUtils.convert(new MockConnection(), String.class);
    }

    @Test
    public void testConvertSameInstance() {
        String test = "test";
        assertEquals(test, ConversionUtils.convert(test, String.class));
    }

    @Test
    public void testConvertAssignableFrom() {
        List<String> list = new ArrayList<>();
        assertEquals(list, ConversionUtils.convert(list, List.class));
    }

    @Test
    public void testConvertString() {
        Integer num = 123;
        assertEquals("123", ConversionUtils.convert(num, String.class));
    }

    @Test
    public void testConvertPrimitive() {
        Integer num = 1;
        assertEquals(1, ConversionUtils.convert(num, int.class));
    }

    @Test
    public void testConvertWrapper() {
        int num = 1;
        assertEquals(1, ConversionUtils.convert(num, Integer.class));
    }

    @Test
    public void testConvertMap() {
        TestBean bean = new TestBean();
        bean.setName("test");
        bean.setValue(123);

        Map<?, ?> map = ConversionUtils.convertBeanToMap(bean);
        assertNotNull(map);
    }

    @Test
    public void testConvertMapToList() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("a", "1");
        map.put("b", "2");

        List<?> list = ConversionUtils.convertMapToList(map);
        assertNotNull(list);
    }

    @Test
    public void testConvertArrayToList() {
        String[] array = new String[] { "1", "2" };
        List<?> list = ConversionUtils.convertArrayToList(array);
        assertNotNull(list);
    }

    @Test
    public void testConvertArrayToSet() {
        String[] array = new String[] { "1", "1", "2" };
        Set<?> set = ConversionUtils.convertArrayToSet(array);
        assertNotNull(set);
    }

    @Test(expected = ConversionException.class)
    public void testConvertException() {
        ConversionUtils.convert(new Object(), Integer.class);
    }

    private static class MockConnection implements IConnection {

		@Override
		public Map<Object, Object> getAttributes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getAttribute(Object name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasAttribute(Object name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setAttribute(Object name, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object removeAttribute(Object name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeAllAttributes() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getReadBytes() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getWrittenBytes() {
			// TODO Auto-generated method stub
			return 0;
		}
    }

    public static class TestBean {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}