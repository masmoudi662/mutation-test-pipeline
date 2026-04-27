java
package net.java.truevfs.kernel.spec;

import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class FsSchemeTest {

    @Test
    public void testCreateValidScheme() {
        FsScheme scheme = FsScheme.create("ftp");
        assertNotNull(scheme);
        assertEquals("ftp", scheme.name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidScheme() {
        FsScheme.create("invalid scheme");
    }

    @Test
    public void testToString() {
        FsScheme scheme = FsScheme.create("http");
        assertEquals("http", scheme.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        FsScheme scheme1 = FsScheme.create("https");
        FsScheme scheme2 = FsScheme.create("https");
        FsScheme scheme3 = FsScheme.create("file");

        assertEquals(scheme1, scheme2);
        assertEquals(scheme1.hashCode(), scheme2.hashCode());
        assertNotEquals(scheme1, scheme3);
        assertNotEquals(scheme1.hashCode(), scheme3.hashCode());
    }

    @Test
    public void testGetName() {
        FsScheme scheme = FsScheme.create("jar");
        assertEquals("jar", scheme.name());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullScheme() {
        FsScheme.create(null);
    }

    @Test
    public void testCreateUpperCaseScheme() {
        FsScheme scheme = FsScheme.create("HTTP");
        assertNotNull(scheme);
        assertEquals("http", scheme.name());
    }

    @Test
    public void testCreateSchemeWithDigits() {
        FsScheme scheme = FsScheme.create("s32");
        assertNotNull(scheme);
        assertEquals("s32", scheme.name());
    }

    @Test
    public void testCreateSchemeWithHyphen() {
        FsScheme scheme = FsScheme.create("ftp-tls");
        assertNotNull(scheme);
        assertEquals("ftp-tls", scheme.name());
    }
}