package com.yqg.drools.executor.loanLimit;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.drools.executor.base.ApplicationFlowEnum;
import com.yqg.drools.extract.LoanLimitDecreaseExtractor;
import com.yqg.drools.model.LoanLimitModel;
import com.yqg.drools.model.base.LoanLimitRuleResult;
import com.yqg.drools.service.RuleService;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.LoanLimitRuleResultDao;
import com.yqg.risk.entity.LoanLimitRuleResultEntity;
import com.yqg.service.order.OrderCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanLimitExecutor {

    @Autowired
    private OrderCheckService orderCheckService;
    @Autowired
    private LoanLimitDecreaseExtractor loanLimitDecreaseExtractor;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private LoanLimitRuleResultDao loanLimitRuleResultDao;

    public List<LoanLimitRuleResult> checkUserLoanLimit(String userUuid) {
        //查询用户首单
        Optional<OrdOrder> order = orderCheckService.getFirstSettledOrder(userUuid);
        if (!order.isPresent()) {
            return new ArrayList<>();
        }

        Optional<LoanLimitModel> model = loanLimitDecreaseExtractor.extract(order.get());
        if (!model.isPresent()) {
            log.warn("cannot get loanLimit model");
            return new ArrayList<>();
        }
        List<Object> facts = new ArrayList<>();
        facts.add(model.get());
        List<LoanLimitRuleResult> resultList = ruleService.executeLoanLimit(ApplicationFlowEnum.LOAN_LIMIT_FIRST_RE_BORROWING, facts);

        if (!CollectionUtils.isEmpty(resultList)) {
            List<LoanLimitRuleResultEntity> saveList = resultList.stream().map(elem -> {
                LoanLimitRuleResultEntity entity = new LoanLimitRuleResultEntity();
                entity.setPass(elem.getPass());
                entity.setRuleDesc(elem.getRuleDesc());
                entity.setRuleName(elem.getRuleName());
                entity.setProductType(model.get().getIsProduct50() ? "PRODUCT_50" : "PRODUCT_100");
                entity.setUserUUid(userUuid);
                entity.setOrderNo(order.get().getUuid());
                entity.setUuid(UUIDGenerateUtil.uuid());
                entity.setCreateTime(new Date());
                entity.setUpdateTime(new Date());
                return entity;
            }).collect(Collectors.toList());
//            log.info("save mongo param: {}", JsonUtils.serialize(mongoSaveList));
            //loanLimitRuleResultRepository.addRuleResultList(mongoSaveList);
            for(LoanLimitRuleResultEntity elem: saveList){
                loanLimitRuleResultDao.insert(elem);
            }
        }
        return resultList;
    }
}
