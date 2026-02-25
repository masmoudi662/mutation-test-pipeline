java
package org.jbasics.csv;

import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CSVTableProviderTest {

    @Test
    void isReadable_CSVTable_ReturnsTrue() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(CSVTable.class, null, null, null);
        assertTrue(result);
    }

    @Test
    void isReadable_SubclassOfCSVTable_ReturnsTrue() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(MyCSVTable.class, null, null, null);
        assertTrue(result);
    }

    @Test
    void isReadable_NonCSVTable_ReturnsFalse() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(String.class, null, null, null);
        assertFalse(result);
    }

    @Test
    void isReadable_NullType_ReturnsFalse() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(null, null, null, null);
        assertFalse(result);
    }

    @Test
    void isReadable_WithMediaType_CSVTable_ReturnsTrue() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(CSVTable.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        assertTrue(result);
    }

    @Test
    void isReadable_WithAnnotations_CSVTable_ReturnsTrue() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(CSVTable.class, null, new Annotation[0], null);
        assertTrue(result);
    }

    @Test
    void isReadable_WithGenericType_CSVTable_ReturnsTrue() {
        CSVTableProvider provider = new CSVTableProvider();
        boolean result = provider.isReadable(CSVTable.class, String.class, null, null);
        assertTrue(result);
    }

    private static class MyCSVTable extends CSVTable {

        public MyCSVTable() {
            super();
        }
    }
}