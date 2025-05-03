package com.cardo.demojava.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
 /**
 * 资源;
 * @author : http://www.chiner.pro
 * @date : 2025-2-14
 */
@ApiModel(value = "资源",description = "资源")
@TableName("resource")
@Data
public class Resource implements Serializable,Cloneable{
    /**  */
    @TableId(type= IdType.AUTO)
    @ApiModelProperty(name = "",notes = "")
    private String id ;
    /** 任务id */
    @ApiModelProperty(name = "任务id",notes = "")
    private String taskId ;
    /** 上传人id */
    @ApiModelProperty(name = "上传人id",notes = "")
    private String userId ;
    /** 文件类型 */
    @ApiModelProperty(name = "文件类型",notes = "")
    private String resourceType ;
    /** 文件url链接 */
    @ApiModelProperty(name = "文件url链接",notes = "")
    private String resourceUrl ;
     @ApiModelProperty(name = "文件url链接",notes = "")
     private String resourceName ;
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