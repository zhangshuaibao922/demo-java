package com.cardo.demojava.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
@ApiModel(value = "抽取结果",description = "")
@Data
@TableName("task_result")
public class TaskResult {
    @ApiModelProperty(name = "主键",notes = "")
    @TableId(type= IdType.AUTO)
    private String id;
    
    private String taskId;
    
    private String userId;
    private String userName ;
    private Integer fieldId ;
    
    private Integer score ;
    private String description ;
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