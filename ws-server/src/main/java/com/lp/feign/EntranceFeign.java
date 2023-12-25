package com.lp.feign;

import com.lp.config.DynamicRoutingConfig;
import com.lp.dto.Message;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author lp
 */
@FeignClient(name = "entranceFeign", configuration = DynamicRoutingConfig.class)
public interface EntranceFeign {

    /**
     * 消息通信
     *
     * @param serviceName 服务名
     * @param userId      用户
     * @param language    语言
     * @param dto         消息体
     * @return ResponseVO
     */
    @PostMapping(value = "//{serviceName}/entrance/{userId}")
    Message entrance(@PathVariable("serviceName") String serviceName, @PathVariable("userId") Long userId, @RequestHeader("Accept-Language") String language, @RequestBody @Valid Message dto);
    
}
