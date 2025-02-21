package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.entity.*;
import com.cardo.demojava.mapper.ClassmateMapper;
import com.cardo.demojava.mapper.ColleagueMapper;
import com.cardo.demojava.mapper.UserMapper;
import com.cardo.demojava.service.ExpertService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cardo.demojava.contant.Code.*;


@Service
public class ExpertServiceImpl implements ExpertService {


    @Autowired
    UserMapper userMapper;
    @Autowired
    ColleagueMapper colleagueMapper;
    @Autowired
    ClassmateMapper classmateMapper;

    @Override
    public Response<IPage<Expert>> getAll(Page<User> pagination, String name) throws JsonProcessingException {
        // 创建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(User::getName, name);
        }
        // 执行分页查询
        Page<User> userPage = userMapper.selectPage(pagination, queryWrapper);
        List<User> users = userPage.getRecords();
        List<Expert> experts = new ArrayList<>();
        for (User user : users) {
            Expert expert = new Expert();
            expert.setId(user.getId());
            expert.setName(user.getName());
            List<String> classmate= new ArrayList<>();
            List<Classmate> classmates = classmateMapper.selectList(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, user.getRelationship()));
            if(classmates != null && !classmates.isEmpty()) {
                // 提取 classmateId
                // 通过 classmateId 获取 User，并提取名字
                // 将名字收集到 List 中
                classmate= classmates.stream()
                        .map(Classmate::getClassmateId) // 提取 classmateId
                        .map(classmateId -> userMapper.selectById(classmateId).getName())
                        .collect(Collectors.toList());
            }
            expert.setClassmate(classmate);
            List<String> colleague = new ArrayList<>();
            List<Colleague> colleagues = colleagueMapper.selectList(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, user.getRelationship()));
            if(colleagues != null && !colleagues.isEmpty()) {
                colleague=colleagues.stream()
                        .map(Colleague::getColleagueId)
                        .map(colleagueId-> userMapper.selectById(colleagueId).getName())
                        .collect(Collectors.toList());
            }
            expert.setColleague(colleague);
            experts.add(expert);
        }
        Page<Expert> expertPage = new Page<>();
        BeanUtils.copyProperties(userPage, expertPage);
        expertPage.setRecords(experts);
        return Response.ok(expertPage);
    }

    @Override
    public Response<String> update(Expert expert) throws JsonProcessingException {
        User user = userMapper.selectById(expert.getId());
        String relationship = user.getRelationship();
        try {
            //拿到原来的标签对方的id
            List<Classmate> classmates = classmateMapper
                    .selectList(new LambdaQueryWrapper<Classmate>()
                    .eq(Classmate::getRelationship, relationship));
            //删除自己的标签
            classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, relationship));
            //根据对方的id，删除相应的标签
            for (Classmate classmate : classmates) {
                String relationship1 = userMapper.selectById(classmate.getClassmateId()).getRelationship();
                classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, relationship1));
            }
            //现在根据上传过来的expert中的classmateID来进行填充
            List<String> classmate1 = expert.getClassmate();
            for (String classmateName : classmate1) {
                //新增对应name所拥有的标签
                User other = userMapper.selectOne(new LambdaQueryWrapper<User>()
                        .eq(User::getName, classmateName));

                String otherRelationship = other.getRelationship();
                Classmate otherClassmate = new Classmate();
                otherClassmate.setClassmateId(expert.getId());
                otherClassmate.setRelationship(otherRelationship);
                classmateMapper.insert(otherClassmate);

                //修改自己存的标签
                Classmate classmate = new Classmate();
                classmate.setClassmateId(other.getId());
                classmate.setRelationship(relationship);
                classmateMapper.insert(classmate);

            }

            List<Colleague> colleagues = colleagueMapper
                    .selectList(new LambdaQueryWrapper<Colleague>()
                            .eq(Colleague::getRelationship, relationship));
            //删除自己的标签
            colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, relationship));
            //根据对方的id，删除相应的标签
            for (Colleague colleague : colleagues) {
                String relationship1 = userMapper.selectById(colleague.getColleagueId()).getRelationship();
                colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, relationship1));
            }
            //现在根据上传过来的expert中的colleagueID来进行填充
            List<String> colleague1 = expert.getColleague();
            for (String colleagueName : colleague1) {
                //新增对应name所拥有的标签
                User other = userMapper.selectOne(new LambdaQueryWrapper<User>()
                        .eq(User::getName, colleagueName));

                String otherRelationship = other.getRelationship();
                Colleague othercolleague = new Colleague();
                othercolleague.setColleagueId(expert.getId());
                othercolleague.setRelationship(otherRelationship);
                colleagueMapper.insert(othercolleague);

                //修改自己存的标签
                Colleague colleague = new Colleague();
                colleague.setColleagueId(other.getId());
                colleague.setRelationship(relationship);
                colleagueMapper.insert(colleague);
            }
        } catch (Exception e) {
            return Response.error(UPDATE_FAIL);
        }
        return Response.ok("OK");
    }

