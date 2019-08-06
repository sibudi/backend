package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface UsrBankDao extends BaseMapper<UsrBank> {

    @Select("select * from usrBank where disabled = 0 and userUuid = #{userUuid} and bankCode = #{bankCode}")
    UsrBank getUserBankInfoByIds(@Param("userUuid") String userUuid, @Param("bankCode") String bankCode);


    @Select("select * from usrBank where disabled = 0 and userUuid = #{userUuid} and uuid = #{userBankUuid}")
    UsrBank getUserBankInfoById(@Param("userUuid") String userUuid, @Param("userBankUuid") String userBankUuid);


    @Select("select * from usrBank where disabled = 0 and userUuid = #{userUuid} and bankNumberNo = #{bankNumberNo} and bankCode = #{bankCode} ")
    UsrBank getUserBankInfoByIdAndNo(@Param("userUuid") String userUuid, @Param("bankNumberNo") String bankNumberNo,@Param("bankCode") String bankCode);


    @Select("select * from usrBank where disabled = 0 and status in (1,2) and userUuid = #{userUuid}")
    List<UsrBank> getUserBankList(@Param("userUuid") String userUuid);

    @Select("select * from usrBank where disabled = 0 and status in (1,2) and userUuid = #{userUuid} and bankNumberNo != #{bankNumberNo}")
    List<UsrBank> getUserBankListWithout(@Param("userUuid") String userUuid,@Param("bankNumberNo") String bankNumberNo);

    @Select("select max(bankorder) from usrBank where status in (1,2) and disabled = 0 and userUuid = #{userUuid}")
    Integer getMaxBankorder(@Param("userUuid") String userUuid);

    @Select("select * from usrBank where status in (1,2) and disabled = 0 and userUuid = #{userUuid} order by bankorder desc")
    List<UsrBank> getSuccessAndPedding(@Param("userUuid") String userUuid);

    @Select("select * from usrBank where status = 2 and disabled = 0 and userUuid = #{userUuid} order by bankorder desc")
    List<UsrBank> getSuccess(@Param("userUuid") String userUuid);


    @Select("select count(distinct userUuid) from usrBank b where b.disabled=0\n" +
            "and b.bankNumberNo = #{bankcardNumber} and b.bankCode=#{bankCode}\n" +
            "and userUuid !=#{userUuid}")
    Integer countOfSameBankcardNumberWithOthers(@Param("bankcardNumber") String bankcardNo,@Param("bankCode") String bankCode ,
                                            @Param("userUuid") String userUuid);

    @Select("select count(1) from usrBank b where b.userUuid = #{userUuid} and b.disabled=0\n" +
            "and (select count(1) from usrBank bb where bb.userUuid!=b.userUuid and bb.bankNumberNo = b.bankNumberNo and bb.bankCode=b.bankCode and bb.disabled\n" +
            "                                                                                                                                         =0)>=1\n" +
            ";")
    Integer userInMultiBankcardUser(@Param("userUuid") String userUuid);
}
