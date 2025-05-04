package com.cardo.demojava.controller;

import com.cardo.demojava.dto.Login;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.service.LoginService;
import com.cardo.demojava.service.UserService;
import com.cardo.demojava.service.VerificationCodeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    UserService userService;
    @Autowired
    VerificationCodeService codeService;
    @PostMapping("/login")
    public Response<User> login(@RequestBody Login login) {
        return loginService.login(login);
    }

    @PostMapping("/create/{code}")
    public Response<String> create(@RequestBody User user, @PathVariable String code){
        return userService.create(user,code);
    }

    @GetMapping("/code/{email}")
    public Response<Boolean> code(@PathVariable String email){
        codeService.sendVerificationCode(email);
        return Response.ok(true);
    }
}
