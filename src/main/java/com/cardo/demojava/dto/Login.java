package com.cardo.demojava.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "登陆",description = "登陆")
@Data
public class Login implements Serializable {
    /** 账号 */
    @ApiModelProperty(name = "账号",notes = "")
    private String account ;
    /** 密码 */
    @ApiModelProperty(name = "密码",notes = "")
    private String password ;
}
