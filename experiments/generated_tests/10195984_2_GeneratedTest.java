java
package net.java.sezpoz;

import java.lang.annotation.Annotation;
import org.junit.Test;
import static org.junit.Assert.*;

public class IndexTest {

    @Test
    public void testLoadWithContextClassLoader() {
        try {
            Index<Deprecated, Object> index = Index.load(Deprecated.class, Object.class);
            assertNotNull(index);
        } catch (IllegalArgumentException e) {
            fail("Unexpected IllegalArgumentException: " + e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAnnotationClassNull() {
        Index.load(null, Object.class);
    }

    @Test(expected = NullPointerException.class)
    public void testLoadInstanceTypeNull() {
        Index.load(Deprecated.class, null);
    }

    @Test
    public void testLoadEmptyIndex() {
        try {
            Index<TestAnnotation, String> index = Index.load(TestAnnotation.class, String.class);
            assertNotNull(index);
        } catch (IllegalArgumentException e) {
            fail("Unexpected IllegalArgumentException: " + e.getMessage());
        }
    }

    @Test
    public void testLoadNonExistentAnnotation() {
        try {
            Index<NonExistentAnnotation, Object> index = Index.load(NonExistentAnnotation.class, Object.class);
            assertNotNull(index);
        } catch (IllegalArgumentException e) {
            fail("Unexpected IllegalArgumentException: " + e.getMessage());
        }
    }

    private @interface TestAnnotation {}
    private @interface NonExistentAnnotation {}
}