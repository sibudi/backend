package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdPayCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jacob
 * Date: 07/02/2018
 * Time: 1:35 PM
 */
@Mapper
public interface OrdPayCheckDao extends BaseMapper<OrdPayCheck>{

    @Select("select * from ordPayCheck where disabled = 0 and createTime > #{startTime} and createTime < #{endTime} order by createTime asc")
//    @Select("select * from ordPayCheck where disabled = 0")
    List<OrdPayCheck> getpayCheckedOrderListByTimeStamp(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
