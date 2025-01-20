package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}
