package com.yqg.drools.service;

import com.yqg.service.RuleExecuteSourceThreadLocal;
import com.yqg.service.third.advance.AdvanceService;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.RUserInfo.IdentityVerifyResult;
import com.yqg.drools.model.RUserInfo.IdentityVerifyResultType;
import com.yqg.service.util.RuleConstants;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.SmsService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.user.service.UserVerifyResultService;
import com.yqg.service.user.service.UsrCertificationService;
import com.yqg.user.entity.UsrCertificationInfo;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrVerifyResult;
import com.yqg.user.entity.UsrVerifyResult.VerifyResultEnum;
import com.yqg.user.entity.UsrVerifyResult.VerifyTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 * 依图实名验证
 ****/

@Service
@Slf4j
public class YiTuVerificationService {
    @Autowired
    private UsrCertificationService usrCertificationService;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private UsrBlackListService usrBlackListService;
    @Autowired
    private RealNameVerificationService realNameVerificationService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private UserVerifyResultService userVerifyResultService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdvanceService advanceService;



    public IdentityVerifyResult verifyIdentity(String realName, String idCardNo,
                                               OrdOrder order) {
        //检查是否有相同税卡号，相同姓名的审核通过的用户
        String taxNumber = usrCertificationService.getTaxNumber(order.getUserUuid());
        if(userRiskService.hasSameRealNameAndTaxNumber(realName,taxNumber,order.getUserUuid())){
            log.warn("userUuid: {} blackList",order.getUserUuid());
            usrBlackListService.addFraudUserBlackList(order.getUserUuid());
            return new IdentityVerifyResult(IdentityVerifyResultType.REAL_NAME_TAX_NUMBER_BLACKLIST, RuleConstants.REAL_NAME_TAX_NUMBER_FRAUD_USER_DESC);
        }
        //检查用户身份证姓名是否已经验证成功
        UsrCertificationInfo identityCertificationInfo = usrCertificationService.getSuccessVerificationInfo(order.getUserUuid(),CertificationEnum
                .USER_IDENTITY);
        if (identityCertificationInfo!=null) {
            if (StringUtils.isEmpty(identityCertificationInfo.getRemark())) {
                log.info("{}:用户之前实名认证已经认证成功", order.getUserUuid());
                return new IdentityVerifyResult(IdentityVerifyResultType.FORWARD_MATCH_SUCCESS, "用户之前实名认证已经认证成功");
            } else {
                if("IZI".equals(identityCertificationInfo.getRemark())){
                    log.info("{}:IZI实名认证已经认证成功", order.getUserUuid());
                    return new IdentityVerifyResult(IdentityVerifyResultType.IZI_REAL_NAME_VERIFY_ALREADY_SUCCESS, "IZI实名认证已经认证成功");
                }else if (identityCertificationInfo.getRemark().equals("JXL")) {
                    log.info("{}:聚信立实名认证已经认证成功", order.getUserUuid());
                    return new IdentityVerifyResult(IdentityVerifyResultType.JUXINLI_MATCH_SUCCESS, "聚信立实名认证已经认证成功");
                // } else if("XENDIT".equals(identityCertificationInfo.getRemark())){
                //     log.info("{}: xendit实名认证已经认证成功", order.getUserUuid());
                //     return new IdentityVerifyResult(IdentityVerifyResultType.XENDIT_REALNAME_VERIFY_ALREADY_SUCCESS, "xendit已经实名验证通过");
                }else {
                    log.info("{}:用户之前实名认证已经认证成功", order.getUserUuid());
                    return new IdentityVerifyResult(IdentityVerifyResultType.FORWARD_MATCH_SUCCESS, "用户之前实名认证已经认证成功");
                }
            }
        }

        //检查税卡验证是否已经成功
        UsrCertificationInfo taxNumberVerification = usrCertificationService.getSuccessVerificationInfo(order.getUserUuid(),
                CertificationEnum.STEUERKARTED);
        if (taxNumberVerification != null) {
            log.info("税卡验证已经成功: orderNo: {}", order.getUuid());
            return new IdentityVerifyResult(IdentityVerifyResultType.TAX_NUMBER_VERIFY_ALREADY_SUCCESS, "税卡已经实名验证通过");
        }

        //检查izi验证是否成功
        UsrVerifyResult iziVerify = userVerifyResultService.getVerifyResult(order.getUuid(),order.getUserUuid(),VerifyTypeEnum.IZI_PHONE);
        if(iziVerify!=null&&iziVerify.getVerifyResult()!=null && iziVerify.getVerifyResult()==VerifyResultEnum.SUCCESS.getCode()){
            log.info("izi验证已经成功");
            return new IdentityVerifyResult(IdentityVerifyResultType.IZI_PHONE_IDCARD_VERIFY_ALREADY_SUCCESS, "izi验证已经成功");
        }
        
        if(advanceService.isAdvanceSwitchOn()){
            // 走advance实名认证
            //记录认证初始化
            String executeSource = RuleExecuteSourceThreadLocal.getSource();
            IdentityVerifyResult yituResult = null;
            userVerifyResultService.initVerifyResult(order.getUuid(), order.getUserUuid(), VerifyTypeEnum.ADVANCE);

            yituResult = realNameVerificationService.advanceVerification(realName, idCardNo, order);
            boolean isAdvanceVerifySuccess = isAdvanceRealNameVerifySuccess(yituResult);

            userVerifyResultService.updateVerifyResult(order.getUuid(), order.getUserUuid(), yituResult.getDesc(),
                    isAdvanceVerifySuccess ? VerifyResultEnum.SUCCESS : VerifyResultEnum.FAILED, VerifyTypeEnum.ADVANCE);
            return yituResult;
        }
        else{
            return new IdentityVerifyResult(IdentityVerifyResultType.IZI_PHONE_IDCARD_VERIFY_FAILED, "izi验证已经成功");
        }
    }


    private boolean isAdvanceRealNameVerifySuccess(IdentityVerifyResult result) {
        //姓名完全匹配成功/姓名模糊匹配成功
        if (result.getAdvanceVerifyResultType().equals(IdentityVerifyResultType.NAME_FULL_MATCH_SUCCESS)
                || result.getAdvanceVerifyResultType().equals(IdentityVerifyResultType.NAME_FUZZY_MATCH_SUCCESS)) {
            return true;
        }
        return false;
    }
    private boolean isXenditRealNameVerifySuccess(IdentityVerifyResult result) {
        //姓名匹配
        if (result.getAdvanceVerifyResultType().equals(IdentityVerifyResultType.XENDIT_REALNAME_VERIFY_MATCH)
                || result.getAdvanceVerifyResultType().equals(IdentityVerifyResultType.XENDIT_REALNAME_VERIFY_ALREADY_SUCCESS)) {
            return true;
        }
        return false;
    }



    private boolean isTaxNumberRealNameVerifySuccess(IdentityVerifyResult result){
        if(result==null){
            return false;
        }
        if(result.getAdvanceVerifyResultType().equals(IdentityVerifyResultType.TAX_VERIFY_FULL_MATCH)||
                result.getAdvanceVerifyResultType().equals(IdentityVerifyResultType.TAX_VERIFY_FUZZY_MATCH_SUCCESS)){
            return true;
        }
        return false;
    }


    private void sendSmsmForEmptyTaxNumber(String userUuid) {
        UsrUser user = userService.getUserInfo(userUuid);
        try {
            smsService.sendToRejectUserByNotRealNameAndNoSteuerkarted(user);
        } catch (Exception e) {
            log.error("send sms error for empty taxNumber", e);
        }
    }



}
