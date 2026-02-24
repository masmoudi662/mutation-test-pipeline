java
package fitnesse.revisioncontrol;

import fitnesse.html.HtmlTag;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RevisionControlOperationTest {

    @Test
    public void testAddExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        when(revisionController.add("TestPage")).thenReturn(new Results(true, "Added"));

        Results results = RevisionControlOperation.ADD.execute(revisionController, "TestPage");
        assertEquals("Added", results.getMessage());
    }

    @Test
    public void testSyncExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        State expectedState = new State(true, true, true);
        when(revisionController.getState("TestPage")).thenReturn(expectedState);

        State state = RevisionControlOperation.SYNC.execute(revisionController, "TestPage");
        assertEquals(expectedState, state);
    }

    @Test
    public void testUpdateExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        NewRevisionResults expectedResults = new NewRevisionResults("123", "Updated");
        when(revisionController.update("TestPage")).thenReturn(expectedResults);

        NewRevisionResults results = RevisionControlOperation.UPDATE.execute(revisionController, "TestPage");
        assertEquals(expectedResults, results);
    }

    @Test
    public void testCheckoutExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        Results expectedResults = new Results(true, "Checked out");
        when(revisionController.checkout("TestPage")).thenReturn(expectedResults);

        Results results = RevisionControlOperation.CHECKOUT.execute(revisionController, "TestPage");
        assertEquals(expectedResults.getMessage(), results.getMessage());
    }

    @Test
    public void testCheckinExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        NewRevisionResults expectedResults = new NewRevisionResults("456", "Checked in");
        when(revisionController.checkin("TestPage", "Commit message")).thenReturn(expectedResults);

        Map<String, String> args = new HashMap<>();
        args.put("commitMessage", "Commit message");

        NewRevisionResults results = RevisionControlOperation.CHECKIN.execute(revisionController, "TestPage", args);
        assertEquals(expectedResults, results);
    }

    @Test
    public void testRevertExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        Results expectedResults = new Results(true, "Reverted");
        when(revisionController.revert("TestPage")).thenReturn(expectedResults);

        Results results = RevisionControlOperation.REVERT.execute(revisionController, "TestPage");
        assertEquals(expectedResults.getMessage(), results.getMessage());
    }

    @Test
    public void testStatusExecute() {
        RevisionController revisionController = mock(RevisionController.class);
        StatusResults expectedResults = new StatusResults("OK");
        when(revisionController.getStatus("TestPage")).thenReturn(expectedResults);

        StatusResults results = RevisionControlOperation.STATUS.execute(revisionController, "TestPage");
        assertEquals(expectedResults.getStatus(), results.getStatus());
    }

    @Test
    public void testMakeHtml() {
        HtmlTag tag = RevisionControlOperation.ADD.makeHtml("resource");
        assertEquals("<a accesskey=\"a\" href=\"resource?addToRevisionControl\"><span class=\"addToRevisionControl\">Add</span></a>", tag.html());
    }

    @Test
    public void testExecuteWithoutArgs() {
        RevisionController revisionController = mock(RevisionController.class);
        when(revisionController.add("TestPage")).thenReturn(new Results(true, "Added"));
        Results results = RevisionControlOperation.ADD.execute(revisionController, "TestPage");
        assertEquals("Added", results.getMessage());
    }
}