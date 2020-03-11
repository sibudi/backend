package com.yqg.service.order;

import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.entity.OrdBill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by wanghuaizhou on 2019/4/20.
 */
@Service
@Slf4j
public class OrdBillService {

    @Autowired
    private OrdBillDao ordBillDao;

    public List<OrdBill> billInfo(OrdBill bill) {
        List<OrdBill> billList = ordBillDao.scan(bill);
        return billList;
    }

    public OrdBill getBillByBillNo(String billNo) {
        if (StringUtils.isEmpty(billNo)) {
            return null;
        }
        OrdBill searchInfo = new OrdBill();
        searchInfo.setUuid(billNo);
        searchInfo.setDisabled(0);
        List<OrdBill> bills = ordBillDao.scan(searchInfo);
        if (CollectionUtils.isEmpty(bills)) {
            return null;
        } else {
            return bills.get(0);
        }
    }

    public OrdBill getFirstBillNeedPay(String orderNo){
        return ordBillDao.getFirstBillNeedPay(orderNo);
    }
}
