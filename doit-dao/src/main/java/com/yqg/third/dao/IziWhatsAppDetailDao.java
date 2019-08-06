package com.yqg.third.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.third.entity.IziWhatsAppDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IziWhatsAppDetailDao extends BaseMapper<IziWhatsAppDetailEntity> {

    @Select("select *from (select * from iziWhatsAppDetail where userUuid=#{userUuid} and type=#{type} and mobileNumber=#{mobile} order by updateTime " +
            "desc) a limit 1")
    IziWhatsAppDetailEntity getLatestResultByUserIdAndType(@Param("userUuid") String userUuid, @Param("type") Integer type,
                                                           @Param("mobile") String mobile);
}
