package com.yqg.manage.service.system;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.system.ManAppVersionDao;
import com.yqg.manage.service.system.request.ManAppVersionListRequest;
import com.yqg.manage.service.system.request.ManAppVersionRequest;
import com.yqg.manage.service.system.response.AppVersionListResponse;
import com.yqg.system.entity.SysAppVersion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author alan
 */
@Component
public class ManAppVersionService  {
    @Autowired
    private ManAppVersionDao manAppVersionDao;

    /**
     * 添加版本信息
     */
    public void addAppVersion(ManAppVersionRequest sysAppVersion) throws ServiceExceptionSpec {
        SysAppVersion addInfo = new SysAppVersion();
        addInfo.setAppVersion(sysAppVersion.getVersionNo());
        addInfo.setSysType(sysAppVersion.getSysType());
        addInfo.setDisabled(0);

        List<SysAppVersion> versionList = this.manAppVersionDao.scan(addInfo);
        if (!CollectionUtils.isEmpty(versionList)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        addInfo = this.getVersionObj(sysAppVersion);
        addInfo.setCreateTime(new Date());
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        addInfo.setAppUpdateTime(DateUtils.DateToString(new Date()));
        this.manAppVersionDao.insert(addInfo);
    }

    /**
     * 修改app版本信息
     * id是唯一标识*/
    public void editVersion(ManAppVersionRequest sysAppVersion) throws ServiceExceptionSpec {
        SysAppVersion searchInfo = this.getVersionObj(sysAppVersion);
        searchInfo.setId(sysAppVersion.getId());

        searchInfo.setUpdateTime(new Date());
        int i = this.manAppVersionDao.update(searchInfo);
        if (i < 1) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
    }

    /**
     * 查询app版本信息列表
     * status 1.启用 2.禁用
     */
    public PageData versionList(ManAppVersionListRequest listRequest) throws ServiceExceptionSpec {

        PageData result = new PageData();
        List<SysAppVersion> data = this.manAppVersionDao.appVersionList(listRequest);
        if (CollectionUtils.isEmpty(data)) {
            result.setData(new ArrayList<>());
            result.setPageNo(listRequest.getPageNo());
            result.setPageSize(listRequest.getPageSize());
            result.setRecordsTotal(0);
        }
        List<AppVersionListResponse> rList = new ArrayList<>();
        for (SysAppVersion sysAppVersion : data) {

            AppVersionListResponse response = new AppVersionListResponse();
            BeanUtils.copyProperties(sysAppVersion, response);
            response.setTitle(sysAppVersion.getUpdateTitle());
            response.setMemo(sysAppVersion.getUpdateContent());
            response.setVersionNo(sysAppVersion.getAppVersion());
            response.setIsForce(sysAppVersion.getIsForceUpdate());
            response.setLeftButton(sysAppVersion.getLeftBtnTitle());
            response.setRightButton(sysAppVersion.getRightBtnTitle());
            rList.add(response);
        }
        Integer resultCount = this.manAppVersionDao.appVersionListCount(listRequest);
        result.setData(rList);
        result.setPageNo(listRequest.getPageNo());
        result.setPageSize(listRequest.getPageSize());
        result.setRecordsTotal(resultCount);
        return result;
    }

    /**
     * 获取添加修改公共参数
     */
    public SysAppVersion getVersionObj(ManAppVersionRequest versionRequest) {
        SysAppVersion appVersion = new SysAppVersion();
        appVersion.setIsForceUpdate(versionRequest.getIsForce());
        appVersion.setDownloadAddress(versionRequest.getDownloadAddress());
        appVersion.setLeftBtnTitle(versionRequest.getLeftButton());
        appVersion.setRightBtnTitle(versionRequest.getRightButton());
        appVersion.setUpdateContent(versionRequest.getMemo());
        appVersion.setStatus(versionRequest.getStatus());
        appVersion.setAppVersion(versionRequest.getVersionNo());
        appVersion.setAppType(versionRequest.getAppType());
        appVersion.setUpdateTitle(versionRequest.getTitle());
        appVersion.setSysType(versionRequest.getSysType());
        appVersion.setUpdateTime(new Date());
        return appVersion;
    }
}
