package com.imooc.order.utils;

import java.util.Random;

public class KeyUtil {

    /**
     * 生成唯一的主键
     * 格式: 时间+随机数
     * 注意注意，这里只是降低重复概率，要想唯一的话，还得单独弄一个项目，要主键的话，就调用这个服务api
     */
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;

        return System.currentTimeMillis() + String.valueOf(number);
    }
}
