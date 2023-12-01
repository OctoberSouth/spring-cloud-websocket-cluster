package com.lp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserServerDTO {

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 服务
     */
    private String server;

    /**
     * 用户指定连接服务名
     */
    private String serverName;

}
