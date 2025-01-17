package com.cardo.demojava.controller;

import com.cardo.demojava.entity.Field;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.service.FieldService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ;(user)表控制层
 * @author :
 * @date : 2024-12-27
 */
@Api(tags = "领域功能接口")
@RestController
@RequestMapping("/filed")
public class FieldController {
    @Autowired
    private FieldService fieldService;
    //全部
    @GetMapping("/all")
    public Response<List<Field>> getAll() {
        return fieldService.getAll();
    }
    //新增
    @PostMapping("/add")
    public Response<String> addUser(@RequestBody Field field) {
        return fieldService.addUser(field);
    }

    //删除
    @GetMapping("/delete/{id}")
    public Response<String> deleteUser(@PathVariable Integer id) {
        return fieldService.deleteUser(id);
    }
    //修改
    @PostMapping("/update")
    public Response<Field> updateUser(@RequestBody Field field) {
        return fieldService.updateUser(field);
    }
}
