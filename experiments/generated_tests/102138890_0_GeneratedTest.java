java
package $package;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HelloWorldBuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testSetUseFrench() {
        HelloWorldBuilder builder = new HelloWorldBuilder("World");
        builder.setUseFrench(true);
        assertEquals(true, builder.isUseFrench());
    }

    @Test
    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        HelloWorldBuilder builder = new HelloWorldBuilder("World");
        builder.setUseFrench(true);
        project.getBuildersList().add(builder);

        project = jenkins.configRoundtrip(project);

        HelloWorldBuilder after = project.getBuildersList().get(HelloWorldBuilder.class);

        assertEquals("World", after.getName());
        assertEquals(true, after.isUseFrench());
    }
}