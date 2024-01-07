package com.lhcygan.rpccommon.model.entity;

import lombok.Data;

@Data
public class RpcRequest {

    /**
     * 请求的Id, 唯一标识该请求
     */
    private String requestId;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 版本
     */
    private String serviceVersion;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 具体参数
     */
    private Object[] parameters;

}
