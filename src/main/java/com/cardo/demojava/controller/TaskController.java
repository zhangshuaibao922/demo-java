package com.cardo.demojava.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.service.TaskService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * ;(user)表控制层
 * @author :
 * @date : 2024-12-27
 */
@Api(tags = "任务功能接口")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

     @GetMapping("/page")
     public Response<IPage<Task>> queryTasks(
             @RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(required = false) String taskName,
             @RequestParam(required = false) Integer status) {

         // 构建分页参数
         Page<Task> pagination = new Page<>(page, size);
         return taskService.queryTasks(pagination,taskName,status);
     }

     //新增
     @PostMapping("/add")
     public Response<String> addTask(@RequestBody Task task) {
         return taskService.add(task);
     }

     @PostMapping("/delete/all")
     public Response<String> deleteAllUser(@RequestBody List<Task> tasks) {
         return null;
     }

     //删除
     @GetMapping("/delete/{id}")
     public Response<String> deleteUser(@PathVariable String id) {
         return null;
     }
     //修改
     @PostMapping("/update")
     public Response<Task> updateUser(@RequestBody Task tasks) {
         return null;
     }
}