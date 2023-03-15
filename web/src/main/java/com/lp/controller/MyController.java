package com.lp.controller;


import com.alibaba.fastjson2.JSONObject;
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

}