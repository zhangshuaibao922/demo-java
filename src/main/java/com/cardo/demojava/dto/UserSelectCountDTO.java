package com.cardo.demojava.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserSelectCountDTO implements Serializable {
    private String userId;
    private String userName;
    private Integer count;
    
    public UserSelectCountDTO() {
        this.count = 0;
    }
    
    public UserSelectCountDTO(String userId, String userName, Integer count) {
        this.userId = userId;
        this.userName = userName;
        this.count = count;
    }
} 