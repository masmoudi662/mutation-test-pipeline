java
package de.vier_bier.habpanelviewer.openhab;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class OpenhabSseConnectionTest {

    @Mock
    private Openhab openhab;

    private OpenhabSseConnection openhabSseConnection;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        openhabSseConnection = new OpenhabSseConnection(openhab);
    }

    @Test
    public void testBuildUrl() {
        when(openhab.getBaseUrl()).thenReturn("http://localhost:8080");
        openhabSseConnection.mUrl = "http://localhost:8080";
        openhabSseConnection.topics = new String[]{"testTopic"};
        String expectedUrl = "http://localhost:8080/rest/events?topics=testTopic";
        String actualUrl = openhabSseConnection.buildUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testBuildUrlMultipleTopics() {
        when(openhab.getBaseUrl()).thenReturn("http://localhost:8080");
        openhabSseConnection.mUrl = "http://localhost:8080";
        openhabSseConnection.topics = new String[]{"testTopic1", "testTopic2"};
        String expectedUrl = "http://localhost:8080/rest/events?topics=testTopic1,testTopic2";
        String actualUrl = openhabSseConnection.buildUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testBuildUrlNoTopics() {
        when(openhab.getBaseUrl()).thenReturn("http://localhost:8080");
        openhabSseConnection.mUrl = "http://localhost:8080";
        openhabSseConnection.topics = new String[]{};
        String expectedUrl = "http://localhost:8080/rest/events?topics=";
        String actualUrl = openhabSseConnection.buildUrl();
        assertEquals(expectedUrl, actualUrl);
    }
}