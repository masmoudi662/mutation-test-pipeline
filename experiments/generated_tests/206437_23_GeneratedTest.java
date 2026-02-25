java
package org.apache.directory.server.xdbm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.IOException;

public class EmptyIndexCursorTest {

    private EmptyIndexCursor<String> cursor;

    @BeforeEach
    public void setUp() throws Exception {
        cursor = new EmptyIndexCursor<>();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (IOException e) {
            }
        }
    }

    @Test
    public void testClose() throws IOException {
        cursor.close();
        assertTrue(cursor.isClosed());
    }

    @Test
    public void testAvailable() throws Exception {
        assertFalse(cursor.available());
    }

    @Test
    public void testNext() throws Exception {
        assertFalse(cursor.next());
    }

    @Test
    public void testPrevious() throws Exception {
        assertFalse(cursor.previous());
    }

    @Test
    public void testGet() throws Exception {
        assertNull(cursor.get());
    }

    @Test
    public void testGetKey() throws Exception {
        assertNull(cursor.key());
    }
}