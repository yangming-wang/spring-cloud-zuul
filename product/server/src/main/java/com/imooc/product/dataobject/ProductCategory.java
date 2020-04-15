package com.imooc.product.dataobject;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity     //用来标记该类是一个JPA实体类,还可以使用注解@Table指定关联的数据库的表名，解@Id用来定义记录的唯一标识，并结合注解@GeneratedValue将其设置为自动生成。
public class ProductCategory {

    @Id
    @GeneratedValue             //这个是自增的一个注解
    private Integer categoryId;

    /** 类目名字. */
    private String categoryName;

    /** 类目编号. */
    private Integer categoryType;

    private Date createTime;

    private Date updateTime;
}
