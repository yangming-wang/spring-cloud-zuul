package com.imooc.product.controller;


import com.imooc.product.VO.ProductInfoVO;
import com.imooc.product.VO.ProductVO;
import com.imooc.product.VO.ResultVO;
import com.imooc.product.common.DecreaseStockInput;
import com.imooc.product.dataobject.ProductCategory;
import com.imooc.product.dataobject.ProductInfo;
import com.imooc.product.service.CategoryService;
import com.imooc.product.service.ProductService;
import com.imooc.product.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;



    /**
     * 1. 查询所有在架的商品
     * 2. 获取类目type列表
     * 3. 查询类目
     * 4. 构造数据
     */
    @GetMapping("/list")
    //使用这个注解，能够让这个api实现跨域访问了
    //allowCredentials,值得注意的是,当它设置为true的话
    //就是允许cookie跨域
    @CrossOrigin(allowCredentials = "true")
    public ResultVO<ProductVO> list(){
        //1. 查询所有在架的商品
        List<ProductInfo> productInfoList=productService.findUpAll();
        //2. 获取类目type列表
        List<Integer>integerList=productInfoList.stream().
                map(ProductInfo::getCategoryType).collect(Collectors.toList());
        /*如果要用map的话
        Map<Integer,List<ProductInfo>> productMap = productInfoList.stream().
        collect(Collectors.groupingBy(ProductInfo::getCategoryType));*/
        //3. 查询类目
        List<ProductCategory>productCategoryList=categoryService.findByCategoryTypeIn(integerList);
        //4. 构造数据
        List<ProductVO> productVOList=new ArrayList<>();
        int i=0;
        for (ProductCategory productCategory: productCategoryList){
            ProductVO productVO = new ProductVO();
            productVO.setCategoryName(productCategory.getCategoryName());
            productVO.setCategoryType(productCategory.getCategoryType());
            ProductInfo productInfo=productInfoList.get(i);
            List<ProductInfoVO> productInfoVOList = new ArrayList<>();
            if (productInfo.getCategoryType().equals(productCategory.getCategoryType())) {
                ProductInfoVO productInfoVO = new ProductInfoVO();
                BeanUtils.copyProperties(productInfo, productInfoVO);
                productInfoVOList.add(productInfoVO);
            }
/*

            for (ProductInfo productInfo: productInfoList) {



            }*/
            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
            i++;
        }

        return ResultVOUtil.success(productVOList);
    }



    /**
     * 1. 查询所有在架的商品
     * 2. 获取类目type列表
     * 3. 查询类目
     * 4. 构造数据
     */
//    @GetMapping("/list")
//    public ResultVO<ProductVO> list(){
//        return null;
//    }

    /**
     * 获取商品列表(给订单服务用的)
     * @param productIdList
     * @return
     */
    @PostMapping("/listForOrder")
    public List<ProductInfo> listForOrder(@RequestBody List<String> productIdList){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return productService.findList(productIdList);
    }

    @PostMapping("/decreaseStock")
    public void decreaseStock(@RequestBody List<DecreaseStockInput> decreaseStockInputList) {
        productService.decreaseStock(decreaseStockInputList);
    }

}
