package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.system.entity.TeleVoiceMessageResult;
import com.yqg.system.entity.TwilioCallResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/6/24
 * time: 1:57 PM
 */
@Mapper
public interface TeleVoiceMessageResultDao extends BaseMapper<TeleVoiceMessageResult>{


    //查询所有需要外呼的订单
    @Select("SELECT\n" +
            "\tord.uuid as orderNo,\n" +
            "\tord.userUuid as userUuid,\n" +
            "\tusr.mobileNumberDES as tellNumber\n" +
            "FROM\n" +
            "\tdoit.ordOrder ord\n" +
            "\tJOIN doit.usrUser usr ON usr.uuid = ord.userUuid \n" +
            "WHERE\n" +
            "\tDATEDIFF( now( ), ord.refundTime ) = #{days} \n" +
            "\tAND ord.disabled = 0  and ord.remark!='no need voice call' \n" +
            "\tAND usr.disabled = 0 \n" +
            "\tAND ord.STATUS IN ( 7, 8 ) order by ord.refundTime")
    List<TeleVoiceMessageResult> listCallAllOrder(@Param("days") Integer days);

    //获得不需要发送的订单
    @Select("SELECT orderNo FROM teleVoiceMessageResult twilio WHERE twilio.disabled = 0 AND twilio.callPhase = #{callPhase} " +
            "AND twilio.callResult = 2 and twilio.createTime > CAST(CAST(SYSDATE()AS DATE)AS DATETIME);")
    List<String> listNoNeedOrderNos(@Param("callPhase") String callPhase);


    @Select("select * from teleVoiceMessageResult where callState = 1 and disabled = 0 and datediff(now(),createTime) < 2 and mod(id,3)=#{mode} order by " +
            "createTime desc;")
    List<TeleVoiceMessageResult> getTeleVoiceMessageResult(int mode);

}
