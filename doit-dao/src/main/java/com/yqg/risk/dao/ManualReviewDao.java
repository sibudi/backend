package com.yqg.risk.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ManualReviewDao {

    @Select("select count(1) from manOrderRemark s join manTeleReviewRecord t on s.id = t.manOrderRemarkId" +
            " where s.orderNo=#{orderNo} and s.disabled=0 and t.disabled=0 and s.teleReviewOperationType=2" +
            " and s.type=3 and t.question REGEXP '是在贵公司工作吗 |di perusahaan ini' \n" +
            " and t.answer=1  ")
    Integer hitCompanyRule(@Param("orderNo") String orderNo);

}
