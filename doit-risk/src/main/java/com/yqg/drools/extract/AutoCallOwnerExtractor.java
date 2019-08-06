package com.yqg.drools.extract;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.drools.model.AutoCallModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.AutoCallService;
import com.yqg.service.NonManualReviewService;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AutoCallOwnerExtractor  implements BaseExtractor<AutoCallModel> {
    @Autowired
    private AutoCallService autoCallService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.AUTO_CALL_OWNER.equals(ruleSet);
    }

    @Override
    public Optional<AutoCallModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        List<CallResult> callResultList = autoCallService.getTelCallList(order.getUuid(), null);
        if (CollectionUtils.isEmpty(callResultList)) {
            return Optional.empty();
        }
        AutoCallModel autoCallModel = new AutoCallModel();
        autoCallModel.setBorrowingCount(order.getBorrowingCount());
        autoCallModel.setIsCashCahOrder(order.getThirdType() != null && 1 == order.getThirdType());
        if (1 == order.getBorrowingCount()) {
            List<TeleCallResult> ownerCallList = callResultList.stream().filter(elem -> TeleCallResult.CallTypeEnum.OWNER.getCode().equals(elem
                    .getCallType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ownerCallList)) {
                autoCallModel.setHasValidCall(false);
                autoCallModel.setNeedReject(false);
                autoCallModel.setExceedLimit(false);
            } else {
                //有过接通？
                boolean hasValidCall = ownerCallList.stream().filter(elem -> elem.isCallReceived())
                        .count() > 0;
                //命中拒绝？
                boolean isNeedReject = ownerCallList.stream().filter(elem -> elem.isCallInvalid()).count() >= RuleConstants.REJECT_CALL_TIMES;
                //超过发送次数限制？
                boolean exceedLimit = ownerCallList.size() >= RuleConstants.ORDER_OWNER_CALL_LIMIT;

                autoCallModel.setHasValidCall(hasValidCall);
                autoCallModel.setNeedReject(isNeedReject);
                autoCallModel.setExceedLimit(exceedLimit);
            }

        }
        return Optional.of(autoCallModel);
    }
}
