package com.yqg.service.third.Inforbip;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.third.Inforbip.Enum.CallReusltEnum;
import com.yqg.service.third.Inforbip.Response.GetReportResponse;
import com.yqg.service.third.Inforbip.Response.InforbipResponse;
import com.yqg.service.third.Inforbip.Response.ReportMessage;
import com.yqg.service.third.twilio.request.TwilioCallResultRequest;
import com.yqg.service.third.twilio.response.TwilioUserInfoResponse;
import com.yqg.system.dao.TeleVoiceMessageResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.system.entity.TeleVoiceMessageResult;
import com.yqg.system.entity.TwilioCallResult;
import com.yqg.system.entity.UserResponse;
import com.yqg.user.dao.UsrDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: tonggen
 * Date: 2019/6/24
 * time: 3:58 PM
 */
@Service
@Slf4j
public class InforbipCollectionService {

    @Autowired
    private TeleVoiceMessageResultDao teleVoiceMessageResultDao;

    @Autowired
    private InforbipService inforbipService;


    /**
     * insert data into teleVoiceMessage.
     * @param bulkId
     * @param messageId
     * @param response
     * @param message
     */
    public void insertVoiceMessageResult(String bulkId, String messageId, TwilioUserInfoResponse response,
                                         ReportMessage message) {
        TeleVoiceMessageResult result = new TeleVoiceMessageResult();
        result.setDisabled(0);
        result.setOrderNo(response.getOrderNo());
        result.setUserUuid(response.getUserUuid());
        result.setTellNumber(message.getTo());
        result.setCallState(TeleCallResult.CallStatusEnum.CALL_SEND.getCode());
        result.setCallBulkId(bulkId);
        result.setCallMsgId(messageId);
        if (StringUtils.isNotEmpty(messageId)) {
            result.setCallResponse(JsonUtils.serialize(message));
        }
        result.setCallPhase(response.getCallPhase());
        result.setCallPhaseType(response.getCallPhaseType());
        result.setBatchNo(response.getBatchNo());
        teleVoiceMessageResultDao.insert(result);
    }

