package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.user.ReviewerOrderTask;
import com.yqg.manage.service.order.response.ManOrderListResponse;
import com.yqg.order.entity.OrdOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import org.apache.ibatis.annotations.Update;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 *
 * 审核订单
 ****/

@Mapper
public interface ReviewerOrderTaskDAO extends BaseMapper<ReviewerOrderTask> {

    //历史分配记录失效
    @Insert("<script>" +
            " update reviewerOrderTask set status = 1,updateTime = now(),updateUser=#{operator}" +
            " where orderUUID in " +
            " <foreach collection='orderUUIDs' item='item' index='index' separator=',' open='(' close=')'>"
            +
            "  ${item} " +
            " </foreach>" +
            " and status = 0" +
            " and reviewerRole=#{reviewerRole}" +
            "</script>")
    int disableHistAssignment(@Param("orderUUIDs") List<String> orderUUIDs,
                              @Param("operator") Integer operator, @Param("reviewerRole") String reviewerRole);

    //新的分配记录
    @Insert("<script>" +
            " insert into reviewerOrderTask(createUser,createTime,updateUser,updateTime,orderUUID,status,reviewerId,uuid,finish,reviewerRole) " +
            " values " +
            " <foreach collection='orderUUIDs' item='item' index='index' separator=','>" +
            "  (#{operator},now(),#{operator},now(),#{item},0,#{reviewerId},replace(uuid(), '-', ''),0,#{reviewerRole})" +
            " </foreach>" +
            "</script>")
    int batchInsertReviewTaskAssignment(@Param("orderUUIDs") List<String> orderUUIDs,
                                        @Param("operator") Integer operator,
                                        @Param("reviewerId") Integer reviewerId,
                                        @Param("reviewerRole") String reviewerRole);

    @Select(
            " select o.uuid, o.userUuid,o.amountApply, o.borrowingTerm,o.channel,o.updateTime, o.firstChecker from ordOrder o, reviewerOrderTask r"
                    +
                    " where  r.reviewerId = #{reviewerId} " +
                    " and r.status = 0" +
                    " and r.finish = 0" +
                    " and o.uuid = r.orderUUID" +
                    " and o.status = #{orderStatus} " +
                    " and o.disabled = 0 " +
                    " and r.reviewerRole=#{reviewerRole}" +
                    " order by o.updateTime asc")
    List<ManOrderListResponse> getAssignedReviewOrders(@Param("reviewerId") Integer reviewerId,
                                                       @Param("orderStatus") Integer orderStatus, @Param("reviewerRole") String reviewerRole);

    @Select("<script>"
            + " select orderUUID,createTime,createUser,updateTime,updateUser,reviewerId,status,finish,reviewerRole from reviewerOrderTask"
            + " where orderUUID in "
            + " <foreach collection='orderUUIDs' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + " </foreach>"
            + " and finish = 1 and status = 0 "
            + "</script>")
    List<ReviewerOrderTask> getFinishedAssignedTaskByOrderUUIDs(@Param("orderUUIDs") List<String> orderUUIDs);

    @Update("update reviewerOrderTask set finish = 1, updateTime = now() where orderUUID =#{orderUUID} and reviewerRole =#{reviewerRole} and status = 0")
    int updateFinishStatus(@Param("orderUUID") String orderUUID,
                           @Param("reviewerRole") String reviewerRole);

    /**
     * 查询是否有复审提醒日志
     * @param uuid
     * @return
     */
    @Select("select max(updateTime) from manOrderRemark where orderNo = #{orderNo} and disabled = 0 and (type = 1 or type = 3)")
    String getManRemarkCount(@Param("orderNo") String uuid);

    /**
     * 查询最新分配订单的时间
     * @param uuid
     * @return
     */
    @Select("select max(createTime) from reviewerOrderTask where orderUUID = #{orderNo}")
    String getMaxCreateTime(@Param("orderNo") String uuid);
}
