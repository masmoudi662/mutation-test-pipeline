java
package de.qaware.qav.input.javacode.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

class JavaScopeReaderTest {

    @InjectMocks
    private JavaScopeReader javaScopeReader;

    @Mock
    private Logger LOGGER;

    @Mock
    private JavaCodeReader javaCodeReader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRead_emptyBaseDirName() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(JavaScopeReader.BASE_DIR_KEY, "");

        javaScopeReader.read(parameters);

        verify(LOGGER).warn("{} missing - no files will be read!", JavaScopeReader.BASE_DIR_KEY);
    }

    @Test
    void testRead_nullBaseDirName() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(JavaScopeReader.BASE_DIR_KEY, null);

        javaScopeReader.read(parameters);

        verify(LOGGER).warn("{} missing - no files will be read!", JavaScopeReader.BASE_DIR_KEY);
    }

    @Test
    void testRead_baseDirNotExists() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(JavaScopeReader.BASE_DIR_KEY, "not_existing_dir");

        javaScopeReader.read(parameters);

        verify(LOGGER).warn("baseDir {} missing - no files will be read!", new File("not_existing_dir").getAbsolutePath());
    }

    @Test
    void testRead_baseDirIsDirectory() {
        Map<String, Object> parameters = new HashMap<>();
        File tempDir = new File("testDir");
        tempDir.mkdir();
        tempDir.deleteOnExit();

        parameters.put(JavaScopeReader.BASE_DIR_KEY, "testDir");

        javaScopeReader.read(parameters);

        verify(javaCodeReader).readDirectory(any(File.class), eq(parameters));
        tempDir.delete();
    }

    @Test
    void testRead_baseDirIsJarFile() {
        Map<String, Object> parameters = new HashMap<>();
        File tempFile = new File("test.jar");
        parameters.put(JavaScopeReader.BASE_DIR_KEY, "test.jar");

        javaScopeReader.read(parameters);

        verify(javaCodeReader).readJarFile(any(File.class), eq(parameters));
    }

}