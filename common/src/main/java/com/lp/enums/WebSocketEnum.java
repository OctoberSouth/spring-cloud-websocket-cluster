package com.lp.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * websocket通用枚举
 *
 * @author lp
 */
@Getter
public enum WebSocketEnum {

    /**
     * 1000以内服务的状态为通用状态，其他服务禁用
     */
    //心跳
    HEARTBEAT(200, "heartbeat", "ws-server", "{}"),
    //token不合法
    TOKEN_ILLEGALITY(500, "illegality", "gateway-server", "{}");

    /**
     * 消息类型
     */
    private final Integer code;
    /**
     * 额外信息
     */
    private final String message;
    /**
     * 额外信息
     */
    private final String serverName;
    /**
     * 额外信息
     */
    private final String data;


    WebSocketEnum(Integer code, String message, String serverName, String data) {
        this.code = code;
        this.message = message;
        this.serverName = serverName;
        this.data = data;
    }

    /**
     * 获取枚举类
     *
     * @param code
     * @return
     */
    public static WebSocketEnum getEnum(Integer code) {
        for (WebSocketEnum request : WebSocketEnum.values()) {
            if (Objects.equals(request.getCode(), code)) {
                return request;
            }
        }
        return null;
    }
}
