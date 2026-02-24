java
package org.apache.hc.client5.http.impl.cache;

import org.apache.hc.client5.http.cache.ResponseCacheControl;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TestResponseCachingPolicy {

    private ResponseCachingPolicy cachingPolicy;
    private ClassicHttpRequest request;
    private ClassicHttpResponse response;
    private ResponseCacheControl cacheControl;

    @BeforeEach
    public void setUp() {
        cachingPolicy = new ResponseCachingPolicy(true, false, false);
        request = new BasicClassicHttpRequest(Method.GET, "/test");
        response = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
        cacheControl = Mockito.mock(ResponseCacheControl.class);
    }

    @Test
    public void testIsResponseCacheable_HTTP10WithQueryString_NeverCache() {
        cachingPolicy = new ResponseCachingPolicy(true, true, false);
        request = new BasicClassicHttpRequest(Method.GET, "/test?param=value");
        request.setVersion(HttpVersion.HTTP_1_0);
        response = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
        assertTrue(cachingPolicy.isResponseCacheable(cacheControl, request, response));
    }

    @Test
    public void testIsResponseCacheable_HTTP11WithQueryString_ExplicitCacheable() {
        cachingPolicy = new ResponseCachingPolicy(true, false, true);
        request = new BasicClassicHttpRequest(Method.GET, "/test?param=value");
        response = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
        Mockito.when(cacheControl.getMaxAge()).thenReturn(3600);
        assertTrue(cachingPolicy.isResponseCacheable(cacheControl, request, response));
    }

    @Test
    public void testIsExplicitlyNonCacheable_NoStoreDirective() {
        Mockito.when(cacheControl.isNoStore()).thenReturn(true);
        assertTrue(cachingPolicy.isExplicitlyNonCacheable(cacheControl));
    }

    @Test
    public void testIsExplicitlyNonCacheable_PrivateDirective_SharedCache() {
        Mockito.when(cacheControl.isCachePrivate()).thenReturn(true);
        cachingPolicy = new ResponseCachingPolicy(true, false, false);
        assertTrue(cachingPolicy.isExplicitlyNonCacheable(cacheControl));
    }

    @Test
    public void testIsExplicitlyNonCacheable_PrivateDirective_NonSharedCache() {
        Mockito.when(cacheControl.isCachePrivate()).thenReturn(true);
        cachingPolicy = new ResponseCachingPolicy(false, false, false);
        assertFalse(cachingPolicy.isExplicitlyNonCacheable(cacheControl));
    }

    @Test
    public void testIsExplicitlyCacheable_PublicDirective() {
        Mockito.when(cacheControl.isPublic()).thenReturn(true);
        assertTrue(cachingPolicy.isExplicitlyCacheable(cacheControl, response));
    }

    @Test
    public void testIsHeuristicallyCacheable_KnownCacheableStatus_FreshnessLifetimePositive() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(60);
        cachingPolicy = new ResponseCachingPolicy(true, false, false);
        assertTrue(cachingPolicy.isHeuristicallyCacheable(cacheControl, HttpStatus.SC_OK, past, now));
    }

    @Test
    public void testIsHeuristicallyCacheable_UnknownStatusCode() {
        assertFalse(cachingPolicy.isHeuristicallyCacheable(cacheControl, 199, null, null));
    }

    @Test
    public void testResponseIsStillFresh_ValidDateAndFresh() {
        Instant now = Instant.now();
        Duration freshnessLifetime = Duration.ofSeconds(60);
        Instant responseDate = now.minusSeconds(30);
        assertTrue(cachingPolicy.responseIsStillFresh(responseDate, freshnessLifetime));
    }

    @Test
    public void testResponseIsStillFresh_InvalidDate() {
        assertFalse(cachingPolicy.responseIsStillFresh(null, Duration.ofSeconds(60)));
    }
}