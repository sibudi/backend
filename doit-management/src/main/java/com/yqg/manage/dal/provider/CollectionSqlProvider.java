package com.yqg.manage.dal.provider;


import com.yqg.common.utils.DESUtils;
import com.yqg.manage.service.collection.request.AssignableCollectionOrderReq;
import com.yqg.manage.service.order.request.OrderSearchRequest;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.service.order.request.PaidOrderRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author alan
 */
public class CollectionSqlProvider {

    private Logger logger = LoggerFactory.getLogger(CollectionSqlProvider.class);

    public String outCollectionsList(OverdueOrderRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select oo.uuid AS orderNo," +
                "oo.amountApply," +
                "oo.borrowingTerm," +
                "oo.refundTime," +
                "oo.borrowingCount," +
                "oo.orderType," +
                "coll.orderTag," +
                "usr.realName," +
                "usr.userRole," +
                "usr.sex as userSex," +
                "usr.uuid from ordOrder oo " +
                "LEFT JOIN usrUser usr ON usr.uuid = oo.userUuid " +
                "LEFT JOIN collectionOrderDetail coll ON coll.orderUUID = oo.uuid " +
                "WHERE " +
                "oo.disabled = 0 " +
                "AND usr.disabled = 0 " +
                "AND coll.disabled = 0 and coll.sourceType=#{OverdueOrderRequest.sourceType} " );

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest).append(" order by oo.applyTime desc,oo.id desc ");

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String outCollectionsByOutSourceIdList(OverdueOrderRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select oo.uuid AS orderNo," +
                "oo.amountApply,oo.orderType," +
                "oo.borrowingTerm," +
                "oo.refundTime," +
                "oo.borrowingCount, coll.promiseRepaymentTime, " +
                "coll.orderTag," +
                "usr.realName,man.username as collectiorName, " +
                "usr.userRole, usr.payDay, " +
                "usr.sex as userSex," +
                "usr.uuid from ordOrder oo " +
                "LEFT JOIN usrUser usr ON usr.uuid = oo.userUuid " +
                "JOIN collectionOrderDetail coll ON coll.orderUUID = oo.uuid left join manUser man on man.disabled = 0 ");
                StringBuilder tempSql = new StringBuilder();
        if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 1) {
            tempSql.append(" and man.id = coll.subOutSourceId ");
        } else {
            tempSql.append(" and man.id = coll.outsourceId");
        }
        selectSql.append(tempSql);
         selectSql.append(" WHERE " +
                 "oo.disabled = 0 " +
                 "AND usr.disabled = 0 " +
                 "AND coll.disabled = 0 and coll.sourceType = #{OverdueOrderRequest.sourceType}");

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest).append(" order by oo.applyTime desc,oo.id desc ");

        selectSql.append(conditionSql);
        logger.info(selectSql.toString());
        return selectSql.toString();
    }


    public String payDayCount(OverdueOrderRequest searchResquest){

        StringBuilder selectSql = new StringBuilder("select count(*) " +
                " from ordOrder oo " +
                "LEFT JOIN usrUser usr ON usr.uuid = oo.userUuid " +
                "JOIN collectionOrderDetail coll ON coll.orderUUID = oo.uuid " +
                "WHERE " +
                "oo.disabled = 0 " +
                "AND usr.disabled = 0 " +
                "AND coll.disabled = 0 and coll.sourceType = #{OverdueOrderRequest.sourceType}" );

        if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 1) {
            selectSql.append(" and coll.subOutSourceId = #{OverdueOrderRequest.outsourceId}");
        } else if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 0){
            selectSql.append(" and coll.outsourceId = #{OverdueOrderRequest.outsourceId}");
        }
        if (searchResquest.getPayDay() != null
                && !searchResquest.getPayDay().equals(0)) {
            selectSql.append(" and usr.payDay=#{OverdueOrderRequest.payDay}");
        }

        selectSql.append(" and (oo.`status` = 8 or oo.`status` = 7) ");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String manOrderRemarkList(OrderSearchRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select remark.createTime,remark.promiseRepaymentTime,usr.realName AS operator,remark.remark,remark.orderTag,remark.alertTime from " +
                "manOrderRemark remark JOIN manUser usr ON usr.id = remark.createUser " +
                "WHERE usr.disabled = 0 AND remark.disabled = 0 AND remark.type = 2 and remark.orderNo = #{OrderSearchRequest.uuid} order by remark.createTime ASC");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String manOrderRemarkListNew(OrderSearchRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select remark.createTime,remark.promiseRepaymentTime,usr.realName AS operator,remark.remark,remark.orderTag,remark.alertTime , remark.contactMobile, remark.contactResult, remark.contactMode from " +
                "manCollectionRemark remark JOIN manUser usr ON usr.id = remark.createUser " +
                "WHERE usr.disabled = 0 AND remark.disabled = 0 and remark.orderNo = #{OrderSearchRequest.uuid} order by remark.createTime ASC");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String repaymentOrderList(PaidOrderRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select oo.uuid AS orderNo,oo.applyTime,oo.orderType,oo.amountApply,oo.borrowingTerm," +
                "oo.refundTime,oo.actualRefundTime,usr.realName,usr.userRole,usr.uuid from " +
                "ordOrder oo LEFT JOIN usrUser usr ON usr.uuid = oo.userUuid " +
                "WHERE " +
                "oo.disabled = 0 "  );

        StringBuilder conditionSql = this.generateConditionCompleteOrd(searchResquest).append(" order by oo.applyTime desc,oo.id desc ");
        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String repaymentOrderByOutSourceIdList(PaidOrderRequest searchResquest){
        StringBuilder selectSql = new StringBuilder("select oo.uuid AS orderNo,oo.orderType,oo.applyTime,oo.amountApply,oo.borrowingTerm," +
                "oo.refundTime,oo.actualRefundTime,usr.realName,usr.userRole,usr.uuid, man.username as collectiorName from " +
                "ordOrder oo LEFT JOIN usrUser usr ON usr.uuid = oo.userUuid inner join collectionOrderDetail coll on coll.orderUUID = oo.uuid left join manUser man on man.disabled = 0 ");
                StringBuilder tempSql = new StringBuilder();
                if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 1) {
                    tempSql.append(" and man.id = coll.subOutSourceId ");
                } else {
                    tempSql.append(" and man.id = coll.outsourceId");
                }
                selectSql.append(tempSql);
                selectSql.append(" WHERE " +
                "oo.disabled = 0 " +
                "AND coll.disabled = 0 and coll.sourceType = #{PaidOrderRequest.sourceType}" );

        StringBuilder conditionSql = this.generateConditionCompleteOrd(searchResquest).append(" order by oo.applyTime desc,oo.id desc ");
        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    private StringBuilder generateConditionAllOrd(OverdueOrderRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();

        if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 1) {
            conditionSql.append(" and coll.subOutSourceId = #{OverdueOrderRequest.outsourceId}");
        } else if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 0){
            conditionSql.append(" and coll.outsourceId = #{OverdueOrderRequest.outsourceId}");
        }

        if (StringUtils.isNotBlank(searchResquest.getUuid())) {
            conditionSql.append(" and oo.uuid = #{OverdueOrderRequest.uuid}");
        }
        if (StringUtils.isNotBlank(searchResquest.getMobile())) {
            conditionSql.append(" and usr.mobileNumberDES= '" + DESUtils.encrypt(searchResquest.getMobile())+"'");
        }
        if (StringUtils.isNotBlank(searchResquest.getRealName())) {
            conditionSql.append(" and usr.realName = #{OverdueOrderRequest.realName} ");
        }
        if (searchResquest.getBorrowingTerm() != null) {
            conditionSql.append(" and oo.borrowingTerm = #{OverdueOrderRequest.borrowingTerm} ");
        }
        if (searchResquest.getAmountApply() != null) {
            conditionSql.append(" and oo.amountApply = #{OverdueOrderRequest.amountApply} " );
        }

        if (searchResquest.getOrderType() != null) {
            conditionSql.append(" and oo.orderType = #{OverdueOrderRequest.orderType} " );
        }
        if(searchResquest.getMinOverdueDays()!=null){
            conditionSql.append(" and datediff(now(),oo.refundTime)>=#{OverdueOrderRequest.minOverdueDays}");
        }

        if(searchResquest.getMaxOverdueDays()!=null){
            conditionSql.append(" and datediff(now(),oo.refundTime)<=#{OverdueOrderRequest.maxOverdueDays}");
        }

        if(searchResquest.getOrderTag()!=null){
            conditionSql.append(" and coll.orderTag=#{OverdueOrderRequest.orderTag}");
        }
        if (searchResquest.getPayDay() != null
                && !searchResquest.getPayDay().equals(0)) {
            conditionSql.append(" and usr.payDay=#{OverdueOrderRequest.payDay}");
        }
        if (searchResquest.getIsTerms() != null) {
            if (searchResquest.getIsTerms().equals(1)) {
                conditionSql.append(" and oo.orderType = 3 ");
            } else if (searchResquest.getIsTerms().equals(2)) {
                conditionSql.append(" and oo.orderType != 3 ");
            }
        }
        conditionSql.append(" and (oo.`status` = 8 or oo.`status` = 7)");
        return conditionSql;
    }

