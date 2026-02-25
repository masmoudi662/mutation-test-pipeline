java
package org.apache.hc.client5.http.impl.cache;

import org.apache.hc.client5.http.cache.HeaderConstants;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TestResponseCachingPolicy {

    private ResponseCachingPolicy cachingPolicy;
    private HttpResponse response;

    @BeforeEach
    public void setUp() {
        cachingPolicy = new ResponseCachingPolicy();
        response = Mockito.mock(HttpResponse.class);
    }

    @Test
    public void testNonGetOrHeadMethodsAreNotCacheable() {
        assertFalse(cachingPolicy.isResponseCacheable("POST", response));
        assertFalse(cachingPolicy.isResponseCacheable("PUT", response));
        assertFalse(cachingPolicy.isResponseCacheable("DELETE", response));
    }

    @Test
    public void testUncacheableStatusCodesAreNotCacheable() {
        when(response.getCode()).thenReturn(HttpStatus.SC_FORBIDDEN);
        assertFalse(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }

    @Test
    public void testUnknownStatusCodesAreNotCacheable() {
        when(response.getCode()).thenReturn(600);
        assertFalse(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }

   @Test
    public void testContentLengthExceedingMaxObjectSizeIsNotCacheable() {
        cachingPolicy.maxObjectSizeBytes = 100;
        Header contentLengthHeader = Mockito.mock(Header.class);
        when(contentLengthHeader.getValue()).thenReturn("200");
        when(response.getFirstHeader("Content-Length")).thenReturn(contentLengthHeader);
        when(response.getCode()).thenReturn(HttpStatus.SC_OK);
        assertFalse(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }

    @Test
    public void testMultipleAgeHeadersIsNotCacheable() {
        when(response.countHeaders(HeaderConstants.AGE)).thenReturn(2);
        when(response.getCode()).thenReturn(HttpStatus.SC_OK);
        assertFalse(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }

    @Test
    public void testMissingDateHeaderIsNotCacheable() {
        when(response.countHeaders("Date")).thenReturn(0);
        when(response.getCode()).thenReturn(HttpStatus.SC_OK);
        assertFalse(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }

    @Test
    public void testVaryStarIsNotCacheable() {
        Header varyHeader = Mockito.mock(Header.class);
        when(varyHeader.getValue()).thenReturn("*");
        when(response.getHeaders("Vary")).thenReturn(new Header[]{varyHeader});
        when(response.getCode()).thenReturn(HttpStatus.SC_OK);
        assertFalse(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }

    @Test
    public void testExplicitlyNonCacheableResponseIsNotCacheable() {
        HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
        Header cacheControlHeader = Mockito.mock(Header.class);
        when(cacheControlHeader.getValue()).thenReturn("no-store");
        when(mockResponse.getHeaders("Cache-Control")).thenReturn(new Header[]{cacheControlHeader});
        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        ResponseCachingPolicy policy = new ResponseCachingPolicy();
        assertFalse(policy.isResponseCacheable("GET", mockResponse));
    }

    @Test
    public void testExplicitlyCacheableResponseIsCacheable() {
        HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
        Header cacheControlHeader = Mockito.mock(Header.class);
        when(cacheControlHeader.getValue()).thenReturn("public, max-age=3600");
        when(mockResponse.getHeaders("Cache-Control")).thenReturn(new Header[]{cacheControlHeader});
        when(mockResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        ResponseCachingPolicy policy = new ResponseCachingPolicy();
        assertTrue(policy.isResponseCacheable("GET", mockResponse));
    }

    @Test
    public void testCacheableResponseIsCacheable() {
        when(response.getCode()).thenReturn(HttpStatus.SC_OK);
        when(response.getFirstHeader("Date")).thenReturn(Mockito.mock(Header.class));
        when(response.countHeaders("Date")).thenReturn(1);
        assertTrue(cachingPolicy.isResponseCacheable(HeaderConstants.GET_METHOD, response));
    }
}