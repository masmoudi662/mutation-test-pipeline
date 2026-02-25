java
package fitnesse.revisioncontrol;

import fitnesse.html.HtmlTag;
import fitnesse.wiki.WikiPageAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static fitnesse.revisioncontrol.CheckinOperationHtmlBuilder.COMMIT_MESSAGE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RevisionControlOperationTest {

    private RevisionControlOperation<String> operation;
    private RevisionController revisionController;
    private String pagePath;
    private Map<String, String> parameters;

    @Before
    public void setUp() {
        operation = new RevisionControlOperation<String>() {
            @Override
            public String execute(RevisionController revisionController, String pagePath, Map<String, String> parameters) {
                return "executed";
            }
        };
        revisionController = mock(RevisionController.class);
        pagePath = "TestPage";
        parameters = new HashMap<>();
    }

    @Test
    public void testExecuteWithEmptyParameters() {
        RevisionControlOperation<String> mockOperation = Mockito.mock(RevisionControlOperation.class);
        RevisionController mockController = Mockito.mock(RevisionController.class);
        String pagePath = "TestPage";
        when(mockOperation.execute(mockController, pagePath, new HashMap<>())).thenReturn("mocked");

        String result = mockOperation.execute(mockController, pagePath);

        assertEquals("mocked", result);
    }

    @Test
    public void testExecuteCallsExecuteWithEmptyMap() {
        RevisionControlOperation<String> testOperation = new RevisionControlOperation<String>() {
            @Override
            public String execute(RevisionController revisionController, String pagePath, Map<String, String> parameters) {
                assertEquals(0, parameters.size());
                return "success";
            }
        };

        String result = testOperation.execute(revisionController, pagePath);

        assertEquals("success", result);
    }
}