package com.lp.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 设备枚举
 *
 * @author lp
 */
@Getter
public enum DeviceEnum {

    PC("PC", "电脑端"),
    IPAD("IPAD", "PAD端");

    /**
     * 服务名
     */
    private final String deviceType;
    /**
     * 描述
     */
    private final String describe;


    DeviceEnum(String deviceType, String describe) {
        this.deviceType = deviceType;
        this.describe = describe;
    }

    /**
     * 获取枚举类
     *
     * @param deviceType
     * @return
     */
    public static DeviceEnum getEnum(String deviceType) {
        for (DeviceEnum request : DeviceEnum.values()) {
            if (Objects.equals(request.getDeviceType(), deviceType)) {
                return request;
            }
        }
        return null;
    }
}
