package com.yqg.signcontract.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.signcontract.entity.OrderContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderContractDao extends BaseMapper<OrderContract> {

    @Select("select *from orderContract where status = 21 and disabled=0 order by id desc limit 30")
    List<OrderContract> getNeedToDownLoadListForContract();

    //查询发送成功的单
    @Select("select *from orderContract where status = 11 and disabled=0 order by id desc limit 30")
    List<OrderContract> getNeedToCheckSignStatusContracts();
}
