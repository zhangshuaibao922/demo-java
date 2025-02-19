package com.cardo.demojava.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

 /**
 * 规则;
 * @author : http://www.chiner.pro
 * @date : 2025-2-19
 */
@ApiModel(value = "规则",description = "")
@TableName("condtion")
@Data
public class Condtion implements Serializable{
    /**  */
    @ApiModelProperty(name = "",notes = "")
    @TableId(type= IdType.AUTO)
    private String id ;
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String conditionId ;
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String conditionName ;
    /** 条件 */
    @ApiModelProperty(name = "条件",notes = "")
    private String conditionIf ;
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String conditionValue ;
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
