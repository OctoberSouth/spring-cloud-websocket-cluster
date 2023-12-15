package com.lp.controller;

import com.lp.dto.Message;
import com.lp.dto.MessageDTO;
import com.lp.util.WebSocketUtil;
import com.lp.vo.R;
import com.lp.vo.ResponseVO;
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
    public ResponseVO<Void> pushMessage(@PathVariable Long userId, @RequestBody Message vo) {
        WebSocketUtil.sendMessage(userId, vo);
        return R.success();
    }

    /**
     * 群发消息
     *
     * @param vo
     */
    @PostMapping()
    public ResponseVO<Void> pushMessage(@RequestBody Message vo) {
        WebSocketUtil.sendMessage(vo);
        return R.success();
    }


    /**
     * 批量发送消息，直接把用户传给ws，减少feign调用
     *
     * @param dto
     */
    @PostMapping("batch")
    public ResponseVO<Void> pushBatchMessage(@RequestBody MessageDTO dto) {
        WebSocketUtil.sendMessage(dto);
        return R.success();
    }
}
