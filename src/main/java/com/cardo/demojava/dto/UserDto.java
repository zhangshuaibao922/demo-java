package com.cardo.demojava.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDto implements Serializable {
    /**  */
    @ApiModelProperty(name = "主键",notes = "")
    @TableId(type= IdType.AUTO)
    private String id ;
    /** 账号 */
    @ApiModelProperty(name = "账号",notes = "")
    private String account ;
    /** 密码 */
    @ApiModelProperty(name = "密码",notes = "")
    private String password ;
    /** 姓名 */
    @ApiModelProperty(name = "姓名",notes = "")
    private String name ;
    /**
     * 邮件
     */
    @ApiModelProperty(name = "邮件", notes = "")
    private String email;
    /** 领域 */
    @ApiModelProperty(name = "领域",notes = "")
    private String fieldName ;
    /**
     * 标签
     */
    @ApiModelProperty(name = "标签内容", notes = "")
    private String relationship;
    /** 权限 */
    @ApiModelProperty(name = "权限",notes = "")
    private String roleName ;
    /** 年限 */
    @ApiModelProperty(name = "年限",notes = "")
    private Double old ;
    /** 评分 */
    @ApiModelProperty(name = "评分",notes = "")
    private Double score ;
}
