java
package com.car2go.endpoint2mock2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RegistryTest {

    private Registry registry;

    @Mock
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        registry = new Registry();
        registry.configuration = configuration;
        Method getMockedEndpointsMethod = Registry.class.getDeclaredMethod("getMockedEndpoints");
        getMockedEndpointsMethod.setAccessible(true);
        registry.getMockedEndpointsMethod = getMockedEndpointsMethod;
    }

    @Test
    public void isInRegistry_nullGetMockedEndpointsMethod() {
        registry.getMockedEndpointsMethod = null;
        assertFalse(registry.isInRegistry("test"));
    }

    @Test
    public void isInRegistry_emptyMockedEndpoints() {
        when(configuration.getMockedEndpoints()).thenReturn(Collections.emptySet());
        assertFalse(registry.isInRegistry("test"));
    }

    @Test
    public void isInRegistry_endpointMatches() {
        Set<String> mockedEndpoints = new HashSet<>();
        mockedEndpoints.add("test");
        when(configuration.getMockedEndpoints()).thenReturn(mockedEndpoints);
        assertTrue(registry.isInRegistry("test"));
    }

    @Test
    public void isInRegistry_endpointNotMatches() {
        Set<String> mockedEndpoints = new HashSet<>();
        mockedEndpoints.add("test");
        when(configuration.getMockedEndpoints()).thenReturn(mockedEndpoints);
        assertFalse(registry.isInRegistry("test1"));
    }

    @Test
    public void isInRegistry_multipleEndpointsOneMatches() {
        Set<String> mockedEndpoints = new HashSet<>();
        mockedEndpoints.add("test1");
        mockedEndpoints.add("test2");
        mockedEndpoints.add("test3");
        when(configuration.getMockedEndpoints()).thenReturn(mockedEndpoints);
        assertFalse(registry.isInRegistry("test4"));

    }

    @Test
    public void isInRegistry_emptyUrl() {
        Set<String> mockedEndpoints = new HashSet<>();
        mockedEndpoints.add("");
        when(configuration.getMockedEndpoints()).thenReturn(mockedEndpoints);
        assertTrue(registry.isInRegistry(""));
    }

    @Test
    public void isInRegistry_nullUrl() {
        Set<String> mockedEndpoints = new HashSet<>();
        mockedEndpoints.add(null);
        when(configuration.getMockedEndpoints()).thenReturn(mockedEndpoints);
        assertFalse(registry.isInRegistry(null));
    }

    @Test
    public void isInRegistry_withNullEndpoint() {
        Set<String> mockedEndpoints = new HashSet<>();
        mockedEndpoints.add(null);
        when(configuration.getMockedEndpoints()).thenReturn(mockedEndpoints);
        assertFalse(registry.isInRegistry("test"));
    }
}