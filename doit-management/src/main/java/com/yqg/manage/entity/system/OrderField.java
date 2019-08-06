package com.yqg.manage.entity.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: microservice
 * @description: 排序字段
 * @author: 许金泉
 * @create: 2019-04-03 16:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderField {

    // 需要排序的字段
    private String orderField;

    private boolean orderDesc;// true:DESC false:ASC

    public String getOrderBySQL() {
        return " " + orderField + " " + (orderDesc ? "DESC" : "ASC") + " ";
    }
}
