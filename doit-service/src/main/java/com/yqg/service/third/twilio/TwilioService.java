package com.yqg.service.third.twilio;


import com.twilio.Twilio;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.twilio.config.TwilioConfig;
import com.yqg.service.third.twilio.request.TwilioCallResultRequest;
import com.yqg.service.third.twilio.request.TwilioWhatsAppRecordRequest;
import com.yqg.service.third.twilio.response.TwilioUserInfoResponse;
import com.yqg.system.dao.TwilioCallResultDao;
import com.yqg.system.dao.TwilioWhatsAppRecordDao;
import com.yqg.system.entity.TwilioCallResult;
import com.yqg.system.entity.TwilioWhatsAppRecord;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 语音外呼
 * Created by tonggen on 2018/10/11.
 */
@Service
@Slf4j
public class TwilioService {

    private Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Autowired
    private TwilioWhatsAppRecordDao twilioWhatsAppRecordDao;

    @Autowired
    private TwilioCallResultDao twilioCallResultDao;

    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private SysParamService sysParamService;

    /**
     * 查询出需要wa的订单，并进行wa发送
     * @param request
     */
    public void startWhatsAppTwilio(TwilioWhatsAppRecordRequest request) {

//        List<TwilioCallResult> lists = twilioWhatsAppRecordDao.listNeedWhatsApp(request.getDays());

        List<TwilioCallResult> lists = new ArrayList<>();
        TwilioCallResult result = new TwilioCallResult();
        result.setOrderNo("011712052324338100");
        result.setUserUuid("34445416F0934877B55A64723CB50A6A");
        lists.add(result);
        if (CollectionUtils.isEmpty(lists)) {
            logger.info("no order need WhatsApp：" + request.getDays());
            return ;
        }
        initTwilio();
        //开始进行每个订单发送
        for (TwilioCallResult elem : lists) {
            //对象转换
            TwilioWhatsAppRecord record = getWhatAppRequest(elem, request);

            if (StringUtils.isEmpty(record.getUserPhone())) {
                logger.info("orderNo:" + elem.getOrderNo() + "，userPhone is error!");
                continue;
            }
            //进行订单外呼
            this.sendWhatsApp(record);
        }
        logger.info("this twilio wa send count ：" + lists.size());
    }

    private TwilioWhatsAppRecord getWhatAppRequest(TwilioCallResult callResult, TwilioWhatsAppRecordRequest request) {

        TwilioWhatsAppRecord record = new TwilioWhatsAppRecord();
        record.setOrderNo(callResult.getOrderNo());
        record.setUserUuid(callResult.getUserUuid());
//        if (StringUtils.isNotBlank(callResult.getPhoneNumber())) {
//            String temp = CheakTeleUtils.telephoneNumberValid2(DESUtils.decrypt(callResult.getPhoneNumber()));
//            if (StringUtils.isNotBlank(temp)) {
//                record.setUserPhone("whatsapp:+62" + temp);
//                record.setPhoneNum("+62" + temp);
//            }
//        }
        record.setUserPhone("whatsapp:+8618119604421");
        record.setPhoneNum("+8618119604421");
        record.setTwilioPhone(twilioConfig.getWhatsAppFrom());
        record.setBatchNo(request.getBatchNo());
        record.setReplyContent(request.getReplyContent());
        record.setDirection(1);
        return record;
    }

