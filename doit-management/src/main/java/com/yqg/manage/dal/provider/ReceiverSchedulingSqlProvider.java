package com.yqg.manage.dal.provider;

import com.yqg.common.utils.DateUtils;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: microservice
 * @description: 催收人员排班SQL
 * @author: 许金泉
 * @create: 2019-04-16 11:29
 **/
@Slf4j
public class ReceiverSchedulingSqlProvider {


    public String getReceiverSchedulingList(ReceiverSchedulingRequest searchRequest) {
        StringBuilder selectSql = new StringBuilder("select * from receiverScheduling  " + "where disabled=0  ");
        StringBuilder conditionSql = this.generateConditionAllOrd(searchRequest);
        conditionSql.append(this.byPageSql(searchRequest));
        selectSql.append(conditionSql);
        return selectSql.toString();
    }

    public String getReceiverSchedulingCount(ReceiverSchedulingRequest searchRequest) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from receiverScheduling  where disabled=0   ");
        StringBuilder conditionSql = this.generateConditionAllOrd(searchRequest);
        selectSql.append(conditionSql);
        return selectSql.toString();
    }


    /**
     * 产生分页sql
     *
     * @param searchRequest
     * @return
     */
    private StringBuilder byPageSql(ReceiverSchedulingRequest searchRequest) {
        StringBuilder conditionSql = new StringBuilder();
        if (searchRequest.getPageNo() != null && searchRequest.getPageSize() != null) {
            Integer num = (searchRequest.getPageNo() - 1) * searchRequest.getPageSize();
            conditionSql.append(" limit ").append(num.toString()).append(",").append(searchRequest.getPageSize().toString());
        }
        return conditionSql;
    }

    private StringBuilder generateConditionAllOrd(ReceiverSchedulingRequest searchRequest) {
        StringBuilder conditionSql = new StringBuilder();

        if (StringUtils.isNotBlank(searchRequest.getUserName())) {
            conditionSql.append(" and  userName = #{searchRequest.userName}  ");
        }

        if (searchRequest.getWork()!=null) {
            conditionSql.append(" and  work = #{searchRequest.work}  ");
        }
        if (searchRequest.getStartTime() != null) {
            conditionSql.append(" and  workTime >= '" + DateUtils.DateToString(searchRequest.getStartTime()) + "'  ");
        }
        if (searchRequest.getEndTime() != null) {
            conditionSql.append(" and  workTime <='" +  DateUtils.DateToString(searchRequest.getEndTime()) + "'  ");
        }

        if (searchRequest.getWorkTime() != null) {
            conditionSql.append(" and  workTime ='" +  DateUtils.DateToString(searchRequest.getWorkTime()) + "'  ");
        }

        return conditionSql;
    }


}
