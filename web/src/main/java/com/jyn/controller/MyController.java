package com.jyn.controller;


import com.alibaba.fastjson2.JSONObject;
import com.jyn.dto.UserDTO;
import com.jyn.service.PushService;
import com.jyn.vo.BaseVO;
import com.jyn.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 10263
 */
@RestController
@RequestMapping("/my")
@Slf4j
@Tag(name = "UserControllerApi", description = "用户的增删改查")
public class MyController {

    @Resource
    private PushService pushService;

    @GetMapping("/del/{name}")
    @Operation(summary = "删除用户",
            description = "根据姓名删除用户")
    public void delUser(@Parameter(description = "姓名") @PathVariable String name) {
        log.info("name============" + name);
    }


    @GetMapping("/get/{id}")
    @Operation(summary = "查找用户",
            description = "根据ID查找用户")
    public BaseVO<UserVO> getUserById(@Parameter(name = "id", description = "Id") @PathVariable Long id) {
        UserVO userVO = new UserVO();
        userVO.setAge(11);
        return BaseVO.success(userVO);
    }

    @PostMapping("/add")
    @Operation(summary = "添加用户",
            description = "添加用户")
    public String addUser(@Validated @RequestBody UserDTO user) {
        return "new UserVO()";
    }

    @PostMapping("/add/name")
    @Operation(summary = "添加用户名字",
            description = "添加用户名字")
    public String addUser(@Parameter(description = "用户名")
                          @RequestParam String name) {
        return "new UserVO()";
    }

    @PostMapping("/push/{userId}")
    @Operation(summary = "socket推送测试",
            description = "添加用户名字")
    public String push(@PathVariable Long userId, @RequestBody JSONObject message) {
        this.pushService.pushMessage(userId, message);
        return "推送成功";
    }

}