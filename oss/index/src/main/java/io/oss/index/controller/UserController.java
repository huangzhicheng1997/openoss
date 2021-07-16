package io.oss.index.controller;

import io.oss.index.dto.Result;
import io.oss.index.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oss")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public Result login(String userName, String pwd) {
        return Result.success(userService.login(userName, pwd));
    }
}
