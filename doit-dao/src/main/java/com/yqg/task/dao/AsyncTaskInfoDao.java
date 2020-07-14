package com.yqg.task.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AsyncTaskInfoDao extends BaseMapper<AsyncTaskInfoEntity> {

    // budi: change query o.status in (2,17,4) -> o.status = 5
    // budi: change to status = 2 need to do digisign register and activation first before send to p2p
    @Select("select * from asyncTaskInfo a where disabled=0 and taskType=#{type} " +
            " and exists(select 1 from ordOrder o where o.uuid = a.orderNo and o.disabled=0 and o.status in (2,17)) " +
            " and not exists (select 1 from orddigisign_error oe where oe.orderNo = a.orderNo) " +
            " limit #{limitCount}")
    List<AsyncTaskInfoEntity> getNeedDigitalSignOrders(@Param("type") Integer type, @Param("limitCount") Integer limitCount);

}
