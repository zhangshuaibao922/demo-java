package com.cardo.demojava.service;

import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.entity.TaskResult;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.CondtionMapper;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.mapper.TaskResultMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.dto.Expert;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskScheduleService {
    
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private CondtionMapper condtionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExpertService expertService;
    @Autowired
    private UserFilterService userFilterService;
    @Autowired
    private TaskResultMapper taskResultMapper;

    @Scheduled(fixedDelay = 3600000) 
    @Transactional
    public void checkAndFilterUsers() {
        long currentTimestamp = System.currentTimeMillis();
        long futureTimestamp = currentTimestamp + 3600000; // 1小时后的时间戳
        String currentTime = String.valueOf(currentTimestamp);
        String futureTime = String.valueOf(futureTimestamp);
        
        List<Task> tasks = taskMapper.selectList(
            new LambdaQueryWrapper<Task>()
                .between(Task::getSiphonTime, currentTime, futureTime)
                .eq(Task::getStatus, 1)
                .isNotNull(Task::getConditionId)
        );
        for (Task task : tasks) {
            processTask(task);
        }
    }

    private void processTask(Task task) {
        try {
            // 获取条件列表
            List<Condtion> conditions = condtionMapper.selectList(
                new LambdaQueryWrapper<Condtion>()
                    .eq(Condtion::getId, task.getConditionId())
            );
            if (conditions == null || conditions.isEmpty()) {
                return;
            }

            // 获取用户数据
            List<User> allUsers = userMapper.selectList(null);
            List<Expert> expertMap = expertService.getAllExpert();

            // 执行筛选
            List<User> filteredUsers = userFilterService.filterUsers(conditions, allUsers, expertMap);

            // 更新任务状态并保存结果
            task.setStatus(2); // 设置为抽取状态
            taskMapper.updateById(task);

            // 保存筛选结果到TaskResult
            for (User user : filteredUsers) {
                TaskResult taskResult = new TaskResult();
                taskResult.setTaskId(task.getId());
                taskResult.setUserId(user.getId());
                taskResultMapper.insert(taskResult);
            }
            
        } catch (Exception e) {
            // 记录错误日志
            task.setStatus(0); // 设置为废弃状态
            taskMapper.updateById(task);
            throw e;
        }
    }
} 