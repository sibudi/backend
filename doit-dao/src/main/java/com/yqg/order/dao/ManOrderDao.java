package com.yqg.order.dao;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author alan
 */
@Mapper
public interface ManOrderDao extends BaseMapper<OrdOrder> {

    /*通过订单状态统计订单金额*/
    @Select("select sum(amountApply) from ordOrder where disabled = 0 and status in ( ${orderStatus} )")
    public BigDecimal orderSumByStatus(@Param("orderStatus") String orderStatus);

    /*通过refundTime和status统计订单总额*/
    @Select("select sum(amountApply) from ordOrder where status in ( ${orderStatus} ) and refundTime >=  '${time} 00:00:00' and refundTime <= '${time} 23:59:59' and disabled = 0 ")
    public BigDecimal orderSumByRefundTimeStatus(@Param("orderStatus") String orderStatus, @Param("time") String Time);

    /*到期应还金额*/
    @Select("select sum(amountApply) from ordOrder where  status in (7,8,10,11) and refundTime <= '${time} 23:59:59' and disabled = 0 ")
    public BigDecimal sumOrderExpireShouldPayAmount(@Param("time") String time);

    /*当日到期应还订单数*/
    @Select("select count(1) from ordOrder where  status in (7,8,10,11) and refundTime <= '${time} 23:59:59' and refundTime >= '${time} 00:00:00' and disabled = 0 ")
    public Integer countOrderExpireShouldPayDaily(@Param("time") String time);

    /*通过refundTime统计订单总额*/
    @Select("select sum(amountApply) from ordOrder where id disabled = 0  and refundTime >=  '${time} 00:00:00' and refundTime <= '${time} 23:59:59' ")
    public BigDecimal orderSumByRefundTime(@Param("time") String Time);

    /*通过lendingTime统计订单总额*/
    @Select("select sum(amountApply) from ordOrder where disabled = 0 and lendingTime > '${time} 00:00:00' and lendingTime < '${time} 23:59:59' and status in ( ${orderStatus} )")
    public BigDecimal orderSumByLendingTimeStatus(@Param("orderStatus") String orderStatus, @Param("time") String time);

    /*通过lendingTime统计订单数*/
    @Select("select count(1) from ordOrder where disabled = 0 and lendingTime > '${time} 00:00:00' and lendingTime < '${time} 23:59:59' and status in ( ${orderStatus} )")
    public Integer orderCountByLendingTimeStatus(@Param("orderStatus") String orderStatus, @Param("time") String time);

    /*通过订单状态统计订单数*/
    @Select("select count(1) from ordOrder where disabled = 0 and status in ( ${orderStatus} )")
    public Integer orderCountByStatus(@Param("orderStatus") String orderStatus);

    /*今日放款用户数*/
    @Select("select count(1) from ordOrder where status in ( 7,10 ) and lendingTime >=  '${time} 00:00:00' and lendingTime <= '${time} 23:59:59' and disabled = 0 group by userUuid ")
    public List<Integer> todayLoanUserCount(@Param("time") String time);

    /*累计放款用户数*/
    @Select("select count(distinct userUuid) from ordOrder where status in (7,8,10,11) and disabled = 0  ")
    public Integer totalLoanUserCount();

    /*今日还款总额*/
    @Select("select sum(amountApply) as amountApplySum from ordOrder where uuid in( select orderId from ordHistory where disabled = 0 and createTime <= '${time} 23:59:59' and createTime >= '${time} 00:00:00' and status in ( 7,10 ) ) ")
    public BigDecimal todayRepaymentAmount(@Param("time") String time);

    /*今日正常还款总额*/
    @Select(" select sum(amountApply) from ordOrder where  status = 10 and datediff(now(),updateTime) = 0 and refundTime <= '${time} 23:59:59' and disabled = 0 ")
    public BigDecimal todayNormalRepaymentAmount(@Param("time") String time);

