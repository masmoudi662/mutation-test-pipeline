java
package com.scienceminer.glutton.utils.grobid;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GrobidResponseStaxHandlerTest {

    private GrobidResponseStaxHandler handler;

    @Before
    public void setUp() {
        handler = new GrobidResponseStaxHandler();
    }

    @Test
    public void testGetResponse_default() {
        assertNull(handler.getResponse());
    }

    @Test
    public void testGetResponse_afterSetting() {
        GrobidResponse expectedResponse = new GrobidResponse();
        handler.response = expectedResponse;
        assertEquals(expectedResponse, handler.getResponse());
    }

    private class GrobidResponse {

    }
}