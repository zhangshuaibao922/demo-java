package com.cardo.demojava.service;

import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.dto.Expert;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFilterService {
    
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
                    // 如果找不到专家信息,返回false
                    if (expert == null) return false;
                    // 将条件值转换为阈值
                    double threshold = Double.parseDouble(value);
                    // 根据条件名称(classmate或colleague)获取对应的关系列表
                    List<String> relationships = conditionName.equals("classmate") ? 
                        expert.getClassmate() : expert.getColleague();
                    // 如果关系列表为空,返回false
                    if (relationships == null || relationships.isEmpty()) return false;
                    // 计算关系比例 = 关系数量/总用户数
                    double relationshipRatio = (double) relationships.size() / totalUsers;
                    // 比较关系比例和阈值
                    return compareDouble(relationshipRatio, operator, threshold);
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
} 