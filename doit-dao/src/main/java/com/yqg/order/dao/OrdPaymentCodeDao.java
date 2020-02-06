package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdPaymentCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2018/1/1.
 */
@Mapper
public interface OrdPaymentCodeDao extends BaseMapper<OrdPaymentCode> {

    @Select("SELECT * FROM doit.ordPaymentCode where orderNo = #{orderNo} and disabled = 0 and datediff(now(),createTime) < 14;")
    List<OrdPaymentCode> getOrderPaymentCodeByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM doit.ordPaymentCode where orderNo = #{orderNo} and paymentCode = #{paymentCode} and disabled = 0 ORDER BY createTime DESC limit 1;")
    List<OrdPaymentCode> getOrderPaymentCodeDesc(@Param("orderNo") String orderNo, @Param("paymentCode") String paymentCode);

    @Select("SELECT * FROM doit.ordPaymentCode where orderNo = #{orderNo} and disabled = 0 ORDER BY createTime DESC limit 1;")
    List<OrdPaymentCode> getOrderPaymentCodeByOrderNoDesc(@Param("orderNo") String orderNo);

}
