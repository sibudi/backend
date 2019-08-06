package com.yqg.manage.dal.system;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.SysAppVersionSqlProvider;
import com.yqg.manage.service.system.request.ManAppVersionListRequest;
import com.yqg.system.entity.SysAppVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author alan
 */
@Mapper
public interface ManAppVersionDao extends BaseMapper<SysAppVersion> {
    @SelectProvider(type = SysAppVersionSqlProvider.class, method = "appList")
    @Options(useGeneratedKeys = true)
    public List<SysAppVersion> appVersionList(@Param("ManAppVersionListRequest") ManAppVersionListRequest appVersionListRequest);

    @SelectProvider(type = SysAppVersionSqlProvider.class, method = "appListCount")
    @Options(useGeneratedKeys = true)
    public Integer appVersionListCount(@Param("ManAppVersionListRequest") ManAppVersionListRequest appVersionListRequest);
}
