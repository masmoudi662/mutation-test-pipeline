java
package org.intalio.tempo.web.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.intalio.tempo.security.token.TokenService;
import org.intalio.tempo.web.ApplicationState;
import org.intalio.tempo.web.Constants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class LoginControllerTest extends TestCase {

    private LoginController loginController;
    private TokenService tokenService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private BindException errors;
    private LoginCommand loginCommand;
    private ApplicationState applicationState;
    private HttpSession session;

    @Before
    public void setUp() throws Exception {
        tokenService = mock(TokenService.class);
        loginController = new LoginController(tokenService, "/defaultRedirect");
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        errors = new BindException(new LoginCommand(), "loginCommand");
        loginCommand = new LoginCommand();
        applicationState = new ApplicationState();
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(ApplicationState.APPLICATION_STATE)).thenReturn(applicationState);
    }

    @Test
    public void testLogOut() throws Exception {
        applicationState.setCurrentUser(new org.intalio.tempo.web.User("testUser", new String[]{"role1"}, "token", false));
        ModelAndView modelAndView = loginController.logOut(request, response, loginCommand, errors);

        assertNull(applicationState.getCurrentUser());
        assertNull(applicationState.getPreviousAction());
        assertEquals(Constants.LOGIN_VIEW, modelAndView.getViewName());
        Map model = modelAndView.getModel();
        assertNotNull(model.get("login"));
    }
}