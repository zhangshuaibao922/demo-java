package com.cardo.demojava.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cardo.demojava.dto.Login;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper extends BaseMapper<Login> {
}