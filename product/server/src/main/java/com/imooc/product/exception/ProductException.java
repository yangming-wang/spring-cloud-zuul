package com.imooc.product.exception;

import com.imooc.product.enums.ResultEnum;

public class ProductException extends RuntimeException{

    private Integer code;

    public ProductException(Integer code,String message){
        super(message);
        this.code = code;
    }

    //设定异常，根据上面的构造方法的参数，在枚举类中找到它对应的异常
    public ProductException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code=resultEnum.getCode();
    }


}
