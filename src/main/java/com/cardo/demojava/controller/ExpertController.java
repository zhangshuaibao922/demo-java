package com.cardo.demojava.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cardo.demojava.dto.Expert;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.entity.User;
import com.cardo.demojava.service.ExpertService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expert")
@Api(tags = "标签功能接口")
public class ExpertController {
    @Autowired
    private ExpertService expertService;

    //全部
    @GetMapping("/all")
    public Response<IPage<Expert>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) throws JsonProcessingException {
        // 构建分页参数
        Page<User> pagination = new Page<>(page, size);
        return expertService.getAll(pagination,name);
    }
    //修改
    @PostMapping("/update")
    public Response<String> updateExpert(@RequestBody Expert expert) throws JsonProcessingException {
        return expertService.update(expert);
    }
//    //新增
//    @PostMapping("/add")
//    public Response<String> addExpert(@RequestBody Expert expert) throws JsonProcessingException {
//        return expertService.add(expert);
//    }

    //删除
    @GetMapping("/delete/{id}")
    public Response<String> deleteExpert(@PathVariable String id) throws JsonProcessingException {
        return expertService.delete(id);
    }
    @PostMapping("/delete/all")
    public Response<String> deleteAllExpert(@RequestBody List<Expert> experts) {
        return expertService.deleteAllUser(experts);
    }
    @GetMapping("/all/{username}")
    public Response<List<User>> getAllByUsername(@PathVariable String username) {
        return expertService.getAllByUsername(username);
    }
}
