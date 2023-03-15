package com.jyn.entity;

import lombok.Data;

import javax.persistence.Id;

/**
 * @author 10263
 */
@Data
public class User {

    @Id
    private Long id;
    private String userName;
    private Integer sex;

}