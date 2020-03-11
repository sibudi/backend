package com.yqg.manage.service.vainfo;

import lombok.extern.slf4j.Slf4j;

import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.pay.response.RepayResponse;
import com.yqg.service.order.OrdBillService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.entity.OrdBill;
import com.yqg.manage.service.user.ManUserService;


@Service
@Slf4j
public class VAInfoService {

    @Autowired
    private RepayService repayService;

    @Autowired
    private OrdBillService ordBillService;

    @Autowired
    private ManUserService manUserService;

    public RepayResponse vaInfo(RepayRequest repayRequest) throws Exception {
        
        if(repayRequest.getOutsourceId() >= 0 && !manUserService.isAllowToSearchOrder(repayRequest.getOrderNo(), repayRequest.getOutsourceId())){
            throw new ServiceException(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        if (repayRequest.getType().equals("1") || repayRequest.getType().equals("2")){
            //normal and extension
            if(repayRequest == null || StringUtils.isEmpty(repayRequest.getOrderNo())){
                log.info("the repayment order no is not exist");
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
            else{
                return repayService.repayment(repayRequest);
            }
        }else if (repayRequest.getType().equals("3")){
           //  Installment Repayment
            OrdBill bill = this.ordBillService.getFirstBillNeedPay(repayRequest.getOrderNo());
            if (bill == null) {
                log.info("the repayment order is not exist for orderNo: " + repayRequest.getOrderNo());
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
            else{
                repayRequest.setOrderNo(bill.getUuid());
                return repayService.repayment(repayRequest);
            }
        }

        return null;
    }
}