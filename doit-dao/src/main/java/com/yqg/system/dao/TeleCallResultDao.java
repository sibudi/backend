package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by wanghuaizhou on 2018/8/28.
 */
@Mapper
public interface TeleCallResultDao extends BaseMapper<TeleCallResult> {


    //ahalim remark unused code
    //@Select("\n" +
    //        "select * from teleCallResult t where t.disabled =1 and t.remark = '外呼超时20181030';")
    //List<TeleCallResult> getErrorList();

    //ahalim: speedup for demo 90 min -> 5 min
    @Select("select * from doit.teleCallResult s " +
        " inner join doit.ordOrder o on o.uuid = s.orderNo " +
        " where o.status in (17,18) and o.disabled=0 and  s.callState = 1 and s.disabled = 0 and s.callType != 1 " +
        " and TIMESTAMPDIFF(MINUTE,s.createTime,now()) >=5")
    List<TeleCallResult> getStuckedTeleCallResults();

    //Query orders in the status of 17,18 but there is no enabled teleCallResult 
    //ex1: teleCallResult Disabled by disabledStuckedTeleCallResult() but transaction terminated before inserting new teleCallResult record
    //ex2: teleCallResult Disabled manually, and there is enabled teleCallResult
    @Select("select * from ordOrder o " +
        " inner join teleCallResult t1 on t1.orderNo = o.uuid and t1.disabled=1 " + //Have telecall result with status disabled
        " left  join teleCallResult t2 on t2.orderNo = o.uuid and t2.disabled=0 " + //Doesn't have telecall result with status enabled (t2.id is null)
        " where o.disabled=0 and o.status in (17,18) and now() > adddate(t1.createtime, interval 5 minute) " +  //5 minutes after disable, there is no enabled record
        " and t2.id is null;")
    List<OrdOrder>  getExceptionAutoCallList();

// ahalim remark unused code
//     @Select("\n" +
//             "select * from ordOrder where disabled=0 and amountApply=100*2000 and borrowingCount=1\n" +
//             "and date(lendingTime)>'20181101' and STATUS in (7,8,10,11) and uuid not in (\n" +
//             "select DISTINCT orderNo from usrIziVerifyResult where disabled=0);")
//     List<OrdOrder> getHistoryDataToRunIzi();


    @Update("update doit.teleCallResult s " +
        " inner join doit.ordOrder o on o.uuid = s.orderNo " +
        " set s.disabled=1,s.remark='auto resend perday' ,s.updateTime = now() " +
        " where o.status in (17,18) and o.disabled=0 and  s.callState = 1 and s.disabled = 0 and s.callType != 1 " +
        " and TIMESTAMPDIFF(MINUTE,s.createTime,now()) >=90;")
    Integer disabledStuckedTeleCallResult();

    @Select("select * from teleCallResult where callState = 1 and disabled = 0 " +
            " and createTime > subdate(curdate(), interval 5 day) and #{mode}=#{mode} " +
            " order by createTime desc;")
    List<TeleCallResult> getCallSendTeleCallResult(int mode);

// ahalim remark unused code
//     @Select("select * from ordOrder o where o.uuid in (\n" +
//             "            select orderNo from ordBlack b where b.remark='外呼重跑20181216'\n" +
//             "    );")
//     List<OrdOrder> reRunOrders();

// ahalim remark unused code
//     @Select("select * from teleCallResult where orderNo=#{orderNo} and callType = 3 and callResultType in (2,3) and disabled=0")
//     List<TeleCallResult> getErrorResultByOrderNo(@Param("orderNo") String orderNo);

    //The outgoing call number that has been retried, the earliest days filled out
    @Select("select max(datediff(now(),createTime)) from teleCallResult t where t.orderNo = #{orderNo} and callType=#{callType} and " +
            "tellNumber=#{tellNumber}\n" +
            "  and t.createTime>=(select max(createTime) from ordHistory ss where ss.orderId = t.orderNo and ss.status = 2) " +
            " and exists(\n" +
            "    select 1 from teleCallResult tt where tt.orderNo = t.orderNo and tt.callType = t.callType and tt.tellNumber = t.tellNumber and tt.remark='auto resend perday'\n" +
            ");")
    Integer getEarliestCallDiffDays(@Param("orderNo") String orderNo, @Param("callType") Integer callType, @Param("tellNumber") String telNumber);

    @Select("select * from teleCallResult where orderNo=#{orderNo} and callType = #{callType} ")
    List<CallResult> getCallResultByOrderNoAndType(@Param("orderNo") String orderNo, @Param("callType") Integer callType);

// ahalim remark unused method        
//     @Select("select *from teleCallResult where remark='auto resend perday-data-error-20190226'")
//     List<TeleCallResult> getErrorList190219();

// ahalim remark unused method
//     @Select("select count(1) from teleCallResult where orderNo=#{orderNo} and tellNumber=#{telNumber} and disabled=1")
//     Integer getRetryTimes(@Param("orderNo") String orderNo, @Param("telNumber") String telNumber);

    @Select("\n" +
            "select * from (select * from teleCallResult where orderNo = #{orderNo} and callType = #{callType} order by createTime desc) a limit 1;")
    TeleCallResult getLatestTelCallResultByType(@Param("orderNo") String  orderNo, @Param("callType") Integer type);

//     @Select("select * from teleCallResult where callState = 1 and disabled = 0 and datediff(now(),createTime) < 5 and createTime>'2019-06-19 " +
//             "09:50:00' and  createTime <'2019-06-19 14:00:00' order by createTime desc;")
//     List<TeleCallResult> getTeleCallResultForHistory();


}