//    private StringBuilder generateConditionByTime(OverdueOrderRequest searchResquest){
//        StringBuilder conditionSql = new StringBuilder();
//
//        //1查询D0
//        if (searchResquest.getD0OrOverDue() != null && searchResquest.getD0OrOverDue() == 1) {
//            conditionSql.append(" and datediff(now(),oo.refundTime)=0");
//        } else if (searchResquest.getD0OrOverDue() != null && searchResquest.getD0OrOverDue() == 2){
//            conditionSql.append(" and datediff(now(),oo.refundTime)>0");
//        }
//
//        if (StringUtils.isNotBlank(searchResquest.getUuid())) {
//            conditionSql.append(" and oo.uuid = #{OverdueOrderRequest.uuid}");
//        }
//        if (StringUtils.isNotBlank(searchResquest.getRealName())) {
//            conditionSql.append(" and usr.realName = #{OverdueOrderRequest.realName} ");
//        }
//        if (searchResquest.getBorrowingTerm() != null) {
//            conditionSql.append(" and oo.borrowingTerm = #{OverdueOrderRequest.borrowingTerm} ");
//        }
//        if (searchResquest.getAmountApply() != null) {
//            conditionSql.append(" and oo.amountApply = #{OverdueOrderRequest.amountApply} " );
//        }
//
//        if (searchResquest.getOrderType() != null) {
//            conditionSql.append(" and oo.orderType = #{OverdueOrderRequest.orderType} " );
//        }
//
//        if(searchResquest.getOrderTag()!=null){
//            conditionSql.append(" and coll.orderTag=#{OverdueOrderRequest.orderTag}");
//        }
//        if (searchResquest.getPayDay() != null
//                && !searchResquest.getPayDay().equals(0)) {
//            conditionSql.append(" and usr.payDay=#{OverdueOrderRequest.payDay}");
//        }
//        conditionSql.append(" and (oo.`status` = 8 or oo.`status` = 7) ");
//        return conditionSql;
//    }
    private StringBuilder generateConditionCompleteOrd(PaidOrderRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();

        if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 1) {
            conditionSql.append("  and coll.subOutSourceId = #{PaidOrderRequest.outsourceId}");
        } else if (searchResquest.getIsThird() != null && searchResquest.getIsThird() == 0){
            conditionSql.append("  and coll.outsourceId = #{PaidOrderRequest.outsourceId}");
        }
        if (StringUtils.isNotBlank(searchResquest.getUuid())) {
            conditionSql.append(" and oo.uuid = #{PaidOrderRequest.uuid}");
        }
        if (StringUtils.isNotBlank(searchResquest.getMobile())) {
            conditionSql.append(" and usr.mobileNumberDES= '" + DESUtils.encrypt(searchResquest.getMobile())+"'");
        }
        if (StringUtils.isNotBlank(searchResquest.getRealName())) {
            conditionSql.append(" and usr.realName = #{PaidOrderRequest.realName} ");
        }

        if(StringUtils.isNotBlank(searchResquest.getActualPaymentStartDate())) {
            conditionSql.append(" and oo.actualRefundTime >= '" + searchResquest.getActualPaymentStartDate()+ " 00:00:00'");
        }
        if(StringUtils.isNotBlank(searchResquest.getActualPaymentEndDate())) {
            conditionSql.append(" and oo.actualRefundTime <= '" + searchResquest.getActualPaymentEndDate()+ " 23:59:59'");
        }
        if (searchResquest.getBorrowingTerm() != null) {
            conditionSql.append(" and oo.borrowingTerm = #{PaidOrderRequest.borrowingTerm} ");
        }
        if (searchResquest.getAmountApply() != null) {
            conditionSql.append(" and oo.amountApply = #{PaidOrderRequest.amountApply} " );
        }

        if(searchResquest.getMinOverdueDays()!=null){
            conditionSql.append(" and datediff(oo.actualRefundTime, oo.refundTime)>=#{PaidOrderRequest.minOverdueDays}");
        }

        if(searchResquest.getMaxOverdueDays()!=null){
            conditionSql.append(" and datediff(oo.actualRefundTime, oo.refundTime)<=#{PaidOrderRequest.maxOverdueDays}");
        }
        if (searchResquest.getOrderType() != null) {
            conditionSql.append(" and oo.orderType = #{PaidOrderRequest.orderType} ");
        }
        if (searchResquest.getIsTerms() != null) {
            if (searchResquest.getIsTerms().equals(1)) {
                conditionSql.append(" and oo.orderType = 3 ");
            } else if (searchResquest.getIsTerms().equals(2)) {
                conditionSql.append(" and oo.orderType != 3 ");
            }
        }

        conditionSql.append(" and (oo.`status` = 11 or oo.`status` = 10) ");
        return conditionSql;
    }


    public String getAssignableCollectionOrderList(AssignableCollectionOrderReq request){
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT "
                + "    o.uuid,"
                + "    o.applyTime,"
                + "    o.borrowingCount,"
                + "    o.borrowingTerm,"
                + "    o.amountApply,"
                + "    o.status,"
                + "    o.refundTime,"
                + "    o.actualRefundTime,"
                + "    u.realName, u.uuid as userUuid, "
                + "    c.isTest,"
                + "    c.orderTag, c.promiseRepaymentTime, "
                + "    c.outsourceId, c.subOutSourceId, o.orderType"
                + " FROM "
                + "    ordOrder o "
                + "        JOIN "
                + "    usrUser u ON u.uuid = o.userUuid AND o.disabled = 0 "
                + "        LEFT JOIN "
                + "    collectionOrderDetail c ON c.orderUUID = o.uuid AND c.disabled = 0 and c.sourceType = 0 "
                + " WHERE o.disabled = 0 " );// and DATE_FORMAT(o.refundTime,'%Y-%m-%d')<='"+ com.yqg.manage.util.DateUtils.getStrCurrentDate()+"' ");

        if(request.getStatus()!=null){
            sql.append(" and o.status = "+request.getStatus().getCode());
        }
        if (request.getOrderType() != null) {
            sql.append(" and o.orderType = #{AssignableCollectionOrderReq.orderType} ");
        }
        if(StringUtils.isNotEmpty(request.getUuid())){
            sql.append(" and o.uuid = #{AssignableCollectionOrderReq.uuid} ");
        }
        if (request.getIsRepeatBorrowing() != null) {
            if (request.getIsRepeatBorrowing() == 1) {
                sql.append(" and o.borrowingCount>1 ");
            } else {
                sql.append(" and o.borrowingCount<=1 ");
            }
        }

        if(request.getIsAssigned()!=null){
            if (request.getIsAssigned() == 1) {
                //已分配,collectionOrderDetail表的分配ID不为0
                sql.append(" and c.outsourceId != '0' ");
            } else {
                sql.append(" and (c.id is null or c.outsourceId = '0' ) ");
            }
        }

        if(request.getOrderTag()!=null){
            sql.append(" and c.orderTag= #{AssignableCollectionOrderReq.orderTag}");
        }


        if(request.getOutsourceId()!=null){
            sql.append(" and c.outsourceId = #{AssignableCollectionOrderReq.outsourceId}");
        }

        if (request.getAmountApply() != null) {
            sql.append(" and o.amountApply = #{AssignableCollectionOrderReq.amountApply}");
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                sql.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                sql.append(" and o.orderType != 3 ");
            }
        }

