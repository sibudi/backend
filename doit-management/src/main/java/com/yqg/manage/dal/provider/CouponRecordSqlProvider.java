package com.yqg.manage.dal.provider;


import com.yqg.manage.service.order.request.CouponRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alan
 */
public class CouponRecordSqlProvider {

    private Logger logger = LoggerFactory.getLogger(CouponRecordSqlProvider.class);

    public String listCouponRecord(CouponRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select * from couponRecord record join couponConfig config on record.couponConfigId = config.id " +
                "where ");

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest).append(" order by record.createTime desc ");

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }


    private StringBuilder generateConditionAllOrd(CouponRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();
        if (StringUtils.isNotBlank(searchResquest.getAlias())) {
            conditionSql.append(" config.alias = #{CouponRequest.alias} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getIndonisaName())) {
            conditionSql.append(" config.indonisaName = #{CouponRequest.indonisaName} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getOrderNo())) {
            conditionSql.append(" record.orderNo = #{CouponRequest.orderNo} and ");
        }
        if (searchResquest.getMoney() != null) {
            conditionSql.append(" record.money = #{CouponRequest.money} and ");
        }
        if (searchResquest.getStatus() != null) {
            conditionSql.append(" record.status = #{CouponRequest.status} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getSendStartTime())) {
            conditionSql.append(" record.createTime >= '"+ searchResquest.getSendStartTime() + " 00:00:00' and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getSendEndTime())) {
            conditionSql.append(" record.createTime <= '" + searchResquest.getSendEndTime() + " 23:59:59' and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getUseStartTime())) {
            conditionSql.append(" record.usedDate >= '"+ searchResquest.getUseStartTime() + " 00:00:00' and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getUseEndTime())) {
            conditionSql.append(" record.usedDate <= '" + searchResquest.getUseEndTime() + " 23:59:59' and ");
        }
        conditionSql.append(" config.disabled = 0 and record.disabled = 0 and config.status = 0 ");
        return conditionSql;
    }

}



