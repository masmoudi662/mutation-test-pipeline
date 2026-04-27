java
package com.linkedin.kafka.cruisecontrol.servlet.security.trustedproxy;

import com.linkedin.kafka.cruisecontrol.servlet.security.DefaultRoleSecurityProvider;
import com.linkedin.kafka.cruisecontrol.servlet.security.SecurityUtils;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.AuthorizationService;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrustedProxyAuthorizationServiceTest {

  @Test
  public void testGetUserIdentityWithMatchingIP() {
    UserStore mockUserStore = mock(UserStore.class);
    UserIdentity mockUserIdentity = mock(UserIdentity.class);
    when(mockUserStore.getUserIdentity("serviceName")).thenReturn(mockUserIdentity);

    Pattern trustedProxyIpPattern = Pattern.compile("127\\.0\\.0\\.1");
    TrustedProxyAuthorizationService authorizationService = new TrustedProxyAuthorizationService();
    authorizationService._adminUserStore = mockUserStore;
    authorizationService._trustedProxyIpPattern = trustedProxyIpPattern;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

    UserIdentity userIdentity = authorizationService.getUserIdentity(mockRequest, "serviceName");
    assertNotNull(userIdentity);
    assertEquals(mockUserIdentity, userIdentity);
  }

  @Test
  public void testGetUserIdentityWithNonMatchingIP() {
    UserStore mockUserStore = mock(UserStore.class);
    Pattern trustedProxyIpPattern = Pattern.compile("127\\.0\\.0\\.1");
    TrustedProxyAuthorizationService authorizationService = new TrustedProxyAuthorizationService();
    authorizationService._adminUserStore = mockUserStore;
    authorizationService._trustedProxyIpPattern = trustedProxyIpPattern;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");

    UserIdentity userIdentity = authorizationService.getUserIdentity(mockRequest, "serviceName");
    assertNull(userIdentity);
  }

  @Test
  public void testGetUserIdentityWithNullIPPattern() {
    UserStore mockUserStore = mock(UserStore.class);
    UserIdentity mockUserIdentity = mock(UserIdentity.class);
    when(mockUserStore.getUserIdentity("serviceName")).thenReturn(mockUserIdentity);

    TrustedProxyAuthorizationService authorizationService = new TrustedProxyAuthorizationService();
    authorizationService._adminUserStore = mockUserStore;
    authorizationService._trustedProxyIpPattern = null;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);

    UserIdentity userIdentity = authorizationService.getUserIdentity(mockRequest, "serviceName");
    assertNotNull(userIdentity);
    assertEquals(mockUserIdentity, userIdentity);
  }

  @Test
  public void testGetUserIdentityWithNameHostSeparator() {
    UserStore mockUserStore = mock(UserStore.class);
    UserIdentity mockUserIdentity = mock(UserIdentity.class);
    when(mockUserStore.getUserIdentity("serviceName")).thenReturn(mockUserIdentity);

    Pattern trustedProxyIpPattern = Pattern.compile("127\\.0\\.0\\.1");
    TrustedProxyAuthorizationService authorizationService = new TrustedProxyAuthorizationService();
    authorizationService._adminUserStore = mockUserStore;
    authorizationService._trustedProxyIpPattern = trustedProxyIpPattern;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

    UserIdentity userIdentity = authorizationService.getUserIdentity(mockRequest, "serviceName/host");
    assertNotNull(userIdentity);
    assertEquals(mockUserIdentity, userIdentity);
  }

  @Test
  public void testGetUserIdentityNoMatchingService() {
    UserStore mockUserStore = mock(UserStore.class);
    when(mockUserStore.getUserIdentity("serviceName")).thenReturn(null);

    Pattern trustedProxyIpPattern = Pattern.compile("127\\.0\\.0\\.1");
    TrustedProxyAuthorizationService authorizationService = new TrustedProxyAuthorizationService();
    authorizationService._adminUserStore = mockUserStore;
    authorizationService._trustedProxyIpPattern = trustedProxyIpPattern;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

    UserIdentity userIdentity = authorizationService.getUserIdentity(mockRequest, "serviceName");
    assertNull(userIdentity);
  }

  @Test
  public void testGetUserIdentityEmptyServiceName() {
    UserStore mockUserStore = mock(UserStore.class);
    when(mockUserStore.getUserIdentity("")).thenReturn(null);

    Pattern trustedProxyIpPattern = Pattern.compile("127\\.0\\.0\\.1");
    TrustedProxyAuthorizationService authorizationService = new TrustedProxyAuthorizationService();
    authorizationService._adminUserStore = mockUserStore;
    authorizationService._trustedProxyIpPattern = trustedProxyIpPattern;

    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

    UserIdentity userIdentity = authorizationService.getUserIdentity(mockRequest, "");
    assertNull(userIdentity);
  }
}