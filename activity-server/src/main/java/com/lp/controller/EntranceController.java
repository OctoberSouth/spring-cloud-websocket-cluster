package com.lp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.lp.dto.Message;
import com.lp.dto.RequestDTO;
import com.lp.service.EntranceService;
import com.lp.vo.ResponseVO;
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
@RequestMapping("entrance")
@Slf4j
public class EntranceController {


    @Resource
    private EntranceService entranceService;

    @PostMapping("{userId}")
    public ResponseVO<?> entrance(@PathVariable Long userId, @RequestBody @Valid Message dto) {
        return this.entranceService.operation(userId, BeanUtil.toBean(dto, RequestDTO.class));
    }

}
