package com.lhcygan.rpccommon;

import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RpcCommonApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void objTest() {
        Objenesis objenesis = new ObjenesisStd();
        Integer a = (Integer) objenesis.newInstance(Integer.class);
        System.out.println(a);
    }

}
