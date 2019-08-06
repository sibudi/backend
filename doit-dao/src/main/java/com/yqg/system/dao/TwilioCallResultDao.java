package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.TwilioCallResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by tonggen on 2018/10/16.
 */
@Mapper
public interface TwilioCallResultDao extends BaseMapper<TwilioCallResult> {


    //查询所有需要外呼的订单
    @Select("SELECT\n" +
            "\tord.uuid as orderNo,\n" +
            "\tord.userUuid as userUuid,\n" +
            "\tusr.mobileNumberDES as phoneNumber\n" +
            "FROM\n" +
            "\tdoit.ordOrder ord\n" +
            "\tJOIN doit.usrUser usr ON usr.uuid = ord.userUuid \n" +
            "WHERE\n" +
            "\tDATEDIFF( now( ), ord.refundTime ) = #{days} \n" +
            "\tAND ord.disabled = 0  and ord.remark!='不用外呼' \n" +
            "\tAND usr.disabled = 0 \n" +
            "\tAND ord.STATUS IN ( 7, 8 ) order by ord.refundTime")
    List<TwilioCallResult>  listCallAllOrder(@Param("days") Integer days);

    //沉默的用户
    @Select("SELECT\n" +
            "\t'2' as orderNo,\n" +
            "\ta.useruuid as userUuid,\n" +
            "\tmobileNumberDES as phoneNumber\n" +
            "FROM\n" +
            "\t( SELECT max( actualRefundTime ) AS actualDay, useruuid FROM ordOrder WHERE disabled = 0 AND STATUS IN ( 10, 11 ) GROUP BY useruuid ) a\n" +
            "\tLEFT JOIN ( SELECT useruuid FROM ordOrder WHERE disabled = 0 AND STATUS IN (2,5,6,7, 8 ) GROUP BY useruuid ) b ON b.useruuid = a.useruuid\n" +
            "\tLEFT JOIN ( SELECT userUuid FROM usrBlackList WHERE disabled = 0 GROUP BY userUuid ) c ON c.userUuid = a.useruuid\n" +
            "\tLEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tuseruuid,\n" +
            "\t\tmax( datediff( actualRefundTime, refundTime ) ) AS dayType \n" +
            "\tFROM\n" +
            "\t\tordOrder \n" +
            "\tWHERE\n" +
            "\t\tdisabled = 0 \n" +
            "\t\tAND STATUS IN ( 10, 11 ) \n" +
            "\tGROUP BY\n" +
            "\t\tuseruuid \n" +
            "\tHAVING\n" +
            "\t\tdayType > 9 \n" +
            "\t) d ON d.useruuid = a.useruuid\n" +
            "\tINNER JOIN ( SELECT uuid, realName, mobileNumberDES FROM usrUser WHERE disabled = 0 AND mobileNumberDES IS NOT NULL ) e ON e.uuid = a.useruuid \n" +
            "WHERE\n" +
            "\tb.userUuid IS NULL \n" +
            "\tAND c.userUuid IS NULL \n" +
            "\tAND d.useruuid IS NULL \n" +
            "\tAND timestampdiff( HOUR, actualDay, now( ) ) BETWEEN 24 * 1 \n" +
            "\tAND 24 * 7 \n" +
            "ORDER BY\n" +
            "\tactualDay ")
    List<TwilioCallResult> listSilentUser();

    //查询上午已经外呼并且没有接通的沉默用户
    @Select("SELECT\n" +
            "\torderNo,\n" +
            "\tuserUuid,\n" +
            "\tusr.mobileNumberDES AS phoneNumber \n" +
            "FROM\n" +
            "\tdoit.twilioCallResult result\n" +
            "\tJOIN usrUser usr ON usr.uuid = result.userUUId and usr.disabled = 0 \n" +
            "WHERE\n" +
            "\tDATE_FORMAT( result.createTime, '%y-%m-%d' ) = DATE_FORMAT( now( ), '%y-%m-%d' ) \n" +
            "\tAND result.orderNo = '2' \n" +
            "\tAND result.callResult != 'completed' \n" +
            "\tAND result.disabled = 0 ")
    List<TwilioCallResult> listSilentSomeUser();

