package com.imooc.product.service;


import com.imooc.product.dataobject.ProductCategory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 廖师兄
 * 2017-12-09 22:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CategoryServiceTest  {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void findByCategoryTypeIn() throws Exception {
        List<ProductCategory> list = categoryService.findByCategoryTypeIn(Arrays.asList(11, 22));
        Assert.assertTrue(list.size() > 0);
    }
    public void aa(){

    }

}