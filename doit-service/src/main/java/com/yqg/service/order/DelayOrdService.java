package com.yqg.service.order;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdDelayRecordDao;
import com.yqg.order.entity.OrdDelayRecord;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.order.response.DelayOrdResponse;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.DelayOrderRequest;
import com.yqg.service.system.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 延期订单相关
 * Created by wanghuaizhou on 2018/4/2.
 */
@Service
@Slf4j
public class DelayOrdService {

    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private RepayService repayService;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrdDelayRecordDao ordDelayRecordDao;

    /**
     *  1，展期时需收取一定的展期服务费；
     2，未还本金作为新的账单重新开始计算期限费用等；
     3，展期金额≥本金*0.5，颗粒度为50000，即展期金额为50000的整数倍；
     4，展期期限：若上一账单期限为7天，只可以展期7天；若上一账单期限为14天，可以展期7天或14天；
     5，业务特点：只可以在用户逾期3天以上时（从逾期第4天开始）可以申请展期，3天要求后台可以设置。
     * */

    /**
     *   展期订单初始化
     * */
    public DelayOrdResponse delayOrderInfo(SaveOrderUserUuidRequest request){

        DelayOrdResponse response = new DelayOrdResponse();
        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setUuid(request.getOrderNo());
        List<OrdOrder> ordList = this.ordDao.scan(ordOrder);
        if (!CollectionUtils.isEmpty(ordList)){

            response = getDelayProductConfig(ordList.get(0));
        }
        return  response;
    }

    /**
     *   获取展期订单产品配置
     * */
    public DelayOrdResponse getDelayProductConfig(OrdOrder order){

        DelayOrdResponse response = new DelayOrdResponse();

        String amountApply = order.getAmountApply().toString();
        response.setAmountApply(Float.parseFloat(amountApply));

        // 展期金额颗粒度
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.DELAYORDER_OF_GRANULARITY);
        if (StringUtils.isEmpty(sysParamValue)){
            sysParamValue = "50000";
        }
        response.setGranulaNum(Float.parseFloat(sysParamValue));

        ArrayList<Integer> dateList = new ArrayList<>();
        // 30天
        dateList.add(30);

        response.setOverDueFee(Float.parseFloat(this.repayService.calculateOverDueFee(order)));
        response.setPenaltyFee(Float.parseFloat(this.repayService.calculatePenaltyFee(order)));
        // 如果还款金额大于本金的1.2倍

        response.setInterest(Float.parseFloat(order.getInterest().toString()));

        // 展期服务费比例 申请展期金额*申请展期期限*%
        String sysParamValue2 = this.sysParamService.getSysParamValue(SysParamContants.DELAYORDER_OF_FEE);
        response.setDelayFee(Float.parseFloat(sysParamValue2));

        // 总还款金额 下次还款时间 剩余还款金额 list配置
        List<Map<String,String>> confList = new ArrayList<>();

        for(int j=0; j<dateList.size();j++){
            int date = dateList.get(j);
            Map<String,String> confMap = new HashMap<>();
            confMap.put("day",String.valueOf(date));
            confMap.put("nextRepayDay", DateUtils.DateToString(DateUtils.addDate(new Date(),date)));
            confList.add(confMap);
        }

