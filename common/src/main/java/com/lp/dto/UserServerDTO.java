package com.lp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户对象
 *
 * @author 10263
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserServerDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 服务
     */
    private String server;

    /**
     * 用户指定连接服务名
     */
    private String serverName;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 连接唯一ID
     */
    private String uuid;

    /**
     * 是否上线
     */
    private Boolean login;

}
