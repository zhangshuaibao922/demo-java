package com.cardo.demojava.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.UserService;
import com.cardo.demojava.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cardo.demojava.contant.Code.*;

/**
 * ;(user)表服务实现类
 * @author : http://www.chiner.pro
 * @date : 2024-12-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FieldMapper fieldMapper;


    @Override
     public Response<IPage<User>> queryUsers(Page<User> pagination, String name, String fieldName, String score) {
         // 创建查询条件
         LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
         if (name != null && !name.trim().isEmpty()) {
             queryWrapper.like(User::getName, name);
         }
         if (fieldName != null && !fieldName.trim().isEmpty()) {
             LambdaQueryWrapper<Field> fieldLambdaQueryWrapper = new LambdaQueryWrapper<>();
             fieldLambdaQueryWrapper.like(Field::getFieldName, fieldName);
             List<Field> fields = fieldMapper.selectList(fieldLambdaQueryWrapper);
             for (Field field : fields) {
                 queryWrapper.eq(User::getFieldId, field.getFieldId());
             }
         }

         if(score != null) {
             queryWrapper.ge(User::getScore,Double.valueOf(score));
         }
         // 执行分页查询
         Page<User> userPage = userMapper.selectPage(pagination, queryWrapper);
         return Response.ok(userPage);
     }

    @Override
    public Response<String> addUser(User user) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        String relationship = snowflakeIdGenerator.nextIdAsString();
        user.setRelationship(relationship);
        int insert = userMapper.insert(user);
        if(insert > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(ADD_FAIL);
        }

    }

    @Override
    public Response<String> deleteUser(String id) {
         int delete = userMapper.deleteById(id);
        if(delete > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<User> updateUser(User user) {
         int update = userMapper.updateById(user);
         if(update > 0) {
             return Response.ok(user);
         }else {
             return Response.error(UPDATE_FAIL);
         }
    }

    @Override
    public Response<String> deleteAllUser(List<User> users) {
        List<String> collect = users.stream().map(User::getId).collect(Collectors.toList());
        int delete = userMapper.deleteBatchIds(collect);
        if(delete > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<Integer> getUserCount() {
        Integer count = userMapper.selectCount(null);
        return Response.ok(count);
    }
}