package com.yqg.order.dao;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface OrdStepDao extends BaseMapper<OrdStep> {

    @Select("select * from ordStepHistory s where s.orderId = #{orderNo} and s.step = #{step} ")
    List<OrdStep> getOrderSteps(@Param("orderNo") String orderNo, @Param("step") Integer step);

    @Select("select  * from ordStepHistory s where s.orderId = #{orderNo} and disabled=0")
    List<OrdStep> getOrderStepList(@Param("orderNo") String orderNo);

}
