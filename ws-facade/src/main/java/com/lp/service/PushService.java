package com.lp.service;


import com.alibaba.fastjson2.JSONObject;
import com.lp.feign.PushFeign;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 消息推送
 */
@Service
public class PushService {

    @Resource
    private PushFeign pushFeign;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 静态常量
     */
    private static final String SOCKET_USER_SPRING_APPLICATION_NAME = "ws:socket:user:spring:application:name";

    /**
     * 发送消息
     *
     * @param userId
     * @param message
     */
    public void pushMessage(Long userId, JSONObject message) {
        Object serviceName = this.stringRedisTemplate.opsForHash().get(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "");
        if (serviceName != null) {
            this.pushFeign.pushMessage(serviceName.toString(), userId, message);
        }
    }

}
