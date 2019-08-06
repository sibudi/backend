package com.yqg.task.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AsyncTaskInfoDao extends BaseMapper<AsyncTaskInfoEntity> {

    @Select("select * from asyncTaskInfo a where disabled=0 and taskType=#{type} and exists(" +
            " select 1 from ordOrder o where o.uuid = a.orderNo and o.disabled=0 and o.status in (2,17,4)) limit #{limitCount}")
    List<AsyncTaskInfoEntity> getNeedDigitalSignOrders(@Param("type") Integer type, @Param("limitCount") Integer limitCount);

}
