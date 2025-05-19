package com.cardo.demojava.service;

import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.Task;
import com.cardo.demojava.entity.TaskResult;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.CondtionMapper;
import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.mapper.TaskResultMapper;
import com.cardo.demojava.mapper.UserMapper;

import com.cardo.demojava.util.SendMailUtils;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.dto.Expert;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Autowired
    SendMailUtils sendMailUtils;

    //每小时执行一次，先执行抽取任务
    @Scheduled(fixedDelay = 3600000, initialDelay = 0) 
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
    //每小时执行一次，检查已完成评审的任务,给出得分
    @Scheduled(fixedDelay = 3600000, initialDelay = 600000)
    @Transactional
    public void checkAndFinishReview() {
        long currentTimestamp = System.currentTimeMillis();
        long pastTimestamp = currentTimestamp - 3600000; // 1小时前的时间戳
        String currentTime = String.valueOf(currentTimestamp);
        String pastTime = String.valueOf(pastTimestamp);
        
        // 查询状态为4（评审状态）且结束时间在一小时前到当前之间的任务
        List<Task> tasks = taskMapper.selectList(
            new LambdaQueryWrapper<Task>()
                .between(Task::getEndTime, pastTime, currentTime)
                .eq(Task::getStatus, 4)
        );
        
        log.info("开始检查已完成评审的任务，找到{}个需要结束评审的任务", tasks.size());
        
        for (Task task : tasks) {
            processReviewTask(task);
        }
    }

    public void processReviewTask(Task task) {
        // 查询该任务的所有TaskResult记录
        List<TaskResult> taskResults = taskResultMapper.selectList(
            new LambdaQueryWrapper<TaskResult>()
                .eq(TaskResult::getTaskId, task.getId())
        );
        
        // 计算平均分数，去掉最高分和最低分
        double totalScore = 0;
        int validResultCount = 0;
        
        // 找出有效的评分结果
        List<TaskResult> validResults = new ArrayList<>();
        for (TaskResult result : taskResults) {
            if (result.getScore() != null && result.getScore() >= 0) {
                validResults.add(result);
            }
        }
        
        if (validResults.size() >= 3) {  // 至少需要3个评分才能去掉最高分和最低分
            // 按分数排序
            TaskResult minScoreResult = null;
            TaskResult maxScoreResult = null;
            int minScore = Integer.MAX_VALUE;
            int maxScore = Integer.MIN_VALUE;
            
            // 找出最高分和最低分
            for (TaskResult result : validResults) {
                if (result.getScore() < minScore) {
                    minScore = result.getScore();
                    minScoreResult = result;
                }
                if (result.getScore() > maxScore) {
                    maxScore = result.getScore();
                    maxScoreResult = result;
                }
            }
            
            // 更新最高分和最低分对应用户的score+1
            if (minScoreResult != null) {
                User minScoreUser = userMapper.selectById(minScoreResult.getUserId());
                if (minScoreUser != null) {
                    minScoreUser.setScore(minScoreUser.getScore() + 1);
                    userMapper.updateById(minScoreUser);
                }
            }
            
            if (maxScoreResult != null) {
                User maxScoreUser = userMapper.selectById(maxScoreResult.getUserId());
                if (maxScoreUser != null) {
                    maxScoreUser.setScore(maxScoreUser.getScore() + 1);
                    userMapper.updateById(maxScoreUser);
                }
            }
            
            // 计算去掉最高分和最低分后的总分
            for (TaskResult result : validResults) {
                if (result != minScoreResult && result != maxScoreResult) {
                    totalScore += result.getScore();
                    validResultCount++;
                }
            }
        } else {
            // 如果评分数量少于3，则使用所有有效评分
            for (TaskResult result : validResults) {
                totalScore += result.getScore();
                validResultCount++;
            }
        }
        // 如果有有效评分，计算平均分并更新任务
        if (validResultCount > 0) {
            double averageScore = totalScore / validResultCount;
            task.setResultScore(averageScore);
        }
        
        // 更新任务状态为5（评审完成）
        task.setStatus(5);
        taskMapper.updateById(task);
        //发送通知告诉用户，这玩意有没有通过
        ArrayList<String> strings = new ArrayList<>();
        User user = userMapper.selectById(task.getUserId());
        strings.add(user.getEmail());
        // 创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(task.getTaskName()+"评审结果");
        if(task.getResultScore()>=6.00){
         message.setText(task.getTaskName()+"的最终得分为"+task.getResultScore()+",通过审核");
        }else {
            message.setText(task.getTaskName()+"的最终得分为"+task.getResultScore()+",没有通过");
        }
        sendMailUtils.sendEmail(strings,message.getSubject(),message.getText());

        log.info("任务ID: {}, 任务名称: {}, 评审人数: {}, 最终得分: {}", 
                task.getId(), task.getTaskName(), taskResults.size(), task.getResultScore());
    }

    //每小时执行一次，检查评审任务，在抽取任务之后执行
    @Scheduled(fixedDelay = 3600000, initialDelay = 300000)
    @Transactional
    public void checkAndStartReview() {
        long currentTimestamp = System.currentTimeMillis();
        long futureTimestamp = currentTimestamp + 3600000; // 1小时后的时间戳
        String currentTime = String.valueOf(currentTimestamp);
        String futureTime = String.valueOf(futureTimestamp);
        
        // 查询状态为3（抽取状态）且开始评审时间在当前到一小时后之间的任务
        List<Task> tasks = taskMapper.selectList(
            new LambdaQueryWrapper<Task>()
                .between(Task::getStartTime, currentTime, futureTime)
                .eq(Task::getStatus, 3)
        );
        // 记录日志
        log.info("开始检查评审任务，找到{}个需要开始评审的任务", tasks.size());
        
        // 对于每个任务，获取其对应的所有评审结果
        for (Task task : tasks) {
            // 查询该任务的所有TaskResult记录
            List<TaskResult> taskResults = taskResultMapper.selectList(
                new LambdaQueryWrapper<TaskResult>()
                    .eq(TaskResult::getTaskId, task.getId())
            );
            // 获取所有评审人的用户ID
            List<String> reviewerIds = taskResults.stream()
                .map(TaskResult::getUserId)
                .collect(Collectors.toList());
            
            // 获取所有评审人的用户信息
            List<User> userList = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                    .in(User::getId, reviewerIds)
            );

            List<String> emails = userList.stream().map(User::getEmail).collect(Collectors.toList());
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(task.getTaskName()+"开始评审");
            message.setText(task.getTaskName()+"的评审链接为"+"\n<a href=\"http://localhost:9999/\">点击这里</a>查看");
            sendMailUtils.sendEmail(emails,message.getSubject(),message.getText());
            // 发送系统通知
            // 记录该任务的评审人数
            log.info("任务ID: {}, 任务名称: {}, 评审人数: {}", 
                    task.getId(), task.getTaskName(), taskResults.size());
        }
        // 将这些任务的状态更新为4（评审状态）
        for (Task task : tasks) {
            task.setStatus(4);
            taskMapper.updateById(task);
        }
    }

    public void processTask(Task task) {
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