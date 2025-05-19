package com.cardo.demojava;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.config.RedisService;
import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.dto.TaskExpertCountDTO;
import com.cardo.demojava.entity.*;
import com.cardo.demojava.mapper.*;
import java.util.*;
import com.cardo.demojava.service.ExpertService;
import com.cardo.demojava.service.LoginService;
import com.cardo.demojava.service.TaskScheduleService;
import com.cardo.demojava.service.TaskService;
import com.cardo.demojava.util.SendMailUtils;
import com.cardo.demojava.util.SnowflakeIdGenerator;
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
    @Autowired
    TaskScheduleService taskScheduleService;
    @Autowired
    TaskService taskService;

    //抽取评委
    @Test
    void Test01() {
        taskScheduleService.checkAndFilterUsers();
    }
    //开启评审
    @Test
    void Test03() {
        taskScheduleService.checkAndStartReview();
    }
    //结束评审，给出得分
    @Test
    void Test02() {
        taskScheduleService.checkAndFinishReview();
    }



}
