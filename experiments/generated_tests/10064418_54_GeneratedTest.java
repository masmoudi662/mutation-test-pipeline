java
package org.trimou.extension.spring.starter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrimouTemplateAvailabilityProviderTest {

    private TemplateAvailabilityProvider provider;
    private Environment environment;
    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private Resource resource;

    @Before
    public void setup() {
        provider = new TrimouTemplateAvailabilityProvider();
        environment = mock(Environment.class);
        classLoader = getClass().getClassLoader();
        resourceLoader = mock(ResourceLoader.class);
        resource = mock(Resource.class);
    }

    @Test
    public void testIsTemplateAvailable_trimouNotPresent() {
        ClassLoader cl = mock(ClassLoader.class);
        assertFalse(provider.isTemplateAvailable("test", environment, cl, resourceLoader));
    }

    @Test
    public void testIsTemplateAvailable_resourceExists() {
        when(resourceLoader.getResource("classpath:/templates/test.mustache")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        assertTrue(provider.isTemplateAvailable("test", environment, classLoader, resourceLoader));
    }

    @Test
    public void testIsTemplateAvailable_resourceDoesNotExist() {
        when(resourceLoader.getResource("classpath:/templates/test.mustache")).thenReturn(resource);
        when(resource.exists()).thenReturn(false);
        assertFalse(provider.isTemplateAvailable("test", environment, classLoader, resourceLoader));
    }

    @Test
    public void testIsTemplateAvailable_customPrefixSuffix() {
        when(environment.getProperty("trimou.prefix", "classpath:/templates/")).thenReturn("custom/");
        when(environment.getProperty("trimou.suffix", ".mustache")).thenReturn(".trimou");
        when(resourceLoader.getResource("custom/test.trimou")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        assertTrue(provider.isTemplateAvailable("test", environment, classLoader, resourceLoader));
    }

     @Test
    public void testIsTemplateAvailable_nullPrefixSuffix() {
        when(resourceLoader.getResource("classpath:/templates/test.mustache")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        assertTrue(provider.isTemplateAvailable("test", environment, classLoader, resourceLoader));
    }
}