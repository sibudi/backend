package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.OrderScoreDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderScoreDetailDao extends BaseMapper<OrderScoreDetail> {

    @Update("update orderRiskScore set disabled=1 ,updateTime= now() where orderNo = #{orderNo} and disabled=0")
    Integer disabledByOrderNo(@Param("orderNo") String orderNo);

    @Insert("<script>" +
            "insert into orderScoreDetail\n" +
            "(\n" +
            "  variableName,\n" +
            "  variableThresholdName,\n" +
            "  score,\n" +
            "  realValue ,\n" +
            "  modelName,\n" +
            "  createTime,\n" +
            "  updateTime,\n" +
            "  remark,\n" +
            "  uuid,\n" +
            "  orderNo,\n" +
            "  userUuid,\n" +
            "  version" +
            ")"
            + " values "
            + "   <foreach collection='scoreDetailList' item='record' separator=',' >"
            + "     (#{record.variableName},"
            + "      #{record.variableThresholdName}," +
            "        #{record.score},"
            + "      #{record.realValue}," +
            "        #{record.modelName},"
            + "      #{record.createTime}," +
            "        #{record.updateTime},"
            + "      #{record.remark}, " +
            "        #{record.uuid}," +
            "        #{record.orderNo}," +
            "        #{record.userUuid}," +
            "        #{record.version}" +
            ")"
            + "   </foreach>"
            + " </script>")
    int addScoreDetail(@Param("scoreDetailList") List<OrderScoreDetail> scoreDetailList);

}
