package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ExpertService {

    Response<String> update(Expert expert) throws JsonProcessingException;

//    Response<String> add(Expert expert)throws JsonProcessingException;

    Response<String> delete(String id)throws JsonProcessingException;

    Response<List<User>> getAllByUsername(String username);

    Response<String> deleteAllUser(List<Expert> experts);

    Response<IPage<Expert>> getAll(Page<User> pagination, String name) throws JsonProcessingException;

    List<Expert> getAllExpert();
}
