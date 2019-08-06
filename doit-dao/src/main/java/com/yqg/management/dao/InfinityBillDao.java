package com.yqg.management.dao;

import com.yqg.common.third.InfinityApp;
import com.yqg.management.entity.InfinityBillEntity;
import com.yqg.management.entity.InfinityExtnumberEntity;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/*****
 * @Author super
 * Created at 2019/04/08
 * @Email lijianping@yishufu.com
 *
 ****/

@Mapper
public interface InfinityBillDao {


    @Insert("<script>"
            + " INSERT INTO qualityCheckingVoice("
            + "    uuid,extnumber,  destnumber,billsec,callNode,callType,createTime,updateTime  "
            + " values "
            + "   <foreach collection='billList' item='record' separator=',' >"
            + "     (#{record.uuid},#{record.extnumber},#{record.destnumber},#{record.billsec},#{record.callNode},#{record.callType},now(),now())"
            + "   </foreach>"
            + " </script>")
    int saveBillList(@Param("billList") List<InfinityBillEntity> infinityBill);

    @Insert(
            "insert into qualityCheckingVoice( " + // uuid,extnumber,  destnumber,status,createTime,updateTime) "
                    "uuid,orderNo,extNumber,destNumber,realName,applyAmount,applyDeadline,userName,callNode,callType,createTime,updateTime, userId"
                    + " ) values( " +
                    "#{uuid},#{orderNo},#{extnumber},#{destnumber},#{realName},#{applyAmount},#{applyDeadline},#{userName},#{callNode},#{callType},now(),now(),#{userid})")
    int saveBill(InfinityBillEntity entity);

    @Select("select * from qualityCheckingVoice where disabled=0")
    List<InfinityExtnumberEntity> getExtnumberList();

    @Select("select * from qualityCheckingVoice where uuid = #{uuid}")
    List<InfinityApp> infinityExtnumberById(@Param("uuid") String uuid);

    @Update("update qualityCheckingVoice set disabled=1 where uuid=#{uuid} and disabled=0")
    Integer disableExtnumberById(@Param("uuid") String uuid);


    @Update("update infinityExtnumber set status=#{status} ,updateTime = now() where extnumber=#{extnumber}")
    Integer updateStatusByExtnumber(@Param("extnumber") String extnumber, @Param("status") String status);


    @Select("\n" +
            "select count(1) from ordOrder o where amountApply = #{amount} and o.disabled=0 and o.status  in (5,6,7,8,10,11)")
    Integer totalIssuedByAmount(@Param("amount") BigDecimal amount);


}

