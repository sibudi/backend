package com.yqg.manage.dal.provider;


import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tonggen
 */
public class SmsTemplateProvider {

    private Logger logger = LoggerFactory.getLogger(SmsTemplateProvider.class);

    public String collectSmsTempList(SmsTemplateRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("SELECT id, sender, loaner, receiver, smsTitle, smsContent, isArrived, sendTime " +
                "FROM smsOneStepStatistics where disabled = '0' " );

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest).append(" order by createTime DESC ");

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String collectSmsTempListCount(String nowTime, String orderNo) {
        StringBuilder selectSql = new StringBuilder("SELECT count(*) " +
                "FROM smsOneStepStatistics where disabled = '0' and updateTime >='" + nowTime + " 00:00:00 ' " +
                "and updateTime <='" + nowTime + " 23:59:59 ' and orderNo = #{orderNo}");

//        logger.info(selectSql.toString());
        return selectSql.toString();
    }
    private StringBuilder generateConditionAllOrd(SmsTemplateRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();

        if (StringUtils.isNotBlank(searchResquest.getSmsTitle())) {
            conditionSql.append(" and smsTitle = #{SmsTemplateRequest.smsTitle}");
        }
        if (StringUtils.isNotBlank(searchResquest.getSendUser())) {
            conditionSql.append(" and sender = #{SmsTemplateRequest.sendUser}");
        }
        if (searchResquest.getIsArrived() != null) {
            conditionSql.append(" and isArrived = #{SmsTemplateRequest.isArrived} ");
        }

        if (StringUtils.isNotBlank(searchResquest.getMinSendTime())) {
            conditionSql.append(" and sendTime >= '" + searchResquest.getMinSendTime() + " 00:00:00 '");
        }
        if (StringUtils.isNotBlank(searchResquest.getMaxSendTime())) {
            conditionSql.append(" and sendTime <= '" + searchResquest.getMaxSendTime() + " 23:59:59 '");
        }
        return conditionSql;
    }


}
