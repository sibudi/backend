package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.FraudUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FraudUserOrderInfoDao extends BaseMapper<FraudUserInfo> {

    @Select("select * from fraudUserOrderInfo where disabled=0")
    List<FraudUserInfo> getAllFraudUserOrderInfo();

    @Select("select count(1) from fraudUserOrderInfo where disabled=0 and orderNo=#{orderNo}")
    Integer existFraudUserOrder(@Param("orderNo") String orderNo);
}
