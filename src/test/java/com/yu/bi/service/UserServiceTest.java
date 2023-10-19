package com.yu.bi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
*/
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "xiaoming";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String userName = "小明";
        String userAvatar = "";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword,userName,userAvatar);
            Assertions.assertEquals(-1, result);
//            userAccount = "yu";
//            result = userService.userRegister(userAccount, userPassword, checkPassword,userName,userAvatar);
//            Assertions.assertEquals(-1, result);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
