package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.entity.user.ReviewerScheduler;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Mapper
public interface ReviewerSchedulerDAO extends BaseMapper<ReviewerScheduler> {

    @Update(" update reviewerScheduler set status = 1,updateTime = now(),updateUser=#{operator} " +
            " where reviewerId in " +
            " (select ur.userId from manSysUserRole ur where ur.roleId=#{roleId} and ur.disabled=0) "
            + "   and reviewerRole =#{reviewerRole} ")
    int disableHistSchedulerByRoleId(@Param("roleId") int roleId,
                                     @Param("operator") Integer operatorId, @Param("reviewerRole") String reviewerRole);

    @Insert("<script>" +
            " insert into reviewerScheduler(createUser,createTime,updateUser,updateTime,reviewerId,status,uuid,reviewerRole) "
            +
            " values " +
            " <foreach collection='reviewerIds' item='item' index='index' separator=','>" +
            "  (#{operatorId},now(),#{operatorId},now(),#{item},0,replace(uuid(), '-', ''),#{reviewerRole})"
            +
            " </foreach>" +
            "</script>")
    int batchInsertSchedulerInfo(@Param("reviewerIds") List<Integer> reviewerIds,
                                 @Param("operatorId") Integer operatorId, @Param("reviewerRole") String reviewerRole);

    @Select(" select u.id, u.realname, u.uuid,u.username " +
            " from manSysUserRole ur,manUser u " +
            " where ur.roleId = #{systemRoleId} " +
            " and u.id = ur.userId and u.status = 0 and ur.status=0 and ur.disabled =0 and u.disabled=0 "
            +
            " and ur.userId in ( select reviewerId from reviewerScheduler rs where rs.status = 0  and rs.reviewerRole = #{reviewerRole})")
    List<ManUser> getCurrentReviewersByRole(@Param("systemRoleId") Integer systemRoleId,
                                            @Param("reviewerRole") String reviewerRole);
}
