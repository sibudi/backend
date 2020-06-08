package com.yqg.service.third.Inforbip;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.Inforbip.Enum.CallReusltEnum;
import com.yqg.service.third.Inforbip.Request.InfobipRequestMessage;
import com.yqg.service.third.Inforbip.Request.InfobipRequestMessages;
import com.yqg.service.third.Inforbip.Request.InfobipVoiceRequestMessage;
import com.yqg.service.third.Inforbip.Request.InforbipRequest;
import com.yqg.service.third.Inforbip.Response.GetReportResponse;
import com.yqg.service.third.Inforbip.Response.InforbipResponse;
import com.yqg.service.third.Inforbip.Response.ReportMessage;
import com.yqg.service.third.Inforbip.config.InforbipConfig;
import com.yqg.service.third.twilio.TwilioService;
import com.yqg.service.third.twilio.config.TwilioConfig;
import com.yqg.service.third.twilio.response.TwilioUserInfoResponse;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wanghuaizhou on 2018/8/8.
 */
@Service
@Slf4j
public class InforbipService {

    @Autowired
    private InforbipConfig inforbipConfig;
    @Autowired
    private TeleCallResultDao teleCallResultDao;
    @Autowired
    private TwilioService twilioService;
    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private OkHttpClient httpClient;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SysParamService sysParamService;


    private String getInforbipHost(){
        String oldEndPoint = redisClient.get(RedisContants.INFORBIP_OLD_ENDPOINT);
        if(StringUtils.isNotEmpty(oldEndPoint)){
            return oldEndPoint;
        }
        return inforbipConfig.getHost();
    }

    /**
     *  Called by 
     *  1. PrdDataHandler.java sendEmergencyCall
     *  2. AutoCallErrorService.java resendCall
     *  3. AutoCallService.java sendFirstBorrowAutoCall
     *  4. AutoCallService.java sendReBorrowingAutoCall
     *  5. AutoCallScheduler.java autoCall -> Currently deactivated
     * */
    public void sendVoiceMessage(List<InforbipRequest> requestList) {
        // log.info("request numbers: {}",JsonUtils.serialize(requestList));
        log.info("InfobipService - sendVoiceMessage - start");
        InforbipResponse response;
        if(CollectionUtils.isEmpty(requestList)){
            log.info("sendVoiceMessage - the request param is empty");
            return;
        }
        requestList = requestList.stream().filter(elem -> StringUtils.isNotEmpty(elem.getMobileNumber())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(requestList)) {
            log.info("sendVoiceMessage - request param is empty,numbers: {}", JsonUtils.serialize(requestList));
            return;
        }

        //List<InforbipRequest> ownerList;
        //One day later, after the switch was turned off, some of my outgoing calls were not completed. Continue to use twilio
        //ownerList = requestList.stream().filter(elem -> isTwilioMobileCall(elem.getMobileNumber())).collect(Collectors.toList());
        //Use twilio to make outbound calls
        //logErrorThenResendVoiceByTwilio(ownerList);
        
        List<InforbipRequest> unOwnerList;
        unOwnerList = requestList.stream().filter(elem -> !isTwilioMobileCall(elem.getMobileNumber())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(unOwnerList)) {
            log.info("no unOwner data send.");
            return ;
        }
        Map<String, List<InforbipRequest>> requestParamMap;
        
        requestParamMap = unOwnerList.stream().collect(Collectors.groupingBy(InforbipRequest::getMobileNumber));

        //How to process different orders with the same mobile phone number? [Group by number]
        try {
            String[] numbers = requestParamMap.keySet().toArray(new String[0]);
            if (requestParamMap.size() > 1) {
                response = numberMultiple(numbers, null);
            } else {
                response = numberSingle(numbers[0], null);
            }
            if (response == null) {
                String errorMessage = "sendVoiceMessage - Infobip response is null";
                throw new Exception(errorMessage);
            }
            if (CollectionUtils.isEmpty(response.getMessages())) {
                String errorMessage = "sendVoiceMessage - Infobip response is empty";
                throw new Exception(errorMessage);
            }
            for (ReportMessage message : response.getMessages()) {
                log.info("sendVoiceMessage - Infobip response: {}", JsonUtils.serialize(message));
                List<InforbipRequest> mobileNumberRelatedOrders = requestParamMap.get(message.getTo());
                if (CollectionUtils.isEmpty(mobileNumberRelatedOrders)) {
                    log.error("Infobip response missmatch. Response received: to={}", message.getTo());
                    // ahalim: Not sure what to do here. This should be never happen.
                    continue;
                }
                for (InforbipRequest item : mobileNumberRelatedOrders) {
                    String bulkId = response.getBulkId();
                    String messageId = message.getMessageId();

                    TeleCallResult call = new TeleCallResult();
                    call.setDisabled(0);
                    call.setOrderNo(item.getOrderNo());
                    call.setUserUuid(item.getUserUuid());
                    call.setCallNode(item.getCallNode());
                    call.setCallType(item.getCallType());
                    call.setTellNumber(message.getTo());
                    call.setCallState(TeleCallResult.CallStatusEnum.CALL_SEND.getCode());
                    call.setCallBulkId(bulkId);
                    call.setCallMsgId(messageId);
                    if(StringUtils.isEmpty(messageId)){
                        call.setCallResponse(JsonUtils.serialize(message));
                    }
                    this.teleCallResultDao.insert(call);
                }
            }
        } catch (Exception e) {
            log.error("sendVoiceMessage error", e);
            String errorMessage = String.format("sendVoiceMessage - Error %.100s", e.toString());
            logErrorTeleCallResult(unOwnerList, errorMessage);
        }
        log.info("InfobipService - sendVoiceMessage - end");
    }

