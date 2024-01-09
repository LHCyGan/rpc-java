package com.lhcygan.rpcserver;

import com.lhcygan.rpccommon.codec.RpcDecoder;
import com.lhcygan.rpccommon.codec.RpcEncoder;
import com.lhcygan.rpccommon.model.entity.RpcRequest;
import com.lhcygan.rpccommon.model.entity.RpcResponse;
import com.lhcygan.rpcregistry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RpcServer.class);

    // 服务地址（比如服务被暴露在 Netty 的 8000 端口，服务地址就是 127.0.0.1:8000）
    private String serviceAddress;

    // 服务注册组件（Zookeeper）
    private ServiceRegistry serviceRegistry;

    // 存储服务名称与服务对象之间的映射关系
    private Map<String, Object> handlerMap = new HashMap<>();

    /**
     * 该构造函数用于提供给用户在配置文件中注入服务地址和服务注册组件
     * @param serviceRegistry
     */
    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.serviceAddress = serviceAddress;
    }

    /**
     * Spring 容器在加载的时候会自动调用一次 setApplicationContext, 并将上下文 ApplicationContext 传递给这个方法
     * 该方法的作用就是获取带有 @RpcSerivce 注解的类的 value (被暴露的实现类的接口名称) 和 version (被暴露的实现类的版本号，默认为 “”)
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                // 获取服务名称
                String serviceName = rpcService.interfaceName().getName();
                // 获取服务版本
                String serviceVersion = rpcService.serviceVersion();
                if (serviceVersion != null) {
                    serviceVersion = serviceVersion.trim();
                    if (!StringUtils.isEmpty(serviceVersion)) {
                        serviceName = serviceName + "-" + serviceVersion;
                    }
                }
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

    /**
     * 在初始化 Bean 的时候会自动执行该方法
     * 该方法的目标就是启动 Netty 服务器进行服务端和客户端的通信，接收并处理客户端发来的请求,
     * 并且还要将服务名称和服务地址注册进 Zookeeper（注册中心）
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    // 解码器
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));
                    // 编码器
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));
                    // 处理 RPC 请求
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                }
            });
            // 获取服务地址/端口号，建立连接
            String[] addressArray = StringUtils.split(serviceAddress, ':');
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            ChannelFuture future = serverBootstrap.bind(ip, port).sync();

            // 注册服务
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    log.info("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            log.info("server started on port {}", port);

            // 关闭 RPC 服务器
            future.channel().close().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
