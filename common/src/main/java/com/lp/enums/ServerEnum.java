package com.lp.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 服务枚举
 *
 * @author lp
 */
@Getter
public enum ServerEnum {

    HALL("hall-server", "大厅服务", true),
    ACTIVITY("activity-server", "活动服务", false);

    /**
     * 服务名
     */
    private final String serverName;
    /**
     * 描述
     */
    private final String describe;
    /**
     * 是否是有状态服务
     */
    private final Boolean state;


    ServerEnum(String serverName, String describe, Boolean state) {
        this.serverName = serverName;
        this.describe = describe;
        this.state = state;
    }

    /**
     * 获取枚举类
     *
     * @param serverName
     * @return
     */
    public static ServerEnum getEnum(String serverName) {
        for (ServerEnum request : ServerEnum.values()) {
            if (Objects.equals(request.getServerName(), serverName)) {
                return request;
            }
        }
        return null;
    }
}
