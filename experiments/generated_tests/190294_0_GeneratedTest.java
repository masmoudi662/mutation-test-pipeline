java
package org.jbasics.csv;

import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVTableProviderTest {

    @Test
    public void testIsReadable_CSVTableClass() {
        CSVTableProvider provider = new CSVTableProvider();
        assertTrue(provider.isReadable(CSVTable.class, null, null, null));
    }

    @Test
    public void testIsReadable_SubclassOfCSVTable() {
        CSVTableProvider provider = new CSVTableProvider();
        assertTrue(provider.isReadable(MyCSVTable.class, null, null, null));
    }

    @Test
    public void testIsReadable_NotCSVTable() {
        CSVTableProvider provider = new CSVTableProvider();
        assertFalse(provider.isReadable(String.class, null, null, null));
    }

    @Test
    public void testIsWriteable_CSVTableClass() {
        CSVTableProvider provider = new CSVTableProvider();
        assertTrue(provider.isWriteable(CSVTable.class, null, null, null));
    }

    @Test
    public void testIsWriteable_SubclassOfCSVTable() {
        CSVTableProvider provider = new CSVTableProvider();
        assertTrue(provider.isWriteable(MyCSVTable.class, null, null, null));
    }

    @Test
    public void testIsWriteable_NotCSVTable() {
        CSVTableProvider provider = new CSVTableProvider();
        assertFalse(provider.isWriteable(String.class, null, null, null));
    }

    @Test
    public void testIsReadable_MediaTypeNull() {
        CSVTableProvider provider = new CSVTableProvider();
        assertTrue(provider.isReadable(CSVTable.class, null, null, null));
    }

    @Test
    public void testIsWriteable_MediaTypeNull() {
        CSVTableProvider provider = new CSVTableProvider();
        assertTrue(provider.isWriteable(CSVTable.class, null, null, null));
    }

    @Test
    public void testIsReadable_MediaTypeSpecific() {
        CSVTableProvider provider = new CSVTableProvider();
        MediaType mediaType = mock(MediaType.class);
        assertTrue(provider.isReadable(CSVTable.class, null, null, mediaType));
    }

    @Test
    public void testIsWriteable_MediaTypeSpecific() {
        CSVTableProvider provider = new CSVTableProvider();
        MediaType mediaType = mock(MediaType.class);
        assertTrue(provider.isWriteable(CSVTable.class, null, null, mediaType));
    }


    private static class MyCSVTable extends CSVTable {
    }
}