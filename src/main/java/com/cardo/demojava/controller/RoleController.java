package com.cardo.demojava.controller;

import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.Role;
import com.cardo.demojava.service.RoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ;(user)表控制层
 * @author :
 * @date : 2024-12-27
 */
@Api(tags = "权限功能接口")
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    //全部
    @GetMapping("/all")
    public Response<List<Role>> getAll() {
        return roleService.getAll();
    }
}
