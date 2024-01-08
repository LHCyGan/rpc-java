package com.lhcygan.rpccommon;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.springframework.boot.test.context.SpringBootTest;

@Data
class Person{
    private String name;
    private Integer age;
    private String sex;
    private String a;
    private Long b;
    private String c;
    private String d;
    private String e;
    private String f;
    private String ff;
    private String fff;
    private String gggh;

    private Stu stu;
}

class Stu {

}

@SpringBootTest
class RpcCommonApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void objTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        long st1 = System.currentTimeMillis();
        Objenesis objenesis = new ObjenesisStd(true);
        Person a = objenesis.newInstance(Person.class);
        System.out.println(a);
        System.out.println(System.currentTimeMillis() - st1);

        long st2 = System.currentTimeMillis();
        Class<?> cls = Class.forName("com.lhcygan.rpccommon.Person");
        Person b = (Person) cls.newInstance();
        System.out.println(b);
        System.out.println(System.currentTimeMillis() - st2);
    }

}
