java
package org.eclipse.rdf4j.console.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import org.eclipse.rdf4j.console.ConsoleIO;
import org.eclipse.rdf4j.console.ConsoleState;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FederateTest {

  private Federate federateCommand;
  private ConsoleIO consoleIO;
  private ConsoleState consoleState;
  private RepositoryManager repositoryManager;

  @BeforeEach
  public void setUp() {
    consoleIO = mock(ConsoleIO.class);
    consoleState = mock(ConsoleState.class);
    repositoryManager = mock(RepositoryManager.class);

    federateCommand = new Federate();
    federateCommand.setConsoleIO(consoleIO);
    federateCommand.setConsoleState(consoleState);
    federateCommand.setRepositoryManager(repositoryManager);
  }

  @Test
  public void testExecuteWithNotEnoughParameters() throws IOException {
    federateCommand.execute("federate", "param1", "param2");
    verify(consoleIO).writeln(federateCommand.getHelpLong());
  }

  @Test
  public void testExecuteWithDuplicateRepositoryIds() throws IOException {
    federateCommand.execute("federate", "fed1", "repo1", "repo1");
    verify(consoleIO).writeError("Duplicate repository id's specified.");
  }

  @Test
  public void testExecuteWithValidParameters() throws IOException {
    federateCommand.execute("federate", "fed1", "repo1", "repo2");
    verify(consoleIO, Mockito.never()).writeError("Duplicate repository id's specified.");
  }

  @Test
  public void testExecuteWithDistinctAndReadonlyParameters() throws IOException {
    federateCommand.execute("federate", "distinct=true", "readonly=false", "fed1", "repo1", "repo2");
    verify(consoleIO, Mockito.never()).writeError("Duplicate repository id's specified.");
  }

  @Test
  public void testExecuteWithDistinctParameterOnly() throws IOException {
    federateCommand.execute("federate", "distinct=true", "fed1", "repo1", "repo2");
    verify(consoleIO, Mockito.never()).writeError("Duplicate repository id's specified.");
  }

  @Test
  public void testExecuteWithReadonlyParameterOnly() throws IOException {
    federateCommand.execute("federate", "readonly=false", "fed1", "repo1", "repo2");
    verify(consoleIO, Mockito.never()).writeError("Duplicate repository id's specified.");
  }
}