java
package net.petrikainulainen.spring.trenches.scheduling.job;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AuthenticationUtilTest {

    @Test
    public void configureAuthentication_ShouldSetAuthenticationWithCorrectRole() {
        String role = "ROLE_ADMIN";
        AuthenticationUtil.configureAuthentication(role);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(AuthenticationUtil.USERNAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(role, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role)));
    }

    @Test
    public void configureAuthentication_WithEmptyRole() {
        String role = "";
        AuthenticationUtil.configureAuthentication(role);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(AuthenticationUtil.USERNAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(role, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
    }

    @Test
    public void configureAuthentication_WithNullRole() {
        AuthenticationUtil.configureAuthentication(null);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(AuthenticationUtil.USERNAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().isEmpty());
    }

    @Test
    public void configureAuthentication_MultipleRoles() {
        String role = "ROLE_ADMIN,ROLE_USER";
        AuthenticationUtil.configureAuthentication(role);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(AuthenticationUtil.USERNAME, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(role, SecurityContextHolder.getContext().getAuthentication().getCredentials());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}