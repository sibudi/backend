package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdRepayAmoutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Didit Dwianto on 2018/2/2.
 */
@Mapper
public interface OrdRepayAmoutRecordDao extends BaseMapper<OrdRepayAmoutRecord>{

    @Select("SELECT COALESCE(ord.orderType, 3) as orderType, " //3 is orderType Staging (installment)
        + " COALESCE(ord.uuid, bill.orderNo) as parentOrderNo, "
        + " repay.* FROM ordRepayAmoutRecord AS repay "
        + " LEFT OUTER JOIN ordOrder AS ord ON repay.orderNo=ord.uuid  AND ord.lendingTime >= '2019-04-08' AND ord.disabled=0 "
        + " LEFT OUTER JOIN ordBill as bill on repay.orderNo=bill.uuid AND bill.createTime >= '2019-04-08' AND bill.disabled=0 "
        + " WHERE repay.repayMethod='CIMB' AND repay.status='WAITING_REPAYMENT_TO_RDN' AND repay.disabled=0 AND (ord.uuid is not null or bill.uuid is not null)")   //Ignore 90+ //Ignore Lending Time before TCC contracts
    List<OrdRepayAmoutRecord> getOrderRepayRecordWaitingRepaymentToRdn(@Param("repayChannel") String repayChannel);

    @Update("<script>"
        + "UPDATE ordRepayAmoutRecord set STATUS=#{newStatus}, updateTime=now() WHERE STATUS=#{oldStatus} AND id in "
        + "<foreach collection='ids' item='id' separator=',' open='(' close=')'>"
        + " #{id}"
        + "</foreach>"
        + " </script>")
    int bulkUpdateStatus(@Param("oldStatus") String oldStatus
        , @Param("newStatus") String newStatus
        , @Param("ids") List<Integer> ids);
}
