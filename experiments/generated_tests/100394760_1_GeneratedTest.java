java
package io.scalecube.config;

import static org.junit.jupiter.api.Assertions.*;

import io.scalecube.config.utils.ThrowableUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class ObjectPropertyFieldTest {

  @Test
  void testApplyValueParser() throws Exception {
    TestClass instance = new TestClass();
    Field field = TestClass.class.getDeclaredField("stringField");
    field.setAccessible(true);

    ObjectPropertyField objectPropertyField =
        new ObjectPropertyField(
            field, s -> s.toUpperCase(), String.class, String.class, "stringField");
    objectPropertyField.applyValueParser(instance, "test");

    assertEquals("TEST", instance.stringField);
  }

  @Test
  void testApplyValueParserInteger() throws Exception {
    TestClass instance = new TestClass();
    Field field = TestClass.class.getDeclaredField("integerField");
    field.setAccessible(true);

    ObjectPropertyField objectPropertyField =
        new ObjectPropertyField(
            field, Integer::parseInt, Integer.class, Integer.class, "integerField");
    objectPropertyField.applyValueParser(instance, "123");

    assertEquals(123, instance.integerField);
  }

  @Test
  void testApplyValueParserBoolean() throws Exception {
    TestClass instance = new TestClass();
    Field field = TestClass.class.getDeclaredField("booleanField");
    field.setAccessible(true);

    ObjectPropertyField objectPropertyField =
        new ObjectPropertyField(
            field, Boolean::parseBoolean, Boolean.class, Boolean.class, "booleanField");
    objectPropertyField.applyValueParser(instance, "true");

    assertTrue(instance.booleanField);
  }

  @Test
  void testApplyValueParserIllegalAccessException() throws Exception {
    TestClass instance = new TestClass();
    Field field = TestClass.class.getDeclaredField("privateStringField");
    field.setAccessible(true);

    ObjectPropertyField objectPropertyField =
        new ObjectPropertyField(
            field, s -> s.toUpperCase(), String.class, String.class, "privateStringField");

    assertThrows(
        ThrowableUtil.PropagateException.class,
        () -> objectPropertyField.applyValueParser(instance, "test"));
  }

  static class TestClass {
    String stringField;
    Integer integerField;
    Boolean booleanField;
    private String privateStringField;

    public String getPrivateStringField() {
      return privateStringField;
    }
  }
}