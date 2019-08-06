package com.yqg.service.order;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.entity.OrdHistory;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.util.RuleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderCheckService {
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrdHistoryDao ordHistoryDao;
    /***
     * 订单是降额产品
     * @param orderNo
     * @return
     */
    public boolean histSpecifiedProductWithDecreasedCreditLimit(String orderNo) {
        OrdOrder searchParam = new OrdOrder();
        searchParam.setUuid(orderNo);
        searchParam.setDisabled(0);
        List<OrdOrder> orders = ordDao.scan(searchParam);
        if (CollectionUtils.isEmpty(orders)) {
            log.warn("can not get the order info");
            return false;
        }
        OrdOrder currentOrder = orders.get(0);
        if (currentOrder.getAmountApply().compareTo(RuleConstants.PRODUCT100) == 0
                || currentOrder.getAmountApply().compareTo(RuleConstants.PRODUCT50) == 0) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        OrdOrder currentOrder = new OrdOrder();
        currentOrder.setAmountApply(new BigDecimal("300000.00"));
        System.err.println( (currentOrder.getAmountApply().compareTo(RuleConstants.PRODUCT100) == 0
                || currentOrder.getAmountApply().compareTo(RuleConstants.PRODUCT50) == 0));
    }

    public Long successLoanCount(String userUuid){
        List<OrdOrder> orders = ordDao.getOrder(userUuid);
        if(CollectionUtils.isEmpty(orders)){
            return 0L;
        }
        return orders.stream().filter(elem -> (elem.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode())
                || (elem.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode())
                || (elem.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode())
                || (elem.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode())).count();
    }

    public Optional<OrdOrder> getFirstSettledOrder(String userUuid) {
        List<OrdOrder> orders = getSettledOrders(userUuid);
        if (CollectionUtils.isEmpty(orders)) {
            return Optional.empty();
        }
        return orders.stream().filter(elem -> elem.getBorrowingCount() == 1).findFirst();

    }

    public Boolean isFirstBorrowingNotOverdue(String userUuid){
        if(StringUtils.isEmpty(userUuid)){
            return false;
        }
        OrdOrder searchParam = new OrdOrder();
        searchParam.setUserUuid(userUuid);
        searchParam.setDisabled(0);
        searchParam.setBorrowingCount(1);
        searchParam.setStatus(OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode());
        List<OrdOrder> resultList =  ordDao.scan(searchParam);
        return !CollectionUtils.isEmpty(resultList);
    }

    public List<OrdOrder> getSettledOrders(String userUuid){
        List<OrdOrder> orders = ordDao.getOrder(userUuid);
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }
        return orders.stream().filter(elem ->elem.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()
                || elem.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()
        ).collect(Collectors.toList());
    }

    public Date getFirstSettledOrderApplyTime(String userUuid){
       Optional<OrdOrder> firstOrder =  getFirstSettledOrder(userUuid);
       if(firstOrder.isPresent()){
           return ordHistoryDao.getSubmitDate(firstOrder.get().getUuid());
       }
       return null;
    }

}
