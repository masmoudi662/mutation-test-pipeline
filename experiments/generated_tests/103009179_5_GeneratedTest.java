java
package org.jasig.cas.support.openid.web.mvc;

import org.jasig.cas.support.openid.OpenIdConstants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

public class SmartOpenIdControllerTest {

    private SmartOpenIdController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        controller = new SmartOpenIdController();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testCanHandleAssociateMode() {
        request.setParameter(OpenIdConstants.OPENID_MODE, OpenIdConstants.ASSOCIATE);
        assertTrue(controller.canHandle(request, response));
    }

    @Test
    public void testCanHandleOtherMode() {
        request.setParameter(OpenIdConstants.OPENID_MODE, "checkid_immediate");
        assertFalse(controller.canHandle(request, response));
    }

    @Test
    public void testCanHandleNoMode() {
        assertFalse(controller.canHandle(request, response));
    }

    @Test
    public void testCanHandleNullMode() {
        request.setParameter(OpenIdConstants.OPENID_MODE, null);
        assertFalse(controller.canHandle(request, response));
    }

    @Test
    public void testCanHandleEmptyMode() {
        request.setParameter(OpenIdConstants.OPENID_MODE, "");
        assertFalse(controller.canHandle(request, response));
    }
}