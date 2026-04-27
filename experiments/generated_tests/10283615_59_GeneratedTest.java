java
package eu.stratosphere.sopremo.type;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaToJsonMapperTest {

    @Test
    public void testMapNull() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        IJsonNode result = mapper.map(null);
        assertEquals(NullNode.getInstance(), result);
    }

    @Test
    public void testMapString() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        IJsonNode result = mapper.map("test");
        assertEquals(new TextNode("test"), result);
    }

    @Test
    public void testMapInteger() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        IJsonNode result = mapper.map(123);
        assertEquals(new IntNode(123), result);
    }

    @Test
    public void testMapDouble() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        IJsonNode result = mapper.map(123.45);
        assertEquals(new DoubleNode(123.45), result);
    }

    @Test
    public void testMapBoolean() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        IJsonNode result = mapper.map(true);
        assertEquals(BooleanNode.TRUE, result);
    }

    @Test
    public void testMapBigDecimal() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        BigDecimal bigDecimal = new BigDecimal("1234567890.0987654321");
        IJsonNode result = mapper.map(bigDecimal);
        assertEquals(new DecimalNode(bigDecimal), result);
    }

    @Test
    public void testMapBigInteger() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        BigInteger bigInteger = new BigInteger("12345678901234567890");
        IJsonNode result = mapper.map(bigInteger);
        assertEquals(new BigIntegerNode(bigInteger), result);
    }

    @Test
    public void testMapList() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        List<String> list = Arrays.asList("a", "b", "c");
        IJsonNode result = mapper.map(list);
        assertTrue(result instanceof ArrayNode);
        ArrayNode arrayNode = (ArrayNode) result;
        assertEquals(3, arrayNode.size());
        assertEquals(new TextNode("a"), arrayNode.get(0));
        assertEquals(new TextNode("b"), arrayNode.get(1));
        assertEquals(new TextNode("c"), arrayNode.get(2));
    }

    @Test
    public void testMapSet() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        Set<String> set = new HashSet<>(Arrays.asList("a", "b", "c"));
        IJsonNode result = mapper.map(set);
        assertTrue(result instanceof ArrayNode);
        ArrayNode arrayNode = (ArrayNode) result;
        assertEquals(3, arrayNode.size());
    }

    @Test
    public void testMapMap() {
        JavaToJsonMapper mapper = new JavaToJsonMapper();
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        IJsonNode result = mapper.map(map);
        assertTrue(result instanceof ObjectNode);
        ObjectNode objectNode = (ObjectNode) result;
        assertEquals(2, objectNode.size());
        assertEquals(new IntNode(1), objectNode.get("a"));
        assertEquals(new IntNode(2), objectNode.get("b"));
    }
}