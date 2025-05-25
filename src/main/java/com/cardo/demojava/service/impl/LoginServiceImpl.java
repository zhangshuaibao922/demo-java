package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.dto.Login;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.LoginService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cardo.demojava.contant.Code.*;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Response<User> login(Login login) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getAccount,login.getAccount());
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if(user == null){
            return Response.error(NONE);
        }
        String encryptedPassword = DigestUtils.md5Hex(login.getPassword());
        if(encryptedPassword.equals(user.getPassword())){
            return Response.ok(user);
        }else {
            return Response.error(PASSWORD_FAIL);
        }
    }
}
