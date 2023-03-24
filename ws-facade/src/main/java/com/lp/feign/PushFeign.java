package com.lp.feign;

import com.alibaba.fastjson2.JSONObject;
import com.lp.config.DynamicRoutingConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 10263
 */
@FeignClient(name = "pushFeign", configuration = DynamicRoutingConfig.class)
public interface PushFeign {

    /**
     * 推送消息
     *
     * @param serviceName 服务名
     * @param userId      用户
     * @param message     消息体
     */
    @PostMapping(value = "//{serviceName}/push/{userId}")
    void pushMessage(@PathVariable String serviceName, @PathVariable Long userId, @RequestBody JSONObject message);
}