//        if(request.getOverdueDayMax()!=null){
        sql.append(" and datediff(now(),o.refundTime)<=#{AssignableCollectionOrderReq.overdueDayMax}");
//        }

//        if(request.getOverdueDayMin()!=null){
        sql.append(" and datediff(now(),o.refundTime)>=#{AssignableCollectionOrderReq.overdueDayMin}");
//        }
        logger.info("collleciton list sql: " + sql.toString());
        return sql.toString();
    }


    public String getAssignableQualityCheckOrderList(AssignableCollectionOrderReq request){
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT "
                        + "    o.uuid,o.orderType,"
                        + "    o.applyTime,"
                        + "    o.borrowingCount,"
                        + "    o.borrowingTerm,"
                        + "    o.amountApply,"
                        + "    o.status,"
                        + "    o.refundTime,"
                        + "    o.actualRefundTime,"
                        + "    u.realName, u.uuid as userUuid, "
                        + "    cc.isTest,cc.sourceType,"
                        + "    cc.orderTag, cc.promiseRepaymentTime, "
                        + "    cc.outsourceId, cc.subOutSourceId, config.title as checkResult,config.titleInn as checkResultInn,voiceConfig.title as voiceCheckResult,voiceConfig.titleInn as voiceCheckResultInn, e.createTime as updateTime"
                        + " from doit.ordOrder o join doit.usrUser u on u.uuid = o.userUuid and o.disabled = 0 ");
        sql.append("LEFT JOIN (select orderNo, max(createTime) as createTime from doit.manQualityCheckRecord group by orderNo ) e ON o.uuid = e.orderNo ");
        sql.append(" join (");
        sql.append("select c.* from doit.collectionOrderDetail c where c.sourceType = 1 and c.disabled = 0 ");
        boolean flag = false;

        if(request.getIsAssigned() != null){
            if (request.getIsAssigned() == 1) {
                //已分配,collectionOrderDetail表的分配ID不为0
                flag = true;
                sql.append(" and c.outsourceId != '0' ");
            } else {
                sql.append(" and c.outsourceId = '0' ");
            }
        }
        if(request.getCheckerId()!=null){
            flag = true;
            sql.append(" and c.outsourceId = #{AssignableCollectionOrderReq.checkerId}");
        }
        if (request.getOutsourceId() != null && request.getOutsourceId() != 0) {
            sql.append(" and c.orderUUid in (select orderUUid from collectionOrderDetail where disabled = 0 and sourceType = 0 and (outSourceId=#{AssignableCollectionOrderReq.outsourceId} or suboutSourceId=#{AssignableCollectionOrderReq.outsourceId})) ");
        }
        if (!flag) {
            sql.append(" UNION all select a.* from doit.collectionOrderDetail a where a.disabled = 0 and a.sourceType = 0 and a.orderUUID not in (select orderUUid from doit.collectionOrderDetail b where b.sourceType = 1 and b.disabled = 0)");
            if (request.getOutsourceId() != null && request.getOutsourceId() != 0) {
                sql.append(" and (a.outSourceId=#{AssignableCollectionOrderReq.outsourceId} or a.suboutSourceId=#{AssignableCollectionOrderReq.outsourceId}) ");
            }
        }
        sql.append(") cc on cc.orderUUid = o.uuid and cc.disabled = 0 ");
        sql.append(" left join manQualityCheckConfig config on config.id = cc.checkResult and config.disabled = 0 ");
        sql.append(" left join manQualityCheckConfig voiceConfig on voiceConfig.id = cc.voiceCheckResult and voiceConfig.disabled = 0 ");
        sql.append(" where datediff(now(),o.refundTime)<=#{AssignableCollectionOrderReq.overdueDayMax}");
        sql.append(" and datediff(now(),o.refundTime)>=#{AssignableCollectionOrderReq.overdueDayMin}");

        if (StringUtils.isNotBlank(request.getStartUpdateTime())) {
            sql.append(" and DATE_FORMAT(e.createTime,'%Y-%m-%d') >='" + request.getStartUpdateTime() + "' ");
        }
        if (StringUtils.isNotBlank(request.getEndUpdateTime())) {
            sql.append(" and DATE_FORMAT(e.createTime,'%Y-%m-%d') <='" + request.getEndUpdateTime() + "' ");
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                sql.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                sql.append(" and o.orderType != 3 ");
            }
        }


        if(StringUtils.isNotEmpty(request.getUuid())){
            sql.append(" and o.uuid = #{AssignableCollectionOrderReq.uuid} ");
        }
        logger.info("quality check lists sql is :" + sql.toString());
        return sql.toString();
    }

    public String getAssignableCollectionOrdersWithParam(Integer status,
                                                         Integer borrowingCountMin,
                                                         Integer borrowingCountMax,
                                                         Integer minOverdueDays,
                                                         Integer maxOverdueDays,
                                                         Integer sourceType, String amountApply, String otherAmount) {
        StringBuffer sql = new StringBuffer();
        sql.append(
                "select o.uuid from ordOrder o left join collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled = 0 and sourceType = #{sourceType}"
                        + " where o.disabled = 0 and o.status = #{status} and (c.id IS NULL OR c.outsourceId = '0' ) ");

        sql.append(" AND DATEDIFF(NOW(), o.refundTime) >=#{minOverdueDays} ");
        sql.append(" AND DATEDIFF(NOW(), o.refundTime) <=#{maxOverdueDays} ");
        if (StringUtils.isNotBlank(otherAmount)) {
            sql.append(" and o.amountApply not in (" + otherAmount + ")");
        } else {
            if (StringUtils.isNotBlank(amountApply)) {
                sql.append(" and o.amountApply in (" + amountApply + ")");
            }
        }
        if (borrowingCountMax == null) {
            sql.append(" and o.borrowingCount>=#{borrowingCountMin} ");
        } else if (borrowingCountMax.equals(borrowingCountMin)) {
            sql.append(" and o.borrowingCount=#{borrowingCountMin} ");
        } else {
            sql.append(" and o.borrowingCount>=#{borrowingCountMin} ");
            sql.append(" and o.borrowingCount<=#{borrowingCountMax} ");
        }
        logger.info(sql.toString());
        return sql.toString();
    }

    public String getAssignableCollectionOrdersWithParam1(Integer status,
                                                         Integer outSourceId,
                                                          Integer borrowingCountMin,
                                                          Integer borrowingCountMax,
                                                          Integer sourceType, String amountApply, String otherAmount) {
        StringBuffer sql = new StringBuffer();
        sql.append(
                "select o.uuid from ordOrder o left join collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled = 0 and c.sourceType = #{sourceType} "
                        + " where o.disabled = 0 and o.status in (7,8) and c.subOutSourceId = 0 and c.outsourceId = #{outSourceId}  ");

        if (StringUtils.isNotBlank(otherAmount)) {
            sql.append(" and o.amountApply not in (" + otherAmount + ")");
        } else {
            if (StringUtils.isNotBlank(amountApply)) {
                sql.append(" and o.amountApply in (" + amountApply + ")");
            }
        }
        if (borrowingCountMax == null) {
            sql.append(" and o.borrowingCount>=#{borrowingCountMin} ");
        } else if (borrowingCountMax.equals(borrowingCountMin)) {
            sql.append(" and o.borrowingCount=#{borrowingCountMin} ");
        } else {
            sql.append(" and o.borrowingCount>=#{borrowingCountMin} ");
            sql.append(" and o.borrowingCount<=#{borrowingCountMax} ");
        }
        logger.info(sql.toString());
        return sql.toString();
    }

    /**
     * 委外催收列表
     * @param request
     * @return
     */
    public String getAssignableOverdueOutSourceOrderList(AssignableCollectionOrderReq request){
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT "
                + "    o.uuid,o.orderType,"
                + "    o.applyTime,"
                + "    o.borrowingCount,"
                + "    o.borrowingTerm,"
                + "    o.amountApply,"
                + "    o.status,"
                + "    o.refundTime,"
                + "    o.actualRefundTime,"
                + "    u.realName, u.uuid as userUuid, "
                + "    c.isTest,"
                + "    c.orderTag, c.promiseRepaymentTime, "
                + "    c.subOutSourceId AS outsourceId "
                + " FROM "
                + "    ordOrder o "
                + "        JOIN "
                + "    usrUser u ON u.uuid = o.userUuid AND o.disabled = 0 "
                + "        LEFT JOIN "
                + "    collectionOrderDetail c ON c.orderUUID = o.uuid AND c.disabled = 0 and c.sourceType = #{AssignableCollectionOrderReq.sourceType}"
                + " WHERE o.disabled = 0 and DATE_FORMAT(o.refundTime,'%Y-%m-%d')<='"+ com.yqg.manage.util.DateUtils.getStrCurrentDate()+"' ");
