package com.cardo.demojava.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.dto.Login;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;

public interface LoginService extends IService<Login> {
    Response<User> login(Login login);
}
