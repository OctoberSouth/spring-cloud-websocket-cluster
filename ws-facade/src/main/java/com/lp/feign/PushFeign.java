package com.lp.feign;

import com.lp.config.DynamicRoutingConfig;
import com.lp.dto.Message;
import com.lp.dto.MessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author lp
 */
@FeignClient(name = "pushFeign", configuration = DynamicRoutingConfig.class)
public interface PushFeign {

    /**
     * 推送消息
     *
     * @param serviceName 服务名
     * @param userId      用户
     * @param vo          消息体
     */
    @PostMapping(value = "//{serviceName}/push/{userId}")
    void pushMessage(@PathVariable("serviceName") String serviceName, @PathVariable("userId") Long userId, @RequestBody Message vo);

    /**
     * 群发
     *
     * @param serviceName 服务名
     * @param vo          消息体
     */
    @PostMapping(value = "//{serviceName}/push")
    void pushMessage(@PathVariable("serviceName") String serviceName, @RequestBody Message vo);

    /**
     * 群发
     *
     * @param serviceName 服务名
     * @param dto         消息体
     */
    @PostMapping(value = "//{serviceName}/push/batch")
    void pushBatchMessage(@PathVariable("serviceName") String serviceName, @RequestBody MessageDTO dto);

}
