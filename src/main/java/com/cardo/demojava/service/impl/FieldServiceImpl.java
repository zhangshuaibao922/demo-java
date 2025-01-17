package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.mapper.FieldMapper;
import com.cardo.demojava.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.cardo.demojava.contant.Code.*;

@Service
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements FieldService {
    @Autowired
    private FieldMapper fieldMapper;

    @Override
    public Response<List<Field>> getAll() {
        List<Field> list = fieldMapper.selectList(null);
        return Response.ok(list);
    }

    @Override
    public Response<String> addUser(Field field) {
        int insert = fieldMapper.insert(field);
        if(insert > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(ADD_FAIL);
        }
    }

    @Override
    public Response<String> deleteUser(Integer id) {
        int delete = fieldMapper.deleteById(id);
        if(delete > 0) {
            return Response.ok("OK");
        }else {
            return Response.error(DELETE_FAIL);
        }
    }

    @Override
    public Response<Field> updateUser(Field field) {
        int update = fieldMapper.updateById(field);
        if(update > 0) {
            return Response.ok(field);
        }else {
            return Response.error(UPDATE_FAIL);
        }
    }
}