    //提额未申请的用户
    @Select("SELECT\n" +
            "\t'1' AS orderNo,\n" +
            "\tmobilenumberDES AS phoneNumber,\n" +
            "\ta.userUuid AS userUUid \n" +
            "FROM\n" +
            "\t( SELECT userUuid, max( createTime ) AS createday FROM usrProductRecord WHERE disabled = 0 GROUP BY userUuid ) a\n" +
            "\tLEFT JOIN ( SELECT userUuid FROM ordOrder WHERE disabled = 0 AND amountApply IN ( 1500000 ) GROUP BY useruuid ) b ON b.userUuid = a.userUuid\n" +
            "\tLEFT JOIN ( SELECT uuid, mobilenumberDES FROM usrUser WHERE disabled = 0 ) c ON c.uuid = a.userUuid \n" +
            "WHERE\n" +
            "\tb.userUuid IS NULL \n" +
            "\tAND timestampdiff( HOUR, a.createday, now( ) ) > 24")
    List<TwilioCallResult> listUpgradeLimit();

    //提额未申请的未接通部分用户
    @Select("SELECT\n" +
            "\torderNo,\n" +
            "\tuserUuid,\n" +
            "\tusr.mobileNumberDES AS phoneNumber \n" +
            "FROM\n" +
            "\tdoit.twilioCallResult result\n" +
            "\tJOIN usrUser usr ON usr.uuid = result.userUUId and usr.disabled = 0 \n" +
            "WHERE\n" +
            "\tDATE_FORMAT( result.createTime, '%y-%m-%d' ) = DATE_FORMAT( now( ), '%y-%m-%d' ) \n" +
            "\tAND result.orderNo = '1' \n" +
            "\tAND result.callResult != 'completed' \n" +
            "\tAND result.disabled = 0 ")
    List<TwilioCallResult> listSomeUpgradeLimit();

    //提额未申请的未接通部分用户
    @Select("select ord.uuid as orderNo, usr.uuid as userUuid, usr.mobileNumberDES as phoneNumber from usrUser usr join ordOrder ord on ord.userUuid = usr.uuid and usr.disabled = 0 and usr.status = 1 and ord.disabled = 0 where ord.status = 19 and TIMESTAMPDIFF(HOUR,ord.updateTime, now()) > 12 and TIMESTAMPDIFF(month,ord.updateTime,now()) <1;")
    List<TwilioCallResult> listAllReduceLimit();

    //暂时查询需要处理的数据
    @Select("SELECT\n" +
            "\tord.uuid AS orderNo,\n" +
            "\tord.userUuid AS userUuid,\n" +
            "\tusr.mobileNumberDES AS phoneNumber \n" +
            "FROM\n" +
            "\tdoit.ordOrder ord\n" +
            "\tJOIN doit.usrUser usr ON usr.uuid = ord.userUuid \n" +
            "WHERE\n" +
            "\tDATEDIFF( now( ), ord.refundTime ) = #{days} \n" +
            "\tAND ord.disabled = 0 and ord.remark!='不用外呼' \n" +
            "\tAND usr.disabled = 0 \n" +
            "\tAND ord.STATUS IN ( 7, 8 ) \n" +
            " order by ord.refundTime")
    List<TwilioCallResult> listCallSomeOrder(@Param("days") Integer days, @Param("callPhase") String callPhase);

    //获得不需要发送的订单
    @Select("SELECT orderNo FROM doit.twilioCallResult twilio WHERE twilio.disabled = 0 AND twilio.callPhase = #{callPhase} " +
            "AND twilio.callResultType = 1 and twilio.createTime > CAST(CAST(SYSDATE()AS DATE)AS DATETIME);")
    List<String> listNoNeedOrderNos(@Param("callPhase") String callPhase);

    //查询需要更新的订单
    @Select("select * from doit.twilioCallResult where disabled = 0 and " +
            "(callState = 1 or callResult = 'ringing' or callResult = 'queued' or callResult = 'in-progress') " +
            "and datediff(now(), createTime) < 5 and mod(id,3) = #{num} order by createTime ")
    List<TwilioCallResult> listNeedUpdateStatus(@Param("num") Integer num);

//    @Update("update doit.twilioCallResult set batchNo = #{result} where phoneNumber = #{phone}")
//    Integer insertValue(@Param("phone") String phone, @Param("result") Integer result);
}
