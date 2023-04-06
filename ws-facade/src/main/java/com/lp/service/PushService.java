package com.lp.service;


import com.lp.feign.PushFeign;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author 消息推送
 */
@Service
public class PushService {

    /**
     * 静态常量
     */
    private static final String SOCKET_USER_SPRING_APPLICATION_NAME = "ws:socket:user:spring:application:name";
    @Resource
    private PushFeign pushFeign;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送消息
     *
     * @param userId
     * @param message
     */
    public void pushMessage(Long userId, Object message) {
        Object serviceName = this.stringRedisTemplate.opsForHash().get(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "");
        if (serviceName != null) {
            this.pushFeign.pushMessage(serviceName.toString(), userId, message);
        }
    }

    /**
     * 群发
     *
     * @param message
     */
    public void pushMessage(Object message) {
        Set<Object> serviceNameSet = new HashSet<>(this.stringRedisTemplate.opsForHash().values(SOCKET_USER_SPRING_APPLICATION_NAME));
        serviceNameSet.forEach(e -> this.pushFeign.pushMessage(e.toString(), message));
    }

    /**
     * 有返回发送消息
     *
     * @param userId
     * @param message
     * @return
     */
    public Future<Void> pushMessageFuture(Long userId, Object message) {
        Object serviceName = this.stringRedisTemplate.opsForHash().get(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "");
        if (serviceName != null) {
            return this.pushFeign.pushMessageFuture(serviceName.toString(), userId, message);
        } else {
            throw new RuntimeException("该用户没有连接");
        }
    }

}
