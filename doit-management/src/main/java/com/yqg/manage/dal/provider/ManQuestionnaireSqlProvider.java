package com.yqg.manage.dal.provider;

import com.yqg.common.utils.StringUtils;
import com.yqg.service.user.request.UsrQuestionnaireListRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 11:05 AM
 */
@Slf4j
public class ManQuestionnaireSqlProvider {

    public String listQuestionnaire (UsrQuestionnaireListRequest request) {

        StringBuffer result = new StringBuffer("select q.uuid, q.userUuid,q.type,q.createTime,q.state,q.checker as checker " +
                "from usrQuestionnaire q where q.disabled = 0 ");

        if (request.getType() != null) {
            result.append(" and q.type = #{request.type}");
        }
        if (request.getState() != null) {
            result.append(" and q.state = #{request.state}");
        }
        if (StringUtils.isNotEmpty(request.getCreateTimeBegin())) {
            result.append(" and q.createTime >= '" + request.getCreateTimeBegin() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getCreateTimeEnd())) {
            result.append(" and q.createTime <= '" + request.getCreateTimeEnd() + " 23:59:59'");
        }
        if (StringUtils.isNotEmpty(request.getUserUuid())) {
            result.append(" and q.userUuid = #{request.userUuid}");
        }

        log.info("listQuestionnaire sql is {}", result.toString());

        return result.toString();
    }
}
