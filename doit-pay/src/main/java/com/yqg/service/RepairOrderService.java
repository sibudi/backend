package com.yqg.service;

import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdDelayRecordDao;
import com.yqg.order.dao.OrdPaymentCodeDao;
import com.yqg.order.dao.OrdRepayAmoutRecordDao;
import com.yqg.order.entity.OrdDelayRecord;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdPaymentCode;
import com.yqg.order.entity.OrdRepayAmoutRecord;
import com.yqg.service.loan.response.CheckRepayResponse;
import com.yqg.service.loan.service.LoanInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by wanghuaizhou on 2018/10/29.
 */
@Service
@Slf4j
public class RepairOrderService {

    @Autowired
    private OrdDao ordDao;
    @Autowired
    private PayService payService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdPaymentCodeDao ordPaymentCodeDao;
    @Autowired
    private OrdRepayAmoutRecordDao ordRepayAmoutRecordDao;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private OrdDelayRecordDao ordDelayRecordDao;

    /**
     *   已还款订单 还款流水插入
     * */
    public  void  repairData() {

        List<OrdOrder> list = this.ordDao.getOrderWithNotRepayRecord();

        if (CollectionUtils.isEmpty(list)) {
            log.info("=============查询列表为空=======================");
            return;
        }

        log.info("无还款流水的已还款订单一共有" + list.size());

        for (OrdOrder order : list) {
            // 根据订单号去第三方查询
            UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
            // 查询还款处理中 订单在第三方状态
            CheckRepayResponse response = this.payService.cheakRepay(order.getUuid(), user);

            if (response != null) {
                if (response.getCode().equals("0")) {
                    if (response.getDepositStatus().equals("COMPLETED")) {
                        // 还款成功
                        OrdRepayAmoutRecord record = new OrdRepayAmoutRecord();
                        record.setOrderNo(order.getUuid());
                        record.setUserUuid(order.getUserUuid());
                        record.setRemark("手动修复添加");
                        if (!StringUtils.isEmpty(response.getDepositMethod())) {
                            record.setRepayMethod(response.getDepositMethod());
                        }
                        if (!StringUtils.isEmpty(response.getTransactionId())) {
                            record.setTransactionId(response.getTransactionId());
                        }
                        if (!StringUtils.isEmpty(response.getTransactionId())) {
                            record.setTransactionId(response.getTransactionId());
                        }
                        if (!StringUtils.isEmpty(response.getAmount())) {
                            record.setActualRepayAmout(response.getAmount());
                        }
                        // 查询还款码 获取用户当时的还款信息
                        OrdPaymentCode scan = new OrdPaymentCode();
                        scan.setOrderNo(order.getUuid());
                        scan.setPaymentCode(response.getPaymentCode());

                        List<OrdPaymentCode> codeList = this.ordPaymentCodeDao.scan(scan);
                        if (!CollectionUtils.isEmpty(codeList)) {
                            OrdPaymentCode paymentCode = codeList.get(0);
                            if (!StringUtils.isEmpty(paymentCode.getInterest())) {
                                record.setInterest(paymentCode.getInterest());
                            }
                            if (!StringUtils.isEmpty(paymentCode.getOverDueFee())) {
                                record.setOverDueFee(paymentCode.getOverDueFee());
                            }
                            if (!StringUtils.isEmpty(paymentCode.getPenaltyFee())) {
                                record.setPenaltyFee(paymentCode.getPenaltyFee());
                            }
                            if (!StringUtils.isEmpty(paymentCode.getCodeType())) {
                                record.setRepayChannel(paymentCode.getCodeType());
                            }
                        }
                        this.ordRepayAmoutRecordDao.insert(record);

                        OrdDelayRecord ordDelayRecord = new OrdDelayRecord();
                        ordDelayRecord.setOrderNo(order.getUuid());
                        List<OrdDelayRecord> ordDelayRecordList =  this.ordDelayRecordDao.scan(ordDelayRecord);
                        if (!CollectionUtils.isEmpty(ordDelayRecordList)){
                            OrdDelayRecord o = ordDelayRecordList.get(0);
                            // 处理展期订单数据
                            this.loanInfoService.copyDataToDelayOrder(order,o.getDelayOrderNo());
                        }


                    }
                }

            }

            log.info("添加还款流水成功，订单号为："+order.getUuid());
        }

    }
}
