package com.lhcygan.rpcsampleserver.config;

import com.lhcygan.rpcregistry.zookeeper.ZookeeperServiceDiscovery;
import com.lhcygan.rpcregistry.zookeeper.ZookeeperServiceRegistry;
import com.lhcygan.rpcserver.RpcServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcServerConfig {

    @Value("${rpc.registry_address}")
    private String registryAddress;

    @Value("${rpc.service_address}")
    private String serviceAddress;

    @Bean
    public ZookeeperServiceRegistry serviceRegistry() {
        return new ZookeeperServiceRegistry(registryAddress);
    }

    @Bean
    public RpcServer rpcServer() {
        return new RpcServer(serviceAddress, serviceRegistry());
    }
}
