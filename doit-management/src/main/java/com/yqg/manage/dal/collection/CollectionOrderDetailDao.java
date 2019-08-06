package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.collection.CollectionOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface CollectionOrderDetailDao extends BaseMapper<CollectionOrderDetail>{

    /**
     * 删除订单没有还款的分配信息(只回收催收的，sourceType为0）
     * @return
     */
    @Update("UPDATE collectionOrderDetail\n" +
            "SET outsourceId = 0,subOutSourceId = 0,\n" +
            " assignedTime = now(),\n" +
            " createTime = now(),\n" +
            " createUser = 0,\n" +
            " updateTime = now(),\n" +
            " updateUser = 0\n" +
            "WHERE\n" +
            "\torderUUID IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\tuuid\n" +
            "\t\tFROM\n" +
            "\t\t\tordOrder\n" +
            "\t\tWHERE\n" +
            "\t\t\t(STATUS = '7'\n" +
            "\t\tOR STATUS = '8')\n" +
            "\t\tAND disabled = 0 AND (datediff(now(),refundTime) in (${stages}) " +
            "\t)) and outsourceId != 207 and sourceType = 0 \n" +
            "AND disabled = 0\n" +
            "\n")
    int recycleCollectionOrder (@Param("stages") String stages);

    @Select("SELECT orderUUID from collectionOrderDetail " +
            "WHERE\n" +
            "\torderUUID IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\tuuid\n" +
            "\t\tFROM\n" +
            "\t\t\tordOrder\n" +
            "\t\tWHERE\n" +
            "\t\t\t(STATUS = '7'\n" +
            "\t\tOR STATUS = '8')\n" +
            "\t\tAND disabled = 0 AND (datediff(now(),refundTime) in (${stages}) " +
            "\t)) and outsourceId != 207 and sourceType = 0 \n" +
            "AND disabled = 0\n" +
            "\n")
    List<String> getRecycleCollectionOrder(@Param("stages") String stages);

    /**
     * 删除订单没有还款的质检信息(sourceType为1）
     * @return
     */
    @Update("UPDATE collectionOrderDetail\n" +
            "SET outsourceId = 0,subOutSourceId = 0,\n" +
            " assignedTime = now(),\n" +
            " createTime = now(),\n" +
            " createUser = 0,\n" +
            " updateTime = now(),\n" +
            " updateUser = 0\n" +
            "WHERE\n" +
            "\torderUUID IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\tuuid\n" +
            "\t\tFROM\n" +
            "\t\t\tordOrder\n" +
            "\t\tWHERE\n" +
            "\t\t\t(STATUS = '7'\n" +
            "\t\tOR STATUS = '8')\n" +
            "\t\tAND disabled = 0 AND (datediff(now(),refundTime) in (${stages}) " +
            "\t)) and outsourceId != 207 and sourceType = 1 \n" +
            "AND disabled = 0\n" +
            "\n")
    int recycleQualityOrder (@Param("stages") String stages);


    @Select("SELECT orderUUID from collectionOrderDetail " +
            "WHERE\n" +
            "\torderUUID IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\tuuid\n" +
            "\t\tFROM\n" +
            "\t\t\tordOrder\n" +
            "\t\tWHERE\n" +
            "\t\t\t(STATUS = '7'\n" +
            "\t\tOR STATUS = '8')\n" +
            "\t\tAND disabled = 0 AND (datediff(now(),refundTime) in (${stages}) " +
            "\t)) and outsourceId != 207 and sourceType = 1 \n" +
            "AND disabled = 0\n" +
            "\n")
    List<String> getRecycleQualityOrder(@Param("stages") String stages);

    /**
     * 只删除逾期三天，而且不包括委外人员（其中角色Id，写死，线上16是委外母账号，测试是10）
     * @return
     */
    @Update("update doit.collectionOrderDetail set outsourceId = 0 , remark = '单独回收逾期三天的内催人员' where outsourceId in " +
            "(select distinct(usr.id) from doit.manUser usr left join doit.manSysUserRole sysUsrRole on sysUsrRole.userId = usr.id" +
            " where usr.disabled = 0  and usr.status = 0 and sysUsrRole.disabled = 0 and sysUsrRole.roleId != 16) " +
            "and disabled = 0 and orderUuid in (select uuid from ordOrder where `status` = 8 and disabled = 0 and " +
            "dateDiff(now(),refundTime) = 3) and outsourceId != 207 and sourceType = 0 ")
    int recycleThreeDaysSelfOrder();

    @Update("update collectionOrderDetail set updateTime = now(), checkResult = #{checkResult} where orderUUid = #{orderNo} and sourceType = 1 ")
    int updateCollectionDetailCheck(@Param("checkResult") Integer checkResult, @Param("orderNo") String orderNo);

    @Update("update collectionOrderDetail set updateTime = now(), voiceCheckResult = #{checkResult} where orderUUid = #{orderNo} and sourceType = 1 ")
    int updateCollectionDetailCheckVoice(@Param("checkResult") Integer checkResult, @Param("orderNo") String orderNo);

    @Update("update collectionOrderDetail set updateTime = now(), orderTag = #{orderTag}, promiseRepaymentTime = #{promiseRepaymentTime} where orderUUid = #{orderNo} ")
    int updateOrderTagAndPromiseTime(@Param("orderNo") String orderNo, @Param("orderTag") Integer orderTag, @Param("promiseRepaymentTime") Date promiseRepaymentTime);
}
