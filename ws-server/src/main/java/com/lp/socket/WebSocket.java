package com.lp.socket;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.lp.constants.MqTopicConstants;
import com.lp.constants.RedisKeyConstants;
import com.lp.dto.Message;
import com.lp.dto.UserServerDTO;
import com.lp.enums.DeviceEnum;
import com.lp.enums.ServerEnum;
import com.lp.feign.NotStateServerFeign;
import com.lp.feign.StateServerFeign;
import com.lp.pojo.WebSocketInfo;
import com.lp.pool.MessageUserPool;
import com.lp.util.LocalCache;
import com.lp.util.WebSocketUtil;
import com.lp.vo.ResponseVO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

    private final StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    private final StateServerFeign stateServerFeign = SpringUtil.getBean(StateServerFeign.class);
    private final NotStateServerFeign notStateServerFeign = SpringUtil.getBean(NotStateServerFeign.class);
    private final DiscoveryClient discoveryClient = SpringUtil.getBean(DiscoveryClient.class);
    private final Environment environment = SpringUtil.getBean(Environment.class);


    /**
     * 当有新的WebSocket连接完成时
     *
     * @param session
     * @param userId  用户ID
     * @param device  设备类型
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId, @PathParam("device") String device) throws UnknownHostException {
        if (Objects.isNull(DeviceEnum.getEnum(device))) {
            //设备不匹配直接拒绝连接
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Map<DeviceEnum, WebSocketInfo> webSocketMap = WebSocketUtil.get(userId);
        if (Objects.nonNull(webSocketMap)) {
            //关闭重复连接
            try {
                WebSocketInfo webSocket = webSocketMap.get(DeviceEnum.getEnum(device));
                if (Objects.nonNull(webSocket)) {
                    webSocket.getSession().close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        WebSocketInfo webSocket = new WebSocketInfo();
        webSocket.setSession(session);
        //根据token获取用户信息
        webSocket.setUserId(userId);
        webSocket.setDevice(device);
        webSocket.setUuid(IdUtil.simpleUUID());
        webSocket.setAddress(InetAddress.getLocalHost().getHostAddress() + ":" + (StrUtil.isBlank(environment.getProperty("server.port")) ? "8080" : environment.getProperty("server.port")));
        WebSocketUtil.putMap(userId, webSocket, DeviceEnum.getEnum(device));
        this.stringRedisTemplate.opsForHash().put(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME, userId + "@" + device, webSocket.getAddress() + "@" + webSocket.getUuid());
        //通知上线
        UserServerDTO userDTO = new UserServerDTO(userId, "ws-server", webSocket.getAddress(), device, webSocket.getUuid(), true);
        this.stringRedisTemplate.convertAndSend(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION, JSONUtil.toJsonStr(userDTO));
    }

    /**
     * 当有WebSocket连接关闭时
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId, @PathParam("device") String device) {
        close(session, userId, device);
    }

    /**
     * 当有WebSocket抛出异常时
     */
    @OnError
    public void onError(Session session, @PathParam("userId") Long userId, @PathParam("device") String device, Throwable throwable) {
        //删除缓存信息
        close(session, userId, device);
        log.error("WebSocket异常关闭：{},用户：{}，设备：{}", throwable, userId, device);
    }

    /**
     * 关闭连接
     *
     * @param session
     */
    private void close(Session session, @PathParam("userId") Long userId, @PathParam("device") String device) {
        Map<DeviceEnum, WebSocketInfo> webSocketMap = WebSocketUtil.get(userId);
        WebSocketInfo webSocketInfo = webSocketMap.get(DeviceEnum.getEnum(device));
        if (Objects.equals(webSocketInfo.getSession(), session)) {
            //删除缓存信息
            Object value = this.stringRedisTemplate.opsForHash().get(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME, userId + "@" + device);
            if (Objects.nonNull(value) && Objects.equals(value.toString(), webSocketInfo.getAddress() + "@" + webSocketInfo.getUuid())) {
                this.stringRedisTemplate.opsForHash().delete(RedisKeyConstants.SOCKET_USER_SPRING_APPLICATION_NAME, userId + "@" + device);
            }
            WebSocketUtil.removeMap(userId, device, webSocketInfo.getUuid());
            //通知下线
            UserServerDTO userDTO = new UserServerDTO(userId, "ws-server", webSocketInfo.getAddress(), device, webSocketInfo.getUuid(), false);
            this.stringRedisTemplate.convertAndSend(MqTopicConstants.SOCKET_USER_SPRING_APPLICATION, JSONUtil.toJsonStr(userDTO));
        }
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @OnMessage
    public void onMessage(Session session, @PathParam("userId") Long userId, String bytes) {
        //此举是为了保证消息的有序性
        //反序列化 转成对应Java对象
        //按用户区分，采用异步处理
        MessageUserPool.getMessageUserPool(userId).execute(() -> {
            Message message = JSONUtil.toBean(bytes, Message.class);
            //获取服务名
            ServerEnum serverEnum = ServerEnum.getEnum(message.getServerName());
            if (Objects.isNull(serverEnum)) {
                //服务为空不处理
                return;
            }
            //连接服务名
            String serverName = message.getServerName();
            //调用远程服务
            ResponseVO<?> vo;
            if (serverEnum.getState()) {
                /*
                   有状态服务才需要这样获取
                 */
                serverName = LocalCache.userServer.get(userId + serverName);
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
                    LocalCache.userServer.put(userId + serverName, serverName);
                }
                List<String> servicesOfServer = discoveryClient.getServices();
                if (!servicesOfServer.contains(serverName)) {
                    //如果不在服务列表里面，说明服务已经重启过，还是要随机获取一个
                    serverName = getName(message.getServerName());
                    //添加到缓存里面
                    this.stringRedisTemplate.opsForHash().put(RedisKeyConstants.USER_SERVER_NAME_HASH, userId + message.getServerName(), serverName);
                    LocalCache.userServer.put(userId + serverName, serverName);
                }
                vo = this.stateServerFeign.entrance(serverName, message.getPath(), userId, JSONUtil.parseObj(message.getData()));
            } else {
                vo = this.notStateServerFeign.entrance(serverName, message.getPath(), userId, JSONUtil.parseObj(message.getData()));
            }
            if (Objects.nonNull(vo)) {
                //不为null的话，转换成字节数组 发送消息
                message.setData(vo);
                session.getAsyncRemote().sendText(JSONUtil.toJsonStr(message));
            }
        });
    }

    /**
     * 通过服务名随机获取一个服务
     *
     * @param serverName 服务名
     * @return String
     */
    private String getName(String serverName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serverName);
        Random random = new Random();
        int n = random.nextInt(instances.size());
        //根据服务得到服务IP
        ServiceInstance instance = instances.get(n);
        return instance.getHost() + ":" + instance.getPort();
    }
}
