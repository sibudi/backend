package com.yqg.manage.dal.provider;


import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.manage.enums.ReviewerPostEnum;
import com.yqg.manage.service.order.request.AssignableOrderRequest;
import com.yqg.manage.service.order.request.CompletedOrderRequest;
import com.yqg.manage.service.order.request.D0OrderRequest;
import com.yqg.manage.service.order.request.ManAllOrdListSearchResquest;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.service.order.request.PaidOrderRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alan
 */
public class OrderOrderSqlProvider {

    private Logger logger = LoggerFactory.getLogger(OrderOrderSqlProvider.class);

    public String allOrdList(ManAllOrdListSearchResquest searchResquest){
        StringBuilder selectSql = new StringBuilder("select ord.*, usr.userRole, usr.realName from ordOrder ord , usrUser usr " +
                "where ord.userUuid = usr.uuid and ");

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest).append(" order by ord.applyTime desc,ord.id desc ");

        conditionSql.append(this.byPageSql(searchResquest));
        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String orderListCount(ManAllOrdListSearchResquest searchResquest){
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from ordOrder ord , usrUser usr where ord.userUuid = usr.uuid and ");

        StringBuilder conditionSql = this.generateConditionAllOrd(searchResquest);
        selectSql.append(conditionSql);

//        this.logger.info(selectSql.toString());
        return selectSql.toString();
    }

