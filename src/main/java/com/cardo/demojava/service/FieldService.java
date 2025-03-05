package com.cardo.demojava.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;

import java.util.List;

public interface FieldService extends IService<Field> {
    Response<List<Field>> getAll();

    Response<String> addUser(Field field);

    Response<String> deleteUser(Integer id);

    Response<Field> updateUser(Field field);

    Response<IPage<Field>> queryFieldByPage(Page<Field> pagination, String fieldName);
}
