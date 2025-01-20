package com.cardo.demojava.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

 /**
 * 同门;
 * @author : http://www.chiner.pro
 * @date : 2025-1-20
 */
@ApiModel(value = "同门",description = "")
@TableName("classmate")
@Data
public class Classmate implements Serializable{
    /**  */
    @ApiModelProperty(name = "",notes = "")
    @TableId(type= IdType.AUTO)
    private String id ;
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String relationship ;
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String classmateId ;
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