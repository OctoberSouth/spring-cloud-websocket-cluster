# spring-cloud-websocket-cluster

#### 介绍

基于spring-cloud与redis的websocket集群解决方案

#### 软件架构

本项目基于spring-cloud 与 redis 做出的websocket集群方案

#### 需要第三方服务

1. nacos
2. redis

#### 使用说明

1. ws子项目为websocket服务，可以启动多个
2. ws-facade子项目为发送消息包，需要通过websocket推送消息的直接在项目种引入，然后调用PushService下面的pushMessage方法
3. web子项目为调用实例，可以参考

#### 项目优势

1. 传统websocket集群服务服务端给客户端发送消息，需要采用MQ进行广播发送，每个websocket服务都要接收消息，然后解析发送，这种方案链路太长，同时严重浪费性能
2. 本项目直接通过feign接口调用的方式，直接将消息发送到用户链接的socket服务上，不需要走第三方服务
3. 待更新