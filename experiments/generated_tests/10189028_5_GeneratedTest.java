java
package com.alibaba.doris.common.serialize;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JavaObjectSerializerTest {

    @Test
    public void testDeserialize_nullInput() {
        JavaObjectSerializer serializer = new JavaObjectSerializer();
        Object result = serializer.deserialize(null, null);
        assertNull(result);
    }

    @Test
    public void testDeserialize_validObject() throws Exception {
        JavaObjectSerializer serializer = new JavaObjectSerializer();
        String testString = "test string";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testString);
        byte[] bytes = baos.toByteArray();

        Object result = serializer.deserialize(bytes, null);
        assertEquals(testString, result);
    }

    @Test(expected = RuntimeException.class)
    public void testDeserialize_invalidData() {
        JavaObjectSerializer serializer = new JavaObjectSerializer();
        byte[] invalidData = new byte[]{1, 2, 3};
        serializer.deserialize(invalidData, null);
    }
}