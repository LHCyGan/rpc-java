package com.lhcygan.rpcregistry.zookeeper;

import com.lhcygan.rpcregistry.ServiceDiscovery;
import com.lhcygan.rpcregistry.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 使用 ZooKeeper 实现服务发现功能
 */
@Slf4j
@Service
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private ZkClient zkClient;

    @Autowired
    public ZookeeperServiceDiscovery(ZkClient zkClient) {
        this.zkClient = zkClient;
        log.info("connect zookeeper");
    }


    /**
     * 服务发现
     * @param serviceName 服务名称（被暴露的实现类的接口名称）
     * @return
     */
    @Override
    public String discovery(String serviceName) {

        try {
            // 根据 serviceName 查找 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s\", servicePath"));
            }
            // 查找 address 节点
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtils.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            String address;
            int size = addressList.size();
            if (size == 1) {
                // 如果只有一个 address 节点，则直接获取该地址
                address = addressList.get(0);
                log.info("get only address node: {}", address);
            } else {
                // 若存在多个address 节点，则通过负载均衡策略获取一个地址（这里就选择了最简单的随机获取）
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                log.info("get random address node: {}", address);
            }
            // 读取 address 节点的内容（服务地址）
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }
}
