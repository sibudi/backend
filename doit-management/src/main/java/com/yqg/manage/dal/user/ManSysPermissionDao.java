package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.user.ManSysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author alan
 */
@Mapper
public interface ManSysPermissionDao extends BaseMapper<ManSysPermission> {
    @Select("select id,uuid,createTime,updateTime,remark,parentId,permissionName,permissionCode,permissionUrl" +
            " from manSysPermission where id in ( ${ids} ) and disabled = 0 order by permissionCode")
    public List<ManSysPermission> permissionListById(@Param("ids") String ids);
}
