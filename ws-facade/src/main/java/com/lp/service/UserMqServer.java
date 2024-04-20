package com.lp.service;

import cn.hutool.json.JSONUtil;
import com.lp.constants.MqTopicConstants;
import com.lp.dto.UserServerDTO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 用户服务关系建立与解除
 */
@Service
public class UserMqServer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 解除关系
     *
     * @param uid    用户ID
     * @param server 服务
     */
    public void relieve(Long uid, String server) {
        UserServerDTO dto = new UserServerDTO();
        dto.setUserId(uid);
        dto.setServer(server);
        this.stringRedisTemplate.convertAndSend(MqTopicConstants.USER_SERVER_NAME, JSONUtil.toJsonStr(dto));
    }

    /**
     * 建立关系
     *
     * @param uid        用户ID
     * @param server     服务
     * @param serverName 服务IP加端口，例如：127.0.0.1:9090
     */
    public void build(Long uid, String server, String serverName) {
        UserServerDTO dto = new UserServerDTO();
        dto.setUserId(uid);
        dto.setServer(server);
        dto.setServerName(serverName);
        this.stringRedisTemplate.convertAndSend(MqTopicConstants.USER_SERVER_NAME, JSONUtil.toJsonStr(dto));
    }
}