    /**
     * 调用twilio进行发送whatsApp，保存一条记录到twilioWhatsAppRecord
     * @param record
     */
    public TwilioWhatsAppRecord sendWhatsApp(TwilioWhatsAppRecord record) {
        log.info("whatsapp with twilio ,mobileNumber: {}",record.getUserPhone());
        //参数检查
        if (record == null || StringUtils.isEmpty(record.getUserPhone())
                || StringUtils.isEmpty(record.getOrderNo())
                || StringUtils.isEmpty(record.getUserUuid())
                || StringUtils.isEmpty(record.getTwilioPhone())
                || record.getDirection() == 0) {
            logger.info("twilio send WhatApp is ERROR！");
            return new TwilioWhatsAppRecord();
        }

        String  sid = "";
        try {
            //开始发送whatsApp
            Message message = Message.creator(
				new com.twilio.type.PhoneNumber(record.getUserPhone()),
				new com.twilio.type.PhoneNumber(record.getTwilioPhone()),
				record.getReplyContent())
				.create();

            sid = message.getSid();
        } catch (Exception e) {
            logger.error("call twilio whatsApp error orderNo:" + record.getOrderNo() + e);
        }

        record.setSid(sid);
        List<TwilioWhatsAppRecord> lists = twilioWhatsAppRecordDao.listRecordBySid(record.getSid());
        if (CollectionUtils.isEmpty(lists)) {
            twilioWhatsAppRecordDao.insert(record);
        } else {
            record.setId(lists.get(0).getId());
            record.setUuid(lists.get(0).getUuid());
            twilioWhatsAppRecordDao.update(record);
        }
        return record;
    }

    /**
     * 更新所有订单
     */
    public void startUpdateWhatsApp() {

        List<TwilioWhatsAppRecord> lists = twilioWhatsAppRecordDao.listNeedUpdateStatus();

        if (CollectionUtils.isEmpty(lists)) {
            logger.info("本次没有需要更新的twilio whatsapp");
            return ;
        }
        //通过sid更新数据
        initTwilio();
        lists.stream().forEach(elem -> {
           Message message =  Message.fetcher(elem.getSid()).fetch();
           elem.setPrice(String.valueOf(message.getPrice()));
           elem.setStatus(message.getStatus().toString());
           elem.setDataCreateTime(message.getDateCreated().toDate());
           twilioWhatsAppRecordDao.update(elem);
        });
        logger.info("this update whatsapp order count  is : " + lists.size());
    }

    @Autowired
    private RedisClient redisClient;
    /**
     * 更新所有订单
     */
    public void startUpdateCallResult(Integer num) {

        String lockKey = RedisContants.STARTUPDATECALLRESULT + ":" + num;
        boolean lock = redisClient.lockDistributed(lockKey,"true",60*20);
        if (!lock ){
            log.info("locked: {}",lockKey);
            return;
        }
        try {
            List<TwilioCallResult> lists = twilioCallResultDao.listNeedUpdateStatus(num);

            if (CollectionUtils.isEmpty(lists)) {
                logger.info("no data need update twilio");
                return ;
            }
            initTwilio();
            for (TwilioCallResult result : lists) {
                logger.info("this update orderno is {}.", result.getOrderNo());
                long startSendTime = System.currentTimeMillis();
                this.updateTwilioCallResult(result);
                long endSendTime = System.currentTimeMillis();
                logger.info("one order cost {}", endSendTime - startSendTime);
            }

            logger.info("this update twilio count ：" + lists.size());
        } finally {
            redisClient.unLock(lockKey);
        }


    }
    /**
     * 查询并更新一条外呼数据
     * @param twilioCallResult
     */
    public void updateTwilioCallResult(TwilioCallResult twilioCallResult) {

        if (StringUtils.isEmpty(twilioCallResult.getSid())) {
            logger.info("查询获取twilio外呼失败 sid:" + twilioCallResult.getSid());
            return ;
        }
        //调用sdk查询外呼结果
        try{
//            initTwilio();
            Call call = Call.fetcher(twilioCallResult.getSid()).fetch();
            getTwilioCallResult(call, twilioCallResult);
            twilioCallResultDao.update(twilioCallResult);
        } catch (Exception e) {

            logger.info("调用twilio sdk查询结果出现异常 orderNo:" + twilioCallResult.getOrderNo());
            twilioCallResult.setCallResultType(TwilioCallResult.CallStateEnum.CALL_SEND_ERROR.getCode());
            twilioCallResult.setRemark("调用twilio sdk查询结果出现异常");
            twilioCallResultDao.update(twilioCallResult);
        }

    }

