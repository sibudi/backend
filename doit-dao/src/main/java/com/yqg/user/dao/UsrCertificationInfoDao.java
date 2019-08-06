package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrCertificationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface UsrCertificationInfoDao extends BaseMapper<UsrCertificationInfo> {

    @Update("update doit.usrCertificationInfo set disabled = 1 where userUuid = #{userUuid} " +
            "and certificationType = #{certificationType} and disabled = 0")
    void deleteUsrCertificationInfo(@Param("userUuid") String userUuid,
                                    @Param("certificationType") Integer certificationType);

    @Select("select * from doit.usrCertificationInfo where userUuid = #{userUuid} " +
            "and certificationType in  (1,12) and disabled = 0")
    List<UsrCertificationInfo> listCertificationInfo(@Param("userUuid") String userUuid);

}