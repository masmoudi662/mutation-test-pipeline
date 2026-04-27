java
package com.agorapulse.gru.cookie;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CookieTest {

    @Test
    public void testGetPath() {
        Cookie cookie = new Cookie.Builder()
            .name("testCookie")
            .value("testValue")
            .domain("example.com")
            .path("/testPath")
            .build();

        assertEquals("/testPath", cookie.getPath());
    }

    @Test
    public void testGetPathNull() {
        Cookie cookie = new Cookie.Builder()
            .name("testCookie")
            .value("testValue")
            .domain("example.com")
            .build();

        assertEquals("/", cookie.getPath());
    }
}