    /*今日正常还款订单数*/
    @Select("select count(userUuid) FROM ordHistory where status = 10 and createTime <= '${time} 23:59:59' and createTime >= '${time} 00:00:00'  and disabled = 0 ")
    public Integer todayNormalRepaymentCount(@Param("time") String time);

    /*今日正常还款用户数*/
    @Select("select count(distinct userUuid) as uuidCount from ordHistory where status = 10 and datediff(now(),createTime) = 0 and disabled = 0 ")
    public Integer todayNormalRepaymentUserCount();

    /*今日逾期还款总额*/
    @Select("select sum(amountApply) FROM ordOrder where  status = 11 and datediff(now(),updateTime) = 0 and disabled = 0")
    public BigDecimal todayOverDueRepaymentAmount();

    /*今日逾期还款用户数*/
    @Select("select count(distinct userUuid) from ordHistory where status = 11 and datediff(now(),createTime) = 0 and disabled = 0 ")
    public Integer todayOverDueRepaymentUserCount();

    /*今日提前还款用户数*/
    @Select("select count(distinct userUuid)  from ordOrder where uuid in(select orderId from ordHistory where \n" +
            "status = 10 and createTime <= '${time} 23:59:59' and createTime >= '${time} 00:00:00'\n" +
            " and disabled = 0) and refundTime > '${time} 23:59:59' and disabled = 0")
    public Integer todayPrepaymentUserCount(@Param("time") String time);

    /*今日提前还款单数*/
    @Select("select count(1) from ordOrder where uuid in(select orderId from ordHistory where \n" +
            "status = 10 and createTime <= '${time} 23:59:59' and createTime >= '${time} 00:00:00'\n" +
            " and disabled = 0) and refundTime > '${time} 23:59:59' and disabled = 0\n" +
            " ")
    public Integer todayPrepaymentOrderSum(@Param("time") String time);

    /*今日逾期用户数*/
    @Select(" select count(distinct userUuid) from ordOrder where  status = 8 and datediff(now(),updateTime) = 0 and disabled = 0")
    public Integer todayOverDueUserCountResult();

    /*今日逾期总数数*/
    @Select("select sum(amountApply) from ordOrder where status = 8 and datediff(now(),refundTime) = 1 and disabled = 0 ")
    public BigDecimal todayOverDueAmountSum(@Param("time") String time);

    /*今日复借总额*/
    @Select("select sum(amountApply) from ordOrder where status in (7,8) and borrowingCount > 1 and createTime >=  '${time} 00:00:00' and createTime <= '${time} 23:59:59' and disabled = 0")
    public BigDecimal todayRepeatOrderSum(@Param("time") String time);

    /*累计复借总额*/
    @Select("select sum(amountApply) from ordOrder where status in (7,8,10,11) and borrowingCount > 1 and disabled = 0 ")
    public BigDecimal totalRepeatOrderSum();

    /*今日复借用户数*/
    @Select("select count(distinct userUuid) from ordOrder where  status in (7,8) and borrowingCount > 1 and datediff(now(),createTime) = 0 " +
            " and disabled = 0")
    public Integer todayRepeatUserCountResult();

    /*通过逾期天使查询逾期订单数*/
    @Select("SELECT count(1) from ordOrder where status = 8 and datediff(now(),refundTime) >= #{minDay} and datediff(now(),refundTime) <= #{maxDay} and disabled = 0 ")
    public Integer orderCountByoverDueDay(@Param("maxDay") Integer maxDay, @Param("minDay") Integer minDay);

    /*通过到期日查询今日已还*/
    @Select(" select count(1) from ordOrder where refundTime <= '${time} 23:59:59' and refundTime >= '${time} 00:00:00' and status in (10,11) and datediff(now(),updateTime) = 0 and disabled = 0 ")
    public Integer todayRepayOrderCountByRefundTime(@Param("time") String time);

}
