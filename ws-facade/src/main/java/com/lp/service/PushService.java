package com.lp.service;


import cn.hutool.core.util.StrUtil;
import com.lp.constants.RedisKeyConstants;
import com.lp.dto.Message;
import com.lp.enums.DeviceEnum;
import com.lp.feign.PushFeign;
import com.lp.util.LocalCache;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public void pushMessage(Message<?> vo, Long... userId) {
        for (Long id : userId) {
            List<String> serviceNameList = getServiceName(id);
            serviceNameList.forEach(serviceName -> {
                this.pushFeign.pushMessage(serviceName.split("@")[0], id, vo);
            });
        }
    }


    /**
     * 根据用户ID获取所有服务名
     *
     * @param id
     * @return
     */
    private List<String> getServiceName(Long id) {
        List<String> serviceNameList = new ArrayList<>();
        for (DeviceEnum request : DeviceEnum.values()) {
            String serviceName = LocalCache.wsUser.get(id + "@" + request.getDeviceType());
            if (StrUtil.isBlank(serviceName)) {
                long millis = System.currentTimeMillis();
                if (millis - cacheTime > 1000 * 60 * 5) {
                    synchronized (this) {
                        Map<Object, Object> userMap = this.stringRedisTemplate.opsForHash().entries(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME);
                        //将获取到的数据放到缓存里面
                        LocalCache.wsUser.putAll(userMap.entrySet().stream().collect(Collectors.toMap(key -> key.getKey().toString(), value -> value.getValue().toString())));
                        cacheTime = millis;
                    }
                }
                serviceName = LocalCache.wsUser.get(id + "@" + request.getDeviceType());
            }
            if (StrUtil.isNotBlank(serviceName)) {
                serviceNameList.add(serviceName);
            }
        }
        return serviceNameList;
    }

    /**
     * 发送消息，异步
     *
     * @param userId
     * @param vo
     */
    public void pushMessage(Message<?> vo, Set<Long> userId) {
        for (Long id : userId) {
            List<String> serviceNameList = getServiceName(id);
            serviceNameList.forEach(serviceName -> this.pushFeign.pushMessage(serviceName.split("@")[0], id, vo));
        }
    }

    /**
     * 群发，异步
     *
     * @param vo
     */
    public void pushMessage(Message<?> vo) {
        List<String> servicesOfServer = discoveryClient.getServices();
        List<String> list = servicesOfServer.stream().filter(e -> e.startsWith("ws-server")).toList();
        list.forEach(e -> this.pushFeign.pushMessage(e, vo));
    }
}
