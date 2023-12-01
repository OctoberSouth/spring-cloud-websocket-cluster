package com.lp.controller;

import com.lp.dto.Message;
import com.lp.dto.MessageDTO;
import com.lp.util.WebSocketUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 推送
 *
 * @author lp
 */
@RestController
@RequestMapping("push")
public class PushController {


    @PostMapping("{userId}")
    public void pushMessage(@PathVariable Long userId, @RequestBody Message vo) {
        WebSocketUtil.sendMessage(userId, vo);
    }

    /**
     * 群发消息
     *
     * @param vo
     */
    @PostMapping()
    public void pushMessage(@RequestBody Message vo) {
        WebSocketUtil.sendMessage(vo);
    }


    /**
     * 批量发送消息，直接把用户传给ws，减少feign调用
     *
     * @param dto
     */
    @PostMapping("batch")
    public void pushBatchMessage(@RequestBody MessageDTO dto) {
        WebSocketUtil.sendMessage(dto);
    }
}
