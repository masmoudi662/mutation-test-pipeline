java
package org.intalio.tempo.web.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class LoginControllerTest {

    private LoginController loginController;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private LoginCommand loginCommand;
    private BindException errors;
    private ApplicationState state;

    @Before
    public void setUp() {
        loginController = new LoginController();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        loginCommand = new LoginCommand();
        errors = new BindException(loginCommand, "loginCommand");
        state = new ApplicationState();
    }

    @Test
    public void testLogOut_WithState() throws Exception {
        when(request.getSession(false)).thenReturn(mock(javax.servlet.http.HttpSession.class));
        when(request.getSession().getAttribute("ApplicationState")).thenReturn(state);

        ModelAndView modelAndView = loginController.logOut(request, response, loginCommand, errors);

        assertEquals("login", modelAndView.getViewName());
        Map model = modelAndView.getModel();
        assertEquals(true, model.containsKey("login"));
    }

    @Test
    public void testLogOut_NoState() throws Exception {
        when(request.getSession(false)).thenReturn(mock(javax.servlet.http.HttpSession.class));
        when(request.getSession().getAttribute("ApplicationState")).thenReturn(null);

        ModelAndView modelAndView = loginController.logOut(request, response, loginCommand, errors);

        assertEquals("login", modelAndView.getViewName());
        Map model = modelAndView.getModel();
        assertEquals(true, model.containsKey("login"));
    }
}