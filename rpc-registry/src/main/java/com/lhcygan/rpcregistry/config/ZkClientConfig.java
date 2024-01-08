package com.lhcygan.rpcregistry.config;

import com.lhcygan.rpcregistry.constant.Constant;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkClientConfig {

    @Value("${zkAddress}")
    private String zkAddress;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
    }
}
