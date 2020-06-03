package com.imooc.order.controller;

import com.imooc.order.VO.ResultVO;
import com.imooc.order.converter.OrderForm2OrderDTOConverter;
import com.imooc.order.dto.OrderDTO;
import com.imooc.order.enums.ResultEnum;
import com.imooc.order.exception.OrderException;
import com.imooc.order.form.OrderForm;
import com.imooc.order.service.OrderService;
import com.imooc.order.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 创建订单
     * 1. 参数检验
     * 2. 查询商品信息(调用商品服务)
     * 3. 计算总价
     * 4. 扣库存(调用商品服务)
     * 5. 订单入库
     */
    @PostMapping("/create")
    public ResultVO create(@Valid OrderForm orderForm,
                           BindingResult bindingResult) {
        //对参数进行一个校验
        if (bindingResult.hasErrors()) {
            log.error("创建订单参数不正确,orderForm={}", orderForm);
            throw new OrderException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        //orderForm前端的转换到==>OrderDTO
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        //前对参数进行了一个校检，但是在前面中他对购物车车这个参数是
        // 当成一个字符串判断非空的，转了之后,购物车的信息可能就是空的
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("[创建订单] 购物车信息为空");
            throw new OrderException(ResultEnum.CART_EMPTY);
        }
        OrderDTO orderDTO1 = orderService.create(orderDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderDTO1.getOrderId());
        return ResultVOUtil.success(map);
    }

    /**
     * 完结订单
     *
     * @param orderId
     * @return
     */
    @PostMapping("/finish")
    public ResultVO<OrderDTO> finish(@RequestParam("orderId") String orderId) {
        return ResultVOUtil.success(orderService.finish(orderId));
    }
}
