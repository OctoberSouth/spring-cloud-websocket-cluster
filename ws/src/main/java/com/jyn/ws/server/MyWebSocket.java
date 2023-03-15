package com.jyn.ws.server;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.jyn.annotation.*;
import org.jyn.pojo.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.MultiValueMap;

import java.io.EOFException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 10263
 */
@ServerEndpoint(path = "/ws/{userId}", port = "9090")
@Slf4j
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

    @Value("${spring.application.name}")
    private String applicationName;

    private StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);

    /**
     * 静态常量
     */
    private static final String SOCKET_USER_SPRING_APPLICATION_NAME = "ws:socket:user:spring:application:name";

    /**
     * 当有新的WebSocket连接完成时
     *
     * @param session
     * @param headers
     * @param req
     * @param reqMap
     * @param userId
     * @param pathMap
     */
    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable Long userId, @PathVariable Map pathMap) {
        System.out.println("new connection");
        System.out.println(req);
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
    public void onClose(Session session) {
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
    public void onError(Session session, Throwable throwable) {
        this.stringRedisTemplate.opsForHash().delete(SOCKET_USER_SPRING_APPLICATION_NAME, userId + "");
        throwable.printStackTrace();
        if (Objects.nonNull(this.session) && Objects.nonNull(throwable) && !(throwable instanceof EOFException)) {
            log.error("UserId = {}, 通道ID={}, 出错信息={}", userId, this.session.id(), throwable.toString());
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
    public void onMessage(Session session, String message) {
        System.out.println(message);
        session.sendText("Hello Netty!");
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
                webSocket.session.sendText(JSONObject.toJSONString(message));
            }
        }
    }

}
