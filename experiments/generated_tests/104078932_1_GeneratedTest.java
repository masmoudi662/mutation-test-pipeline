java
package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void selectUser() {
        User user = userService.selectUser(1);
        Assert.assertNotNull(user);
    }

    @Test
    public void selectUser_nonExistentId() {
        User user = userService.selectUser(999);
        Assert.assertNull(user);
    }

    @Test
    public void selectUser_negativeId() {
        User user = userService.selectUser(-1);
        Assert.assertNull(user);
    }

    @Test
    public void selectUser_zeroId() {
        User user = userService.selectUser(0);
        Assert.assertNull(user);
    }
}