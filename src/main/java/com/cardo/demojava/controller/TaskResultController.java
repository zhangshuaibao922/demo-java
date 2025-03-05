package com.cardo.demojava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.dto.TaskResultDto;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.TaskResult;
import com.cardo.demojava.service.TaskResultService;


@RestController
@RequestMapping("/taskResult")
public class TaskResultController {

    @Autowired
    private TaskResultService taskResultService;

    /**
      * 分页查询
      * @param page 当前页码
      * @param size 每页记录数
      * @param taskId 任务id
      * @param name 用户名字(可选条件)
      * @param fieldName 领域(可选条件)
      * @return
      */
      @GetMapping("/page")
      public Response<IPage<TaskResultDto>> queryTaskResultDtos(
              @RequestParam(defaultValue = "1") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(required = true) String taskId,
              @RequestParam(required = false) String name,
              @RequestParam(required = false) String fieldName) {
 
          // 构建分页参数
          Page<TaskResult> pagination = new Page<>(page, size);
 
          // 调用服务层方法
          return taskResultService.queryTaskResultDtos(pagination,taskId, name, fieldName);
      }

           //删除
     @GetMapping("/delete/{id}")
     public Response<String> deleteTaskResult(@PathVariable String id) {
         return taskResultService.deleteTaskResult(id);
     }
     @PostMapping("/delete/all")
     public Response<String> deleteAllTaskResult(@RequestBody List<TaskResult> taskResults) {
         return taskResultService.deleteAllTaskResult(taskResults);
     }

           //获取用户总数
     @GetMapping("/count/{taskId}")
     public Response<Integer> getTaskResultDtoCount(@PathVariable String taskId) {
         return taskResultService.getTaskResultDtoCount(taskId);
     }

}
