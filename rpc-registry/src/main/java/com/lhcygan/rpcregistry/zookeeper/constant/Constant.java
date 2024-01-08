package com.lhcygan.rpcregistry.zookeeper.constant;


/**
 * 定义 Zookeeper 使用过程中会用到的一些常量
 */
public class Constant {

    public static final int ZK_SESSION_TIMEOUT = 5000;
    public static final int ZK_CONNECTION_TIMEOUT = 1000;

    // 在该节点下存放所有的服务节点
    public static final String ZK_REGISTRY_PATH = "/registry";
}
