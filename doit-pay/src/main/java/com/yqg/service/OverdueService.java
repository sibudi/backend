package com.yqg.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdBillStatusEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.DateUtils;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdBill;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.order.OrdService;
import com.yqg.service.system.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2018/1/1.
 */
@Service
@Slf4j
public class OverdueService {

    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrdService ordService;
    @Autowired
    private Cash2OrderService cash2OrderService;
    @Autowired
    private CheetahOrderService cheetahOrderService;
    @Autowired
    private OrdBillDao ordBillDao;

    /**
     * 逾期入口
     */
    public void overdue(){
        //同步逾期订单状态
        this.getOverdue();
        //同步逾期账单状态
        this.getOverdueBill();
    }

    /**
     * 跑逾期
     */
    @Transactional
    private void getOverdue() {
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.OVERDUE_OFF_NO);
        if(sysParamValue!=null&&Integer.valueOf(sysParamValue)==1){
            //????
            List<OrdOrder> orderOrders = this.ordDao.getNeedRepayList();
            int overdueDay = 0;
            for (OrdOrder order : orderOrders) {
                try {
                    overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                    if(overdueDay>0){

                        //重新查订单，如果不存在，则跳出
                        OrdOrder orderOrder = new OrdOrder();
                        orderOrder.setDisabled(0);
                        orderOrder.setUuid(order.getUuid());
                        List<OrdOrder> orderList = this.ordDao.scan(orderOrder);
                        if (CollectionUtils.isEmpty(orderList)) {
                            continue;
                        }

                        OrdOrder tempOrder = orderList.get(0);

                        //判断订单状态是否是已还款,如果status=10,11,则跳出
                        if(tempOrder.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()
                                || tempOrder.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){
                            continue;
                        }

                        synOrderStatus(order);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *  同步逾期订单状态
     * */
    @Transactional
    public void synOrderStatus(OrdOrder order){

        // 如果是cashcash的订单 反馈更新订单状态
        if (order.getThirdType() == 1){
            this.cash2OrderService.ordStatusFeedback(order, Cash2OrdStatusEnum.OVERDUE);
        }else if (order.getThirdType() == 2) {
            // 猎豹金融 cheetah
            this.cheetahOrderService.ordStatusFeedback(order, CheetahOrdStatusEnum.OVERDUE);
        }

        OrdOrder entity = new OrdOrder();
        entity.setUuid(order.getUuid());
        entity.setUpdateTime(new Date());
        entity.setStatus(OrdStateEnum.RESOLVING_OVERDUE.getCode());
        entity.setLoanStatus(2);
        this.ordService.updateOrder(entity);

        order.setStatus(OrdStateEnum.RESOLVING_OVERDUE.getCode());
        order.setUpdateTime(new Date());
        order.setLoanStatus(2);
        this.ordService.addOrderHistory(order);

    }

    /**
     * 跑逾期
     */
    @Transactional
    private void getOverdueBill() {
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.OVERDUE_OFF_NO);
        if(sysParamValue!=null&&Integer.valueOf(sysParamValue)==1){
            //????
            List<OrdBill> bills = this.ordBillDao.getNeedRepayBillList();
            int overdueDay = 0;
            for (OrdBill bill : bills) {
                try {
                    overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                    if(overdueDay>0){

                        //重新查订单，如果不存在，则跳出
                        OrdBill scan = new OrdBill();
                        scan.setDisabled(0);
                        scan.setUuid(bill.getUuid());
                        List<OrdBill> scanList = this.ordBillDao.scan(scan);
                        if (CollectionUtils.isEmpty(scanList)) {
                            continue;
                        }

                        OrdBill tempBill = scanList.get(0);

                        //判断订单状态是否是已还款,如果status=10,11,则跳出
                        if(tempBill.getStatus() == OrdBillStatusEnum.RESOLVED.getCode()
                                || tempBill.getStatus() == OrdBillStatusEnum.RESOLVED_OVERDUE.getCode()){
                            continue;
                        }

                        OrdBill update = new OrdBill();
                        update.setUuid(tempBill.getUuid());
                        update.setUpdateTime(new Date());
                        update.setStatus(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode());
                        this.ordBillDao.update(update);

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
