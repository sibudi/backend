package com.yqg.manage.dal.provider;


import com.yqg.manage.service.order.request.OverdueOrderRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author alan
 */
public class QualityCheckSqlProvider {

    private Logger logger = LoggerFactory.getLogger(QualityCheckSqlProvider.class);

    public String listQualityChecks(OverdueOrderRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select oo.uuid AS orderNo," +
                "oo.amountApply,oo.orderType," +
                "oo.borrowingTerm," +
                "oo.refundTime," +
                "oo.borrowingCount, coll.promiseRepaymentTime, " +
                "coll.orderTag,coll.checkResult,coll.voiceCheckResult,c.createTime as followTime,b.createTime as updateTime," +
                "usr.realName," +
                "usr.userRole, usr.payDay, " +
                "usr.sex as userSex, coll1.outsourceId as collectiorId," +
                "usr.uuid from ordOrder oo " +
                "LEFT JOIN usrUser usr ON usr.uuid = oo.userUuid " +
                "JOIN collectionOrderDetail coll ON coll.orderUUID = oo.uuid " +
                "LEFT JOIN (select orderNo, max(createTime) as createTime from doit.manQualityCheckRecord where disabled =0 group by orderNo ) b ON coll.orderUuid = b.orderNo " +
                "LEFT JOIN (select orderNo, max(createTime) as createTime from doit.manCollectionRemark where disabled=0 and orderTag !=0 group by orderNo ) c ON coll.orderUuid = c.orderNo "+
                "LEFT JOIN collectionOrderDetail coll1 on coll1.orderUUID = oo.uuid and coll1.disabled = 0 and coll1.sourceType = 0 " +
                "WHERE " +
                "oo.disabled = 0 " +
                "AND usr.disabled = 0 " +
                "AND coll.disabled = 0 and coll.sourceType = 1" );

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest).append(" order by oo.applyTime desc,oo.id desc ");

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    private StringBuilder generateConditionAllOrd(OverdueOrderRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();

        conditionSql.append(" and coll.outsourceId = #{OverdueOrderRequest.outsourceId}");

        if (searchResquest.getCollectionInQualityCheckId() != null) {
            conditionSql.append(" and coll1.outsourceId = #{OverdueOrderRequest.collectionInQualityCheckId}");
        }
        if (StringUtils.isNotBlank(searchResquest.getUuid())) {
            conditionSql.append(" and oo.uuid = #{OverdueOrderRequest.uuid}");
        }
        if (StringUtils.isNotBlank(searchResquest.getRealName())) {
            conditionSql.append(" and usr.realname = #{OverdueOrderRequest.realName} ");
        }
        if (searchResquest.getBorrowingTerm() != null) {
            conditionSql.append(" and oo.borrowingTerm = #{OverdueOrderRequest.borrowingTerm} ");
        }
        if (searchResquest.getAmountApply() != null) {
            conditionSql.append(" and oo.amountApply = #{OverdueOrderRequest.amountApply} " );
        }

        if(searchResquest.getOrderTag()!=null){
            conditionSql.append(" and coll.orderTag=#{OverdueOrderRequest.orderTag}");
        }
        if (searchResquest.getPayDay() != null
                && !searchResquest.getPayDay().equals(0)) {
            conditionSql.append(" and usr.payDay=#{OverdueOrderRequest.payDay}");
        }
        //质检开始时间
        if (StringUtils.isNotBlank(searchResquest.getUpdateBeginTime())) {
            conditionSql.append(" and DATE_FORMAT(b.createTime,'%Y-%m-%d') >= '" + searchResquest.getUpdateBeginTime() + "' ");
        }
        if (StringUtils.isNotBlank(searchResquest.getUpdateEndTime())) {
            conditionSql.append(" and DATE_FORMAT(b.createTime,'%Y-%m-%d') <= '" + searchResquest.getUpdateEndTime() + "' ");
        }
        //订单跟进时间
        if (StringUtils.isNotBlank(searchResquest.getDueDayStartTime())) {
            conditionSql.append(" and DATE_FORMAT(c.createTime,'%Y-%m-%d') >= '" + searchResquest.getDueDayStartTime() + "' ");
        }
        if (StringUtils.isNotBlank(searchResquest.getDueDayEndTime())) {
            conditionSql.append(" and DATE_FORMAT(c.createTime,'%Y-%m-%d') <= '" + searchResquest.getDueDayEndTime() + "' ");
        }
        return conditionSql;
    }

    public String getTotalData(String collectors, Integer checkerId, String startTime, String endTime){
        StringBuilder selectSql = new StringBuilder("select record.collectorId , record.checkTag ,record.type,usr.username as collector, count(record.checkTag) as questionCount, sum(config.fineMoney) as fineMoneys, config.title , config.titleInn from manQualityCheckRecord record " +
                "left join manUser usr on usr.id = record.collectorId and usr.disabled = 0 join manQualityCheckConfig config on config.id = record.checkTag where config.disabled =0 and record.disabled = 0");

        StringBuilder conditionSql = this.generateConditionAllOrd1(collectors, checkerId, startTime, endTime);

        selectSql.append(conditionSql);
        selectSql.append(" group by 1,2,3").append(" order by usr.id ");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String getDetailData(String collectors, Integer checkerId, String startTime, String endTime){
        StringBuilder selectSql = new StringBuilder("select record.orderNo as orderNo,record.type as type, record.checkTag as checkTag, record.userUuid as userName, record.collectorId as collectorId, DATE_FORMAT(record.createTime, '%Y-%m-%d') as days, record.createUser as operator, record.createTime as createTime, record.remark as remark " +
                "from manQualityCheckRecord record where record.disabled = 0 ");

        StringBuilder conditionSql = this.generateConditionAllOrd1(collectors, checkerId, startTime, endTime).append(" order by record.createTime desc ");

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String getQualityDataSheet3(String collectors, Integer checkerId, String startTime, String endTime){
        StringBuilder selectSql = new StringBuilder("SELECT\n" +
                "\toutsourceId,\n" +
                "\tcount( outsourceId ) AS totalOrderCount,\n" +
                "\ta.qulityOrderCount,\n" +
                "\ta.remarkOrderCount,\n" +
                "concat(format(a.qulityOrderCount/count( outsourceId ) *100,1),'%')as qualityCheckRate,\t\n" +
                "concat(format(a.remarkOrderCount/count( outsourceId ) *100,1),'%')as avgQualityCheckRate,\n" +
                "\t\tb.voiceQulityOrderCount,\n" +
                "\tb.voiceRemarkOrderCount,\n" +
                "\tconcat( format( b.voiceQulityOrderCount / count( outsourceId ) * 100, 1 ), '%' ) AS voiceQualityCheckRate,\n" +
                "\tconcat( format( b.voiceRemarkOrderCount / count( outsourceId ) * 100, 1 ), '%' ) AS voiceAvgQualityCheckRate " +
                "FROM\n" +
                "\tdoit.collectionOrderHistory coll\n" +
                "\tleft JOIN (\n" +
                "\tSELECT\n" +
                "\t  createUser,\n" +
                "\t\tcount( DISTINCT ( orderNo ) ) AS qulityOrderCount,\n" +
                "\t\tcount( orderNo ) AS remarkOrderCount\n" +
                "\tFROM\n" +
                "\t\tdoit.manQualityCheckRecord \n" +
                "\tWHERE\n" +
                "\t\tdisabled = 0 ");
        if (StringUtils.isNotBlank(collectors)) {
            selectSql.append(" and collectorId in (").append(collectors).append(")");
        }

        selectSql.append(" and Date_FORMAT(createTime,'%Y-%m-%d') >= '" + startTime + "'");
        selectSql.append(" and Date_FORMAT(createTime,'%Y-%m-%d') <= '" + endTime + "'");
        selectSql.append(" and type = 0 ");

        selectSql.append(" group by 1 ) a ON a.createUser = coll.outSourceId ");
        //新增语音质检
        selectSql.append("\tleft JOIN (\n" +
                "\tSELECT\n" +
                "\t  createUser,\n" +
                "\t\tcount( DISTINCT ( orderNo ) ) AS voiceQulityOrderCount,\n" +
                "\t\tcount( orderNo ) AS voiceRemarkOrderCount\n" +
                "\tFROM\n" +
                "\t\tdoit.manQualityCheckRecord \n" +
                "\tWHERE\n" +
                "\t\tdisabled = 0 ");
        if (StringUtils.isNotBlank(collectors)) {
            selectSql.append(" and collectorId in (").append(collectors).append(")");
        }

        selectSql.append(" and Date_FORMAT(createTime,'%Y-%m-%d') >= '" + startTime + "'");
        selectSql.append(" and Date_FORMAT(createTime,'%Y-%m-%d') <= '" + endTime + "'");
        selectSql.append(" and type = 1 ");

        selectSql.append(" group by 1 ) b ON b.createUser = coll.outSourceId ");

        selectSql.append("where coll.disabled = 0 \n" +
                "\tAND coll.sourceType = 1");

        if (checkerId != null && !checkerId.equals(0)) {
            selectSql.append(" and coll.outSourceId = #{checkerId}");
        }
        if (StringUtils.isNotBlank(collectors)) {
            selectSql.append(" and coll.orderUUid in (select orderUuid from doit.collectionOrderHistory " +
                    "where outsourceId in (").append(collectors).append(")").append(" and sourceType = 0 and disabled = 0)");
        }
        selectSql.append(" and Date_FORMAT(coll.createTime,'%Y-%m-%d') >= '" + startTime + "'");
        selectSql.append(" and Date_FORMAT(coll.createTime,'%Y-%m-%d') <= '" + endTime + "'");
        selectSql.append(" group by 1 ");
        logger.info("getQualityDataSheet3 sql is : " + selectSql.toString());
        return selectSql.toString();
    }

    private StringBuilder generateConditionAllOrd1(String collectors, Integer checkerId, String startTime, String endTime){
        StringBuilder conditionSql = new StringBuilder();
        if (StringUtils.isNotBlank(collectors)) {
            conditionSql.append(" and record.collectorId in (").append(collectors).append(")");
        }
        if (checkerId != null && !checkerId.equals(0)) {
            conditionSql.append(" and record.createUser = #{checkerId}");
        }
        conditionSql.append(" and Date_FORMAT(record.createTime,'%Y-%m-%d') >= '" + startTime + "'");
        conditionSql.append(" and Date_FORMAT(record.createTime,'%Y-%m-%d') <= '" + endTime + "'");
        return conditionSql;
    }

}
