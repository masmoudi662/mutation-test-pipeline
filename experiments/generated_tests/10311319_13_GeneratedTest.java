java
package net.openhft.lang.io.serialization.direct;

import org.junit.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import static net.openhft.lang.io.serialization.direct.DirectSerializationFilter.stopAtFirstIneligibleField;

public class DirectSerializationFilterTest {

    static class TestClass {
        int field1;
        long field2;
        String field3; // Ineligible
        double field4;
    }

    static class TestClass2 {
        transient int field1; // Ineligible
        int field2;
    }

    @Test
    public void testStopAtFirstIneligibleField_emptyList() {
        List<Field> fields = new ArrayList<>();
        List<Field> result = DirectSerializationFilter.stopAtFirstIneligibleField(fields);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testStopAtFirstIneligibleField_allEligible() throws NoSuchFieldException {
        List<Field> fields = Arrays.asList(
                TestClass.class.getDeclaredField("field1"),
                TestClass.class.getDeclaredField("field2")
        );
        List<Field> result = DirectSerializationFilter.stopAtFirstIneligibleField(fields);
        assertEquals(2, result.size());
    }

    @Test
    public void testStopAtFirstIneligibleField_oneIneligible() throws NoSuchFieldException {
        List<Field> fields = Arrays.asList(
                TestClass.class.getDeclaredField("field1"),
                TestClass.class.getDeclaredField("field2"),
                TestClass.class.getDeclaredField("field3"),
                TestClass.class.getDeclaredField("field4")
        );
        List<Field> result = DirectSerializationFilter.stopAtFirstIneligibleField(fields);
        assertEquals(2, result.size());
    }

     @Test
    public void testStopAtFirstIneligibleField_firstIneligible() throws NoSuchFieldException {
        List<Field> fields = Arrays.asList(
                TestClass2.class.getDeclaredField("field1"),
                TestClass2.class.getDeclaredField("field2")
        );
        List<Field> result = DirectSerializationFilter.stopAtFirstIneligibleField(fields);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testStopAtFirstIneligibleField_noEligible() throws NoSuchFieldException{
         List<Field> fields = Arrays.asList(
                 TestClass2.class.getDeclaredField("field1"));
        List<Field> result = DirectSerializationFilter.stopAtFirstIneligibleField(fields);
        assertTrue(result.isEmpty());

    }

    @Test
    public void testStopAtFirstIneligibleField_nullList() {
        List<Field> fields = null;
        try {
            DirectSerializationFilter.stopAtFirstIneligibleField(fields);
        } catch (NullPointerException e) {
        }
    }
}