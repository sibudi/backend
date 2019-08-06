package com.yqg.drools.extract;

import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.LoanInfo;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdStepDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdStep;
import com.yqg.service.order.OrderCheckService;
import com.yqg.user.entity.UsrUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class LoanInfoExtractor implements BaseExtractor<LoanInfo> {
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrdStepDao ordStepDao;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderCheckService orderCheckService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.LOAN_INFO.equals(ruleSet);
    }

    @Override
    public Optional<LoanInfo> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        LoanInfo loanInfo = new LoanInfo();
        loanInfo.setCurrentBorrowCount(order.getBorrowingCount());
        Date applyTime = order.getApplyTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(applyTime);
        Integer applyTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
        loanInfo.setApplyTimeHour(applyTimeHour);
        List<OrdOrder> orders = ordDao.getOrder(order.getUserUuid());
        if(CollectionUtils.isEmpty(orders)){
            loanInfo.setHistorySubmitCount(0);
        }else{
            loanInfo.setHistorySubmitCount(orders.size()-1);
        }

        List<OrdStep> stepList = ordStepDao.getOrderStepList(order.getUuid());
        if(!CollectionUtils.isEmpty(stepList)){
            Optional<OrdStep> stepOne =
                    stepList.stream().filter(elem-> elem.getStep() == OrdStepTypeEnum.CHOOSE_ROLE.getType()).max(Comparator.comparing(OrdStep::getCreateTime));
            Optional<OrdStep> stepTwo =
                    stepList.stream().filter(elem-> elem.getStep() == OrdStepTypeEnum.IDENTITY.getType()).max(Comparator.comparing(OrdStep::getCreateTime));

            if(stepOne.isPresent()&&stepTwo.isPresent()){
                Long diffMinutesOfStepOneStepTwo  = DateUtil.getDiffMinutes(stepOne.get().getCreateTime(),stepTwo.get().getCreateTime());
                loanInfo.setDiffMinutesOfStepOne2StepTwo(diffMinutesOfStepOneStepTwo);
            }
        }

        UsrUser userInfo = userService.getUserInfo(order.getUserUuid());

        loanInfo.setDiffMinutesOfUserCreateTimeAndOrderSubmitTime(DateUtil.getDiffMinutes(userInfo.getCreateTime(),applyTime));
        loanInfo.setBorrowingPurpose(userService.getBorrowingPurpose(order.getUserUuid()));

        Optional<OrdOrder> firstOrder = orderCheckService.getFirstSettledOrder(order.getUserUuid());
        if(firstOrder.isPresent()){
            loanInfo.setFirstBorrowingAmount(firstOrder.get().getAmountApply());
        }

        loanInfo.setBorrowingAmount(order.getAmountApply());
        return Optional.of(loanInfo);
    }

}
