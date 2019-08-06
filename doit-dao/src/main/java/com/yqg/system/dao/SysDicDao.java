package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysDic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface SysDicDao extends BaseMapper<SysDic> {

    @Select(" select id,uuid,createUser,createTime,updateUser,updateTime,remark,dicName,dicCode from sysDic where disabled = 0 limit #{pageStart},#{pageSize} ")
    public List<SysDic> sysDicByPage(@Param("pageSize") Integer pageSize, @Param("pageStart") Integer pageStart);

    /**
    * @Description: 查询所有委外公司帐号
    * @Param: []
    * @return: java.util.List<com.yqg.system.entity.SysDic>
    * @Author: 许金泉
    * @Date: 2019/4/18 10:56
    */
    @Select(" SELECT * FROM sysDic d WHERE d.dicCode LIKE 'THIRD_COMPANY%'")
    public List<SysDic> getThirdCompany();

}
