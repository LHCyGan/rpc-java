## RPC-JAVA

本项目基于 Spring + Netty + Zookeeper + Protostuff 从零开始设计实现一个**轻量级分布式 RPC 框架**

- **Java 基础**
    - 动态代理机制
    - Java I/O 系统
    - 序列化机制以及序列化框架（Kryo ......）的基本使用
    - Java 网络编程（Socket 编程）
    - Java 并发/多线程
    - Java 反射
    - Java 注解
    - ..........

- **Netty 4.x**：使 NIO 编程更加容易，屏蔽了 Java 底层的 NIO 细节

- **Zookeeper**：提供服务注册与发现功能，开发分布式系统的必备选择，具备天生的集群能力

- **Spring**：最强大的依赖注入框架，业界的权威标准

## 功能列表

- [x] 使用 Spring 提供依赖注入与参数配置
- [ ] 集成 Spring 通过注解注册服务
- [ ] 集成 Spring 通过注解消费服务
- [x] 使用 Netty 进行网络传输
    - [x] 基于开源的序列化框架 Protostuff 实现消息对象的序列化/反序列化
        - [ ] **可优化**：用户通过配置文件指定序列化方式，避免硬编码
    - [x] 自定义编解码器
    - [x] TCP 心跳机制
        - [ ] **可优化**：自定义应用层的 Netty 心跳机制
    - [x] 使用 JDK/CGLIB 动态代理机制调用远程方法

- [x] 使用 Zookeeper（ZkClient 客户端）实现服务注册和发现
    - [ ] **可优化**：基于 SPI 机制使得用户可以通过配置文件指定注册与发现中心的实现方式，避免硬编码
    - [x] 客户端调用远程服务的时候进行负载均衡 ：调用服务的时候，从很多服务地址中根据相应的负载均衡策略选取一个服务地址。目前使用的策略为随机负载均衡
        - [ ] **可优化**：支持多种负载均衡策略

**本框架的核心功能模块**：

- `rpc-common`：包含封装 <u>RPC 请求与响应</u>（网络传输）的实体类/消息体 `entity`，Netty 编解码器 `codec` 以及序列化/反序列 `serialize`
- `rpc-server`：Netty / RPC 服务端，处理并响应客户端的请求 / 消息体）
- `rpc-client`：Netty / RPC 客户端，向服务端发送请求 / 消息体 + 接收服务端的响应
- `rpc-registry`：定义服务注册与发现行为的接口
- `rpc-registry-zookeeper`：基于 Zookeeper 及其客户端 ZkClient 实现服务的注册与发现

**下述这三个模块展示了如何使用本框架**：

- `rpc-sample-api`：定义服务接口（RPC 接口）
- `rpc-sample-server`：实现服务接口（RPC 接口），启动 / 发布 RPC 服务
- `rpc-sample-client`：调用 RPC 服务（使用动态代理调用远程方法）

## 使用说明

**框架的使用样例代码存放在 `rpc-sample-xxx` 包中**。

要想使用这个框架，我们需要该框架的服务注册组件和 RPC 服务器注入进服务端包 **rpc-sample-server** 中，将服务发现组件和 RPC 客户端（代理）注入进客户端 **rpc-sample-client** 包中，下面详细讲解一下本框架的基本使用 ⬇️