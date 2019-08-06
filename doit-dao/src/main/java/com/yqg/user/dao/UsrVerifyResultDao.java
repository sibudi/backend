package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrVerifyResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UsrVerifyResultDao extends BaseMapper<UsrVerifyResult> {
    @Select("SELECT * FROM usrVerifyResult s WHERE s.verifyType=1 AND verifyResult=0 AND createTime<'2018-08-18 10:44'")
    List<UsrVerifyResult> getKtpErrorDataList();

    @Select("select * from usrVerifyResult where verifyType = #{type} and disabled=0 and orderNo=#{orderNo} order by createTime desc limit 1 ")
    UsrVerifyResult getLatestVerifyResultWithType(@Param("orderNo") String orderNo,@Param("type") Integer type);


    @Select("select * from usrVerifyResult where verifyType = #{type} and disabled=0 and userUuid=#{userUuid} order by createTime desc limit 1 ")
    UsrVerifyResult getLatestVerifyResultByUserUuid(@Param("userUuid") String userUuid,@Param("type") Integer type);
}
