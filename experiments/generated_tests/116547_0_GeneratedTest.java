java
package cuke4duke.mojo;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class CucumberMojoTest {

    @Test
    public void testAllCucumberArgsWithCucumberArgs() {
        CucumberMojo mojo = new CucumberMojo();
        mojo.cucumberArgs = new ArrayList<>();
        mojo.cucumberArgs.add("--format");
        mojo.cucumberArgs.add("pretty");
        mojo.features = "features";
        String result = mojo.allCucumberArgs();
        assertEquals("--format pretty features", result);
    }

    @Test
    public void testAllCucumberArgsWithExtraCucumberArgs() {
        CucumberMojo mojo = new CucumberMojo();
        mojo.extraCucumberArgs = "--dry-run";
        mojo.features = "features";
        String result = mojo.allCucumberArgs();
        assertEquals("--dry-run features", result);
    }

    @Test
    public void testAllCucumberArgsWithBothArgs() {
        CucumberMojo mojo = new CucumberMojo();
        mojo.cucumberArgs = new ArrayList<>();
        mojo.cucumberArgs.add("--format");
        mojo.cucumberArgs.add("pretty");
        mojo.extraCucumberArgs = "--dry-run";
        mojo.features = "features";
        String result = mojo.allCucumberArgs();
        assertEquals("--format pretty --dry-run features", result);
    }

    @Test
    public void testAllCucumberArgsWithNoArgs() {
        CucumberMojo mojo = new CucumberMojo();
        mojo.features = "features";
        String result = mojo.allCucumberArgs();
        assertEquals("features", result);
    }

    @Test
    public void testAllCucumberArgsWithNullCucumberArgs() {
        CucumberMojo mojo = new CucumberMojo();
        mojo.cucumberArgs = null;
        mojo.extraCucumberArgs = "--dry-run";
        mojo.features = "features";
        String result = mojo.allCucumberArgs();
        assertEquals("--dry-run features", result);
    }

    @Test
    public void testAllCucumberArgsWithNullExtraCucumberArgs() {
        CucumberMojo mojo = new CucumberMojo();
        mojo.cucumberArgs = new ArrayList<>();
        mojo.cucumberArgs.add("--format");
        mojo.cucumberArgs.add("pretty");
        mojo.extraCucumberArgs = null;
        mojo.features = "features";
        String result = mojo.allCucumberArgs();
        assertEquals("--format pretty features", result);
    }
}