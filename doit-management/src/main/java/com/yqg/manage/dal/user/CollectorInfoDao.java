package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.user.CollectorInfo;
import com.yqg.manage.service.collection.response.CollectorResponseInfo;
import java.util.List;

import org.apache.ibatis.annotations.*;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface CollectorInfoDao extends BaseMapper<CollectorInfo> {

    @Select("select userId,realName,postId from collectorInfo where disabled =0 and postId=#{postId} and rest = 0" +
            " and sourceType = #{sourceType}")
    List<CollectorInfo> getCollectorsByPostId(@Param("postId") Integer postId, @Param("sourceType") Integer sourceType);

    @Select("select userId,realName,postId from collectorInfo where disabled =0 and postId=#{postId} and rest = 1" +
            " and sourceType = #{sourceType}")
    List<CollectorInfo> getRestCollectorsByPostId(@Param("postId") Integer postId, @Param("sourceType") Integer sourceType);

    @Select("select * from collectorInfo where postId=#{postId} and disabled = 0 and sourceType = #{sourceType}")
    List<CollectorInfo> selectCollectorStatusByPostId(@Param("postId") Integer postId, @Param("sourceType") Integer sourceType);

    @Select("<script>"
            + "update collectorInfo set disabled = 1 where postId=#{postId} and disabled = 0 and sourceType = #{sourceType} and userId in "
            +
            " <foreach collection='codes' item='item' separator=',' open='(' close=')'>" +
            "  #{item}"
            + " </foreach>"
            + "</script>")
    Integer updateCollectorStatusByPostId(@Param("postId") Integer postId, @Param("codes") List<String> codes, @Param("sourceType") Integer sourceType);

    @Select("update collectorInfo set rest = 0 where postId=#{postId} and disabled = 0 and rest = 1 and sourceType = #{sourceType}")
    Integer updateRestCollectorStatusByPostId(@Param("postId") Integer postId, @Param("sourceType") Integer sourceType);

    @Insert("<script>" +
            " insert into collectorInfo(createUser,createTime,updateUser,updateTime,postId,userId,realName,disabled,uuid,sourceType) "
            +
            " values " +
            " <foreach collection='collectors' item='item' index='index' separator=','>" +
            "  (#{operatorId},now(),#{operatorId},now(),#{postId},#{item.code},#{item.name},0,replace(uuid(), '-', ''),#{sourceType})"
            + " </foreach>" +
            "</script>")
    Integer insertCollectors(@Param("collectors") List<CollectorResponseInfo> collectors,
                             @Param("postId") Integer postId,
                             @Param("operatorId") Integer operatorId,
                             @Param("sourceType") Integer sourceType);


    @Update("<script>" +
            " update collectorInfo set rest = 1, updateUser = #{operatorId} , updateTime = now() where postId = #{postId} and disabled = 0 " +
            " and sourceType = #{sourceType} and userId in  ("
            +
            " <foreach collection='collectors' item='item' index='index' separator=','>" +
            "  #{item.code}"
            + " </foreach>)" +
            "</script>")
    Integer insertRestCollectors(@Param("collectors") List<CollectorResponseInfo> collectors,
                             @Param("postId") Integer postId,
                             @Param("operatorId") Integer operatorId, @Param("sourceType") Integer sourceType);

    @Select("update collectorInfo set rest =#{rest} where userId=#{userId} and disabled = 0 ")
    Integer updateCollectorStatusByUserId(@Param("rest") Integer rest, @Param("userId") Integer userId);
}
