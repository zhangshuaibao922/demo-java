package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.dto.TaskPageResultDto;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Task;

import java.util.List;


public interface TaskService extends IService<Task> {
    Response<IPage<Task>> queryTasks(Page<Task> pagination, String taskName, Integer status,String id);

    Response<String> add(Task task,String userId);

    Response<String> deleteAll(List<Task> tasks);

    Response<String> delete(String id);

    Response<Task> update(Task task);

    Response<IPage<TaskPageResultDto>> queryTasksResult(Page<Task> pagination, String taskName, Integer status, String id);
}
