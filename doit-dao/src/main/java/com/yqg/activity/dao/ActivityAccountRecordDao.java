package com.yqg.activity.dao;

import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.base.data.provider.SqlProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;


/**
 * Features:
 * Created by huwei on 18.8.15.
 */
@Mapper
public interface ActivityAccountRecordDao extends BaseMapper<ActivityAccountRecord> {
    //    @Select("<script> select id,uuid,createUser,createTime,updateUser,updateTime,remark,userUuid,balance,amount,type,channel from activityAccountRecord where disabled = 0 and channel =1 " +
//            "<if test=\"#{uuid} != null\"> and uuid=#{uuid} </if>  " +
//            "<if test=\"#{type} != null\"> and type=#{type} </if>" +
//            "<if test=\"#{caseoutAccount} != null\"> and caseoutAccount=#{caseoutAccount} </if>  " +
//            "<if test=\"#{caseoutAccountName} != null\"> and goPayUserName=#{caseoutAccountName} </if>  " +
//            "<if test=\"#{beginTime} != null and #{endTime} != null\"> and createTime between  #{beginTime} and #{endTime}</if>  " +
//            "<if test=\"#{userUuid} != null\"> and userUuid=#{userUuid} </if>  " +
//            " order by createTime desc limit #{pageStart},#{pageSize} " +
//            "</script>")
    @SelectProvider(type = SqlProvider.class, method = "scanAccountRecords")
    List<ActivityAccountRecord> scanAccountRecords(@Param("uuid") String uuid, @Param("type") String type, @Param("caseoutAccount") String caseoutAccount, @Param("caseoutAccountName") String caseoutAccountName, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("userUuid") String userUuid, @Param("pageSize") Integer pageSize, @Param("pageStart") Integer pageStart, @Param("channle")String channel);


    @SelectProvider(type = SqlProvider.class, method = "scanAccountRecordsCount")
    Integer scanAccountRecordCount(@Param("uuid") String uuid, @Param("type") String type, @Param("caseoutAccount") String caseoutAccount, @Param("caseoutAccountName") String caseoutAccountName, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("userUuid") String userUuid, @Param("channle")String channel);


//    @Select("<script> select id,uuid,createUser,createTime,updateUser,updateTime,remark,userUuid,balance,amount,type,channel from activityAccountRecord where disabled = 0 " +
//            "<if test=\"#{uuid} != null\"> and uuid=#{uuid} </if>  " +
//            "<if test=\"#{caseoutAccount} != null\"> and caseoutAccount=#{caseoutAccount} </if>  " +
//            "<if test=\"#{caseoutAccountName} != null\">and goPayUserName=#{caseoutAccountName} </if>  " +
//            "<if test=\"#{beginTime} != null and #{endTime} != null\"> and createTime between  #{beginTime} and #{endTime} </if>  " +
//            "<if test=\"#{userUuid} != null\"> and userUuid=#{userUuid} </if> " +
//            "and type in (3,4,5,6) " +
//            "<if test=\"#{type} != null\"> and type=#{type} </if> " +
//            " order by createTime desc limit #{pageStart},#{pageSize} </script>")
@SelectProvider(type = SqlProvider.class, method = "withdrawRecord")
    List<ActivityAccountRecord> withdrawRecord(@Param("type") String type, @Param("caseoutAccount") String caseoutAccount, @Param("caseoutAccountName") String caseoutAccountName, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("userUuid") String userUuid, @Param("uuid") String uuid, @Param("pageSize") Integer pageSize, @Param("pageStart") Integer pageStart);

//    @Select("<script> select count(1) from activityAccountRecord where disabled = 0 " +
//            "<if test=\"#{uuid} != null\"> and uuid=#{uuid} </if>  " +
//            "<if test=\"#{caseoutAccount} != null\"> and caseoutAccount=#{caseoutAccount} </if>  " +
//            "<if test=\"#{caseoutAccountName} != null\"> and goPayUserName=#{caseoutAccountName} </if>  " +
//            "<if test=\"#{beginTime} != null and #{endTime} != null\">and createTime between  #{beginTime} and #{endTime}</if>  " +
//            "<if test=\"#{userUuid} != null\"> and userUuid=#{userUuid} </if>" +
//            " and type in (3,4,5,6) " +
//            "<if test=\"#{type} != null\"> and type=#{type} </if>  " +
//            "</script>")
@SelectProvider(type = SqlProvider.class, method = "withdrawRecordCount")
    Integer withdrawRecordCount(@Param("type") String type, @Param("caseoutAccount") String caseoutAccount, @Param("caseoutAccountName") String caseoutAccountName, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("userUuid") String userUuid, @Param("uuid") String uuid);


    @Select("select channel,caseoutAccount,goPayUserName from activityAccountRecord where userUuid=#{userUuid} and type in (3,4,5,6) order by createTime desc limit 1")
    List<ActivityAccountRecord> getLastCard(@Param("userUuid") String userUuid);

    // 待打款提现流水
    @Select("SELECT * FROM doit.activityAccountRecord where  channel = 1 and type = 4 and disabled = 0 and loanStatus = 0;")
    List<ActivityAccountRecord> getLoanList();

    // 打款中提现流水
    @Select("SELECT * FROM doit.activityAccountRecord where  channel = 1 and type = 4 and disabled = 0 and loanStatus = 1;")
    List<ActivityAccountRecord> getLoaningList();

    // 根据银行卡卡号查询是否有提现中操作
    @Select("SELECT * FROM doit.activityAccountRecord where  channel = 1 and type = 4 and disabled = 0 and loanStatus in(0,1) and caseoutAccount = #{caseoutAccount} ;")
    List<ActivityAccountRecord> getLoaningWithCard(@Param("caseoutAccount") String caseoutAccount);
}