//    @Override
//    public Response<String> add(Expert expert) throws JsonProcessingException {
//        User user = userMapper.selectById(expert.getId());
//        String relationship = user.getRelationship();
//        try {
//            classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, relationship));
//            List<String> classmateList = expert.getClassmate();
//            for (String classmateId : classmateList) {
//                Classmate classmate = new Classmate();
//                classmate.setClassmateId(classmateId);
//                classmate.setRelationship(relationship);
//                classmateMapper.insert(classmate);
//            }
//            colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, relationship));
//            List<String> colleagueList = expert.getColleague();
//            for (String colleagueId : colleagueList) {
//                Colleague colleague = new Colleague();
//                colleague.setColleagueId(colleagueId);
//                colleague.setRelationship(relationship);
//                colleagueMapper.insert(colleague);
//            }
//        } catch (Exception e) {
//            return Response.error(ADD_FAIL);
//        }
//        return Response.ok("OK");
//    }

    @Override
    public Response<String> delete(String id) throws JsonProcessingException {
        User user = userMapper.selectById(id);
        String relationship = user.getRelationship();
        classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, relationship));
        classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getClassmateId,id));
        colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, relationship));
        colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getColleagueId,id));
        return Response.ok("OK");
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
        if (experts.isEmpty()) {
            return Response.error(DELETE_FAIL);
        } else {
            for (Expert expert : experts) {
                User user = userMapper.selectById(expert.getId());
                String relationship = user.getRelationship();
                classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, relationship));
                colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, relationship));
                classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, relationship));
                classmateMapper.delete(new LambdaQueryWrapper<Classmate>().eq(Classmate::getClassmateId,user.getId()));
                colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, relationship));
                colleagueMapper.delete(new LambdaQueryWrapper<Colleague>().eq(Colleague::getColleagueId,user.getId()));
            }
            return Response.ok("OK");
        }
    }

    @Override
    public List<Expert> getAllExpert() {
        List<User> users = userMapper.selectList(null);
        List<Expert> experts = new ArrayList<>();
        for (User user : users) {
             Expert expert = new Expert();
             expert.setId(user.getId());
             expert.setName(user.getName());
             List<String> classmate= new ArrayList<>();
             List<Classmate> classmates = classmateMapper.selectList(new LambdaQueryWrapper<Classmate>().eq(Classmate::getRelationship, user.getRelationship()));
             if(classmates != null && !classmates.isEmpty()) {
                 // 提取 classmateId
                 // 通过 classmateId 获取 User，并提取名字
                 // 将名字收集到 List 中
                 classmate= classmates.stream()
                         .map(Classmate::getClassmateId) // 提取 classmateId
                         .map(classmateId -> userMapper.selectById(classmateId).getName())
                         .collect(Collectors.toList());
             }
             expert.setClassmate(classmate);
             List<String> colleague = new ArrayList<>();
             List<Colleague> colleagues = colleagueMapper.selectList(new LambdaQueryWrapper<Colleague>().eq(Colleague::getRelationship, user.getRelationship()));
             if(colleagues != null && !colleagues.isEmpty()) {
                 colleague=colleagues.stream()
                         .map(Colleague::getColleagueId)
                         .map(colleagueId-> userMapper.selectById(colleagueId).getName())
                         .collect(Collectors.toList());
             }
             expert.setColleague(colleague);
             experts.add(expert);
         }
         return experts;
    }
}
