package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrderChangeHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderChangeHistoryDao extends BaseMapper<OrderChangeHistory> {
    @Insert("INSERT INTO `orderChangeHistory`\n" +
            "(\n" +
            "`uuid`,\n" +
            "`disabled`,\n" +
            "`createTime`,\n" +
            "`updateTime`,\n" +
            "`createUser`,\n" +
            "`updateUser`,\n" +
            "`remark`,\n" +
            "`productUuid`,\n" +
            "`status`,\n" +
            "`orderStep`,\n" +
            "`amountApply`,\n" +
            "`borrowingTerm`,\n" +
            "`serviceFee`,\n" +
            "`interest`,\n" +
            "`borrowingCount`,\n" +
            "`channel`,\n" +
            "`payChannel`,\n" +
            "`orderPositionId`,\n" +
            "`userUuid`,\n" +
            "`userBankUuid`,\n" +
            "`applyTime`,\n" +
            "`lendingTime`,\n" +
            "`refundTime`,\n" +
            "`actualRefundTime`,\n" +
            "`firstChecker`,\n" +
            "`secondChecker`,\n" +
            "`orderType`,\n" +
            "`lendingRating`,\n" +
            "`overOneApply`,\n" +
            "`requestGuarantee`,\n" +
            "`paymentFrequencyType`,\n" +
            "`paymentType`,\n" +
            "`otherLoaninformation`,\n" +
            "`loanStatus`,\n" +
            "`approvedAmount`,\n" +
            "`markStatus`,\n" +
            "`score`,\n" +
            "`thirdType`)\n" +
            "SELECT \n" +
            "    `uuid`,\n" +
            "    `disabled`,\n" +
            "    `createTime`,\n" +
            "    `updateTime`,\n" +
            "    `createUser`,\n" +
            "    `updateUser`,\n" +
            "    `remark`,\n" +
            "    `productUuid`,\n" +
            "    `status`,\n" +
            "    `orderStep`,\n" +
            "    `amountApply`,\n" +
            "    `borrowingTerm`,\n" +
            "    `serviceFee`,\n" +
            "    `interest`,\n" +
            "    `borrowingCount`,\n" +
            "    `channel`,\n" +
            "    `payChannel`,\n" +
            "    `orderPositionId`,\n" +
            "    `userUuid`,\n" +
            "    `userBankUuid`,\n" +
            "    `applyTime`,\n" +
            "    `lendingTime`,\n" +
            "    `refundTime`,\n" +
            "    `actualRefundTime`,\n" +
            "    `firstChecker`,\n" +
            "    `secondChecker`,\n" +
            "    `orderType`,\n" +
            "    `lendingRating`,\n" +
            "    `overOneApply`,\n" +
            "    `requestGuarantee`,\n" +
            "    `paymentFrequencyType`,\n" +
            "    `paymentType`,\n" +
            "    `otherLoaninformation`,\n" +
            "    `loanStatus`,\n" +
            "    `approvedAmount`,\n" +
            "    `markStatus`,\n" +
            "    `score`,\n" +
            "    `thirdType`\n" +
            "FROM `ordOrder` where uuid = #{orderNo};")
    int copyFromOrder(@Param("orderNo") String orderNo);
}
