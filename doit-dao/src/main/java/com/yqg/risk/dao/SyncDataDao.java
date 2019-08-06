package com.yqg.risk.dao;

import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.entity.RiskSyncDataConfig;
import com.yqg.risk.entity.RiskSyncDataIds;
import com.yqg.user.entity.UsrIziVerifyResult;
import org.apache.ibatis.annotations.*;
import java.util.Date;

import java.util.List;

@Mapper
public interface SyncDataDao {
    @Select("select next_start_time startTime,\n" +
            "\t\t\t next_end_time endTime from kettle_job_msg where\n" +
            "\t\tjob_name='orderRiskRecord_sync' and disabled=0; ")
    RiskSyncDataConfig getRiskDataSyncConfig();

    @Update("update kettle_job_msg s set s.next_start_time = s.next_end_time,s.next_end_time=date_sub(now(), interval 1 minute) where " +
            "job_name='orderRiskRecord_sync' and disabled=0;\n")
    Integer updateRiskDataSyncConfig();

    @Insert("<script>"
            + "INSERT INTO ordRiskRecord_last(userUuid,"
            + "   orderNo,ruleType,"
            + "   ruleDetailType,ruleDesc,"
            + "   ruleRealValue,"
            + "   remark,"
            + "   uuid,createTime,updateTime) "
            + " values "
            + "   <foreach collection='riskRecords' item='record' separator=',' >"
            + "     (#{record.userUuid},"
            + "      #{record.orderNo},#{record.ruleType},"
            + "      #{record.ruleDetailType},#{record.ruleDesc},"
            + "      #{record.ruleRealValue},"
            + "      #{record.remark},"
            + "      #{record.uuid}, #{record.createTime},#{record.updateTime})"
            + "   </foreach>"
            + "</script>")
    int addRiskRecordList(@Param("riskRecords") List<OrdRiskRecord> riskRecords);

    @Update("delete from ordRiskRecord_last where orderNo=#{orderNo} and disabled=0")
    int disabledRecord(@Param("orderNo") String orderNo);

    @Select("select min(createTime) from ordRiskRecord_last;")
    Date getMinDateFromOrderRiskRecordLast();

    @Select("select min(id) minId,max(id) maxId from ordRiskRecord_last where createTime<#{endDate} and createTime>=#{startDate}")
    RiskSyncDataIds getMaxMinIdsByDate(@Param("endDate") Date endDate, @Param("startDate") Date startDate);

    @Insert("INSERT INTO ordRiskRecord_01" +
            "(id,\n" +
            "uuid,\n" +
            "disabled,\n" +
            "createTime,\n" +
            "updateTime,\n" +
            "createUser,\n" +
            "updateUser,\n" +
            "remark,\n" +
            "userUuid,\n" +
            "orderNo,\n" +
            "ruleType,\n" +
            "ruleDetailType,\n" +
            "ruleDesc,\n" +
            "ruleRealValue)\n" +
            "select id,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,userUuid,orderNo,ruleType,ruleDetailType,ruleDesc,ruleRealValue\n" +
            "from ordRiskRecord_last o where id>=#{startId} and id<#{endId} and createTime>=#{startDate} and createTime<#{endDate}" +
            " and not exists(select 1 from ordRiskRecord_01 oo where oo.uuid = o.uuid);")
    Integer batchInsertByIdPeriod(@Param("startId") Long startId,@Param("endId") Long endId, @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate);

    @Delete("delete from ordRiskRecord_last where id>=#{startId} and id<#{endId}  and createTime>=#{startDate} and createTime<#{endDate}")
    Integer batchDeleteByIdPeriod(@Param("startId") Long startId,@Param("endId") Long endId, @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate);

    @Select("select min(id) minId,max(id) maxId from ordRiskRecord_01 where createTime<#{maxDate}")
    RiskSyncDataIds getMaxMinIdsForTable01(@Param("maxDate") Date maxDate);

    @Delete("delete from ordRiskRecord_01 where createTime<#{maxDate} limit 10000")
    int batchDeleteWithLimit(@Param("maxDate") Date maxDate);