        response.setConfig(confList);
        return response;
    }

    /**
     *   提交还款申请（逾期订单 提交展期申请）
     *
     * */
    public void repayToDelayOrder(DelayOrderRequest request) throws Exception{

        log.info("订单号："+request.getOrderNo());

        OrdOrder orderOrder = new OrdOrder();
        orderOrder.setDisabled(0);
        orderOrder.setUuid(request.getOrderNo());
        orderOrder.setUserUuid(request.getUserUuid());
        List<OrdOrder> orderList = this.ordService.orderInfo(orderOrder);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("订单不存在");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        OrdOrder order = orderList.get(0);
//         if( order.getStatus() != OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() && order.getStatus() != OrdStateEnum.RESOLVING_OVERDUE.getCode()&& order.getStatus() != OrdStateEnum.RESOLVED_DEALING.getCode()){
        if( order.getStatus() != OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() && order.getStatus() != OrdStateEnum.RESOLVING_OVERDUE.getCode()){
            log.info("订单状态异常,非待还款状态订单");
            throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
        }
        if( order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() || order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){
            log.info("该订单已还款，返回订单记录查看，请勿重复操作");
            throw new ServiceException(ExceptionEnum.SYSTEM_IS_REFUND);
        }

        String amountApply = order.getAmountApply().toString();
        Float orderAmountFloat =  Float.parseFloat(amountApply);
        Float requestAmount = Float.parseFloat(request.getRepayNum());
        // 请求的金额 > 订单的实际金额
        // 2018.09.13  展期金额可以从零开始
        if (requestAmount > orderAmountFloat ){
            log.info("展期订单申请金额有误");
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
//        }else if (requestAmount != orderAmountFloat){
        }else {
            // 延期申请金额和订单金额相等  则不是延期
            // 在还款回调那里判断
            OrdDelayRecord record = new OrdDelayRecord();
            record.setOrderNo(request.getOrderNo());
            record.setDisabled(0);
            record.setUserUuid(request.getUserUuid());
            List<OrdDelayRecord> recordList =  this.ordDelayRecordDao.scan(record);
            // 如果提交过申请 还能走到这个请求 说明还款未成功 这时候需要更新一下

            // 展期服务费比例 申请展期金额*申请展期期限*2%
            String sysParamValue2 = this.sysParamService.getSysParamValue(SysParamContants.DELAYORDER_OF_FEE);

            if (!CollectionUtils.isEmpty(recordList)){

                OrdDelayRecord update = recordList.get(0);
                update.setUpdateTime(new Date());
                update.setRepayNum(new BigDecimal(request.getRepayNum()));
                update.setDelayDay(Integer.valueOf(request.getDelayDay()));
                update.setInterest(order.getInterest());

                BigDecimal penaltyFee = new BigDecimal(this.repayService.calculatePenaltyFee(order));
                BigDecimal realPenaltyFee =  getRealPenaltyFee(order.getAmountApply()
                        ,order.getInterest()
                        ,new BigDecimal(this.repayService.calculateOverDueFee(order)),penaltyFee);
                update.setPenaltyFee(realPenaltyFee+"");

                update.setOverDueFee(this.repayService.calculateOverDueFee(order));
                update.setDelayFee(order.getAmountApply()
                        .subtract(new BigDecimal(request.getRepayNum())).multiply(new BigDecimal(request.getDelayDay()))
                        .multiply(new BigDecimal(sysParamValue2)).setScale(2));


                this.ordDelayRecordDao.update(update);
            }else {
//                record.setType("1");
                record.setRepayNum(new BigDecimal(request.getRepayNum()));
                record.setDelayDay(Integer.valueOf(request.getDelayDay()));
                record.setInterest(order.getInterest());

                BigDecimal penaltyFee = new BigDecimal(this.repayService.calculatePenaltyFee(order));
                BigDecimal realPenaltyFee =  getRealPenaltyFee(order.getAmountApply()
                        ,order.getInterest()
                        ,new BigDecimal(this.repayService.calculateOverDueFee(order)),penaltyFee);
                record.setPenaltyFee(realPenaltyFee+"");

                record.setOverDueFee(this.repayService.calculateOverDueFee(order));
                record.setDelayFee(order.getAmountApply()
                        .subtract(new BigDecimal(request.getRepayNum())).multiply(new BigDecimal(request.getDelayDay()))
                        .multiply(new BigDecimal(sysParamValue2)).setScale(2));
                this.ordDelayRecordDao.insert(record);

            }
        }
    }

    public BigDecimal getRealPenaltyFee(BigDecimal totalAmout,BigDecimal interest,BigDecimal overDueFee,BigDecimal penaltyFee) {
        BigDecimal repaymentFeeUpLimit = totalAmout.multiply(BigDecimal.valueOf(0.2)).setScale(2);
        if(interest.add(overDueFee).add(penaltyFee).compareTo(repaymentFeeUpLimit) >= 0) {
            return repaymentFeeUpLimit.subtract(overDueFee).subtract(interest);
        }
        return penaltyFee;
    }
}
