package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by wanghuaizhou on 2019/4/19.
 */
@Mapper
public interface OrdBillDao extends BaseMapper<OrdBill> {

    @Select("select * from ordBill where disabled = 0 and userUuid = #{userUuid} and orderNo = #{orderNo}  order by billTerm")
    List<OrdBill> billsWithUserUuidAndOrderNo(@Param("userUuid") String userUuid,@Param("orderNo") String orderNo);

    // 获取待还款账单 (每个订单 代还款账单的第一笔)
    @Select("select * from ordBill where id in(select min(id) from ordBill where disabled = 0 and status in(1,2) group by orderNo);")
    public List<OrdBill> getInRepayBillList();

    // 获取未逾期待还款账单 (每个订单的第一笔为逾期还款账单)
    @Select("select * from ordBill where id in(select min(id) from ordBill where disabled = 0 and status in(1,2) group by orderNo) and status = 1;")
    public List<OrdBill> getNeedRepayBillList();

    // 获取当前用户待还款账单的第一笔
    @Select("select * from ordBill where disabled = 0 and status in(1,2) and orderNo = #{orderNo} and uuid != #{uuid} order by billTerm limit 1;")
    public OrdBill getRepayBillWithOrderNo(@Param("orderNo") String orderNo, @Param("uuid") String uuid);

}
