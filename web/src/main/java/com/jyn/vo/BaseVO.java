package com.jyn.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 10263
 */
@Schema(name = "BaseVO", description = "基础返回信息")
@Data
public class BaseVO<T> {

    @Schema(description = "提示消息")
    private String message;

    @Schema(description = "实际数据")
    private T data;

    @Schema(description = "code")
    private Integer code;

    public static <T> BaseVO<T> success(T data) {
        return new BaseVO(data);
    }

    private BaseVO() {
    }

    private BaseVO(Integer code, String msg, T data) {
        this.message = msg;
        this.data = data;
        this.code = code;
    }

    private BaseVO(T data) {
        this.data = data;
    }
}
