package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.dto.FieldUserCountDTO;
import com.cardo.demojava.dto.PasswordDTO;
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

    Response<String> create(User user,String code);
    
    /**
     * 获取每个领域的用户数量
     * @return 包含每个领域名称和用户数量的列表
     */
    Response<List<FieldUserCountDTO>> getFieldUserCount();
    
    /**
     * 修改用户密码
     * @param passwordDTO 包含用户ID、旧密码和新密码的对象
     * @return 修改结果
     */
    Response<String> changePassword(PasswordDTO passwordDTO);
}