    /**
     * 调用twilio进行外呼，保存一条记录到twilioCallResult
     * @param request
     */
    public TwilioCallResult callTwilio(TwilioUserInfoResponse request) {
        log.info("call with twilio ,mobileNumber: {}",request.getPhoneNumber());
        //参数检查
        if (request == null || StringUtils.isEmpty(request.getCallUrl())
                || StringUtils.isEmpty(request.getPhoneNumber())
                || StringUtils.isEmpty(request.getOrderNo())
                || StringUtils.isEmpty(request.getUserUuid())
                || StringUtils.isEmpty(request.getCallPhase())
                || request.getCallPhaseType() == null
                || StringUtils.isEmpty(request.getFrom())) {
            logger.info("twilio外呼参数错误");
            return new TwilioCallResult();
        }

        try{
            //初始化并且调用外呼
//            initTwilio();
            Call call = Call.creator(
                    new PhoneNumber(request.getPhoneNumber()),
                    new PhoneNumber(request.getFrom()),
                    URI.create(request.getCallUrl()))
                    .setMethod(HttpMethod.GET)
                    .create();
            //保存一条外呼记录
            TwilioCallResult twilioCallResult = new TwilioCallResult();
            twilioCallResult.setOrderNo(request.getOrderNo());
            twilioCallResult.setUserUuid(request.getUserUuid());
            twilioCallResult.setSid(call.getSid());
            twilioCallResult.setCallPhase(request.getCallPhase());
            twilioCallResult.setCallPhaseType(request.getCallPhaseType());
            twilioCallResult.setCallState(TwilioCallResult.CallStateEnum.CALL_SEND.getCode());
            twilioCallResult.setBatchNo(request.getBatchNo());
            twilioCallResult.setRemark(request.getRemark());

            twilioCallResult.setCallNode(request.getCallNode());
            twilioCallResult.setCallType(request.getCallType());
            twilioCallResult.setPhoneNumber(request.getPhoneNumber());
            twilioCallResultDao.insert(twilioCallResult);
            return twilioCallResult;
        } catch (Exception e) {
            logger.error("调用twilio外呼系统失败 orderNo:" + request.getOrderNo(),e);
        }

        return new TwilioCallResult();
    }

    /**
     * 将Call返回的结果封装成对象
     */
    private void getTwilioCallResult(Call call, TwilioCallResult twilioCallResult) {

        if (call == null) {
            logger.info("======查询twilio外呼失败======");
            return ;
        }
        twilioCallResult.setDuration(call.getDuration());
        if (call.getStartTime() != null) {
            twilioCallResult.setStartTime(call.getStartTime().toDate());
        }
        if (call.getEndTime() != null) {
            twilioCallResult.setEndTime(call.getEndTime().toDate());
        }
        twilioCallResult.setPhoneNumber(call.getTo());
        twilioCallResult.setCallResult(call.getStatus().toString());
        twilioCallResult.setCallResultType();
        twilioCallResult.setPrice(call.getPrice());
        twilioCallResult.setCallState(TwilioCallResult.CallStateEnum.CALL_COMPLETE.getCode());
    }

    public void initTwilio () {
        String accountSid = twilioConfig.getAccountSid();
        String authToken = twilioConfig.getAuthToken();
        Twilio.init(accountSid, authToken);
    }

