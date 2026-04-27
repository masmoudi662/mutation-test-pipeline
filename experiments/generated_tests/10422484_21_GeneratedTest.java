java
package org.greencheek.related.searching.repository;

import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.related.util.config.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeOrTransportBasedElasticSearchClientFactoryCreatorTest {

    private NodeOrTransportBasedElasticSearchClientFactoryCreator factoryCreator;
    private Configuration configuration;

    @Before
    public void setUp() {
        factoryCreator = new NodeOrTransportBasedElasticSearchClientFactoryCreator();
        configuration = mock(Configuration.class);
    }

    @Test
    public void testGetElasticSearchClientConnectionFactoryWithNodeClientType() {
        when(configuration.getElasticSearchClientType()).thenReturn(Configuration.ClientType.NODE);
        ElasticSearchClientFactory factory = factoryCreator.getElasticSearchClientConnectionFactory(configuration);
        assertTrue(factory instanceof NodeBasedElasticSearchClientFactory);
    }

    @Test
    public void testGetElasticSearchClientConnectionFactoryWithTransportClientType() {
        when(configuration.getElasticSearchClientType()).thenReturn(Configuration.ClientType.TRANSPORT);
        ElasticSearchClientFactory factory = factoryCreator.getElasticSearchClientConnectionFactory(configuration);
        assertTrue(factory instanceof TransportBasedElasticSearchClientFactory);
    }

    @Test
    public void testGetElasticSearchClientConnectionFactoryWithDefaultClientType() {
        when(configuration.getElasticSearchClientType()).thenReturn(null);
        ElasticSearchClientFactory factory = factoryCreator.getElasticSearchClientConnectionFactory(configuration);
        assertTrue(factory instanceof TransportBasedElasticSearchClientFactory);
    }

    @Test
    public void testGetElasticSearchClientConnectionFactoryWithUnknownClientType() {
        when(configuration.getElasticSearchClientType()).thenReturn(Configuration.ClientType.UNKNOWN);
        ElasticSearchClientFactory factory = factoryCreator.getElasticSearchClientConnectionFactory(configuration);
        assertTrue(factory instanceof TransportBasedElasticSearchClientFactory);
    }
}