package com.yqg.manage.service.check.request;

import com.yqg.manage.entity.system.OrderField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: microservice
 * @description: 前端请求实体
 * @author: 许金泉
 * @create: 2019-04-03 10:27
 **/
@Data
public class QualityCheckingVoiceRequest {


    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 姓名
     */
    private String realName;


    /**
     * 催收人员
     */
    private String userName;


    /**
     * 搜索开始时间
     */
    private String recordBeginTime;

    /**
     * 搜索结束时间
     */
    private String recordEndTime;

    private String phone;

    /**
     * 分页查询当前页码从1开始
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;


    /**
     * 需要排序的字段
     */
    private List<OrderField> orderFields;

}
