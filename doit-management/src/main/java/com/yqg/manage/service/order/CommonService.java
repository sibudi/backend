package com.yqg.manage.service.order;

import com.yqg.common.enums.order.OrdBillStatusEnum;
import com.yqg.common.enums.order.OrderTypeEnum;
import com.yqg.manage.service.collection.response.CollectionBaseResponse;
import com.yqg.manage.service.order.response.OrderBaseResponse;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.entity.OrdBill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Author: tonggen
 * Date: 2019/4/23
 * time: 3:34 PM
 */
@Service
@Slf4j
public class CommonService {

    @Autowired
    private OrdBillDao ordBillDao;

    public void setTerms(OrderBaseResponse response) {

        if (Integer.valueOf(OrderTypeEnum.STAGING.getCode()).equals(response.getOrderType())) {
            response.setBorrowingTerm(response.getBorrowingTerm() + " terms");
            response.setIsTerm(1);
            //查询改订单的还款计划
            OrdBill ordBill = new OrdBill();
            ordBill.setDisabled(0);
            ordBill.setOrderNo(response.getUuid());
            List<OrdBill> ordBills = ordBillDao.scan(ordBill);
            if (!CollectionUtils.isEmpty(ordBills)) {
                Optional<OrdBill> bill = ordBills.stream().sorted(Comparator.comparing(OrdBill::getBillTerm))
                        .filter(elem -> elem.getStatus().equals(OrdBillStatusEnum.RESOLVING.getCode())
                                || elem.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode())).findFirst();
                if (bill.isPresent()) {
                    response.setNeedPayTerm(bill.get().getBillTerm());
                }
            }

        } else {
            response.setIsTerm(2);
        }
    }

    public void setCollectionOrdTerms(CollectionBaseResponse response) {

        //兼容部分数据返回的是uuid
        String orderNo = response.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            orderNo = response.getUuid();
        }
        if (response.getOrderType().equals(Integer.valueOf(OrderTypeEnum.STAGING.getCode()))) {
            response.setBorrowingTerm(response.getBorrowingTerm() + " terms");
            response.setIsTerm(1);
            //查询改订单的还款计划
            OrdBill ordBill = new OrdBill();
            ordBill.setDisabled(0);
            ordBill.setOrderNo(orderNo);
            List<OrdBill> ordBills = ordBillDao.scan(ordBill);
            if (!CollectionUtils.isEmpty(ordBills)) {
                Optional<OrdBill> bill = ordBills.stream().sorted(Comparator.comparing(OrdBill::getBillTerm))
                        .filter(elem -> elem.getStatus().equals(OrdBillStatusEnum.RESOLVING.getCode())
                                || elem.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode())).findFirst();
                if (bill.isPresent()) {
                    response.setNeedPayTerm(bill.get().getBillTerm());
                }
            }

        } else {
            response.setIsTerm(2);
        }
    }
}
