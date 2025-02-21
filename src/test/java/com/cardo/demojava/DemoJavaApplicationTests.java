package com.cardo.demojava;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.config.RedisService;
import com.cardo.demojava.entity.*;
import com.cardo.demojava.mapper.*;
import java.util.*;
import com.cardo.demojava.service.ExpertService;
import com.cardo.demojava.service.LoginService;
import com.cardo.demojava.util.SendMailUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;

@SpringBootTest
class DemoJavaApplicationTests {

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    FieldMapper fieldMapper;
    @Autowired
    LoginService loginService;
    @Autowired
    SendMailUtils sendMailUtils;
    @Autowired
    RedisService redisService;
    @Autowired
    ExpertService expertService;
    @Autowired
    ColleagueMapper colleagueMapper;
    @Autowired
    ClassmateMapper classmateMapper;
    @Autowired
    TaskMapper taskMapper;
    @Test
    void contextLoads() {
        redisService.deleteKey("experts");
    }
    @Test
    void contextLoads1() {
        User user = userMapper.selectById(10);
        System.out.println(user.getRelationship());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readValue(user.getRelationship(), new TypeReference<List<RelationShip>>() {});
            System.out.println(123);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void contextLoads2() {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setAccount(String.valueOf(200+i));
            user.setPassword(String.valueOf(200+i));
            user.setName(String.valueOf(200+i));
            user.setEmail("2742520302@qq.com");
            user.setRelationship("{'201': '同门', '202': '上级'}");
            user.setFieldId(1);
            user.setRoleId(1);
            user.setOld(1.00);
            user.setScore(100.00);
            userMapper.insert(user);
        }
    }
    @Test
    void contextLoads3() {
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
        System.out.println(tasks);
    }

}
