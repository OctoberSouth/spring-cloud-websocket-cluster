package com.lp.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lp.dto.UserServerDTO;
import com.lp.util.LocalCache;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 用户信息ws连接信息缓存
 */
@Component
public class WsUserServerListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UserServerDTO userDTO = JSONUtil.toBean(message.toString(), UserServerDTO.class);
        if (StrUtil.isBlank(userDTO.getServerName())) {
            LocalCache.wsUser.remove(userDTO.getUid());
        } else {
            LocalCache.wsUser.put(userDTO.getUid(), userDTO.getServerName());
        }
    }

}
