package com.cardo.demojava.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskPageResultDto implements Serializable {
    /** 主键 */
    @ApiModelProperty(name = "主键",notes = "")
    private String id ;
    /** 任务名称 */
    @ApiModelProperty(name = "任务名称",notes = "")
    private String taskName ;
    /** 条件id */
    @ApiModelProperty(name = "条件id",notes = "")
    private String conditionId ;
    private String userId;
    private String number;
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
}
