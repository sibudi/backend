package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrdServiceOrderDao extends BaseMapper<OrdServiceOrder> {

    //  查询 待放款的服务费订单   status = 2
    @Select("select * from ordServiceOrder where  disabled = 0 and status = 2;")
    List<OrdServiceOrder> queryServiceChargeList();

    //  查询 放款处理中的服务费订单   status = 3
    @Select("select * from ordServiceOrder where  disabled = 0 and status = 3;")
    List<OrdServiceOrder> queryServiceCharge();

}
