package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.user.ManSysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author alan
 */
@Mapper
public interface ManSysRolePermissionDao extends BaseMapper<ManSysRolePermission> {
    @Update("update manSysRolePermission set disabled = 1 where roleId = #{roleId} and disabled = 0")
    public void delByRoleId(@Param("roleId") Integer roleId);
}
