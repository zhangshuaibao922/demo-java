package com.cardo.demojava.dto;

import lombok.Data;

@Data
public class TaskResultDto {

    private String id;
    
    private String taskId;
    private String taskName;
    
    private String userId;
    private String userName;
    private String email;
    private String fieldName;

    private Integer score;

    private String description;

}