    private StringBuilder generateConditionAllOrd(ManAllOrdListSearchResquest searchResquest){
        StringBuilder conditionSql = new StringBuilder();
        if (searchResquest.getStatus() != null) {
            conditionSql.append(" ord.status = "+searchResquest.getStatus().getCode()+" and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getRealName())) {
            conditionSql.append(" usr.realName = #{ManAllOrdListSearchResquest.realName} and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getMobile())) {
            conditionSql.append(" usr.mobileNumberDES = #{ManAllOrdListSearchResquest.mobile} and ");
        }
        if (searchResquest.getUserRole() != null) {
            conditionSql.append(" usr.userRole = " +searchResquest.getUserRole().getCode() + " and ");
        }
        if (StringUtils.isNotBlank(searchResquest.getUuid())) {
            conditionSql.append(" ord.uuid = #{ManAllOrdListSearchResquest.uuid} and ");
        }
        if (searchResquest.getIsRepeatBorrowing() != null) {
            if (searchResquest.getIsRepeatBorrowing() == 0) {
                conditionSql.append(" ord.borrowingCount <2 and ");
            } else {
                conditionSql.append(" ord.borrowingCount >1 and ");
            }
        }
        if (searchResquest.getChannel() != null) {
            conditionSql.append(" ord.channel = " +searchResquest.getChannel() + " and ");
        }
        if (!StringUtils.isEmpty(searchResquest.getCreateBeginTime())) {
            conditionSql.append(" ord.applyTime >= '"+ searchResquest.getCreateBeginTime() + " 00:00:00' and ");
        }
        if (!StringUtils.isEmpty(searchResquest.getCreateEndTime())) {
            conditionSql.append(" ord.applyTime <= '"+ searchResquest.getCreateEndTime() + " 23:59:59' and ");
        }
        if (!StringUtils.isEmpty(searchResquest.getUpdateBeginTime())) {
            conditionSql.append(" ord.updateTime >= '"+ searchResquest.getUpdateBeginTime() + " 00:00:00' and ");
        }
        if (!StringUtils.isEmpty(searchResquest.getUpdateEndTime())) {
            conditionSql.append(" ord.updateTime <= '"+ searchResquest.getUpdateEndTime() + " 23:59:59' and ");
        }
        if (searchResquest.getBorrowingTerm() != null) {
            conditionSql.append(" ord.borrowingTerm = #{ManAllOrdListSearchResquest.borrowingTerm} and ");
        }
        if (searchResquest.getOrderType() != null) {
            conditionSql.append(" ord.orderType = #{ManAllOrdListSearchResquest.orderType} and ");
        }
        if (searchResquest.getIsTerms() != null) {
            if (searchResquest.getIsTerms().equals(1)) {
                conditionSql.append(" ord.orderType = 3 and ");
            } else if (searchResquest.getIsTerms().equals(2)) {
                conditionSql.append(" ord.orderType != 3 and ");
            }
        }
        conditionSql.append(" ord.disabled = 0 and usr.disabled = 0");
        return conditionSql;
    }

    private StringBuilder baseSql(){
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select uuid,createTime,updateTime,status,orderStep,amountApply,borrowingTerm,serviceFee,interest,borrowingCount");
        selectSql.append(",channel,userUuid,applyTime,lendingTime,refundTime,actualRefundTime,firstChecker,secondChecker");
        selectSql.append(" from ordOrder where ");

        return selectSql;
    }

//    /*产生分页sql*/
//    private StringBuilder byPageSql(ManOrderListSearchResquest searchResquest){
//        StringBuilder conditionSql = new StringBuilder();
//        if(searchResquest.getPageNo() != null && searchResquest.getPageSize() != null ){
//            Integer num = (searchResquest.getPageNo() - 1) * searchResquest.getPageSize();
//            conditionSql.append(" limit ").append(num.toString()).append(",")
//                    .append(searchResquest.getPageSize().toString());
//        }
//        return conditionSql;
//    }

    /**
     * 产生分页sql
     * @param searchResquest
     * @return
     */
    private StringBuilder byPageSql(ManAllOrdListSearchResquest searchResquest){
        StringBuilder conditionSql = new StringBuilder();
        if(searchResquest.getPageNo() != null && searchResquest.getPageSize() != null ){
            Integer num = (searchResquest.getPageNo() - 1) * searchResquest.getPageSize();
            conditionSql.append(" limit ").append(num.toString()).append(",")
                    .append(searchResquest.getPageSize().toString());
        }
        return conditionSql;
    }


    public String getAssignableOrderList(AssignableOrderRequest request) {
        StringBuffer selectBuffer = new StringBuffer();
        StringBuffer whereBuffer = new StringBuffer();
        selectBuffer.append(
                "select o.orderType,o.uuid,o.userUuid,o.updateTime,o.channel,a.createTime as reviewTime, o.amountApply,o.borrowingTerm, a.reviewerId,u.userRole,u.realName  "
                        + "from ordOrder o join usrUser u on o.userUuid = u.uuid and u.disabled =0 left join reviewerOrderTask a on a.orderUUID = o.uuid "
                        + " and a.finish = 0 and a.status=0 ");

        if(request.getStatus()!=null){
            switch (request.getStatus()){
                case FIRST_CHECK:
                    selectBuffer.append(" and a.reviewerRole='"+ReviewerPostEnum.JUNIOR_REVIEWER.name()+"'");
                    break;
                case SECOND_CHECK:
                    selectBuffer.append(" and a.reviewerRole='"+ReviewerPostEnum.SENIOR_REVIEWER.name()+"'");
                    break;
            }
        }
        whereBuffer.append(" where o.status in (3,4) and o.disabled=0");

        if (request.getReviewerId() != null) {
            whereBuffer.append(" and a.reviewerId = #{AssignableOrderRequest.reviewerId} ");
        }

        if (request.getIsAssignment() != null) {
            if (request.getIsAssignment() == 0) {
                whereBuffer.append(" and a.reviewerId is null  ");
            } else {
                whereBuffer.append(" and a.reviewerId is not null  ");
            }
        }

        if (request.getStatus() != null) {
            whereBuffer.append(" and o.status =" + request.getStatus().getCode() + " ");
        } else {

        }

        if (StringUtils.isNotEmpty(request.getUuid())) {
            whereBuffer.append(" and o.uuid = #{AssignableOrderRequest.uuid} ");
        }
        if (request.getOrderType() != null) {
            whereBuffer.append(" and o.orderType = #{AssignableOrderRequest.orderType} ");
        }
        if (request.getBorrowingTerm() != null) {
            whereBuffer.append(" and o.borrowingTerm = #{AssignableOrderRequest.borrowingTerm} ");
        }
        if (request.getChannel() != null) {
            whereBuffer.append(" and o.channel = " + request.getChannel());
        }

        if (StringUtils.isNotEmpty(request.getCreateBeginTime())) {
            whereBuffer
                    .append(" and o.applyTime >= '" + request.getCreateBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getCreateEndTime())) {
            whereBuffer.append(" and o.applyTime <= '" + request.getCreateEndTime() + " 23:59:59'");
        }

        if (StringUtils.isNotEmpty(request.getUpdateBeginTime())) {
            whereBuffer
                    .append(" and o.updateTime >= '" + request.getUpdateBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getUpdateEndTime())) {
            whereBuffer.append(" and o.updateTime <= '" + request.getUpdateEndTime() + " 23:59:59'");
        }

        if (StringUtils.isNotEmpty(request.getRealName())) {
            whereBuffer.append(" and u.realName = #{AssignableOrderRequest.realName} ");
        }
        if (StringUtils.isNotEmpty(request.getMobile())) {
            whereBuffer.append(" and u.mobileNumberDES= '" + DESUtils.encrypt(request.getMobile())+"'");
        }
        if (request.getUserRole() != null) {
            whereBuffer.append(" and u.userRole = " + request.getUserRole().getCode());
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                whereBuffer.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                whereBuffer.append(" and o.orderType != 3 ");
            }
        }

        return selectBuffer.toString() + whereBuffer.toString()+" order by o.updateTime asc ";
    }

    public String getCompletedOrderList(CompletedOrderRequest request) {
        StringBuffer selectBuffer = new StringBuffer();
        StringBuffer whereBuffer = new StringBuffer();
        selectBuffer.append(
                "select o.orderType, o.uuid,o.userUuid,o.updateTime,o.channel,o.status,o.applyTime,o.refundTime,o.actualRefundTime, o.amountApply,o.borrowingTerm,"
                        + " o.firstChecker juniorReviewerId,o.secondChecker seniorReviewerId,u.userRole,u.realName "
                        + " from ordOrder o join usrUser u on o.userUuid = u.uuid  and u.disabled =0 ");

        //join reviewerOrderTask a on a.orderUUID = o.uuid
        whereBuffer.append(" where o.disabled =0 and o.uuid in ( ");

        //查询已经完成的订单（初审或者复审完成）
        StringBuffer finishOrderSql = new StringBuffer();
        finishOrderSql
                .append(" select a.orderUUID from reviewerOrderTask a left join reviewerOrderTask b "
                        + " on a.orderUUID = b.orderUUID "
                        + " and b.finish = 1 and b.status = 0 and b.reviewerRole='" + ReviewerPostEnum.SENIOR_REVIEWER.name()
                        + "'");
        finishOrderSql.append(
                " where a.finish=1 and a.status = 0 and a.reviewerRole = '" + ReviewerPostEnum.JUNIOR_REVIEWER.name()
                        + "'");

        if (StringUtils.isNotEmpty(request.getJuniorAssignBeginTime())) {
            finishOrderSql
                    .append(" and a.createTime>='" + request.getJuniorAssignBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getJuniorAssignEndTime())) {
            finishOrderSql
                    .append(" and a.createTime<='" + request.getJuniorAssignEndTime() + " 23:59:59'");
        }

        if (StringUtils.isNotEmpty(request.getJuniorFinishBeginTime())) {
            finishOrderSql
                    .append(" and a.updateTime>='" + request.getJuniorFinishBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getJuniorFinishEndTime())) {
            finishOrderSql
                    .append(" and a.updateTime<='" + request.getJuniorFinishEndTime() + " 23:59:59'");
        }



        if (StringUtils.isNotEmpty(request.getSeniorAssignBeginTime())) {
            finishOrderSql
                    .append(" and b.createTime>='" + request.getSeniorAssignBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getSeniorAssignEndTime())) {
            finishOrderSql
                    .append(" and b.createTime<='" + request.getSeniorAssignEndTime() + " 23:59:59'");
        }

        if (StringUtils.isNotEmpty(request.getSeniorFinishBeginTime())) {
            finishOrderSql
                    .append(" and b.updateTime>='" + request.getSeniorFinishBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getSeniorFinishEndTime())) {
            finishOrderSql
                    .append(" and b.updateTime<='" + request.getSeniorFinishEndTime() + " 23:59:59'");
        }






        whereBuffer.append(finishOrderSql.toString() + " )");



        if (request.getJuniorReviewerId() != null) {
            whereBuffer.append(
                    " and o.firstChecker = #{CompletedOrderRequest.juniorReviewerId}");
        }
        if (request.getSeniorReviewerId() != null) {
            whereBuffer.append(
                    " and o.secondChecker = #{CompletedOrderRequest.seniorReviewerId}");
        }

        //非管理员
        if (request.getIsAdmin() != null && !request.getIsAdmin().booleanValue()) {
            whereBuffer.append(
                    " and (o.firstChecker = #{CompletedOrderRequest.operatorId} or o.secondChecker = #{CompletedOrderRequest.operatorId})");
        }

        if (request.getStatus() != null) {
            whereBuffer.append(" and o.status =" + request.getStatus().getCode() + " ");
        }

        if (StringUtils.isNotEmpty(request.getUuid())) {
            whereBuffer.append(" and o.uuid = #{CompletedOrderRequest.uuid} ");
        }
        if (request.getBorrowingTerm() != null) {
            whereBuffer.append(" and o.borrowingTerm = #{CompletedOrderRequest.borrowingTerm} ");
        }
        if (request.getChannel() != null) {
            whereBuffer.append(" and o.channel = " + request.getChannel());
        }

        if (StringUtils.isNotEmpty(request.getCreateBeginTime())) {
            whereBuffer
                    .append(" and o.applyTime >= '" + request.getCreateBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getCreateEndTime())) {
            whereBuffer.append(" and o.applyTime <= '" + request.getCreateEndTime() + " 23:59:59'");
        }

        if (StringUtils.isNotEmpty(request.getRealName())) {
            whereBuffer.append(" and u.realName = #{CompletedOrderRequest.realName} ");
        }
        if (StringUtils.isNotEmpty(request.getMobile())) {
            whereBuffer.append(" and u.mobileNumberDES= '" + DESUtils.encrypt(request.getMobile())+"'");
        }
        if (request.getUserRole() != null) {
            whereBuffer.append(" and u.userRole = " + request.getUserRole().getCode());
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                whereBuffer.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                whereBuffer.append(" and o.orderType != 3 ");
            }
        }

        return selectBuffer.toString() + whereBuffer.toString()+" order by o.updateTime desc ";
    }





