package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Task;


public interface TaskService extends IService<Task> {
    Response<IPage<Task>> queryTasks(Page<Task> pagination, String taskName, Integer status);

    Response<String> add(Task task);
}
