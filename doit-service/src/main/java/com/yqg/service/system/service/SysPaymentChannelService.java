package com.yqg.service.system.service;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.system.request.SysPaymentChannelRequest;
import com.yqg.service.system.response.SysPaymentChannelResponse;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.system.dao.SysPaymentChannelDao;
import com.yqg.system.entity.SysPaymentChannel;
import com.yqg.user.entity.UsrBank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/26.
 */
@Component
@Slf4j
public class SysPaymentChannelService {


    @Autowired
    private SysPaymentChannelDao sysPaymentChannelDao;
    @Autowired
    private OrdService ordService;
    @Autowired
    private P2PService p2PService;
    @Autowired
    private UsrBankService usrBankService;

    /**
     *   ??????
     */
    public List<SysPaymentChannelResponse> getSysPaymentChennelList(SysPaymentChannelRequest channelRequest) throws ServiceException {
        log.info("订单号为"+channelRequest.getOrderNo());

        OrdOrder orderOrder = new OrdOrder();
        orderOrder.setDisabled(0);
        orderOrder.setUuid(channelRequest.getOrderNo());
        orderOrder.setUserUuid(channelRequest.getUserUuid());
        List<OrdOrder> orderList = this.ordService.orderInfo(orderOrder);
        if (CollectionUtils.isEmpty(orderList)) {
            log.info("未找到对应的订单");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrdOrder order = orderList.get(0);
        if( order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() || order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){
            log.info("订单状态异常");
            throw new ServiceException(ExceptionEnum.SYSTEM_IS_REFUND);
        }

        List<SysPaymentChannelResponse> channelResponseList = new ArrayList<>();
        List<SysPaymentChannel> channelList = new ArrayList<>();
        if (this.p2PService.isP2PIssuedLoan(order.getUuid())){

            /**
             *  p2p修改  对应的放款渠道 只能通过对应的还款渠道
             * */
            UsrBank usrBank = this.usrBankService.getBankCardInfo(order.getUserBankUuid());
            if (usrBank.getBankCode().equals("BCA")){
                channelList =   this.sysPaymentChannelDao.getRepaymentChanelWithBankCode("4","BCA");
            }else  if (usrBank.getBankCode().equals("BNI")){
                channelList =   this.sysPaymentChannelDao.getRepaymentChanelWithBankCode("4","BNI");
            }else {
                channelList =   this.sysPaymentChannelDao.getRepaymentChanelWithBankCode("4","CIMB");
            }

        }else {

            channelList = this.sysPaymentChannelDao.getRepaymentChanelListForDOKUNew();
        }

        if(CollectionUtils.isEmpty(channelList)){
            return  channelResponseList;
        }
        for(SysPaymentChannel paymentChannel:channelList){
            SysPaymentChannelResponse response=new SysPaymentChannelResponse();
            response.setPaymentChannelName(paymentChannel.getPaymentChannelName());
            response.setPaymentType(paymentChannel.getPaymentChannelCode());
            response.setPaymentChannel(String.valueOf(paymentChannel.getType()));
            if (paymentChannel.getType() == 11 &&  (channelRequest.getClient_version().compareTo("1.0.4")< 0)){
                response.setPaymentChannel("8");
            }
            channelResponseList.add(response);
        }
        return channelResponseList;
    }

}
