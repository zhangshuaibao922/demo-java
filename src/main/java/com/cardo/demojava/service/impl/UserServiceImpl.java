package com.cardo.demojava.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.dto.FieldUserCountDTO;
import com.cardo.demojava.dto.PasswordDTO;
import com.cardo.demojava.dto.UserVo;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.UserService;
import com.cardo.demojava.service.VerificationCodeService;
import com.cardo.demojava.util.SendMailUtils;
import com.cardo.demojava.util.SnowflakeIdGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    VerificationCodeService codeService;



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

         if(score != null && !score.trim().isEmpty()) {
             // 根据文本标签设置分数区间条件
             switch (score) {
                 case "优秀":
                     queryWrapper.le(User::getScore, 5.0); // 小于等于5分
                     break;
                 case "良好":
                     queryWrapper.gt(User::getScore, 5.0).le(User::getScore, 15.0); // 5分到15分
                     break;
                 case "一般":
                     queryWrapper.gt(User::getScore, 15.0).le(User::getScore, 25.0); // 15分到25分
                     break;
                 case "及格":
                     queryWrapper.gt(User::getScore, 25.0); // 大于25分
                     break;
                 default:
                     // 如果是数字，则按原来逻辑处理
                     try {
                         Double scoreValue = Double.valueOf(score);
                         queryWrapper.ge(User::getScore, scoreValue);
                     } catch (NumberFormatException e) {
                         // 忽略无效输入
                     }
                     break;
             }
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
        String encryptedPassword = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(encryptedPassword);
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
        String encryptedPassword = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(encryptedPassword);
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
        //TODO 删除关联关系
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

    @Override
    public Response<List<UserVo>> getUserByUsername(String username) {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().like(User::getName, username));
        if(users.size() > 0) {
            return Response.ok(users.stream().map(user -> {
                UserVo userVo = new UserVo();
                userVo.setId(user.getId());
                userVo.setName(user.getName());
                return userVo;
            }).collect(Collectors.toList()));
        }else {
            return Response.error(NONE);
        }
    }

    @Override
    public Response<String> create(User user,String code) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        String relationship = snowflakeIdGenerator.nextIdAsString();
        user.setRelationship(relationship);
        // 对密码进行MD5加密
        String encryptedPassword = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(encryptedPassword);
        boolean b = codeService.verifyCode(user.getEmail(), code);
        if(b){
            int insert = userMapper.insert(user);
            return Response.ok("OK");
        }else {
            return Response.error(ADD_FAIL);
        }
    }

    @Override
    public Response<List<FieldUserCountDTO>> getFieldUserCount() {
        // 获取所有领域
        List<Field> fields = fieldMapper.selectList(null);
        
        // 构建结果列表
        List<FieldUserCountDTO> resultList = new ArrayList<>();
        
        // 对每个领域，查询该领域的用户数量
        for (Field field : fields) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getFieldId, field.getFieldId());
            
            Integer userCount = userMapper.selectCount(queryWrapper);
            
            // 创建DTO并添加到结果列表
            FieldUserCountDTO dto = new FieldUserCountDTO(field.getFieldName(), userCount);
            resultList.add(dto);
        }
        
        return Response.ok(resultList);
    }
    
    @Override
    public Response<String> changePassword(PasswordDTO passwordDTO) {
        // 根据ID查询用户
        User user = userMapper.selectById(passwordDTO.getId());
        if (user == null) {
            return Response.error(NONE);
        }
        
        // 对传入的旧密码进行MD5加密
        String encryptedOldPassword = DigestUtils.md5Hex(passwordDTO.getPassword());
        
        // 验证旧密码是否正确
        if (!encryptedOldPassword.equals(user.getPassword())) {
            return Response.error(PASSWORD_FAIL);
        }
        
        // 对新密码进行MD5加密
        String encryptedNewPassword = DigestUtils.md5Hex(passwordDTO.getNewPassword());
        
        // 更新用户密码
        user.setPassword(encryptedNewPassword);
        int update = userMapper.updateById(user);
        
        if (update > 0) {
            return Response.ok("密码修改成功");
        } else {
            return Response.error(UPDATE_FAIL);
        }
    }
}