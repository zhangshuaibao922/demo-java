package com.cardo.demojava.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.dto.TaskResultDto;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.TaskResult;

public interface TaskResultService extends IService<TaskResult> {
    
    Response<IPage<TaskResultDto>> queryTaskResultDtos(Page<TaskResult> pagination, String taskId, String name, Integer fieldId);

    Response<Integer> getTaskResultDtoCount(String taskId);

    Response<String> deleteTaskResult(String id);

    Response<String> deleteAllTaskResult(List<TaskResultDto> taskResultsDtos);

    Response<String> add(TaskResult taskResult);

    Response<String> update(TaskResult taskResult);
}
