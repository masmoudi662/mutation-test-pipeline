java
package eu.planets_project.ifr.core.common.conf;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class ServiceConfigTest {

    private static final Logger _log = LoggerFactory.getLogger(ServiceConfigTest.class);

    @Test
    public void testGetConfigurationWithClass() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration(ServiceConfigTest.class);
        assertNotNull(config);
    }

    @Test(expected = ConfigurationException.class)
    public void testGetConfigurationWithInvalidClass() throws ConfigurationException {
        ServiceConfig.getConfiguration("NonExistentClass");
    }

    @Test
    public void testGetConfigurationWithName() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration("eu.planets_project.ifr.core.common.conf.ServiceConfigTest");
        assertNotNull(config);
    }

    @Test
    public void testGetConfigurationDefault() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration((String) null);
        assertNotNull(config);
    }

    @Test
    public void testGetConfigurationEmpty() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration("");
        assertNotNull(config);
    }

    @Test
    public void testConfigurationGetValue() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration(ServiceConfigTest.class);
        config.setValue("test.property", "test.value");
        assertEquals("test.value", config.getValue("test.property"));
    }

    @Test
    public void testConfigurationGetValueDefault() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration(ServiceConfigTest.class);
        assertEquals("default", config.getValue("non.existent.property", "default"));
    }

    @Test
    public void testConfigurationSetValue() throws ConfigurationException {
        Configuration config = ServiceConfig.getConfiguration(ServiceConfigTest.class);
        config.setValue("test.property", "new.value");
        assertEquals("new.value", config.getValue("test.property"));
    }
}