package com.lp.controller;

import com.lp.dto.Message;
import com.lp.dto.MessageDTO;
import com.lp.service.PushService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 消息入口
 *
 * @author lp
 */
@RestController
@RequestMapping("ws")
@Slf4j
public class WebSocketController {

    @Resource
    private PushService pushService;

    /**
     * 指定人发送
     * 其他项目对用户发送消息
     * 目前一般用不到
     *
     * @param dto
     */
    @PostMapping("push")
    public void pushMessage(@RequestBody @Valid MessageDTO dto) {
        this.pushService.pushMessage(dto.getData(), dto.getUserId());
    }

    /**
     * 群发
     * 其他项目对用户群发发送消息
     * 目前一般用不到
     *
     * @param dto
     */
    @PostMapping("push/all")
    public void pushAllMessage(@RequestBody @Valid Message dto) {
        this.pushService.pushMessage(dto);
    }

    /**
     * 指定人发送
     * 其他项目对用户发送消息
     * 目前一般用不到
     *
     * @param dto
     * @param userId
     */
    @PostMapping("push/{userId}")
    public void pushMessage(@PathVariable Long userId, @RequestBody @Valid Message dto) {
        this.pushService.pushMessage(dto, userId);
    }

}
