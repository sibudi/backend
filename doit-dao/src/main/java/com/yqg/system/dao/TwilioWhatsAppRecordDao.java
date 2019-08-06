package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.TwilioCallResult;
import com.yqg.system.entity.TwilioWhatsAppRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by tonggen on 2018/10/16.
 */
@Mapper
public interface TwilioWhatsAppRecordDao extends BaseMapper<TwilioWhatsAppRecord> {


    //查询部分需要wa的订单
    @Select("select ord.uuid as orderNo, ord.userUuid as userUuid , u.mobileNumberDES as phoneNumber from " +
            "doit.ordOrder ord join usrUser u on u.uuid = ord.userUuid and ord.disabled =0 and u.disabled =0  " +
            "where DATEDIFF( now( ), ord.refundTime ) = #{days} and ord.status in (7,8) order by ord.refundTime")
    List<TwilioCallResult> listNeedWhatsApp(@Param("days") Integer days);

    /**
     * 通过sid更新数据状态
     */
    @Update("update doit.twilioWhatsAppRecord set status = #{status} where sid = #{sid} and userPhone=#{to} and direction = 1")
    int updateWaStautsBySid (@Param("sid") String sid, @Param("status") String status, @Param("to") String to);

    /**
     * 通过sid获得 WA发送的消息
     * @param sid
     * @return
     */
    @Select("select id,uuid from doit.twilioWhatsAppRecord where sid = #{sid} and direction=1 and disabled = 0 ")
    List<TwilioWhatsAppRecord> listRecordBySid(@Param("sid") String sid);

    /**
     * 通过用户手机号，获得最近的batchNo
     * @param userPhone
     * @return
     */
    @Select("select * from doit.twilioWhatsAppRecord where userPhone=#{userPhone} and direction=1 and disabled = 0 " +
            "order by createTime desc limit 1")
    List<TwilioWhatsAppRecord> getBatchNoByPhone(@Param("userPhone") String userPhone);

    /**
     * 查询到所有需要更新的订单
     * @return
     */
    @Select("select * from doit.twilioWhatsAppRecord where dataCreateTime is null and sid != '' ")
    List<TwilioWhatsAppRecord> listNeedUpdateStatus();
}
