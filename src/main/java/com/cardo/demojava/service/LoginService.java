package com.cardo.demojava.service;

import com.cardo.demojava.dto.Login;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;

public interface LoginService {
    Response<User> login(Login login);
}
