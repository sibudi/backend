package com.yqg.manage.dal.provider;


import com.yqg.manage.service.order.request.ManOrderRemarkRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alan
 */
public class OrderRemarkSqlProvider {

    private Logger logger = LoggerFactory.getLogger(OrderRemarkSqlProvider.class);

    public String orderRemarkList(ManOrderRemarkRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select id from manOrderRemark " +
                "where ");

        StringBuilder conditionSql = this.generateConditionRemarkList(searchResquest).append(" order by createTime desc ");

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    private StringBuilder generateConditionRemarkList(ManOrderRemarkRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();
        if (searchResquest.getCreateUser() != null) {
            conditionSql.append(" createUser = #{ManOrderRemarkRequest.createUser} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getOrderNo())) {
            conditionSql.append(" orderNo = #{ManOrderRemarkRequest.orderNo} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getRemark())) {
            conditionSql.append(" remark = #{ManOrderRemarkRequest.remark} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getAlertTime())) {
            conditionSql.append(" alertTime = '"+ searchResquest.getAlertTime() + ":00' and ");
        }
        if (searchResquest.getOrderTag() != null) {
            conditionSql.append(" orderTag = #{ManOrderRemarkRequest.orderTag} and ");

        }
        if (!StringUtils.isEmpty(searchResquest.getStartTime())) {
            conditionSql.append(" startTime = '"+ searchResquest.getStartTime() + "' and ");
        }
        if (!StringUtils.isEmpty(searchResquest.getEndTime())) {
            conditionSql.append(" endTime = '"+ searchResquest.getEndTime() + "' and ");
        }
        if (!StringUtils.isEmpty(searchResquest.getBurningTime())) {
            conditionSql.append(" burningTime = #{ManOrderRemarkRequest.burningTime} and ");
        }
        conditionSql.append(" disabled = 0 ");
        return conditionSql;
    }




}
