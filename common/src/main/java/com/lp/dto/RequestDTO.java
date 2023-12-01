package com.lp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author lp
 */
@Data
public class RequestDTO {
    /**
     * 消息类型
     */
    @NotNull
    private Integer code;

    /**
     * 额外数据
     */
    private String message;

    /**
     * 服务名
     */
    @NotNull
    private String serverName;


    /**
     * 具体数据信息，一般用json包装
     */
    private String data;

}
