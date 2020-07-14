package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrLinkManInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface UsrLinkManDao extends BaseMapper<UsrLinkManInfo> {

    @Select("SELECT * FROM usrLinkManInfo where  disabled = 0 and sequence in(1,2,3) and userUuid = #{userUuid}")
    List<UsrLinkManInfo> getUsrLinkManWithUserUuid(@Param("userUuid") String userUuid);

    @Select("<script>"
            + "select count(1) from usrLinkManInfo where disabled=0 and sequence in (1,2) and contactsMobile in "
            + "<foreach collection='mobileNumbers' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>"
            + "</script>")
    Integer getEmergencyTelCountWithMobiles(@Param("mobileNumbers") List<String> mobileNumbers);

    @Select("select userUuid from (select * from usrLinkManInfo where formatMobile=#{formatMobile} and userUuid!=#{userUuid} and " +
            "sequence = 1 order by createTime asc) a limit 1")
    String getTheFirstUserIdWithSameFirstEmergencyTel(@Param("userUuid") String userUuid, @Param("formatMobile") String formatMobile);

    @Select("<script>" +
            "select userUuid from (select * from usrLinkManInfo where formatMobile in "
            + "<foreach collection='formatMobiles' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>"+
            " and userUuid!=#{userUuid} " +
            " order by createTime asc) a limit 1" +
            "</script>")
    String getTheFirstUserIdWithSameEmergencyTel(@Param("userUuid") String userUuid,@Param("formatMobiles") List<String> formatMobiles);


    @Select("select * from usrLinkManInfo u where u.disabled=0 and u.formatMobile is null limit 1000")
    List<UsrLinkManInfo> getDataFixList();

    @Update("update usrLinkManInfo set formatMobile=#{formatMobile} where id = #{id}")
    Integer updateFormatMobile(@Param("formatMobile") String formatMobile,@Param("id") Integer id);

    @Select("select * from usrLinkManInfo where disabled=0 and sequence!=3 and formatMobile=#{mobile}")
    List<UsrLinkManInfo> getLinkmanByFormatMobile(@Param("mobile") String mobile);
}