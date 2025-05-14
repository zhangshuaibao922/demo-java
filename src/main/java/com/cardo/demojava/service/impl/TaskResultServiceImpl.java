package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.dto.TaskResultDto;
import com.cardo.demojava.dto.UserSelectCountDTO;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.entity.TaskResult;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.mapper.TaskResultMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.TaskResultService;
import com.cardo.demojava.service.TaskScheduleService;
import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cardo.demojava.contant.Code.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskResultServiceImpl extends ServiceImpl<TaskResultMapper, TaskResult> implements TaskResultService {
    @Autowired
    private TaskResultMapper taskResultMapper;
    @Autowired
    private FieldMapper fieldMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    TaskScheduleService taskScheduleService;

    @Override
    public Response<IPage<TaskResultDto>> queryTaskResultDtos(Page<TaskResult> pagination, String taskId, String name,
            Integer fieldId) {
        LambdaQueryWrapper<TaskResult> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(TaskResult::getUserName, name);
        }

        if (fieldId != null) {
            queryWrapper.eq(TaskResult::getFieldId, fieldId);
        }
        queryWrapper.eq(TaskResult::getTaskId, taskId);
        Page<TaskResult> taskResultPage = taskResultMapper.selectPage(pagination, queryWrapper);
        List<TaskResult> taskResults = taskResultPage.getRecords();
        if(taskResults.size() == 0) {
            return Response.error(NONE);
        }
        List<TaskResultDto> taskResultDtos = new ArrayList<>();
        Task task = taskMapper.selectById(taskResults.get(0).getTaskId());

        for (TaskResult taskResult : taskResults) {
            TaskResultDto taskResultDto = new TaskResultDto();
            BeanUtils.copyProperties(taskResult, taskResultDto);
            taskResultDto.setTaskName(task.getTaskName());
            User user = userMapper.selectById(taskResult.getUserId());
            taskResultDto.setEmail(user.getEmail());
            Field field = fieldMapper.selectById(taskResult.getFieldId());
            taskResultDto.setFieldName(field.getFieldName());
            taskResultDtos.add(taskResultDto);
        }
        Page<TaskResultDto> dtoPage = new Page<>();
        dtoPage.setRecords(taskResultDtos);
        dtoPage.setTotal(taskResultPage.getTotal());
        dtoPage.setCurrent(taskResultPage.getCurrent());
        dtoPage.setSize(taskResultPage.getSize());
        
        return Response.ok(dtoPage);
    }

    @Override
    public Response<Integer> getTaskResultDtoCount(String taskId) {
        LambdaQueryWrapper<TaskResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskResult::getTaskId, taskId);
        Integer count = taskResultMapper.selectCount(queryWrapper);
        return Response.ok(count);
    }

    @Override
    public Response<String> deleteTaskResult(String id) {
        TaskResult result = taskResultMapper.selectById(id);
        int delete = taskResultMapper.deleteById(id);
        String taskId = result.getTaskId();
        Task task = taskMapper.selectById(taskId);
        taskScheduleService.processReviewTask(task);
        if(delete > 0) {
            return Response.ok("OK");
        }else { 
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<String> deleteAllTaskResult(List<TaskResultDto> taskResultsDtos) {
        List<String> ids = taskResultsDtos.stream().map(TaskResultDto::getId).collect(Collectors.toList());
        TaskResult result = taskResultMapper.selectById(ids.get(0));
        int delete = taskResultMapper.deleteBatchIds(ids);
        String taskId = result.getTaskId();
        Task task = taskMapper.selectById(taskId);
        taskScheduleService.processReviewTask(task);
        if(delete > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<String> add(String taskId, String userId) {
        TaskResult taskResult = new TaskResult();
        taskResult.setTaskId(taskId);
        taskResult.setUserId(userId);
        User user = userMapper.selectById(userId);
        taskResult.setUserName(user.getName());
        taskResult.setFieldId(user.getFieldId());
        taskResult.setScore(-1);
        taskResult.setDescription("暂无评价");
        int insert = taskResultMapper.insert(taskResult);
        if(insert > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(ADD_FAIL);
        }
    }

    @Override
    public Response<String> update(TaskResult taskResult) {
        TaskResult updateResult = taskResultMapper.selectOne(
                new LambdaQueryWrapper<TaskResult>()
                        .eq(TaskResult::getTaskId, taskResult.getTaskId())
                        .eq(TaskResult::getUserId, taskResult.getUserId()));
        updateResult.setScore(taskResult.getScore());
        updateResult.setDescription(taskResult.getDescription());
        int update = taskResultMapper.updateById(updateResult);
        if(update > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(UPDATE_FAIL);
        }
    }

    @Override
    public Response<TaskResult> queryOne(String taskId, String id) {
        TaskResult result = taskResultMapper.selectOne(
                new LambdaQueryWrapper<TaskResult>()
                        .eq(TaskResult::getTaskId, taskId)
                        .eq(TaskResult::getUserId, id));
        return Response.ok(result);
    }

    @Override
    public Response<List<UserSelectCountDTO>> getTopSelectedUsers() {
        // 使用QueryWrapper构建分组查询
        List<Map<String, Object>> countResults = taskResultMapper.selectMaps(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TaskResult>()
                .select("user_id", "user_name", "COUNT(*) as count")
                .groupBy("user_id", "user_name")
                .orderByDesc("COUNT(*)")
                .last("LIMIT 10")
        );
        
        // 转换为DTO列表
        List<UserSelectCountDTO> topUsers = new ArrayList<>();
        for (Map<String, Object> result : countResults) {
            // 安全处理可能的null值
            Object userIdObj = result.get("user_id");
            Object userNameObj = result.get("user_name");
            Object countObj = result.get("count");
            
            if (userIdObj != null && userNameObj != null && countObj != null) {
                String userId = userIdObj.toString();
                String userName = userNameObj.toString();
                Integer count = ((Number) countObj).intValue();
                topUsers.add(new UserSelectCountDTO(userId, userName, count));
            }
        }
        
        return Response.ok(topUsers);
    }
}
