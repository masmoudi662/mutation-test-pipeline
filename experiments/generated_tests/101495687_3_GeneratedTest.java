java
package at.bestsolution.maven.osgi.targetplatform.lib.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.junit.Test;

public class AdditionalDependencyProviderTest {

	@Test
	public void testReadAdditionalDependencies_emptyFile() {
		InputStream emptyFile = new ByteArrayInputStream("".getBytes());
		Set<Dependency> dependencies = AdditionalDependencyProvider.readAdditionalDependencies(emptyFile);
		assertTrue(dependencies.isEmpty());
	}

	@Test
	public void testReadAdditionalDependencies_singleDependency() {
		String dependencyLine = "groupId:artifactId:version:type:scope";
		InputStream fileWithDependency = new ByteArrayInputStream(dependencyLine.getBytes());
		Set<Dependency> dependencies = AdditionalDependencyProvider.readAdditionalDependencies(fileWithDependency);
		assertEquals(1, dependencies.size());

		Dependency dependency = dependencies.iterator().next();
		assertEquals("groupId", dependency.getGroupId());
		assertEquals("artifactId", dependency.getArtifactId());
		assertEquals("version", dependency.getVersion());
		assertEquals("type", dependency.getType());
		assertEquals("scope", dependency.getScope());
	}

	@Test
	public void testReadAdditionalDependencies_multipleDependencies() {
		String dependencyLines = "groupId1:artifactId1:version1:type1:scope1\n" +
				"groupId2:artifactId2:version2:type2:scope2";
		InputStream fileWithDependencies = new ByteArrayInputStream(dependencyLines.getBytes());
		Set<Dependency> dependencies = AdditionalDependencyProvider.readAdditionalDependencies(fileWithDependencies);
		assertEquals(2, dependencies.size());
	}

	@Test
	public void testReadAdditionalDependencies_invalidDependencyFormat() {
		String invalidDependencyLine = "invalid-format";
		InputStream fileWithInvalidDependency = new ByteArrayInputStream(invalidDependencyLine.getBytes());
		Set<Dependency> dependencies = AdditionalDependencyProvider.readAdditionalDependencies(fileWithInvalidDependency);
		assertTrue(dependencies.isEmpty());
	}

	@Test
	public void testReadAdditionalDependencies_dependencyWithClassifier() {
		String dependencyLine = "groupId:artifactId:version:type:scope:classifier";
		InputStream fileWithDependency = new ByteArrayInputStream(dependencyLine.getBytes());
		Set<Dependency> dependencies = AdditionalDependencyProvider.readAdditionalDependencies(fileWithDependency);

		Dependency dependency = dependencies.iterator().next();
		assertEquals("classifier", dependency.getClassifier());

	}
}