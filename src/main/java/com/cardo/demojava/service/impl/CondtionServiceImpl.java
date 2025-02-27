package com.cardo.demojava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cardo.demojava.entity.*;
import com.cardo.demojava.mapper.CondtionMapper;
// import com.cardo.demojava.mapper.TaskMapper;
import com.cardo.demojava.service.CondtionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.cardo.demojava.contant.Code.*;

@Service
public class CondtionServiceImpl extends ServiceImpl<CondtionMapper, Condtion> implements CondtionService {

    @Autowired
    private CondtionMapper condtionMapper;
    // @Autowired
    // private TaskMapper taskMapper;

    @Override
    public Response<String> saveBatch(List<Condtion> condtions,String conditionId) {
            condtionMapper.delete(new LambdaQueryWrapper<Condtion>().eq(Condtion::getConditionId, conditionId));
            for (Condtion condtion : condtions) {
                if (condtion.getConditionName() == null || condtion.getConditionIf() == null || condtion.getConditionValue() == null) {
                    return Response.error(ADD_FAIL);
                }
                condtionMapper.insert(condtion);
            }
            return Response.ok("SUCCESS");
    }

    @Override
    public Response<List<Condtion>> getAllCondtions(String conditionId) {
        List<Condtion> condtions =condtionMapper.selectList(new LambdaQueryWrapper<Condtion>()
        .eq(Condtion::getConditionId, conditionId));
        return Response.ok(condtions);
    }

}