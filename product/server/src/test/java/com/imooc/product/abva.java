package com.imooc.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class abva {

    @Autowired
    Environment environment;
    @Test
    public void addd(){
        String a=environment.getProperty("com.chainway.ming");
        System.out.println("===================================::::"+a);
    }
}
