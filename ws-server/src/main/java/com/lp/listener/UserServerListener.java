package com.lp.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lp.dto.UserServerDTO;
import com.lp.util.LocalCache;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 用户服务绑定通知
 */
@Component
public class UserServerListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UserServerDTO wsUser = JSONUtil.toBean(message.toString(), UserServerDTO.class);
        if (StrUtil.isBlank(wsUser.getServerName())) {
            LocalCache.userServer.remove(wsUser.getUid() + wsUser.getServer());
        } else {
            LocalCache.userServer.put(wsUser.getUid() + wsUser.getServer(), wsUser.getServerName());
        }
    }

}
