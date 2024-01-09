package com.lhcygan.rpcsampleclient;

import com.lhcygan.rpcclient.RpcProxy;
import com.lhcygan.rpcsampleapi.HelloService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

@SpringBootApplication
public class RpcSampleClientApplication {

    @Resource
    private RpcProxy rpcProxy;

    public static void main(String[] args) {
//        SpringApplication.run(RpcSampleClientApplication.class, args);
        ApplicationContext context = SpringApplication.run(RpcSampleClientApplication.class, args);

        RpcSampleClientApplication clientApplication = context.getBean(RpcSampleClientApplication.class);
        clientApplication.run();
    }

    public void run() {
        /**
         * 测试 HelloService 接口的实现类 1
         */
        // 调用 RPC 代理接口的方法(调用远程接口方法就像调用本地方法一样简单）
        HelloService helloServiceImpl1 = rpcProxy.create(HelloService.class);
        String result = helloServiceImpl1.hello("Jack");
        System.out.println(result);

        /**
         * 测试 HelloService 接口的实现类 2
         */
        HelloService helloServiceImpl2 = rpcProxy.create(HelloService.class, "helloServiceImpl2");
        String result2 = helloServiceImpl2.hello("Tom");
        System.out.println(result2);
    }

}
