package com.imooc.order.service.impl;

import com.imooc.order.dataobject.OrderDetail;
import com.imooc.order.dataobject.OrderMaster;
import com.imooc.order.dto.OrderDTO;
import com.imooc.order.enums.OrderStatusEnum;
import com.imooc.order.enums.PayStatusEnum;
import com.imooc.order.repository.OrderDetailRepository;
import com.imooc.order.repository.OrderMasterRepository;
import com.imooc.order.service.OrderService;
import com.imooc.order.utils.KeyUtil;
import com.imooc.product.client.ProductClient;
import com.imooc.product.common.DecreaseStockInput;
import com.imooc.product.common.ProductInfoOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    OrderMasterRepository orderMasterRepository;
    @Autowired
    ProductClient productClient;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        String orderId = KeyUtil.genUniqueKey();    //每下一个订单，产生一个订单号
        //TODO 查询商品信息(调用商品服务)   弄秒杀的话调用商品服务查询不行了,要弄缓存了
            List<String> productIdList = orderDTO.getOrderDetailList().stream()
                    .map(OrderDetail::getProductId)
                    .collect(Collectors.toList());
            List<ProductInfoOutput> productInfos = productClient.listForOrder(productIdList);
        //TODO 下面这段还得加一个分布式锁
        //TODO 读redis
        //TODO 减库存并将新值重新设置进redis

        //TODO 订单入库异常, 手动回滚redis

        //TODO 计算总价
        BigDecimal orderAmout = new BigDecimal(BigInteger.ZERO);   //总价,初始化为零
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            for (ProductInfoOutput productInfo : productInfos) {
                if (productInfo.getProductId().equals(orderDetail.getProductId())) {
                    //计算总价
                    orderAmout = productInfo.getProductPrice()                 //犯了个低级错误
                            .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                            .add(orderAmout);   //这个是累加
                    BeanUtils.copyProperties(productInfo, orderDetail);      //将前者copy到后者这个类中去了
                    orderDetail.setOrderId(orderId);
                    orderDetail.setDetailId(KeyUtil.genUniqueKey());         //生成商品id
                    //订单详情入库
                    orderDetailRepository.save(orderDetail);
                }
            }

        }
        //TODO 扣库存(调用商品服务)
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productClient.decreaseStock(decreaseStockInputList);

        //TODO 订单入库  (目前只能做入库)
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);        //将orderDTO拷贝到orderMaster中
        orderMaster.setOrderAmount(new BigDecimal(5));      //小数格式，跟数据库的保持一致
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }
}
