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

import com.aliyuncs.ecs.model.v20140526.DescribeTasksResponse.Task;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.dto.TaskResultDto;
import com.cardo.demojava.dto.UserSelectCountDTO;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.TaskResult;
import com.cardo.demojava.service.TaskResultService;
import io.swagger.annotations.ApiOperation;


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
      * @param fieldId 领域id(可选条件)
      * @return
      */
      @GetMapping("/page")
      public Response<IPage<TaskResultDto>> queryTaskResultDtos(
              @RequestParam(defaultValue = "1") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(required = true) String taskId,
              @RequestParam(required = false) String name,
              @RequestParam(required = false) Integer fieldId) {
 
          // 构建分页参数
          Page<TaskResult> pagination = new Page<>(page, size);
 
          // 调用服务层方法
          return taskResultService.queryTaskResultDtos(pagination,taskId, name, fieldId);
      }

      @GetMapping("/one/{taskId}/{id}")
      public Response<TaskResult> queryOne(@PathVariable String taskId,@PathVariable String id){
          return taskResultService.queryOne(taskId,id);
      }
      //新增
     @PostMapping("/add/{taskId}/{userId}")
     public Response<String> addTaskResult(@PathVariable String taskId, @PathVariable String userId) {
         return taskResultService.add(taskId, userId);
     }
     

     //修改
     @PostMapping("/update")
     public Response<String> updateTaskResult(@RequestBody TaskResult taskResult) {
         return taskResultService.update(taskResult);
     }

      //删除
     @GetMapping("/delete/{id}")
     public Response<String> deleteTaskResult(@PathVariable String id) {
         return taskResultService.deleteTaskResult(id);
     }
     //删除全部
     @PostMapping("/delete/all")
     public Response<String> deleteAllTaskResult(@RequestBody List<TaskResultDto> taskResultsDtos) {
         return taskResultService.deleteAllTaskResult(taskResultsDtos);
     }

    //获取用户总数
     @GetMapping("/count/{taskId}")
     public Response<Integer> getTaskResultDtoCount(@PathVariable String taskId) {
         return taskResultService.getTaskResultDtoCount(taskId);
     }

    @GetMapping("/top-selected-users")
    @ApiOperation("获取被抽中次数最多的前10名用户")
    public Response<List<UserSelectCountDTO>> getTopSelectedUsers() {
        return taskResultService.getTopSelectedUsers();
    }

}
