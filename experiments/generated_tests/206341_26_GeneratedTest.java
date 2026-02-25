java
package org.apache.maven.enforcer.rules;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependencyGraph;
import org.codehaus.plexus.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;

class RequireNoRepositoriesTest {

    @Test
    void testExecuteNoRepositories() throws Exception {
        RequireNoRepositories rule = new RequireNoRepositories();
        EnforcerRuleHelper helper = mock(EnforcerRuleHelper.class);
        MavenSession session = mock(MavenSession.class);
        ProjectDependencyGraph projectDependencyGraph = mock(ProjectDependencyGraph.class);
        MavenProject mavenProject = mock(MavenProject.class);
        Model model = new Model();

        when(helper.evaluate("${session}")).thenReturn(session);
        when(session.getProjectDependencyGraph()).thenReturn(projectDependencyGraph);
        when(projectDependencyGraph.getSortedProjects()).thenReturn(Collections.singletonList(mavenProject));
        when(mavenProject.getOriginalModel()).thenReturn(model);

        rule.execute(helper);
    }

    @Test
    void testExecuteWithRepositoriesBanned() throws Exception {
        RequireNoRepositories rule = new RequireNoRepositories();
        rule.setBanRepositories(true);
        EnforcerRuleHelper helper = mock(EnforcerRuleHelper.class);
        MavenSession session = mock(MavenSession.class);
        ProjectDependencyGraph projectDependencyGraph = mock(ProjectDependencyGraph.class);
        MavenProject mavenProject = mock(MavenProject.class);
        Model model = new Model();
        model.addRepository(new Repository());

        when(helper.evaluate("${session}")).thenReturn(session);
        when(session.getProjectDependencyGraph()).thenReturn(projectDependencyGraph);
        when(projectDependencyGraph.getSortedProjects()).thenReturn(Collections.singletonList(mavenProject));
        when(mavenProject.getOriginalModel()).thenReturn(model);
        when(mavenProject.getGroupId()).thenReturn("org.test");
        when(mavenProject.getArtifactId()).thenReturn("test-artifact");
        when(mavenProject.getVersion()).thenReturn("1.0");

        assertThrows(EnforcerRuleException.class, () -> rule.execute(helper));
    }

    @Test
    void testExecuteWithPluginRepositoriesBanned() throws Exception {
        RequireNoRepositories rule = new RequireNoRepositories();
        rule.setBanPluginRepositories(true);
        EnforcerRuleHelper helper = mock(EnforcerRuleHelper.class);
        MavenSession session = mock(MavenSession.class);
        ProjectDependencyGraph projectDependencyGraph = mock(ProjectDependencyGraph.class);
        MavenProject mavenProject = mock(MavenProject.class);
        Model model = new Model();
        model.addPluginRepository(new Repository());

        when(helper.evaluate("${session}")).thenReturn(session);
        when(session.getProjectDependencyGraph()).thenReturn(projectDependencyGraph);
        when(projectDependencyGraph.getSortedProjects()).thenReturn(Collections.singletonList(mavenProject));
        when(mavenProject.getOriginalModel()).thenReturn(model);
        when(mavenProject.getGroupId()).thenReturn("org.test");
        when(mavenProject.getArtifactId()).thenReturn("test-artifact");
        when(mavenProject.getVersion()).thenReturn("1.0");

        assertThrows(EnforcerRuleException.class, () -> rule.execute(helper));
    }
}