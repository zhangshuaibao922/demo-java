package com.cardo.demojava.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class FieldUserCountDTO implements Serializable {
    private String fieldName;
    private Integer number;
    
    public FieldUserCountDTO() {
        this.number = 0;
    }
    
    public FieldUserCountDTO(String fieldName, Integer number) {
        this.fieldName = fieldName;
        this.number = number;
    }
} 