package com.lp.socket;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author 10263
 */
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocket {

    /**
     * 存放用户信息
     */
    private static final ConcurrentHashMap<Long, WebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>(16);
    /**
     * 静态常量
     */
    private static final String SOCKET_USER_SPRING_APPLICATION_NAME = "ws:socket:user:spring:application:name";
    /**
     * session
     */
    private Session session;
    private Long userId;
    private String applicationName = System.getProperty("SpringApplicationName");
    private StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);

    /**
     * 实现服务器主动推送
     *
     * @param userId
     * @param message
     * @return
     */
    public static void sendMessage(Long userId, JSONObject message) {
        WebSocket webSocket = WEB_SOCKET_MAP.get(userId);
        if (webSocket != null) {
            synchronized (webSocket.session) {
                webSocket.session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
            }
        }
    }

    /**
     * 有返回的发送消息
     *
     * @param userId
     * @param message
     * @return
     */
    public static Future<Void> sendMessageFuture(Long userId, JSONObject message) {
        WebSocket webSocket = WEB_SOCKET_MAP.get(userId);
        if (webSocket != null) {
            synchronized (webSocket.session) {
                return webSocket.session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
            }
        }
        return null;
    }

    /**
     * 群发消息
     *
     * @param message
     */
    public static void sendMessage(JSONObject message) {
        WEB_SOCKET_MAP.forEach((k, v) -> {
            synchronized (v.session) {
                v.session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
            }
        });
    }

    /**
     * 当有新的WebSocket连接完成时
     *
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        System.out.println("new connection");
        System.out.println(userId);
        this.session = session;
        //根据token获取用户信息
        this.userId = userId;
        WEB_SOCKET_MAP.put(this.userId, this);
        this.stringRedisTemplate.opsForHash().put(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "", applicationName);
    }

    /**
     * 当有WebSocket连接关闭时
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("one connection closed");
        this.stringRedisTemplate.opsForHash().delete(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "");
        WEB_SOCKET_MAP.remove(this);
        //处理自己的业务逻辑
        session.close();
    }

    /**
     * 当有WebSocket抛出异常时
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        this.stringRedisTemplate.opsForHash().delete(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "");
        throwable.printStackTrace();
        if (Objects.nonNull(this.session) && Objects.nonNull(throwable) && !(throwable instanceof EOFException)) {
            System.err.println("UserId = " + userId + ", 通道ID=" + this.session.getId() + ", 出错信息=" + throwable);
        }
        if (Objects.nonNull(session) && session.isOpen()) {
            WEB_SOCKET_MAP.remove(this);
            session.close();
        }
    }

    /**
     * 当接收到字符串消息时
     *
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println(message);
        session.getBasicRemote().sendText("收到你的消息了!" + message);
    }

}