    /**
     * start send voice messages.
     * @param request
     * @throws Exception
     */
    public void startCallInfobip(TwilioCallResultRequest request) throws Exception {

        List<TeleVoiceMessageResult> lists = listNeedCall(request);
        if (CollectionUtils.isEmpty(lists)) {
            log.info("no order need be called，callType is：" + request.getCallPhase());
            return ;
        }

        //set phone number.
        List<TwilioUserInfoResponse> requests = lists.stream().map(elem -> new TwilioUserInfoResponse(
                elem.getOrderNo(),elem.getUserUuid(),elem.getTellNumber(), 
                request.getCallUrl(), request.getCallPhase(),
                request.getCallPhaseType(), request.getBatchNo()))
                .filter(e -> StringUtils.isNotEmpty(e.getPhoneNumber()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(requests)) {
            log.error("no orders voice messages send., callType is {}", request.getCallPhase());
            return;
        }
        //send voice messages by batch.
        int size = requests.size();
        int pageSize = 50;
        List<List<TwilioUserInfoResponse>> splitList = Stream.iterate(0, n -> n + 1)
                .limit((size + pageSize - 1) / pageSize).parallel()
                .map(a -> requests.stream().skip(a * pageSize).limit(pageSize)
                        .parallel().collect(Collectors.toList())).collect(Collectors.toList());

        for (List<TwilioUserInfoResponse> responses : splitList) {
            if (CollectionUtils.isEmpty(lists)) {
                continue;
            }
            String[] phones =  responses.stream().map(TwilioUserInfoResponse :: getPhoneNumber)
                    .collect(Collectors.toList()).toArray(new String[responses.size()]);
            InforbipResponse response = inforbipService.numberMultiple(phones, responses.get(0).getCallUrl());
            if (response == null) {
                log.error("send voice Message, the response is null");
                continue;
            }
            if (CollectionUtils.isEmpty(response.getMessages())) {
                log.error("send voice Message, the response messages is empty ");
                continue;
            }
            for (ReportMessage message : response.getMessages()) {
                Optional<TwilioUserInfoResponse> optional = responses.stream().filter(elem ->
                        message.getTo().equals(elem.getPhoneNumber())).findFirst();
                if (!optional.isPresent()) {
                    log.info("send voice messages is error , to is {}", message.getTo());
                    continue;
                }
                TwilioUserInfoResponse twilioUserInfoResponse = optional.get();
                String bulkId = response.getBulkId();
                String messageId = message.getMessageId();
                insertVoiceMessageResult(bulkId, messageId, twilioUserInfoResponse, message);
            }
        }
    }

    @Autowired
    private UsrDao usrDao;
    private List<TeleVoiceMessageResult> listNeedCall(TwilioCallResultRequest request) {

        List<TeleVoiceMessageResult> result = null;
        List<UserResponse> users;
        if (request.getCallPhaseType() == TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode()) {
            result = teleVoiceMessageResultDao.listCallAllOrder(request.getDays());
        } else if (request.getCallPhaseType() == TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_NOT_RESPONSE.getCode()) {
            //慢sql优化
            List<String> noNeedOrderNos = teleVoiceMessageResultDao.listNoNeedOrderNos(request.getCallPhase());
            result = teleVoiceMessageResultDao.listCallAllOrder(request.getDays());
            if (!CollectionUtils.isEmpty(result)) {
                if (!CollectionUtils.isEmpty(noNeedOrderNos)) {
                    result = result.stream().filter(elem -> !noNeedOrderNos.contains(elem.getOrderNo()))
                            .collect(Collectors.toList());
                }
            }
            //沉默用户
        } else if (request.getCallPhaseType() == 3) {
            users = usrDao.getSilenceUser3();
            if (CollectionUtils.isEmpty(users)) {
                return null;
            }
            result = getVoiceMessageResults(users, result);
            //申请未提交
        } else if (request.getCallPhaseType() == 4) {
            users = usrDao.sendSmsToUseWithNotVerifyOrder();
            if (CollectionUtils.isEmpty(users)) {
                return null;
            }
            result = getVoiceMessageResults(users, result);
            //降额未确认
        } else if (request.getCallPhaseType() == 5) {
            users = usrDao.sendReduceSms();
            if (CollectionUtils.isEmpty(users)) {
                return null;
            }
            result = getVoiceMessageResults(users, result);
        }
        return result;
    }

    private List<TeleVoiceMessageResult> getVoiceMessageResults(List<UserResponse> users, List<TeleVoiceMessageResult> result) {

        result = new ArrayList<>();
        for (UserResponse elem: users) {
            TeleVoiceMessageResult response = new TeleVoiceMessageResult();
            response.setOrderNo(elem.getOrderNo());
            response.setTellNumber(elem.getMobileNumberDES());
            response.setUserUuid(elem.getUuid());
            result.add(response);
        }
        return result;
    }


    //region Get Infobip report
    // Called by TwilioCallScheduling.java API: /getInfobipCollectionReport/managerTask
    public void getReport(int mode) throws Exception{

        List<TeleVoiceMessageResult> callResults = teleVoiceMessageResultDao.getTeleVoiceMessageResult(mode);

        getReportForList(callResults,mode);
    }

    // Called by this.getReport
    private void getReportForList(List<TeleVoiceMessageResult> callResults, int mode){
        if(CollectionUtils.isEmpty(callResults)){
            return;
        }
        log.info("Get Infobip report - start size={} mode= {}", callResults.size(), mode);

        //Need to group more msgId and bulkId
        Map<String,List<TeleVoiceMessageResult>> callResultMap = callResults.stream().collect(Collectors
                .groupingBy(elem->elem.getCallMsgId()+"#"+ elem.getCallBulkId()));

        for(String keyId: callResultMap.keySet()){
            List<TeleVoiceMessageResult> relatedCallResult = callResultMap.get(keyId);
            String bulkId = keyId.split("#")[1];
            String msgId = keyId.split("#")[0];
            String mobile = callResultMap.get(keyId).get(0).getTellNumber();
            log.info("The phone number to get the report is {}, bulkId is {}, and msgId is {} ", mobile, bulkId, msgId);
            try {
                // Get report based on bulkId and msgId
                GetReportResponse getReportResponse = inforbipService.getReportWithId(msgId, bulkId);
                List<GetReportResponse.Results> resultsList = getReportResponse.getResults();
                if (!CollectionUtils.isEmpty(resultsList)) {
                    for (GetReportResponse.Results result : resultsList) {
                        relatedCallResult.stream().forEach(elem->dealWithResultWithReportData(result, elem));
                    }
                } else {
                    log.error("Infobip report response is empty, continue to next query");
                }
            } catch (Exception e) {
                log.error("Infobip report is error", e);
                relatedCallResult.stream().forEach(elem->{
                    elem.setErrorGroupId(0);
                    elem.setErrorId(0);
                    elem.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
                    elem.setCallResponse(String.format("Infobip report is error %.100s", e.getMessage()));
                    teleVoiceMessageResultDao.update(elem);
                });
            }
        }
        log.info("Get Infobip report - end mode= {}", mode);
    }

    /**
     * dealWith result. Called by this.getReportForList
     * @param result
     * @param call
     */
    private void dealWithResultWithReportData(GetReportResponse.Results result, TeleVoiceMessageResult call) {

        Integer errorGroupId = result.getError().getGroupId();
        Integer errorId = result.getError().getId();

        call.setErrorGroupId(errorGroupId);
        call.setErrorId(errorId);
        call.setCallState(TeleCallResult.CallStatusEnum.CALL_FINISHED.getCode());

        switch (errorId){
            case 225:
            case 1158:// Network Congestion
                call.setCallResult(CallReusltEnum.UNKNOWN_ERROR.getCode());
                break;
            case 5000:// Answer the phone manually
            case 5001:// Answered by the voice machine
                call.setCallResult(CallReusltEnum.CONNECT.getCode());
                break;
            case 5002:
                call.setCallResult(CallReusltEnum.BUSY.getCode());
                break;
            case 5003:
                call.setCallResult(CallReusltEnum.NO_ANSWER.getCode());
                break;
            case 5004:// The file specified in the HTTP request is not accessible and cannot be downloaded
                call.setCallResult(CallReusltEnum.ATTACHMENT_USELESS.getCode());
                break;
            case 5005:// The file specified in the HTTP request is having unssuported format
                call.setCallResult(CallReusltEnum.ATTACHMENT_FORMATT_NOT_SUPPORT.getCode());
                break;
            case 5400:// Rejected due to incorrect format
                call.setCallResult(CallReusltEnum.REQUEST_FORMATT_NOT_SUPPORT.getCode());
                break;
            case 5403:// Fees owed (server understands request, but refuses to execute)
                call.setCallResult(CallReusltEnum.ARREARS.getCode());
                break;
            case 5404:
                call.setCallResult(CallReusltEnum.NUMBER_NOT_EXIST.getCode());
                break;
            case 5407:// This request require user identity verification on the operator side
                call.setCallResult(CallReusltEnum.NEED_VERIFY_USER_INFO.getCode());
                break;
            case 5408:// No signal (Failed to find user in time)
                call.setCallResult(CallReusltEnum.NO_SIGNAL.getCode());
                break;
            case 5410:// Not reached (user exists once, but the operator no longer supports the target address)
                call.setCallResult(CallReusltEnum.NO_ARRIVE.getCode());
                break;
            case 5413:
                call.setCallResult(CallReusltEnum.REQUEST_TOO_LARGE.getCode());
                break;
            case 4104:// Too many equal messages to the same destination
            case 5414:// The server refuses to process the request because the Request-URI too long
                call.setCallResult(CallReusltEnum.REFUSE_TO_DEAL.getCode());
                break;
            case 5415:// The file format is not supported
                call.setCallResult(CallReusltEnum.FORMATT_NOT_SUPPORT.getCode());
                break;
            case 5480:// Shutdown (Callee is currently unavailable)
                call.setCallResult(CallReusltEnum.DOWN_TINE.getCode());
                break;
            case 5484:
                call.setCallResult(CallReusltEnum.INVALID_NUMBER.getCode());
                break;
            case 5487:// Request has been terminated with a cancel button and end user refused to receive a voice call.
                call.setCallResult(CallReusltEnum.USER_REFUSE.getCode());
                break;
            case 5488:// The format is not supported (the requested format is not acceptable at the operator's end)
                call.setCallResult(CallReusltEnum.REQUEST_FORMATT_NOT_SUPPORT_IN_SERVER.getCode());
                break;
            case 5491:// Pending requests (the server has some pending requests from the same dialog box)
                call.setCallResult(CallReusltEnum.REQUEST_NEED_DEAL_WITH.getCode());
                break;
            case 5492:// Connect-Reject (same text has been sent to the destination)
                call.setCallResult(CallReusltEnum.REJECT_BY_OPERATOR.getCode());
                break;
            case 5500:// Internal server error
                call.setCallResult(CallReusltEnum.SERVER_ERROR_IN.getCode());
                break;
            case 5501:// Unrealized (not implemented)
                call.setCallResult(CallReusltEnum.NOT_REALIZE.getCode());
                break;
            case 5503:// Not in service area (service unavailable)
                call.setCallResult(CallReusltEnum.NOT_ON_SERVER.getCode());
                break;
            case 5504:
                call.setCallResult(CallReusltEnum.SERVER_TIME_OUT.getCode());
                break;
            case 5603:// Phone switched off
                call.setCallResult(CallReusltEnum.REFUSE.getCode());
                break;

        }
        // Completely effective (Answer manually, Busy, No answer, Phone switched off)
        List<Integer> usefulList = Arrays.asList(5000,5002,5003,5603);
        // Invalid (Number not exist, Invalid number, Rejected by operator)
        List<Integer> uselessList = Arrays.asList(5404,5484,5492);

        if (usefulList.contains(errorId)){
            call.setCallResultType(TeleCallResult.CallResultTypeEnum.VALID.getCode());
        }else if (uselessList.contains(errorId)){
            call.setCallResultType(TeleCallResult.CallResultTypeEnum.INVALID.getCode());
        }else {
            // 5500/5603/5504/5501/255/5004/5005/5400/5407/5410/5413/5414/5415/5488/5491
            call.setCallResultType(TeleCallResult.CallResultTypeEnum.NOT_SURE.getCode());
        }

        // Store outgoing call results
        call.setCallBeginTime(result.getStartTime());
        call.setCallEndTime(result.getEndTime());
        call.setCallDuration(result.getDuration());
        call.setCallResponse(JsonUtils.serialize(result));
        teleVoiceMessageResultDao.update(call);

    }
    //endregion Get Infobip report

}
