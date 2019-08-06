package com.yqg.manage.dal.provider;

import com.yqg.manage.service.system.request.ManAppVersionListRequest;
import lombok.extern.log4j.Log4j;

/**
 * @author alan
 */
@Log4j
public class SysAppVersionSqlProvider {
    public String appList(ManAppVersionListRequest versionListRequest) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select id,appUpdateTime,createTime,disabled,downloadAddress");
        selectSql.append(",isForceUpdate,leftBtnTitle,updateContent,remark,rightBtnTitle,status");
        selectSql.append(",updateTitle,updateTime,uuid,appVersion,appType,sysType from");
        selectSql.append(" sysAppVersion where ");

        StringBuilder conditionSql = this.generateCondition(versionListRequest);
        if (versionListRequest.getPageNo() != null && versionListRequest.getPageSize() != null) {
            Integer num = (versionListRequest.getPageNo() - 1) * versionListRequest.getPageSize();
            conditionSql.append(" limit ").append(num.toString()).append(",")
                    .append(versionListRequest.getPageSize().toString());
        }
        selectSql.append(conditionSql);

        log.debug(selectSql.toString());
        return selectSql.toString();
    }

    public String appListCount(ManAppVersionListRequest versionListRequest) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from sysAppVersion where");

        StringBuilder conditionSql = this.generateCondition(versionListRequest);
        selectSql.append(conditionSql);

        return selectSql.toString();
    }

    private StringBuilder generateCondition(ManAppVersionListRequest versionListRequest) {
        StringBuilder conditionSql = new StringBuilder();
        if (versionListRequest.getAppType() != null) {
            conditionSql.append(" appType = #{ManAppVersionListRequest.appType} and");
        }
        if (versionListRequest.getStatus() != null) {
            conditionSql.append(" status = #{ManAppVersionListRequest.status} and");
        }
        conditionSql.append(" disabled = 0 order by updateTime desc");

        return conditionSql;
    }
}
