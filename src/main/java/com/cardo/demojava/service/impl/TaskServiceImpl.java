package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.Resource;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.mapper.CondtionMapper;
import com.cardo.demojava.mapper.ResourceMapper;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.service.TaskService;
import com.cardo.demojava.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cardo.demojava.contant.Code.*;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    TaskMapper taskMapper;
    @Autowired
    ResourceMapper resourceMapper;
    @Autowired
    CondtionMapper condtionMapper;
    @Override
    public Response<IPage<Task>> queryTasks(Page<Task> pagination, String taskName, Integer status) {
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(taskName != null && !taskName.isEmpty()) {
            taskLambdaQueryWrapper.like(Task::getTaskName, taskName);
        }
        // 状态判断优化
        if (status != null && status != 6) {
            taskLambdaQueryWrapper.eq(Task::getStatus, status);
        }
        Page<Task> taskPage = taskMapper.selectPage(pagination, taskLambdaQueryWrapper);
        return Response.ok(taskPage);
    }

    @Override
    public Response<String> add(Task task) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        task.setConditionId(snowflakeIdGenerator.nextIdAsString());


        int insert = taskMapper.insert(task);
        if (insert > 0) {
            return Response.ok("OK");
        } else {
            return Response.error(ADD_FAIL);
        }
    }

    @Override
    public Response<String> deleteAll(List<Task> tasks) {
        List<String> collect = tasks.stream().map(Task::getId).collect(Collectors.toList());
        int i = taskMapper.deleteBatchIds(collect);
        //TODO删除全部资源
        if (i > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<String> delete(String id) {
        Task task = taskMapper.selectById(id);
        int i = taskMapper.deleteById(id);

        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceMapper.delete(resourceLambdaQueryWrapper.eq(Resource::getTaskId,id));

        LambdaQueryWrapper<Condtion> condtionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        condtionMapper.delete(condtionLambdaQueryWrapper.eq(Condtion::getConditionId,task.getConditionId()));

        if (i > 0) {
            return Response.ok("OK");
        } else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<Task> update(Task task) {
        Task task1 = taskMapper.selectById(task.getId());
        if(task1.getStatus() == 1) {
            task.setStatus(2);
            taskMapper.updateById(task);
            return Response.ok(task);
        }else{
            return Response.error(UPDATE_FAIL);
        }
    }
}
