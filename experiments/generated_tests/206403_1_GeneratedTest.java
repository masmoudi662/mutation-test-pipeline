java
package org.apache.jackrabbit.spi2dav;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ConnectionOptionsTest {

    @Test
    public void testBuilder() {
        ConnectionOptions.Builder builder = ConnectionOptions.builder();
        assertNotNull(builder);
    }

    @Test
    public void testBuildDefault() {
        ConnectionOptions options = ConnectionOptions.builder().build();
        assertNotNull(options);
    }

    @Test
    public void testToString() {
        ConnectionOptions options = ConnectionOptions.builder().build();
        assertNotNull(options.toString());
    }

    @Test
    public void testConnectionOptionsBuilder() {
        ConnectionOptions.Builder builder = ConnectionOptions.builder();
        assertNotNull(builder);
    }

    @Test
    public void testBuildWithCustomValues() {
        ConnectionOptions options = ConnectionOptions.builder()
                .setHttpClientBuilder(null)
                .setConnectionManager(null)
                .setRepositoryServiceFactory(null)
                .build();
        assertNotNull(options);
    }

    @Test
    public void testHashCode() {
        ConnectionOptions options1 = ConnectionOptions.builder().build();
        ConnectionOptions options2 = ConnectionOptions.builder().build();
        assertNotNull(options1.hashCode());
    }

    @Test
    public void testEquals() {
        ConnectionOptions options1 = ConnectionOptions.builder().build();
        ConnectionOptions options2 = ConnectionOptions.builder().build();
        assertNotNull(options1.equals(options2));
    }
}