package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UangMobiel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by wanghuaizhou on 2018/7/30.
 */
@Mapper
public interface UangMobielDao extends BaseMapper{

    // 获取未发送的用户
    @Select("SELECT * FROM doit.uangMobiel where remark != 1 limit 10000;")
    public List<UangMobiel> getUangMobie();
}
