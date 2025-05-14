package com.cardo.demojava.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.dto.FieldUserCountDTO;
import com.cardo.demojava.dto.UserVo;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * ;(user)表控制层
 * @author :
 * @date : 2024-12-27
 */
@Api(tags = "用户功能接口")
@RestController
@RequestMapping("/user")
public class UserController{
    @Autowired
    private UserService userService;


     /**
      * 分页查询
      * @param page 当前页码
      * @param size 每页记录数
      * @param name 名字(可选条件)
      * @param fieldName 领域(可选条件)
      * @param score 评分(可选条件)
      * @return
      */
     @GetMapping("/page")
     public Response<IPage<User>> queryUsers(
             @RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(required = false) String name,
             @RequestParam(required = false) String fieldName,
             @RequestParam(required = false) String  score) {

         // 构建分页参数
         Page<User> pagination = new Page<>(page, size);

         // 调用服务层方法
         return userService.queryUsers(pagination, name, fieldName,score);
     }

     //新增
     @PostMapping("/add")
     public Response<String> addUser(@RequestBody User user) {
         return userService.addUser(user);
     }

     @PostMapping("/delete/all")
     public Response<String> deleteAllUser(@RequestBody List<User> users) {
         return userService.deleteAllUser(users);
     }

     //删除
     @GetMapping("/delete/{id}")
     public Response<String> deleteUser(@PathVariable String id) {
         return userService.deleteUser(id);
     }
     //修改
     @PostMapping("/update")
     public Response<User> updateUser(@RequestBody User user) {
         return userService.updateUser(user);
     }

     @PostMapping("get/{name}")
     public Response<List<UserVo>> getUserByUsername(@PathVariable String name) {
         return userService.getUserByUsername(name);
     }

     //获取用户总数
     @GetMapping("/count")
     public Response<Integer> getUserCount() {
         return userService.getUserCount();
     }
     
     @GetMapping("/field-count")
     @ApiOperation("获取每个领域的用户数量")
     public Response<List<FieldUserCountDTO>> getFieldUserCount() {
         return userService.getFieldUserCount();
     }
}