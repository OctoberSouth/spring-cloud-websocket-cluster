package com.lp.service;


import cn.hutool.core.util.StrUtil;
import com.lp.constants.RedisKeyConstants;
import com.lp.dto.Message;
import com.lp.feign.PushFeign;
import com.lp.util.LocalCache;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 消息推送
 */
@Service
public class PushService {

    /**
     * 缓存时间戳
     */
    private static Long cacheTime = 0L;
    @Resource
    private PushFeign pushFeign;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * 发送消息，异步
     *
     * @param userId
     * @param vo
     */
    public void pushMessage(Message vo, Long... userId) {
        for (Long id : userId) {
            String serviceName = getServiceName(id);
            if (StrUtil.isNotBlank(serviceName)) {
                this.pushFeign.pushMessage(serviceName, id, vo);
            }
        }
    }


    /**
     * 根据用户ID获取服务名
     *
     * @param id
     * @return
     */
    private String getServiceName(Long id) {
        String serviceName = LocalCache.wsUser.get(id);
        if (StrUtil.isBlank(serviceName)) {
            long millis = System.currentTimeMillis();
            if (millis - cacheTime > 1000 * 60 * 5) {
                synchronized (this) {
                    Map<Object, Object> userMap = this.stringRedisTemplate.opsForHash().entries(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME);
                    //将获取到的数据放到缓存里面
                    LocalCache.wsUser.putAll(userMap.entrySet().stream().collect(Collectors.toMap(key -> Long.valueOf(key.getKey().toString()), value -> value.getValue().toString())));
                    serviceName = LocalCache.wsUser.get(id);
                    cacheTime = millis;
                }
            }
        }
        return serviceName;
    }

    /**
     * 发送消息，异步
     *
     * @param userId
     * @param vo
     */
    public void pushMessage(Message vo, Set<Long> userId) {
        for (Long id : userId) {
            String serviceName = getServiceName(id);
            if (StrUtil.isNotBlank(serviceName)) {
                this.pushFeign.pushMessage(serviceName, id, vo);
            }
        }
    }

    /**
     * 群发，异步
     *
     * @param vo
     */
    public void pushMessage(Message vo) {
        List<String> servicesOfServer = discoveryClient.getServices();
        List<String> list = servicesOfServer.stream().filter(e -> e.startsWith("ws-server")).toList();
        list.forEach(e -> this.pushFeign.pushMessage(e, vo));
    }

    /**
     * 同步发送消息，消息发送完后才会返回
     *
     * @param userId
     * @param vo
     * @return
     */
    public void pushMessageFuture(Message vo, Long userId) {
        String serviceName = getServiceName(userId);
        if (StrUtil.isNotBlank(serviceName)) {
            this.pushFeign.pushMessage(serviceName, userId, vo);
        }
    }
}
