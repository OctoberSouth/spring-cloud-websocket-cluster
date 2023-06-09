package com.lp.controller;

import com.lp.socket.WebSocket;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Future;

/**
 * 推送
 *
 * @author 10263
 */
@RestController
@RequestMapping("push")
public class PushController {


    @PostMapping("{userId}")
    public void pushMessage(@PathVariable Long userId, @RequestBody Object message) {
        WebSocket.sendMessage(userId, message);
    }

    /**
     * 群发消息
     *
     * @param message
     */
    @PostMapping()
    public void pushMessage(@RequestBody Object message) {
        WebSocket.sendMessage(message);
    }

    /**
     * 有返回发送消息
     *
     * @param userId
     * @param message
     * @return
     */
    @PostMapping("future/{userId}")
    public Future<Void> pushMessageFuture(@PathVariable Long userId, @RequestBody Object message) {
        return WebSocket.sendMessageFuture(userId, message);
    }
}
