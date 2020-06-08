package com.yqg.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.drools.service.UserService;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.risk.service.AutoCallSendService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.Inforbip.InforbipService;
import com.yqg.service.third.Inforbip.Request.InforbipRequest;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AutoCallService {
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;

    @Autowired
    private InforbipService inforbipService;

    @Autowired
    private AutoCallSendService autoCallSendService;

    @Autowired
    private UserService userService;

    @Autowired
    private NonManualReviewService nonManualReviewService;

    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private OrderScoreService orderScoreService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SysParamService sysParamService;

    public SendAutoCallResult sendFirstBorrowAutoCall(OrdOrder order) {
        boolean callOwner = false;
        boolean callCompanyTel = false;
        boolean callAllLinkman = false;
        List<InforbipRequest> sendList = new ArrayList<>();

//        //判定是否需要进行本人外呼【免核的需要在机审后外呼】
//        boolean hitNonManualRules = nonManualReviewService.isNonManualReviewOrder(order.getUuid());
//        boolean isDecreasedProduct = userRiskService.histSpecifiedProductWithDecreasedCreditLimit(order.getUuid());
//        OrderScore orderScore = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_600_V2);
//        //600评分模型测试数据特殊处理，如果需要人工无需外呼，不用考虑其他。
//        boolean scorePass = orderScore!=null && orderScore.getScorePass()!=null && orderScore.getScorePass() == 1;
//        boolean scoreManual = orderScore!=null && orderScore.getManualReview()!=null && orderScore.getManualReview() == 1;
//        boolean scorePassToCall = scorePass&& !scoreManual;  //分数通过非人工
//        boolean scoreNotPassToCall = !scorePass && (hitNonManualRules || isDecreasedProduct);  //分数没过 并且是（降额或者免核）
//        //取本人电话信息
//        Optional<InforbipRequest> ownerRequest = getOwnerAutoCallParam(order);
//        if (scorePassToCall || scoreNotPassToCall) {
//            log.info("non manual orders, need auto call after auto check ," +
//                            "orderNo: {}, scorePass: {},scoreManual: {}, hitNonManualRules: {}, scorePassTtoCall: {}, scoreNotPassToCall: {}",
//                    order.getUuid(), scorePass, scoreManual, hitNonManualRules, scorePassToCall, scoreNotPassToCall);
//            if (ownerRequest.isPresent()) {
//                sendList.add(ownerRequest.get());
//                callOwner = true;
//            }
//        }


        //取公司电话信息
        Optional<InforbipRequest> companyRequest = getCompanyAutoCallParam(order);
        if (companyRequest.isPresent()) {
            sendList.add(companyRequest.get());
            callCompanyTel = true;
        }
        //联系人信息
        List<InforbipRequest> linkManRequestParam = getLinkmanAutoCallRequest(order);
        if (CollectionUtils.isEmpty(linkManRequestParam)) {
            log.warn("the linkman auto call request param is empty, orderNo: {}", order.getUuid());
        } else {
            sendList.addAll(linkManRequestParam);
            callAllLinkman = true;
        }

        if (CollectionUtils.isEmpty(sendList)) {
            log.info("no linkman info to send auto call, orderNo: {}, userUuid: {}", order.getUuid(), order.getUserUuid());
            return new SendAutoCallResult(false);
        }
        inforbipService.sendVoiceMessage(sendList);

        return new SendAutoCallResult(callOwner || callAllLinkman);

    }


    public List<InforbipRequest> getLinkmanAutoCallRequest(OrdOrder order) {
        List<InforbipRequest> sendList = new ArrayList<>();
        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(order.getUserUuid());
        //Compatible app is not updated, the user has only two contacts
        if (CollectionUtils.isEmpty(linkmanList) || linkmanList.size() != 4) {
            log.warn("the linkman count is not 4");
            return sendList;
        }

        List<BackupLinkmanItem> backupLinkmanItemList = userBackupLinkmanService.getBackupLinkmanList(order.getUuid(), order.getUserUuid());
        if (!CollectionUtils.isEmpty(linkmanList)) {
            linkmanList.forEach(elem -> {
                Optional<InforbipRequest> request = buildLinkmanRequest(elem.getContactsMobile(), TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN,
                        order.getUuid(),
                        order.getUserUuid());
                if (request.isPresent()) {
                    sendList.add(request.get());
                }
            });
        }
//        if (!CollectionUtils.isEmpty(backupLinkmanItemList)) {
//            backupLinkmanItemList.forEach(elem -> {
//                Optional<InforbipRequest> request = buildLinkmanRequest(elem.getLinkmanNumber(), TeleCallResult.CallTypeEnum.BACKUP_LINKMAN,
//                        order.getUuid(),
//                        order.getUserUuid());
//                if (request.isPresent()) {
//                    sendList.add(request.get());
//                }
//            });
//        }
        return sendList;
    }

    public Optional<InforbipRequest> buildLinkmanRequest(String number, TeleCallResult.CallTypeEnum callType, String orderNo, String userUuid) {
        InforbipRequest request = new InforbipRequest();
        request.setOrderNo(orderNo);
        request.setUserUuid(userUuid);
        request.setCallNode(TeleCallResult.CallNodeEnum.BETWEEN_MACHINE_CHECK_AND_FIRST_REVIEW.getCode());
        request.setCallType(callType.getCode());
        String formatPhone = CheakTeleUtils.telephoneNumberValid2(number);
        if (StringUtils.isEmpty(formatPhone)) {
            return Optional.empty();
        }
        request.setMobileNumber("62" + formatPhone);
        return Optional.of(request);
    }

    public Optional<InforbipRequest> getOwnerAutoCallParam(OrdOrder order) {
        UsrUser user = userService.getUserInfo(order.getUserUuid());
        if (user == null) {
            return Optional.empty();
        }
        //未发送过外呼请求
        InforbipRequest owner = new InforbipRequest();
        owner.setOrderNo(order.getUuid());
        owner.setUserUuid(order.getUserUuid());
        owner.setCallNode(order.getStatus() == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode() ?
                TeleCallResult.CallNodeEnum.BETWEEN_FIRST_REVIEW_AND_SECOND_REVIEW.getCode() : TeleCallResult.CallNodeEnum.BETWEEN_MACHINE_CHECK_AND_FIRST_REVIEW.getCode());
        owner.setCallType(TeleCallResult.CallTypeEnum.OWNER.getCode());
        owner.setMobileNumber("62" + DESUtils.decrypt(user.getMobileNumberDES()));
        return Optional.of(owner);
    }

    public Optional<InforbipRequest> getCompanyAutoCallParam(OrdOrder order) {
        UsrUser user = userService.getUserInfo(order.getUserUuid());
        if (user == null) {
            return Optional.empty();
        }

        String companyTel = "";
        if (user.getUserRole() == 2) {
            //工作人员
            UsrWorkDetail userWorkDetail = userService.getUserWorkDetail(order.getUserUuid());
            companyTel = userWorkDetail != null ? userWorkDetail.getCompanyPhone() : "";
        } else if (user.getUserRole() == 3) {
            //家庭主妇
            UsrHouseWifeDetail userHouseWifeDetail = userService.getUserHouseWifeDetail(order.getUserUuid());
            companyTel = userHouseWifeDetail != null ? userHouseWifeDetail.getCompanyPhone() : "";
        }

        companyTel = companyTel.replaceAll("-", "");
        if (org.springframework.util.StringUtils.isEmpty(companyTel)) {
            return Optional.empty();
        }

        if (companyTel.substring(0, 1).equals("0")) {
            companyTel = companyTel.substring(1, companyTel.length());
        }

        if (!companyTel.startsWith("62")) {
            companyTel = "62" + companyTel;
        }
        InforbipRequest company = new InforbipRequest();
        company.setOrderNo(order.getUuid());
        company.setUserUuid(order.getUserUuid());
        company.setCallNode(TeleCallResult.CallNodeEnum.BETWEEN_MACHINE_CHECK_AND_FIRST_REVIEW.getCode());
        company.setCallType(TeleCallResult.CallTypeEnum.COMPANY.getCode());
        company.setMobileNumber(CheakTeleUtils.fetchNumbers(companyTel));
        return Optional.of(company);
    }


    public SendAutoCallResult sendReBorrowingAutoCall(OrdOrder order) {
        boolean needAutoUpdateBackupLinkman = userBackupLinkmanService.needAutoUpdateBackupLinkman(order.getUuid(), order.getUserUuid());
        if (needAutoUpdateBackupLinkman) {
            //Insert backupLinkMan from usrLinkMan previous order
            userBackupLinkmanService.copyFromLastOrder(order.getUserUuid(), order.getUuid());
        }
        //Generate Infobip request
        List<InforbipRequest> linkmanRequestList = getLinkmanAutoCallRequest(order);
        if (CollectionUtils.isEmpty(linkmanRequestList)) {
            return new SendAutoCallResult(false);
        }
        //Start Auto call
        inforbipService.sendVoiceMessage(linkmanRequestList);
        return new SendAutoCallResult(true);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendAutoCallResult {
        private boolean needCall;
    }


    /***
     * 查询某一个类型外呼情况
     * @param orderNo
     * @param callNode
     * @return
     */
    public List<CallResult> getTelCallList(String orderNo, TeleCallResult.CallNodeEnum callNode) {
        return autoCallSendService.getTelCallList(orderNo, callNode);
    }

    public boolean isAutoCallSwitchOpen() {
        return "true".equalsIgnoreCase(sysParamService.getSysParamValue(SysParamContants.RISK_AUTO_CALL_SWITCH));
    }
}
