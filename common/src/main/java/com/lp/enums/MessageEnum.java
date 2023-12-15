package com.lp.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    /**
     * 全局统一消息
     */
    ERROR(500, "系统异常!"),
    SUCCESS(200, "操作成功!"),
    FAIL(0, "操作失败!"),
    NOT_FOUND(404, "数据不见了!"),
    NOT_LOGIN(400, "未登录");

    private final Integer code;
    private final String message;

    MessageEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
