package com.cardo.demojava.controller;

import com.cardo.demojava.dto.Login;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.service.LoginService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ;(user)表控制层
 * @author :
 * @date : 2024-12-27
 */
@Api(tags = "登陆功能接口")
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/login")
    public Response<User> login(@RequestBody Login login) {
        return loginService.login(login);
    }
}
