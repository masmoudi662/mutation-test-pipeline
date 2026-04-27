java
package com.microsoft.java.debug.core.adapter.formatter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ObjectFormatterTest {

  @Test
  public void testToStringWithNullObject() {
    ObjectFormatter formatter = new ObjectFormatter();
    try {
      formatter.toString(null, Collections.emptyMap());
    } catch (NullPointerException e) {
      assertEquals("Cannot invoke \"com.sun.jdi.ObjectReference.referenceType()\" because \"obj\" is null", e.getMessage());
    }
  }

  @Test
  public void testToStringWithMockObject() {
    ObjectFormatter formatter = new ObjectFormatter();
    ObjectReference mockObject = mock(ObjectReference.class);
    Type mockType = mock(Type.class);

    when(mockObject.referenceType()).thenReturn(mockType);
    when(mockType.name()).thenReturn("TestObject");
    when(mockObject.uniqueID()).thenReturn(12345L);

    String result = formatter.toString(mockObject, Collections.emptyMap());
    assertEquals("TestObject@12345", result);
  }

  @Test
  public void testToStringWithPrefixOption() {
    ObjectFormatter formatter = new ObjectFormatter();
    ObjectReference mockObject = mock(ObjectReference.class);
    Type mockType = mock(Type.class);

    when(mockObject.referenceType()).thenReturn(mockType);
    when(mockType.name()).thenReturn("TestObject");
    when(mockObject.uniqueID()).thenReturn(12345L);

    Map<String, Object> options = new HashMap<>();
    options.put("prefix", "CustomPrefix");

    String result = formatter.toString(mockObject, options);
    assertEquals("TestObject@12345", result);
  }
}