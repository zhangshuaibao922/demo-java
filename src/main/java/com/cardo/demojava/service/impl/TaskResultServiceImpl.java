package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.dto.TaskResultDto;
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
import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public Response<IPage<TaskResultDto>> queryTaskResultDtos(Page<TaskResult> pagination, String taskId, String name,
            String fieldName) {
        LambdaQueryWrapper<TaskResult> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(TaskResult::getUserName, name);
        }

        if (fieldName != null && !fieldName.trim().isEmpty()) {
            LambdaQueryWrapper<Field> fieldLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fieldLambdaQueryWrapper.like(Field::getFieldName, fieldName);
            List<Field> fields = fieldMapper.selectList(fieldLambdaQueryWrapper);
            for (Field field : fields) {
                queryWrapper.eq(TaskResult::getFieldId, field.getFieldId());
            }
        }
        queryWrapper.eq(TaskResult::getTaskId, taskId);
        Page<TaskResult> taskResultPage = taskResultMapper.selectPage(pagination, queryWrapper);
        List<TaskResult> taskResults = taskResultPage.getRecords();
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
        int delete = taskResultMapper.deleteById(id);
        if(delete > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<String> deleteAllTaskResult(List<TaskResult> taskResults) {
        List<String> ids = taskResults.stream().map(TaskResult::getId).collect(Collectors.toList());
        int delete = taskResultMapper.deleteBatchIds(ids);
        if(delete > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }
}
