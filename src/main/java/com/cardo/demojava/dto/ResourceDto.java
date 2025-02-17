package com.cardo.demojava.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResourceDto {
    /**  */
    @ApiModelProperty(name = "",notes = "")
    private String id ;
    /** 任务id */
    @ApiModelProperty(name = "任务id",notes = "")
    private String taskId ;
    /** 上传人id */
    @ApiModelProperty(name = "上传人名称",notes = "")
    private String userName ;
    /** 文件类型 */
    @ApiModelProperty(name = "文件类型",notes = "")
    private String resourceType ;
    /** 文件url链接 */
    @ApiModelProperty(name = "文件url链接",notes = "")
    private String resourceUrl ;
}
