package com.cardo.demojava.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
      * 分页查询
      * @param page 当前页码
      * @param size 每页记录数
      * @param fieldName 领域(可选条件)
      * @return
      */
      @GetMapping("/page")
      public Response<IPage<Field>> queryFieldByPage(
              @RequestParam(defaultValue = "1") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(required = false) String fieldName) {
                Page<Field> pagination = new Page<>(page, size);
                return fieldService.queryFieldByPage(pagination,fieldName);
              }
 
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
