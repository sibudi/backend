package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.user.ManSysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author alan
 */
@Mapper
public interface ManSysUserRoleDao extends BaseMapper<ManSysUserRole> {
    @Update(" update manSysUserRole set disabled = 1 where userId = #{userId} and disabled = 0 ")
    public void delUserRoleLink(@Param("userId") Integer userId);
}
