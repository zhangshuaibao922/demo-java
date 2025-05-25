package com.cardo.demojava.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordDTO  implements Serializable {
    String id ;
    String password;
    String newPassword;
}
