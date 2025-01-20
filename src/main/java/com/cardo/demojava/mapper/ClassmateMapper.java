package com.cardo.demojava.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cardo.demojava.entity.Classmate;
import org.apache.ibatis.annotations.Mapper;


 /**
 * 同门;(classmate)表数据库访问层
 * @author : http://www.chiner.pro
 * @date : 2025-1-20
 */
@Mapper
public interface ClassmateMapper  extends BaseMapper<Classmate>{
}