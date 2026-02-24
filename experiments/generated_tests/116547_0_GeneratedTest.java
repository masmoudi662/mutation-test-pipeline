java
package cuke4duke.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CucumberMojoTest extends AbstractMojoTestCase {

    private CucumberMojo mojo;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        File pom = getTestFile("src/test/resources/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        mojo = (CucumberMojo) lookupConfiguredMojo(pom, "cucumber");
        assertNotNull(mojo);
    }

    @Test
    public void testAllCucumberArgs_emptyLists() {
        mojo.cucumberArgs = Collections.emptyList();
        mojo.extraCucumberArgs = null;
        mojo.features = "myFeatures";
        String result = mojo.allCucumberArgs();
        assertEquals("myFeatures", result);
    }

    @Test
    public void testAllCucumberArgs_withCucumberArgs() {
        mojo.cucumberArgs = Arrays.asList("--format", "pretty");
        mojo.extraCucumberArgs = null;
        mojo.features = "myFeatures";
        String result = mojo.allCucumberArgs();
        assertEquals("--format pretty myFeatures", result);
    }

    @Test
    public void testAllCucumberArgs_withExtraCucumberArgs() {
        mojo.cucumberArgs = Collections.emptyList();
        mojo.extraCucumberArgs = "--tags @mytag";
        mojo.features = "myFeatures";
        String result = mojo.allCucumberArgs();
        assertEquals("--tags @mytag myFeatures", result);
    }

    @Test
    public void testAllCucumberArgs_withBothArgs() {
        mojo.cucumberArgs = Arrays.asList("--format", "pretty");
        mojo.extraCucumberArgs = "--tags @mytag";
        mojo.features = "myFeatures";
        String result = mojo.allCucumberArgs();
        assertEquals("--format pretty --tags @mytag myFeatures", result);
    }

    @Test
    public void testGetJvmArgs_null() {
        mojo.jvmArgs = null;
        List<String> result = mojo.getJvmArgs();
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testGetJvmArgs_notNull() {
        mojo.jvmArgs = Arrays.asList("-Xmx512m", "-XX:MaxPermSize=256m");
        List<String> result = mojo.getJvmArgs();
        assertEquals(Arrays.asList("-Xmx512m", "-XX:MaxPermSize=256m"), result);
    }
}