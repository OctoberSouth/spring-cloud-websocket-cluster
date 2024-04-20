package com.lp.listener;

import cn.hutool.json.JSONUtil;
import com.lp.dto.UserServerDTO;
import com.lp.util.LocalCache;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户信息ws连接信息缓存
 *
 * @author 10263
 */
@Component
public class WsUserServerListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UserServerDTO userDTO = JSONUtil.toBean(message.toString(), UserServerDTO.class);
        String key = userDTO.getUserId() + "@" + userDTO.getDevice();
        if (userDTO.getLogin()) {
            LocalCache.wsUser.put(key, userDTO.getServerName() + "@" + userDTO.getUuid());
        } else {
            String value = LocalCache.wsUser.get(key);
            if (Objects.equals(value, userDTO.getServerName() + "@" + userDTO.getUuid())) {
                LocalCache.wsUser.remove(key);
            }
        }
    }

}
