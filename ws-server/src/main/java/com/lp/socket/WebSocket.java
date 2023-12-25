package com.lp.socket;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.lp.constants.MqTopicConstants;
import com.lp.constants.RedisKeyConstants;
import com.lp.dto.Message;
import com.lp.dto.UserServerDTO;
import com.lp.enums.DeviceEnum;
import com.lp.enums.ServerEnum;
import com.lp.feign.EntranceFeign;
import com.lp.util.LocalCache;
import com.lp.util.WebSocketUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * @author lp
 */
@ServerEndpoint("/ws/{language}/{userId}/{device}")
@Slf4j
@Component
public class WebSocket {

    private final String applicationName = System.getProperty("SpringApplicationName");
    private final StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    private final EntranceFeign entranceFeign = SpringUtil.getBean(EntranceFeign.class);
    private final DiscoveryClient discoveryClient = SpringUtil.getBean(DiscoveryClient.class);

    /**
     * session
     */
    @Getter
    private Session session;
    /**
     * 用户ID
     */
    @Getter
    private Long userId;
    /**
     * 语言
     */
    @Getter
    private String language;
    /**
     * 设备
     */
    @Getter
    private String device;

    /**
     * 连接生成唯一ID
     */
    @Getter
    private String uuid;

    /**
     * 当有新的WebSocket连接完成时
     *
     * @param session
     * @param language 语言
     * @param userId   用户ID
     * @param device   设备类型
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("language") String language, @PathParam("userId") Long userId, @PathParam("device") String device) {
        if (Objects.isNull(DeviceEnum.getEnum(device))) {
            //设备不匹配直接拒绝连接
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Map<DeviceEnum, WebSocket> webSocketMap = WebSocketUtil.get(userId);
        if (Objects.nonNull(webSocketMap)) {
            //关闭重复连接
            try {
                WebSocket webSocket = webSocketMap.get(DeviceEnum.getEnum(device));
                if (Objects.nonNull(webSocket)) {
                    webSocket.getSession().close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.session = session;
        //根据token获取用户信息
        this.userId = userId;
        this.device = device;
        this.language = language;
        this.uuid = IdUtil.simpleUUID();
        WebSocketUtil.putMap(this.userId, this, DeviceEnum.getEnum(device));
        this.stringRedisTemplate.opsForHash().put(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME, this.userId + ":" + this.device, this.applicationName + ":" + this.uuid);
        //通知上线
        UserServerDTO userDTO = new UserServerDTO(userId, "ws-server", this.applicationName, this.device, this.uuid, true);
        this.stringRedisTemplate.convertAndSend(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION, JSONUtil.toJsonStr(userDTO));
    }

    /**
     * 当有WebSocket连接关闭时
     */
    @OnClose
    public void onClose(Session session) {
        close(session);
    }

    /**
     * 当有WebSocket抛出异常时
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        //删除缓存信息
        close(session);
        log.error("WebSocket异常关闭：{},用户：{}，设备：{}", throwable, userId, device);
    }

    /**
     * 关闭连接
     *
     * @param session
     */
    private void close(Session session) {
        Map<DeviceEnum, WebSocket> webSocketMap = WebSocketUtil.get(this.userId);
        if (Objects.equals(webSocketMap.get(DeviceEnum.getEnum(this.device)).getSession(), session)) {
            //删除缓存信息
            Object value = this.stringRedisTemplate.opsForHash().get(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME, this.userId + ":" + this.device);
            if (Objects.nonNull(value) && Objects.equals(value.toString(), this.applicationName + ":" + this.uuid)) {
                this.stringRedisTemplate.opsForHash().delete(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME, this.userId + ":" + this.device);
            }
            WebSocketUtil.removeMap(this.userId, this.device, this.uuid);
            //通知下线
            UserServerDTO userDTO = new UserServerDTO(userId, "ws-server", this.applicationName, this.device, this.uuid, false);
            this.stringRedisTemplate.convertAndSend(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION, JSONUtil.toJsonStr(userDTO));
        }
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @OnMessage
    public void onMessage(Session session, String bytes) {
        //此举是为了保证消息的有序性
        //反序列化 转成对应Java对象
        Message message = JSONUtil.toBean(bytes, Message.class);
        //获取服务名
        ServerEnum serverEnum = ServerEnum.getEnum(message.getServerName());
        if (Objects.isNull(serverEnum)) {
            //服务为空不处理
            return;
        }
        //连接服务名
        String serverName = message.getServerName();
        if (serverEnum.getState()) {
                /*
                   有状态服务才需要这样获取
                 */
            serverName = LocalCache.userServer.get(this.userId + serverName);
            if (Objects.isNull(serverName)) {
                //这样可以指定用户访问到指定的服务
                Object server = this.stringRedisTemplate.opsForHash().get(RedisKeyConstants.USER_SERVER_NAME_HASH, message.getServerName() + userId);
                if (Objects.isNull(server)) {
                    //随机获取一个
                    serverName = getName(message.getServerName());
                    this.stringRedisTemplate.opsForHash().put(RedisKeyConstants.USER_SERVER_NAME_HASH, message.getServerName() + userId, serverName);
                } else {
                    serverName = server.toString();
                }
                LocalCache.userServer.put(this.userId + serverName, serverName);
            }
            List<String> servicesOfServer = discoveryClient.getServices();
            if (!servicesOfServer.contains(serverName)) {
                //如果不在服务列表里面，说明服务已经重启过，还是要随机获取一个
                serverName = getName(message.getServerName());
                //添加到缓存里面
                this.stringRedisTemplate.opsForHash().put(RedisKeyConstants.USER_SERVER_NAME_HASH, userId + message.getServerName(), serverName);
                LocalCache.userServer.put(this.userId + serverName, serverName);
            }
        }
        //调用远程服务
        Message data = this.entranceFeign.entrance(serverName, userId, language, message);
        if (Objects.nonNull(data)) {
            //不为null的话，转换成字节数组 发送消息
            session.getAsyncRemote().sendText(JSONUtil.toJsonStr(data));
        }
    }

    /**
     * 通过服务名随机获取一个服务
     *
     * @param serverName 服务名
     * @return String
     */
    private String getName(String serverName) {
        List<String> servicesOfServer = discoveryClient.getServices();
        List<String> list = servicesOfServer.stream().filter(e -> e.startsWith(serverName)).toList();
        Random random = new Random();
        int n = random.nextInt(list.size());
        //根据服务得到服务IP
        return list.get(n);
    }
}
