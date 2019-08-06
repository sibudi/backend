package com.yqg.service.risk.service;

import com.github.pagehelper.StringUtil;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.third.Inforbip.InforbipService;
import com.yqg.service.third.Inforbip.Request.InforbipRequest;
import com.yqg.service.third.twilio.TwilioService;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.system.entity.TwilioCallResult;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AutoCallSendService {
    @Autowired
    private UsrService usrService;
    @Autowired
    private InforbipService inforbipService;

    @Autowired
    private OrdService ordService;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;
    @Autowired
    private TwilioService twilioService;


    public void sendOwnerCall(OrdOrder order) {
        UsrUser user = usrService.getUserByUuid(order.getUserUuid());
        if (user == null) {
            return;
        }
        String mobileNumber = "62" + DESUtils.decrypt(user.getMobileNumberDES());
        //检查同一订单是否已经发送过
        List<TeleCallResult> callList = inforbipService.getTelCallList(order.getUuid(),null);
        boolean alreadySend = false;
        if (!CollectionUtils.isEmpty(callList)) {
            alreadySend =
                    callList.stream().filter(elem -> mobileNumber.equalsIgnoreCase(elem.getTellNumber())
                            &&elem.getCallType().equals(TeleCallResult.CallTypeEnum.OWNER.getCode())).findFirst().isPresent();
        }
        if (alreadySend) {
            log.info("the order already send owner phone, orderNo: {}", order.getUuid());
            return;
        }
        //未发送过外呼请求
        InforbipRequest owner = new InforbipRequest();
        owner.setOrderNo(order.getUuid());
        owner.setUserUuid(order.getUserUuid());
        owner.setCallNode(TeleCallResult.CallNodeEnum.BETWEEN_FIRST_REVIEW_AND_SECOND_REVIEW.getCode());
        owner.setCallType(TeleCallResult.CallTypeEnum.OWNER.getCode());
        owner.setMobileNumber("62" + DESUtils.decrypt(user.getMobileNumberDES()));
        inforbipService.sendVoiceMessage(Arrays.asList(owner));
    }


    /***
     * 查询某一个类型外呼情况
     * @param orderNo
     * @param callNode
     * @return
     */
    public List<CallResult> getTelCallList(String orderNo, TeleCallResult.CallNodeEnum callNode) {
        List<TeleCallResult> inforbipResultList = inforbipService.getTelCallList(orderNo, callNode);//inforbip结果

        List<TwilioCallResult> dbTwilioResultList = twilioService.listTwilioCallResultByOrderNo(orderNo);//twilio 结果

        List<TeleCallResult> twilioResultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dbTwilioResultList)) {
            twilioResultList =
                    dbTwilioResultList.stream()
                            .filter(elem -> elem.getCallNode() == TeleCallResult.CallNodeEnum.BETWEEN_FIRST_REVIEW_AND_SECOND_REVIEW.getCode()
                                    || elem.getCallNode() == TeleCallResult.CallNodeEnum.BETWEEN_MACHINE_CHECK_AND_FIRST_REVIEW.getCode())
                            .map(elem -> {
                                TeleCallResult result = new TeleCallResult();
                                result.setCreateTime(elem.getCreateTime());
                                result.setId(elem.getId());
                                result.setCallType(elem.getCallType());
                                result.setCallNode(elem.getCallNode());
                                result.setOrderNo(elem.getOrderNo());
                                result.setUserUuid(elem.getUserUuid());
                                result.setTellNumber(StringUtil.isNotEmpty(elem.getPhoneNumber()) && elem.getPhoneNumber().startsWith("+6") ?
                                        elem.getPhoneNumber().substring(1) : elem.getPhoneNumber());
                                //'外呼的结果:queued,ringing,in-progress,completed,busy,failed,no-answer,
                                if (StringUtil.isNotEmpty(elem.getCallResult()) && Arrays.asList("completed", "busy", "failed", "no-answer", "canceled").contains(elem.getCallResult())) {
                                    //外呼报告已经获得到了
                                    result.setCallState(TeleCallResult.CallStatusEnum.CALL_FINISHED.getCode());
                                } else if (elem.getCallState() == 3) {
                                    result.setCallState(TeleCallResult.CallStatusEnum.CALL_ERROR.getCode());
                                } else {
                                    result.setCallState(TeleCallResult.CallStatusEnum.CALL_SEND.getCode());
                                }
                                result.setErrorId("completed".equalsIgnoreCase(elem.getCallResult()) ? 5000 : 0);

                                if ("completed".equalsIgnoreCase(elem.getCallResult())) {
                                    result.setCallResultType(TeleCallResult.CallResultTypeEnum.VALID.getCode());
                                } else if (Arrays.asList("failed", "no-canceled").contains(elem.getCallResult())) {
                                    result.setCallResultType(TeleCallResult.CallResultTypeEnum.INVALID.getCode());
                                } else if (Arrays.asList("busy", "no-answer").contains(elem.getCallResult())) {
                                    result.setCallResultType(TeleCallResult.CallResultTypeEnum.VALID.getCode());
                                } else {
                                    result.setCallResultType(0);
                                }
                                return result;
                            }).collect(Collectors.toList());

        }

        List<CallResult> resultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(inforbipResultList)) {
            //error的数据都通过twillio重新外呼，
            List<TeleCallResult> filterOwnerResultList = new ArrayList<>();
            for(TeleCallResult item: inforbipResultList){
                if(item.getCallState()!=null&&item.getCallState() == TeleCallResult.CallStatusEnum.CALL_ERROR.getCode()){
                    //本人外呼忽略掉系统错误的记录
                    continue;
                }
                filterOwnerResultList.add(item);
            }

            List<CallResult> inforbipList = JsonUtils.toList(JsonUtils.serialize(filterOwnerResultList), CallResult.class);
            inforbipList.forEach(elem -> elem.setCallChannel(CallResult.CallResultEnum.INFORBIP));
            resultList.addAll(inforbipList);
        }
        if (!CollectionUtils.isEmpty(twilioResultList)) {
            List<CallResult> twilioList = JsonUtils.toList(JsonUtils.serialize(twilioResultList), CallResult.class);
            twilioList.forEach(elem -> elem.setCallChannel(CallResult.CallResultEnum.TWILIO));
            resultList.addAll(twilioList);
        }

        resultList = filterEmergencyCallResult(resultList, orderNo);

        return resultList;
    }

    private List<CallResult> filterEmergencyCallResult(List<CallResult> resultList, String orderNo) {
        if(CollectionUtils.isEmpty(resultList)){
            return resultList;
        }
        /***
         * 紧急联系人一个订单可能重复填多次进行外呼，需要做如下处理
         * 1、只筛选出最新填写的紧急联系人号码
         * 2、最新的紧急联系人号码如果有外呼多次的情况，只取最新的一次
         */


        OrdOrder order = ordService.getOrderByOrderNo(orderNo);
        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(order.getUserUuid());
        List<String> mobiles = linkmanList.stream().map(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getContactsMobile())).collect(Collectors.toList());

        //按号码分组，每个取最新的
        Map<String, Optional<CallResult>> emergencyLinkmanResult =
                resultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode())
                        .collect(Collectors.groupingBy(CallResult::getTellNumber, Collectors.maxBy(Comparator.comparing(CallResult::getCreateTime))));



        resultList = resultList.stream().filter(elem -> {
            if (elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode()) {
                String callMobile = CheakTeleUtils.telephoneNumberValid2(elem.getTellNumber());
                if (mobiles.contains(callMobile)) {
                    //id是否是最新一次的外呼id
                    Optional<CallResult> opt = emergencyLinkmanResult.get(elem.getTellNumber());
                    if(opt.isPresent() && opt.get().getId().equals(elem.getId()) && opt.get().getCallChannel().equals(elem.getCallChannel())){
                        return  true;
                    }
                }
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        return resultList;
    }

}
