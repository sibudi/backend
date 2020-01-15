package com.yqg.service.risk.service;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.order.OrdService;
import com.yqg.service.system.service.SysAutoReviewRuleService;
import com.yqg.service.third.advance.AdvanceService;
import com.yqg.service.third.advance.response.BlacklistCheckResponse;
import com.yqg.service.third.advance.response.MultiPlatformResponse;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.response.IziResponse.IziBlackListResponse;
import com.yqg.service.third.izi.response.IziResponse.IziMultiInquiriesV1Response;
import com.yqg.service.user.service.UsrService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

@Service
@Slf4j
public class RiskReviewService {

    @Autowired
    private AdvanceService advanceService;
    @Autowired
    private IziService iziService;
    @Autowired
    private UsrService usrService;

    @Autowired
    private OrderModelScoreService orderModelScoreService;
    @Autowired
    private BlackListManageService blackListManageService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private SysAutoReviewRuleService sysAutoReviewRuleService;
    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;
    @Autowired
    private RiskResultDao riskResultDao;

    /**
     * 600的首借用户进行黑名单多头检查
     *
     * @param order
     */
    public boolean hitBlackListOrMultiPlatform(OrdOrder order) {

        if (order.getBorrowingCount() != 1) {
            return false;
        }
        if (RuleConstants.PRODUCT600.compareTo(order.getAmountApply()) != 0) {
            return false;
        }
        try {
            UsrUser user = usrService.getUserByUuid(order.getUserUuid());
            if(advanceService.isAdvanceSwitchOn()){
                BlacklistCheckResponse response = advanceService.checkBlacklist(order, user.getRealName(), user.getIdCardNo(),
                DESUtils.decrypt(user.getMobileNumberDES()));

                if (response != null && response.isHitReject()) {
                    saveRejectRules(order, BlackListTypeEnum.ADVANCE_BLACKLIST);
                    return true;
                }
            }
            else{
                IziBlackListResponse response = iziService.checkBlacklist(user.getRealName(), user.getIdCardNo(), 
                    DESUtils.decrypt(user.getMobileNumberDES()), order);
                
                if (response != null && !"OK".equalsIgnoreCase(response.getStatus()) && !"REJECT".equalsIgnoreCase(response.getMessage()))  {
                    saveRejectRules(order, BlackListTypeEnum.ADVANCE_BLACKLIST);
                    return true;
                }
            }
            
            //检查600模型分数
            OrderScore orderScore = orderModelScoreService.getLatestScoreWithModel(order.getUuid(), "PRODUCT_600");
            if (orderScore == null || orderScore.getTotalScore() == null) {
                return false;
            }
            
            if (orderScore.getTotalScore().compareTo(new BigDecimal("505")) > 0) {
                return false;
            }

            if(advanceService.isAdvanceSwitchOn()){
                MultiPlatformResponse multiPlatformResponse = advanceService.checkMultiPlatform(order, user.getIdCardNo());
                if (multiPlatformResponse != null && multiPlatformResponse.isHitReject(user)) {
                    //reject
                    saveRejectRules(order, BlackListTypeEnum.ADVANCE_MULTI_PLATFORM);
                    return true;
                }
            }
            else{
                IziMultiInquiriesV1Response iziMultiInquiriesV1 = iziService.checkKtpMultiInquiriesV1(user.getIdCardNo(), order);
                if (iziMultiInquiriesV1 != null && iziMultiInquiriesV1.isHitReject(user)) {

                    saveRejectRules(order, BlackListTypeEnum.ADVANCE_MULTI_PLATFORM);
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("check advance blacklist and multi-platform error, orderNo: " + order.getUuid(), e);
            return false;
        }
    }

    // ADVANCE_BLACKLIST(2100025,"ADVANCE_BLACKLIST"),
    //                ADVANCE_MULTI_PLATFORM(2100025,"ADVANCE_MULTI_PLATFORM"),
    public void saveRejectRules(OrdOrder order, BlackListTypeEnum ruleTypeEnum) {
        //订单状态改为12
        ordService.changeOrderStatus(order, OrdStateEnum.MACHINE_CHECK_NOT_ALLOW);
        //增加ordBlack记录
        //增加ordRiskRecord记录
        SysAutoReviewRule rule = sysAutoReviewRuleService.getRuleConfigByName(ruleTypeEnum.getMessage());
        OrdRiskRecord record = new OrdRiskRecord();
        record.setOrderNo(order.getUuid());
        record.setUserUuid(order.getUserUuid());
        record.setRuleRealValue("true");

        record.setRuleType(rule.getRuleType());
        record.setRuleDetailType(rule.getRuleDetailType());
        record.setRuleDesc(rule.getRuleDesc());
        record.setUuid(UUIDGenerateUtil.uuid());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        orderRiskRecordRepository.addRiskRecordList(Arrays.asList(record));

        OrdBlack ordBlack = new OrdBlack();
        ordBlack.setOrderNo(order.getUuid());
        ordBlack.setUserUuid(order.getUserUuid());
        ordBlack.setResponseMessage(rule.getRuleDesc());
        ordBlack.setUuid(UUIDGenerateUtil.uuid());
        ordBlack.setRuleHitNo(rule.getRuleType() + "-" + rule.getRuleDetailType());
        ordBlack.setRuleRealValue("true");
        ordBlack.setRuleValue(rule.getRuleValue());
        ordBlack.setRuleRejectDay(rule.getRuleRejectDay());
        riskResultDao.addBlackList(Arrays.asList(ordBlack));


        //记录黑名单信息usrBlackList
        if (BlackListTypeEnum.ADVANCE_BLACKLIST.equals(ruleTypeEnum)) {
            log.info("add fraud user for user: {}", order.getUserUuid());
            blackListManageService.addFraudUserByOrders(Arrays.asList(order.getUuid()));
        }
    }

}
