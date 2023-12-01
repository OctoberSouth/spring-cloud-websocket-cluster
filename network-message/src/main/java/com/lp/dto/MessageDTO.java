package com.lp.dto;

import lombok.Data;

import java.util.Set;

@Data
public class MessageDTO {

    /**
     * 用户列表
     */
    private Set<Long> userId;

    /**
     * 数据
     */
    private Message data;
}
