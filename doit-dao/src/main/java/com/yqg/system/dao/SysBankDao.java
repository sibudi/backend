package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysBankBasicInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface SysBankDao extends BaseMapper<SysBankBasicInfo> {

    @Select("select * from sysBankBasicInfo where disabled = 0 and uuid = #{userBankUuid} ")
    SysBankBasicInfo getBankBasicInfoById(@Param("userBankUuid") String userBankUuid);

    @Select("select * from sysBankBasicInfo where disabled = 0")
    List<SysBankBasicInfo> getBankInfo();

    @Select("SELECT * FROM doit.sysBankBasicInfo where disabled = 0 and isUsed = 1; ")
    List<SysBankBasicInfo> getBankInfoForCIMB();

    @Select("select * from sysBankBasicInfo where disabled = 0 and bankCode = #{bankCode}")
    SysBankBasicInfo getBankInfoByBankCode(@Param("bankCode")String bankCode);
}
