java
package org.appfuse.service.impl;

import org.appfuse.dao.UserDao;
import org.appfuse.model.User;
import org.appfuse.service.UserExistsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserManagerImplTest {

    @InjectMocks
    private UserManagerImpl userManager;

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userManager.setDao(userDao);
    }

    @Test
    public void testSaveUserNewUser() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userDao.saveUser(user)).thenReturn(user);
        when(passwordEncoder.encodePassword(user.getPassword(), null)).thenReturn("encodedPassword");

        User savedUser = userManager.saveUser(user);

        assertEquals("testuser", savedUser.getUsername());
        verify(passwordEncoder).encodePassword("password", null);
        verify(userDao).saveUser(user);
    }

    @Test
    public void testSaveUserExistingUserPasswordChanged() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("newpassword");
        user.setEmail("test@example.com");
        user.setVersion(1L);

        when(userDao.getUserPassword("testuser")).thenReturn("oldpassword");
        when(userDao.saveUser(user)).thenReturn(user);
        when(passwordEncoder.encodePassword(user.getPassword(), null)).thenReturn("encodedPassword");

        User savedUser = userManager.saveUser(user);

        assertEquals("testuser", savedUser.getUsername());
        verify(passwordEncoder).encodePassword("newpassword", null);
        verify(userDao).saveUser(user);
    }

    @Test
    public void testSaveUserExistingUserPasswordNotChanged() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setVersion(1L);

        when(userDao.getUserPassword("testuser")).thenReturn("password");
        when(userDao.saveUser(user)).thenReturn(user);

        User savedUser = userManager.saveUser(user);

        assertEquals("testuser", savedUser.getUsername());
        verify(passwordEncoder, never()).encodePassword(anyString(), any());
        verify(userDao).saveUser(user);
    }

    @Test(expected = UserExistsException.class)
    public void testSaveUserUserExistsExceptionDataIntegrityViolation() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userDao.saveUser(user)).thenThrow(new DataIntegrityViolationException("duplicate"));

        userManager.saveUser(user);
    }

    @Test(expected = UserExistsException.class)
    public void testSaveUserUserExistsExceptionJpaSystemException() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userDao.saveUser(user)).thenThrow(new JpaSystemException(new DataIntegrityViolationException("duplicate")));

        userManager.saveUser(user);
    }

    @Test
    public void testSaveUserUsernameLowercase() throws UserExistsException {
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("password");

        when(userDao.saveUser(user)).thenReturn(user);
        when(passwordEncoder.encodePassword(user.getPassword(), null)).thenReturn("encodedPassword");

        User savedUser = userManager.saveUser(user);

        assertEquals("testuser", savedUser.getUsername());
        verify(userDao).saveUser(user);
    }
}