package com.cardo.demojava.dto;

import com.cardo.demojava.entity.RelationShip;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data

public class Expert implements Serializable {
    String id; //用户id
    String name; //用户名
    List<String> classmate;
    List<String> colleague;
    public Expert() {
    }
}