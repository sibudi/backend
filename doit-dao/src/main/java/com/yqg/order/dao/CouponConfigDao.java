package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.CouponConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:23 PM
 */
@Mapper
public interface CouponConfigDao  extends BaseMapper<CouponConfig> {

    @Select("select * from couponConfig where disabled = 0 ")
    List<CouponConfig> listCouponConfig();

    @Update("update couponConfig set disabled = 1 where id = #{id};")
    void deleteCouponConfig (@Param("id") Integer id);
}
