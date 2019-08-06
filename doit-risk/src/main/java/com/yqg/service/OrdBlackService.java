package com.yqg.service;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.dao.OrdBlackTempDao;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdBlackTemp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Didit Dwianto on 2017/11/30.
 */
@Service
@Slf4j
public class OrdBlackService {

    @Autowired
    private OrdBlackDao ordBlackDao;
    @Autowired
    private OrdBlackTempDao ordBlackTempDao;

    /**
     *
     * @param orderNo               ????
     * @param responseMessage       ??????
     * @param ruleHitNo             ??????
     * @param ruleRealValue         ???
     * @param userId                ??UUID
     * @param ruleValue             ???
     * @param ruleRejectDay         ????
     */
    public void addBackList(String orderNo, String responseMessage, String ruleHitNo,String ruleRealValue, String userId ,String ruleValue,Integer ruleRejectDay) throws Exception{
            OrdBlack ordBlack = new OrdBlack();
            ordBlack.setOrderNo(orderNo);
            ordBlack.setUserUuid(userId);
            ordBlack.setResponseMessage(responseMessage);
            ordBlack.setUuid(UUIDGenerateUtil.uuid());
            ordBlack.setRuleHitNo(ruleHitNo);
            ordBlack.setRuleRealValue(ruleRealValue);
            ordBlack.setRuleValue(ruleValue);
            ordBlack.setRuleRejectDay(ruleRejectDay);
            this.ordBlackDao.insert(ordBlack);

    }

    /**
     *
     * @param orderNo               ????
     * @param responseMessage       ??????
     * @param ruleHitNo             ??????
     * @param ruleRealValue         ???
     * @param userId                ??UUID
     * @param ruleValue             ???
     */
    public void addBackListTemp(String orderNo, String responseMessage, String ruleHitNo,String ruleRealValue, String userId ,String ruleValue) throws Exception{
        OrdBlackTemp ordBlackTemp = new OrdBlackTemp();
        ordBlackTemp.setOrderNo(orderNo);
        ordBlackTemp.setUserUuid(userId);
        ordBlackTemp.setResponseMessage(responseMessage);
        ordBlackTemp.setUuid(UUIDGenerateUtil.uuid());
        ordBlackTemp.setRuleHitNo(ruleHitNo);
        ordBlackTemp.setRuleRealValue(ruleRealValue);
        ordBlackTemp.setRuleValue(ruleValue);
        this.ordBlackTempDao.insert(ordBlackTemp);
    }

}
