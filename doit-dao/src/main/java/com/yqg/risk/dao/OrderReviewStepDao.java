package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.OrderReviewStepEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderReviewStepDao  extends BaseMapper<OrderReviewStepEntity> {
    @Select("\n" +
            "select *from (select *from orderReviewStep where disabled=0 and orderNo=#{orderNo} order by createTime desc) a limit 1;\n")
    OrderReviewStepEntity getLatestStep(@Param("orderNo") String orderNo);
}
