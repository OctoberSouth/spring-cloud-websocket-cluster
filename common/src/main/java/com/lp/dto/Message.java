// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Message.proto

package com.lp.dto;

import lombok.Data;

/**
 * ws 发送消息
 *
 * @author 10263
 */
@Data
public class Message<T> {
    //消息类型
    private Integer code;
    //额外信息
    private String message;
    //服务名
    private String serverName;
    //具体数据信息
    private T data;
    //是否异步请求
    private Boolean async;

}