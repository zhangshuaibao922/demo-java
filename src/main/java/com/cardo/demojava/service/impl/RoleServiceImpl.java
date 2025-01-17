package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Role;
import com.cardo.demojava.mapper.RoleMapper;
import com.cardo.demojava.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Response<List<Role>> getAll() {
        List<Role> roles = roleMapper.selectList(null);
        return Response.ok(roles);
    }
}