    public static void main(String[] args) {
        Twilio.init("AC8521b50dcb9ecb3cd79304d6749e11d5", "5c900a16403d61d949a883b98cdbc004");
        Call call = Call.fetcher("CA99f0c8b07d57e3ee01a8ea820240a253").fetch();
    }
    /**
     * 查询出需要外呼的订单，并进行外呼
     * @param request
     */
    public void startCallTwilio(TwilioCallResultRequest request) {

        List<TwilioCallResult> lists = null;
        if (request.getCallPhaseType() == TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode()) {
            lists = twilioCallResultDao.listCallAllOrder(request.getDays());
        } else if (request.getCallPhaseType() == TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_NOT_RESPONSE.getCode()) {
            //慢sql优化
            List<String> noNeedOrderNos = twilioCallResultDao.listNoNeedOrderNos(request.getCallPhase());
            lists = twilioCallResultDao.listCallSomeOrder(request.getDays(), request.getCallPhase());
            if (!CollectionUtils.isEmpty(lists)) {
                if (!CollectionUtils.isEmpty(noNeedOrderNos)) {
                    lists = lists.stream().filter(elem -> {
                         return !noNeedOrderNos.contains(elem.getOrderNo());
                    }).collect(Collectors.toList());
                }
            }
            //查询沉默用户（全部）订单号为2写死
        } else if (request.getCallPhaseType() == 3) {
            lists = twilioCallResultDao.listSilentUser();
        }   //查询沉默用户（为接通）订单号为2写死
         else if (request.getCallPhaseType() == 4) {
            lists = twilioCallResultDao.listSilentSomeUser();
        } //查询提额用户 订单号为1写死
         else if (request.getCallPhaseType() == 5) {
            lists = twilioCallResultDao.listUpgradeLimit();
            //查询提额部分用户 订单号为1写死
        } else if (request.getCallPhaseType() == 6) {
            lists = twilioCallResultDao.listSomeUpgradeLimit();
        } else if (request.getCallPhaseType() == 7) {
            lists = twilioCallResultDao.listAllReduceLimit();
        }
        if (CollectionUtils.isEmpty(lists)) {
            logger.info("no order need be called，callType is：" + request.getCallPhase());
            return ;
        }
        initTwilio();

        //每次固定发送两个电话号码
        TwilioCallResult result1 = new TwilioCallResult();
        result1.setUserUuid("testUserUuid1");
        result1.setOrderNo("testOrderNo1");
        result1.setPhoneNumber("+6287787117873");
        lists.add(result1);
        for (TwilioCallResult elem : lists) {
            //对象转换
            TwilioUserInfoResponse response = new TwilioUserInfoResponse(elem.getOrderNo(),elem.getUserUuid(),
                    elem.getPhoneNumber(), request.getCallUrl(), request.getCallPhase(),
                    request.getCallPhaseType(), request.getBatchNo());
            //获得外显电话号码
            response.setFrom(getTwilioFromNumber());
            if (StringUtils.isEmpty(response.getPhoneNumber())) {
                logger.info("orderNo:" + elem.getOrderNo() + "，the phone number is error!");
                continue;
            }
            //进行订单外呼
            this.callTwilio(response);
        }
        logger.info("this twilio counts is ：" + lists.size());
    }

    /**
     * 获得twilio 外呼的外显号码
     * @return
     */
    public String getTwilioFromNumber() {
        String froms = "";
        try {
            froms = sysParamService.getSysParamValue("twilio:From:Phone");
            if (StringUtils.isEmpty(froms)) {
                log.error("the twilio phones is error");
                return "";
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        String[] fromTemps = froms.split(",");
        if (fromTemps.length == 0) {
            return "";
        }
        int index = new Random().nextInt(fromTemps.length);
        return fromTemps[index];
    }

    /**
     * 通过订单号查询外呼结果
     * @param orderNo
     * @return
     */
    public List<TwilioCallResult> listTwilioCallResultByOrderNo(String orderNo) {

        if (StringUtils.isEmpty(orderNo)) {
            return null;
        }
        TwilioCallResult result = new TwilioCallResult();
        result.setDisabled(0);
        result.setOrderNo(orderNo);
        result.set_orderBy("createTime desc");
        return twilioCallResultDao.scan(result);

    }
}
