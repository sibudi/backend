package com.yqg.service.third.Inforbip;


import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
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


    public String getInforbipHost(){
        String oldEndPoint = redisClient.get(RedisContants.INFORBIP_OLD_ENDPOINT);
        if(StringUtils.isNotEmpty(oldEndPoint)){
            return oldEndPoint;
        }
        return inforbipConfig.getHost();
    }

    /**
     *  测试一天直接使用twilio进行外呼
     * */
    public void sendVoiceMessage(List<InforbipRequest> requestList) {
        // log.info("request numbers: {}",JsonUtils.serialize(requestList));
        InforbipResponse response;
        if(CollectionUtils.isEmpty(requestList)){
            log.info("the request param is empty");
            return;
        }
        requestList = requestList.stream().filter(elem -> StringUtils.isNotEmpty(elem.getMobileNumber())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(requestList)) {
            log.info("the request param is empty,numbers: {}", JsonUtils.serialize(requestList));
            return;
        }

        //分开本人和非本人
        List<InforbipRequest> unOwnerList;
        List<InforbipRequest> ownerList;
        Map<String, List<InforbipRequest>> requestParamMap;
        //一天后开关关闭后 有些本人外呼没有完成的 继续使用twilio
        ownerList = requestList.stream().filter(elem -> isTwilioMobileCall(elem.getMobileNumber())).collect(Collectors.toList());
        unOwnerList = requestList.stream().filter(elem -> !isTwilioMobileCall(elem.getMobileNumber())).collect(Collectors.toList());
        //使用twilio进行本人外呼
        sendVoiceByTwilio(ownerList);
        if (CollectionUtils.isEmpty(unOwnerList)) {
            log.info("no unOwner data send.");
            return ;
        }
        requestParamMap = unOwnerList.stream().collect(Collectors.groupingBy(InforbipRequest::getMobileNumber));

        //相同手机号不同订单怎么处理？[按号码分组]
        try {
            String[] numbers = requestParamMap.keySet().toArray(new String[0]);
            if (requestParamMap.size() > 1) {
                response = numberMultiple(numbers, "http://h5.do-it.id/Test.mp3");
            } else {
                response = numberSingle(numbers);
            }
            if (response == null) {
                log.error("send voice Message, the response is null");
                throw new Exception("response is null");
            }
            if (CollectionUtils.isEmpty(response.getMessages())) {
                log.error("send voice Message, the response messages is empty ");
                throw new Exception("response msg empty");
            }
            for (ReportMessage message : response.getMessages()) {
                log.info("外呼返回 message: {}", JsonUtils.serialize(message));
                List<InforbipRequest> mobileNumberRelatedOrders = requestParamMap.get(message.getTo());
                if (CollectionUtils.isEmpty(mobileNumberRelatedOrders)) {
                    log.error("the related orders is empty, mobile: {}", message.getTo());
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
            log.error("send voice message error", e);
            sendVoiceByTwilio(unOwnerList);
        }

    }

    /**
     * 使用twilio进行外呼
     * @param requestList
     */
    private void sendVoiceByTwilio (List<InforbipRequest> requestList) {
        for (InforbipRequest item : requestList) {
            TeleCallResult call = new TeleCallResult();
            call.setDisabled(0);
            call.setOrderNo(item.getOrderNo());
            call.setUserUuid(item.getUserUuid());
            call.setCallNode(item.getCallNode());
            call.setCallType(item.getCallType());
            call.setTellNumber(item.getMobileNumber());
            call.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
            //调用twilio发送
            resendWithError(call);
            this.teleCallResultDao.insert(call);
        }
    }

    private boolean isTwilioMobileCall(String mobile) {

        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        return redisClient.sisMember(RedisContants.TWILIO_TEST_MOBILES, mobile);
    }

    /**
     *  查询通话过的电话，使用BulkId和msgId 去获取报告
     * */
    public void getReport(int mode) throws Exception{

        List<TeleCallResult> callResults = this.teleCallResultDao.getTeleCallResult(mode);

        getReportForList(callResults,mode);
    }

    private void getReportForList(List<TeleCallResult> callResults, int mode){
        if(CollectionUtils.isEmpty(callResults)){
            return;
        }
        log.info("需要获取的报告数量为: {}, mode= {}", callResults.size(), mode);

        //防止批量发送的时候相同的手机号码只发送了一次，记录相同的msgId和bulkId。需要更加msgId和bulkId分组
        Map<String,List<TeleCallResult>> callResultMap = callResults.stream().collect(Collectors.groupingBy(elem->elem.getCallMsgId()+"#"+ elem
                .getCallBulkId
                        ()));
        for(String keyId: callResultMap.keySet()){
            List<TeleCallResult> relatedCallResult = callResultMap.get(keyId);
            String bulkId = keyId.split("#")[1];
            String msgId = keyId.split("#")[0];
            String mobile = callResultMap.get(keyId).get(0).getTellNumber();
            log.info("获取报告的手机号为 {}, bulkId为{},  msgId为 {} ", mobile, bulkId, msgId);
            try {
                // 根据bulkId和msgId获取报告
                GetReportResponse getReportResponse = getReportWithId(msgId, bulkId);
                List<GetReportResponse.Results> resultsList = getReportResponse.getResults();
                if (!CollectionUtils.isEmpty(resultsList)) {
                    for (GetReportResponse.Results result : resultsList) {
                        // 如果获取到报告 根据errorGroupId 和 errorId判断外呼的结果
                        relatedCallResult.stream().forEach(elem->dealWithResultWithReportData(result, elem));
                    }
                } else {
                    log.error("获取到的报告为空，继续查询");
                }
            } catch (Exception e) {
                log.error("获取外呼报告异常", e);
                relatedCallResult.stream().forEach(elem->{
                    elem.setErrorGroupId(0);
                    elem.setErrorId(0);
                    // 呼叫完成（已经获取到报告）
                    elem.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
                    // 存储外呼结果
                    elem.setCallResponse("获取报告异常" + e.getMessage());
                    resendWithError(elem);
                    this.teleCallResultDao.update(elem);

                });

            }
        }
        //afterReport()-->检查联系人外呼是否结束，结束了判定是否需要替换其紧急联系人


        log.info("finished the get report...");
    }


    public void getReportWithOldEndPoint() {
        List<TeleCallResult> callResults = this.teleCallResultDao.getTeleCallResultForHistory();
        getReportForList(callResults, 0);
    }

    // 根据报告处理结果
    public void dealWithResultWithReportData(GetReportResponse.Results result, TeleCallResult call) {

        Integer errorGroupId = result.getError().getGroupId();
        Integer errorId = result.getError().getId();

        call.setErrorGroupId(errorGroupId);
        call.setErrorId(errorId);
        // 呼叫完成（已经获取到报告）
        call.setCallState(TeleCallResult.CallStatusEnum.CALL_FINISHED.getCode());

        switch (errorId){
            case 225: // 未知错误
                call.setCallResult(CallReusltEnum.UNKNOWN_ERROR.getCode());
                break;
            case 5000://  接通（人工应答电话）
            case 5001://  接通（呼叫已被语音机接收和应答）
                call.setCallResult(CallReusltEnum.CONNECT.getCode());
                break;
            case 5002:// 通话中/忙（用户在尝试呼叫时很忙）
                call.setCallResult(CallReusltEnum.BUSY.getCode());
                break;
            case 5003:// 无人接听（用户被通知，但没有应答呼叫）
                call.setCallResult(CallReusltEnum.NO_ANSWER.getCode());
                break;
            case 5004:// 文档不可用（HTTP请求中指定的文件不可访问，无法下载。）
                call.setCallResult(CallReusltEnum.ATTACHMENT_USELESS.getCode());
                break;
            case 5005:// 格式不支持（不支持指定文件的格式）
                call.setCallResult(CallReusltEnum.ATTACHMENT_FORMATT_NOT_SUPPORT.getCode());
                break;
            case 5400:// 格式不正确（收到的请求被拒绝，因为格式不正确）
                call.setCallResult(CallReusltEnum.REQUEST_FORMATT_NOT_SUPPORT.getCode());
                break;
            case 5403:// 已欠费（服务器理解请求，但拒绝执行）
                call.setCallResult(CallReusltEnum.ARREARS.getCode());
                break;
            case 5404:// 号码不存在
                call.setCallResult(CallReusltEnum.NUMBER_NOT_EXIST.getCode());
                break;
            case 5407:// 验证用户信息（该请求需要在运营商端进行用户身份验证）
                call.setCallResult(CallReusltEnum.NEED_VERIFY_USER_INFO.getCode());
                break;
            case 5408:// 无信号（未能及时找到用户）
                call.setCallResult(CallReusltEnum.NO_SIGNAL.getCode());
                break;
            case 5410:// 未到达（用户存在一次，但操作员不再支持目标地址）
                call.setCallResult(CallReusltEnum.NO_ARRIVE.getCode());
                break;
            case 5413:// 请求过大（请求实体主体大于服务器愿意或能够处理的主体）
                call.setCallResult(CallReusltEnum.REQUEST_TOO_LARGE.getCode());
                break;
            case 5414:// 拒绝处理（服务器拒绝处理请求，因为Request-URI比服务器长）
                call.setCallResult(CallReusltEnum.REFUSE_TO_DEAL.getCode());
                break;
            case 5415:// 格式不支持（不支持文件格式）
                call.setCallResult(CallReusltEnum.FORMATT_NOT_SUPPORT.getCode());
                break;
            case 5480:// 关机（被叫者（Callee）目前不可用）
                call.setCallResult(CallReusltEnum.DOWN_TINE.getCode());
                break;
            case 5484:// 号码无效
                call.setCallResult(CallReusltEnum.INVALID_NUMBER.getCode());
                break;
            case 5487:// 拒接（请求已被取消按钮终止，最终用户拒绝接听语音呼叫）
                call.setCallResult(CallReusltEnum.USER_REFUSE.getCode());
                break;
            case 5488:// 格式不支持（请求的格式在运营商端是不可接受的）
                call.setCallResult(CallReusltEnum.REQUEST_FORMATT_NOT_SUPPORT_IN_SERVER.getCode());
                break;
            case 5491:// 待处理请求（服务器具有来自同一对话框的一些待处理请求）
                call.setCallResult(CallReusltEnum.REQUEST_NEED_DEAL_WITH.getCode());
                break;
            case 5492:// 接通-拒绝（同样的文本已经发送到目的地）
                call.setCallResult(CallReusltEnum.REJECT_BY_OPERATOR.getCode());
                break;
            case 5500:// 错误（服务器内部错误）
                call.setCallResult(CallReusltEnum.SERVER_ERROR_IN.getCode());
                break;
            case 5501:// 未实现（没有实现）
                call.setCallResult(CallReusltEnum.NOT_REALIZE.getCode());
                break;
            case 5503:// 不在服务区（服务不可用）
                call.setCallResult(CallReusltEnum.NOT_ON_SERVER.getCode());
                break;
            case 5504:// 服务器超时
                call.setCallResult(CallReusltEnum.SERVER_TIME_OUT.getCode());
                break;
            case 5603:// 拒绝
                call.setCallResult(CallReusltEnum.REFUSE.getCode());
                break;

        }
        // 完全有效
        List<Integer> usefulList = Arrays.asList(5000,5002,5003,5603);
        // 无效
        List<Integer> uselessList = Arrays.asList(5404,5484,5492);

        if (usefulList.contains(errorId)){
            call.setCallResultType(1);
        }else if (uselessList.contains(errorId)){
            call.setCallResultType(3);
        }else {
            // 可能有效 5500/5603/5504/5501/255/5004/5005/5400/5407/5410/5413/5414/5415/5488/5491
            call.setCallResultType(2);
        }

        // 存储外呼结果
        call.setCallBeginTime(result.getStartTime());
        call.setCallEndTime(result.getEndTime());
        call.setCallDuration(result.getDuration());
        call.setCallResponse(JsonUtils.serialize(result));
        resendWithError(call);
        this.teleCallResultDao.update(call);

    }

    /**
     *  发送语音验证码（单人） to 必须是62开头  from 如果是手机号码 08开头 如果是固定电话 021开头
     * */
    public InforbipResponse numberSingle(String[] mobieNumber) throws Exception{

        InforbipResponse inforbipResponse = new InforbipResponse();

        String keyStr = this.inforbipConfig.getUserName() + ":" + this.inforbipConfig.getPassword();

        RequestBody requestBody = new FormBody.Builder()
                .add("from",this.inforbipConfig.getFromTel())   //
                .add("to",mobieNumber[0])
//                .add("text","this is a test")
                .add("language","id")
                .add("text", "Terima kasih telah mendaftar duit")
                .add("voice", "\"name\": \"Andika\", \"gender\": \"male\"")
                //.add("audioFileUrl","http://h5.do-it.id/Test.mp3")
                .build();

        log.info("request number: {}",mobieNumber);
        Request request = new Request.Builder()
                .url(getInforbipHost()+this.inforbipConfig.getMobileLookUpUrl())
                .post(requestBody)
                .header("Authorization", "Basic "+Base64Utils.encode(keyStr.getBytes()))
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .build();

        // 请求数据落库，SysThirdLogs
//        this.sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0, order.getUuid(),null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 查询放款响应
            log.info("外呼 请求后返回:{}", JsonUtils.serialize(responseStr));
             inforbipResponse = JsonUtils.deserialize(responseStr,InforbipResponse.class);
            // 响应数据落库，sysThirdLogs
//            sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0,null,responseStr);
        }else {
            log.error("外呼请求失败"+response);
        }
         return inforbipResponse;
    }

    /**
     *  发送语音验证码（一次多人）  to 必须是62开头  from 如果是手机号码 08开头 如果是固定电话 021开头
     * */
    public InforbipResponse numberMultiple(String[] mobieNumbers, String fileUrl) throws Exception{

        InforbipResponse inforbipResponse = new InforbipResponse();

        String keyStr = this.inforbipConfig.getUserName() + ":" + this.inforbipConfig.getPassword();

        // 拼接请求参数
        InfobipRequestMessage message =new InfobipRequestMessage();
        // message.setAudioFileUrl(fileUrl);
        message.setFrom(this.inforbipConfig.getFromTel());
        message.setTo(mobieNumbers); //mobieNumbers
        message.setLanguage("id");
        message.setText("Terima kasih telah mendaftar duit");

        InfobipVoiceRequestMessage voice = new InfobipVoiceRequestMessage();
        voice.setName("Andika");
        voice.setGender("male");
        message.setVoice(voice);

        InfobipRequestMessages messages = new InfobipRequestMessages();
        List<InfobipRequestMessage> messageList = new ArrayList<>();
        messageList.add(message);
        messages.setMessages(messageList);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),JSONObject.toJSON(messages).toString());

        log.info("外呼的电话号码为"+JsonUtils.serialize(mobieNumbers));

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
            // 查询放款响应
            log.info("外呼 请求后返回:{}", JsonUtils.serialize(responseStr));
            inforbipResponse = JsonUtils.deserialize(responseStr,InforbipResponse.class);
        }else {
            log.error("外呼请求失败"+response);
        }
        return inforbipResponse;
    }

    /**
     *  获取查询的报告
     * */
    public GetReportResponse getReportWithId(String messageId,String bulkId) throws Exception{

        GetReportResponse getReportResponse = new GetReportResponse();

        String keyStr = this.inforbipConfig.getUserName() + ":" + this.inforbipConfig.getPassword();

        Request request = new Request.Builder()
                .url(getInforbipHost()+this.inforbipConfig.getReportUrl()+"?messageId="+messageId+"&bulkId="+bulkId)
                .header("Authorization", "Basic "+Base64Utils.encode(keyStr.getBytes()))
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .build();
        // 请求数据落库，SysThirdLogs
//        this.sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0, order.getUuid(),null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 查询放款响应
            log.info("查询报告msgId: {} 请求后返回:{}",messageId, JsonUtils.serialize(responseStr));
            getReportResponse = JsonUtils.deserialize(responseStr,GetReportResponse.class);
            // 响应数据落库，sysThirdLogs
//            sysThirdLogsService.addSysThirdLogs(order.getUuid(),usrUser.getUuid(), SysThirdLogsEnum.CHEAK_LOAN.getCode(),0,null,responseStr);
        }else {
            log.info("查询报告请求失败,msgId: {}, response: {}", messageId, response == null ? null : JsonUtils.serialize(response));
        }
        return getReportResponse;
    }

    /***
     * 查询某一个类型外呼情况
     * @param orderNo
     * @param callNode
     * @return
     */
    public List<TeleCallResult> getTelCallList(String orderNo, TeleCallResult.CallNodeEnum callNode) {
        TeleCallResult searchEntity = new TeleCallResult();
        searchEntity.setOrderNo(orderNo);
        if(callNode!=null){
            searchEntity.setCallNode(callNode.getCode());
        }
        searchEntity.setDisabled(0);
        return teleCallResultDao.scan(searchEntity);
    }


    public void resendWithError(TeleCallResult result){
        if(result ==null){
            return ;
        }

        //针对外呼异常的情况本人增加外呼通道
        if(result.getCallState() == TeleCallResult.CallStatusEnum.CALL_ERROR.getCode()){
            //出错--》调用twilio 发送
            sendTwilioCall(result);
            return;
        }

        if(result.getCallType()!=TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode()){
            return;
        }

        if(result.getCallResultType() == TeleCallResult.CallResultTypeEnum.INVALID.getCode()
                || result.getCallResultType() == TeleCallResult.CallResultTypeEnum.NOT_SURE.getCode()){
            //无效--》调用twilio 发送
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

}
