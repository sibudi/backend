package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface OrdHistoryDao extends BaseMapper<OrdHistory> {

    @Select("select createTime from ordHistory where status = 2 and orderId= #{orderNo} order by createTime desc limit 1")
    Date getSubmitDate(@Param("orderNo") String orderNo);

    @Select("select * from ordHistory where orderId = #{orderNo} and status = 19;")
    List<OrdHistory> getDeratingRecord(@Param("orderNo") String orderNo);

}