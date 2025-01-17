package com.cardo.demojava;

import com.cardo.demojava.config.RedisService;
import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.entity.*;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.mapper.RoleMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.ExpertService;
import com.cardo.demojava.service.LoginService;
import com.cardo.demojava.util.SendMailUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

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
            List<RelationShip> relationShips = objectMapper.readValue(user.getRelationship(), new TypeReference<List<RelationShip>>() {
            });
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
        List<String> list= new ArrayList<>();
        list.add("1357756491@qq.com");

        sendMailUtils.sendEmail(list, "测试发送邮件",
                "您好！这是我发送的一封测试邮件。");
    }

    @Test
    void contextLoads4() throws JsonProcessingException {
//        Response<List<Expert>> all = expertService.getAll();
//        System.out.println(all.getData().size());

    }
}
