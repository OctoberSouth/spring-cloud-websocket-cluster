package com.lp.feign;

import com.lp.config.DynamicRoutingConfig;
import com.lp.vo.ResponseVO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author lp
 */
@FeignClient(name = "notStateServerFeign", configuration = DynamicRoutingConfig.class)
public interface NotStateServerFeign {

    /**
     * 消息通信
     * 只要其他服务接口是post请求 有@PathVariable("userId") Long userId, @RequestBody @Valid Object dto 两个字段即可
     *
     * @param serviceName 服务名
     * @param path        请求路径
     * @param userId      用户
     * @param dto         消息体
     * @return ResponseVO
     */
    @PostMapping(value = "//{serviceName}/{path}/{userId}")
    ResponseVO<?> entrance(@PathVariable("serviceName") String serviceName, @PathVariable("path") String path, @PathVariable("userId") Long userId, @RequestBody @Valid Object dto);


}
