package com.lp.util;

import cn.hutool.json.JSONUtil;
import com.lp.dto.Message;
import com.lp.dto.MessageDTO;
import com.lp.socket.WebSocket;

import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket操作类
 *
 * @author lp
 */
public class WebSocketUtil {

    /**
     * 存放用户信息
     */
    private static final ConcurrentHashMap<Long, WebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>(16);


    /**
     * 存储缓存关系
     *
     * @param userId
     * @param webSocket
     */
    public static void putMap(Long userId, WebSocket webSocket) {
        WEB_SOCKET_MAP.put(userId, webSocket);
    }

    /**
     * 删除缓存
     *
     * @param userId
     */
    public static void removeMap(Long userId) {
        WEB_SOCKET_MAP.remove(userId);
    }


    /**
     * 批量发送消息
     *
     * @param dto
     */
    public static void sendMessage(MessageDTO dto) {
        dto.getUserId().forEach(e -> {
            WebSocket webSocket = WEB_SOCKET_MAP.get(e);
            if (webSocket != null) {
                sendMessage(webSocket, dto.getData());
            }
        });
    }

    /**
     * 实现服务器主动推送
     *
     * @param userId
     * @param vo
     * @return
     */
    public static void sendMessage(Long userId, Message vo) {
        WebSocket webSocket = WEB_SOCKET_MAP.get(userId);
        if (webSocket != null) {
            sendMessage(webSocket, vo);
        }
    }

    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    public static WebSocket get(Long userId) {
        return WEB_SOCKET_MAP.get(userId);
    }

    /**
     * 群发消息
     *
     * @param vo
     */
    public static void sendMessage(Message vo) {
        WEB_SOCKET_MAP.forEach((k, v) -> sendMessage(v, vo));
    }

    /**
     * 发送消息
     *
     * @param webSocket
     * @param vo
     */
    private static void sendMessage(WebSocket webSocket, Message vo) {
        //转换成字节数组
        webSocket.getSession().getAsyncRemote().sendText(JSONUtil.toJsonStr(vo));
    }
}
