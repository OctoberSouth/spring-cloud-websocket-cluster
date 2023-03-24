package com.lp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.lp.server.MyWebSocket;
import org.springframework.web.bind.annotation.*;

/**
 * 推送
 *
 * @author 10263
 */
@RestController
@RequestMapping("/push")
public class PushController {


    @PostMapping("{userId}")
    public void pushMessage(@PathVariable Long userId, @RequestBody JSONObject message) {
        MyWebSocket.sendMessage(userId, message);
    }
}