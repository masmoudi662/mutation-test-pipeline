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

import static org.junit.Assert.*;
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
    }

    @Test
    public void testSaveUserNewUser() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userDao.saveUser(user)).thenReturn(user);
        when(passwordEncoder.encodePassword("password", null)).thenReturn("encodedPassword");

        User savedUser = userManager.saveUser(user);

        assertEquals("testuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userDao).saveUser(user);
        verify(passwordEncoder).encodePassword("password", null);
    }

    @Test(expected = UserExistsException.class)
    public void testSaveUserExistingUserException() throws UserExistsException {
        User user = new User();
        user.setUsername("existinguser");

        when(userDao.saveUser(user)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        userManager.saveUser(user);
    }

    @Test
    public void testGetUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userDao.get(1L)).thenReturn(user);

        User retrievedUser = userManager.getUser("1");

        assertEquals("testuser", retrievedUser.getUsername());
    }

    @Test
    public void testRemoveUser() {
        userManager.removeUser("1");
        verify(userDao).remove(1L);
    }

    @Test
    public void testSaveUserJpaSystemException() throws UserExistsException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userDao.saveUser(user)).thenThrow(new JpaSystemException(new DataIntegrityViolationException("")));

        try {
            userManager.saveUser(user);
            fail("Expected UserExistsException");
        } catch (UserExistsException e) {
            assertEquals("User 'testuser' already exists!", e.getMessage());
        }
    }
}