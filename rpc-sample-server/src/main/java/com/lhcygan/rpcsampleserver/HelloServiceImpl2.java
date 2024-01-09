package com.lhcygan.rpcsampleserver;

import com.lhcygan.rpcsampleapi.HelloService;
import com.lhcygan.rpcserver.RpcService;

@RpcService(interfaceName = HelloService.class, serviceVersion = "helloServiceImpl2")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return name + " Hello from " + "HelloServiceImpl2";
    }
}
