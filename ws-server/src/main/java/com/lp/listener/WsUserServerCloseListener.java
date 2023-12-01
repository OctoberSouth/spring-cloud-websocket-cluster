package com.lp.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lp.dto.UserServerDTO;
import com.lp.socket.WebSocket;
import com.lp.util.WebSocketUtil;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * 多服务挤下线
 */
@Component
public class WsUserServerCloseListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UserServerDTO wsUser = JSONUtil.toBean(message.toString(), UserServerDTO.class);
        WebSocket webSocket = WebSocketUtil.get(wsUser.getUid());
        //不是同个客户端，挤下线
        if (Objects.nonNull(webSocket) && StrUtil.isNotBlank(wsUser.getServerName()) && !Objects.equals(wsUser.getServerName(), System.getProperty("SpringApplicationName"))) {
            try {
                webSocket.getSession().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
