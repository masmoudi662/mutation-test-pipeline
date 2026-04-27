java
package org.jboss.forge.furnace.manager.maven.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class MavenRepositoriesTest
{
   private static final String MAVEN_CENTRAL_REPO = "https://repo.maven.apache.org/maven2/";

   @Test
   public void testGetRemoteRepositories()
   {
      MavenContainer container = Mockito.mock(MavenContainer.class);
      Settings settings = new Settings();

      List<RemoteRepository> enabledRepos = new ArrayList<>();
      enabledRepos.add(new RemoteRepository.Builder("test", "default", "http://example.com").build());
      Mockito.when(container.getEnabledRepositoriesFromProfile(settings)).thenReturn(enabledRepos);

      List<RemoteRepository> remoteRepositories = MavenRepositories.getRemoteRepositories(container, settings);

      Assert.assertEquals(2, remoteRepositories.size());
      Assert.assertEquals("test", remoteRepositories.get(0).getId());
      Assert.assertEquals("central", remoteRepositories.get(1).getId());
      Assert.assertEquals(MAVEN_CENTRAL_REPO, remoteRepositories.get(1).getUrl());
   }

   @Test
   public void testGetRemoteRepositoriesWithMirror()
   {
      MavenContainer container = Mockito.mock(MavenContainer.class);
      Settings settings = new Settings();
      Mirror mirror = new Mirror();
      mirror.setId("central-mirror");
      mirror.setUrl("http://mirror.example.com");
      mirror.setMirrorOf("central");
      settings.addMirror(mirror);

      List<RemoteRepository> enabledRepos = new ArrayList<>();
      Mockito.when(container.getEnabledRepositoriesFromProfile(settings)).thenReturn(enabledRepos);

      List<RemoteRepository> remoteRepositories = MavenRepositories.getRemoteRepositories(container, settings);

      Assert.assertEquals(1, remoteRepositories.size());
      Assert.assertEquals("central", remoteRepositories.get(0).getId());
      Assert.assertEquals("http://mirror.example.com", remoteRepositories.get(0).getUrl());
   }
}