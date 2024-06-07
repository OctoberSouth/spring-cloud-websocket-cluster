package com.lp.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lp.dto.UserServerDTO;
import com.lp.enums.DeviceEnum;
import com.lp.pojo.WebSocketInfo;
import com.lp.util.WebSocketUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;

/**
 * 多服务挤下线
 *
 * @author 10263
 */
@Component
public class WsUserServerCloseListener implements MessageListener {

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        UserServerDTO wsUser = JSONUtil.toBean(message.toString(), UserServerDTO.class);
        Map<DeviceEnum, WebSocketInfo> webSocketMap = WebSocketUtil.get(wsUser.getUserId());
        String address;
        try {
            address = InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //不是同个客户端，挤下线
        if (Objects.nonNull(webSocketMap) && StrUtil.isNotBlank(wsUser.getServerName()) && !Objects.equals(wsUser.getServerName(), address)) {
            try {
                WebSocketInfo webSocket = webSocketMap.get(DeviceEnum.getEnum(wsUser.getDevice()));
                if (Objects.nonNull(webSocket)) {
                    webSocket.getSession().close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
