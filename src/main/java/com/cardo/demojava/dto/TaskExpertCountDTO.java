package com.cardo.demojava.dto;

import com.cardo.demojava.entity.Task;
import lombok.Data;
import java.io.Serializable;

@Data

public class TaskExpertCountDTO implements Serializable  {
     String taskName;
     Integer expertCount;

} 