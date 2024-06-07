package com.lp.pojo;

import jakarta.websocket.Session;
import lombok.Data;

@Data
public class WebSocketInfo {

    /**
     * session
     */
    private Session session;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 设备
     */
    private String device;

    /**
     * 连接生成唯一ID
     */
    private String uuid;

    /**
     * 链接地址
     */
    private String address;
}
