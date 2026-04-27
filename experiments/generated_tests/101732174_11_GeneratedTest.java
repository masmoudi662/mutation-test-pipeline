java
package com.linecorp.centraldogma.internal.jsonpatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonPatchTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void apply_emptyPatch_returnsOriginalNode() throws IOException {
        JsonNode original = mapper.readTree("{\"a\": 1, \"b\": \"foo\"}");
        JsonPatch patch = new JsonPatch(new ArrayList<>());
        JsonNode result = patch.apply(original);
        assertThat(result).isEqualTo(original);
    }

    @Test
    void apply_addOperation_modifiesNode() throws IOException {
        JsonNode original = mapper.readTree("{\"a\": 1}");
        JsonPatchOperation addOp = new JsonPatchOperation(
                JsonPatchOperationType.ADD, "/b", new TextNode("foo"));
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(addOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.get("b").asText()).isEqualTo("foo");
    }

    @Test
    void apply_removeOperation_modifiesNode() throws IOException {
        JsonNode original = mapper.readTree("{\"a\": 1, \"b\": \"foo\"}");
        JsonPatchOperation removeOp = new JsonPatchOperation(JsonPatchOperationType.REMOVE, "/b", null);
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(removeOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.has("b")).isFalse();
    }

    @Test
    void apply_replaceOperation_modifiesNode() throws IOException {
        JsonNode original = mapper.readTree("{\"a\": 1, \"b\": \"foo\"}");
        JsonPatchOperation replaceOp = new JsonPatchOperation(
                JsonPatchOperationType.REPLACE, "/b", new TextNode("bar"));
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(replaceOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.get("b").asText()).isEqualTo("bar");
    }

    @Test
    void apply_multipleOperations_modifiesNode() throws IOException {
        JsonNode original = mapper.readTree("{\"a\": 1}");
        JsonPatchOperation addOp = new JsonPatchOperation(
                JsonPatchOperationType.ADD, "/b", new TextNode("foo"));
        JsonPatchOperation replaceOp = new JsonPatchOperation(
                JsonPatchOperationType.REPLACE, "/a", new IntNode(2));
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(addOp);
        ops.add(replaceOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.get("a").asInt()).isEqualTo(2);
        assertThat(result.get("b").asText()).isEqualTo("foo");
    }

    @Test
    void apply_nestedObjectAdd() throws IOException {
        JsonNode original = mapper.readTree("{}");
        JsonPatchOperation addOp = new JsonPatchOperation(JsonPatchOperationType.ADD, "/a/b", new TextNode("foo"));
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(addOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.get("a").get("b").asText()).isEqualTo("foo");
    }

    @Test
    void apply_arrayAdd() throws IOException {
        JsonNode original = mapper.readTree("[]");
        JsonPatchOperation addOp = new JsonPatchOperation(JsonPatchOperationType.ADD, "/0", new TextNode("foo"));
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(addOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.get(0).asText()).isEqualTo("foo");
    }

    @Test
    void apply_arrayReplace() throws IOException {
        JsonNode original = mapper.readTree("[\"a\"]");
        JsonPatchOperation replaceOp = new JsonPatchOperation(JsonPatchOperationType.REPLACE, "/0", new TextNode("b"));
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(replaceOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.get(0).asText()).isEqualTo("b");
    }

    @Test
    void apply_arrayRemove() throws IOException {
        JsonNode original = mapper.readTree("[\"a\", \"b\"]");
        JsonPatchOperation removeOp = new JsonPatchOperation(JsonPatchOperationType.REMOVE, "/0", null);
        List<JsonPatchOperation> ops = new ArrayList<>();
        ops.add(removeOp);
        JsonPatch patch = new JsonPatch(ops);
        JsonNode result = patch.apply(original);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).asText()).isEqualTo("b");
    }
}