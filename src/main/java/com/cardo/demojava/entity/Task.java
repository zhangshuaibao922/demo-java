package com.cardo.demojava.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

 /**
 * 任务;
 * @author : http://www.chiner.pro
 * @date : 2025-1-20
 */
@ApiModel(value = "任务",description = "")
@TableName("task")
@Data
public class Task implements Serializable{
    /** 主键 */
    @ApiModelProperty(name = "主键",notes = "")
    @TableId(type= IdType.AUTO)
    private String id ;
    /** 任务名称 */
    @ApiModelProperty(name = "任务名称",notes = "")
    private String taskName ;
    /** 条件id */
    @ApiModelProperty(name = "条件id",notes = "")
    private String conditionId ;
     private String userId;
    /** 抽取时间 */
    @ApiModelProperty(name = "抽取时间",notes = "")
    private String siphonTime ;
    /** 开始评审时间 */
    @ApiModelProperty(name = "开始评审时间",notes = "")
    private String startTime ;
    /** 结束时间 */
    @ApiModelProperty(name = "结束时间",notes = "")
    private String endTime ;
    /** 得分 */
    @ApiModelProperty(name = "得分",notes = "")
    private Double resultScore ;
     /** 状态 */
     @ApiModelProperty(name = "状态",notes = "1初始化 2资源准备完成 3抽取状态 4评审状态 5评审完成 0废弃")
     private Integer status ;
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