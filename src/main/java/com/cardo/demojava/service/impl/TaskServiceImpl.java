package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cardo.demojava.contant.Code.*;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    TaskMapper taskMapper;
    @Override
    public Response<IPage<Task>> queryTasks(Page<Task> pagination, String taskName, Integer status) {
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(taskName != null && !taskName.isEmpty()) {
            taskLambdaQueryWrapper.like(Task::getTaskName, taskName);
        }
        // 状态判断优化
        if (status != null && status != 5) {
            taskLambdaQueryWrapper.eq(Task::getStatus, status);
        }
        Page<Task> taskPage = taskMapper.selectPage(pagination, taskLambdaQueryWrapper);
        return Response.ok(taskPage);
    }

    @Override
    public Response<String> add(Task task) {
        //将resouceid放在上传的时候确定
//        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
//        task.setConditionId(snowflakeIdGenerator.nextIdAsString());
        int insert = taskMapper.insert(task);
        if (insert > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(ADD_FAIL);
        }
    }

    @Override
    public Response<String> deleteAll(List<Task> tasks) {
        List<String> collect = tasks.stream().map(Task::getId).collect(Collectors.toList());
        int i = taskMapper.deleteBatchIds(collect);
        if (i > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<String> delete(String id) {
        int i = taskMapper.deleteById(id);
        if (i > 0) {
            return Response.ok("OK");
        } else {
            return Response.error(DELETE_FAIL);
        }
    }
}
