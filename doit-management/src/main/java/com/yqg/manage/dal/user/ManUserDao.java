package com.yqg.manage.dal.user;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.ManUserSqlProvider;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.user.request.ManSysUserListRequest;
import com.yqg.manage.service.user.response.TeamParentResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author alan
 */
@Mapper
public interface ManUserDao extends BaseMapper<ManUser> {
    @SelectProvider(type= ManUserSqlProvider.class,method = "sysUserList")
    @Options(useGeneratedKeys = true)
    public List<ManUser> sysUserList(@Param("request") ManSysUserListRequest request);

//    @SelectProvider(type= ManUserSqlProvider.class,method = "sysUserListCondition")
//    @Options(useGeneratedKeys = true)
//    public List<ManUser> sysUserListCondition(@Param("request") ManOrderListSearchResquest request);

    @SelectProvider(type= ManUserSqlProvider.class,method = "sysUserListCount")
    @Options(useGeneratedKeys = true)
    public Integer sysUserListCount(@Param("request") ManSysUserListRequest request);

    @SelectProvider(type= ManUserSqlProvider.class,method = "sysUserListConditionCount")
    @Options(useGeneratedKeys = true)
    public Integer sysUserListConditionCount(@Param("request") ManOrderListSearchResquest request);

    @Select(" select id,realname,uuid from manUser where disabled = 0 ")
    public List<ManUser> manUserTotalList();

    @Select(" select u.id, u.realname, u.uuid,u.username,u.third " +
            " from manSysUserRole ur,manUser u " +
            " where ur.roleId = #{systemRoleId}" +
            " and u.id = ur.userId and u.status = 0 and ur.status=0 and ur.disabled = 0 and u.disabled = 0")
    List<ManUser> getSystemUsersByRole(@Param("systemRoleId") Integer systemRoleId);


    @Select("<script>"
            + " select u.id,u.realname,u.uuid,u.username from manUser u where u.id in "
            + " <foreach collection='idList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + " </foreach>"
            + "</script>")
    List<ManUser> getSysUserByIds(@Param("idList") List<Integer> idList);

    @Select("select id, userName as username from manUser where id = parentId and disabled = 0 and status = 0 order by updateTime desc")
    List<TeamParentResponse> listParent ();

    @Select("select * from manUser where parentId = #{id} and id <> #{id} and disabled = 0 and status = 0 order by updateTime desc")
    List<ManUser> listTeam (@Param("id") Integer id);

    //janhsen: check order allow to access
    @Select("SELECT COUNT(o.uuid) FROM ordOrder o " + 
    "INNER JOIN collectionOrderDetail coll ON coll.orderUUID = o.uuid " + 
    "INNER JOIN manUser man ON coll.outsourceId = man.id " + 
    "WHERE o.disabled = 0 AND coll.disabled = 0 and man.disabled = 0 " + 
    "AND (man.parentId = #{outsourceId} or man.id = #{outsourceId}) AND o.uuid = #{orderUuid}") 
    Integer isAllowToSearchOrder(@Param("orderUuid") String orderUuid, @Param("outsourceId") Integer outsourceId) ;
}
