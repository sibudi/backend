package com.yqg.service;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.risk.dao.RiskErrorLogDao;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.RuleApplicationService;
import com.yqg.drools.service.RuleService;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/***
 * 异常情况重跑数据service
 */

@Service
@Slf4j
public class BugFixTestService {

    @Autowired
    private OrdDao ordDao ;

    @Autowired
    private UsrService usrService;

    @Autowired
    private RuleApplicationService ruleApplicationService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RiskErrorLogDao riskErrorLogDao;

    //没有审核结果的单重跑
    public void reRunOrders(){
        List<String> orderNos = Arrays.asList("011808031602351520",
                "011808031558123750",
                "011808031559454210",
                "011808031601375280",
                "011808031547326060",
                "011808031544178020",
                "011808031545087490",
                "011808031541420720",
                "011808031543260440",
                "011808031525156260",
                "011808031452091740",
                "011808031553432290",
                "011808031558055480",
                "011808031556527100",
                "011808031547121990",
                "011808031543413830",
                "011808031524311750",
                "011807292236338590");

        List<OrdOrder> orders = ordDao.getOrderByOrderNos(orderNos);
        if(CollectionUtils.isEmpty(orders)){
            log.info("empty orders");
            return ;
        }

        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();
        for (OrdOrder order: orders){
            try {
                UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
                if (order.getBorrowingCount() >= 2) {// 复借用户

                    //新规则使用：
                    RuleSetExecutedResult ruleSetResult = ruleApplicationService
                            .reBorrow(order, allRules);
                    if (ruleSetResult.isRuleSetResult()) {
                        log.info("the order rerun {} is pass", order.getUuid());
                        // this.multiReviewPass(order, user);
                    } else {
                        //this.reviewRefuse(order, ruleSetResult.getFirstRejectRule());
                        log.info("the order rerun {} is rejected", order.getUuid());
                    }

                } else {// 初借用户

                    //新规则使用：
                    RuleSetExecutedResult ruleSetResult = ruleApplicationService
                            .firstBorrow(order, allRules);
                    if (ruleSetResult.isRuleSetResult()) {
                        log.info("the order rerun {} is pass", order.getUuid());
                        // this.multiReviewPass(order, user);
                    } else {
                        //this.reviewRefuse(order, ruleSetResult.getFirstRejectRule());
                        log.info("the order rerun {} is rejected", order.getUuid());
                    }
                }
            }catch (Exception e){
                log.info("the order {} bug fix rerun error",order.getUuid());
            }
        }
    }


    //因某些规则误拒绝订单，重新跑看是否可以成功：
    public void errorRejectOrderReRun(){
        List<OrdOrder> orderList = riskErrorLogDao.getOrdersWithErrorRules();
        if(CollectionUtils.isEmpty(orderList)){
            log.warn("the orderList is empty");
        }
        List<RuleSetEnum> firstBorrowRuleSet = Arrays
                .asList(RuleSetEnum.YQG_BLACK_LIST, RuleSetEnum.USER_IDENTITY,
                        RuleSetEnum.CONTACT_INFO, RuleSetEnum.USER_CALL_RECORDS, RuleSetEnum.SHORT_MESSAGE,
                        RuleSetEnum.INSTALL_APP, RuleSetEnum.DEVICE_INFO, RuleSetEnum.GOJEK,
                        RuleSetEnum.TOKOPEIDA, RuleSetEnum.FACEBOOK,
                        RuleSetEnum.BLACK_LIST_USER, RuleSetEnum.SPECIAL_RULE);

        Map<String,SysAutoReviewRule> allRules = ruleService.getAllRules();
        int count = 1;
        for(OrdOrder order:orderList){
            long startTime = System.currentTimeMillis();
            try {
                MDC.put("X-Request-Id", order.getUuid());
                Date date = riskErrorLogDao.getMachineCheckDate(order.getUuid());
                order.setApplyTime(date);
                order.setUpdateTime(date);
                List<Object> facts = ruleApplicationService.fetchRuleFacts(order, allRules, firstBorrowRuleSet);

                List<RuleResult> resultList = ruleService
                        .executeRules(RuleSetEnum.FIRST_BORROWING, facts, allRules);


                SysAutoReviewRule rejectRule = fetchRejectRule(resultList,allRules);
                String remark="rulePass";
                if(rejectRule != null){
                    remark = "ruleReject: "+rejectRule.getRuleDetailType();
                }
                riskErrorLogDao.addReRunResult(order.getUuid(),remark);

                log.info("execute " + RuleSetEnum.FIRST_BORROWING + " rules with ruleSetResult: "
                        + (rejectRule == null ? null : JsonUtils.serialize(rejectRule)));

                if(count%100==0){
                    log.info("finished count:  {}",count++);
                }

            } catch (Exception e) {
                log.error("execute " + RuleSetEnum.FIRST_BORROWING + " rules error, orderNo: " + order
                        .getUuid(), e);
            } finally {
                log.info("{} rules cost: {} ms", RuleSetEnum.FIRST_BORROWING,
                        System.currentTimeMillis() - startTime);
                MDC.remove("X-Request-Id");
            }
        }

        log.warn("total rerun finished...");
    }


    private SysAutoReviewRule fetchRejectRule(List<RuleResult> ruleResultList, Map<String, SysAutoReviewRule> allRules) {
        List<RuleResult> rejectRules = ruleResultList.stream().filter(elem -> {
            SysAutoReviewRule rule = allRules.get(elem.getRuleName());
            boolean isHitRule = elem.isPass() && !elem.getRuleName()
                    .equalsIgnoreCase(BlackListTypeEnum.DATA_EMPTY.getMessage());
            if (isHitRule && rule.getRuleStatus() == 1 && rule.getRuleResult() == 2) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(rejectRules)) {
            return null;
        }
        log.info("拒绝规则：" + JsonUtils.serialize(rejectRules));
        return allRules.get(rejectRules.get(0).getRuleName());

    }



}
