package com.cardo.demojava.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : 袁奇
 * @date : 2024-12-27
 */
@ApiModel(value = "用户", description = "用户")
@TableName("user")
@Data
public class User implements Serializable, Cloneable {
    /**
     *
     */
    @ApiModelProperty(name = "主键", notes = "")
    @TableId(type = IdType.AUTO)
    private String id;
    /**
     * 账号
     */
    @ApiModelProperty(name = "账号", notes = "")
    private String account;
    /**
     * 密码
     */
    @ApiModelProperty(name = "密码", notes = "")
    private String password;
    /**
     * 姓名
     */
    @ApiModelProperty(name = "姓名", notes = "")
    private String name;
    /**
     * 邮件
     */
    @ApiModelProperty(name = "邮件", notes = "")
    private String email;
    /**
     * 领域
     */
    @ApiModelProperty(name = "领域", notes = "")
    private Integer fieldId;
    /**
     * 标签
     */
    @ApiModelProperty(name = "标签", notes = "")
    private String relationship;
    /**
     * 权限
     */
    @ApiModelProperty(name = "权限", notes = "")
    private Integer roleId;
    /**
     * 年限
     */
    @ApiModelProperty(name = "年限", notes = "")
    private Double old;
    /**
     * 得分
     */
    @ApiModelProperty(name = "得分", notes = "")
    private Double score;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间", notes = "")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(name = "更新时间", notes = "")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}