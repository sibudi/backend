package com.yqg.management.dao;

import org.apache.ibatis.annotations.*;

import java.sql.Date;
import java.util.List;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.management.entity.QuirosResultEntity;


/***** @Author super
 * Created at 2019/04/08
 * @Email lijianping@yishufu.com
 *
 ****/

@Mapper
public interface QuirosResultDao extends BaseMapper<QuirosResultEntity>  {

    @Insert(
        "insert into qualityCheckingVoice( " + // uuid,extnumber,  destnumber,status,createTime,updateTime) "
                "uuid,orderNo,extNumber,destNumber,realName,applyAmount,applyDeadline,userName,callNode,callType,createTime,updateTime, userId, sourceType "
                + " ) values( " +
                "#{uuid},#{orderNo},#{extnumber},#{destnumber},#{realName},#{applyAmount},#{applyDeadline},#{userName},#{callNode},#{callType},now(),now(),#{userid},#{sourceType})")
    int saveBill(QuirosResultEntity entity);

    @Select("\n" +
    "select * from doit.qualityCheckingVoice where createTime > '2019-11-17' and downUrl='' and timestampdiff(HOUR, createTime, now()) <= 48" )
    List<QuirosResultEntity> getOrderListToUpdate();

    @Update("update doit.qualityCheckingVoice set downUrl=#{downUrl}, result=#{status}, recordLength=#{duration}, recordBeginTime=#{recordBeginTime}, recordEndTime=#{recordEndTime} where uuid =#{uuid}")
    Integer updateTableSuccess(@Param("downUrl") String downUrl,@Param("uuid") String uuid,@Param("status") String status, @Param("duration") int duration, @Param("recordBeginTime") java.sql.Timestamp recordBeginTime,@Param("recordEndTime") java.sql.Timestamp recordEndTime);

    @Update("update doit.qualityCheckingVoice set result='NOT CALLING' where uuid =#{uuid}")
    Integer updateTableFailed(@Param("uuid") String uuid);

}

