package com.lp.server;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 10263
 */
@Component
@ServerEndpoint("/ws/{userId}")
public class MyWebSocket {

    /**
     * 存放用户信息
     */
    private static final ConcurrentHashMap<Long, MyWebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>(16);
    /**
     * session
     */
    private Session session;

    private Long userId;

    private String applicationName = System.getProperty("SpringApplicationName");

    private StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);

    /**
     * 静态常量
     */
    private static final String SOCKET_USER_SPRING_APPLICATION_NAME = "ws:socket:user:spring:application:name";

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
        session.getBasicRemote().sendText("Hello Netty!");
    }


    /**
     * 实现服务器主动推送
     *
     * @param userId
     * @param message
     * @return
     */
    public static void sendMessage(Long userId, JSONObject message) {
        MyWebSocket webSocket = WEB_SOCKET_MAP.get(userId);
        if (webSocket != null) {
            synchronized (webSocket.session) {
                webSocket.session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
            }
        }
    }

}
