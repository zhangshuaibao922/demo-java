package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public Response<IPage<Field>> queryFieldByPage(Page<Field> pagination, String fieldName) {
        // 构建查询条件
        LambdaQueryWrapper<Field> queryWrapper = new LambdaQueryWrapper<>();
        if(fieldName != null && !fieldName.isEmpty()) {
            queryWrapper.like(Field::getFieldName, fieldName);
        }
        // 执行分页查询
        IPage<Field> result = fieldMapper.selectPage(pagination, queryWrapper);
        return Response.ok(result);
    }
}
