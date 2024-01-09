package com.lhcygan.rpcserver;


import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义服务暴露注解 @RpcService 供用户使用
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    // 服务类型（被暴露的实现类的接口类型）
    Class<?> interfaceName();

    // 服务版本（默认为空）
    String serviceVersion() default "";
}
