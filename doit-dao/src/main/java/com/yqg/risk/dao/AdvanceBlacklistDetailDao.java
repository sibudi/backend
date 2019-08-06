package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.AdvanceBlacklistDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdvanceBlacklistDetailDao extends BaseMapper<AdvanceBlacklistDetail> {
    @Insert("<script>"+
            "\n"+
            "INSERT INTO advanceBlacklistDetail\n" +
            "(id,\n" +
            "userUuid,\n" +
            "orderNo,\n" +
            "eventTime,\n" +
            "hitReason,\n" +
            "productType,\n" +
            "reasonCode,\n" +
            "createTime,\n" +
            "updateTime,\n" +
            "remark,\n" +
            "disabled,\n" +
            "uuid)" +
            " values "
            + "   <foreach collection='saveList' item='record' separator=',' >"
            +"(#{record.id},\n" +
            "#{record.userUuid},\n" +
            "#{record.orderNo},\n" +
            "#{record.eventTime},\n" +
            "#{record.hitReason},\n" +
            "#{record.productType},\n" +
            "#{record.reasonCode},\n" +
            "#{record.createTime},\n" +
            "#{record.updateTime},\n" +
            "#{record.remark},\n" +
            "#{record.disabled},\n" +
            "#{record.uuid})"
            + "   </foreach>"
            + " </script>")
    Integer insertBatch(@Param("saveList") List<AdvanceBlacklistDetail> saveList);
}
