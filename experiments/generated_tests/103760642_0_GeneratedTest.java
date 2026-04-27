java
package com.intendia.qualifier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ExtensionTest {

    @Test
    public void testExtensionGetKey() {
        NamedExtension<String> extension = new NamedExtension<>("testKey");
        assertEquals("testKey", extension.getKey());
    }

    @Test
    public void testExtensionHashCode() {
        NamedExtension<String> extension1 = new NamedExtension<>("key1");
        NamedExtension<String> extension2 = new NamedExtension<>("key1");
        assertEquals(extension1.hashCode(), extension2.hashCode());

        NamedExtension<String> extension3 = new NamedExtension<>("key2");
        assertNotEquals(extension1.hashCode(), extension3.hashCode());
    }

    @Test
    public void testExtensionEquals() {
        NamedExtension<String> extension1 = new NamedExtension<>("key1");
        NamedExtension<String> extension2 = new NamedExtension<>("key1");
        assertEquals(extension1, extension2);

        NamedExtension<String> extension3 = new NamedExtension<>("key2");
        assertNotEquals(extension1, extension3);

        assertNotEquals(extension1, null);
        assertNotEquals(extension1, new Object());
    }

    @Test
    public void testExtensionToString() {
        NamedExtension<String> extension = new NamedExtension<>("testKey");
        assertNotNull(extension.toString());
    }

    private static class NamedExtension<T> implements Extension<T> {
        private final String key;

        public NamedExtension(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            NamedExtension<?> that = (NamedExtension<?>) obj;
            return Objects.equals(key, that.key);
        }

        @Override
        public String toString() {
            return "NamedExtension{key='" + key + "'}";
        }
    }
}