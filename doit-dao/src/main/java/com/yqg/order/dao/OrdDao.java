package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.*;
import com.yqg.system.entity.SysProduct;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface OrdDao extends BaseMapper<OrdOrder> {
    @Select("select * from ordOrder where disabled=0 and status =#{status}")
    List<OrdOrder> getOrdersByStatus(@Param("status") Integer status);

    @Select("select * from ordOrder where disabled = 0 and userUuid = #{userUuid} and orderType != 3  and STATUS <>10 and STATUS <>11 and STATUS <>15  order by createTime desc")
    List<OrdOrder> hasOrder(@Param("userUuid") String userUuid);

    @Select("select * from ordOrder where disabled = 0 and userUuid = #{userUuid}  and orderType != 3  and status  in(2,3,4,5,6,7,8,16,17,18,19,20)")
    List<OrdOrder> hasOldOrder(@Param("userUuid") String userUuid);

    @Select("select * from ordOrder where disabled = 0 and userUuid = #{userUuid} and orderType = 3 and STATUS <>10 and STATUS <>11 and STATUS <>15  order by createTime desc")
    List<OrdOrder> hasStagingOrder(@Param("userUuid") String userUuid);

    @Select("SELECT * FROM ordOrder where  userUuid = #{userUuid} and status in(2,3,4,5,6,7,8,9,16,17,18,19,20) and disabled = 0 order by " +
            "createTime " +
            "desc;")
    List<OrdOrder> hasProcessingOrder(@Param("userUuid") String userUuid);

    //budi: digisign percentage
    @Select("select count(*) from ordOrder where orderType in (0, 3) and lendingTime between subdate(current_date, 1) and current_date;")
    int countOfYesterdayOrder();

    //  是否放过款
    @Select("SELECT * FROM ordOrder where  userUuid = #{userUuid} and status in(5,6,7,8,10,11) and disabled = 0")
    List<OrdOrder> hasLoan(@Param("userUuid") String userUuid);

    //  是否放过款
    @Select("SELECT * FROM ordOrder where  userUuid = #{userUuid} and status != 1 and disabled = 0")
    List<OrdOrder> hasCommit(@Param("userUuid") String userUuid);


    //  是否有过 10w 20w 40w 80w 降额
    @Select("select * from ordOrder where  userUuid = #{userUuid} and amountApply in ('100000','160000','200000','300000','400000','800000') and disabled = 0 and status in(2,3,4,5,6,7,8,10,11);")
    List<OrdOrder> has10WOR20WOR40WOR80WLoan(@Param("userUuid") String userUuid);

    //  是否有过120w借款
    @Select("select * from ordOrder where  userUuid = #{userUuid} and amountApply = '1200000.00' and disabled = 0 and status in(10,11)")
    List<OrdOrder> has120WLoan(@Param("userUuid") String userUuid);

    //  上一笔成功的借款
    @Select("select * from sysProduct where uuid = (select productUuid from ordOrder where  userUuid = #{userUuid} and disabled = 0 and status in(10,11) and orderType != 1 order by createTime desc limit 1)")
    SysProduct nextSuccessOrderProduct(@Param("userUuid") String userUuid);


    @Select("select * from ordOrder where status in (2,3,4,5,6,7,8,9,16,17,18,19,20) and disabled = 0 and userUuid = #{userUuid} ")
    List<OrdOrder> hasN0AllowOrder(String userUuid);

    @Select("select * from ordOrder where status in (10,11,12,13,14,15) and disabled = 0 and orderType != 1 and userUuid = #{userUuid}  ")
    List<OrdOrder> isNewOrder(@Param("userUuid") String userUuid);

    @Select("select * from ordOrder where status in (5,7,8,9,10,11,12) and disabled = 0 and userUuid = #{userUuid}  ")
    List<OrdOrder> hasLoanOrder(@Param("userUuid") String userUuid);

    // 是否是复借用户
    @Select("select * from ordOrder where status in (10,11) and disabled = 0 and userUuid = #{userUuid}  ")
    List<OrdOrder> isLoanAgain(@Param("userUuid") String userUuid);

    @Select("select * from ordOrder where disabled = 0 and userUuid = #{userUuid} order by applyTime desc")
    List<OrdOrder> getOrder(@Param("userUuid") String userUuid);

    @Select("select * from ordOrder where disabled = 0 and userUuid = #{userUuid}  and status in(2,3,4,17,18,19)  and orderStep = 7")
    List<OrdOrder> loneOrderList(@Param("userUuid") String userUuid);

    @Select("select * from ordOrder o where o.status = 5 and o.orderStep in(7,8) and o.disabled = 0 " 
        //    " and not exists (select 1 from usrUser ss where ss.uuid=o.userUuid and ss.userSource not in(81,82,83,84) and o.borrowingCount<=1 and ss.disabled=0) "
    )
    public List<OrdOrder> getLoanList();

    @Select("select * from ordOrder where status = 5 and orderStep in(7,8) and orderType != 3 and markStatus is not null and markStatus not in ('0','6','7','8','30') " +
            " and disabled = 0 ")
    List<OrdOrder> getP2PSendPendingOrders();

    // 借款端的待放款订单
    @Select("select * from ordOrder where status = 5 and orderStep in(7,8) and disabled = 0 and markStatus = 8")
    public List<OrdOrder> getLoanListWithFlowMrker();
    // P2P的待放款订单
    @Select("select * from ordOrder where status = 5 and orderStep in(7,8) and disabled = 0 and markStatus != 8")
    public List<OrdOrder> getLoanListWithP2P();

    @Select("select * from ordOrder where disabled = 0 and userUuid in (select uuid from usrUser where userSource=39 and disabled=0) and " +
            "createTime<#{endDate}")
    public List<OrdOrder> getXiaoMiOrderList(@Param("endDate") Date date);

    @Select("select * from ordOrder where status = 6 and orderStep in(7,8) and disabled = 0 ")
    public List<OrdOrder> getLoaningList();

    @Select("select * from ordOrder where status in (2,3,4,5,6,7,8,9,17,18,19,20) and disabled = 0 and userUuid = #{userUuid} ")
    List<OrdOrder> canReloanOrder(String userUuid);

    // 借款端的放款处理中订单
    @Select("select * from ordOrder where status = 6 and orderStep in(7,8) and disabled = 0 and markStatus = 8 ")
    public List<OrdOrder> getLoaningListWithFlowMrker();

    @Select("select * from ordOrder where status in (7,8) and orderStep in(7,8) and disabled = 0  ")
    public List<OrdOrder> getInRepayOrderList();

    @Select("select * from ordOrder where status in (7,8) and orderStep in(7,8) and orderType != 3 and disabled = 0  and id like '%${num}'")
    public List<OrdOrder> getInRepayOrderListById(@Param("num") Integer num);

    // 借款端的待还款用户
    @Select("select * from ordOrder where status in (7,8) and orderStep in(7,8) and disabled = 0  and markStatus = 8 and id like '%${num}'")
    public List<OrdOrder> getInRepayOrderListByIdWithFlowMrker(@Param("num") Integer num);

    // P2P的待还款用户
    @Select("select * from ordOrder where status in (7,8) and orderStep in(7,8) and disabled = 0  and markStatus != 8 ")
    public List<OrdOrder> getInRepayOrderListByP2P();

    @Select("select * from ordOrder where status = 2 and orderStep in(7,8) and disabled = 0  ")
    public List<OrdOrder> getRiskOrderList();


    @Select("select * from ordOrder where status = 3 and disabled = 0  limit 100")
    public List<OrdOrder> getRiskOrderTestList();

    //ahalim: speedup for demo 5min -> 1 min
    @Select("select * from ordOrder u where u.disabled=0 and u.status in (17,18) and updateTime< date_add(now(),INTERVAL -1 MINUTE) " +
            // "  and mod(id,3)=#{modId}" +
            "  and #{modId}=#{modId}" +
            " and not exists(select 1 from asyncTaskInfo aa where aa.orderNo = u.uuid and aa.disabled=0 and aa.taskType=1)" +
            " order by  updateTime asc")
    List<OrdOrder> getWaitingAutoCallOrders(@Param("modId") Integer modId);

    @Select("select * from ordOrder u where u.disabled=0 and u.status in (17,18) and borrowingCount=1 and updateTime<=#{updateTime}")
    List<OrdOrder> getWaitingAutoCallOrdersWithUpdateTime(@Param("updateTime") Date updateTime);

    @Select("select * from ordOrder where status = 19 and disabled=0 and datediff(now(),updateTime) >= 7")
    List<OrdOrder> getNeedConfirmationOrders();


    // 第二次获取待放款订单号
    @Select("SELECT * FROM ordOrder where status = 5 and disabled = 0 ;")
    public List<OrdOrder> getLoanOrderTwice();


    // Janhsen
    // Risk only support kudo user
//     @Select("select * from ordOrder o where status = 2 and orderStep in(7,8) and disabled = 0  " +
//             " and updateTime< date_add(now(),INTERVAL -3 MINUTE) " +
//             " and not exists (select 1 from riskErrorLog ss where ss.orderNo= o.uuid and ss.disabled=0) " +
//             " and not exists (select 1 from riskErrorLog ss where ss.orderNo= o.uuid and ss.remark like 'exceed max retry times:%') " +
//             " and mod(id,11)=#{num}  " +
//             " and not exists (select 1 from asyncTaskInfo ss where ss.orderNo = o.uuid and ss.disabled=0 and ss.taskType=1) " +
//             " and not exists (select 1 from usrUser ss where ss.uuid=o.userUuid and ss.userSource not in(81,82,83,84) and o.borrowingCount<=1 and ss.disabled=0) " +
//             "  order by id desc limit 30 ")

        @Select("select * from ordOrder o" +
                "inner join doit.usrUser u on u.uuid = o.userUuid and borrowingCount = 1 and u.createTime >= '2020-01-12' " +
                "where o.status = 2 and (u.userSource in (81,82,83,84) or productUuid = '1006')  " +
                "and o.disabled = 0 and u.disabled = 0 and o.createTime >= '2020-01-12' " +
                "and not exists (select 1 from riskErrorLog ss where ss.orderNo= o.uuid and ss.disabled=0)  " +
                "and not exists (select 1 from riskErrorLog ss where ss.orderNo= o.uuid and ss.remark like 'exceed max retry times:%')  " +
                "and not exists (select 1 from asyncTaskInfo ss where ss.orderNo = o.uuid and ss.disabled=0 and ss.taskType=1)  " +
                "and o.updateTime< date_add(now(),INTERVAL -1 MINUTE) " + 
                "limit 30")
    List<OrdOrder> getRiskOrderListById(@Param("num") Integer num);

        //try 100 data for new user 
        @Select("select o.* " +
        "from ordOrder o " +
        "inner join usrUser u on u.uuid = o.userUuid and u.createTime >= '2020-01-12' " +
        "inner join temp_OrderRisk t on t.orderNo = o.uuid " + 
        "where o.status = 2 " +
        //"and (u.userSource in (81,82,83,84) or productUuid = '1006')  " +
        "and o.disabled = 0 and u.disabled = 0 and o.createTime >= '2020-01-12' " +
        "and not exists (select 1 from riskErrorLog ss where ss.orderNo= o.uuid and ss.disabled=0)  " +
        "and not exists (select 1 from riskErrorLog ss where ss.orderNo= o.uuid and ss.remark like 'exceed max retry times:%')  " +
        " and mod(o.id,11)=#{num}  " +
        "and not exists (select 1 from asyncTaskInfo ss where ss.orderNo = o.uuid and ss.disabled=0 and ss.taskType=1)  " +
        "and o.updateTime< date_add(now(),INTERVAL -1 MINUTE) " +
        "LIMIT 30")
        List<OrdOrder> getRiskOrderListManual(@Param("num") Integer num);

        //budi: delete temp_OrderRisk
        @Delete("delete from temp_OrderRisk where orderNo = #{Uuid}")
        Integer deleteOrderFromTemp_OrderRisk(@Param("Uuid") String Uuid);

    @Select("select * from ordOrder where status = 7 and orderStep in(7,8) and disabled = 0  ")
    public List<OrdOrder> getNeedRepayList();

    @Select("SELECT * FROM ordOrder where status = 12 and updateTime < '2018-1-4 13:30:49';")
    public List<OrdOrder> getUserOrderDataList();

    @Select("SELECT * FROM doit.ordOrder  where status = 16 and disabled = 0 and remark != 'BANK_CARD_ERROR';")
    public List<OrdOrder> getUserOrderLoanFaildList();


    // 处理因为余额不足打款失败的订单
    @Select("select * from ordOrder where  status = 16 and disabled = 0  and remark like '%Couldn%';")
    public List<OrdOrder> getUserOrderLoanFaildOrder();

    // 处理BNI渠道打款失败订单
    @Select("select * from ordOrder where  status = 16 and disabled = 0  and remark = 'Undefined error';")
    public List<OrdOrder> getLoanFaildOrderWithBniBank();

    // 处理BCA渠道打款失败订单
    @Select("select * from ordOrder where status = 16 and remark = 'Unauthorized';")
    public List<OrdOrder> getLoanFaildOrderWithBCABank();

    @Select("SELECT * FROM ordOrder where datediff((case when actualRefundTime is null then now() else actualRefundTime end),refundTime) > #{num} and disabled = 0" )
    public List<OrdOrder> moreThanXDay(@Param("num") Integer num);


    @Select("SELECT * FROM ordOrder where userUuid = #{userUuid} and disabled = 0 and status in (10,11) order by createTime desc" )
    public List<OrdOrder> getLastSuccessOrder(@Param("userUuid") String userUuid);


    @Select("select * from ordOrder where status = 7 and orderStep in(7,8) and disabled = 0  " )
    public List<OrdOrder> getInRepayOrderListWithNotOverdue();

    @Select("select * from ordOrder where status = 8 and orderStep in(7,8) and disabled = 0  " )
    public List<OrdOrder> getInRepayOrderListWithOverdue();

    @Select("select * from ordOrder where status in(7,8,10,11) and disabled = 0 and lendingTime > #{startTime} and lendingTime < #{endTime} order by lendingTime asc")
    List<OrdOrder> getInRepayOrderListByTimeStamp(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("select * from ordOrder where status in(16) and disabled = 0 and lendingTime > #{startTime} and lendingTime < #{endTime} order by lendingTime asc")
    List<OrdOrder> getPayFailedOrderListByTimeStamp(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("<script>"
            + "select * from ordOrder where disabled = 0 and refundTime is not null "
            + "  and datediff((case when actualRefundTime is null then now() else actualRefundTime end),refundTime) >#{num}"
            + "  and userUuid in "
            + "<foreach collection='uuids' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>"
            + "</script>")
    List<OrdOrder> getOrdersByMinOverdueDaysAndUUIDS(@Param("num") Integer num,
                                                     @Param("uuids") List<String> uuids);

    @Select("select * from ordOrder where  status = 3 and applyTime > '2018-01-27 00:00:00' and disabled = 0 limit 60" )
    public List<OrdOrder> getLoanOrderWithSixthyNumber();


    @Select("SELECT * FROM ordOrder where updateTime > '2018-01-31 21:15:00' and status in(6,7,8,10,11) and borrowingCount > '1';")
    public List<OrdOrder> getUserOrderDataList2();


    @Select("select * from ordOrder where uuid in(SELECT EXTERNAL_ID from uangPay.T_DISBURSEMENT WHERE CREATE_TIME > '2018-11-19' and DISBURSE_STATUS = 'PENDING') and disabled = 0;")
    public List<OrdOrder> getFieldOrderWhithCIMB();

    @Select("SELECT * FROM ordOrder where disabled = 0;")
    public List<OrdOrder> getAllOrder();

    @Select("select * from ordOrder where disabled =0 and userUuid = #{userUuid} order by createTime desc")
    List<OrdOrder> getLatestOrder(@Param("userUuid")String userUuid);

    @Update("update ordOrder set status = #{status},markStatus=#{markStatus},updateTime=now() where uuid=#{orderNo} and status=#{oldStatus}")
    Integer updateOrderInfoWithOldStatus(@Param("orderNo") String orderNo,
                                         @Param("status") Integer status,
                                         @Param("markStatus") String markStatus,
                                         @Param("oldStatus") Integer oldStatus);


    // 表1、新老用户放款笔数与放款金额(RMB:万元)
    @Select("SELECT \n" +
            "    w.lendDay,\n" +
            "    www.newLendDay,\n" +
            "    www.newLendAmount,\n" +
            "    ww.oldLendNum,\n" +
            "    ww.oldLendAmount,\n" +
            "    w.allLendNum,\n" +
            "    w.allLendAmount\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        DATE(lendingTime) AS lendDay,\n" +
            "            SUM(IF(status IN (7 , 8, 10, 11), 1, 0)) AS allLendNum,\n" +
            "            FORMAT(SUM(IF(status IN (7 , 8, 10, 11), amountApply, 0)) / 20000000, 1) allLendAmount\n" +
            "    FROM\n" +
            "        doit.ordOrder\n" +
            "    WHERE\n" +
            "        disabled = 0 AND orderType IN (0 , 2)\n" +
            "            AND DATE(lendingTime) BETWEEN DATE_SUB(CURDATE(), INTERVAL 9 DAY) AND CURDATE()\n" +
            "    GROUP BY 1) w\n" +
            "        LEFT JOIN\n" +
            "    (SELECT \n" +
            "        DATE(lendingTime) AS lendDay,\n" +
            "            SUM(IF(status IN (7 , 8, 10, 11), 1, 0)) AS oldLendNum,\n" +
            "            FORMAT(SUM(IF(status IN (7 , 8, 10, 11), amountApply, 0)) / 20000000, 1) oldLendAmount\n" +
            "    FROM\n" +
            "        doit.ordOrder\n" +
            "    WHERE\n" +
            "        disabled = 0\n" +
            "            AND DATE(lendingTime) BETWEEN DATE_SUB(CURDATE(), INTERVAL 9 DAY) AND CURDATE()\n" +
            "            AND orderType IN (0 , 2)\n" +
            "            AND borrowingCount > 1\n" +
            "    GROUP BY 1) ww ON w.lendDay = ww.lendDay\n" +
            "        LEFT JOIN\n" +
            "    (SELECT \n" +
            "        DATE(lendingTime) AS lendDay,\n" +
            "            SUM(IF(status IN (7 , 8, 10, 11), 1, 0)) AS newLendDay,\n" +
            "            FORMAT(SUM(IF(status IN (7 , 8, 10, 11), amountApply, 0)) / 20000000, 1) newLendAmount\n" +
            "    FROM\n" +
            "        doit.ordOrder\n" +
            "    WHERE\n" +
            "        disabled = 0 AND orderType IN (0 , 2)\n" +
            "            AND DATE(lendingTime) BETWEEN DATE_SUB(CURDATE(), INTERVAL 9 DAY) AND CURDATE()\n" +
            "            AND borrowingCount = 1\n" +
            "    GROUP BY 1) www ON w.lendDay = www.lendDay\n" +
            "ORDER BY 1 DESC;")
    List<NewAndOldLoansAmount> getNewAndOldLoansAmount();


    //  金额逾期率
    @Select("select \n" +
            "date,                                                        -- 到期日 Tanggal Harus Membayar\n" +
            "format(orders,0) as orders,                                  -- 应还单量 Jumlah yang Harus Mengembalikan Pinjaman\n" +
            "format(repayOrder,0) as repayOrder,                          -- 已还单量 Jumlah yang sudah Lunas\n" +
            "concat(round(ordOverDueRate*100,2),'%') as ordOverDueRate,   -- 订单逾期率 Persentase Keterlambatan\n" +
            "format(dueAmount/2000,0) as dueAmount,                       -- 到期金额 Besaran Dana yang Harus Dilunasi (Tidak termasuk Biaya Keterlambatan/RMB)\n" +
            "format(repayAmount/2000,0) as repayAmount,                   -- 还款金额(本金+服务费)Besaran Dana yang Sudah Dilunasi (Blm termausk Biaya Keterlambatan/RMB)\n" +
            "concat(round(amtOverDueRate*100,2),'%') as amtOverDueRate    -- 金额逾期率 Persentase Besaran Dana Keterlambatan (Tidak Termasuk Biaya Keterlambatan)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "if(dueDay is null,'AVG',dueDay) as date,\n" +
            "count(orderId) as orders,\n" +
            "sum(case when ordStatus = 'repay' then 1 else 0 end) as repayOrder,\n" +
            "1 - sum(case when ordStatus = 'repay' then 1 else 0 end)/count(orderId) as ordOverDueRate,\n" +
            "sum(dueAmount) as dueAmount,\n" +
            "sum(case when ordStatus = 'repay' then dueAmount else 0 end) as repayAmount,\n" +
            "1 - sum(case when ordStatus = 'repay' then dueAmount else 0 end)/sum(dueAmount) as amtOverDueRate\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "orderType,\n" +
            "case when orderType = 3 then billId when orderType in (0,1,2) then orderId else null end as orderId,\n" +
            "case when orderType = 3 then billStatus in (3,4) when orderType in (0,1,2) then ordStatus else null end as ordStatus,\n" +
            "case when orderType = 3 then billStep when orderType in (0,1,2) then ordStep else null end as dayStep,\n" +
            "case when orderType = 3 then billDueDay when orderType in (0,1,2) then ordDueDay else null end as dueDay,\n" +
            "case when orderType = 3 then billDueAmount when orderType = 2 then ordApplyAmount - delayAmount else ordApplyAmount end as dueAmount\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "case when status in (7,8) then datediff(curdate(),refundtime) \n" +
            "else datediff(actualRefundTime,refundtime) end as ordStep,\n" +
            "date(refundtime) as ordDueDay,\n" +
            "date(lendingTime) as ordLendDay,\n" +
            "amountApply as ordApplyAmount,\n" +
            "if(status in (10,11),'repay','noback') as ordStatus,\n" +
            "orderType\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,1,2,3)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 20 day) and curdate()\n" +
            ") ord  \n" +
            "left join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "case when status in (1,2) then datediff(curdate(),refundtime) \n" +
            "else datediff(actualRefundTime,refundtime) end as billStep,\n" +
            "billTerm,\n" +
            "date(refundTime) as billDueDay,\n" +
            "date(actualRefundTime) as billRepayDay,\n" +
            "billAmout as billDueAmount,\n" +
            "if(status in (1,2),'repay','noback') as billStatus\n" +
            "from ordBill\n" +
            "where disabled = 0\n" +
            "and status in (1,2,3,4)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 20 day) and curdate()\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            "left join \n" +
            "(\n" +
            "select orderNo,delayOrderNo,delayFee\n" +
            "from ordDelayRecord\n" +
            "where disabled = 0 and type = 2\n" +
            ") mid on mid.orderNo = ord.orderId\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "uuid as delayOrderId,\n" +
            "status as delayStatus,\n" +
            "borrowingTerm as delayTerm,\n" +
            "amountapply as delayAmount,\n" +
            "date(refundTime) as delayDueDay,\n" +
            "date(actualRefundTime) as delayRepayDay\n" +
            "from ordOrder\n" +
            "where disabled = 0 and ordertype = 1\n" +
            ") del on del.delayOrderId = mid.delayOrderNo\n" +
            ") result\n" +
            "where orderId is not null\n" +
            "group by dueDay with rollup\n" +
            ") result;")
    List<MoneyOverdueRate> getMoneyOverdueRate();

    // 表2、用户d1至d7逾期率（新户+老户）
    @Select("SELECT \n" +
            "date(refundTime) as date,  -- 到期日\n" +
            "COUNT(DISTINCT uuid) as expirenum,  -- 合计到期笔数\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as overdue_d1,  -- D1逾期率\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>1,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as overdue_d2,  -- D2逾期率\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>2,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as \n" +
            "overdue_d3,  -- D3逾期率\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>3,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as \n" +
            "overdue_d4,  -- D4逾期率\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>4,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as \n" +
            "overdue_d5,  -- D5逾期率\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>5,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as \n" +
            "overdue_d6,  -- D6逾期率\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as \n" +
            "overdue_d7  -- D7逾期率\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and date(refundTime) between date_sub(curdate(),interval 10 day) and curdate()-1\n" +
            "GROUP BY 1\n" +
            "order by 1 desc\n" +
            ";")
    List<NewAndOldD1D7OverdueRate> getNewAndOldD1D7OverdueRate();

    // 表3、用户d1至d7逾期率（新户）
    @Select("SELECT \n" +
            "date(refundTime) as arrivalDay,-- ???\n" +
            "COUNT(DISTINCT uuid) as newArrivalNum,-- ??????\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD1OverdueRate,-- ??D1???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>1,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD2OverdueRate,-- ??D2???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>2,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD3OverdueRate,-- ??D3???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>3,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD4OverdueRate,-- ??D4???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>4,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD5OverdueRate,-- ??D5???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>5,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD6OverdueRate,-- ??D6???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD7OverdueRate -- ??D7???\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and date(refundTime) between date_sub(curdate(),interval 10 day) and curdate()-1 -- 2?5??? \n" +
            "and borrowingCount=1#??\n" +
            "GROUP BY 1\n" +
            "order by 1 desc;")
    List<NewD1D7OverdueRate> getNewD1D7OverdueRate();

    // 表4、用户户d1至d7逾期率（老户）
    @Select("SELECT \n" +
            "date(refundTime) as arrivalDay,-- ???\n" +
            "COUNT(DISTINCT uuid) as oldArrivalNum,-- ??????\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD1OverdueRate,-- ??D1???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>1,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD2OverdueRate,-- ??D2???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>2,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD3OverdueRate,-- ??D3???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>3,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD4OverdueRate,-- ??D4???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>4,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD5OverdueRate,-- ??D5???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>5,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD6OverdueRate,-- ??D6???\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD7OverdueRate--  ??D7???\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and date(refundTime) between date_sub(curdate(),interval 10 day) and curdate()-1 -- 2?5??? \n" +
            "and borrowingCount>1#??\n" +
            "GROUP BY 1\n" +
            "order by 1 desc;")
    List<OldD1D7OverdueRate> getOldD1D7OverdueRate();

    // 表5、新老用户d7逾期率
    @Select("SELECT\n" +
            "z.p1,\n" +
            "zzz.p2,\n" +
            "zzz.p3,\n" +
            "zzz.p4,\n" +
            "zz.p5,\n" +
            "zz.p6,\n" +
            "zz.p7,\n" +
            "z.p8,\n" +
            "z.p9,\n" +
            "z.p10\n" +
            "from\n" +
            "(SELECT \n" +
            "date(refundTime) as p1,\n" +
            "COUNT(DISTINCT uuid) as p8,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as p9,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as p10\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and DATEDIFF(CURDATE(),refundTime)>6\n" +
            "GROUP BY 1)z\n" +
            "LEFT JOIN\n" +
            "(SELECT \n" +
            "date(refundTime) as p1,\n" +
            "COUNT(DISTINCT uuid) as p5,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as p6,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as p7\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and DATEDIFF(CURDATE(),refundTime)>6\n" +
            "and borrowingCount>1#老户\n" +
            "GROUP BY 1)zz on z.p1=zz.p1\n" +
            "LEFT JOIN\n" +
            "(SELECT \n" +
            "date(refundTime) as p1,\n" +
            "COUNT(DISTINCT uuid) as p2,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as p3,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as p4\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and DATEDIFF(CURDATE(),refundTime)>6\n" +
            "and borrowingCount=1#新户\n" +
            "GROUP BY 1)zzz on z.p1=zzz.p1\n" +
            "order by 1 desc\n" +
            "limit 10;")
    List<NewAndOldD7OverdueRate> getNewAndOldD7OverdueRate();

    // 表6、新老用户D15逾期率
    @Select("SELECT\n" +
            "z.arrivalDay,\t\t\t\t-- ???\n" +
            "zzz.newArrivalNum,\t\t-- ??????\n" +
            "zzz.newD1OverdueRate,\t\t-- ??D1???\n" +
            "zzz.newD7OverdueRate,\t\t-- ??D7???\n" +
            "zzz.newD15OverdueRate,\t\t-- ??D15???\n" +
            "zz.oldArrivalNum,\t\t-- ??????\n" +
            "zz.oldD1OverdueRate,\t\t-- ??D1???\n" +
            "zz.oldD7OverdueRate,\t\t-- ??D7???\n" +
            "zz.oldD15OverdueRate,\t\t-- ??D15???\n" +
            "z.allArrivalNum,\t\t\t-- ??????\n" +
            "z.allD1OverdueRate,\t\t\t-- ??D1???\n" +
            "z.allD7OverdueRate,\t\t\t-- ??D7???\n" +
            "z.allD15OverdueRate\t\t\t-- ??D15???\n" +
            "from\n" +
            "(SELECT \n" +
            "date(refundTime) as arrivalDay,\n" +
            "COUNT(DISTINCT uuid) as allArrivalNum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD1OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD7OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>14,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD15OverdueRate\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and DATEDIFF(CURDATE(),refundTime)>14 \n" +
            "and date(refundTime)>'2018-01-23'\n" +
            "GROUP BY 1\n" +
            "order by 1 desc \n" +
            "limit 10)z\n" +
            "LEFT JOIN\n" +
            "(SELECT \n" +
            "date(refundTime) as arrivalDay,\n" +
            "COUNT(DISTINCT uuid) as oldArrivalNum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD1OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD7OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>14,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD15OverdueRate\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and  DATEDIFF(CURDATE(),refundTime)>14\n" +
            "and borrowingCount>1#??\n" +
            "GROUP BY 1\n" +
            "order by 1 desc \n" +
            "limit 10)zz on z.arrivalDay=zz.arrivalDay\n" +
            "LEFT JOIN\n" +
            "(SELECT \n" +
            "date(refundTime) as arrivalDay,\n" +
            "COUNT(DISTINCT uuid) as newArrivalNum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD1OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD7OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>14,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD15OverdueRate\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and  DATEDIFF(CURDATE(),refundTime)>14\n" +
            "and borrowingCount=1#??\n" +
            "GROUP BY 1\n" +
            "order by 1 desc \n" +
            "limit 10)zzz on z.arrivalDay=zzz.arrivalDay\n" +
            "order by 1 desc;")
    List<NewDOverdueRate> getNewD15OverdueRate();

    // 表7、新老用户D30逾期率
    @Select("SELECT\n" +
            "z.arrivalDay,\t\t\t-- ???\n" +
            "zzz.newArrivalNum,\t\t-- ??????\n" +
            "zzz.newD1OverdueRate,\t\t-- ??D1???\n" +
            "zzz.newD7OverdueRate,\t\t-- ??D7???\n" +
            "zzz.newD15OverdueRate,\t\t-- ??D15???\n" +
            "zzz.newD30OverdueRate,\t\t-- ??D30??? \n" +
            "zz.oldArrivalNum,\t\t-- ??????\n" +
            "zz.oldD1OverdueRate,\t\t-- ??D1???\n" +
            "zz.oldD7OverdueRate,\t\t-- ??D7???\n" +
            "zz.oldD15OverdueRate,\t\t-- ??D15???\n" +
            "zz.oldD30OverdueRate,\t\t-- ??D30???\n" +
            "z.allArrivalNum,\t\t\t-- ??????\n" +
            "z.allD1OverdueRate,\t\t\t-- ??D1???\n" +
            "z.allD7OverdueRate,\t\t\t-- ??D7???\n" +
            "z.allD15OverdueRate,\t\t-- ??D15???\n" +
            "z.allD30OverdueRate\t\t\t-- ??D30???\n" +
            "from\n" +
            "(SELECT \n" +
            "date(refundTime) as arrivalDay,\n" +
            "COUNT(DISTINCT uuid) as allArrivalNum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD1OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD7OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>14,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD15OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>29,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as allD30OverdueRate\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and DATEDIFF(CURDATE(),refundTime)>29\n" +
            "and date(refundTime)>'2018-01-23'\n" +
            "GROUP BY 1\n" +
            "order by 1 desc \n" +
            "limit 10)z\n" +
            "LEFT JOIN\n" +
            "(SELECT \n" +
            "date(refundTime) as arrivalDay,\n" +
            "COUNT(DISTINCT uuid) as oldArrivalNum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD1OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD7OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>14,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD15OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>29,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as oldD30OverdueRate\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and  DATEDIFF(CURDATE(),refundTime)>29\n" +
            "and borrowingCount>1#??\n" +
            "GROUP BY 1\n" +
            "order by 1 desc \n" +
            "limit 10)zz on z.arrivalDay=zz.arrivalDay\n" +
            "LEFT JOIN\n" +
            "(SELECT \n" +
            "date(refundTime) as arrivalDay,\n" +
            "COUNT(DISTINCT uuid) as newArrivalNum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD1OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD7OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>14,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD15OverdueRate,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>29,1,0))/COUNT(DISTINCT Uuid)*100,1),'%') as newD30OverdueRate\n" +
            "from ordOrder d \n" +
            "where STATUS in (7,8,10,11) and disabled=0\n" +
            "and  DATEDIFF(CURDATE(),refundTime)>29\n" +
            "and borrowingCount=1#??\n" +
            "GROUP BY 1\n" +
            "order by 1 desc \n" +
            "limit 10)zzz on z.arrivalDay=zzz.arrivalDay\n" +
            "order by 1 desc;")
    List<NewDOverdueRate> getNewD30OverdueRate();


    // 表8、新老用户每日到期用户提前还款率
    @Select("SELECT\n" +
            "t1.date as p1,\n" +
            "t2.p2,\n" +
            "t2.p3,\n" +
            "t2.p4,\n" +
            "t3.p5,\n" +
            "t3.p6,\n" +
            "t3.p7,\n" +
            "t1.p8,\n" +
            "t1.p9,\n" +
            "t1.p10\n" +
            "from \n" +
            "(SELECT\n" +
            "z.*,\n" +
            "CONCAT(FORMAT(p9/p8 *100,1),'%') as p10\n" +
            "from\n" +
            "(SELECT\n" +
            "date(refundTime) as date,\n" +
            "count(distinct uuid)p8,\n" +
            "SUM(IF(DATEDIFF(refundTime,actualRefundTime)>0,1,0)) as p9\n" +
            "FROM ordOrder \n" +
            "where disabled=0\n" +
            "and `status` in (7,8,10,11)\n" +
            "and DATEDIFF(CURDATE(),refundTime)<4\n" +
            "and DATEDIFF(refundTime,CURDATE())<14\n" +
            "group by 1\n" +
            "order by 1 desc)z)t1\n" +
            "left join\n" +
            "(SELECT\n" +
            "z.*,\n" +
            "CONCAT(FORMAT(p3/p2 *100,1),'%') as p4\n" +
            "from\n" +
            "(SELECT\n" +
            "date(refundTime) as date,\n" +
            "count(distinct uuid)p2,\n" +
            "SUM(IF(DATEDIFF(refundTime,actualRefundTime)>0,1,0)) as p3\n" +
            "FROM ordOrder \n" +
            "where disabled=0\n" +
            "and `status` in (7,8,10,11)\n" +
            "and borrowingCount=1#新户\n" +
            "and DATEDIFF(CURDATE(),refundTime)<4\n" +
            "and DATEDIFF(refundTime,CURDATE())<14\n" +
            "group by 1\n" +
            "order by 1 desc)z)t2 on t1.date=t2.date\n" +
            "left join\n" +
            "(SELECT\n" +
            "z.*,\n" +
            "CONCAT(FORMAT(p6/p5 *100,1),'%') as p7\n" +
            "from\n" +
            "(SELECT\n" +
            "date(refundTime) as date,\n" +
            "count(distinct uuid)p5,\n" +
            "SUM(IF(DATEDIFF(refundTime,actualRefundTime)>0,1,0)) as p6\n" +
            "FROM ordOrder \n" +
            "where disabled=0\n" +
            "and `status` in (7,8,10,11)\n" +
            "and borrowingCount>1#老户\n" +
            "and DATEDIFF(CURDATE(),refundTime)<4\n" +
            "and DATEDIFF(refundTime,CURDATE())<14\n" +
            "group by 1\n" +
            "order by 1 desc)z)t3 on t1.date=t3.date\n" +
            ";\n")
    List<DailybeforehandBackRate> getDailybeforehandBackRate();


    // 免电核规则D1至D7逾期率
    @Select("select\n" +
            "date ,  -- 到期日\n" +
            "ruleDesc,  -- 免电核规则\n" +
            "duenum,  -- 到期笔数\n" +
            "overduerateD1,  -- D1逾期率\n" +
            "overduerateD2,  -- D2逾期率\n" +
            "overduerateD3,  -- D3逾期率\n" +
            "overduerateD4,  -- D4逾期率\n" +
            "overduerateD5,  -- D5逾期率\n" +
            "overduerateD6,  -- D6逾期率\n" +
            "overduerateD7  -- D7逾期率\n" +
            "from \n" +
            "((SELECT \n" +
            "date(refundTime) as date,\n" +
            "'合计' as ruleDesc,\n" +
            "COUNT(DISTINCT d.uuid) as duenum,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD1,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>1,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD2,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>2,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD3,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>3,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD4,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>4,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD5,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>5,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%')as overduerateD6,\n" +
            "CONCAT(FORMAT(count(distinct IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,d.uuid,null))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD7\n" +
            "from ordOrder d \n" +
            "inner join ordRiskRecord \tr on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u on u.ruleDetailType=r.ruleDetailType\n" +
            "where STATUS in (7,8,10,11) and d.disabled=0 and r.disabled=0\n" +
            "and datediff(CURDATE(),refundTime) BETWEEN 1 and 5\n" +
            "and borrowingCount=1\n" +
            "and r.ruleRealValue ='true'\n" +
            "GROUP BY 1,2\n" +
            "order by 1 desc)\n" +
            "union all\n" +
            "(SELECT \n" +
            "date(refundTime) as date,\n" +
            "u.ruleDesc,\n" +
            "COUNT(DISTINCT d.uuid) as duenum,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD1,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>1,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD2,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>2,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD3,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>3,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD4,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>4,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD5,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>5,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD6,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT d.Uuid)*100,1),'%') as overduerateD7\n" +
            "from ordOrder d \n" +
            "inner join ordRiskRecord \tr on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u on u.ruleDetailType=r.ruleDetailType\n" +
            "where STATUS in (7,8,10,11) and d.disabled=0 and r.disabled=0\n" +
            "and datediff(CURDATE(),refundTime) BETWEEN 1 and 5\n" +
            "and borrowingCount=1\n" +
            "and r.ruleRealValue ='true'\n" +
            "GROUP BY 1,2\n" +
            "order by 1 desc))b \n" +
            "order by 1 desc,3;")
    List<DailyRateD1ToD7WithNoPhone> getDailyRateD1ToD7WithNoPhone();


    // 新增城市的风控通过率
    @Select("SELECT\n" +
            "DATE_FORMAT(createTime,'%x年-第%v周') as p1,  -- 创建周, %v 星期一是一周的第一天,\n" +
            "city as p2,   -- 新增城市,\n" +
            "count(uuid) p3,  -- 提交订单人数,\n" +
            "count(uuid) -SUM(if(dstatus in (12,13,14,15),1,0)) p4, -- 通过人数,\n" +
            "Concat(FORMAT((1-SUM(if(dstatus in (12,13,14,15),1,0))/count(uuid))*100,2),'%') p5 -- 风控通过率\n" +
            "from \n" +
            "(SELECT DISTINCT h.createTime,d.uuid,d.userUuid, d.status as dstatus,h.status as hstatus, city from\n" +
            "ordOrder d\n" +
            "JOIN\n" +
            "ordHistory h on d.uuid=h.orderId\n" +
            "join\n" +
            "usrAddressDetail t on d.userUuid=t.userUuid\n" +
            "where d.disabled=0 and borrowingCount=1\n" +
            "and city in ('Kabupaten Serang','Kota Serang','Kota Yogyakarta','Kota Surabaya','Kota Pontianak','Kota Makassar','Kota Palembang','Kota Medan')\n" +
            "and t.disabled=0 and h.createTime>='2018-6-26 18:30:00' and h.status=2 and DATEDIFF(NOW(),h.createTime)<30 and addressType in (1,2))w \n" +
            "GROUP BY 1,2\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "SELECT\n" +
            "DATE_FORMAT(createTime,'%x年-第%v周') as RefundWeek,  -- 到期星期, %v 星期一是一周的第一天,\n" +
            "city as NewCity,   -- 新增城市,\n" +
            "count(uuid) SubmitPeople,  -- 提交订单人数,\n" +
            "count(uuid) -SUM(if(dstatus in (12,13,14,15),1,0)) PassPeople, -- 通过人数,\n" +
            "Concat(FORMAT((1-SUM(if(dstatus in (12,13,14,15),1,0))/count(uuid))*100,2),'%') CheckPassRate -- 风控通过率\n" +
            "from \n" +
            "(SELECT DISTINCT h.createTime,d.uuid,d.userUuid, d.status as dstatus,h.status as hstatus, city from\n" +
            "ordOrder d\n" +
            "JOIN\n" +
            "ordHistory h on d.uuid=h.orderId\n" +
            "join\n" +
            "usrAddressDetail t on d.userUuid=t.userUuid\n" +
            "where d.disabled=0 and borrowingCount=1\n" +
            "and city in ('Kabupaten Semarang','Kota Semarang','Kota Banjarmasin','Kota Bandar Lampung','Kota Pekanbaru','Kota Padang','Surakarta (Solo)')\n" +
            "and t.disabled=0 and h.createTime>='2018-7-25 16:15:00' and h.status=2 and DATEDIFF(NOW(),h.createTime)<30 and addressType in (1,2))w\n" +
            "GROUP BY 1,2\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "SELECT\n" +
            "DATE_FORMAT(createTime,'%x年-第%v周') as RefundWeek,  -- 到期星期, %v 星期一是一周的第一天,\n" +
            "city as NewCity,   -- 新增城市,\n" +
            "count(uuid) SubmitPeople,  -- 提交订单人数,\n" +
            "count(uuid) -SUM(if(dstatus in (12,13,14,15),1,0)) PassPeople, -- 通过人数,\n" +
            "Concat(FORMAT((1-SUM(if(dstatus in (12,13,14,15),1,0))/count(uuid))*100,2),'%') CheckPassRate -- 风控通过率\n" +
            "from \n" +
            "(SELECT DISTINCT h.createTime,d.uuid,d.userUuid, d.status as dstatus,h.status as hstatus, city from\n" +
            "ordOrder d\n" +
            "JOIN\n" +
            "ordHistory h on d.uuid=h.orderId\n" +
            "join\n" +
            "usrAddressDetail t on d.userUuid=t.userUuid\n" +
            "where d.disabled=0 and borrowingCount=1\n" +
            "and city in ('Kabupaten Sidoarjo','Kabupaten Malang','Kota Malang','Kota Batam','Kota Manado','Kabupaten Bantul','Kabupaten Kediri','Kota Kediri','Kabupaten Cianjur')\n" +
            "and t.disabled=0 and h.createTime>='2018-8-13 18:50:00' and h.status=2 and DATEDIFF(NOW(),h.createTime)<30 and addressType in (1,2))w\n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc;")
    List<NewCityRiskRate> getNewCityRiskRate();

    // 新增城市的逾期率
    @Select("SELECT\n" +
            "DATE_FORMAT(refundTime,'%x年-第%v周') as p1, -- 到期星期, %v 星期一是一周的第一天,\n" +
            "city as p2, -- 新增城市\n" +
            "COUNT(DISTINCT uuid) as  p3, -- 新户到期笔数,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>0,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p4, -- 新户D1逾期率,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>1,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p5, -- 新户D2逾期率,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>2,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p6, -- 新户D3逾期率,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>3,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p7, -- 新户D4逾期率,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>4,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p8, -- 新户D5逾期率,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>5,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p9, -- 新户D6逾期率,\n" +
            "CONCAT(FORMAT(SUM(IF(IF(dstatus=8,DATEDIFF(NOW(),refundTime),DATEDIFF(actualRefundTime,refundTime))>6,1,0))/COUNT(DISTINCT uuid)*100,1),'%') as p10 -- 新户D7逾期率\n" +
            "from \n" +
            "(SELECT DISTINCT h.createTime,d.refundTime,d.actualRefundTime, d.uuid,d.userUuid, d.status as dstatus,h.status as hstatus, city from\n" +
            "ordOrder d\n" +
            "JOIN\n" +
            "ordHistory h on d.uuid=h.orderId\n" +
            "join\n" +
            "usrAddressDetail t on d.userUuid=t.userUuid\n" +
            "where d.disabled=0 and borrowingCount=1\n" +
            "and city in ('Kabupaten Serang','Kota Serang','Kota Yogyakarta','Kota Surabaya','Kota Pontianak','Kota Makassar',\n" +
            "'Kota Palembang','Kota Medan','Kabupaten Semarang','Kota Semarang','Kota Banjarmasin','Kota Bandar Lampung',\n" +
            "'Kota Pekanbaru','Kota Padang','Surakarta (Solo)','Kabupaten Sidoarjo','Kabupaten Malang','Kota Malang','Kota Batam',\n" +
            "'Kota Manado','Kabupaten Bantul','Kabupaten Kediri','Kota Kediri','Kabupaten Cianjur') and addressType in (1,2)\n" +
            "and t.disabled=0 and h.createTime>='2018-6-26 18:30:00' and h.status=2\n" +
            "and d.status in (7,8,10,11) \n" +
            ")w \n" +
            "where date(refundTime) between date_sub(curdate(),interval 30 day) and curdate()-1 \n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc")
    List<NewCityOverDueRate> getNewCityOverDueRate();


    // Doit 每日逾期率
    @Select("select \n" +
            "refundDay,\t\t\t\t              -- 到期日\n" +
            "count(1) as nums,                                                                    -- 到期笔数\n" +
            "concat(round(sum(case when step >= 1 then 1 else 0 end)/count(1)*100,2),'%') as D1,  -- D1逾期率 \n" +
            "concat(round(sum(case when step >= 2 then 1 else 0 end)/count(1)*100,2),'%') as D2,  -- D2逾期率\n" +
            "concat(round(sum(case when step >= 3 then 1 else 0 end)/count(1)*100,2),'%') as D3,  -- D3逾期率\n" +
            "concat(round(sum(case when step >= 4 then 1 else 0 end)/count(1)*100,2),'%') as D4,  -- D4逾期率\n" +
            "concat(round(sum(case when step >= 5 then 1 else 0 end)/count(1)*100,2),'%') as D5,  -- D5逾期率\n" +
            "concat(round(sum(case when step >= 6 then 1 else 0 end)/count(1)*100,2),'%') as D6,  -- D6逾期率 \n" +
            "concat(round(sum(case when step >= 7 then 1 else 0 end)/count(1)*100,2),'%') as D7,  -- D7逾期率\n" +
            "concat(round(sum(case when step >= 8 then 1 else 0 end)/count(1)*100,2),'%') as D8,  -- D8逾期率\n" +
            "concat(round(sum(case when step >= 9 then 1 else 0 end)/count(1)*100,2),'%') as D9,  -- D9逾期率\n" +
            "concat(round(sum(case when step >= 10 then 1 else 0 end)/count(1)*100,2),'%') as D10,  -- D10逾期率\n" +
            "concat(round(sum(case when step >= 11 then 1 else 0 end)/count(1)*100,2),'%') as D11,  -- D11逾期率\n" +
            "concat(round(sum(case when step >= 12 then 1 else 0 end)/count(1)*100,2),'%') as D12,  -- D12逾期率\n" +
            "concat(round(sum(case when step >= 13 then 1 else 0 end)/count(1)*100,2),'%') as D13,  -- D13逾期率\n" +
            "concat(round(sum(case when step >= 14 then 1 else 0 end)/count(1)*100,2),'%') as D14,  -- D14逾期率\n" +
            "concat(round(sum(case when step >= 15 then 1 else 0 end)/count(1)*100,2),'%') as D15,   -- D15逾期率\n" +
            "concat(round(sum(case when step >= 16 then 1 else 0 end)/count(1)*100,2),'%') as D16,  -- D16逾期率\n" +
            "concat(round(sum(case when step >= 17 then 1 else 0 end)/count(1)*100,2),'%') as D17,   -- D17逾期率\n" +
            "concat(round(sum(case when step >= 18 then 1 else 0 end)/count(1)*100,2),'%') as D18,  -- D18逾期率\n" +
            "concat(round(sum(case when step >= 19 then 1 else 0 end)/count(1)*100,2),'%') as D19,  -- D19逾期率\n" +
            "concat(round(sum(case when step >= 20 then 1 else 0 end)/count(1)*100,2),'%') as D20,  -- D20逾期率\n" +
            "concat(round(sum(case when step >= 21 then 1 else 0 end)/count(1)*100,2),'%') as D21,  -- D21逾期率\n" +
            "concat(round(sum(case when step >= 22 then 1 else 0 end)/count(1)*100,2),'%') as D22,  -- D22逾期率\n" +
            "concat(round(sum(case when step >= 23 then 1 else 0 end)/count(1)*100,2),'%') as D23,  -- D23逾期率\n" +
            "concat(round(sum(case when step >= 24 then 1 else 0 end)/count(1)*100,2),'%') as D24,  -- D24逾期率\n" +
            "concat(round(sum(case when step >= 25 then 1 else 0 end)/count(1)*100,2),'%') as D25,  -- D25逾期率\n" +
            "concat(round(sum(case when step >= 26 then 1 else 0 end)/count(1)*100,2),'%') as D26,  -- D26逾期率\n" +
            "concat(round(sum(case when step >= 27 then 1 else 0 end)/count(1)*100,2),'%') as D27,  -- D27逾期率\n" +
            "concat(round(sum(case when step >= 28 then 1 else 0 end)/count(1)*100,2),'%') as D28,  -- D28逾期率\n" +
            "concat(round(sum(case when step >= 29 then 1 else 0 end)/count(1)*100,2),'%') as D29,  -- D29逾期率\n" +
            "concat(round(sum(case when step >= 30 then 1 else 0 end)/count(1)*100,2),'%') as D30,  -- D30逾期率\n" +
            "concat(round(sum(case when step >= 31 then 1 else 0 end)/count(1)*100,2),'%') as D31,  -- D31逾期率\n" +
            "concat(round(sum(case when step >= 32 then 1 else 0 end)/count(1)*100,2),'%') as D32   -- D32逾期率\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundtime) as refundDay,\n" +
            "uuid,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and date(refundtime) <= date_sub(curdate(),interval 1 day)\n" +
            "and date(refundTime) >= '2018-06-01'\n" +
            ") ord  \n" +
            "group by refundDay;")
    List<OverDueRateForMail> getDayOverdueRate();

    // Doit 每日逾期率(新户)
    @Select("select \n" +
            "refundDay,\t\t\t\t               -- 到期日\n" +
            "count(1) as nums,                                                                    -- 到期笔数\n" +
            "concat(round(sum(case when step >= 1 then 1 else 0 end)/count(1)*100,2),'%') as D1,  -- D1逾期率 \n" +
            "concat(round(sum(case when step >= 2 then 1 else 0 end)/count(1)*100,2),'%') as D2,  -- D2逾期率\n" +
            "concat(round(sum(case when step >= 3 then 1 else 0 end)/count(1)*100,2),'%') as D3,  -- D3逾期率\n" +
            "concat(round(sum(case when step >= 4 then 1 else 0 end)/count(1)*100,2),'%') as D4,  -- D4逾期率\n" +
            "concat(round(sum(case when step >= 5 then 1 else 0 end)/count(1)*100,2),'%') as D5,  -- D5逾期率\n" +
            "concat(round(sum(case when step >= 6 then 1 else 0 end)/count(1)*100,2),'%') as D6,  -- D6逾期率 \n" +
            "concat(round(sum(case when step >= 7 then 1 else 0 end)/count(1)*100,2),'%') as D7,  -- D7逾期率\n" +
            "concat(round(sum(case when step >= 8 then 1 else 0 end)/count(1)*100,2),'%') as D8,  -- D8逾期率\n" +
            "concat(round(sum(case when step >= 9 then 1 else 0 end)/count(1)*100,2),'%') as D9,  -- D9逾期率\n" +
            "concat(round(sum(case when step >= 10 then 1 else 0 end)/count(1)*100,2),'%') as D10,  -- D10逾期率\n" +
            "concat(round(sum(case when step >= 11 then 1 else 0 end)/count(1)*100,2),'%') as D11,  -- D11逾期率\n" +
            "concat(round(sum(case when step >= 12 then 1 else 0 end)/count(1)*100,2),'%') as D12,  -- D12逾期率\n" +
            "concat(round(sum(case when step >= 13 then 1 else 0 end)/count(1)*100,2),'%') as D13,  -- D13逾期率\n" +
            "concat(round(sum(case when step >= 14 then 1 else 0 end)/count(1)*100,2),'%') as D14,  -- D14逾期率\n" +
            "concat(round(sum(case when step >= 15 then 1 else 0 end)/count(1)*100,2),'%') as D15,   -- D15逾期率\n" +
            "concat(round(sum(case when step >= 16 then 1 else 0 end)/count(1)*100,2),'%') as D16,  -- D16逾期率\n" +
            "concat(round(sum(case when step >= 17 then 1 else 0 end)/count(1)*100,2),'%') as D17,   -- D17逾期率\n" +
            "concat(round(sum(case when step >= 18 then 1 else 0 end)/count(1)*100,2),'%') as D18,  -- D18逾期率\n" +
            "concat(round(sum(case when step >= 19 then 1 else 0 end)/count(1)*100,2),'%') as D19,  -- D19逾期率\n" +
            "concat(round(sum(case when step >= 20 then 1 else 0 end)/count(1)*100,2),'%') as D20,  -- D20逾期率\n" +
            "concat(round(sum(case when step >= 21 then 1 else 0 end)/count(1)*100,2),'%') as D21,  -- D21逾期率\n" +
            "concat(round(sum(case when step >= 22 then 1 else 0 end)/count(1)*100,2),'%') as D22,  -- D22逾期率\n" +
            "concat(round(sum(case when step >= 23 then 1 else 0 end)/count(1)*100,2),'%') as D23,  -- D23逾期率\n" +
            "concat(round(sum(case when step >= 24 then 1 else 0 end)/count(1)*100,2),'%') as D24,  -- D24逾期率\n" +
            "concat(round(sum(case when step >= 25 then 1 else 0 end)/count(1)*100,2),'%') as D25,  -- D25逾期率\n" +
            "concat(round(sum(case when step >= 26 then 1 else 0 end)/count(1)*100,2),'%') as D26,  -- D26逾期率\n" +
            "concat(round(sum(case when step >= 27 then 1 else 0 end)/count(1)*100,2),'%') as D27,  -- D27逾期率\n" +
            "concat(round(sum(case when step >= 28 then 1 else 0 end)/count(1)*100,2),'%') as D28,  -- D28逾期率\n" +
            "concat(round(sum(case when step >= 29 then 1 else 0 end)/count(1)*100,2),'%') as D29,  -- D29逾期率\n" +
            "concat(round(sum(case when step >= 30 then 1 else 0 end)/count(1)*100,2),'%') as D30,  -- D30逾期率\n" +
            "concat(round(sum(case when step >= 31 then 1 else 0 end)/count(1)*100,2),'%') as D31,  -- D31逾期率\n" +
            "concat(round(sum(case when step >= 32 then 1 else 0 end)/count(1)*100,2),'%') as D32   -- D32逾期率\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundtime) as refundDay,\n" +
            "uuid,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and borrowingCount = 1 \n" +
            "and status in (7,8,10,11)\n" +
            "and date(refundtime) <= date_sub(curdate(),interval 1 day)\n" +
            "and date(refundTime) >= '2018-06-01'\n" +
            ") ord  \n" +
            "group by refundDay;")
    List<OverDueRateForMail> getDayOverdueRateWithNewUser();

    // Doit 每日逾期率(老户)
    @Select("select \n" +
            "refundDay,\t\t\t\t               -- 到期日\n" +
            "count(1) as nums,                                                                    -- 到期笔数\n" +
            "concat(round(sum(case when step >= 1 then 1 else 0 end)/count(1)*100,2),'%') as D1,  -- D1逾期率 \n" +
            "concat(round(sum(case when step >= 2 then 1 else 0 end)/count(1)*100,2),'%') as D2,  -- D2逾期率\n" +
            "concat(round(sum(case when step >= 3 then 1 else 0 end)/count(1)*100,2),'%') as D3,  -- D3逾期率\n" +
            "concat(round(sum(case when step >= 4 then 1 else 0 end)/count(1)*100,2),'%') as D4,  -- D4逾期率\n" +
            "concat(round(sum(case when step >= 5 then 1 else 0 end)/count(1)*100,2),'%') as D5,  -- D5逾期率\n" +
            "concat(round(sum(case when step >= 6 then 1 else 0 end)/count(1)*100,2),'%') as D6,  -- D6逾期率 \n" +
            "concat(round(sum(case when step >= 7 then 1 else 0 end)/count(1)*100,2),'%') as D7,  -- D7逾期率\n" +
            "concat(round(sum(case when step >= 8 then 1 else 0 end)/count(1)*100,2),'%') as D8,  -- D8逾期率\n" +
            "concat(round(sum(case when step >= 9 then 1 else 0 end)/count(1)*100,2),'%') as D9,  -- D9逾期率\n" +
            "concat(round(sum(case when step >= 10 then 1 else 0 end)/count(1)*100,2),'%') as D10,  -- D10逾期率\n" +
            "concat(round(sum(case when step >= 11 then 1 else 0 end)/count(1)*100,2),'%') as D11,  -- D11逾期率\n" +
            "concat(round(sum(case when step >= 12 then 1 else 0 end)/count(1)*100,2),'%') as D12,  -- D12逾期率\n" +
            "concat(round(sum(case when step >= 13 then 1 else 0 end)/count(1)*100,2),'%') as D13,  -- D13逾期率\n" +
            "concat(round(sum(case when step >= 14 then 1 else 0 end)/count(1)*100,2),'%') as D14,  -- D14逾期率\n" +
            "concat(round(sum(case when step >= 15 then 1 else 0 end)/count(1)*100,2),'%') as D15,   -- D15逾期率\n" +
            "concat(round(sum(case when step >= 16 then 1 else 0 end)/count(1)*100,2),'%') as D16,  -- D16逾期率\n" +
            "concat(round(sum(case when step >= 17 then 1 else 0 end)/count(1)*100,2),'%') as D17,   -- D17逾期率\n" +
            "concat(round(sum(case when step >= 18 then 1 else 0 end)/count(1)*100,2),'%') as D18,  -- D18逾期率\n" +
            "concat(round(sum(case when step >= 19 then 1 else 0 end)/count(1)*100,2),'%') as D19,  -- D19逾期率\n" +
            "concat(round(sum(case when step >= 20 then 1 else 0 end)/count(1)*100,2),'%') as D20,  -- D20逾期率\n" +
            "concat(round(sum(case when step >= 21 then 1 else 0 end)/count(1)*100,2),'%') as D21,  -- D21逾期率\n" +
            "concat(round(sum(case when step >= 22 then 1 else 0 end)/count(1)*100,2),'%') as D22,  -- D22逾期率\n" +
            "concat(round(sum(case when step >= 23 then 1 else 0 end)/count(1)*100,2),'%') as D23,  -- D23逾期率\n" +
            "concat(round(sum(case when step >= 24 then 1 else 0 end)/count(1)*100,2),'%') as D24,  -- D24逾期率\n" +
            "concat(round(sum(case when step >= 25 then 1 else 0 end)/count(1)*100,2),'%') as D25,  -- D25逾期率\n" +
            "concat(round(sum(case when step >= 26 then 1 else 0 end)/count(1)*100,2),'%') as D26,  -- D26逾期率\n" +
            "concat(round(sum(case when step >= 27 then 1 else 0 end)/count(1)*100,2),'%') as D27,  -- D27逾期率\n" +
            "concat(round(sum(case when step >= 28 then 1 else 0 end)/count(1)*100,2),'%') as D28,  -- D28逾期率\n" +
            "concat(round(sum(case when step >= 29 then 1 else 0 end)/count(1)*100,2),'%') as D29,  -- D29逾期率\n" +
            "concat(round(sum(case when step >= 30 then 1 else 0 end)/count(1)*100,2),'%') as D30,  -- D30逾期率\n" +
            "concat(round(sum(case when step >= 31 then 1 else 0 end)/count(1)*100,2),'%') as D31,  -- D31逾期率\n" +
            "concat(round(sum(case when step >= 32 then 1 else 0 end)/count(1)*100,2),'%') as D32   -- D32逾期率\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundtime) as refundDay,\n" +
            "uuid,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and borrowingCount > 1 \n" +
            "and status in (7,8,10,11)\n" +
            "and date(refundtime) <= date_sub(curdate(),interval 1 day)\n" +
            "and date(refundTime) >= '2018-06-01'\n" +
            ") ord  \n" +
            "group by refundDay;")
    List<OverDueRateForMail> getDayOverdueRateWithOldUser();


    // 数据app相关
    /*通过订单状态统计订单金额*/
    @Select("select sum(amountApply) from ordOrder where disabled = 0 and status in ( ${orderStatus} ) ")
    public BigDecimal orderListSumByStatus(@Param("orderStatus") String orderStatus);

    /*通过订单状态统计订单数量*/
    @Select("select count(1) from ordOrder where disabled = 0 and status in ( ${orderStatus} ) ")
    public Integer orderListCountByStatus(@Param("orderStatus") String orderStatus);

    /*通过refundTime和status统计订单数*/
    @Select("select count(1) from ordOrder where status in ( ${orderStatus} ) and refundTime >=  '${time} 00:00:00' and refundTime <= '${time} 23:59:59' and disabled=0 ")
    public Integer orderCountByRefundTimeStatus(@Param("orderStatus") String orderStatus,@Param("time") String Time);

    /*累计还款金额*/
    @Select("select sum(amountApply) from ordOrder where status in( 10,11 )and disabled = 0 ")
    public BigDecimal getRepaymentTotalAmount();

    /*累计复借笔数*/
    @Select("select userUuid,count(userUuid) as userCount from ordOrder where  status in(7,8,10,11) and disabled=0 and borrowingCount >= 1 group by userUuid ")
    public List<ManOrderSecondLoanSpec> secondLoanTotalCount();


    // Doit 新户免审核用户d1至d60逾期率
    @Select("SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "if(到期星期 is null , '合计' ,到期星期 )   'p1',     -- '(免审核)到期星期',\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D1到期笔数 as D1ExpirationOrder,                                                -- D1到期笔数\n" +
            "D1逾期率 as D1OverdueRate,                                                        -- D1逾期率\n" +
            "D7到期笔数 as D7ExpirationOrder,                                                -- D7到期笔数\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D7逾期率 as D7OverdueRate,\t\t                                      -- D7逾期率\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D15到期笔数 as D15ExpirationOrder,\t                                      -- D15到期笔数\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D15逾期率 as D15OverdueRate,\t                                      -- D15逾期率\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D30到期笔数 as D30ExpirationOrder,\t                                      -- D30到期笔数\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D30逾期率 as D30OverdueRate,\t                                      -- D30逾期率\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D60到期笔数 as D60ExpirationOrder,\t                                      -- D60到期笔数\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D60逾期率 as D60OverdueRate\t\t                      -- D60逾期率\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "(SELECT\t\t\t\t\t\n" +
            "DATE_FORMAT(d.refundTime,'%x年-第%v周') as 到期星期,  -- %v 星期一是一周的第一天\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>0,1,0)) as D1到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>0,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>0,1,0))*100,2),'%') as D1逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>6,1,0)) as D7到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>6,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>6,1,0))*100,2),'%') as D7逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>14,1,0)) as D15到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>14,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>14,1,0))*100,2),'%') as D15逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>29,1,0)) as D30到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>29,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>29,1,0))*100,2),'%') as D30逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>59,1,0)) as D60到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>59,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>59,1,0))*100,2),'%') as D60逾期率\t\t\t\t\t\n" +
            "from doit.ordOrder d\t\t\t\t\t\n" +
            "where d.borrowingCount=1 \t\t\t\t\t\n" +
            "and d.STATUS in (7,8,10,11)\t\t\t\t\t\n" +
            "and d.disabled=0 \t\t\t\t\t\n" +
            "and DATE_SUB(CURDATE(), INTERVAL 70 DAY) <= date(d.refundTime)\t\t\t\t\t\n" +
            "and DATEDIFF(NOW(),d.refundTime)>0\t\t\t\t\t\n" +
            "and \t\t\t\t\t\n" +
            "not exists(\t\t\t\t\t\n" +
            "select 1 from doit.ordHistory s where s.disabled=0 and status in (3,4) and s.orderId = d.uuid -- 经过人工审核的订单\t\t\t\t\t\n" +
            ")\t\t\t\t\t\n" +
            "and exists (\t\t\t\t\t\n" +
            "select 1 from doit.ordHistory s where s.disabled=0 and status =5 and s.orderId = d.uuid\t\t\t\t\t\n" +
            ") \t\t\t\t\t\n" +
            "group by 1 with rollup) m\t\t\t\t\t\n" +
            "order by 1 asc\t\t\t\t\t\n" +
            ";")
    List<NewUserWithNotCollectionD1ToD60> getNewUserWithNotCollectionD1ToD60();


    // Doit 新户审核用户d1至d60逾期率
    @Select("SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "if(到期星期 is null , '合计' ,到期星期 )   'p1',    -- '(审核)到期星期',\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "D1到期笔数 as D1ExpirationOrder,                                           -- D1到期笔数\t\t\t\t\t\t\t\t\t\t\n" +
            "D1逾期率 as D1OverdueRate,                                                   -- D1逾期率\t\t\t\t\t\t\t\t\n" +
            "D7到期笔数 as D7ExpirationOrder,\t\t                 -- D7到期笔数\t\t\t\t\t\t\t\t\t\t\n" +
            "D7逾期率 as D7OverdueRate,\t\t\t\t -- D7逾期率\t\t\t\t\t\t\t\t\n" +
            "D15到期笔数 as D15ExpirationOrder,\t\t\t -- D15到期笔数\t\t\t\t\t\t\t\t\t\t\n" +
            "D15逾期率 as D15OverdueRate,\t\t\t -- D15逾期率\t\t\t\t\t\t\t\t\t\n" +
            "D30到期笔数 as D30ExpirationOrder,\t\t                 -- D30到期笔数\t\t\t\t\t\t\t\t\t\t\n" +
            "D30逾期率 as D30OverdueRate,\t\t\t -- D30逾期率\t\t\t\t\t\t\t\t\t\n" +
            "D60到期笔数 as D60ExpirationOrder,\t\t\t -- D60到期笔数\t\t\t\t\t\t\t\t\t\t\n" +
            "D60逾期率 as D60OverdueRate\t\t\t -- D60逾期率\t\t\t\t\t\t\t\t\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "(SELECT\t\t\t\t\t\n" +
            "DATE_FORMAT(d.refundTime,'%x年-第%v周') as 到期星期,  -- %v 星期一是一周的第一天\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>0,1,0)) as D1到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>0,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>0,1,0))*100,2),'%') as D1逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>6,1,0)) as D7到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>6,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>6,1,0))*100,2),'%') as D7逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>14,1,0)) as D15到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>14,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>14,1,0))*100,2),'%') as D15逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>29,1,0)) as D30到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>29,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>29,1,0))*100,2),'%') as D30逾期率,\t\t\t\t\t\n" +
            "SUM(IF(DATEDIFF(NOW(),d.refundTime)>59,1,0)) as D60到期笔数,\t\t\t\t\t\n" +
            "CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>59,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>59,1,0))*100,2),'%') as D60逾期率\t\t\t\t\t\n" +
            "from doit.ordOrder d\t\t\t\t\t\n" +
            "where d.borrowingCount=1 \t\t\t\t\t\n" +
            "and d.STATUS in (7,8,10,11)\t\t\t\t\t\n" +
            "and d.disabled=0 \t\t\t\t\t\n" +
            "and DATE_SUB(CURDATE(), INTERVAL 70 DAY) <= date(d.refundTime)\t\t\t\t\t\n" +
            "and DATEDIFF(NOW(),d.refundTime)>0\t\t\t\t\t\n" +
            "and \t\t\t\t\t\n" +
            "exists(\t\t\t\t\t\n" +
            "select 1 from doit.ordHistory s where s.disabled=0 and status in (3,4) and s.orderId = d.uuid\t\t\t\t\t\n" +
            ")\t\t\t\t\t\n" +
            "group by 1 with rollup) m\t\t\t\t\t\n" +
            "order by 1 asc\t\t\t\t\t\n" +
            ";")
    List<NewUserWithNotCollectionD1ToD60> getNewUserWithCollectionD1ToD60();


    // 每周新户自动化比例，即免审核订单数/机审通过数
    @Select("select \n" +
            "m.week,\n" +
            "          \n" +
            "m.autoCheckPassNum,   -- 机审通过数    \n" +
            "          \n" +
            "mm.noManCheckPassNum,   -- 免审核订单数\n" +
            "\n" +
            "          concat(format(mm.noManCheckPassNum/m.autoCheckPassNum*100,2) ,'%')as 'autoRate'\n" +
            "\n" +
            "from \n" +
            "\n" +
            "(SELECT date_format(createTime,'%x年-第%v周')as week,\n" +
            "\t\n" +
            "              count(distinct uuid) as autoCheckPassNum      \n" +
            "    \n" +
            "from doit.ordOrder         \n" +
            "    \n" +
            "where disabled=0\n" +
            " and borrowingCount=1\n" +
            " and status not in (1,2,12)\n" +
            " and datediff(now(),createTime)<49\n" +
            "    \n" +
            "group by 1 desc)m\n" +
            "\n" +
            "left join\n" +
            "\n" +
            "(select date_format(o.createTime,'%x年-第%v周')as week, \n" +
            "            count(distinct o.uuid) as noManCheckPassNum\n" +
            "\n" +
            "from  doit.ordOrder o \n" +
            "\n" +
            "join doit.ordHistory h on o.userUuid = h.userUuid\n" +
            "\n" +
            "where o.disabled = 0 and h.disabled = 0 and o.borrowingCount = 1 and  datediff(now(),o.createTime)<49\n" +
            "\n" +
            "and o.uuid in (select distinct orderId from doit.ordHistory h where h.disabled = 0 and h.status = 5 \n" +
            "                       \n" +
            "and not exists(select 1 from doit.ordHistory t where t.disabled = 0 and t.status = 3 and t.orderId = h.orderId))\n" +
            "\n" +
            "group by 1 desc)mm on m.week=mm.week;")
    List<WeekAutoReviewRate> getWeekAutoReviewRate();

    // 每周新户自动化比例，即免审核订单数/机审通过数
    @Select("select a.week, \n" +
            "    \n" +
            "          (a.600产品进入初审订单数+b.600产品免审核订单数) as proAutoCheckPassNumber, #600产品机审通过数 \n" +
            " \n" +
            "          b.600产品免审核订单数 as proNoManCheckNumber,\n" +
            "      \n" +
            "          concat(format(b.600产品免审核订单数/(a.600产品进入初审订单数+b.600产品免审核订单数)*100,2),'%') as proAutoRate #600产品免审核比例\n" +
            "\n" +
            "from \n" +
            "\n" +
            "(select \n" +
            " date_format(d.createTime,'%x年-第%v周')as week, \n" +
            "  \n" +
            "            count(distinct d.uuid) as '600产品进入初审订单数'\n" +
            "\n" +
            "  from  doit.ordOrder d \n" +
            "\n" +
            "  join doit.ordHistory h on d.userUuid = h.userUuid and d.uuid = h.orderId\n" +
            "\n" +
            "  where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 \n" +
            "and h.status =3 and d.amountApply = 1200000\n" +
            "  \n" +
            "group by 1 desc) a\n" +
            "\n" +
            "join\n" +
            "\n" +
            "(select date_format(d.createTime,'%x年-第%v周')as week,\n" +
            "     \n" +
            "       count(distinct d.uuid) as '600产品免审核订单数'\n" +
            "\n" +
            "            from  doit.ordOrder d \n" +
            "join doit.ordHistory h on d.userUuid = h.userUuid and d.uuid = h.orderId\n" +
            "\n" +
            "  where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 \n" +
            " \n" +
            "     and h.status = 5\n" +
            "   \n" +
            "     and d.uuid not in (select distinct orderId from doit.ordHistory  where disabled = 0 and status = 3) \n" +
            "  \n" +
            "     and d.amountApply = 1200000\n" +
            "    \n" +
            "     group by 1 desc) b on a.week = b.week\n" +
            "\n" +
            "group by 1 desc\n" +
            "\n" +
            "limit 7;    \n")
    List<WeekSixHundredProductNoReviewRate> getWeekSixHundredProductNoReviewRate();

    // 因非本人规则被拒的订单在进入初审的全部订单中的占比
    @Select("select  w.date,\n" +
            "     \n" +
            "           w.rejectNum,  -- 初审时被非本人规则拒绝的单子数\n" +
            "\t  \n" +
            "           ww.firstCheckNum,   -- 进入初审的订单数\n" +
            "       \n" +
            "           concat(format(w.rejectNum/ww.firstCheckNum*100,2),'%') as rate\n" +
            "\n" +
            "from\n" +
            "\n" +
            "(select date(createTime)as date, \n" +
            "            count(distinct uuid)as firstCheckNum\n" +
            "\n" +
            "from doit.ordHistory where disabled = 0 and status = 3 and datediff(now(),createTime) < 10\n" +
            "group by 1 desc) ww\n" +
            "\n" +
            "left join\n" +
            " \n" +
            "(select date(o.createTime)as date ,\n" +
            "            count(distinct o.orderId) as rejectNum\n" +
            "\n" +
            "  from doit.ordHistory o \n" +
            "\n" +
            "  join doit.manOrderCheckRule r on r.orderNo = o.orderId\n" +
            "\n" +
            "       where o.disabled = 0 and r.disabled = 0 and o.status = 13 and datediff(now(),o.createTime) < 10       \n" +
            "       and r.description regexp '视频非用户本人|手持照片人脸（非身份证）与活体人脸不一致|活体与身份证照不一致|活体与驾照或护照照片不一致|出身年份为偶数但身份证背景为红色|身份证姓名/号码等关键信息造假|申请人性别与视频中人不相符|用户填写的姓名和身份证姓名不同|用户填写的身份证号和身份证上的不同'\n" +
            "\n" +
            "       and r.ruleResult = 1\n" +
            "  \n" +
            "group by 1 desc) w  on w.date = ww.date; ")
    List<RefuseRateWithNotSelfRule> getRefuseRateWithNotSelfRule();

    @Update("update ordOrder o set o.userBankUuid = #{userBankId} where o.disabled=0 and o.uuid=#{orderNo}")
    Integer udpateOrderBankId(@Param("orderNo") String orderNo, @Param("userBankId") String userBankId);


    @Select("<script>" +
            "select * from ordOrder where disabled=0 and uuid in "
            + "<foreach collection='orderNos' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "</script>")
    List<OrdOrder> getOrderByOrderNos(@Param("orderNos") List<String> orderNos);

    @Select("select * from ordOrder where disabled=0 and uuid = #{orderNo}")
    OrdOrder getOrderByOrderNo(@Param("orderNo") String orderNo);

    // 各渠道每周机审通过率（取最近3周）
    @Select("select  date_format(o.createTime,'%x年-第%v周')as week,\n" +
            "\n" +
            "           case when u.userSource = 1 then '安卓'\n" +
            "            \n" +
            "                   when u.userSource = 2 then 'ios'\n" +
            "\n" +
            "                   when u.userSource = 21 then '贷超商21'\n" +
            "     \n" +
            "                   when u.userSource = 27 then '贷超商27'\n" +
            "        \n" +
            "                   when u.userSource = 28 then 'cashcash'\n" +
            "       \n" +
            "                   when u.userSource = 30 then '贷超商30'\n" +
            "     \n" +
            "                   when u.userSource = 31 then '贷超商31'\n" +
            "       \n" +
            "                   when u.userSource = 34 then '贷超商34'\n" +
            "     \n" +
            "                   else '其他贷超商' end as 'source',\n" +
            "        \n" +
            "            count(distinct o.uuid)as 'commitNum',    -- 提交订单数\n" +
            "     \n" +
            "            concat(format(sum(if(o.status not in (1,2,12),1,0))/sum(if(o.status not in (1),1,0))*100,2),'%') as 'autoCheckPassRate' -- 机审通过率\n" +
            "\n" +
            "from doit.ordOrder o \n" +
            "\n" +
            "join doit.usrUser u on o.userUuid = u.uuid\n" +
            "\n" +
            "where o.disabled = 0 and u.disabled = 0  \n" +
            "and o.borrowingCount = 1 \n" +
            "           and o.status not in (1) and datediff(curdate(),o.createTime)<21\n" +
            "\n" +
            "group by 1,2\n" +
            "\n" +
            "order by 1 desc,3 desc; ")
    List<SourceDayPassRate> getSourceDayPassRate();

    // 每周各渠道到期笔数，D1，D7，D15逾期率 （取最近5周）
    @Select("SELECT  date_format(o.refundTime,'%x年-第%v周')as expireWeek, -- 到期时间\n" +
            "    \n" +
            "              case when u.userSource = 1 then '安卓'\n" +
            "             \n" +
            "              when u.userSource = 2 then 'ios'\n" +
            "\n" +
            "              when u.userSource = 21 then '贷超商21'\n" +
            "    \n" +
            "              when u.userSource = 27 then '贷超商27'\n" +
            "      \n" +
            "              when u.userSource = 28 then 'cashcash'\n" +
            "    \n" +
            "              when u.userSource = 30 then '贷超商30'\n" +
            "       \n" +
            "              when u.userSource = 31 then '贷超商31'\n" +
            "            \n" +
            "              when u.userSource = 34 then '贷超商34'\n" +
            "    \n" +
            "              else '其他贷超商' end as 'source',  -- 渠道\n" +
            "\n" +
            "              COUNT(DISTINCT o.uuid) as 'expireNum',  -- 到期笔数\n" +
            "       \n" +
            "             CONCAT(FORMAT(SUM(IF(IF(o.STATUS=8,DATEDIFF(NOW(),o.refundTime),DATEDIFF(o.actualRefundTime,o.refundTime))>0,1,0))/COUNT(DISTINCT o.uuid)*100,1),'%') as D1overDueRate, -- 新户D1逾期率\n" +
            " \n" +
            "             CONCAT(FORMAT(SUM(IF(IF(o.STATUS=8,DATEDIFF(NOW(),o.refundTime),DATEDIFF(o.actualRefundTime,o.refundTime))>6,1,0))/COUNT(DISTINCT o.uuid)*100,1),'%') as D7overDueRate,\n" +
            "  \n" +
            "             CONCAT(FORMAT(SUM(IF(IF(o.STATUS=8,DATEDIFF(NOW(),o.refundTime),DATEDIFF(o.actualRefundTime,o.refundTime))>14,1,0))/COUNT(DISTINCT o.uuid)*100,1),'%') as D15overDueRate\n" +
            "\n" +
            "from doit.ordOrder o\n" +
            "\n" +
            "join doit.usrUser u on o.userUuid =u.Uuid\n" +
            "\n" +
            "where o.disabled = 0 and  u.disabled = 0 and o.borrowingCount = 1\n" +
            "and o.status in (7,8,10,11) \n" +
            "          and DATEDIFF(CURDATE(),o.refundTime)>14 and DATEDIFF(CURDATE(),o.refundTime)<49\n" +
            "\n" +
            "group by 1,2\n" +
            "order by 1 desc, 3 desc;")
    List<SouceInDateNum> getSouceInDateNum();

    // 聚信立税卡接口每天返回情况统计
    @Select("select a.*, \n" +
            "          b.total ,\n" +
            "         \n" +
            " concat(format(a.amount/b.total * 100,2),'%') as proportion\n" +
            "\n" +
            "from \n" +
            "\n" +
            "(SELECT \n" +
            " DATE(a.createTime)as date,\n" +
            " \n" +
            "              a.response, \n" +
            "\n" +
            "              COUNT(a.response) as amount\n" +
            "\n" +
            "               FROM\n" +
            "  (SELECT \n" +
            "   CASE  WHEN response NOT IN ('{\"name\":\"\"}' , '{\"name\":null}', '{}')and response not like '%\"name\":\"\"%'  and response not like '%\"name\":null%'  THEN '正常数据'\n" +
            "\n" +
            "\t                            ELSE response\n" +
            "   END as response,\n" +
            "  \n" +
            "                                            s.createTime\n" +
            " \n" +
            "                            FROM\n" +
            "  doit.sysThirdLogs s\n" +
            " \n" +
            "                             WHERE\n" +
            "   s.thirdType = 13\n" +
            "  and s.disabled = 0 \n" +
            "                            AND response IS NOT NULL\n" +
            "   AND response != ''\n" +
            "   and datediff(curdate(),createTime)<10) a     \n" +
            "      \n" +
            "GROUP BY 1, 2\n" +
            "     \n" +
            "ORDER BY 1 DESC) a \n" +
            "\n" +
            "join\n" +
            "\n" +
            "(select date(createTime) as date , \n" +
            "            count(1)as total \n" +
            "from\n" +
            "  doit.sysThirdLogs \n" +
            "\n" +
            " WHERE\n" +
            "   thirdType = 13 and disabled= 0\n" +
            "   AND response IS NOT NULL\n" +
            "   AND response != ''\n" +
            "   and datediff(curdate(),createTime)<10\n" +
            " group by 1) b on a.date = b.date\n" +
            "\n" +
            "order by 1 desc;\n")
    List<JXLDayResponse> getJXLDayResponse();

    // 每日advance验证返回具体结果
    @Select("select a.*,b.total, \n" +
            "  \n" +
            "          concat(format(a.amount/b.total * 100 ,2),'%') as proportion\n" +
            "\n" +
            "from\n" +
            "\n" +
            "(select date(u.createTime) as date, \n" +
            "\n" +
            "            u.response, \n" +
            "\n" +
            "            count(u.response) as 'amount'  #返回该结果的人数\n" +
            "\n" +
            "           from\n" +
            " \n" +
            "           (select case when response regexp '\"PERSON_NOT_FOUND\"' then '\"PERSON NOT FOUND\"'\n" +
            "\n" +
            "                               when response regexp '\"INVALID_ID_NUMBER\"' then '\"INVALID ID NUMBER\"'\n" +
            "    \n" +
            "                               when response regexp '\"ERROR\"' then '\"ERROR\"'\n" +
            " \n" +
            "                               when response regexp '\"INSUFFICIENT_BALANCE\"' then '\"INSUFFICIENT BALANCE\"'\n" +
            "          \n" +
            "                               when response regexp '\"EMPTY_PARAMETER_ERROR\"' then '\"EMPTY PARAMETER ERROR\"'\n" +
            "   \n" +
            "                               else 'advance成功' end as response, #avdance验证返回具体结果     \n" +
            "\t\t\n" +
            "                       createTime\n" +
            "\n" +
            "               from doit.sysThirdLogs \n" +
            "where thirdType = 2 and disabled= 0 and datediff(curdate(),createTime)<10\n" +
            " and length(response) != 0) u\n" +
            "\n" +
            "                group by 1,2\n" +
            "               \n" +
            "order by 1 desc, 3 desc) a\n" +
            "\n" +
            "join\n" +
            "\n" +
            "(select  date(createTime) as date, \n" +
            "             count(1) as total\n" +
            "from\n" +
            "doit.sysThirdLogs \n" +
            " \n" +
            "where thirdType = 2 and datediff(curdate(),createTime)<10\n" +
            " and length(response) != 0 and disabled = 0\n" +
            " \n" +
            "group by 1 desc) b on a.date = b.date\n" +
            "order by 1 desc; \n" +
            "\n ")
    List<JXLDayResponse> getAdvanceResponse();

    // 每天放款用户中 设备号，IMEI都为空的人数
    @Select("select date(o.lendingTime)as 'lending date',     -- 放款时间\n" +
            "   \n" +
            "           count(distinct o.userUuid)as 'nodeviceNum' --  设备号，IMEI都为空的放款人数\n" +
            "\n" +
            "from doit.ordOrder o\n" +
            "\n" +
            "join doit.ordDeviceInfo d  on o.uuid = d.orderNo and o.userUuid = d.userUuid\n" +
            "\n" +
            "where o.disabled = 0 and d.disabled = 0  and o.status in(6,7,8,10,11)   \n" +
            "\n" +
            "and length(d.deviceId) = 0 and length(d.IMEI) = 0\n" +
            "\n" +
            "group by 1 desc;")
    List<LoanUserWithNoDeiviceId> getLoanUserWithNoDeiviceId();

    // Doit 每日外呼数据
    @Select("select\n" +
            "usr.mobileNumberDES as mobileDes,  -- 加密手机号\n" +
            "date(refundTime) as refundDay,     --  到期时间\n" +
            "if(borrowingCount = 1 ,'新用户','老用户') as userType    -- 用户类型\n" +
            "from ordOrder ord left join usrUser usr on usr.uuid = ord.userUuid\n" +
            "where ord.disabled = 0\n" +
            "and ord.`status` = 7\n" +
            "and date(refundTime) between date_add(curdate(),interval 0 day) and date_add(curdate(),interval 3 day)\n" +
            "order by usertype,refundDay;\n")
    List<DayCallNumber> getDayCallNumber();

    // 提额用户d1至d60逾期率
    @Select("SELECT \n" +
            "    IF(beachId IS NULL, '合计', beachId) 'p1',\n" +
            "    D1到期笔数 AS D1ExpirationOrder,\n" +
            "    D1逾期率 AS D1OverdueRate,\n" +
            "    D7到期笔数 AS D7ExpirationOrder,\n" +
            "    D7逾期率 AS D7OverdueRate,\n" +
            "    D15到期笔数 AS D15ExpirationOrder,\n" +
            "    D15逾期率 AS D15OverdueRate,\n" +
            "    D30到期笔数 AS D1ExpirationOrder,\n" +
            "    D30逾期率 AS D30OverdueRate,\n" +
            "    D60到期笔数 AS D60ExpirationOrder,\n" +
            "    D60逾期率 AS D60OverdueRate\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        c.beachId,\n" +
            "            SUM(IF(DATEDIFF(NOW(), d.refundTime) > 0, 1, 0)) AS D1到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS = 8, DATEDIFF(NOW(), d.refundTime), DATEDIFF(d.actualRefundTime, d.refundTime)) > 0, 1, 0)) / SUM(IF(DATEDIFF(NOW(), d.refundTime) > 0, 1, 0)) * 100, 2), '%') AS D1逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(), d.refundTime) > 6, 1, 0)) AS D7到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS = 8, DATEDIFF(NOW(), d.refundTime), DATEDIFF(d.actualRefundTime, d.refundTime)) > 6, 1, 0)) / SUM(IF(DATEDIFF(NOW(), d.refundTime) > 6, 1, 0)) * 100, 2), '%') AS D7逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(), d.refundTime) > 14, 1, 0)) AS D15到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS = 8, DATEDIFF(NOW(), d.refundTime), DATEDIFF(d.actualRefundTime, d.refundTime)) > 14, 1, 0)) / SUM(IF(DATEDIFF(NOW(), d.refundTime) > 14, 1, 0)) * 100, 2), '%') AS D15逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(), d.refundTime) > 29, 1, 0)) AS D30到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS = 8, DATEDIFF(NOW(), d.refundTime), DATEDIFF(d.actualRefundTime, d.refundTime)) > 29, 1, 0)) / SUM(IF(DATEDIFF(NOW(), d.refundTime) > 29, 1, 0)) * 100, 2), '%') AS D30逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(), d.refundTime) > 59, 1, 0)) AS D60到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS = 8, DATEDIFF(NOW(), d.refundTime), DATEDIFF(d.actualRefundTime, d.refundTime)) > 59, 1, 0)) / SUM(IF(DATEDIFF(NOW(), d.refundTime) > 59, 1, 0)) * 100, 2), '%') AS D60逾期率\n" +
            "    FROM\n" +
            "        doit.ordOrder d\n" +
            "    JOIN usrProductTemp c ON d.userUuid = c.userUuid\n" +
            "    WHERE\n" +
            "        amountApply = 1500000\n" +
            "            AND d.STATUS IN (7 , 8, 10, 11)\n" +
            "            AND c.beachId NOT IN ('20181225_146' , '20181226_84', '20181227_90')\n" +
            "            AND d.disabled = 0\n" +
            "            AND DATEDIFF(NOW(), d.refundTime) > 0\n" +
            "    GROUP BY 1 WITH ROLLUP) m\n" +
            "ORDER BY 1 ASC\n" +
            ";")
    List<UpUserD1ToD7> getUpUserD1ToD7();

    // 提额至1000用户d1至d60逾期率
    @Select("SELECT\n" +
            "            if(beachId is null , '合计' ,beachId )   'p1', -- '提额批次',\n" +
            "            D1到期笔数 as D1ExpirationOrder, -- D1到期笔数\n" +
            "            D1逾期率 as D1OverdueRate, -- D1逾期率\n" +
            "            D7到期笔数 as D7ExpirationOrder, -- D7到期笔数\n" +
            "            D7逾期率 as D7OverdueRate, -- D7逾期率\n" +
            "            D15到期笔数 as D15ExpirationOrder, -- D15到期笔数\n" +
            "            D15逾期率 as D15OverdueRate, -- D15逾期率\n" +
            "            D30到期笔数 as D30ExpirationOrder, -- D30到期笔数\n" +
            "            D30逾期率 as D30OverdueRate, -- D30逾期率\n" +
            "            D60到期笔数 as D60ExpirationOrder, -- D60到期笔数\n" +
            "            D60逾期率 as D60OverdueRate -- D60逾期率\n" +
            "            from\n" +
            "            (SELECT c.beachId,\n" +
            "            SUM(IF(DATEDIFF(NOW(),d.refundTime)>0,1,0)) as D1到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>0,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>0,1,0))*100,2),'%') as D1逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(),d.refundTime)>6,1,0)) as D7到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>6,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>6,1,0))*100,2),'%') as D7逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(),d.refundTime)>14,1,0)) as D15到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>14,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>14,1,0))*100,2),'%') as D15逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(),d.refundTime)>29,1,0)) as D30到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>29,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>29,1,0))*100,2),'%') as D30逾期率,\n" +
            "            SUM(IF(DATEDIFF(NOW(),d.refundTime)>59,1,0)) as D60到期笔数,\n" +
            "            CONCAT(FORMAT(SUM(IF(IF(STATUS=8,DATEDIFF(NOW(),d.refundTime),DATEDIFF(d.actualRefundTime,d.refundTime))>59,1,0))/SUM(IF(DATEDIFF(NOW(),d.refundTime)>59,1,0))*100,2),'%') as D60逾期率\n" +
            "            from doit.ordOrder d\n" +
            "            JOIN usrProductTemp c on d.userUuid=c.userUuid\n" +
            "            where amountApply = 2000000\n" +
            "            and d.STATUS in (7,8,10,11)\n" +
            "            and c.beachId in('20181225_146','20181226_84','20181227_90') -- 提额1000的三批客户\n" +
            "            and d.disabled=0 \n" +
            "            and DATEDIFF(NOW(),d.refundTime)>0\n" +
            "            group by 1 with rollup) m\n" +
            "            order by 1 asc\n" +
            "            ;")
    List<UpUserD1ToD7> getUpUserD1ToD7With1000Product();

    // 提额用户的通过率
    @Select("SELECT \n" +
            "    p.beachId AS batchId,\n" +
            "    提额人数 AS batchNum,\n" +
            "    申请人数 AS applyingNum,\n" +
            "    申请提交人数 AS commitNum,\n" +
            "    通过人数 AS checkPassNum,\n" +
            "    放款人数 AS lendSuccessNum,\n" +
            "    CONCAT(FORMAT(申请人数 / 提额人数 * 100,\n" +
            "                2),\n" +
            "            '%') AS batchApplyingRate,\n" +
            "    CONCAT(FORMAT(申请提交人数 / 申请人数 * 100,\n" +
            "                2),\n" +
            "            '%') AS commitRate\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        u.beachId, COUNT(DISTINCT u.userUuid) AS 提额人数\n" +
            "    FROM\n" +
            "        doit.usrProductTemp u\n" +
            "    WHERE\n" +
            "        u.disabled = 0\n" +
            "    GROUP BY 1) p\n" +
            "        JOIN\n" +
            "    (SELECT \n" +
            "        beachId,\n" +
            "            COUNT(DISTINCT IF(status IN (1 , 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16), d.userUuid, NULL)) AS 申请人数,\n" +
            "            COUNT(DISTINCT IF(status IN (2 , 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16), d.userUuid, NULL)) AS 申请提交人数,\n" +
            "            COUNT(DISTINCT IF(status IN (7 , 8, 9, 10, 11), d.userUuid, NULL)) AS 放款人数,\n" +
            "            COUNT(DISTINCT IF(status IN (5 , 6, 7, 8, 9, 10, 11, 16), d.userUuid, NULL)) AS 通过人数\n" +
            "    FROM\n" +
            "        doit.ordOrder d\n" +
            "    JOIN (SELECT DISTINCT\n" +
            "        orderNo, userUuid, beachId\n" +
            "    FROM\n" +
            "        doit.usrProductTemp\n" +
            "    WHERE\n" +
            "        disabled = 0) u ON d.userUuid = u.userUuid\n" +
            "    WHERE\n" +
            "        d.disabled = 0 AND amountApply = 1500000\n" +
            "        and u.beachId not in ('20181225_146','20181226_84','20181227_90') -- 排除提额1000的三批客户\n" +
            "    GROUP BY 1\n" +
            "    ORDER BY 1 DESC) z ON p.beachId = z.beachId;")
    List<UpUserOverRate> getUpUserOverRate();


    // 提额用户的通过率
    @Select("SELECT \n" +
            "    p.beachId AS batchId,\n" +
            "    提额人数 AS batchNum,\n" +
            "    申请人数 AS applyingNum,\n" +
            "    申请提交人数 AS commitNum,\n" +
            "    通过人数 AS checkPassNum,\n" +
            "    放款人数 AS lendSuccessNum,\n" +
            "    CONCAT(FORMAT(申请人数 / 提额人数 * 100,\n" +
            "                2),\n" +
            "            '%') AS batchApplyingRate,\n" +
            "    CONCAT(FORMAT(申请提交人数 / 申请人数 * 100,\n" +
            "                2),\n" +
            "            '%') AS commitRate\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        u.beachId, COUNT(DISTINCT u.userUuid) AS 提额人数\n" +
            "    FROM\n" +
            "        doit.usrProductTemp u\n" +
            "    WHERE\n" +
            "        u.disabled = 0\n" +
            "    GROUP BY 1) p\n" +
            "        JOIN\n" +
            "    (SELECT \n" +
            "        beachId,\n" +
            "            COUNT(DISTINCT IF(status IN (1 , 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16), d.userUuid, NULL)) AS 申请人数,\n" +
            "            COUNT(DISTINCT IF(status IN (2 , 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16), d.userUuid, NULL)) AS 申请提交人数,\n" +
            "            COUNT(DISTINCT IF(status IN (7 , 8, 9, 10, 11), d.userUuid, NULL)) AS 放款人数,\n" +
            "            COUNT(DISTINCT IF(status IN (5 , 6, 7, 8, 9, 10, 11, 16), d.userUuid, NULL)) AS 通过人数\n" +
            "    FROM\n" +
            "        doit.ordOrder d\n" +
            "    JOIN (SELECT DISTINCT\n" +
            "        orderNo, userUuid, beachId\n" +
            "    FROM\n" +
            "        doit.usrProductTemp\n" +
            "    WHERE\n" +
            "        disabled = 0) u ON d.userUuid = u.userUuid\n" +
            "    WHERE\n" +
            "        d.disabled = 0 AND amountApply = 2000000\n" +
            "        and u.beachId in ('20181225_146','20181226_84','20181227_90') -- 提额1000的三批客户\n" +
            "    GROUP BY 1\n" +
            "    ORDER BY 1 DESC) z ON p.beachId = z.beachId;")
    List<UpUserOverRate> getUpUserOverRateWith1000Product();

    @Select("<script>"
            + "select distinct userUuid from ordOrder where uuid in "
            + "<foreach collection='orderNos' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>"
            + "</script>")
    List<String> getUserIdsByOrderNos(@Param("orderNos") List<String> orderNos);

    @Select("select * from ordOrder where status in(10,11) and uuid not in (select orderNo from ordRepayAmoutRecord where disabled = 0) and disabled = 0 and remark != '测试订单';")
    List<OrdOrder> getOrderWithNotRepayRecord();

    @Select("select m.*, \n" +
            "n.rejectedOrd, -- 被拒笔数\n" +
            " concat(format(rejectedOrd/hitOrd*100,2),'%') as fraudRuleRejectRate  -- 欺诈规则机审拒绝率 \n" +
            "from\n" +
            "(SELECT DATE(createTime) date,  -- 日期 \n" +
            "ruleDesc as fraudRule, -- 欺诈规则\n" +
            "count(DISTINCT orderNo) as hitOrd -- 命中人数\n" +
            "FROM ordRiskRecord_last  where ruleType=24 and ruleRealValue='true' and DATEDIFF(NOW(),createTime)<10\n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc )m\n" +
            "\n" +
            "JOIN\n" +
            "\n" +
            "(SELECT DATE(createTime) date, -- 日期\n" +
            "responseMessage fraudRule, -- 欺诈规则 \n" +
            "count(DISTINCT orderNo) rejectedOrd -- 被拒笔数\n" +
            "FROM ordBlack where DATEDIFF(NOW(),createTime)<10\n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc) n on m.date=n.date and m.fraudRule=n.fraudRule\n" +
            "\n" +
            "union all \n" +
            "\n" +
            "select m.*, \n" +
            "n.rejectedOrd, -- 被拒笔数\n" +
            " concat(format(rejectedOrd/hitOrd*100,2),'%') as fraudRuleRejectRate  -- 欺诈规则机审拒绝率 \n" +
            "from\n" +
            "(SELECT DATE(createTime) date, -- 日期\n" +
            "ruleRealValue as fraudRule, -- 欺诈规则\n" +
            "COUNT(DISTINCT orderNo) hitOrd -- 命中人数\n" +
            "FROM ordRiskRecord_last  where ruleRealValue='姓名税卡相同命中欺诈黑名单' and DATEDIFF(NOW(),createTime)<10\n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc )m\n" +
            "\n" +
            "JOIN\n" +
            "\n" +
            "(SELECT DATE(createTime) date, -- 日期\n" +
            "ruleRealValue as fraudRule, -- 欺诈规则\n" +
            "COUNT(DISTINCT orderNo) rejectedOrd -- 被拒人数\n" +
            "FROM ordBlack where ruleRealValue='姓名税卡相同命中欺诈黑名单' and DATEDIFF(NOW(),createTime)<10\n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc) n on m.date=n.date and m.fraudRule=n.fraudRule\n" +
            "ORDER BY 1 desc\n")
    List<FraudRule> getFraudRuleData();


    // 因为CIMB的问题打款失败的订单
    @Select("select * from ordOrder where uuid in (SELECT EXTERNAL_ID from uangPay.T_DISBURSEMENT WHERE CREATE_TIME > '2018-11-19 12:00:00' and DISBURSE_STATUS = 'PENDING') and disabled = 0")
    public List<OrdOrder> getFieldOrderWhithCIMB2();

    // 因为CIMB的问题打款失败的订单
    @Select("select * from ordOrder where  status = 16 and disabled = 0 and remark = '';")
    public List<OrdOrder> getFieldOrderWhithCIMB3();

    // 降额后有问题的订单
    @Select("select * from ordOrder where amountApply = '200000.00' and productUuid = '';")
    public List<OrdOrder> getOrderWithNoProductUuid();

    // 整点首逾监控(overall first pass monitoring)
    @Select("select \n" +
            "ifnull(dueDay,'Avg') as dueDay,  -- 到期日(due date)\n" +
            "sum(1) as orders,                -- 订单(pesanan)\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 9,1,0)))/sum(1)*100,1),'%') as H09,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 10,1,0)))/sum(1)*100,1),'%') as H10,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 11,1,0)))/sum(1)*100,1),'%') as H11,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 12,1,0)))/sum(1)*100,1),'%') as H12,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 13,1,0)))/sum(1)*100,1),'%') as H13,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 14,1,0)))/sum(1)*100,1),'%') as H14,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 15,1,0)))/sum(1)*100,1),'%') as H15,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 16,1,0)))/sum(1)*100,1),'%') as H16,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 17,1,0)))/sum(1)*100,1),'%') as H17,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 18,1,0)))/sum(1)*100,1),'%') as H18,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 19,1,0)))/sum(1)*100,1),'%') as H19,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 20,1,0)))/sum(1)*100,1),'%') as H20,\n" +
            "concat(round((sum(if(ordStatus = 'noback',1,0)) + sum(if(ordStatus = 'repay' and date(repayTime) = dueDay and date_format(repayTime,'%k') >= 21,1,0)))/sum(1)*100,1),'%') as H21\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "orderType,\n" +
            "case when orderType = 3 then billId when orderType in (0,1,2) then orderId else null end as orderId,\n" +
            "case when orderType = 3 then billStatus when orderType in (0,1,2) then ordStatus else null end as ordStatus,\n" +
            "case when orderType = 3 then billDueDay when orderType in (0,1,2) then ordDueDay else null end as dueDay,\n" +
            "case when orderType = 3 then billRepayTime when orderType in (0,1,2) then repayTime else null end as repayTime\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "date(refundtime) as ordDueDay,\n" +
            "date(lendingTime) as ordLendDay,\n" +
            "actualRefundTime as repayTime,\n" +
            "if(status = 10,'repay','noback') as ordStatus,\n" +
            "orderType\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,1,2,3)\n" +
            "and if(ordertype = 3,1,date(refundTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 0 day))\n" +
            ") ord  \n" +
            "left join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "billTerm,\n" +
            "date(refundTime) as billDueDay,\n" +
            "actualRefundTime as billRepayTime,\n" +
            "if(status in (3),'repay','noback') as billStatus\n" +
            "from ordBill\n" +
            "where disabled = 0\n" +
            "and status in (1,2,3,4)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 0 day)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") result \n" +
            "where orderId is not null\n" +
            "group by dueDay with rollup;")
    List<MonitoringData> getMonitoringData();

    // 内催D0分组催收情况
    @Select("select \n" +
            "if(staff = 'Total','',createDay) as Date,            -- 日期(tanggal)\n" +
            "Staff,                                               -- 催收员(collector)\n" +
            "Groups,                                              -- 组长(TeamLender)\n" +
            "taskOrders,                                          -- 分案数(jumlah kasus yg dibagi)\n" +
            "repayOrders,                                         -- 回收数(jumlah pengembalian)\n" +
            "Rate                                                 -- 回收率(persentase pengembalian)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "createDay,\n" +
            "ifnull(staff,'Total') as staff,\n" +
            "Groups,\n" +
            "count(1) as taskOrders,\n" +
            "ifnull(sum(repayOrder),0) as repayOrders,\n" +
            "concat(round(ifnull(sum(repayOrder),0)/count(1)*100,1),'%') as rate\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "dueDay,\n" +
            "outsourceId,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "case when datediff(actualRefundTime,refundTime) = 0 then 1 else 0 end as repayOrder\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11) \n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "repayOrder\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "case when datediff(actualRefundTime,refundTime) = 0 then 1 else 0 end as repayOrder\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (1,2,3,4)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,date(createtime) as createDay,outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where outsourceid not in (0)\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.sourceType = 0 \n" +
            "and datediff(curdate(),a.createTime) between 0 and 1\n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) = 0,\n" +
            "datediff(a.createtime,b.refundtime) = 0)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            "where datediff(createDay,dueDay) = 0\n" +
            ") mydata\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "id as staffId,\n" +
            "realname as staff,\n" +
            "parentId\n" +
            "from manUser\n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            "and realname not like '%qinwei%'\n" +
            "and realname not like '%qingwei%'\n" +
            "and realname not like '%QUIROS%'\n" +
            ") mu on mu.staffId = mydata.outsourceid\n" +
            "left join\n" +
            "(\n" +
            "select id,realName as Groups\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            ") par on par.id = mu.parentId\n" +
            "group by Groups,createDay,staff with rollup\n" +
            "having createDay is not null\n" +
            ") result\n" +
            "order by createDay desc,Groups,case when staff = 'total' then 0 else repayOrders/taskOrders + 1 end desc;")
    List<D0CollectionData> getD0CollectionData();

    // 内催D1-2分组催收情况
    @Select("select \n" +
            "if(staff = 'Total','',createDay) as Date,            -- 日期(tanggal)\n" +
            "Staff,                                               -- 催收员(collector)\n" +
            "Groups,                                              -- 组长(TeamLender)\n" +
            "taskOrders,                                          -- 分案数(jumlah kasus yg dibagi)\n" +
            "repayOrders,                                         -- 回收数(jumlah pengembalian)\n" +
            "Rate,                                                -- 回收率(persentase pengembalian)\n" +
            "todayRepay                                           -- 今日回(jumlah pengembalian hari ini)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "createDay,\n" +
            "ifnull(staff,'Total') as staff,\n" +
            "Groups,\n" +
            "count(1) as taskOrders,\n" +
            "ifnull(sum(repayOrder),0) as repayOrders,\n" +
            "concat(round(ifnull(sum(repayOrder),0)/count(1)*100,1),'%') as rate,\n" +
            "ifnull(sum(case when repayDay = curdate() then repayOrder else 0 end),0) as todayRepay\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "outsourceId,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 1 and 2 then 1 else 0 end as repayOrder\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11) \n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 1 and 2 then 1 else 0 end as repayOrder\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (1,2,3,4)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,date(createtime) as createDay,outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where outsourceid not in (0)\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.sourceType = 0 \n" +
            "and datediff(curdate(),a.createTime) between 0 and 2\n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) between 1 and 2,\n" +
            "datediff(a.createtime,b.refundtime) between 1 and 2)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            "where datediff(createDay,dueDay) between 1 and 2\n" +
            ") mydata\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "id as staffId,\n" +
            "realname as staff,\n" +
            "parentId\n" +
            "from manUser\n" +
            "where disabled = 0\n" +
            "and realName is not null\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            "and realname not like '%qinwei%'\n" +
            "and realname not like '%qingwei%'\n" +
            "and realname not like '%QUIROS%'\n" +
            ") mu on mu.staffId = mydata.outsourceid\n" +
            "left join\n" +
            "(\n" +
            "select id,realName as Groups\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            ") par on par.id = mu.parentId\n" +
            "group by Groups,createDay,staff with rollup\n" +
            "having createDay is not null\n" +
            ") result\n" +
            "order by createDay desc,Groups,case when staff = 'total' then 0 else repayOrders/taskOrders + 1 end desc;")
    List<D1AndD2CollectionData> getD1AndD2CollectionData();

    // 内催D3-7分组催收情况
    @Select("select \n" +
            "if(staff = 'Total','',createDay) as Date,            -- 日期(tanggal)\n" +
            "Staff,                                               -- 催收员(collector)\n" +
            "Groups,                                              -- 组长(TeamLender)\n" +
            "taskOrders,                                          -- 分案数(jumlah kasus yg dibagi)\n" +
            "repayOrders,                                         -- 回收数(jumlah pengembalian)\n" +
            "Rate,                                                -- 回收率(persentase pengembalian)\n" +
            "todayRepay                                           -- 今日回(jumlah pengembalian hari ini)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "createDay,\n" +
            "ifnull(staff,'Total') as staff,\n" +
            "Groups,\n" +
            "count(1) as taskOrders,\n" +
            "ifnull(sum(repayOrder),0) as repayOrders,\n" +
            "concat(round(ifnull(sum(repayOrder),0)/count(1)*100,1),'%') as rate,\n" +
            "ifnull(sum(case when repayDay = curdate() then repayOrder else 0 end),0) as todayRepay\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "outsourceId,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 3 and 7 then 1 else 0 end as repayOrder\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11) \n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 3 and 7 then 1 else 0 end as repayOrder\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (1,2,3,4)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,date(createtime) as createDay,outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where outsourceid not in (0)\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.sourceType = 0 \n" +
            "and datediff(curdate(),a.createTime) between 0 and 5\n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) between 3 and 7,\n" +
            "datediff(a.createtime,b.refundtime) between 3 and 7)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            "where datediff(createDay,dueDay) between 3 and 7\n" +
            ") mydata\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "id as staffId,\n" +
            "realname as staff,\n" +
            "parentId\n" +
            "from manUser\n" +
            "where disabled = 0\n" +
            "and realName is not null\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            "and realname not like '%qinwei%'\n" +
            "and realname not like '%qingwei%'\n" +
            "and realname not like '%QUIROS%'\n" +
            ") mu on mu.staffId = mydata.outsourceid\n" +
            "left join\n" +
            "(\n" +
            "select id,realName as Groups\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            ") par on par.id = mu.parentId\n" +
            "group by Groups,createDay,staff with rollup\n" +
            "having createDay is not null\n" +
            ") result\n" +
            "order by createDay desc,Groups,case when staff = 'total' then 0 else repayOrders/taskOrders + 1 end desc;")
    List<D1AndD2CollectionData> getD3ToD7CollectionData();

    // 本月D8-30分组催收情况(kondisi collection D8-30 bulan ini)
    @Select("select            \n" +
            "Staff,                                               -- 催收员(collector)\n" +
            "Groups,                                              -- 组长(TeamLender)\n" +
            "taskToday,                                           -- 今日分案(jumlah kasus yg dibagi hari ini)\n" +
            "taskOrders,                                          -- 分案数(jumlah kasus yg dibagi)\n" +
            "repayOrders,                                         -- 回收数(jumlah pengembalian)\n" +
            "Rate,                                                -- 回收率(persentase pengembalian)\n" +
            "todayRepay                                           -- 今日回(jumlah pengembalian hari ini)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "ifnull(staff,' ') as staff,\n" +
            "Groups,\n" +
            "ifnull(sum(case when createDay = curdate() then 1 else 0 end),0) as taskToday,\n" +
            "count(1) as taskOrders,\n" +
            "ifnull(sum(repayOrder),0) as repayOrders,\n" +
            "concat(round(ifnull(sum(repayOrder),0)/count(1)*100,1),'%') as rate,\n" +
            "ifnull(sum(case when repayDay = curdate() then repayOrder else 0 end),0) as todayRepay\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "outsourceId,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 8 and 30 then 1 else 0 end as repayOrder\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11) \n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 8 and 30 then 1 else 0 end as repayOrder\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (1,2,3,4)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,date(createtime) as createDay,outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where outsourceid not in (0)\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.sourceType = 0 \n" +
            "and date_format(curdate(),'%Y%m') = date_format(a.createTime,'%Y%m')\n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) between 8 and 29,\n" +
            "datediff(a.createtime,b.refundtime) between 8 and 30)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            "where datediff(createDay,dueDay) between 8 and 30\n" +
            ") mydata\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "id as staffId,\n" +
            "realname as staff,\n" +
            "parentId\n" +
            "from manUser\n" +
            "where disabled = 0\n" +
            "and realName is not null\n" +
            "and realName not in ('cuishouheimingdan','liquanxia')\n" +
            ") mu on mu.staffId = mydata.outsourceid\n" +
            "left join\n" +
            "(\n" +
            "select id,realName as Groups\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            ") par on par.id = mu.parentId\n" +
            "group by Groups,staff with rollup\n" +
            ") result\n" +
            "order by Groups,case when staff = ' ' then 0 else repayOrders/taskOrders + 1 end desc;")
    List<D8ToD30CollectionDataThisMouth> getD8ToD30CollectionDataThisMouth();


    // 3.2 上月D8-30分组催收情况(kondisi collection tim D8-30 bulan lalu)
    @Select("select            \n" +
            "Staff,                                               -- 催收员(collector)\n" +
            "Groups,                                              -- 组长(TeamLender)\n" +
            "taskToday,                                           -- 今日分案(jumlah kasus yg dibagi hari ini)\n" +
            "taskOrders,                                          -- 分案数(jumlah kasus yg dibagi)\n" +
            "repayOrders,                                         -- 回收数(jumlah pengembalian)\n" +
            "Rate,                                                -- 回收率(persentase pengembalian)\n" +
            "todayRepay                                           -- 今日回(jumlah pengembalian hari ini)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "ifnull(staff,' ') as staff,\n" +
            "Groups,\n" +
            "ifnull(sum(case when createDay = curdate() then 1 else 0 end),0) as taskToday,\n" +
            "count(1) as taskOrders,\n" +
            "ifnull(sum(repayOrder),0) as repayOrders,\n" +
            "concat(round(ifnull(sum(repayOrder),0)/count(1)*100,1),'%') as rate,\n" +
            "ifnull(sum(case when repayDay = curdate() then repayOrder else 0 end),0) as todayRepay\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "outsourceId,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 8 and 30 then 1 else 0 end as repayOrder\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11) \n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "repayDay,\n" +
            "repayOrder\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "date(actualRefundTime) as repayDay,\n" +
            "case when datediff(actualRefundTime,refundTime) between 8 and 30 then 1 else 0 end as repayOrder\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (1,2,3,4)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,date(createtime) as createDay,outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where outsourceid not in (0)\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.sourceType = 0 \n" +
            "and date_format(date_sub(curdate(),interval 1 month),'%Y%m') = date_format(a.createTime,'%Y%m')\n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) between 8 and 29,\n" +
            "datediff(a.createtime,b.refundtime) between 8 and 30)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            "where datediff(createDay,dueDay) between 8 and 30\n" +
            ") mydata\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "id as staffId,\n" +
            "realname as staff,\n" +
            "parentId\n" +
            "from manUser\n" +
            "where disabled = 0\n" +
            "and realName is not null\n" +
            "and realName not in ('cuishouheimingdan','liquanxia')\n" +
            ") mu on mu.staffId = mydata.outsourceid\n" +
            "left join\n" +
            "(\n" +
            "select id,realName as Groups\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            ") par on par.id = mu.parentId\n" +
            "group by Groups,staff with rollup\n" +
            ") result\n" +
            "order by Groups,case when staff = ' ' then 0 else repayOrders/taskOrders + 1 end desc;")
    List<D8ToD30CollectionDataLastMouth> getD8ToD30CollectionDataLastMouth();

    // 本月D31-D60分组催收情况(reminder kondisi collection team D31-D60)
    @Select("select            \n" +
            "Staff,                                               -- 催收员(collector)\n" +
            "Groups,                                              -- 组长(TeamLender)\n" +
            "taskToday,                                           -- 今日分案(jumlah kasus yg dibagi hari ini)\n" +
            "taskOrders,                                          -- 分案数(jumlah kasus yg dibagi)\n" +
            "repayOrders,                                         -- 回收数(jumlah pengembalian)\n" +
            "Rate,                                                -- 回收率(persentase pengembalian)\n" +
            "todayRepay                                           -- 今日回(jumlah pengembalian hari ini)\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "ifnull(staff,' ') as staff,\n" +
            "Groups,\n" +
            "ifnull(sum(case when createDay = curdate() then 1 else 0 end),0) as taskToday,\n" +
            "sum(taskOrder) as taskOrders,\n" +
            "ifnull(sum(repayOrder),0) as repayOrders,\n" +
            "concat(round(ifnull(sum(repayOrder),0)/sum(taskOrder)*100,1),'%') as rate,\n" +
            "ifnull(sum(case when repayDay = curdate() then repayOrder else 0 end),0) as todayRepay\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "dueDay,\n" +
            "applyAmount,\n" +
            "1 as taskOrder,\n" +
            "null as repayDay,\n" +
            "0 as repayOrder\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "applyAmount\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "amountApply as applyAmount\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11) \n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "applyAmount\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "billAmout as applyAmount\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (1,2,3,4)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "date(createtime) as createDay,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            "and id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid \n" +
            "where a.disabled = 0 and sourceType = 0 \n" +
            "and date_format(a.createtime,'%Y%m') = date_format(curdate(),'%Y%m')\n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) between 31 and 90,\n" +
            "datediff(a.createtime,b.refundtime) between 31 and 90)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select\n" +
            "billId,\n" +
            "orderId,\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "dueDay,\n" +
            "applyAmount,\n" +
            "0 as taskOrder,\n" +
            "repayDay,\n" +
            "1 as repayOrder\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "orderId as billId,\n" +
            "orderId,\n" +
            "dueDay,\n" +
            "applyAmount,\n" +
            "repayDay\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType,\n" +
            "date(refundtime) as dueDay,\n" +
            "amountApply as applyAmount,\n" +
            "date(actualRefundTime) as repayDay\n" +
            "from ordOrder \n" +
            "where disabled = 0 \n" +
            "and status in (10,11) \n" +
            "and datediff(actualRefundTime,refundTime) between 31 and 90\n" +
            "and date_format(actualRefundTime,'%Y%m') = date_format(curdate(),'%Y%m')\n" +
            "and orderType in (0,1,2)\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select \n" +
            "billId,\n" +
            "orderId,\n" +
            "billDueDay,\n" +
            "applyAmount,\n" +
            "repayDay\n" +
            "from  \n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "orderType\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status in (7,8,10,11) and orderType in (3)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "date(refundTime) as billDueDay,\n" +
            "billAmout as applyAmount,\n" +
            "date(actualRefundTime) as repayDay\n" +
            "from ordBill\n" +
            "where disabled = 0 and status in (3,4)\n" +
            "and datediff(actualRefundTime,refundTime) between 31 and 90\n" +
            "and date_format(actualRefundTime,'%Y%m') = date_format(curdate(),'%Y%m')\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") body\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "date(createtime) as createDay,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            "and id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a \n" +
            "inner join ordOrder b on a.orderUUID = b.uuid \n" +
            "left join ordBill c on c.orderNo = b.uuid \n" +
            "where a.disabled = 0 and sourceType = 0 \n" +
            "and if(orderType = 3,datediff(a.createtime,c.refundtime) between 31 and 90,\n" +
            "datediff(a.createtime,b.refundtime) between 31 and 90)\n" +
            "group by orderUUID\n" +
            ")\n" +
            ") third on third.orderUUID = body.orderId\n" +
            ") mydata\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "id as staffId,\n" +
            "realname as staff,\n" +
            "parentId\n" +
            "from manUser\n" +
            "where disabled = 0\n" +
            "and realName is not null\n" +
            "and realName not in ('cuishouheimingdan','liquanxia')\n" +
            ") mu on mu.staffId = mydata.outsourceid\n" +
            "left join\n" +
            "(\n" +
            "select id,realName as Groups\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            ") par on par.id = mu.parentId\n" +
            "where datediff(createDay,dueDay) between 31 and 90\n" +
            "group by Groups,staff with rollup\n" +
            ") result\n" +
            "order by Groups,\n" +
            "case \n" +
            "when staff = ' ' then 0 \n" +
            "when Rate is null then 1 \n" +
            "else repayOrders/taskOrders + 1 end desc;\n")
    List<D8ToD30CollectionDataLastMouth> getD31ToD60CollectionDataThisMouth();


    // 内催D31-D60分组催收情况
    @Select("select             \n" +
            "realName,                                            -- 催收员\n" +
            "taskToday,                                           -- 今日分案\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskToday) as taskToday,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "sum(case when date(createTime) = curdate() then 1 else 0 end) as taskToday,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(curdate()) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 31 and 60 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as todayNum,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and month(actualrefundTime) = month(curdate())\n" +
            "and datediff(actualrefundTime,refundTime) between 31 and 60\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 \n" +
            "and datediff(a.createTime,b.refundTime) between 31 and 60 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and status = 0\n" +
            "and realname not like '%qinwei%'\n" +
            "and realname not like '%qingwei%'\n" +
            "and realname not like '%QUIROS%'\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by realName with rollup\n" +
            ") result\n" +
            "order by case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc;")
    List<D31ToD60CollectionData> getD31ToD60CollectionData();

    // 勤为D1-2分组催收情况
    @Select("select \n" +
            "if(realName = 'Total','',createDay) as date,         -- 日期                \n" +
            "realName,                                            -- 催收员\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "createDay,\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and datediff(curdate(),a.createTime) between 0 and 2\n" +
            "and datediff(a.createTime,b.refundTime) between 1 and 2 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and datediff(actualrefundTime,refundTime) between 1 and 2\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and datediff(curdate(),a.createTime) between 0 and 2\n" +
            "and datediff(a.createTime,b.refundTime) between 1 and 2 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            "and (realname like '%qinwei%' or realname like '%qingwei%')\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by createDay,realName with rollup\n" +
            "having createDay is not null\n" +
            ") result\n" +
            "order by createDay desc,case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc;")
    List<QWD1AndD2CollectionData> getQWD1AndD2CollectionData();

    // 勤为D3-7分组催收情况
    @Select("select \n" +
            "if(realName = 'Total','',createDay) as date,         -- 日期                \n" +
            "realName,                                            -- 催收员\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "createDay,\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and datediff(curdate(),a.createTime) between 0 and 5\n" +
            "and datediff(a.createTime,b.refundTime) between 3 and 7 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and datediff(actualrefundTime,refundTime) between 3 and 7\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and datediff(curdate(),a.createTime) between 0 and 5\n" +
            "and datediff(a.createTime,b.refundTime) between 3 and 7 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and realName not in ('cuishouheimingdan')\n" +
            "and (realname like '%qinwei%' or realname like '%qingwei%')\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by createDay,realName with rollup\n" +
            "having createDay is not null\n" +
            ") result\n" +
            "order by createDay desc,case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc;")
    List<QWD1AndD2CollectionData> getQWD3ToD7CollectionData();

    // 本月勤为D8-30分组催收情况
    @Select("select             \n" +
            "realName,                                            -- 催收员\n" +
            "taskToday,                                           -- 今日分案\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskToday) as taskToday,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "sum(case when date(createTime) = curdate() then 1 else 0 end) as taskToday,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(curdate()) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as todayNum,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and datediff(actualrefundTime,refundTime) between 8 and 30\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(curdate()) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and status = 0\n" +
            "and (realname like '%qinwei%' or realname like '%qingwei%')\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by realName with rollup\n" +
            ") result\n" +
            "order by case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc,realName;")
    List<QWD8ToD30ThisMonthCollectionData> getQWD8ToD30ThisMonthCollectionData();

    // 上月勤为D8-30分组催收情况
    @Select("select             \n" +
            "realName,                                            -- 催收员\n" +
            "taskToday,                                           -- 今日分案\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskToday) as taskToday,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "sum(case when date(createTime) = curdate() then 1 else 0 end) as taskToday,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(date_sub(curdate(),interval day(curdate()) day)) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as todayNum,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and datediff(actualrefundTime,refundTime) between 8 and 30\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(date_sub(curdate(),interval day(curdate()) day)) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and status = 0\n" +
            "and (realname like '%qinwei%' or realname like '%qingwei%')\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by realName with rollup\n" +
            ") result\n" +
            "order by case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc,realName;")
    List<QWD8ToD30ThisMonthCollectionData> getQWD8ToD30LastMonthCollectionData();

    // 本月 QUIROS D8-30分组催收情况
    @Select("select             \n" +
            "realName,                                            -- 催收员\n" +
            "taskToday,                                           -- 今日分案\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskToday) as taskToday,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "sum(case when date(createTime) = curdate() then 1 else 0 end) as taskToday,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(curdate()) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as todayNum,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and datediff(actualrefundTime,refundTime) between 8 and 30\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(curdate()) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and status = 0\n" +
            "and realname like '%QUIROS%'\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by realName with rollup\n" +
            ") result\n" +
            "order by case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc,realName;\n")
    List<QWD8ToD30ThisMonthCollectionData> getQUIROSD8ToD30ThisMonthCollectionData();

    // 上月 QUIROS D8-30分组催收情况
    @Select("select             \n" +
            "realName,                                            -- 催收员\n" +
            "taskToday,                                           -- 今日分案\n" +
            "taskNum,                                             -- 分案数\n" +
            "recoveryNum,                                         -- 回收数\n" +
            "ratio,                                               -- 回收率\n" +
            "todayNum                                             -- 今日回\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "ifnull(realName,'Total') as realName,\n" +
            "sum(taskToday) as taskToday,\n" +
            "sum(taskNum) as taskNum,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,1),'%') as ratio,\n" +
            "sum(todayNum) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId,\n" +
            "sum(case when date(createTime) = curdate() then 1 else 0 end) as taskToday,\n" +
            "count(1) as taskNum,\n" +
            "0 as recoveryNum,\n" +
            "0 as todayNum\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(date_sub(curdate(),interval day(curdate()) day)) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            "group by createDay,outsourceId\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "outsourceId,\n" +
            "0 as todayNum,\n" +
            "0 as taskNum,\n" +
            "count(uuid) as recoveryNum,\n" +
            "sum(if(datediff(curdate(),actualRefundDay) = 0,1,0)) as todayNum\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(actualrefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundday\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status = 11 \n" +
            "and datediff(actualrefundTime,refundTime) between 8 and 30\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "orderuuId,\n" +
            "date(createTime) as createDay,\n" +
            "outsourceId\n" +
            "from collectionOrderHistory \n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) \n" +
            "from collectionOrderHistory a left join ordOrder b on a.orderuuId = b.uuid \n" +
            "where sourcetype = 0 and month(date_sub(curdate(),interval day(curdate()) day)) = month(a.createTime)\n" +
            "and datediff(a.createTime,b.refundTime) between 8 and 30 group by orderuuId\n" +
            ")\n" +
            ") third on third.orderuuId = ord.uuid\n" +
            "group by createDay,outsourceId\n" +
            ") t\n" +
            "inner join\n" +
            "(\n" +
            "select id,realName,parentID\n" +
            "from manUser \n" +
            "where disabled = 0\n" +
            "and status = 0\n" +
            "and realname like '%QUIROS%'\n" +
            ") man on t.outsourceId = man.id\n" +
            "group by realName with rollup\n" +
            ") result\n" +
            "order by case when realName = 'total' then 0 else recoveryNum/taskNum + 1 end desc,realName;")
    List<QWD8ToD30ThisMonthCollectionData> getQUIROSD8ToD30LastMonthCollectionData();


    // 每日放款金额_新老合计 （单位：万/RMB）
    @Select("select \n" +
            "x.放款日期 as LendDate,\n" +
            "format((x.新户金额+y.老户金额)/20000000,1) as totalAmount,#合计金额\n" +
            "format(x.新户金额/20000000,1) as newTotal, #新户合计\n" +
            "format(y.老户金额/20000000,1) as oldTotal, #老户合计\n" +
            "concat(format(y.老户金额/(x.新户金额+y.老户金额)*100,1),'%') as oldProportion ,#老户占比\n" +
            "concat(format(x.新户金额/(x.新户金额+y.老户金额)*100,1),'%') as newProportion #新户占比\n" +
            "from\n" +
            "(select date(lendingTime) as 放款日期, \n" +
            "sum(amountApply) as 新户金额\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount = 1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11) \n" +
            "group by 1 desc)x\n" +
            "join\n" +
            "(select date(lendingTime) as 放款日期, \n" +
            "sum(amountApply) as 老户金额\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount > 1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11) \n" +
            "group by 1 desc)y on x.放款日期 = y.放款日期\n" +
            "limit 15;")
    List<DayLoanAmout> getDayLoanAmout();

    // 每日放款笔数_新老合计
    @Select("select \n" +
            "x.放款日期 as LendDate,\n" +
            "x.新户放款笔数+y.老户放款笔数 as totalLendNum,#合计放款笔数\n" +
            "x.新户放款笔数 as newLendNum,\n" +
            "y.老户放款笔数 as oldLendNum,\n" +
            "concat(format(x.新户放款笔数/(x.新户放款笔数+y.老户放款笔数)*100,1),'%') as newProportion ,#新户占比\n" +
            "concat(format(y.老户放款笔数/(x.新户放款笔数+y.老户放款笔数)*100,1),'%') as oldProportion #老户占比\n" +
            "from\n" +
            "(select date(lendingTime) as 放款日期, \n" +
            "count(distinct uuid) as 新户放款笔数\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount = 1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11) \n" +
            "group by 1 desc)x\n" +
            "join\n" +
            "(select date(lendingTime) as 放款日期, \n" +
            "count(distinct uuid) as 老户放款笔数\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount > 1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11) \n" +
            "group by 1 desc)y on x.放款日期 = y.放款日期\n" +
            "limit 15;")
    List<DayLoanCount2> getDayLoanCount();

    // 各环节当天申请订单的当前通过笔数（新户600产品）
    @Select("select \n" +
            "a.申请日 as applyDate,\n" +
            "a.申请订单 as applyNum,\n" +
            "b.提交订单 as commmitNum,\n" +
            "e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0) as autoCheckPass,#机审通过\n" +
            "e.进入初审 as intoFC,\n" +
            "i.初审完成 as FCfinish,\n" +
            "l.初审通过本人外呼 as FCPassOutCall,\n" +
            "f.进入复审 as intoSC,\n" +
            "j.复审完成 as SCfinish,\n" +
            "k.复审通过风控通过 + ifnull(r.复审通过多头黑名单拒绝,0) as SCpass, # 复审通过\n" +
            "g.风控and黑名单通过+ ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0) as riskPass ,#风控通过\n" +
            "ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0) as BlackListMultiHeadNum, #多头黑名单拒绝,\n" +
            "g.风控and黑名单通过 as riskBlacklistPassNum,\n" +
            "x.待放款 as lendNum,\n" +
            "h.放款成功 as LendSuccessNum\n" +
            "from\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 申请订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0 # and d.thirdType != 1\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 提交订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 2 and d.borrowingCount = 1\n" +
            "and d.disabled = 0 # and d.thirdType != 1\n" +
            "group by 1 desc) b on a.申请日=b.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核风控通过\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType in (0,2)  \n" +
            "and h.disabled = 0 and orderType != 1\n" +
            "and h.status in (5,20) # and d.thirdType != 1\n" +
            "and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "and d.applyTime > '19-01-07 18:30:00' \n" +
            "group by 1 desc) c on a.申请日 = c.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入初审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 3\n" +
            "and d.applyTime > '19-01-07 18:30:00' # and d.thirdType != 1\n" +
            "group by 1 desc) e on a.申请日 = e.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审通过本人外呼\n" +
            " from doit.ordOrder d\n" +
            " join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 \n" +
            " and h.disabled = 0 \n" +
            "and h.status = 18 and d.applyTime > '19-01-07 18:30:00' # and d.thirdType != 1\n" +
            " group by 1 desc) l on a.申请日 = l.申请日\n" +
            " left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入复审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 4 \n" +
            "and d.applyTime > '19-01-07 18:30:00' # and d.thirdType != 1\n" +
            "group by 1 desc) f on a.申请日 = f.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过风控通过\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and d.applyTime > '19-01-07 18:30:00' and h.status = 4 \n" +
            "and h.orderId in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "and orderId in (select distinct orderId from doit.ordHistory where disabled = 0 and status in (5,20)) \n" +
            "and d.amountApply =1200000 and d.orderType != 1 # and d.thirdType != 1 \n" +
            "group by 1 desc)k on a.申请日 = k.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct h.orderId) as 风控and黑名单通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' \n" +
            "and h.status in (5,20) and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType != 1\n" +
            "and d.disabled = 0 # and d.thirdType != 1\n" +
            "group by 1 desc\n" +
            ") g on a.申请日 = g.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 5 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0  and orderType != 1 # and d.thirdType != 1\n" +
            "group by 1 desc) x on a.申请日 = x.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 7 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0 and orderType != 1 # and d.thirdType != 1\n" +
            "group by 1 desc) h on a.申请日 = h.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.applyTime > '19-01-07 18:30:00' \n" +
            "# and d.thirdType != 1 \n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'JUNIOR_REVIEWER')\n" +
            "group by 1 desc) i on a.申请日 = i.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1  and d.applyTime > '19-01-07 18:30:00' #and d.thirdType != 1\n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "group by 1 desc) j on a.申请日 = j.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "group by 1 desc) r on a.申请日 = r.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "group by 1 desc) s on a.申请日 = s.申请日\n" +
            "limit 15;")
    List<PassOrderCountWithNewUserAnd600Product> getPassOrderCountWithNewUserAnd600Product();

    // 各环节当天申请订单的当前通过率（新户600产品）
    @Select("select \n" +
            "a.申请日 as applyDate,\n" +
            "concat(format(b.提交订单/a.申请订单*100,1),'%') as commitRate,#600申请提交率\n" +
            "concat(format((e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as autoCheckPassRate,#机审通过率\n" +
            "concat(format(l.初审通过本人外呼/i.初审完成*100,1),'%') as manFCPassRate, #人工初审通过率\n" +
            "concat(format(f.进入复审/l.初审通过本人外呼*100,1),'%') as  selfCallPassRate,#本人外呼通过率\n" +
            "concat(format((k.复审通过风控通过+ ifnull(r.复审通过多头黑名单拒绝,0))/j.复审完成*100,1),'%') as SCPassRate, #复审通过率\n" +
            "concat(format((g.风控and黑名单通过+ ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as riskPassRate,#风控通过率\n" +
            "concat(format(g.风控and黑名单通过 /b.提交订单*100,1),'%') as riskandBlackListPassRate, #风控and黑名单通过率\n" +
            "concat(format((c.免审核风控通过+ifnull(s.免审核多头黑名单拒绝,0))/(e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0))*100,1),'%') as noMCRateInACpass,#免审核占机审通过比例\n" +
            "concat(format((c.免审核风控通过+ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as noMCrateInCommit, #免审核占提交比例\n" +
            "concat(format((ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))/(g.风控and黑名单通过+ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))*100,1),'%') as BlackListMuliHeadRate,#多头黑名单拒绝率\n" +
            "concat(format(h.放款成功 /x.待放款*100,1),'%') as lendSuccessRate #放款成功率\n" +
            "from\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 申请订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0 # and d.thirdType != 1\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 提交订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 2 and d.borrowingCount = 1\n" +
            "and d.disabled = 0 # and d.thirdType != 1\n" +
            "group by 1 desc) b on a.申请日=b.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核风控通过\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType in (0,2)  \n" +
            "and h.disabled = 0 and orderType != 1\n" +
            "and h.status in (5,20) # and d.thirdType != 1\n" +
            "and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "and d.applyTime > '19-01-07 18:30:00' \n" +
            "group by 1 desc) c on a.申请日 = c.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入初审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 3\n" +
            "and d.applyTime > '19-01-07 18:30:00' # and d.thirdType != 1\n" +
            "group by 1 desc) e on a.申请日 = e.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审通过本人外呼\n" +
            " from doit.ordOrder d\n" +
            " join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 \n" +
            " and h.disabled = 0 \n" +
            "and h.status = 18 and d.applyTime > '19-01-07 18:30:00' # and d.thirdType != 1\n" +
            " group by 1 desc) l on a.申请日 = l.申请日\n" +
            " left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入复审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 4 \n" +
            "and d.applyTime > '19-01-07 18:30:00' # and d.thirdType != 1\n" +
            "group by 1 desc) f on a.申请日 = f.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过风控通过\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and d.applyTime > '19-01-07 18:30:00' and h.status = 4 \n" +
            "and h.orderId in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "and orderId in (select distinct orderId from doit.ordHistory where disabled = 0 and status in (5,20)) \n" +
            "and d.amountApply =1200000 and d.orderType != 1 # and d.thirdType != 1 \n" +
            "group by 1 desc)k on a.申请日 = k.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct h.orderId) as 风控and黑名单通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' \n" +
            "and h.status in (5,20) and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType != 1\n" +
            "and d.disabled = 0 # and d.thirdType != 1\n" +
            "group by 1 desc\n" +
            ") g on a.申请日 = g.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 5 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0  and orderType != 1 # and d.thirdType != 1\n" +
            "group by 1 desc) x on a.申请日 = x.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 7 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0 and orderType != 1 # and d.thirdType != 1\n" +
            "group by 1 desc) h on a.申请日 = h.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.applyTime > '19-01-07 18:30:00' \n" +
            "# and d.thirdType != 1 \n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'JUNIOR_REVIEWER')\n" +
            "group by 1 desc) i on a.申请日 = i.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1  and d.applyTime > '19-01-07 18:30:00' #and d.thirdType != 1\n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "group by 1 desc) j on a.申请日 = j.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "group by 1 desc) r on a.申请日 = r.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "group by 1 desc) s on a.申请日 = s.申请日\n" +
            "limit 15;")
    List<PassOrderRateWithNewUserAnd600Product> getPassOrderRateWithNewUserAnd600Product();

    // 各环节当天申请订单的当前通过率（新户150产品）
    @Select("select \n" +
            "x.提交日 as commitDate,\n" +
            "x.600到150 as Product600To150,\n" +
            "x.600复审拒绝到150 Product600SCTo150,\n" +
            "x.进入150 as getIntoProduct150,\n" +
            "y.审核通过 as RiskPassNum,\n" +
            "y.用户确认 as CSCommitNum,\n" +
            "y.待放款 as LendNum,\n" +
            "y.放款成功 as LendSuccessNum, \n" +
            "concat(format(y.审核通过/x.进入150*100,1),'%') as riskPassRate, #审核通过率\n" +
            "concat(format(y.用户确认/y.审核通过*100,1),'%') as CSCommitRate, #用户确认比例\n" +
            "concat(format(y.放款成功/y.待放款*100,1),'%') as LendSuccessRate #放款成功率\n" +
            "from (select a.提交日, a.600到150, b.600复审拒绝到150, (a.600到150 + ifnull(b.600复审拒绝到150,0)) as 进入150\n" +
            "from \n" +
            "(select date(d.applyTime)as 提交日,\n" +
            " count(distinct b.orderNo) as 600到150 \n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where b.remark = 'PRODUCT600TO150' \n" +
            "and d.disabled = 0 and d.borrowingCount = 1\n" +
            "group by 1\n" +
            "order by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.applyTime)as 提交日,\n" +
            "count(distinct b.orderNo) as 600复审拒绝到150\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where b.remark = 'PRODUCT600TO150_SENIOR_REVIEW' \n" +
            "and d.disabled = 0 and d.borrowingCount = 1\n" +
            "group by 1\n" +
            "order by 1 desc) b on a.提交日 = b.提交日 \n" +
            ")x\n" +
            "join\n" +
            "(select date(d.applyTime)as 提交日,\n" +
            "sum(if(d.status not in (1,2,12,17),1,0)) as 审核通过,\n" +
            "sum(if(d.status in (5,6,7,8,9,10,11,16,20),1,0)) as 用户确认,\n" +
            "sum(if(d.status in (5,6,7,8,9,10,11,16),1,0)) as 待放款,\n" +
            "sum(if(d.status in (7,8,9,10,11),1,0)) as 放款成功\n" +
            "from doit.ordOrder d \n" +
            "where d.borrowingCount = 1 and d.amountApply = 300000 and disabled = 0  and orderType in (0,2)\n" +
            "and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT600TO150','PRODUCT600TO150_SENIOR_REVIEW'))\n" +
            "group by 1 \n" +
            "order by 1 desc) y on x.提交日 = y.提交日\n" +
            "limit 15;")
    List<PassOrderRateWithNewUserAnd150Product> getPassOrderRateWithNewUserAnd150Product();

    // 各环节当天申请订单的当前通过率（新户80产品）
    @Select("select \n" +
            "  x.提交日 as commitDate,\n" +
            "  x.150到80 as getInProduct80,\n" +
            "  y.审核通过 as RiskPassNum,\n" +
            "  y.用户确认 as CScommitNum, \n" +
            "  y.待放款 as LendNum,\n" +
            "  y.放款成功 as LendSuccessNum, \n" +
            "concat(format(y.审核通过/x.150到80*100,1),'%') as  autoCheckPassRate, #机审通过率\n" +
            "concat(format(y.用户确认/y.审核通过*100,1),'%') as  cstCommitRate,#用户确认比例\n" +
            "concat(format(y.放款成功/y.待放款*100,1),'%') as  LendSucRate #放款成功率 \n" +
            "from \n" +
            "(select \n" +
            "date(d.applyTime)as 提交日, \n" +
            "count(distinct b.orderNo) as 150到80\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where b.remark = 'PRODUCT150TO80' \n" +
            "and d.disabled = 0 and d.borrowingCount = 1\n" +
            "group by 1 desc) x\n" +
            "join\n" +
            "(select date(d.applyTime)as 提交日,\n" +
            "        sum(if(d.status not in (1,2,12,17),1,0)) as 审核通过,\n" +
            "        sum(if(d.status in (5,6,7,8,9,10,11,16,20),1,0)) as 用户确认,\n" +
            "        sum(if(d.status in (5,6,7,8,9,10,11,16),1,0)) as 待放款,\n" +
            "\t\tsum(if(d.status in (7,8,9,10,11),1,0)) as 放款成功\n" +
            "from doit.ordOrder d \n" +
            "where d.borrowingCount = 1 and d.amountApply = 160000 and disabled = 0  and orderType in (0,2)\n" +
            "and uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT150TO80'))\n" +
            "group by 1 desc) y on x.提交日 = y.提交日\n" +
            "limit 15; ")
    List<PassOrderRateWithNewUserAnd80Product> getPassOrderRateWithNewUserAnd80Product();


    // 各环节当天申请订单的当前通过率（老户）
    @Select("SELECT \n" +
            "date(createTime)as applyDate, #申请日\n" +
            "  sum(if(status >=1,1,0))as OldApplyNum,#老户申请数\n" +
            "  sum(if(status >=2,1,0))as CommitNum,#提交数\n" +
            "  sum(if(status not in (1,2,12,17),1,0)) as RiskPassNum, # 审核通过\n" +
            "  sum(if(status in (5,7,8,9,10,11,16),1,0)) as LendNum,#待放款 \n" +
            "  sum(if(status in (7,8,9,10,11),1,0)) as LendSuccessNum,#放款成功\n" +
            "  CONCAT(format ( sum(if(status >=2,1,0))/sum(if(status >=1,1,0))*100,1),'%')as commitRate,#申请提交率  \n" +
            "  CONCAT(format ( sum(if(status not in (1,2,12,17),1,0))/sum(if(status >=2,1,0))*100,1),'%')as riskPassRate,#审核通过率\n" +
            "  CONCAT(format ( sum(if(status in (7,8,9,10,11),1,0))/sum(if(status in (5,6,7,8,9,10,11,16),1,0))*100,1),'%')as LendSuccessRate #放款成功率  \n" +
            "  FROM doit.ordOrder \n" +
            "  WHERE disabled = 0 AND borrowingCount>1 and orderType in (0,2)\n" +
            "  GROUP BY 1 desc\n" +
            "  limit 10;")
    List<PassOrderRateWithOldUser> getPassOrderRateWithOldUser();

    // 各环节当天累计订单数（新户600产品）
    @Select("select a.date ,\n" +
            "       a.申请 as applyNum,\n" +
            "       b.提交 as commitNum,\n" +
            "       c.待初审 + d.免审核 as AutoCheckPassNum, \n" +
            "       c.待初审 as waitforFC,       \n" +
            "       e.待复审 as waitforSC,\n" +
            "       f.待放款  as LendNum,\n" +
            "       g.放款成功 as LendSuccessNum,\n" +
            "       k.600拒绝没有进入其他产品 + l.600拒绝进入其他产品 as AutoCheckFailedNum, #机审不通过\n" +
            "       h.初审不通过 as FCFailedNum,\n" +
            "       i.复审不通过 + ifnull(j.600复审拒绝到150,0) as SCFailedNum #复审不通过\n" +
            "from \n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 申请\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0\n" +
            "group by 1 desc) a \n" +
            "join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 提交\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status = 2 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0\n" +
            "group by 1 desc) b on a.date = b.date\n" +
            "join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 待初审\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status = 3 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0\n" +
            "group by 1 desc) c on a.date = c.date\n" +
            "join\n" +
            "(select date(h.createTime)as date, count(distinct d.uuid) as 免审核\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and h.disabled = 0\n" +
            "and h.status = 5 and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "group by 1 desc) d on a.date = d.date\n" +
            "left join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 待复审\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status = 4 and d.borrowingCount = 1 and d.amountApply = 1200000\n" +
            "and d.disabled = 0\n" +
            "group by 1 desc) e on a.date = e.date\n" +
            "join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status = 5 \n" +
            "and d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000\n" +
            "group by 1 desc) f on a.date = f.date\n" +
            "join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and h.status = 7 \n" +
            "and d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000\n" +
            "group by 1 desc) g on a.date = g.date\n" +
            "left join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 初审不通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status in (13,15) and d.borrowingCount = 1 and  d.amountApply = 1200000\n" +
            "and d.disabled = 0\n" +
            "group by 1 desc) h on a.date = h.date\n" +
            "left join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 复审不通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and h.status = 14 and d.borrowingCount = 1 and d.amountApply = 1200000\n" +
            "and d.disabled = 0\n" +
            "group by 1 desc) i on a.date = i.date\n" +
            "left join\n" +
            "(select DATE(h.createTime) as date, count(distinct orderId) as 600复审拒绝到150\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0  and d.borrowingCount = 1 \n" +
            "and d.disabled = 0 \n" +
            "and h.orderId in (select distinct orderNo from doit.ordBlack where remark = 'PRODUCT600TO150_SENIOR_REVIEW')\n" +
            "group by 1 desc) j on a.date = j.date\n" +
            "join\n" +
            "(select date(b.createTime)as 拒绝日期, count(distinct d.uuid) as 600拒绝没有进入其他产品\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.uuid = b.orderNo and d.userUuid = b.userUuid \n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and  b.disabled = 0 and d.status = 12 and d.amountApply = 1200000\n" +
            "and b.orderNo not in (select distinct orderNo from doit.ordBlack where remark in ('PRODUCT600TO50_FRAUD','PRODUCT600TO150')) \n" +
            "and  b.createTime > '2019-01-07 18:30:00' \n" +
            "group by 1 desc) k on a.date = k.拒绝日期\n" +
            "left join \n" +
            "(select date(b.createTime)as 拒绝日期, count(distinct d.uuid) as 600拒绝进入其他产品\n" +
            "from doit.ordBlack b \n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where  \n" +
            "d.disabled = 0 and d.borrowingCount = 1 \n" +
            "and b.remark in ('PRODUCT600TO50_FRAUD','PRODUCT600TO150') and  b.createTime > '2019-01-07 18:30:00'\n" +
            "group by 1 desc) l on a.date = l.拒绝日期\n" +
            "order by 1 desc\n" +
            "limit 15;")
    List<PassOrderCountWithNewUserAnd600Product2> getPassOrderCountWithNewUserAnd600Product2();

    // 各环节当天累计订单数（新户100产品）
    @Select("select \n" +
            "x.拒绝日期 as date,\n" +
            "           x.进入150 as getIntoProduct150,\n" +
            "           y.审核通过 as autoCheckoutCallPassNum,\n" +
            "           z.已确认待放款 as cstCommitLendNum, \n" +
            "           w.放款成功 as  LendSucNum\n" +
            "from \n" +
            "(select a.拒绝日期 , \n" +
            "            a.600到150, \n" +
            "            b.600复审拒绝到150, \n" +
            "            (a.600到150 + ifnull(b.600复审拒绝到150,0)) as 进入150\n" +
            "from\n" +
            "(select date(b.createTime) as 拒绝日期,\n" +
            "\t    count(distinct b.orderNo) as 600到150\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where b.remark = 'PRODUCT600TO150' \n" +
            "and d.disabled = 0 and d.borrowingCount = 1\n" +
            "group by  1 desc) a\n" +
            "left join\n" +
            "(select date(b.createTime) as 拒绝日期, \n" +
            " count(distinct b.orderNo) as 600复审拒绝到150\n" +
            " from doit.ordBlack b\n" +
            " join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            " where b.remark = 'PRODUCT600TO150_SENIOR_REVIEW' \n" +
            " and d.disabled = 0 and d.borrowingCount = 1\n" +
            " group by 1\n" +
            " desc) b on a.拒绝日期 = b.拒绝日期) x\n" +
            "join\n" +
            "(select date(h.createTime) as date,\n" +
            " count(distinct d.uuid) as 审核通过\n" +
            " from doit.ordHistory h\n" +
            " join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            " where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 300000\n" +
            " and h.status = 19\n" +
            " and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT600TO150','PRODUCT600TO150_SENIOR_REVIEW'))\n" +
            " group by 1 desc) y on x.拒绝日期 = y.date\n" +
            "join\n" +
            "(select date(h.createTime) as date,\n" +
            "           count(distinct d.uuid) as 已确认待放款 \n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 300000\n" +
            "and h.status = 5 \n" +
            "and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT600TO150','PRODUCT600TO150_SENIOR_REVIEW'))\n" +
            "group by 1 desc) z on x.拒绝日期 = z.date\n" +
            "join\n" +
            "(select date(h.createTime) as date,count(distinct d.uuid)as 放款成功 \n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 300000\n" +
            "and h.status = 7\n" +
            "and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT600TO150','PRODUCT600TO150_SENIOR_REVIEW'))\n" +
            "group by 1 desc) w on x.拒绝日期 = w.date\n" +
            "order by 1 desc\n" +
            "limit 15;")
    List<PassOrderCountWithNewUserAnd150Product> getPassOrderCountWithNewUserAnd150Product();

    // 各环节当天累计订单数（新户50产品）
    @Select("select \n" +
            "x.拒绝日期 as date ,\n" +
            "           x.150到80 as getIntoProduct80, #150进入80\n" +
            "           y.审核通过 as autoCheckoutCallPassNum,\n" +
            "           z.已确认待放款 as cstCommitLendNum,\n" +
            "           w.放款成功 as LendSuccessNum \n" +
            "from \n" +
            "(select date(b.createTime) as 拒绝日期, count(distinct b.orderNo) as 150到80\n" +
            " from doit.ordBlack b\n" +
            " join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where b.remark = 'PRODUCT150TO80' \n" +
            "and d.disabled = 0 and d.borrowingCount = 1\n" +
            "group by 1 desc) x\n" +
            "left join\n" +
            "(select date(h.createTime) as date,count(distinct d.uuid) as 审核通过 \n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 160000\n" +
            "and h.status = 19\n" +
            "and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT150TO80'))\n" +
            "group by 1 desc) y on x.拒绝日期 = y.date\n" +
            "left join\n" +
            "(select date(h.createTime) as date,count(distinct d.uuid) as 已确认待放款\n" +
            " from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 160000\n" +
            "and h.status = 5\n" +
            "and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT150TO80'))\n" +
            "group by 1 desc) z on x.拒绝日期 = z.date\n" +
            "left join\n" +
            "(select date(h.createTime) as date,count(distinct d.uuid)as 放款成功 \n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where d.disabled = 0 and h.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 160000\n" +
            "and h.status = 7\n" +
            "and d.uuid in(select distinct orderNo from doit.ordBlack where remark in ('PRODUCT150TO80'))\n" +
            "group by 1 desc) w on x.拒绝日期 = w.date\n" +
            "order by 1 desc\n" +
            "limit 15;\n")
    List<PassOrderCountWithNewUserAnd80Product> getPassOrderCountWithNewUserAnd80Product();

    // 各环节当天累计订单数（老户）
    @Select("select \n" +
            "a.date,\n" +
            "a.老户申请数 as applyNum,\n" +
            "b.提交数 as commitNum,\n" +
            "e.待放款 as lendNum,\n" +
            "f.放款成功 as lendSuccessNum\n" +
            "from \n" +
            "(select date(h.createTime) as date , count(distinct h.orderId) as 老户申请数\n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where h.disabled = 0 and d.disabled = 0 and d.borrowingCount > 1 and h.status = 1\n" +
            "group by 1 desc )a\n" +
            "join\n" +
            "(select date(h.createTime) as date , count(distinct h.orderId) as 提交数\n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where h.disabled = 0 and d.disabled = 0 and d.borrowingCount > 1 and h.status = 2\n" +
            "group by 1 desc ) b on a.date = b.date\n" +
            "join\n" +
            "(select date(h.createTime) as date , count(distinct h.orderId) as 待放款\n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where h.disabled = 0 and d.disabled = 0 and d.borrowingCount > 1\n" +
            "and h.status = 5\n" +
            "group by 1 desc) e on a.date = e.date\n" +
            "join\n" +
            "(select date(h.createTime) as date , count(distinct h.orderId) as 放款成功\n" +
            "from doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid\n" +
            "where h.disabled = 0 and d.disabled = 0 and d.borrowingCount > 1\n" +
            "and h.status = 7\n" +
            "group by 1 desc) f on a.date = f.date\n" +
            "group by 1 desc\n" +
            "limit 15; \n" +
            "\n")
    List<OrderCountWithOldUser> getOrderCountWithOldUser();


    @Select("select * from ordOrder where  status = 16 and disabled = 0 and remark ='银行打款异常，手动重新打款';")
    public List<OrdOrder> getOrderWithLoanFaild();

    //通过逾期天数查询订单
    @Select("select * from ordOrder where disabled = 0 and status in (7,8) " +
            "and dateDiff(now(), refundTime) = #{days} order by createTime desc;")
    List<OrdOrder> listOrderByOverDueDays(@Param("days") Integer days);


    //invited
    @Select("select 1 from usrUser where uuid=#{userId} and isInvited=1")
    List<Integer> isInvited(@Param("userId") String userId);

    //Bulk update p2p mark status
    @Update("<script>"
        + "UPDATE ordOrder set markStatus=#{newStatus}, updateTime=now() WHERE markStatus=#{oldStatus} AND uuid in "
        + "<foreach collection='uuids' item='uuid' separator=',' open='(' close=')'>"
        + " #{uuid}"
        + "</foreach>"
        + " </script>")
    int bulkUpdateP2PMarkStatus(@Param("oldStatus") String oldStatus
        , @Param("newStatus") String newStatus
        , @Param("uuids") List<String> uuids);
}
