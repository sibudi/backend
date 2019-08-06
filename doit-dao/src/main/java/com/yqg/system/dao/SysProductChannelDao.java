package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysProductChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by wanghuaizhou on 2019/6/24.
 */
@Mapper
public interface SysProductChannelDao  extends BaseMapper<SysProductChannel> {

    @Select("select productUuid from sysProductChannel where channel = #{channel}  and disabled = 0;")
    List<String> getProductChannel(@Param("channel") Integer channel);

}
