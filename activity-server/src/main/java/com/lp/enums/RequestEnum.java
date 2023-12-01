package com.lp.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 请求枚举类
 *
 * @author lp
 */
@Getter
public enum RequestEnum {
    ONE(1, "describe"),
    TWO(2, "describe");

    /**
     * 类型
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String describe;

    RequestEnum(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    /**
     * 获取枚举类
     *
     * @param code
     * @return
     */
    public static RequestEnum getEnum(Integer code) {
        for (RequestEnum request : RequestEnum.values()) {
            if (Objects.equals(request.getCode(), code)) {
                return request;
            }
        }
        return null;
    }
}
