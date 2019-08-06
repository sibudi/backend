package com.yqg.risk.dao;

import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdBlackTemp;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;

import java.math.BigDecimal;
import java.util.List;

import com.yqg.system.entity.SysAutoReviewRule;
import org.apache.ibatis.annotations.*;

/*****
 * @Author zengxiangcai
 * Created at 2018/2/24
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Mapper
public interface RiskResultDao {


    @Insert("<script>"
        + " INSERT INTO ordBlack(userUuid,"
        + "    orderNo,ruleValue, "
        + "    ruleHitNo,ruleRealValue,"
        + "    responseMessage,ruleRejectDay,"
        + "    uuid,createTime,updateTime)  "
        + " values "
        + "   <foreach collection='blackList' item='record' separator=',' >"
        + "     (#{record.userUuid},"
        + "      #{record.orderNo},#{record.ruleValue},"
        + "      #{record.ruleHitNo},#{record.ruleRealValue},"
        + "      #{record.responseMessage},#{record.ruleRejectDay},"
        + "      #{record.uuid}, now(),now())"
        + "   </foreach>"
        + " </script>")
    int addBlackList(@Param("blackList") List<OrdBlack> ordBlacks);

    @Insert("<script>"
        + " INSERT INTO ordBlackTemp(userUuid,"
        + "    orderNo,ruleValue,"
        + "    ruleHitNo,ruleRealValue,"
        + "    responseMessage,uuid,"
        + "    createTime,updateTime)  "
        + " values "
        + "   <foreach collection='blackTempList' item='record' separator=',' >"
        + "     (#{record.userUuid},"
        + "      #{record.orderNo},#{record.ruleValue},"
        + "      #{record.ruleHitNo},#{record.ruleRealValue},"
        + "      #{record.responseMessage},#{record.uuid},"
        + "       now(),now())"
        + "   </foreach>"
        + "</script>")
    int addBlackTempList(@Param("blackTempList") List<OrdBlackTemp> ordBlackTemps);



    @Select("select * from sysAutoReviewRule u where u.ruleType = #{ruleType} and u.disabled=0")
    List<SysAutoReviewRule> getRulesWithType(@Param("ruleType") Integer ruleType);

    @Update("update ordBlack set remark=#{remark},disabled=1 where orderNo=#{orderNo} and disabled=0")
    Integer disableOrdBlackRecord(@Param("orderNo") String orderNo, @Param("remark") String remark);

    @Select("select * from ordBlack where orderNo = #{orderNo} and disabled=0")
    List<OrdBlack> getOrderBlackList(@Param("orderNo") String orderNo);

    @Select("select * from ordBlack where orderNo = #{orderNo}")
    List<OrdBlack> getOrderBlackListIgnoreDisabled(@Param("orderNo") String orderNo);

    @Update("update ordBlack set remark=#{remark},disabled=1 where id=#{id} and disabled=0")
    Integer disableOrdBlackRecordById(@Param("id") Integer id, @Param("remark") String remark);

    @Select("\n" +
            "select count(1) from ordOrder o where amountApply = #{amount} and o.disabled=0 and o.status  in (5,6,7,8,10,11)")
    Integer totalIssuedByAmount(@Param("amount") BigDecimal amount);

    @Select("\n" +
            "select *from ordOrder o where o.amountApply in (300000,160000) and borrowingCount = 1 and status in (10,11) order by createTime desc limit 100;\n")
    List<OrdOrder> getTestOrders();


}
