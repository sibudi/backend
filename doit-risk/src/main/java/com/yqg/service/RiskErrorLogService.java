package com.yqg.service;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.risk.dao.RiskErrorLogDao;
import com.yqg.risk.entity.RiskErrorLog;
import com.yqg.risk.entity.RiskErrorLog.RiskErrorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RiskErrorLogService {

    @Autowired
    private RiskErrorLogDao riskErrorLogDao;

    public void addRiskError(String orderNo, RiskErrorTypeEnum errorType) {

        log.warn("risk error, orderNo: {}, type: {}", orderNo, errorType == null ? null : errorType.name());
        //检查是否有该异常记录：
        RiskErrorLog search = new RiskErrorLog();
        //search.setDisabled(0);
        search.setOrderNo(orderNo);
        search.setErrorType(errorType == null ? 0 : errorType.getCode());
        List<RiskErrorLog> searchResult = riskErrorLogDao.scan(search);
        if(CollectionUtils.isEmpty(searchResult)){
            //记录异常表
            RiskErrorLog riskErrorLog = new RiskErrorLog();
            riskErrorLog.setOrderNo(orderNo);
            riskErrorLog.setCreateTime(new Date());
            riskErrorLog.setUpdateTime(new Date());
            riskErrorLog.setUuid(UUIDGenerateUtil.uuid());
            riskErrorLog.setErrorType(errorType == null ? 0 : errorType.getCode());
            //riskErrorLog.setRemark();
            riskErrorLogDao.insert(riskErrorLog);
        }else{
            RiskErrorLog riskErrorLog = new RiskErrorLog();
            riskErrorLog.setOrderNo(orderNo);
            riskErrorLog.setCreateTime(new Date());
            riskErrorLog.setUpdateTime(new Date());
            riskErrorLog.setUuid(UUIDGenerateUtil.uuid());
            riskErrorLog.setErrorType(errorType == null ? 0 : errorType.getCode());
            Integer times = searchResult.size()+1;
            riskErrorLog.setTimes(times);
            //riskErrorLog.setRemark();
            riskErrorLogDao.insert(riskErrorLog);
        }


    }

    public List<RiskErrorLog> getTaxNumberNeedRetryOrders() {
        return riskErrorLogDao.getTaxNumberNeedRetryOrders(RiskErrorTypeEnum.TAX_VERIFY_NEED_RETRY.getCode());
    }


    public void disabledErrorLog(Integer id) {
        riskErrorLogDao.disabledErrorLog(id);
    }

    public void disabledErrorLogWithRemark(Integer id, String remark) {
        riskErrorLogDao.disabledErrorLogWithRemark(id, remark);
    }
}
