package com.lp.controller;


import com.lp.service.PushService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author 10263
 */
@RestController
@RequestMapping("/my")
public class MyController {

    @Resource
    private PushService pushService;

    @PostMapping("/push/{userId}")
    public String push(@PathVariable Long userId, @RequestBody Object message) {
        this.pushService.pushMessage(userId, message);
        return "推送成功";
    }

}