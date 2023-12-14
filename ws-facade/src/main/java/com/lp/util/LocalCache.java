package com.lp.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 10263
 */
@Data
public class LocalCache {

    /**
     * ws用户缓存
     */
    public static Map<String, String> wsUser = new HashMap<>();


    /**
     * 用户指定连接缓存
     */
    public static Map<String, String> userServer = new HashMap<>();
}
