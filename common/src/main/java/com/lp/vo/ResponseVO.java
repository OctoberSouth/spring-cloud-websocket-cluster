package com.lp.vo;

import lombok.Data;

/**
 * 统一返回
 *
 * @author lp
 */
@Data
public class ResponseVO {

    /**
     * 消息类型
     */
    private Integer code;

    /**
     * 额外数据
     */
    private String message;

    /**
     * 服务名
     */
    private String serverName;


    /**
     * 具体数据信息，一般用json包装
     */
    private String data;

}
