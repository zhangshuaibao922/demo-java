package com.cardo.demojava.service;

import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.TaskResult;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.TaskResultMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.dto.Expert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFilterService {
    @Autowired
    private TaskResultMapper taskResultMapper;
    
    public List<User> filterUsers(List<Condtion> conditions, List<User> allUsers, List<Expert> expertMap) {
        return allUsers.stream()
            .filter(user -> matchesAllConditions(user, conditions, expertMap, allUsers.size()))
            .collect(Collectors.toList());
    }

    private boolean matchesAllConditions(User user, List<Condtion> conditions, List<Expert> expertMap, int totalUsers) {
        return conditions.stream().allMatch(condition -> {
            String conditionName = condition.getConditionName();  // 条件类型 (old/score等)
            String operator = condition.getConditionIf();         // 运算符 (>/</=)
            String value = condition.getConditionValue();         // 条件值
            
            // 对于person条件，我们在外部处理，这里始终返回true
            if ("person".equals(conditionName)) {
                return true;
            }

            switch (conditionName) {
                case "old":
                    return compareDouble(user.getOld(), operator, Double.parseDouble(value));
                case "score":
                    return compareDouble(user.getScore(), operator, Double.parseDouble(value));
                case "fieldName":
                    return user.getFieldId().toString().equals(value);
                case "classmate":
                case "colleague":
                    // 根据用户ID从专家列表中查找专家信息
                    Expert expert = expertMap.stream()
                            .filter(e -> e.getId().equals(user.getId()))
                            .findFirst()
                            .orElse(null);
                    if (expert == null) return false;

                    // 将条件值转换为阈值
                    double threshold = Double.parseDouble(value);
                    
                    // 根据条件名称获取对应的关系列表
                    List<String> relationships = conditionName.equals("classmate") ? 
                        expert.getClassmate() : expert.getColleague();
                    if (relationships == null || relationships.isEmpty()) return false;

                    // 计算基于Task的关系亲密度得分
                    double intimacyScore = calculateTaskBasedIntimacyScore(expert.getId(), expert, conditionName.equals("classmate"));
                    
                    // 比较亲密度得分和阈值
                    return compareDouble(intimacyScore, operator, threshold);
                default:
                    return false;
            }
        });
    }

    private boolean compareDouble(Double value1, String operator, Double value2) {
        if (value1 == null) return false;
        switch (operator) {
            case ">": return value1 > value2;
            case "=": return value1.equals(value2);
            case "<": return value1 < value2;
            default: return false;
        }
    }
    /**
     * 计算基于Task的关系亲密度得分
     * @param expertId 当前专家ID
     * @param expert 专家对象
     * @param isClassmate 是否为同学关系
     * @return 0-1之间的亲密度得分
     */
    private double calculateTaskBasedIntimacyScore(String expertId, Expert expert, boolean isClassmate) {
        // 获取关系列表(同学或同事)
        List<String> relationships = isClassmate ? expert.getClassmate() : expert.getColleague();
        if(relationships == null || relationships.isEmpty()) {
            return 0.0; // 如果没有关系则返回0分
        }

        // 获取专家参与的所有任务
        List<TaskResult> expertTasks = taskResultMapper.selectList(new LambdaQueryWrapper<TaskResult>()
                .eq(TaskResult::getUserId, expertId)  // 修改为userId
                .groupBy(TaskResult::getTaskId));
        if (expertTasks.isEmpty()) {
            return 0.0; // 如果专家没有参与任何任务则返回0分
        }

        // 计算与每个关系人的共同任务数
        int totalSharedTasks = 0;
        for (String relationId : relationships) {
            // 获取关系人参与的所有任务
            List<TaskResult> relationTasks = taskResultMapper.selectList(
                new LambdaQueryWrapper<TaskResult>()
                    .eq(TaskResult::getUserId, relationId)  // 修改为userId
                    .groupBy(TaskResult::getTaskId)
            );

            // 计算专家和关系人之间的任务交集数量
            int sharedTasks = (int) expertTasks.stream()
                    .filter(expertTask -> relationTasks.stream()
                            .anyMatch(relationTask -> 
                                relationTask.getTaskId().equals(expertTask.getTaskId())))
                    .count();
            totalSharedTasks += sharedTasks;
        }

        // 计算平均每个关系的共同任务数
        double avgSharedTasks = relationships.isEmpty() ? 0.0 : 
            (double) totalSharedTasks / relationships.size();
        
        // 使用sigmoid函数将结果映射到0-1区间
        // 同学关系:平均3个共同任务时得分为0.5
        // 同事关系:平均5个共同任务时得分为0.5
        double midPoint = isClassmate ? 3 : 5;  // 设置中点值
        double rate = isClassmate ? 0.3 : 0.2;  // 设置增长率,同学关系增长更快
        
        // 使用sigmoid函数计算最终得分: 1/(1+e^(-k(x-m)))
        // k为增长率,x为平均共同任务数,m为中点值
        return 1 / (1 + Math.exp(-rate * (avgSharedTasks - midPoint)));
    }
} 