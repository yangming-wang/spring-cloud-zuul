package com.imooc.order.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.imooc.order.utils.JsonUtil;
import com.imooc.product.common.ProductInfoOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductInfoReceiver {
    //关于%s    具体看这里https://blog.csdn.net/anita9999/article/details/82346552
    private static final String PRODDUCT_STOCK_TEMPLATE = "product_stock_%s";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queuesToDeclare = @Queue("productInfo"))
    public void process(String message){
        //message =>ProductInfoOutput   用的商品服务里面common模块里面的公共数据  将String分序列化为 对象
        List<ProductInfoOutput> productInfoOutputList= (List<ProductInfoOutput>)JsonUtil.fromJson(message,
                new TypeReference <List<ProductInfoOutput>>() {});
        log.info("从队列【{}】接收到消息: {}","productInfo",productInfoOutputList);

        //接收的消息存储到redis中
        for (ProductInfoOutput productInfoOutput:productInfoOutputList) {
            redisTemplate.opsForValue().set(String.format(PRODDUCT_STOCK_TEMPLATE,productInfoOutput.getProductId()),
                    String.valueOf(productInfoOutput.getProductStock()));
        }

    }
}
