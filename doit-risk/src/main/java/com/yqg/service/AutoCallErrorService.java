package com.yqg.service;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.Inforbip.InforbipService;
import com.yqg.service.third.Inforbip.Request.InforbipRequest;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AutoCallErrorService {

    @Autowired
    private TeleCallResultDao teleCallResultDao;
    @Autowired
    private InforbipService inforbipService;
    @Autowired
    private AutoCallService autoCallService;

    public void reSend() {
        if (!autoCallService.isAutoCallSwitchOpen()) {
            log.info("reSend - risk:auto_call:switch off");
            return;
        }
        List<TeleCallResult> dbList = teleCallResultDao.getStuckedTeleCallResults();
        if (CollectionUtils.isEmpty(dbList)) {
            log.info("reSend - count is empty");
            return;
        }
        log.info("reSend - count: {}", dbList.size());
        teleCallResultDao.disabledStuckedTeleCallResult();
        resendCall(dbList);
    }

    public void reSendWithException() {
        if (!autoCallService.isAutoCallSwitchOpen()) {
            log.info("resendWithException - risk:auto_call:switch off");
            return;
        }
        List<OrdOrder> dbList = teleCallResultDao.getExceptionAutoCallList();
        if (CollectionUtils.isEmpty(dbList)) {
            log.info("reSendWithException - count is empty");
            return;
        }
        log.info("reSendWithException - count: {}", dbList.size());
        for (OrdOrder order : dbList) {
            LogUtils.addMDCRequestId(order.getUuid());
            try {
                if (order.getBorrowingCount() >= 2) {
                    autoCallService.sendReBorrowingAutoCall(order);
                } else {
                    autoCallService.sendFirstBorrowAutoCall(order);
                }
            }catch (Exception e){
                log.error("resendWithException - error",e);
            }finally {
                LogUtils.removeMDCRequestId();
            }

        }
    }

    public void resendCall(List<TeleCallResult> dbList) {
        log.info("resendCall - Start: {} requests", dbList.size());
        List<InforbipRequest> requestList = new ArrayList<>();
        dbList.stream().forEach(elem -> {
            InforbipRequest item = new InforbipRequest();
            item.setMobileNumber(CheakTeleUtils.fetchNumbers(elem.getTellNumber()));
            item.setOrderNo(elem.getOrderNo());
            item.setCallType(elem.getCallType());
            item.setCallNode(elem.getCallNode());
            item.setUserUuid(elem.getUserUuid());
            requestList.add(item);
        });
        // Send requests in batches [maximum 10 numbers at a time]
        int batchCount = (int) Math.ceil(requestList.size() / (10.0));
        for (int i = 0; i < batchCount; i++) {
            int startIndex = i * 10;
            int endIndex = startIndex + 10;
            if (endIndex > requestList.size()) {
                endIndex = requestList.size();
            }
            long startTime = System.currentTimeMillis();
            List<InforbipRequest> toSendList = new ArrayList<>();
            try {
                toSendList = requestList.subList(startIndex, endIndex);
                //ahalim: Always send to infobip
                inforbipService.sendVoiceMessage(toSendList);
            } catch (Exception e) {
                log.error("resendCall - Error,data: " + JsonUtils.serialize(toSendList), e);
            }
            log.info("resendCall - End: {} ms", (System.currentTimeMillis() - startTime));
        }
    }


    // private void sendWithRandomChannel(List<InforbipRequest> toSendList) {
    //     //第一次重发用twillio，第二次重发用inforbip，第三次重发用twillio，以此类推
    //     List<InforbipRequest> inforbipRequests = new ArrayList<>();
    //     // List<InforbipRequest> twillioCallResult = new ArrayList<>();
    //     for (InforbipRequest request : toSendList) {
    //         //
    //         // Integer count = teleCallResultDao.getRetryTimes(request.getOrderNo(), request.getMobileNumber());
    //         // count = (count == null ? 1 : count);
    //         // if (count % 2 == 1) {
    //             //inforbip
    //             inforbipRequests.add(request);
    //         // } else {
    //         //     //twilio
    //         //     twillioCallResult.add(request);
    //         // }

    //     }
    //     if (CollectionUtils.isEmpty(inforbipRequests)) {
    //         log.info("no inforbip reqeust numbers...");
    //     } else {
    //         log.info("error resend numbers- with inforbip: {}", JsonUtils.serialize(inforbipRequests));
    //         inforbipService.sendVoiceMessage(inforbipRequests);
    //     }
    //     // if (CollectionUtils.isEmpty(twillioCallResult)) {
    //     //     log.info("no twillio reqeust numbers...");
    //     // } else {
    //     //     log.info("error resend numbers- with twillio: {}", JsonUtils.serialize(twillioCallResult));
    //     //     for (InforbipRequest twillioReq : twillioCallResult) {
    //     //         try {
    //     //             TeleCallResult callInfo = new TeleCallResult();
    //     //             callInfo.setTellNumber(twillioReq.getMobileNumber());
    //     //             callInfo.setCallType(twillioReq.getCallType());
    //     //             callInfo.setCallNode(twillioReq.getCallNode());
    //     //             callInfo.setOrderNo(twillioReq.getOrderNo());
    //     //             callInfo.setUserUuid(twillioReq.getUserUuid());
    //     //             inforbipService.sendTwilioCall(callInfo);
    //     //         } catch (Exception e) {
    //     //             log.error("twillio call error with param: {}", JsonUtils.serialize(twillioReq), e);
    //     //         }
    //     //     }
    //     // }

    // }

}
