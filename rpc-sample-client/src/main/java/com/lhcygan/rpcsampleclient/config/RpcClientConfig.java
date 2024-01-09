package com.lhcygan.rpcsampleclient.config;

import com.lhcygan.rpcclient.RpcProxy;
import com.lhcygan.rpcregistry.ServiceDiscovery;
import com.lhcygan.rpcregistry.zookeeper.ZookeeperServiceDiscovery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcClientConfig {

    @Value("${rpc.registry_address}")
    private String registryAddress;

    @Bean
    public ZookeeperServiceDiscovery serviceDiscovery() {
        return new ZookeeperServiceDiscovery(registryAddress);
    }

    @Bean
    public RpcProxy rpcProxy() {
        return new RpcProxy(serviceDiscovery());
    }
}
