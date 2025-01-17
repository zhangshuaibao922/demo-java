package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cardo.demojava.config.RedisService;
import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.entity.RelationShip;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.ExpertService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cardo.demojava.contant.Code.*;


@Service
public class ExpertServiceImpl implements ExpertService {
    @Autowired
    RedisService redisService;

    @Autowired
    UserMapper userMapper;

    @Override
    public Response<List<Expert>> getAll(String name) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String valueByKey = redisService.getValueByKey("experts");
        if(name != null && !name.trim().isEmpty()){
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.like(User::getName, name);
            userLambdaQueryWrapper.isNotNull(User::getRelationship);
            userLambdaQueryWrapper.ne(User::getRelationship, "");
            List<User> users = userMapper.selectList(userLambdaQueryWrapper);
            List<Expert> experts = new ArrayList<>();
            for (User user : users) {
                Expert expert = new Expert();
                expert.setId(user.getId());
                expert.setName(user.getName());
                List<RelationShip> relationShips = objectMapper.readValue(user.getRelationship(), new TypeReference<List<RelationShip>>() {
                });
                List<String> classmate=new ArrayList<>();
                List<String> colleague=new ArrayList<>();
                for (RelationShip relationShip : relationShips) {
                    if(Objects.equals(relationShip.getValue(), "classmate")){
                        classmate.add(relationShip.getId());
                    }
                    if(Objects.equals(relationShip.getValue(), "colleague")){
                        colleague.add(relationShip.getId());
                    }
                }
                expert.setClassmate(classmate);
                expert.setColleague(colleague);
                experts.add(expert);
            }
            return Response.ok(experts);
        }else {
            if (valueByKey != null ) {
                List<Expert> experts = objectMapper.readValue(valueByKey, new TypeReference<List<Expert>>() {
                });
                return Response.ok(experts);
            }else {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.isNotNull(User::getRelationship);
                userLambdaQueryWrapper.ne(User::getRelationship, "");
                List<User> users = userMapper.selectList(userLambdaQueryWrapper);
                List<Expert> experts = new ArrayList<>();
                for (User user : users) {
                    Expert expert = new Expert();
                    expert.setId(user.getId());
                    expert.setName(user.getName());
                    List<RelationShip> relationShips = objectMapper.readValue(user.getRelationship(), new TypeReference<List<RelationShip>>() {
                    });
                    List<String> classmate=new ArrayList<>();
                    List<String> colleague=new ArrayList<>();
                    for (RelationShip relationShip : relationShips) {
                        if(Objects.equals(relationShip.getValue(), "classmate")){
                            classmate.add(relationShip.getId());
                        }
                        if(Objects.equals(relationShip.getValue(), "colleague")){
                            colleague.add(relationShip.getId());
                        }
                    }
                    expert.setClassmate(classmate);
                    expert.setColleague(colleague);
                    experts.add(expert);
                }

                String s = objectMapper.writeValueAsString(experts);
                redisService.setKeyValue("experts", s);
                return Response.ok(experts);
            }
        }
    }

    @Override
    public Response<String> update(Expert expert) throws JsonProcessingException {
        User user = userMapper.selectById(expert.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        List<RelationShip> relationShips=new ArrayList<>();
        List<String> classmate = expert.getClassmate();
        for (String id : classmate) {
            RelationShip relationShip = new RelationShip();
            relationShip.setId(id);
            relationShip.setValue("classmate");
            relationShips.add(relationShip);
        }
        List<String> colleague = expert.getColleague();
        for (String id : colleague) {
            RelationShip relationShip = new RelationShip();
            relationShip.setId(id);
            relationShip.setValue("colleague");
            relationShips.add(relationShip);
        }
        // 将 relationShips 列表转换为 JSON 字符串
        String json = objectMapper.writeValueAsString(relationShips);
        user.setRelationship(json);
        int i = userMapper.updateById(user);
        if(i > 0) {
            redisService.deleteKey("experts");
            return Response.ok("OK");
        }else {
            return Response.error(UPDATE_FAIL);
        }
    }

    @Override
    public Response<String> add(Expert expert) throws JsonProcessingException {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getName, expert.getName());
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        ObjectMapper objectMapper = new ObjectMapper();
        List<RelationShip> relationShips=new ArrayList<>();
        List<String> classmate = expert.getClassmate();
        for (String id : classmate) {
            RelationShip relationShip = new RelationShip();
            relationShip.setId(id);
            relationShip.setValue("classmate");
            relationShips.add(relationShip);
        }
        List<String> colleague = expert.getColleague();
        for (String id : colleague) {
            RelationShip relationShip = new RelationShip();
            relationShip.setId(id);
            relationShip.setValue("colleague");
            relationShips.add(relationShip);
        }
        // 将 relationShips 列表转换为 JSON 字符串
        String json = objectMapper.writeValueAsString(relationShips);
        user.setRelationship(json);
        int i = userMapper.insert(user);
        if(i > 0) {
            redisService.deleteKey("experts");
            return Response.ok("OK");
        }else {
            return Response.error(ADD_FAIL);
        }
    }

    @Override
    public Response<String> delete(String id) throws JsonProcessingException {
        User user = userMapper.selectById(id);
        user.setRelationship("");
        int i = userMapper.updateById(user);
        if(i > 0) {
            redisService.deleteKey("experts");
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<List<User>> getAllByUsername(String username) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getName, username);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        return Response.ok(users);
    }

    @Override
    public Response<String> deleteAllUser(List<Expert> experts) {
        if(experts.isEmpty()) {
            return Response.error(DELETE_FAIL);
        }else {
            for (Expert expert : experts) {
                User user = userMapper.selectById(expert.getId());
                user.setRelationship("");
                int i = userMapper.updateById(user);
            }
            redisService.deleteKey("experts");
            return Response.ok("OK");
        }
    }
}
