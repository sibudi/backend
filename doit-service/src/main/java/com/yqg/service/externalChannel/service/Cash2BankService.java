package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.JsonUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.request.Cash2BankStatusRequest;
import com.yqg.service.externalChannel.request.Cash2UserBankRequest;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.user.entity.UsrBank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class Cash2BankService {

    @Autowired
    private UsrBankService usrBankService;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private OrdService ordService;

    public Cash2Response rebindBankCard(Cash2UserBankRequest bankRequest) throws Exception {
        //查询内部订单号
        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderRelationByExternalOrderNo(bankRequest.getOrderNo());
        if (externalOrderRelation == null) {
            log.error("cannot find orderInfo ,param={}", JsonUtils.serialize(bankRequest));
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.ORDER_NO_ERROR);
        }

        UsrBankRequest req = new UsrBankRequest();
        req.setBankCardName(bankRequest.getBankCardName());
        req.setBankCode(bankRequest.getBankCode());
        req.setBankNumberNo(bankRequest.getBankNumberNo());
        req.setOrderNo(externalOrderRelation.getOrderNo());
        req.setUserUuid(externalOrderRelation.getUserUuid());
        req.setThirdType(1);
        //检查订单
        OrdOrder dbOrder = ordService.getOrderByOrderNo(externalOrderRelation.getOrderNo());


        if (dbOrder.getStatus() == OrdStateEnum.LOAN_FAILD.getCode() && "BANK_CARD_ERROR".equals(dbOrder.getRemark())) {
            //放款因为银行卡失败,调用重绑卡
            usrBankService.changeOrderBankCard(req);
        } else {
            //band card failed
            //检查订单对应银行卡
            UsrBank orderBank = usrBankService.getBankCardInfo(dbOrder.getUserBankUuid());
            if (orderBank.getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
                //bank card failed-->
                usrBankService.rebindBankCardForFailed(req);
            }
        }

        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1);
    }


    public Cash2Response getBankStatus(Cash2BankStatusRequest request) {
        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderRelationByExternalOrderNo(request.getOrderNo());
        if (externalOrderRelation == null) {
            log.error("cannot find orderInfo ,param={}", JsonUtils.serialize(request));
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.ORDER_NO_ERROR);
        }
        OrdOrder order = ordService.getOrderByOrderNo(externalOrderRelation.getOrderNo());
        if (order.getStatus() == OrdStateEnum.LOAN_FAILD.getCode() && "BANK_CARD_ERROR".equals(order.getRemark())){
            Map<String, String> respData = new HashMap<>();
            respData.put("order_no", request.getOrderNo());
            respData.put("bind_status", "2");
            respData.put("reason", "bank card invalid");
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(respData);
        }

        UsrBank usrBank = usrBankService.getBankCardInfo(order.getUserBankUuid());
        if (usrBank == null) {
            Map<String, String> respData = new HashMap<>();
            respData.put("order_no", request.getOrderNo());
            respData.put("bind_status", "2");
            respData.put("reason", "no bank card");
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(respData);
        }
        Map<String, String> respData = new HashMap<>();
        respData.put("order_no", request.getOrderNo());
        if (usrBank.getStatus() == UsrBankCardBinEnum.SUCCESS.getType()) {
            respData.put("bind_status", "1");
        } else if (usrBank.getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
            respData.put("bind_status", "2");
        } else {
            respData.put("bind_status", "0");
        }
        respData.put("reason","");
        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(respData);


    }


}
