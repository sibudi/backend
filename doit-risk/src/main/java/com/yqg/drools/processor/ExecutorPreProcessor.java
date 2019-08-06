package com.yqg.drools.processor;

import com.yqg.service.RiskErrorLogService;
import com.yqg.risk.entity.RiskErrorLog.RiskErrorTypeEnum;
import com.yqg.drools.model.RUserInfo;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/***
 * 规则执行前的预处理
 */
@Service
@Slf4j
public class ExecutorPreProcessor {

    @Autowired
    private RiskErrorLogService riskErrorLogService;

    public boolean preExecute(List<Object> facts, OrdOrder order){

        //税卡处理
        Optional<Object> rUserInfo = facts.stream().filter(elem->elem instanceof RUserInfo).findFirst();
        if (rUserInfo.isPresent()) {
            RUserInfo userModel = (RUserInfo) rUserInfo.get();
            if (userModel.getAdvanceVerifyResult().getAdvanceVerifyResultType().equals(RUserInfo.IdentityVerifyResultType.TAX_VERIFY_NEED_RETRY)) {
                log.warn("need tax retry... orderNo: {}",order.getUuid());
                //insert errorInfo
                riskErrorLogService.addRiskError(order.getUuid(), RiskErrorTypeEnum.TAX_VERIFY_NEED_RETRY);
                return false;
            }
        }
        return true;
    }
}
