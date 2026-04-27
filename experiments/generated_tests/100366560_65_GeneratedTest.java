java
package io.beanmother.core.converter.std;

import com.google.common.reflect.TypeToken;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObjectToStringConverterTest {

    private ObjectToStringConverter converter = new ObjectToStringConverter();

    @Test
    public void testConvertString() {
        String source = "test";
        String result = (String) converter.convert(source, TypeToken.of(String.class));
        assertEquals(source, result);
    }

    @Test
    public void testConvertInteger() {
        Integer source = 123;
        String result = (String) converter.convert(source, TypeToken.of(String.class));
        assertEquals("123", result);
    }

    @Test
    public void testConvertDouble() {
        Double source = 123.45;
        String result = (String) converter.convert(source, TypeToken.of(String.class));
        assertEquals("123.45", result);
    }

    @Test
    public void testConvertBoolean() {
        Boolean source = true;
        String result = (String) converter.convert(source, TypeToken.of(String.class));
        assertEquals("true", result);
    }

    @Test
    public void testConvertObject() {
        Object source = new Object();
        String result = (String) converter.convert(source, TypeToken.of(String.class));
        assertEquals(source.toString(), result);
    }

    @Test
    public void testConvertNull() {
        String result = (String) converter.convert(null, TypeToken.of(String.class));
        assertEquals("null", result);
    }
}