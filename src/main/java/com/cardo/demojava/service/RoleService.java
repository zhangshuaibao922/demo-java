package com.cardo.demojava.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    Response<List<Role>> getAll();

}
