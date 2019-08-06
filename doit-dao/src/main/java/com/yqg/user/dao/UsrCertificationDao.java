package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrCertificationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface UsrCertificationDao extends BaseMapper<UsrCertificationInfo> {

    @Select("select * from usrCertificationInfo where userUuid = #{userUuid} and certificationType in(2,3) and disabled = 0")
    List<UsrCertificationInfo> getCertificationResults(@Param("userUuid") String userUuid);
}
