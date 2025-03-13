package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.dto.UserVo;
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

    /**
     * 获取用户总数
     * @return 包含用户总数的响应对象
     */
    Response<Integer> getUserCount();

    Response<List<UserVo>> getUserByUsername(String username);
}