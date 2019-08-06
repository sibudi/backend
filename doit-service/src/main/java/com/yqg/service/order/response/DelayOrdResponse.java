package com.yqg.service.order.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by wanghuaizhou on 2018/4/2.
 */
@Data
public class DelayOrdResponse {

     private Float amountApply;  // 总还款金额

     // 根据订单  为固定金额
     private Float interest;      // 利息
     private Float overDueFee;    // 逾期服务费
     private Float penaltyFee;    // 逾期滞纳金
     private Float delayFee;    // 展期服务费
     private Float granulaNum;  //展期金额颗粒度

     private List<Map<String,String>> config;  //日期 金额配置
}
