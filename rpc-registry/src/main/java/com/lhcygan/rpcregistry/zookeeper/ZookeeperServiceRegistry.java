package com.lhcygan.rpcregistry.zookeeper;

import com.lhcygan.rpcregistry.ServiceRegistry;
import com.lhcygan.rpcregistry.constant.Constant;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 使用 Zookeeper 实现服务注册（使用 Zookeeper 客户端 ZkClient）
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    // Zookeeper 客户端 ZkClient
    private ZkClient zkClient;

    /**
     * 该构造方法提供给用户（用户通过配置文件指定 zkAddress 完成服务注册组件的注入）
     */
    public ZookeeperServiceRegistry(String zkAddress) {
        this.zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        log.info("connect zookeeper");
    }


    /**
     * 服务注册
     * @param serviceName 服务名称（被暴露的实现类的接口名称）
     * @param serviceAddress 服务地址（比如该服务被暴露在 Netty 的 8000 端口，则服务地址为 127.0.0.1:8000）
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 持久节点，该节点下存放所有的 service 节点
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.info("create registry node: {}", registryPath);
        }
        // 在 registry 节点下创建 service 持久节点，存放服务名称
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            log.info("create service node: {}", registryPath);
        }
        // 在 service 节点下创建 address 临时节点,存放服务地址
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        log.info("create address node: {}", addressNode);
    }
}
