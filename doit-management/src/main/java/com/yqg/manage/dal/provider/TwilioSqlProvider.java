package com.yqg.manage.dal.provider;

import com.yqg.common.utils.StringUtils;
import com.yqg.manage.service.third.request.ThirdTwilioRequest;
import com.yqg.manage.service.third.request.TwilioWhatsAppRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 下午2:16
 */
public class TwilioSqlProvider {

    private Logger logger = LoggerFactory.getLogger(TwilioSqlProvider.class);

    public String listTwilioCallResult(ThirdTwilioRequest request) {
        StringBuilder sf = new StringBuilder("select batchNo,min(startTime) as startTime, min(callPhase) as callPhase, count(1) as sendCount from twilioCallResult where disabled = 0 ");
        sf.append(this.generateConditionAllOrd(request).append(" group by batchNo order by min(startTime) desc"));

        return sf.toString();
    }

    public StringBuilder generateConditionAllOrd (ThirdTwilioRequest request) {

        StringBuilder sf = new StringBuilder();
        if (StringUtils.isNotBlank(request.getBatchNo())) {
            sf.append(" and batchNo = #{request.batchNo}");
        }
        if (StringUtils.isNotBlank(request.getCallPhase())) {
            sf.append(" and callPhase = #{request.callPhase}");
        }
        if (StringUtils.isNotBlank(request.getSendStartTime())) {
            sf.append(" and startTime >= #{request.sendStartTime}");
        }
        if (StringUtils.isNotBlank(request.getSendEndTime())) {
            sf.append(" and endTime <= #{request.sendEndTime}");
        }
        return sf;
    }


    /**
     * 查询WA列表
     * @param request
     * @return
     */
    public String listTwilioWaRecord(TwilioWhatsAppRequest request) {
        StringBuilder sf = new StringBuilder("select record.id as id ,record.userUuid as userUuid, record.phoneNum as phoneNum, record.uuid as uuid, ord.uuid as orderNo, usr.realName as userName, ord.amountApply, ord.borrowingTerm, DATEDIFF(now(),ord.refundTime) as overDueDay, record.replyContent, u.username as operator, record.remark, record.solveType, record.promiseTime\n" +
                "from twilioWhatsAppRecord record join ordOrder ord on ord.uuid = record.orderNo and ord.disabled = 0 join usrUser usr on usr.uuid = record.userUuid and usr.disabled = 0 left join manUser u on u.id = record.createUser and u.disabled =0 \n" +
                "where record.disabled = 0 and record.direction=2 and record.dataCreateTime is not null ");
        sf.append(this.generateConditionlistTwilioWaRecord(request)).append(" order by record.dataCreateTime desc");
        logger.info(sf.toString());
        return sf.toString();
    }

    public StringBuilder generateConditionlistTwilioWaRecord (TwilioWhatsAppRequest request) {

        StringBuilder sf = new StringBuilder();
        if (StringUtils.isNotBlank(request.getOrderNo())) {
            sf.append(" and ord.uuid=#{request.orderNo}");
        }
        if (StringUtils.isNotBlank(request.getUserName())) {
            sf.append(" and usr.realName=#{request.userName}");
        }
        if (StringUtils.isNotBlank(request.getPhoneNumber())) {
            sf.append(" and record.phoneNum=#{request.phoneNumber}");
        }
        if (request.getOverDueDay() != null) {
            sf.append(" and dateDiff(now(), ord.refundTime) = #{request.overDueDay");
        }
        if (StringUtils.isNotBlank(request.getPromiseTimeStart())) {
            sf.append(" and DATE_FORMAT(record.promiseTime,'%Y-%m-%d %H:%i') >= #{request.promiseTimeStart}");
        }
        if (StringUtils.isNotBlank(request.getPromiseTimeEnd())) {
            sf.append(" and DATE_FORMAT(record.promiseTime,'%Y-%m-%d %H:%i') <= #{request.promiseTimeEnd}");
        }
        if (request.getSolveType() != null) {
            sf.append(" and record.solveType=#{request.solveType}");
        }
        //如果用户回复A,B,C,D中的一个，以最后一次回复为准
        if (StringUtils.isNotBlank(request.getReplyContent())) {
            if (!"-".equals(request.getReplyContent())) {
                sf.append(" and (record.replyContent=#{request.replyContent}");
                //可能用户回复的小写的
                request.setReplyContent(request.getReplyContent().toLowerCase());
                sf.append(" or record.replyContent=#{request.replyContent})");
            } else {
                sf.append(" and record.replyContent not in ('A','B','C','D','a','b','c','d')");
            }
            sf.append(" and not exists (select 1 from twilioWhatsAppRecord r where r.disabled=0 and r.orderNo = ord.uuid and r.userUuid= ord.userUuid and r.dataCreateTime > record.dataCreateTime and r.direction=2) ");
        }
        return sf;
    }
}