    private void logErrorTeleCallResult(List<InforbipRequest> requestList, String remark) {
        for (InforbipRequest item : requestList) {
            TeleCallResult call = new TeleCallResult();
            call.setDisabled(0);
            call.setOrderNo(item.getOrderNo());
            call.setUserUuid(item.getUserUuid());
            call.setCallNode(item.getCallNode());
            call.setCallType(item.getCallType());
            call.setTellNumber(item.getMobileNumber());
            call.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
            call.setRemark(remark);
            this.teleCallResultDao.insert(call);
        }
    }

    //region Get Infobip report
    /**
     *  Query the phone call, use BulkId and msgId to get the report
     *  Called by DealWithDataTask.java getReportWithMode -> doit-task
     * */
    public void getReport(int mode) throws Exception{

        List<TeleCallResult> callResults = this.teleCallResultDao.getCallSendTeleCallResult(mode);

        getReportForList(callResults,mode);
    }
    
    // Called by this.getReport & this.getReportWithOldEndPoint
    private void getReportForList(List<TeleCallResult> callResults, int mode){
        if(CollectionUtils.isEmpty(callResults)){
            log.info("InfobipService getReportForList - Nothing to process");
            return;
        }
        log.info("Get Infobip report - start size={} mode= {}", callResults.size(), mode);

        //To prevent batch sending, the same mobile phone number is sent only once, and record the same msgId and bulkId. 
        //Need to group more msgId and bulkId
        Map<String,List<TeleCallResult>> callResultMap = callResults.stream()
            .collect(Collectors.groupingBy(
                elem -> elem.getCallMsgId()+"#"+ elem.getCallBulkId()));
        for(String keyId: callResultMap.keySet()){
            List<TeleCallResult> relatedCallResult = callResultMap.get(keyId);
            String bulkId = keyId.split("#")[1];
            String msgId = keyId.split("#")[0];
            String mobile = callResultMap.get(keyId).get(0).getTellNumber();
            log.info("The phone number to get the report is {}, bulkId is {}, and msgId is {} ", mobile, bulkId, msgId);
            try {
                // Get report based on bulkId and msgId
                GetReportResponse getReportResponse = getReportWithId(msgId, bulkId);
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
                    //ahalim disable send to twilio
                    //resendWithError(elem);
                    this.teleCallResultDao.update(elem);
                });
            }
        }
        //afterReport()-->Check whether the outgoing contact of the contact is over, and determine whether it is necessary to replace its emergency contact
        log.info("Get Infobip report - end mode= {}", mode);
    }
    
    // ahalim remark unused method
    // public void getReportWithOldEndPoint() {
    //     List<TeleCallResult> callResults = this.teleCallResultDao.getTeleCallResultForHistory();
    //     getReportForList(callResults, 0);
    // }

    // !!! Notes !!! that once the report has been retrieved, we can't get it anymore (deleted by infobip)
    public GetReportResponse getReportWithId(String messageId,String bulkId) throws Exception{

        GetReportResponse getReportResponse = new GetReportResponse();

        String keyStr = this.inforbipConfig.getUserName() + ":" + this.inforbipConfig.getPassword();

        Request request = new Request.Builder()
                .url(getInforbipHost()+this.inforbipConfig.getReportUrl()+"?messageId="+messageId+"&bulkId="+bulkId)
                .header("Authorization", "Basic "+Base64Utils.encode(keyStr.getBytes()))
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .build();

        // this.sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0, order.getUuid(),null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            log.info("bulkId:{} messageId:{} response:{}",bulkId, messageId, JsonUtils.serialize(responseStr));
            getReportResponse = JsonUtils.deserialize(responseStr,GetReportResponse.class);
            //  sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0,null,responseStr);
        }else {
            log.info("bulkId:{} messageId:{} response: {}", bulkId, messageId, response == null ? null : JsonUtils.serialize(response));
        }
        return getReportResponse;
    }

    // Called by this.getReportForList
    // Process report data (Update TeleCallResult)
    private void dealWithResultWithReportData(GetReportResponse.Results result, TeleCallResult call) {

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
        //ahalim disable resend using twilio
        //resendWithError(call);
        this.teleCallResultDao.update(call);

    }
    //endregion Get Infobip report

    //region Call using infobip
    /**
     *  Infobip Send voice verification code (single person) 
     *  Called by InforbipService.java sendVoiceMessage
     *  Start with 62
     *  If it is a mobile phone number 08
     *  If it is a fixed phone 021
     * */
    public InforbipResponse numberSingle(String mobileNumber, String fileUrl) throws Exception{

        if (this.isAutoCallWhitelistOpen()) {
            log.info("risk:auto_call:whitelist:number is open");
            if (!Arrays.stream(this.getAutoCallWhitelistedNumbers()).anyMatch(mobileNumber::equals)) {
                log.warn("The number {} is not whitelisted. Aborting auto call", mobileNumber);
                return null;
            }
        }

        InforbipResponse inforbipResponse = new InforbipResponse();
        String keyStr = this.inforbipConfig.getUserName() + ":" + this.inforbipConfig.getPassword();

        InfobipRequestMessage message =new InfobipRequestMessage();
        message.setFrom(this.inforbipConfig.getFromTel());
        message.setTo(new String[]{mobileNumber});
        if (!StringUtils.isEmpty(fileUrl)) {
            message.setAudioFileUrl(fileUrl);
        }
        else {
            message.setLanguage("id");
            message.setText("Terima kasih telah mendaftar duit");
            InfobipVoiceRequestMessage voice = new InfobipVoiceRequestMessage();
            voice.setName("Andika");
            voice.setGender("male");
            message.setVoice(voice);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),JSONObject.toJSON(message).toString());

        log.info("Infobip call single number: {}",mobileNumber);
        Request request = new Request.Builder()
                .url(getInforbipHost()+this.inforbipConfig.getMobileLookUpUrl())
                .post(requestBody)
                .header("Authorization", "Basic "+Base64Utils.encode(keyStr.getBytes()))
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .build();
        // this.sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0, order.getUuid(),null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            log.info("Infobip call single number response: {}", JsonUtils.serialize(responseStr));
            inforbipResponse = JsonUtils.deserialize(responseStr,InforbipResponse.class);
            //  sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0,null,responseStr);
        }else {
            log.error("Infobip call single number failed: "+response);
        }
         return inforbipResponse;
    }
    
    /**
     *  Call using Inforbip
     *  Called by:
     *  1. InforbipCollectionService.java startCallInfobip
     *  2. InforbipService.java sendVoiceMessage
     *  Send voice verification code (multiple people at a time)
     *  Start with 62
     *  If it is a mobile phone number 08 
     *  If it is a fixed phone 021
     * */
    public InforbipResponse numberMultiple(String[] mobileNumbers, String fileUrl) throws Exception{

        if (this.isAutoCallWhitelistOpen()) {
            log.info("risk:auto_call:whitelist:number is open");
            String[] matched_numbers = Arrays.stream(mobileNumbers)
                .filter(el -> Arrays.stream(this.getAutoCallWhitelistedNumbers()).anyMatch(el::equals))
                .toArray(String[]::new);
            log.info("Requested number:{} ", mobileNumbers);
            log.info("Matched whitelisted number: {}", matched_numbers);
            if (mobileNumbers.length != matched_numbers.length) {
                log.warn("There is a non-whitelisted mobile number. Aborting auto call");
                return null;
            }
        }

        InforbipResponse inforbipResponse = new InforbipResponse();
        String keyStr = this.inforbipConfig.getUserName() + ":" + this.inforbipConfig.getPassword();

        InfobipRequestMessage message =new InfobipRequestMessage();
        message.setFrom(this.inforbipConfig.getFromTel());
        message.setTo(mobileNumbers);
        if (!StringUtils.isEmpty(fileUrl)) {
            message.setAudioFileUrl(fileUrl);
        }
        else {
            message.setLanguage("id");
            message.setText("Terima kasih telah mendaftar duit");
            InfobipVoiceRequestMessage voice = new InfobipVoiceRequestMessage();
            voice.setName("Andika");
            voice.setGender("male");
            message.setVoice(voice);
        }
        
        InfobipRequestMessages messages = new InfobipRequestMessages();
        List<InfobipRequestMessage> messageList = new ArrayList<>();
        messageList.add(message);
        messages.setMessages(messageList);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),JSONObject.toJSON(messages).toString());

        log.info("Infobip call multiple numbers: "+JsonUtils.serialize(mobileNumbers));

        Request request = new Request.Builder()
                .url(getInforbipHost()+this.inforbipConfig.getMobileLookUpUrlMulti())
                .post(requestBody)
                .header("Authorization", "Basic "+Base64Utils.encode(keyStr.getBytes()))
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            log.info("Infobip call multiple number response: {}", JsonUtils.serialize(responseStr));
            inforbipResponse = JsonUtils.deserialize(responseStr,InforbipResponse.class);
        }else {
            log.error("Infobip call multiple number failed: "+response);
        }
        return inforbipResponse;
    }
    //endregion Call using infobip

    /***
     * @param orderNo
     * @param callNode
     * @return
     */
    public List<TeleCallResult> getTelCallList(String orderNo, TeleCallResult.CallNodeEnum callNode) {
        TeleCallResult searchEntity = new TeleCallResult();
        searchEntity.setOrderNo(orderNo);
        if(callNode!=null) {
            searchEntity.setCallNode(callNode.getCode());
        }
        searchEntity.setDisabled(0);
        return teleCallResultDao.scan(searchEntity);
    }


    //region Retry auto call using Twilio
    private boolean isTwilioMobileCall(String mobile) {

        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        return redisClient.sisMember(RedisContants.TWILIO_TEST_MOBILES, mobile);
    }

    /**
     * Use twilio to make outbound calls
     * @param requestList
     */
    private void logErrorThenResendVoiceByTwilio (List<InforbipRequest> requestList) {
        for (InforbipRequest item : requestList) {
            TeleCallResult call = new TeleCallResult();
            call.setDisabled(0);
            call.setOrderNo(item.getOrderNo());
            call.setUserUuid(item.getUserUuid());
            call.setCallNode(item.getCallNode());
            call.setCallType(item.getCallType());
            call.setTellNumber(item.getMobileNumber());
            call.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
            //Call twilio to send
            resendWithError(call);
            this.teleCallResultDao.insert(call);
        }
    }

    private void resendWithError(TeleCallResult result){
        if(result ==null){
            return ;
        }

        if(result.getCallState() == TeleCallResult.CallStatusEnum.CALL_ERROR.getCode()){
            //Error -> Call using twilio
            sendTwilioCall(result);
            return;
        }

        if(result.getCallType()!=TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode()){
            return;
        }

        if(result.getCallResultType() == TeleCallResult.CallResultTypeEnum.INVALID.getCode()
                || result.getCallResultType() == TeleCallResult.CallResultTypeEnum.NOT_SURE.getCode()){
            //Invalid -> Call using twilio
            sendTwilioCall(result);
            return;
        }
    }

    public void sendTwilioCall(TeleCallResult result){
        log.info("sent with twilio, orderNo: {}, phone: {}", result.getOrderNo(), result.getTellNumber());
        TwilioUserInfoResponse requestParam = new TwilioUserInfoResponse();
        requestParam.setOrderNo(result.getOrderNo());
        requestParam.setUserUuid(result.getUserUuid());
        requestParam.setCallNode(result.getCallNode());
        requestParam.setCallType(result.getCallType());
        requestParam.setPhoneNumber("+"+result.getTellNumber());
        requestParam.setCallPhase("-");
        requestParam.setCallPhaseType(0);
        requestParam.setCallUrl(twilioConfig.getDemoUrl());
        String from = twilioService.getTwilioFromNumber();
        if (StringUtils.isEmpty(from)) {
            log.error("get twilio from phone number is error.");
            return ;
        }
        requestParam.setFrom(from);
        twilioService.initTwilio();
        twilioService.callTwilio(requestParam);
    }
    //endregion Retry auto call using Twilio

    public boolean isAutoCallWhitelistOpen() {
        return "true".equalsIgnoreCase(sysParamService.getSysParamValue(SysParamContants.RISK_AUTO_CALL_WHITELIST_SWITCH));
    }

    public String[] getAutoCallWhitelistedNumbers() {
        return sysParamService.getSysParamValue(SysParamContants.RISK_AUTO_CALL_WHITELIST_NUMBER)
            .split(",");
    }
}
