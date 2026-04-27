java
package org.fcrepo.lambdora.common.utils;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UriUtilsTest {

    @Test
    public void testGetParentRoot() {
        final URI rootUri = URI.create("http://example.com/");
        assertNull(UriUtils.getParent(rootUri));
    }

    @Test
    public void testGetParentOneLevel() {
        final URI uri = URI.create("http://example.com/object");
        final URI expected = URI.create("http://example.com/");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentTwoLevels() {
        final URI uri = URI.create("http://example.com/object/child");
        final URI expected = URI.create("http://example.com/object");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentTwoLevelsTrailingSlash() {
        final URI uri = URI.create("http://example.com/object/child/");
        final URI expected = URI.create("http://example.com/object");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentThreeLevels() {
        final URI uri = URI.create("http://example.com/object/child/grandchild");
        final URI expected = URI.create("http://example.com/object/child");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentWithPort() {
        final URI uri = URI.create("http://example.com:8080/object");
        final URI expected = URI.create("http://example.com:8080/");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentWithQueryParam() {
        final URI uri = URI.create("http://example.com/object?param=value");
        final URI expected = URI.create("http://example.com/");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentWithPathOnly() {
        final URI uri = URI.create("/object/child");
        final URI expected = URI.create("/object");
        assertEquals(expected, UriUtils.getParent(uri));
    }

    @Test
    public void testGetParentWithRelativePath() {
        final URI uri = URI.create("object/child");
        final URI expected = URI.create("object");
        assertEquals(expected, UriUtils.getParent(uri));
    }
}