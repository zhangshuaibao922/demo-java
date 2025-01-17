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
@ApiModel(value = "权限",description = "权限")
@TableName("role")
@Data
public class Role implements Serializable,Cloneable{
    /**  */
    @ApiModelProperty(name = "",notes = "")
    @TableId(type= IdType.AUTO)
    private Integer roleId ;
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String roleName ;
     /** 创建时间 */
     @ApiModelProperty(name = "创建时间",notes = "")
     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
     @TableField(fill = FieldFill.INSERT)
     private Date createTime ;
     /** 更新时间 */
     @ApiModelProperty(name = "更新时间",notes = "")
     @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
     @TableField(fill = FieldFill.INSERT_UPDATE)
     private Date updateTime ;

}