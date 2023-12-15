package com.lp.vo;


import com.lp.enums.MessageEnum;

import java.util.List;

/**
 * @author 10263
 */
public class R {

    public static ResponseVO<Void> success() {
        return new ResponseVO<>(MessageEnum.SUCCESS.getCode(), MessageEnum.SUCCESS.getMessage());
    }

    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(MessageEnum.SUCCESS.getCode(), MessageEnum.SUCCESS.getMessage(), data);
    }

    public static <T> ResponseVO<T> error() {
        return error(MessageEnum.FAIL.getCode(), MessageEnum.FAIL.getMessage());
    }

    public static <T> ResponseVO<T> error(String msg) {
        return error(MessageEnum.FAIL.getCode(), msg);
    }


    public static <T> ResponseVO<T> error(MessageEnum globalMessage) {
        return error(globalMessage.getCode(), globalMessage.getMessage());
    }

    public static <T> ResponseVO<T> error(Integer code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static <T> ResponseVO<T> error(Integer code, String msg, T data) {
        return new ResponseVO<>(code, msg, data);
    }


    public static <T> ResponseVO<List<T>> list(List<T> list) {
        ResponseVO<List<T>> result = new ResponseVO<>();
        result.setCode(MessageEnum.SUCCESS.getCode());
        result.setMessage(MessageEnum.SUCCESS.getMessage());
        result.setData(list);
        return result;
    }
}
