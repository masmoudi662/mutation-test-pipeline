java
package com.github.rickardoberg.cqrs.memory;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class FileEventStorageTest {

  private FileEventStorage fileEventStorage;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
    fileEventStorage = new FileEventStorage("test", mapper);
  }

  @Test
  void deserialize_validJson_returnsInteractionContext() throws IOException {
    String jsonLine = "{\"aggregateId\":\"agg1\",\"version\":1,\"event\":{\"name\":\"TestEvent\"}}";
    InteractionContext context = fileEventStorage.deserialize(jsonLine);

    assertNotNull(context);
    assertEquals("agg1", context.aggregateId);
    assertEquals(1, context.version);
    assertNotNull(context.event);
  }

  @Test
  void deserialize_invalidJson_throwsIOException() {
    String invalidJsonLine = "{\"aggregateId\":\"agg1\", \"version\":1,";

    assertThrows(IOException.class, () -> fileEventStorage.deserialize(invalidJsonLine));
  }

  @Test
  void deserialize_emptyJson_throwsIOException() {
    String emptyJsonLine = "";

    assertThrows(IOException.class, () -> fileEventStorage.deserialize(emptyJsonLine));
  }

  @Test
  void deserialize_nullJson_throwsIOException() {
    assertThrows(IOException.class, () -> fileEventStorage.deserialize(null));
  }

  @Test
  void dummyTest() {
      assertTrue(true);
  }
}