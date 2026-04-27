java
package consulo.versionControlSystem.log.impl.internal.data;

import consulo.versionControlSystem.log.graph.GraphCommit;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class VcsLogMultiRepoJoinerTest {

  @Test
  public void testSingleRepo() {
    VcsLogMultiRepoJoiner<Integer, GraphCommit<Integer>> joiner = new VcsLogMultiRepoJoiner<>();
    List<GraphCommit<Integer>> repo = new ArrayList<>();
    repo.add(mock(GraphCommit.class));
    repo.add(mock(GraphCommit.class));
    List<List<GraphCommit<Integer>>> repos = new ArrayList<>();
    repos.add(repo);

    List<GraphCommit<Integer>> joined = joiner.join(repos);

    assertEquals(2, joined.size());
  }

  @Test
  public void testMultipleRepos() {
    VcsLogMultiRepoJoiner<Integer, GraphCommit<Integer>> joiner = new VcsLogMultiRepoJoiner<>();

    List<GraphCommit<Integer>> repo1 = new ArrayList<>();
    GraphCommit<Integer> commit1 = mock(GraphCommit.class);
    GraphCommit<Integer> commit2 = mock(GraphCommit.class);
    repo1.add(commit1);
    repo1.add(commit2);

    List<GraphCommit<Integer>> repo2 = new ArrayList<>();
    GraphCommit<Integer> commit3 = mock(GraphCommit.class);
    GraphCommit<Integer> commit4 = mock(GraphCommit.class);
    repo2.add(commit3);
    repo2.add(commit4);

    List<List<GraphCommit<Integer>>> repos = new ArrayList<>();
    repos.add(repo1);
    repos.add(repo2);

    List<GraphCommit<Integer>> joined = joiner.join(repos);

    assertEquals(4, joined.size());
  }

  private static class MockGraphCommit implements GraphCommit<Integer> {
        private final Integer id;

        public MockGraphCommit(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public long getTimestamp() {
            return 0;
        }

        @Override
        public List<Integer> getParents() {
            return Collections.emptyList();
        }
    }

    @Test
    public void testJoinWithOrdering() {
        VcsLogMultiRepoJoiner<Integer, GraphCommit<Integer>> joiner = new VcsLogMultiRepoJoiner<>();

        List<GraphCommit<Integer>> repo1 = new ArrayList<>();
        repo1.add(new MockGraphCommit(3));
        repo1.add(new MockGraphCommit(1));

        List<GraphCommit<Integer>> repo2 = new ArrayList<>();
        repo2.add(new MockGraphCommit(4));
        repo2.add(new MockGraphCommit(2));

        List<List<GraphCommit<Integer>>> repos = new ArrayList<>();
        repos.add(repo1);
        repos.add(repo2);

        List<GraphCommit<Integer>> joined = joiner.join(repos);
        assertEquals(4, joined.size());
    }
}