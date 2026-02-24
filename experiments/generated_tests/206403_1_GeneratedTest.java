java
package org.apache.jackrabbit.spi2dav;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConnectionOptionsTest {

    @Test
    public void testDefaultConnectionOptions() {
        ConnectionOptions options = ConnectionOptions.DEFAULT;
        assertFalse(options.isUseSystemPropertes());
        assertEquals(ConnectionOptions.MAX_CONNECTIONS_DEFAULT, options.getMaxConnections());
        assertFalse(options.isAllowSelfSignedCertificates());
        assertFalse(options.isDisableHostnameVerification());
        assertNull(options.getProxyHost());
        assertEquals(-1, options.getProxyPort());
        assertNull(options.getProxyProtocol());
        assertNull(options.getProxyUsername());
        assertNull(options.getProxyPassword());
        assertEquals(-1, options.getConnectionTimeoutMs());
        assertEquals(-1, options.getRequestTimeoutMs());
        assertEquals(-1, options.getSocketTimeoutMs());
    }

    @Test
    public void testBuilder() {
        ConnectionOptions options = ConnectionOptions.builder()
                .useSystemProperties(true)
                .maxConnections(10)
                .allowSelfSignedCertificates(true)
                .disableHostnameVerification(true)
                .proxyHost("localhost")
                .proxyPort(8080)
                .proxyProtocol("http")
                .proxyUsername("user")
                .proxyPassword("password")
                .connectionTimeoutMs(1000)
                .requestTimeoutMs(2000)
                .socketTimeoutMs(3000)
                .build();

        assertTrue(options.isUseSystemPropertes());
        assertEquals(10, options.getMaxConnections());
        assertTrue(options.isAllowSelfSignedCertificates());
        assertTrue(options.isDisableHostnameVerification());
        assertEquals("localhost", options.getProxyHost());
        assertEquals(8080, options.getProxyPort());
        assertEquals("http", options.getProxyProtocol());
        assertEquals("user", options.getProxyUsername());
        assertEquals("password", options.getProxyPassword());
        assertEquals(1000, options.getConnectionTimeoutMs());
        assertEquals(2000, options.getRequestTimeoutMs());
        assertEquals(3000, options.getSocketTimeoutMs());
    }

    @Test
    public void testToServiceFactoryParameters() {
        ConnectionOptions options = ConnectionOptions.builder()
                .useSystemProperties(true)
                .maxConnections(10)
                .allowSelfSignedCertificates(true)
                .disableHostnameVerification(true)
                .proxyHost("localhost")
                .proxyPort(8080)
                .proxyProtocol("http")
                .proxyUsername("user")
                .proxyPassword("password")
                .connectionTimeoutMs(1000)
                .requestTimeoutMs(2000)
                .socketTimeoutMs(3000)
                .build();

        Map<String, String> params = options.toServiceFactoryParameters();
        assertEquals("true", params.get(ConnectionOptions.PARAM_USE_SYSTEM_PROPERTIES));
        assertEquals("10", params.get(ConnectionOptions.PARAM_MAX_CONNECTIONS));
        assertEquals("true", params.get(ConnectionOptions.PARAM_ALLOW_SELF_SIGNED_CERTIFICATES));
        assertEquals("true", params.get(ConnectionOptions.PARAM_DISABLE_HOSTNAME_VERIFICATION));
        assertEquals("localhost", params.get(ConnectionOptions.PARAM_PROXY_HOST));
        assertEquals("8080", params.get(ConnectionOptions.PARAM_PROXY_PORT));
        assertEquals("http", params.get(ConnectionOptions.PARAM_PROXY_PROTOCOL));
        assertEquals("user", params.get(ConnectionOptions.PARAM_PROXY_USERNAME));
        assertEquals("password", params.get(ConnectionOptions.PARAM_PROXY_PASSWORD));
        assertEquals("1000", params.get(ConnectionOptions.PARAM_CONNECTION_TIMEOUT_MS));
        assertEquals("2000", params.get(ConnectionOptions.PARAM_REQUEST_TIMEOUT_MS));
        assertEquals("3000", params.get(ConnectionOptions.PARAM_SOCKET_TIMEOUT_MS));
    }

    @Test
    public void testFromServiceFactoryParameters() {
        Map<String, String> params = new HashMap<>();
        params.put(ConnectionOptions.PARAM_USE_SYSTEM_PROPERTIES, "true");
        params.put(ConnectionOptions.PARAM_MAX_CONNECTIONS, "10");
        params.put(ConnectionOptions.PARAM_ALLOW_SELF_SIGNED_CERTIFICATES, "true");
        params.put(ConnectionOptions.PARAM_DISABLE_HOSTNAME_VERIFICATION, "true");
        params.put(ConnectionOptions.PARAM_PROXY_HOST, "localhost");
        params.put(ConnectionOptions.PARAM_PROXY_PORT, "8080");
        params.put(ConnectionOptions.PARAM_PROXY_PROTOCOL, "http");
        params.put(ConnectionOptions.PARAM_PROXY_USERNAME, "user");
        params.put(ConnectionOptions.PARAM_PROXY_PASSWORD, "password");
        params.put(ConnectionOptions.PARAM_CONNECTION_TIMEOUT_MS, "1000");
        params.put(ConnectionOptions.PARAM_REQUEST_TIMEOUT_MS, "2000");
        params.put(ConnectionOptions.PARAM_SOCKET_TIMEOUT_MS, "3000");

        ConnectionOptions options = ConnectionOptions.fromServiceFactoryParameters(params);
        assertTrue(options.isUseSystemPropertes());
        assertEquals(10, options.getMaxConnections());
        assertTrue(options.isAllowSelfSignedCertificates());
        assertTrue(options.isDisableHostnameVerification());
        assertEquals("localhost", options.getProxyHost());
        assertEquals(8080, options.getProxyPort());
        assertEquals("http", options.getProxyProtocol());
        assertEquals("user", options.getProxyUsername());
        assertEquals("password", options.getProxyPassword());
        assertEquals(1000, options.getConnectionTimeoutMs());
        assertEquals(2000, options.getRequestTimeoutMs());
        assertEquals(3000, options.getSocketTimeoutMs());
    }

    @Test
    public void testFromServiceFactoryParametersWithDefaults() {
        ConnectionOptions options = ConnectionOptions.fromServiceFactoryParameters(Collections.emptyMap());
        assertFalse(options.isUseSystemPropertes());
        assertEquals(ConnectionOptions.MAX_CONNECTIONS_DEFAULT, options.getMaxConnections());
        assertFalse(options.isAllowSelfSignedCertificates());
        assertFalse(options.isDisableHostnameVerification());
        assertNull(options.getProxyHost());
        assertEquals(-1, options.getProxyPort());
        assertNull(options.getProxyProtocol());
        assertNull(options.getProxyUsername());
        assertNull(options.getProxyPassword());
        assertEquals(-1, options.getConnectionTimeoutMs());
        assertEquals(-1, options.getRequestTimeoutMs());
        assertEquals(-1, options.getSocketTimeoutMs());
    }

    @Test
    public void testEqualsAndHashCode() {
        ConnectionOptions options1 = ConnectionOptions.builder().useSystemProperties(true).build();
        ConnectionOptions options2 = ConnectionOptions.builder().useSystemProperties(true).build();
        ConnectionOptions options3 = ConnectionOptions.builder().useSystemProperties(false).build();

        assertEquals(options1, options2);
        assertEquals(options1.hashCode(), options2.hashCode());
        assertNotEquals(options1, options3);
    }
}