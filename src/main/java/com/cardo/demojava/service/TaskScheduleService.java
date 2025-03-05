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

import java.util.List;

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

    //TODO每小时执行一次1
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
                .eq(Task::getStatus, 2)
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
                    .eq(Condtion::getConditionId, task.getConditionId())
            );
            if (conditions == null || conditions.isEmpty()) {
                return;
            }

            // 获取用户数据
            List<User> allUsers = userMapper.selectList(null);
            List<Expert> expertMap = expertService.getAllExpert();

            // 执行筛选
            List<User> filteredUsers = userFilterService.filterUsers(conditions, allUsers, expertMap);
            
            // 处理人数限制条件
            Integer requiredPersonCount = null;
            for (Condtion condition : conditions) {
                if ("person".equals(condition.getConditionName()) && "=".equals(condition.getConditionIf())) {
                    try {
                        requiredPersonCount = Integer.parseInt(condition.getConditionValue());
                        break;
                    } catch (NumberFormatException e) {
                        // 忽略无效的数值
                    }
                }
            }
            
            // 如果有人数限制条件
            if (requiredPersonCount != null && requiredPersonCount > 0) {
                // 如果筛选出的人数不够
                if (filteredUsers.size() < requiredPersonCount) {
                    // 从未筛选出的用户中随机抽取补足
                    List<User> remainingUsers = new java.util.ArrayList<>(allUsers);
                    remainingUsers.removeAll(filteredUsers);
                    
                    // 随机打乱剩余用户列表
                    java.util.Collections.shuffle(remainingUsers);
                    
                    // 添加需要的用户数量
                    int needMore = requiredPersonCount - filteredUsers.size();
                    for (int i = 0; i < needMore && i < remainingUsers.size(); i++) {
                        filteredUsers.add(remainingUsers.get(i));
                    }
                } 
                // 如果筛选出的人数超过要求
                else if (filteredUsers.size() > requiredPersonCount) {
                    // 随机打乱并只保留需要的数量
                    java.util.Collections.shuffle(filteredUsers);
                    filteredUsers = filteredUsers.subList(0, requiredPersonCount);
                }
            }

            // 更新任务状态并保存结果
            task.setStatus(3); // 设置为抽取状态
            taskMapper.updateById(task);

            // 保存筛选结果到TaskResult
            for (User user : filteredUsers) {
                TaskResult taskResult = new TaskResult();
                taskResult.setTaskId(task.getId());
                taskResult.setUserId(user.getId());
                taskResult.setUserName(user.getName());
                taskResult.setFieldId(user.getFieldId());
                taskResult.setScore(-1);
                taskResult.setDescription("暂无评价");
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