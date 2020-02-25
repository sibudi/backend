package com.yqg.manage.service.order;

import com.yqg.common.enums.order.OrdRepayAmountRecordStatusEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.DateUtils;
import com.yqg.manage.service.order.request.ManualRepayOrderRequest;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdRepayAmoutRecordDao;
import com.yqg.order.dao.OrdUnderLinePayRecordDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRepayAmoutRecord;
import com.yqg.order.entity.OrdUnderLinePayRecord;
import com.yqg.service.order.OrdService;
import com.yqg.service.pay.RepayService;
import com.yqg.service.user.service.UsrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wanghuaizhou on 2018/2/27.
 */
@Service
@Slf4j
public class ChangeOrderService {

    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrdService ordService;
    @Autowired
    private RepayService repayService;
    @Autowired
    private OrdRepayAmoutRecordDao ordRepayAmoutRecordDao;
    @Autowired
    private OrdUnderLinePayRecordDao ordUnderLinePayRecordDao;

    /**
     *   手动处理还款订单 除非用户直接转账到对公账号 否则慎用
     * */
    @Transactional
    public Object manualOperationRepayOrder(ManualRepayOrderRequest repayOrderRequest) throws Exception{

        if (StringUtils.isEmpty(repayOrderRequest.getActualRepayTime())
                || StringUtils.isEmpty(repayOrderRequest.getOrderNo())
                || StringUtils.isEmpty(repayOrderRequest.getActualRepayAmout())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        OrdOrder order = new OrdOrder();
        order.setUuid(repayOrderRequest.getOrderNo());
        order.setUserUuid(repayOrderRequest.getUserUuid());
        List<OrdOrder> scanList = this.ordDao.scan(order);
        if (CollectionUtils.isEmpty(scanList)) {
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_NOT_FOUND);
        }

        OrdOrder dealOrder = scanList.get(0);
        // 订单状态必须是代还款
        if (dealOrder.getStatus() != OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() &&
                dealOrder.getStatus() != OrdStateEnum.RESOLVING_OVERDUE.getCode() ) {
            log.error("订单状态异常");
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_STATES_ERROR);
        }
        //处理实际还款时间在应还款之前
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date actualRepayTime = sf.parse(repayOrderRequest.getActualRepayTime());
        int overdueDay = DateUtils.daysBetween(DateUtils.formDate(dealOrder.getRefundTime(),"yyyy-MM-dd")
                ,DateUtils.formDate(actualRepayTime,"yyyy-MM-dd"));
        //根据还款时间不同，置为不同状态
        log.info("开始还款 实际还款成功=====================》订单号："+dealOrder.getUuid() + "overDueDay is :" + overdueDay);
        OrdOrder entity = new OrdOrder();
        entity.setUuid(dealOrder.getUuid());
        entity.setStatus(overdueDay <= 0 ? OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() : OrdStateEnum.RESOLVED_OVERDUE.getCode());
        entity.setActualRefundTime(actualRepayTime);
        entity.setUpdateTime(new Date());
        this.ordService.updateOrder(entity);

        dealOrder.setStatus(overdueDay <= 0 ? OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() : OrdStateEnum.RESOLVED_OVERDUE.getCode());
        dealOrder.setUpdateTime(new Date());
        dealOrder.setActualRefundTime(actualRepayTime);
        this.ordService.addOrderHistory(dealOrder);

        //需要根据实际还款时间，计算相关金额
        OrdRepayAmoutRecord record = new OrdRepayAmoutRecord();
        record.setOrderNo(dealOrder.getUuid());
        record.setUserUuid(dealOrder.getUserUuid());
        record.setRepayMethod("MANUAL");
        record.setTransactionId("");
//        record.setActualRepayAmout(repayService.calculateRepayAmount(dealOrder,"1"));
        record.setActualRepayAmout(repayService.calculateShouldRepayAmount(dealOrder, overdueDay));
        record.setInterest(dealOrder.getInterest()+"");
        record.setOverDueFee(repayService.calculateOverDueFee(dealOrder, overdueDay));
        record.setPenaltyFee(repayService.calculatePenaltyFeeByRepayDays(dealOrder, overdueDay));
        record.setActualDisbursedAmount(new BigDecimal("".equals(dealOrder.getApprovedAmount()) ? "0.00" : dealOrder.getApprovedAmount()));
        record.setServiceFee(dealOrder.getServiceFee());
        record.setStatus(OrdRepayAmountRecordStatusEnum.WAITING_REPAYMENT_TO_RDN.toString());
        record.setRepayChannel("3");
        this.ordRepayAmoutRecordDao.insert(record);

        //记录前端传来的实际金额
        OrdUnderLinePayRecord ordUnderLinePayRecord = new OrdUnderLinePayRecord();
        ordUnderLinePayRecord.setOrderNo(repayOrderRequest.getOrderNo());
        ordUnderLinePayRecord.setUserUuid(repayOrderRequest.getUserUuid());
        ordUnderLinePayRecord.setDisabled(0);
        ordUnderLinePayRecord.setCreateUser(repayOrderRequest.getOperatorId());
        ordUnderLinePayRecord.setUpdateUser(repayOrderRequest.getOperatorId());
        ordUnderLinePayRecord.setActualRepayAmout(repayOrderRequest.getActualRepayAmout());
        ordUnderLinePayRecord.setActualRepayTime(actualRepayTime);
        ordUnderLinePayRecordDao.insert(ordUnderLinePayRecord);

        return null;

    }

}