//        if(request.getStatus()!=null){
            sql.append(" and (o.status = 7 or o.status = 8) ");
//        }
        if(StringUtils.isNotEmpty(request.getUuid())){
            sql.append(" and o.uuid = #{AssignableCollectionOrderReq.uuid} ");
        }
        if (request.getIsRepeatBorrowing() != null) {
            if (request.getIsRepeatBorrowing() == 1) {
                sql.append(" and o.borrowingCount>1 ");
            } else {
                sql.append(" and o.borrowingCount<=1 ");
            }
        }

        if(request.getIsAssigned()!=null){
            if (request.getIsAssigned() == 1) {
                //已分配,collectionOrderDetail表的分配子账号ID不为0
                sql.append(" and c.subOutSourceId != '0' ");
            } else {
                sql.append(" and c.subOutSourceId = '0' ");
            }
        }

        if(request.getOrderTag()!=null){
            sql.append(" and o.orderTag= #{AssignableCollectionOrderReq.orderTag}");
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                sql.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                sql.append(" and o.orderType != 3 ");
            }
        }

//        if(request.getOutsourceId()!=null){
        sql.append(" and c.outsourceId = #{AssignableCollectionOrderReq.outsourceId}");
//        }

        if(request.getSubOutSourceId()!=null){
            sql.append(" and c.subOutSourceId = #{AssignableCollectionOrderReq.subOutSourceId}");
        }

        logger.info("collection out list sql:" + sql.toString());
        return sql.toString();
    }
}
