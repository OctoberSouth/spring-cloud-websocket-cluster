package com.jyn.aop;

import lombok.Data;

/**
 * @author 10263
 */
@Data
public class WebLog {
    /**
     * 方法说明
     */
    private String description;
    /**
     * 执行时间
     */
    private Long spendTime;
    /**
     * 请求地址
     */
    private String uri;
    /**
     * 请求信息
     */
    private String parameter;
    /**
     * 请求头信息
     */
    private String header;
    /**
     * 返回信息
     */
    private String result;
}