    @Select("select * from ordRiskRecord_last a where a.ruleDetailType = 'RECENT_30_CALL_TIME_MALE'\n" +
            "and not exists(\n" +
            "select 1 from ordRiskRecord_last s where ruleDetailType = 'RECENT_30_CALL_TIME' and s.orderNo = a.orderNo\n" +
            ") limit 100;  ")
    List<OrdRiskRecord> getFixData();


    //需要处理的最大最小id
    @Select("select min(id) minId,max(id) maxId from ordOrder where status!=1 and disabled=0 and orderType!=1 and createTime<#{endDate} and " +
            " createTime>=#{startDate}")
    RiskSyncDataIds getMinMaxIdsForOrder(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Select("select * from ordOrder where status!=1 and disabled=0 and orderType!=1 and id>=#{startId} and id<#{endId}")
    List<OrdOrder> getOrdersByIdLimit(@Param("startId") Long startId,@Param("endId") Long endId);

    //批量插入
    @Insert("<script>"
            + "INSERT INTO ordRiskRecord_app_clean(userUuid,"
            + "   orderNo,ruleType,"
            + "   ruleDetailType,ruleDesc,"
            + "   ruleRealValue,"
            + "   remark,"
            + "   uuid,createTime,updateTime) "
            + " values "
            + "   <foreach collection='riskRecords' item='record' separator=',' >"
            + "     (#{record.userUuid},"
            + "      #{record.orderNo},#{record.ruleType},"
            + "      #{record.ruleDetailType},#{record.ruleDesc},"
            + "      #{record.ruleRealValue},"
            + "      #{record.remark},"
            + "      #{record.uuid}, now(),now())"
            + "   </foreach>"
            + "</script>")
    int addRiskRecordListForAppClean(@Param("riskRecords") List<OrdRiskRecord> riskRecords);


    @Insert("" +
            "INSERT INTO `doit`.`ordRiskRecord_01_tmp`\n" +
            "(`id`,\n" +
            "`uuid`,\n" +
            "`disabled`,\n" +
            "`createTime`,\n" +
            "`updateTime`,\n" +
            "`createUser`,\n" +
            "`updateUser`,\n" +
            "`remark`,\n" +
            "`userUuid`,\n" +
            "`orderNo`,\n" +
            "`ruleType`,\n" +
            "`ruleDetailType`,\n" +
            "`ruleDesc`,\n" +
            "`ruleRealValue`)\n" +
            " select id,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,userUuid,orderNo,ruleType,ruleDetailType,\n" +
            "ruleDesc, ruleRealValue from ordRiskRecord_last \n" +
            " where id >=#{minId} and id< #{maxId}" +
            " and createTime>=#{startTime} and createTime<#{endTime}" +
            "")
    Integer insertBatch01(@Param("minId") Long minId,@Param("maxId") Long maxId,@Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime);
    @Insert("" +
            "INSERT INTO `doit`.`ordRiskRecord_02`\n" +
            "(`id`,\n" +
            "`uuid`,\n" +
            "`disabled`,\n" +
            "`createTime`,\n" +
            "`updateTime`,\n" +
            "`createUser`,\n" +
            "`updateUser`,\n" +
            "`remark`,\n" +
            "`userUuid`,\n" +
            "`orderNo`,\n" +
            "`ruleType`,\n" +
            "`ruleDetailType`,\n" +
            "`ruleDesc`,\n" +
            "`ruleRealValue`)\n" +
            " select id,uuid,disabled,createTime,updateTime,createUser,updateUser,remark,userUuid,orderNo,ruleType,ruleDetailType,\n" +
            "ruleDesc, ruleRealValue from ordRiskRecord_01 o \n" +
            " where id >=#{minId} and id< #{maxId}" +
            " and createTime<#{endTime}" +
            " and not exists(select 1 from ordRiskRecord_02 oo where oo.uuid = o.uuid)" +
            "")
    Integer insertBatch02(@Param("minId") Long minId,@Param("maxId") Long maxId,@Param("endTime") Date endTime);

    @Delete("delete from ordRiskRecord_01 where id>=#{minId} and id<#{maxId} and createTime<#{endTime}")
    Integer deleteBatch01(@Param("minId") Long minId,@Param("maxId") Long maxId,
                                 @Param("endTime") Date endTime);


    @Insert("INSERT INTO `doit`.`usrIziVerifyResult_data_clean_zxc`\n" +
            "(\n" +
            "`uuid`,\n" +
            "`disabled`,\n" +
            "`createTime`,\n" +
            "`updateTime`,\n" +
            "`createUser`,\n" +
            "`updateUser`,\n" +
            "`remark`,\n" +
            "`userUuid`,\n" +
            "`orderNo`,\n" +
            "`iziVerifyType`,\n" +
            "`iziVerifyResult`,\n" +
            "`iziVerifyResponse`)\n" +
            "VALUES\n" +
            "(\n" +
            "replace(uuid(), '-', ''),\n" +
            "0,\n" +
            "now(),\n" +
            "now(),\n" +
            "0,\n" +
            "0,\n" +
            "'20190424更新',\n" +
            "#{record.userUuid},\n" +
            "#{record.orderNo},\n" +
            "#{record.iziVerifyType},\n" +
            "#{record.iziVerifyResult},\n" +
            "#{record.iziVerifyResponse});")
    Integer addIziCleanData(@Param("record") UsrIziVerifyResult iziVerifyResult);

    @Insert("INSERT INTO `doit`.`mk_WhatsappIdentityVerify`\n" +
            "(`UserUuid`,\n" +
            "`uuid`,\n" +
            "`type`,\n" +
            "`result`,\n" +
            "`response`,\n" +
            "`whatsapp`,\n" +
            "`whatsAppNumber`,\n" +
            "`whatsAppNumberType`,\n" +
            "`createtime`,\n" +
            "`updatetime`)\n" +
            "VALUES\n" +
            "(#{record.userUuid},\n" +
            "#{record.orderNo},\n" +
            "#{record.iziVerifyType},\n" +
            "#{record.iziVerifyResult},\n" +
            "#{record.iziVerifyResponse},\n" +
            "#{record.whatsapp},\n" +
            "#{record.whatsAppNumber},\n" +
            "#{record.whatsAppNumberType},\n" +
            "now(),\n" +
            "now());")
    Integer addIziWhatsApp(@Param("record") UserIziVerifyResultMongo record);

    @Delete("delete from mk_WhatsappIdentityVerify where uuid=#{orderNo} and  whatsAppNumberType={sequence}")
    Integer deleteWhatsappByOrderAndSequence(@Param("sequence") String sequence, @Param("orderNo") String orderNo);

    @Select("select count(1) from mk_WhatsappIdentityVerify s where s.uuid=#{orderNo} and s.whatsAppNumber =#{number}")
    Integer hasWhatsAppResult(@Param("number") String number, @Param("orderNo") String orderNo);

    @Select("select count(1) from mk_WhatsappIdentityVerify s where s.uuid=#{orderNo} and s.whatsAppNumber =#{number} and (whatsapp!='checking' or " +
            " result !='OK')")
    Integer hasSuccessWhatsappResult(@Param("number") String number, @Param("orderNo") String orderNo);

    @Select("select  DISTINCT  userUuid,  uuid,  status,  amountApply,applyTime \n" +
            "from  doit.ordOrder b\n" +
            "where  userUuid  in\n" +
            "(SELECT  distinct  userUuid\n" +
            "FROM  doit.usrVerifyResult\n" +
            "where  verifyType=6 \n" +
            "and  verifyResult=2\n" +
            "and  disabled=0)\n" +
            "and  userUuid    in\n" +
            "(SELECT  distinct  userUuid\n" +
            "FROM  doit.usrVerifyResult\n" +
            "where  verifyType=1 \n" +
            "and  verifyResult  =1\n" +
            "and  disabled=0)\n" +
            "and  disabled=0\n" +
            "and  borrowingCount  =  1\n" +
            "and  date(createTime)  >  '2019-03-19'\n" +
            "and  date(applyTime)<'2019-04-20'\n" +
            "and  status>1\n" +
            "\n" +
            "and  not exists(\n" +
            "select 1 from usrVerifyResult aa where aa.userUuid = b.userUuid and verifyType = 2\n" +
            ")")
    List<OrdOrder> getReRunAdvanceData();

}