    public String getD0OrderList(D0OrderRequest request){
        StringBuffer selectBuffer = new StringBuffer();
        StringBuffer whereBuffer = new StringBuffer();
        selectBuffer.append(
                "SELECT "
                        + "    o.uuid,"
                        + "    o.userUuid,"
                        + "    o.updateTime,"
                        + "    o.applyTime,"
                        + "    o.channel,"
                        + "    o.amountApply,"
                        + "    o.borrowingTerm,"
                        + "    o.borrowingCount,"
                        + "    o.refundTime,"
                        + "    o.status,"
                        + "    a.outsourceId,"
                        + "    a.isTest,"
                        + "    u.userRole,"
                        + "    u.realName FROM "
                        + "    ordOrder o "
                        + "        JOIN "
                        + "    usrUser u ON o.userUuid = u.uuid and u.disabled =0 "
                        + "        LEFT JOIN "
                        + "    collectionOrderDetail a ON a.orderUUID = o.uuid and a.disabled = 0 and a.sourceType = 0 ");

        whereBuffer.append(" where o.disabled =0 and o.refundTime >= '" + DateUtils.dateToDay()
                + " 00:00:00' and o.refundTime <='" + DateUtils.dateToDay() + " 23:59:59' ");


        if (request.getStatus() != null) {
            whereBuffer.append(" and o.status =" + request.getStatus().getCode() + " ");
        }
        if (request.getOrderType() != null) {
            whereBuffer.append(" and o.orderType = #{D0OrderRequest.orderType} ");
        }

        if (request.getIsRepeatBorrowing() != null) {
            if (request.getIsRepeatBorrowing() == 1) {
                whereBuffer.append(" and o.borrowingCount >1 ");
            } else {
                whereBuffer.append(" and o.borrowingCount <2 ");
            }
        }

        if (StringUtils.isNotEmpty(request.getUuid())) {
            whereBuffer.append(" and o.uuid = #{D0OrderRequest.uuid} ");
        }
        if (request.getBorrowingTerm() != null) {
            whereBuffer.append(" and o.borrowingTerm = #{D0OrderRequest.borrowingTerm} ");
        }
        if (request.getChannel() != null) {
            whereBuffer.append(" and o.channel = " + request.getChannel());
        }

        if (StringUtils.isNotEmpty(request.getCreateBeginTime())) {
            whereBuffer
                    .append(" and o.applyTime >= '" + request.getCreateBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getCreateEndTime())) {
            whereBuffer.append(" and o.applyTime <= '" + request.getCreateEndTime() + " 23:59:59'");
        }

        if (StringUtils.isNotEmpty(request.getRealName())) {
            whereBuffer.append(" and u.realName = #{D0OrderRequest.realName} ");
        }
        if (StringUtils.isNotEmpty(request.getMobile())) {
            whereBuffer.append(" and u.mobileNumberDES= '" + DESUtils.encrypt(request.getMobile())+"'");
        }
        if (request.getUserRole() != null) {
            whereBuffer.append(" and u.userRole = " + request.getUserRole().getCode());
        }

        if(request.getOrderTag()!=null){
            whereBuffer.append(" and a.orderTag=#{D0OrderRequest.orderTag}");
        }

        if(request.getOutsourceId()!=null){
            whereBuffer.append(" and a.outsourceId=#{D0OrderRequest.outsourceId}");
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                whereBuffer.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                whereBuffer.append(" and o.orderType != 3 ");
            }
        }


