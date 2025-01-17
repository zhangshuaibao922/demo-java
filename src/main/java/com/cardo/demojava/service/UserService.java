package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.dto.UserDto;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;

import java.util.List;

/**
 * ;(user)表服务接口
 * @author : http://www.chiner.pro
 * @date : 2024-12-27
 */
public interface UserService extends IService<User> {

    Response<IPage<User>> queryUsers(Page<User> pagination, String name, String fieldName, String score);

    Response<String> addUser(User user);

    Response<String> deleteUser(String id);

    Response<User> updateUser(User user);

    Response<String> deleteAllUser(List<User> users);
}