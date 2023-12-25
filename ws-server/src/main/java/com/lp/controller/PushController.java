package com.lp.controller;

import com.lp.dto.Message;
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
    public ResponseVO<Void> pushMessage(@PathVariable Long userId, @RequestBody Message<?> vo) {
        WebSocketUtil.sendMessage(userId, vo);
        return R.success();
    }

    /**
     * 群发消息
     *
     * @param vo
     */
    @PostMapping()
    public ResponseVO<Void> pushMessage(@RequestBody Message<?> vo) {
        WebSocketUtil.sendMessage(vo);
        return R.success();
    }
}