        return selectBuffer.toString() + whereBuffer.toString();
    }


    public String getOverdueOrderList(OverdueOrderRequest request){
        StringBuffer selectBuffer = new StringBuffer();
        StringBuffer whereBuffer = new StringBuffer();
        selectBuffer.append(
                "SELECT "
                        + "    o.uuid,"
                        + "    o.userUuid,o.orderType, "
                        + "    o.updateTime,"
                        + "    o.applyTime,"
                        + "    o.channel,"
                        + "    o.amountApply,"
                        + "    o.borrowingTerm,"
                        + "    o.borrowingCount,"
                        + "    o.refundTime,"
                        + "    o.status,"
                        + "    a.outsourceId,"
                        + "    a.orderTag,"
                        + "    a.isTest,"
                        + "    u.userRole,"
                        + "    u.realName FROM "
                        + "    ordOrder o "
                        + "        JOIN "
                        + "    usrUser u ON o.userUuid = u.uuid and u.disabled =0 "
                        + "        LEFT JOIN "
                        + "    collectionOrderDetail a ON a.orderUUID = o.uuid and a.disabled = 0 ");

        whereBuffer.append(" where o.disabled =0 and o.status=" + OrdStateEnum.RESOLVING_OVERDUE.getCode());

        if (request.getIsRepeatBorrowing() != null) {
            if (request.getIsRepeatBorrowing() == 1) {
                whereBuffer.append(" and o.borrowingCount >1 ");
            } else {
                whereBuffer.append(" and o.borrowingCount <2 ");
            }
        }

        if (StringUtils.isNotEmpty(request.getUuid())) {
            whereBuffer.append(" and o.uuid = #{OverdueOrderRequest.uuid} ");
        }
        if (request.getOrderType() != null) {
            whereBuffer.append(" and o.orderType = #{OverdueOrderRequest.orderType} ");
        }
        if (request.getBorrowingTerm() != null) {
            whereBuffer.append(" and o.borrowingTerm = #{OverdueOrderRequest.borrowingTerm} ");
        }
        if (request.getChannel() != null) {
            whereBuffer.append(" and o.channel = " + request.getChannel());
        }

        if (StringUtils.isNotEmpty(request.getCreateBeginTime())) {
            whereBuffer
                    .append(" and o.applyTime >= '" + request.getCreateBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getCreateEndTime())) {
            whereBuffer.append(" and o.applyTime <= '" + request.getCreateEndTime() + " 23:59:59'");
        }

        if(StringUtils.isNotEmpty(request.getDueDayStartTime())){
            whereBuffer
                    .append(" and o.refundTime >= '" + request.getDueDayStartTime() + " 00:00:00'");
        }

        if(StringUtils.isNotEmpty(request.getDueDayEndTime())){
            whereBuffer
                    .append(" and o.refundTime <= '" + request.getDueDayEndTime() + " 23:59:59'");
        }

        if(request.getMinOverdueDays()!=null){
            whereBuffer.append(" and datediff(now(),o.refundTime)>=#{OverdueOrderRequest.minOverdueDays}");
        }

        if(request.getMaxOverdueDays()!=null){
            whereBuffer.append(" and datediff(now(),o.refundTime)<=#{OverdueOrderRequest.maxOverdueDays}");
        }

        if (StringUtils.isNotEmpty(request.getRealName())) {
            whereBuffer.append(" and u.realName = #{OverdueOrderRequest.realName} ");
        }
        if (StringUtils.isNotEmpty(request.getMobile())) {
            whereBuffer.append(" and u.mobileNumberDES= '" + DESUtils.encrypt(request.getMobile())+"'");
        }
        if (request.getUserRole() != null) {
            whereBuffer.append(" and u.userRole = " + request.getUserRole().getCode());
        }

        if(request.getOrderTag()!=null){
            whereBuffer.append(" and a.orderTag=#{OverdueOrderRequest.orderTag}");
        }

        if(request.getOutsourceId()!=null){
            whereBuffer.append(" and a.outsourceId=#{OverdueOrderRequest.outsourceId}");
        }

        if (request.getIsTest() != null && request.getIsTest() == 1) {
            whereBuffer.append(" and a.isTest=#{OverdueOrderRequest.isTest}");
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                whereBuffer.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                whereBuffer.append(" and o.orderType != 3 ");
            }
        }

        return selectBuffer.toString() + whereBuffer.toString();
    }


    public String getPaidOrderList(PaidOrderRequest request){
        StringBuffer selectBuffer = new StringBuffer();
        StringBuffer whereBuffer = new StringBuffer();
        selectBuffer.append(
                "SELECT "
                        + "    o.uuid,o.orderType, "
                        + "    o.userUuid,"
                        + "    o.updateTime,"
                        + "    o.applyTime,"
                        + "    o.channel,"
                        + "    o.amountApply,"
                        + "    o.borrowingTerm,"
                        + "    o.borrowingCount,"
                        + "    o.refundTime,"
                        + "    o.actualRefundTime,"
                        + "    o.status,"
                        + "    a.outsourceId,"
                        + "    a.isTest,"
                        + "    u.userRole,"
                        + "    u.realName FROM "
                        + "    ordOrder o "
                        + "        JOIN "
                        + "    usrUser u ON o.userUuid = u.uuid and u.disabled =0 "
                        + "        LEFT JOIN "
                        + "    collectionOrderDetail a ON a.orderUUID = o.uuid and a.disabled = 0 ");

        whereBuffer.append(
                " where o.disabled =0 and o.status in (" + OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()
                        + ", " + OrdStateEnum.RESOLVED_OVERDUE.getCode() + ") ");

        if (request.getIsRepeatBorrowing() != null) {
            if (request.getIsRepeatBorrowing() == 1) {
                whereBuffer.append(" and o.borrowingCount >1 ");
            } else {
                whereBuffer.append(" and o.borrowingCount <2 ");
            }
        }

        if (StringUtils.isNotEmpty(request.getUuid())) {
            whereBuffer.append(" and o.uuid = #{PaidOrderRequest.uuid} ");
        }
        if (request.getBorrowingTerm() != null) {
            whereBuffer.append(" and o.borrowingTerm = #{PaidOrderRequest.borrowingTerm} ");
        }
        if (request.getChannel() != null) {
            whereBuffer.append(" and o.channel = " + request.getChannel());
        }

        if (request.getOrderType() != null) {
            whereBuffer.append(" and o.orderType = #{PaidOrderRequest.orderType}" );
        }

        if (StringUtils.isNotEmpty(request.getCreateBeginTime())) {
            whereBuffer
                    .append(" and o.applyTime >= '" + request.getCreateBeginTime() + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getCreateEndTime())) {
            whereBuffer.append(" and o.applyTime <= '" + request.getCreateEndTime() + " 23:59:59'");
        }

        if(StringUtils.isNotEmpty(request.getDueDayStartTime())){
            whereBuffer
                    .append(" and o.refundTime >= '" + request.getDueDayStartTime() + " 00:00:00'");
        }

        if(StringUtils.isNotEmpty(request.getDueDayEndTime())){
            whereBuffer
                    .append(" and o.refundTime <= '" + request.getDueDayEndTime() + " 23:59:59'");
        }

        if(request.getMinOverdueDays()!=null){
            whereBuffer.append(" and datediff(o.actualRefundTime,o.refundTime)>=#{PaidOrderRequest.minOverdueDays}");
        }

        if(request.getMaxOverdueDays()!=null){
            whereBuffer.append(" and datediff(o.actualRefundTime,o.refundTime)<=#{PaidOrderRequest.maxOverdueDays}");
        }

        if (StringUtils.isNotEmpty(request.getActualPaymentStartDate())) {
            whereBuffer
                    .append(
                            " and o.actualRefundTime >= '" + request.getActualPaymentStartDate()
                                    + " 00:00:00'");
        }
        if (StringUtils.isNotEmpty(request.getActualPaymentEndDate())) {
            whereBuffer
                    .append(" and o.actualRefundTime <= '" + request.getActualPaymentEndDate()
                            + " 23:59:59'");
        }



        if (StringUtils.isNoneEmpty(request.getRealName())) {
            whereBuffer.append(" and u.realName = #{PaidOrderRequest.realName} ");
        }
        if (StringUtils.isNoneEmpty(request.getMobile())) {
            whereBuffer.append(" and u.mobileNumberDES= '" + DESUtils.encrypt(request.getMobile())+"'");
        }
        if (request.getUserRole() != null) {
            whereBuffer.append(" and u.userRole = " + request.getUserRole().getCode());
        }

        if(request.getOrderTag()!=null){
            whereBuffer.append(" and a.orderTag=#{PaidOrderRequest.orderTag}");
        }

        if(request.getOutsourceId()!=null){
            whereBuffer.append(" and a.outsourceId=#{PaidOrderRequest.outsourceId}");
        }

        if (request.getIsTest() != null && request.getIsTest() == 1) {
            whereBuffer.append(" and a.isTest=#{PaidOrderRequest.isTest}");
        }
        if (request.getIsTerms() != null) {
            if (request.getIsTerms().equals(1)) {
                whereBuffer.append(" and o.orderType = 3 ");
            } else if (request.getIsTerms().equals(2)) {
                whereBuffer.append(" and o.orderType != 3 ");
            }
        }
        return selectBuffer.toString() + whereBuffer.toString();
    }




}
