package com.cardo.demojava.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cardo.demojava.entity.Role;
import org.apache.ibatis.annotations.Mapper;


/**
 * ;(role)表数据库访问层
 *
 * @author : http://www.chiner.pro
 * @date : 2024-12-27
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}