package com.yqg.drools.extract;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.LoanHistory;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanHistoryExtractor implements BaseExtractor<LoanHistory> {

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrdHistoryDao ordHistoryDao;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.LOAN_HISTORY.equals(ruleSet);
    }

    @Override
    public Optional<LoanHistory> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        List<OrdOrder> orderHistory = ordDao.getOrder(order.getUserUuid());

        if(CollectionUtils.isEmpty(orderHistory)){
            return Optional.empty();
        }
        LoanHistory loanHistory = new LoanHistory();
        Long applyCount = orderHistory.stream().count()-1; ////排除当前单
        loanHistory.setApplyCount(applyCount);

        List<OrdOrder> successOrderList = orderHistory.stream().filter(elem->isSuccessLoan(elem)).collect(Collectors.toList());
        Long successLoanCount = successOrderList.stream().count();
        loanHistory.setSuccessLoanCount(successLoanCount);
        loanHistory.setSuccessRatio(new BigDecimal(successLoanCount.toString()).divide(new BigDecimal(applyCount.toString()),
                4,BigDecimal.ROUND_HALF_UP));
        Long overdueCount = successOrderList.stream().filter(elem->isOverdueLoan(elem)).count();
        loanHistory.setOverdueCount(overdueCount);
        loanHistory.setOverdueSuccessLoanRatio(new BigDecimal(overdueCount.toString()).divide(new BigDecimal(successLoanCount.toString()),4,
                BigDecimal.ROUND_HALF_UP) );
        loanHistory.setAverageOverdueDays(new BigDecimal(totalOverdueDays(successOrderList).toString()).divide(new BigDecimal(applyCount.toString()),4,BigDecimal.ROUND_HALF_UP));
        loanHistory.setAverageIntervalDays(calculateAverageIntervalDays(orderHistory));


        List<OrdOrder> overdueList = successOrderList.stream().filter(elem->isOverdueLoan(elem)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(overdueList)){
            return Optional.of(loanHistory);
        }
        //逾期订单按照逾期天数分组
        Map<String, Long> overdueMap =
                overdueList.stream().collect(Collectors.groupingBy((OrdOrder elem) -> {
                    Long overdueDays = DateUtil.getDiffDaysIgnoreHours(elem.getRefundTime(),
                            elem.getActualRefundTime());
                    if (overdueDays > 6) {
                        return "7"; //大于6的统一按照7算
                    } else {
                        return overdueDays.toString();
                    }
                }, Collectors.counting()));
        loanHistory.setOverdue1Count(overdueMap.get("1")==null?0L:overdueMap.get("1"));
        loanHistory.setOverdue2Count(overdueMap.get("2")==null?0L:overdueMap.get("2"));
        loanHistory.setOverdue3Count(overdueMap.get("3")==null?0L:overdueMap.get("3"));
        loanHistory.setOverdue4Count(overdueMap.get("4")==null?0L:overdueMap.get("4"));
        loanHistory.setOverdue5Count(overdueMap.get("5")==null?0L:overdueMap.get("5"));
        loanHistory.setOverdue6Count(overdueMap.get("6")==null?0L:overdueMap.get("6"));
        loanHistory.setOverdueMoreThan6Count(overdueMap.get("7")==null?0L:overdueMap.get("7"));

        loanHistory.setOverdue1Ratio(new BigDecimal(loanHistory.getOverdue1Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));
        loanHistory.setOverdue2Ratio(new BigDecimal(loanHistory.getOverdue2Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));
        loanHistory.setOverdue3Ratio(new BigDecimal(loanHistory.getOverdue3Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));
        loanHistory.setOverdue4Ratio(new BigDecimal(loanHistory.getOverdue4Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));
        loanHistory.setOverdue5Ratio(new BigDecimal(loanHistory.getOverdue5Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));
        loanHistory.setOverdue6Ratio(new BigDecimal(loanHistory.getOverdue6Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));
        loanHistory.setOverdueMoreThan6Ratio(new BigDecimal(loanHistory.getOverdueMoreThan6Count().toString()).divide(new BigDecimal(applyCount.toString()), 4,
                BigDecimal.ROUND_HALF_UP));

        return Optional.of(loanHistory);
    }

    private boolean isSuccessLoan(OrdOrder elem) {
        return OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() == elem.getStatus()
                || OrdStateEnum.RESOLVED_OVERDUE.getCode() == elem.getStatus()
                || OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() == elem.getStatus()
                || OrdStateEnum.RESOLVING_OVERDUE.getCode() == elem.getStatus();
    }

    private boolean isOverdueLoan(OrdOrder elem){
        elem.getActualRefundTime();//实际还款时间
        elem.getRefundTime();//还款时间
        if(elem.getActualRefundTime() == null ||elem.getRefundTime() == null){
            return false;
        }
        Long overdueDays = DateUtil.getDiffDaysIgnoreHours(elem.getRefundTime(),elem.getActualRefundTime());
        return overdueDays>0;
    }

    private Long totalOverdueDays(List<OrdOrder> successOrders){
        if(CollectionUtils.isEmpty(successOrders)){
            return  0L;
        }
        Long totalOverdueDays = successOrders.stream().map(elem->{
            if(elem.getActualRefundTime() == null ||elem.getRefundTime() == null){
                return 0L;
            }
            return DateUtil.getDiffDaysIgnoreHours(elem.getRefundTime(),elem.getActualRefundTime());
        }).reduce(0L,(a,b)->a+b).longValue();
        return totalOverdueDays;
    }

    private BigDecimal calculateAverageIntervalDays(List<OrdOrder> orders){
        orders.sort(Comparator.comparing((OrdOrder::getCreateTime)));//按时间升序排列
        Long totalIntervalDays = 0L;
        Long successLoan = 0L;
        for(int i=0 ;i<orders.size();i++){
            OrdOrder item = orders.get(i);
            if(isSuccessLoan(item)){
                Date beginDate = item.getActualRefundTime();
                //下一笔订单的提交状态时间,防止后面有订单没有提交，循环遍历
                Date submitDate = null;
                for(int j = i+1;j<orders.size();j++){
                    submitDate = ordHistoryDao.getSubmitDate(orders.get(j).getUuid());
                    if(submitDate!=null){
                        break;
                    }
                }
                if(submitDate==null){
                    log.info("the next order submit date is null ,orderNo: "+item.getUuid());
                }else{
                    totalIntervalDays+=DateUtil.getDiffDaysIgnoreHours(beginDate,submitDate);
                    successLoan++;
                }
            }
        }
        if(successLoan == 0L){
            log.error("the success loan count is 0");
            return null;
        }
        return new BigDecimal(totalIntervalDays.toString()).divide(new BigDecimal(successLoan.toString()),4,BigDecimal.ROUND_HALF_UP);
    }
}
