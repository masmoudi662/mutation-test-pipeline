java
package io.hypersistence.utils.hibernate.type.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.type.SerializationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ObjectMapperJsonSerializerTest {

    private ObjectMapperJsonSerializer objectMapperJsonSerializer;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        objectMapperJsonSerializer = new ObjectMapperJsonSerializer();
        objectMapperJsonSerializer.objectMapperWrapper = objectMapperWrapper;
    }

    @Test
    public void testCloneSerializable() {
        String original = "test";
        String cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneNonSerializable() {
        Object original = new Object();
        when(objectMapperWrapper.toBytes(original)).thenReturn(new byte[]{});
        when(objectMapperWrapper.fromBytes(new byte[]{}, Object.class)).thenReturn(original);

        Object cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneCollectionSerializable() {
        List<String> original = new ArrayList<>();
        original.add("test");
        List<String> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneCollectionNonSerializable() {
        List<Object> original = new ArrayList<>();
        original.add(new Object());
        when(objectMapperWrapper.toBytes(original)).thenReturn(new byte[]{});
        when(objectMapperWrapper.fromBytes(new byte[]{}, TypeFactory.defaultInstance().constructParametricType(original.getClass(), Object.class))).thenReturn(original);

        List<Object> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneMapSerializable() {
        Map<String, String> original = new HashMap<>();
        original.put("key", "value");
        Map<String, String> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneMapNonSerializable() {
        Map<Object, Object> original = new HashMap<>();
        original.put(new Object(), new Object());
        when(objectMapperWrapper.toBytes(original)).thenReturn(new byte[]{});
        when(objectMapperWrapper.fromBytes(new byte[]{}, TypeFactory.defaultInstance().constructParametricType(original.getClass(), Object.class, Object.class))).thenReturn(original);

        Map<Object, Object> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneNullValueInCollection(){
        List<String> original = new ArrayList<>();
        original.add(null);
        original.add("test");
        List<String> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneNullValueInMap(){
        Map<String, String> original = new HashMap<>();
        original.put("key", null);
        original.put("key2", "value");
        Map<String, String> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }

    @Test
    public void testCloneNullKeyInMap(){
        Map<String, String> original = new HashMap<>();
        original.put(null, "value");
        original.put("key2", "value2");
        Map<String, String> cloned = objectMapperJsonSerializer.clone(original);
        assertEquals(original, cloned);
    }
}