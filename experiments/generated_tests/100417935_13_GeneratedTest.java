java
package org.rcdukes.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestEnvironment {

    @Test
    public void testDefaultSettings() {
        Environment env = Environment.getInstance();
        assertNotNull(env);
    }

    @Test
    public void testEnvVarAccess() {
        Environment env = Environment.getInstance();
        String path = env.getProperty("PATH", null);
        if (path != null) {
            assertTrue(path.length() > 1);
        }
    }

    @Test
    public void testFromFile() {
        File iniFile = new File("../rc-drivecontrol/src/test/resources/dukes/dukes.ini");
        if (iniFile.exists()) {
            Environment env = Environment.from(iniFile.getPath());
            assertNotNull(env);
            String prop = env.getProperty("camera.device", null);
            assertNotNull(prop);
            assertEquals("/dev/video0", prop);
        } else {
            System.err.println("Skipping testFromFile since " + iniFile.getAbsolutePath() + " does not exist");
        }
    }

    @Test
    public void testMock() {
        Environment.mock();
        Environment env = Environment.getInstance();
        String prop = env.getProperty("camera.device", null);
        assertNotNull(prop);
        assertEquals("/dev/video0", prop);
        env.clear();
    }
    
    @Test
    public void testGetHostName() {
    	Environment env = Environment.getInstance();
    	String hostName = env.getHostName();
    	assertNotNull(hostName);
    }
    
    @Test
    public void testGetHomePath() {
    	Environment env = Environment.getInstance();
    	String homePath = env.getHomePath();
    	assertNotNull(homePath);
    }

    @Test
    public void testGetClassPath() {
        Environment env = Environment.getInstance();
        String classPath = env.getClassPath();
        assertNotNull(classPath);
    }
}