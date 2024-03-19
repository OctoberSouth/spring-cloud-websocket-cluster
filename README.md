# spring-cloud-websocket-cluster

#### 介绍

基于spring-cloud与redis的websocket 集群解决方案

#### 软件架构

本项目基于spring-cloud 与 redis 做出的websocket集群方案

#### 需要第三方服务

1. nacos
2. redis

#### 使用说明

1. 所有服务都可以启动多个，保证了动态扩容
3. common 为基础包，包含了常用的枚举，比如服务类型、通用返回等数据
4. gateway-server 为网关服务，重点为WebSocketGatewayFilter类，此可以保证了网关动态连接到ws服务，建议鉴权可以在此处完成，保证连接到websocket的都是有效连接
5. ws-facade 需要通过其他服务直接给用户发送消息直接集成这个即可
7. ws-server websocket服务，与用户建立联系，接收发送消息，重点可看WebSocket类，onMessage 同步异步发送消息，给有状态，无状态服务发送消息都在这里
8. open-server 是一个对外开放的服务，可以让其他不是该微服务架构体系下的也能给用户发送消息
9. activity-server 服务是一个示例服，该服务可以接收websocket的消息，完成自己的业务处理，然后将消息返回给用户

#### 项目优势

1. 传统websocket集群服务服务端给客户端发送消息，需要采用MQ进行广播发送，每个websocket服务都要接收消息，然后解析发送，这种方案链路太长，同时严重浪费性能
2. 本项目纯微服务架构，直接通过feign接口调用的方式，直接将消息发送到用户链接的socket服务上，不需要走第三方服务
3. websocket服务接受同步与异步两种消息类型
4. websocket服务支持有状态服务与无状态服务两种发消息
5. 开放接口，非该服务架构体系下的项目也能给用户发送消息
6. 需要使用到websocket收发消息的，可快速集成
7. 多服务挤下线功能，支持多端设备同时在线，但同一设备只能链接一个socket，设备类型可以在DeviceEnum枚举类里面设置
8. 查找用户发送消息，基本是在内存查找，效率杠杠的
9. 使用最新springcloud2023 springboot3.2.0 java21,开启虚拟线程处理，访问速度更快了
10. 待更新

#### 应用场景

1. 游戏
2. IM
3. 客服系统
4. 以及其他需要及时通讯的项目
5. 其他需要提高响应速度的项目，可以将http改用websocket

#### 联系我

如有需要使用，但有不明白的地方，可以加我微信：lpshiyue 暗号：天王盖地虎