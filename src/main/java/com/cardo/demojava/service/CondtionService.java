package com.cardo.demojava.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.Response;

public interface CondtionService extends IService<Condtion> {

    Response<String> saveBatch(List<Condtion> condtions,String conditionId);

    Response<List<Condtion>> getAllCondtions(String conditionId);
} 