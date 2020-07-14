package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrAttachmentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface UsrAttachmentInfoDao extends BaseMapper<UsrAttachmentInfo> {


    @Select("SELECT * FROM usrAttachmentInfo where userUuid = #{userUuid} and attachmentType in(4,5,6,7,9,17) and disabled = 0")
    List<UsrAttachmentInfo> getAttachmenInfoByStudent (@Param("userUuid") String userUuid);


    @Select("SELECT * FROM usrAttachmentInfo where userUuid = #{userUuid} and attachmentType in(4,5,6,7,8,9,17) and disabled = 0")
    List<UsrAttachmentInfo> getAttachmenInfoByWorker (@Param("userUuid") String userUuid);


    @Select("SELECT * FROM usrAttachmentInfo where userUuid = #{userUuid} and attachmentType in(10,11,12,13,14,15) and disabled = 0")
    List<UsrAttachmentInfo> getSchoolAttachmenInfoByUserUuid (@Param("userUuid") String userUuid);


    @Select("SELECT * FROM usrAttachmentInfo where userUuid = #{userUuid} and attachmentType in(2,3) and disabled = 0")
    List<UsrAttachmentInfo> getCheckAttachmenInfoByUserUuid (@Param("userUuid") String userUuid);

    @Update("update usrAttachmentInfo set disabled = 1 where userUuid = #{userUuid} and " +
            "attachmentType = #{attachmentType} and disabled = 0")
    void deleteUsrAttachmentInfo(@Param("userUuid") String userUuid,
                                    @Param("attachmentType") Integer attachmentType);

    @Select("\n" +
            "select *from usrAttachmentInfo t where t.attachmentUrl not like 'http://www.do-it.id%'\n" +
            "and t.attachmentUrl not like 'http://image.uanguang.id%';")
    List<UsrAttachmentInfo> getErrorImagePathListForCashCash();

    @Select("select * from usrAttachmentInfo where userUuid = #{userUuid} and " +
            "attachmentType = #{attachmentType} and disabled = 1 and remark = '' order by createTime desc ")
    List<UsrAttachmentInfo> getDisabledUsrAttachmentInfo(@Param("userUuid") String userUuid,
                                 @Param("attachmentType") Integer attachmentType);
}