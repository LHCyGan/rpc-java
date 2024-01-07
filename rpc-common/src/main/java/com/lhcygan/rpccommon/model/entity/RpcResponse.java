package com.lhcygan.rpccommon.model.entity;

import lombok.Data;

@Data
public class RpcResponse {

    /**
     * 请求的Id, 唯一标识该请求
     */
    private String requestId;

    private Exception exception;

    private Object result;
}
