package com.cardo.demojava.service;

import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ExpertService {
    Response<List<Expert>> getAll(String name) throws JsonProcessingException;

    Response<String> update(Expert expert) throws JsonProcessingException;

    Response<String> add(Expert expert)throws JsonProcessingException;

    Response<String> delete(String id)throws JsonProcessingException;

    Response<List<User>> getAllByUsername(String username);

    Response<String> deleteAllUser(List<Expert> experts);
}
