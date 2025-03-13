package com.cardo.demojava.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {
    @ApiModelProperty(name = "主键",notes = "")
    private String id ;
    @ApiModelProperty(name = "姓名",notes = "")
    private String name ;

}
