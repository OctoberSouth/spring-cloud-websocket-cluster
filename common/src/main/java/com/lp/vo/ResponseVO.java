package com.lp.vo;

import com.lp.enums.MessageEnum;
import lombok.Data;

/**
 * 统一返回
 *
 * @author lp
 */
@Data
public class ResponseVO<T> {

    /**
     * 消息类型
     */
    private Integer code;

    /**
     * 额外数据
     */
    private String message;

    /**
     * 具体数据信息，一般用json包装
     */
    private T data;

    public ResponseVO(String message) {
        this.code = MessageEnum.FAIL.getCode();
        this.message = message;
    }

    public ResponseVO(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseVO() {

    }

    public boolean success() {
        return MessageEnum.SUCCESS.getCode().equals(this.code);
    }

}
