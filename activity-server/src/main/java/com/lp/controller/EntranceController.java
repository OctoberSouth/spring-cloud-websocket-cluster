package com.lp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.lp.dto.Message;
import com.lp.dto.RequestDTO;
import com.lp.feign.PushFeign;
import com.lp.service.EntranceService;
import com.lp.vo.ResponseVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 消息入口
 *
 * @author lp
 */
@RestController
@RequestMapping("entrance")
@Slf4j
public class EntranceController {


    @Resource
    private EntranceService entranceService;

    @Resource
    private PushFeign pushFeign;

    @PostMapping("{userId}")
    public Message entrance(@PathVariable Long userId, @RequestBody @Valid Message dto) {
        return operation(userId, dto);
    }


    /**
     * 异步消息
     * 不需要实时返回时使用
     *
     * @param userId
     * @param dto
     */
    @Async
    @PostMapping("{socketServerName}/{userId}")
    public void asyncEntrance(@PathVariable String socketServerName, @PathVariable Long userId, @RequestBody @Valid Message dto) {
        Message vo = operation(userId, dto);
        if (Objects.nonNull(vo)) {
            //返回部位空，则发送消息到客户端
            this.pushFeign.pushMessage(socketServerName, userId, vo);
        }
    }

    /**
     * 具体业务处理
     *
     * @param userId
     * @param dto
     * @return
     */
    private Message operation(Long userId, Message dto) {
        ResponseVO vo = this.entranceService.operation(userId, BeanUtil.toBean(dto, RequestDTO.class));
        if (vo == null) {
            return null;
        }
        return BeanUtil.toBean(vo, Message.class);
    }
}
