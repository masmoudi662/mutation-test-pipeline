java
package com.asymmetrik.nifi.processors.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilTest {

    @Test
    void testGetJsonPathsForTemplatingEmpty() {
        Set<Map.Entry<String, JsonElement>> root = new HashSet<>();
        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetJsonPathsForTemplatingSimple() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "John");
        jsonObject.addProperty("age", 30);

        Set<Map.Entry<String, JsonElement>> root = jsonObject.entrySet();
        Map<String, String> expected = new HashMap<>();
        expected.put("$.name", "John");
        expected.put("$.age", "30");

        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);
        assertEquals(expected, result);
    }

    @Test
    void testGetJsonPathsForTemplatingNested() {
        JsonObject jsonObject = new JsonObject();
        JsonObject address = new JsonObject();
        address.addProperty("city", "New York");
        address.addProperty("zip", "10001");
        jsonObject.add("address", address);
        jsonObject.addProperty("name", "John");

        Set<Map.Entry<String, JsonElement>> root = jsonObject.entrySet();
        Map<String, String> expected = new HashMap<>();
        expected.put("$.address.city", "New York");
        expected.put("$.address.zip", "10001");
        expected.put("$.name", "John");

        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);
        assertEquals(expected, result);
    }

    @Test
    void testGetJsonPathsForTemplatingArray() {
        JsonObject jsonObject = new JsonObject();
        com.google.gson.JsonArray hobbies = new com.google.gson.JsonArray();
        hobbies.add("reading");
        hobbies.add("hiking");
        jsonObject.add("hobbies", hobbies);

        Set<Map.Entry<String, JsonElement>> root = jsonObject.entrySet();
        Map<String, String> expected = new HashMap<>();
        expected.put("$.hobbies[0]", "reading");
        expected.put("$.hobbies[1]", "hiking");

        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);
       assertEquals(2, result.size());
       assertTrue(result.containsKey("$.hobbies[0]"));
       assertTrue(result.containsKey("$.hobbies[1]"));
       assertEquals("reading", result.get("$.hobbies[0]"));
       assertEquals("hiking", result.get("$.hobbies[1]"));
    }

    @Test
    void testGetJsonPathsForTemplatingNullValue() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("nullable", JsonNull.INSTANCE);

        Set<Map.Entry<String, JsonElement>> root = jsonObject.entrySet();
        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);

        assertTrue(result.containsKey("$.nullable"));
        assertEquals("", result.get("$.nullable"));
    }

    @Test
    void testGetJsonPathsForTemplatingBooleanValue() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("active", true);

        Set<Map.Entry<String, JsonElement>> root = jsonObject.entrySet();
        Map<String, String> expected = new HashMap<>();
        expected.put("$.active", "true");

        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);
        assertEquals(expected, result);
    }

    @Test
    void testGetJsonPathsForTemplatingNumberValue() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", 123.45);

        Set<Map.Entry<String, JsonElement>> root = jsonObject.entrySet();
        Map<String, String> expected = new HashMap<>();
        expected.put("$.value", "123.45");

        Map<String, String> result = JsonUtil.getJsonPathsForTemplating(root);
        assertEquals(expected, result);
    }
}