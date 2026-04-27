java
package com.emix.dubai.business.service.system;

import com.emix.dubai.business.entity.system.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEntryptPassword() {
        User user = new User();
        String plainPassword = "testPassword";

        userService.entryptPassword(user, plainPassword);

        assertNotNull(user.getSalt());
        assertNotNull(user.getPassword());
        assertEquals(16, user.getSalt().length()/2);
        assertEquals(40, user.getPassword().length()/2);
    }

    @Test
    public void testEntryptPassword_differentPassword() {
        User user = new User();
        String plainPassword = "anotherPassword";

        userService.entryptPassword(user, plainPassword);

        assertNotNull(user.getSalt());
        assertNotNull(user.getPassword());
        assertEquals(16, user.getSalt().length()/2);
        assertEquals(40, user.getPassword().length()/2);
    }

    @Test
    public void testEntryptPassword_emptyPassword() {
        User user = new User();
        String plainPassword = "";

        userService.entryptPassword(user, plainPassword);

        assertNotNull(user.getSalt());
        assertNotNull(user.getPassword());
        assertEquals(16, user.getSalt().length()/2);
        assertEquals(40, user.getPassword().length()/2);
    }

    @Test
    public void testEntryptPassword_nullPassword() {
        User user = new User();
        String plainPassword = null;

        userService.entryptPassword(user, plainPassword);

        assertNotNull(user.getSalt());
        assertNotNull(user.getPassword());
        assertEquals(16, user.getSalt().length()/2);
        assertEquals(40, user.getPassword().length()/2);
    }
}