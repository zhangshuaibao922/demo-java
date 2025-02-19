package com.cardo.demojava.controller;

import com.cardo.demojava.entity.Condtion;
import com.cardo.demojava.entity.Response;
import com.cardo.demojava.service.CondtionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "规则条件接口")
@RestController
@RequestMapping("/condtion")
public class CondtionController {

    @Autowired
    private CondtionService condtionService;

    @PostMapping("/saveBatch/{conditionId}")
    @ApiOperation(value = "批量保存规则条件")
    public Response<String> saveBatchCondtions(@RequestBody List<Condtion> condtions,@PathVariable String conditionId) {
        return condtionService.saveBatch(condtions,conditionId);
    }
    @GetMapping("/all/{conditionId}")
    public Response<List<Condtion>> getAllCondtions(@PathVariable String conditionId) {
        return condtionService.getAllCondtions(conditionId);
    }
}