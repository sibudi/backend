package com.yqg.signcontract.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.signcontract.entity.UsrSignContractStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UsrSignContractStepDao  extends BaseMapper<UsrSignContractStep> {

    @Select("select * from (select * from usrSignContractStep where userUuid = #{userUuid} and signStep=#{type} and disabled = 0 order by " +
            " id desc) a limit 1")
    UsrSignContractStep getStepResultByType(@Param("userUuid") String userUuid, @Param("type") Integer type);
    @Select("select * from (select * from usrSignContractStep where userUuid = #{userUuid} and disabled = 0 order by " +
            " id desc) a limit 1")
    UsrSignContractStep getLatestStepResult(@Param("userUuid") String userUuid);


}
