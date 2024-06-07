package com.lp.util;

import cn.hutool.json.JSONUtil;
import com.lp.dto.Message;
import com.lp.enums.DeviceEnum;
import com.lp.pojo.WebSocketInfo;

import java.util.Map;
import java.util.Objects;
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
    private static final ConcurrentHashMap<Long, ConcurrentHashMap<DeviceEnum, WebSocketInfo>> WEB_SOCKET_MAP = new ConcurrentHashMap<>(16);


    /**
     * 存储缓存关系
     *
     * @param userId
     * @param webSocket
     */
    public static void putMap(Long userId, WebSocketInfo webSocket, DeviceEnum deviceEnum) {
        ConcurrentHashMap<DeviceEnum, WebSocketInfo> webSocketMap = get(userId);
        if (Objects.isNull(webSocketMap)) {
            webSocketMap = new ConcurrentHashMap<>(16);
        }
        webSocketMap.put(deviceEnum, webSocket);
        WEB_SOCKET_MAP.put(userId, webSocketMap);
    }

    /**
     * 删除缓存
     *
     * @param userId
     */
    public static void removeMap(Long userId, String device, String uuid) {
        DeviceEnum deviceEnum = DeviceEnum.getEnum(device);
        ConcurrentHashMap<DeviceEnum, WebSocketInfo> webSocketMap = get(userId);
        if (Objects.nonNull(webSocketMap) && Objects.nonNull(deviceEnum)) {
            WebSocketInfo webSocket = webSocketMap.get(deviceEnum);
            if (Objects.nonNull(webSocket) && Objects.equals(webSocket.getUuid(), uuid)) {
                webSocketMap.remove(deviceEnum);
            }
        }
    }


    /**
     * 实现服务器主动推送
     *
     * @param userId
     * @param vo
     * @return
     */
    public static void sendMessage(Long userId, Message<?> vo) {
        Map<DeviceEnum, WebSocketInfo> webSocketMap = get(userId);
        if (Objects.nonNull(webSocketMap)) {
            webSocketMap.forEach((k, v) -> sendMessage(v, vo));
        }
    }

    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    public static ConcurrentHashMap<DeviceEnum, WebSocketInfo> get(Long userId) {
        return WEB_SOCKET_MAP.get(userId);
    }

    /**
     * 群发消息
     *
     * @param vo
     */
    public static void sendMessage(Message<?> vo) {
        WEB_SOCKET_MAP.forEach((k, v) -> v.forEach((k1, v2) -> sendMessage(v2, vo)));
    }

    /**
     * 发送消息
     *
     * @param webSocket
     * @param vo
     */
    private static void sendMessage(WebSocketInfo webSocket, Message<?> vo) {
        //转换成字节数组
        webSocket.getSession().getAsyncRemote().sendText(JSONUtil.toJsonStr(vo));
    }
}
