package com.lp.controller;


import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.lp.service.PushService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 10263
 */
@RestController
@RequestMapping("/my")
public class MyController {

    @Resource
    private PushService pushService;

    @PostMapping("/push/{userId}")
    public String push(@PathVariable Long userId, @RequestBody JSONObject message) {
        this.pushService.pushMessage(userId, message);
        return "推送成功";
    }

    @GetMapping("/getAllInstances")
    public String getAllInstances() throws NacosException {
        NamingService naming = NamingFactory.createNamingService("192.168.137.174");
        System.out.println(naming.getServicesOfServer(1, 10));
        return "推送成功";
    }

}