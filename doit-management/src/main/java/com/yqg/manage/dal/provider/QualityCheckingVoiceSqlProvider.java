package com.yqg.manage.dal.provider;

import com.yqg.manage.service.check.request.QualityCheckingVoiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: microservice
 * @description: 质检语音的SQL
 * @author: 许金泉
 * @create: 2019-04-03 11:29
 **/
@Slf4j
public class QualityCheckingVoiceSqlProvider {

    private final List<String> allowOrderByField = Arrays.asList("recordBeginTime", "recordEndTime", "recordLength");

    public String getQualityCheckingVoiceList(QualityCheckingVoiceRequest searchRequest) {
        StringBuilder selectSql = new StringBuilder("select realName,userName,orderNo,id,uuid,extNumber,remark,destNumber as phone,attachmentSavePath,applyAmount," + "applyDeadline,createUser,createTime,updateUser,updateTime,answerStartTime,recordBeginTime,recordEndTime,recordLength from qualityCheckingVoice  " + "where disabled=0 and recordLength != 0 ");

        StringBuilder conditionSql = this.generateConditionAllOrd(searchRequest);
        if (searchRequest.getOrderFields() != null) {
            String orderBySql = searchRequest.getOrderFields().stream().filter(u -> allowOrderByField.contains(u.getOrderField())).map(u -> u.getOrderBySQL()).collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(orderBySql)) {
                conditionSql.append(" order by ").append(orderBySql);
            }
        }
        conditionSql.append(this.byPageSql(searchRequest));
        selectSql.append(conditionSql);
        return selectSql.toString();
    }

    public String getQualityCheckingVoiceCount(QualityCheckingVoiceRequest searchRequest) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from qualityCheckingVoice  where disabled=0 and recordLength != 0  ");
        StringBuilder conditionSql = this.generateConditionAllOrd(searchRequest);
        selectSql.append(conditionSql);
        return selectSql.toString();
    }

    public String getAttachmentSavePathByUuidList(List<String> uuidList) {
        String inListSQL = uuidList.stream().map(u -> "'" + u + "'").collect(Collectors.joining(","));
        return "select attachmentSavePath  from qualityCheckingVoice where uuid in (" + inListSQL + ")   ";
    }


    /**
     * 产生分页sql
     *
     * @param searchRequest
     * @return
     */
    private StringBuilder byPageSql(QualityCheckingVoiceRequest searchRequest) {
        StringBuilder conditionSql = new StringBuilder();
        if (searchRequest.getPageNo() != null && searchRequest.getPageSize() != null) {
            Integer num = (searchRequest.getPageNo() - 1) * searchRequest.getPageSize();
            conditionSql.append(" limit ").append(num.toString()).append(",").append(searchRequest.getPageSize().toString());
        }
        return conditionSql;
    }

    private StringBuilder generateConditionAllOrd(QualityCheckingVoiceRequest searchRequest) {
        StringBuilder conditionSql = new StringBuilder();
        if (StringUtils.isNotBlank(searchRequest.getOrderNo())) {
            conditionSql.append(" and  orderNo = #{searchRequest.orderNo}  ");
        }
        if (StringUtils.isNotBlank(searchRequest.getRealName())) {
            conditionSql.append(" and  realname = #{searchRequest.realName}  ");
        }
        if (StringUtils.isNotBlank(searchRequest.getUserName())) {
            conditionSql.append(" and  userName = #{searchRequest.userName}  ");
        }
        if (StringUtils.isNotBlank(searchRequest.getRecordBeginTime())) {
            conditionSql.append(" and  recordBeginTime >= '" + searchRequest.getRecordBeginTime() + " 00:00:00'  ");
        }
        if (StringUtils.isNotBlank(searchRequest.getRecordEndTime())) {
            conditionSql.append(" and  recordEndTime <='" + searchRequest.getRecordEndTime() + " 23:59:59'  ");
        }

        if (StringUtils.isNotBlank(searchRequest.getPhone())) {
            conditionSql.append(" and destNumber = #{searchRequest.phone}");
        }

        return conditionSql;
    }


}
