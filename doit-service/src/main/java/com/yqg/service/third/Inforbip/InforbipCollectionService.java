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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        //每次固定发送两个电话号码
//        TeleVoiceMessageResult result1 = new TeleVoiceMessageResult();
//        result1.setUserUuid("testUserUuid1");
//        result1.setOrderNo("testOrderNo1");
//        result1.setTellNumber("+6287787117873");
//        result1.setTellNumber("+6287787117873");
//        lists.add(result1);

        //set phone number.
        List<TwilioUserInfoResponse> requests = lists.stream().map(elem -> new TwilioUserInfoResponse(elem.getOrderNo()
                ,elem.getUserUuid(),
                elem.getTellNumber(), request.getCallUrl(), request.getCallPhase(),
                request.getCallPhaseType(), request.getBatchNo())).filter(e -> StringUtils.isNotEmpty(e.getPhoneNumber()))
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
            getVoiceMessageResults(users, result);
            //申请未提交
        } else if (request.getCallPhaseType() == 4) {
            users = usrDao.sendSmsToUseWithNotVerifyOrder();
            if (CollectionUtils.isEmpty(users)) {
                return null;
            }
            getVoiceMessageResults(users, result);
            //降额未确认
        } else if (request.getCallPhaseType() == 5) {
            users = usrDao.sendReduceSms();
            if (CollectionUtils.isEmpty(users)) {
                return null;
            }
            getVoiceMessageResults(users, result);
        }
        return result;
    }

    private void getVoiceMessageResults(List<UserResponse> users, List<TeleVoiceMessageResult> result) {

        for (UserResponse elem: users) {
            TeleVoiceMessageResult response = new TeleVoiceMessageResult();
            response.setOrderNo(elem.getOrderNo());
            response.setTellNumber(elem.getMobileNumberDES());
            response.setUserUuid(elem.getUuid());
            result.add(response);
        }
    }




    public void getReport(int mode) throws Exception{

        List<TeleVoiceMessageResult> callResults = teleVoiceMessageResultDao.getTeleCallResult(mode);

        getReportForList(callResults,mode);
    }

    private void getReportForList(List<TeleVoiceMessageResult> callResults, int mode){
        if(CollectionUtils.isEmpty(callResults)){
            return;
        }
        log.info("get report count is : {}, mode= {}", callResults.size(), mode);

        //msgId and bulkId group by.
        Map<String,List<TeleVoiceMessageResult>> callResultMap = callResults.stream().collect(Collectors
                .groupingBy(elem->elem.getCallMsgId()+"#"+ elem.getCallBulkId()));

        for(String keyId: callResultMap.keySet()){
            List<TeleVoiceMessageResult> relatedCallResult = callResultMap.get(keyId);
            String bulkId = keyId.split("#")[1];
            String msgId = keyId.split("#")[0];
            String mobile = callResultMap.get(keyId).get(0).getTellNumber();
            log.info("phone mobile is {}, bulkId is {},  msgId is {} ", mobile, bulkId, msgId);
            try {
                // 根据bulkId和msgId获取报告
                GetReportResponse getReportResponse = inforbipService.getReportWithId(msgId, bulkId);
                List<GetReportResponse.Results> resultsList = getReportResponse.getResults();
                if (!CollectionUtils.isEmpty(resultsList)) {
                    for (GetReportResponse.Results result : resultsList) {
                        // 如果获取到报告 根据errorGroupId 和 errorId判断外呼的结果
                        relatedCallResult.stream().forEach(elem->dealWithResultWithReportData(result, elem));
                    }
                } else {
                    log.error("get report is null.");
                }
            } catch (Exception e) {
                log.error("get report is error", e);
                relatedCallResult.stream().forEach(elem->{
                    elem.setErrorGroupId(0);
                    elem.setErrorId(0);
                    // 呼叫完成（已经获取到报告）
                    elem.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
                    // 存储外呼结果
                    elem.setCallResponse("get Report infobip is error " + e.getMessage());
                    teleVoiceMessageResultDao.update(elem);

                });

            }
        }
        log.info("finished the get infobip collection report...");
    }


    /**
     * dealWith result.
     * @param result
     * @param call
     */
    public void dealWithResultWithReportData(GetReportResponse.Results result, TeleVoiceMessageResult call) {

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

            default:
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
        teleVoiceMessageResultDao.update(call);

    }
}
