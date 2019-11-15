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


    //错误重跑数据
    @Select("\n" +
            "select * from teleCallResult t where t.disabled =1 and t.remark = '外呼超时20181030';")
    List<TeleCallResult> getErrorList();


    @Select("select * from teleCallResult s where  callState = 1 and disabled = 0 and callType != 1 and TIMESTAMPDIFF(MINUTE,createTime,now()) >=90" +
            " and exists (select 1 from ordOrder o where o.uuid = s.orderNo and o.status in (17,18) and o.disabled=0);")
    List<TeleCallResult> getReportTimeOutAutoCallList();

    //查询订单在17，18状态，但是没有外呼记录的单(主要可能是重跑任务异常，没有放到同一个事务不会回滚)
    @Select("\n" +
            "select *from ordOrder o where o.status =17 and disabled=0\n" +
            "and not exists(\n" +
            "    select 1 from teleCallResult t where t.orderNo = o.uuid and t.disabled =0\n" +
            "    )\n" +
            "and  exists(\n" +
            "    select 1 from teleCallResult t where t.orderNo = o.uuid and t.disabled =1\n" +
            "    );")
    List<OrdOrder>  getExceptionAutoCallList();


    @Select("\n" +
            "select * from ordOrder where disabled=0 and amountApply=100*2000 and borrowingCount=1\n" +
            "and date(lendingTime)>'20181101' and STATUS in (7,8,10,11) and uuid not in (\n" +
            "select DISTINCT orderNo from usrIziVerifyResult where disabled=0);")
    List<OrdOrder> getHistoryDataToRunIzi();








    @Update("update teleCallResult s set disabled=1,remark='auto resend perday' ,updateTime = now() " +
            "  where  callState = 1 and disabled = 0 and callType != 1 and TIMESTAMPDIFF(MINUTE,createTime,now()) >=90" +
            "  and exists (select 1 from ordOrder o where o.uuid = s.orderNo and o.status in (17,18) )")
    Integer disabledNeedReSendItems();

    @Select("select * from teleCallResult where callState = 1 and disabled = 0 and datediff(now(),createTime) < 5 and mod(id,5)=#{mode} order by " +
            "createTime desc;")
    List<TeleCallResult> getTeleCallResult(int mode);

    @Select("select * from ordOrder o where o.uuid in (\n" +
            "            select orderNo from ordBlack b where b.remark='外呼重跑20181216'\n" +
            "    );")
    List<OrdOrder> reRunOrders();

    @Select("select * from teleCallResult where orderNo=#{orderNo} and callType = 3 and callResultType in (2,3) and disabled=0")
    List<TeleCallResult> getErrorResultByOrderNo(@Param("orderNo") String orderNo);

    //有过重试的外呼号码，最早已经外呼的填天数
    @Select("select max(datediff(now(),createTime)) from teleCallResult t where t.orderNo = #{orderNo} and callType=#{callType} and " +
            "tellNumber=#{tellNumber}\n" +
            "  and t.createTime>=(select max(createTime) from ordHistory ss where ss.orderId = t.orderNo and ss.status = 2) " +
            " and exists(\n" +
            "    select 1 from teleCallResult tt where tt.orderNo = t.orderNo and tt.callType = t.callType and tt.tellNumber = t.tellNumber and tt.remark='auto resend perday'\n" +
            ");")
    Integer getEarliestCallDiffDays(@Param("orderNo") String orderNo, @Param("callType") Integer callType, @Param("tellNumber") String telNumber);

    @Select("select * from teleCallResult where orderNo=#{orderNo} and callType = #{callType} ")
    List<CallResult> getCallResultByOrderNoAndType(@Param("orderNo") String orderNo, @Param("callType") Integer callType);

    @Select("select *from teleCallResult where remark='auto resend perday-data-error-20190226'")
    List<TeleCallResult> getErrorList190219();

    @Select("select count(1) from teleCallResult where orderNo=#{orderNo} and tellNumber=#{telNumber} and disabled=1")
    Integer getRetryTimes(@Param("orderNo") String orderNo, @Param("telNumber") String telNumber);

    @Select("\n" +
            "select * from (select * from teleCallResult where orderNo = #{orderNo} and callType = #{callType} order by createTime desc) a limit 1;")
    TeleCallResult getLatestTelCallResultByType(@Param("orderNo") String  orderNo, @Param("callType") Integer type);




    @Select("select * from teleCallResult where callState = 1 and disabled = 0 and datediff(now(),createTime) < 5 and createTime>'2019-06-19 " +
            "09:50:00' and  createTime <'2019-06-19 14:00:00' order by createTime desc;")
    List<TeleCallResult> getTeleCallResultForHistory();


}
