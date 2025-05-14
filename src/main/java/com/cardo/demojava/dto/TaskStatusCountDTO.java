package com.cardo.demojava.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class TaskStatusCountDTO implements Serializable {
    private Integer status1; // 初始化
    private Integer status2; // 资源准备完成
    private Integer status3; // 抽取状态
    private Integer status4; // 评审状态
    private Integer status5; // 评审完成
    
    public TaskStatusCountDTO() {
        this.status1 = 0;
        this.status2 = 0;
        this.status3 = 0;
        this.status4 = 0;
        this.status5 = 0;
    }
} 