package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.dto.TaskExpertCountDTO;
import com.cardo.demojava.dto.TaskPageResultDto;
import com.cardo.demojava.dto.TaskStatusCountDTO;
import com.cardo.demojava.entity.*;
import com.cardo.demojava.mapper.CondtionMapper;
import com.cardo.demojava.mapper.ResourceMapper;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.mapper.TaskResultMapper;
import com.cardo.demojava.service.TaskService;
import com.cardo.demojava.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    TaskResultMapper taskResultMapper;
    @Override
    public Response<IPage<Task>> queryTasks(Page<Task> pagination, String taskName, Integer status,String id) {
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(taskName != null && !taskName.isEmpty()) {
            taskLambdaQueryWrapper.like(Task::getTaskName, taskName);
        }
        // 状态判断优化
        if (status != null && status != 6) {
            taskLambdaQueryWrapper.eq(Task::getStatus, status);
        }
        Page<Task> taskPage = taskMapper.selectPage(pagination, taskLambdaQueryWrapper);
        List<Task> records = taskPage.getRecords();
        if(id!=null){
            // 过滤任务列表，只保留用户参与的任务
            records = records.stream().filter(task -> {
                LambdaQueryWrapper<TaskResult> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TaskResult::getUserId, id)
                           .eq(TaskResult::getTaskId, task.getId())
                        .eq(TaskResult::getScore,-1);
                return taskResultMapper.selectCount(queryWrapper) > 0;
            }).collect(Collectors.toList());
            taskPage.setRecords(records);
        }
        return Response.ok(taskPage);
    }
    @Override
    public Response<IPage<TaskPageResultDto>> queryTasksResult(Page<Task> pagination, String taskName, Integer status, String id) {
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(taskName != null && !taskName.isEmpty()) {
            taskLambdaQueryWrapper.like(Task::getTaskName, taskName);
        }
        // 状态判断优化
        if (status != null && status != 6) {
            taskLambdaQueryWrapper.eq(Task::getStatus, status);
        }
        if(id!=null){
           taskLambdaQueryWrapper.eq(Task::getUserId,id);
        }
        Page<Task> taskPage = taskMapper.selectPage(pagination, taskLambdaQueryWrapper);

        // 转换为 TaskPageResultDto
        List<TaskPageResultDto> taskPageResultDtos = taskPage.getRecords().stream().map(task -> {
            TaskPageResultDto dto = new TaskPageResultDto();
            // 复制基本属性
            dto.setId(task.getId());
            dto.setTaskName(task.getTaskName());
            dto.setConditionId(task.getConditionId());
            dto.setUserId(task.getUserId());
            dto.setSiphonTime(task.getSiphonTime());
            dto.setStartTime(task.getStartTime());
            dto.setEndTime(task.getEndTime());
            dto.setResultScore(task.getResultScore());
            dto.setStatus(task.getStatus());

            // 设置 number 字段为 TaskResult 的计数
            LambdaQueryWrapper<TaskResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TaskResult::getTaskId, task.getId());
            Integer count = taskResultMapper.selectCount(queryWrapper);
            dto.setNumber(String.valueOf(count));

            return dto;
        }).collect(Collectors.toList());

        // 创建新的分页对象
        Page<TaskPageResultDto> resultPage = new Page<>(taskPage.getCurrent(), taskPage.getSize(), taskPage.getTotal());
        resultPage.setRecords(taskPageResultDtos);

        return Response.ok(resultPage);
    }

    @Override
    public Response<IPage<Task>> queryTasksTeacher(Page<Task> pagination, String taskName, Integer status, String id) {
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(taskName != null && !taskName.isEmpty()) {
            taskLambdaQueryWrapper.like(Task::getTaskName, taskName);
        }
        // 状态判断优化
        if (status != null && status != 6) {
            taskLambdaQueryWrapper.eq(Task::getStatus, status);
        }
        if(id!=null){
            taskLambdaQueryWrapper.eq(Task::getUserId,id);
        }
        Page<Task> taskPage = taskMapper.selectPage(pagination, taskLambdaQueryWrapper);
        return Response.ok(taskPage);
    }

    @Override
    public Response<String> add(Task task,String userId) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
        task.setConditionId(snowflakeIdGenerator.nextIdAsString());
        task.setUserId(userId);
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

        taskResultMapper.delete(new LambdaQueryWrapper<TaskResult>().eq(TaskResult::getTaskId,task.getId()));
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

    @Override
    public Response<List<TaskExpertCountDTO>> queryTasksWithExpertCount() {
        // 查询状态为3、4、5的任务（抽取状态、评审状态、评审完成）
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Task::getStatus, 3, 4, 5);
        List<Task> tasks = taskMapper.selectList(queryWrapper);
        
        // 构建结果列表
        List<TaskExpertCountDTO> resultList = new ArrayList<>();
        
        // 对每个任务，查询对应的专家人数
        for (Task task : tasks) {
            LambdaQueryWrapper<TaskResult> resultQueryWrapper = new LambdaQueryWrapper<>();
            resultQueryWrapper.eq(TaskResult::getTaskId, task.getId());
            Integer expertCount = taskResultMapper.selectCount(resultQueryWrapper);
            
            // 创建DTO并添加到结果列表
            TaskExpertCountDTO dto = new TaskExpertCountDTO();
            dto.setTaskName(task.getTaskName());
            dto.setExpertCount(expertCount);
            resultList.add(dto);
        }
        
        return Response.ok(resultList);
    }

    @Override
    public Response<TaskStatusCountDTO> getTaskStatusCount() {
        TaskStatusCountDTO countDTO = new TaskStatusCountDTO();
        
        // 查询状态为1的任务数量（初始化）
        LambdaQueryWrapper<Task> status1Query = new LambdaQueryWrapper<>();
        status1Query.eq(Task::getStatus, 1);
        countDTO.setStatus1(taskMapper.selectCount(status1Query));
        
        // 查询状态为2的任务数量（资源准备完成）
        LambdaQueryWrapper<Task> status2Query = new LambdaQueryWrapper<>();
        status2Query.eq(Task::getStatus, 2);
        countDTO.setStatus2(taskMapper.selectCount(status2Query));
        
        // 查询状态为3的任务数量（抽取状态）
        LambdaQueryWrapper<Task> status3Query = new LambdaQueryWrapper<>();
        status3Query.eq(Task::getStatus, 3);
        countDTO.setStatus3(taskMapper.selectCount(status3Query));
        
        // 查询状态为4的任务数量（评审状态）
        LambdaQueryWrapper<Task> status4Query = new LambdaQueryWrapper<>();
        status4Query.eq(Task::getStatus, 4);
        countDTO.setStatus4(taskMapper.selectCount(status4Query));
        
        // 查询状态为5的任务数量（评审完成）
        LambdaQueryWrapper<Task> status5Query = new LambdaQueryWrapper<>();
        status5Query.eq(Task::getStatus, 5);
        countDTO.setStatus5(taskMapper.selectCount(status5Query));
        
        return Response.ok(countDTO);
    }
}
