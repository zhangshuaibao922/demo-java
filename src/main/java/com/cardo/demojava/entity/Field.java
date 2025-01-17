package com.cardo.demojava.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

 /**
 * ;
 * @author : http://www.chiner.pro
 * @date : 2024-12-30
 */
@ApiModel(value = "领域",description = "领域")
@TableName("field")
@Data
public class Field implements Serializable{
    /**  */
    @ApiModelProperty(name = "领域id",notes = "")
    @TableId(type= IdType.AUTO)
    private Integer fieldId ;
    /**  */
    @ApiModelProperty(name = "领域",notes = "")
    private String fieldName ;
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