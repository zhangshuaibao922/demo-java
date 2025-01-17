package com.cardo.demojava.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.dto.UserDto;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Role;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.mapper.RoleMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    @Autowired
    private RoleMapper roleMapper;


    @Override
     public Response<IPage<User>> queryUsers(Page<User> pagination, String name, String fieldName, String score) {
         // 创建查询条件
         LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
         if (name != null && !name.trim().isEmpty()) {
             queryWrapper.eq(User::getName, name);
         }
         if (fieldName != null && !fieldName.trim().isEmpty()) {
             LambdaQueryWrapper<Field> fieldLambdaQueryWrapper = new LambdaQueryWrapper<>();
             fieldLambdaQueryWrapper.like(Field::getFieldName, fieldName);
             List<Field> fields = fieldMapper.selectList(fieldLambdaQueryWrapper);
             for (Field field : fields) {
                 queryWrapper.like(User::getFieldId, field.getFieldId());
             }
         }

         if(score != null) {
             queryWrapper.ge(User::getScore,Double.valueOf(score));
         }
         // 执行分页查询
         Page<User> userPage = userMapper.selectPage(pagination, queryWrapper);
//         List<Field> fields = fieldMapper.selectList(null);
//        List<Role> roles = roleMapper.selectList(null);
//        ArrayList<UserDto> userDtos = new ArrayList<>();
//         for (User record : userPage.getRecords()) {
//             UserDto userDto = new UserDto();
//             BeanUtils.copyProperties(record, userDto);
//             for (Field field : fields) {
//                 if (Objects.equals(field.getFieldId(), record.getFieldId())) {
//                     userDto.setFieldName(field.getFieldName());
//                 }
//             }
//             for (Role role : roles) {
//                 if (role.getRoleId().equals(record.getRoleId())) {
//                     userDto.setRoleName(role.getRoleName());
//                 }
//             }
//             userDtos.add(userDto);
//         }
//        Page<UserDto> userDtoPage = new Page<>();
//        BeanUtils.copyProperties(userPage, userDtoPage);
//        userDtoPage.setRecords(userDtos);
         return Response.ok(userPage);
     }

    @Override
    public Response<String> addUser(User user) {
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
}