package com.yqg.drools.extract;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.drools.model.ExtendModel;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.NonManualReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ExtendModelExtractor {

    public static final List<String> IGNORE_NON_MANUAL_RULES = Arrays.asList(BlackListTypeEnum.NON_REVIEW_RELIGION.getMessage(),
            BlackListTypeEnum.CAP_MARRIAGE_FEMALE_GOJEK_MOBILE_NOT_SAME.getMessage(),
            BlackListTypeEnum.GOJEK_WHATSAPP_WHATSAPPRATIO_SEX.getMessage()
    );
    @Autowired
    private NonManualReviewService nonManualReviewService;
    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;

    public Optional<ExtendModel> extractModel(OrdOrder order) {
        ExtendModel model = new ExtendModel();
        model.setHit600NonManualRules(nonManualReviewService.isNonManualReviewOrder(order.getUuid()));

        //命中免核并且排除如下3条
        model.setHit600NonManualRulesForPrd100(false);
        boolean nonManual = nonManualReviewService.isNonManualReviewOrder(order.getUuid());
        if (nonManual) {
            boolean hitSpecifiedRule = false;
            List<OrdRiskRecord> ruleResultList = orderRiskRecordRepository.getOrderRiskRecordList(order.getUuid());
            if (!CollectionUtils.isEmpty(ruleResultList)) {
                hitSpecifiedRule =
                        ruleResultList.stream().filter(elem -> "true".equals(elem.getRuleRealValue()) && IGNORE_NON_MANUAL_RULES.contains(elem.getRuleDetailType())).findFirst().isPresent();
            }
            if (hitSpecifiedRule) {
                model.setHit600NonManualRulesForPrd100(false);
            } else {
                model.setHit600NonManualRulesForPrd100(true);
            }
        }
        return Optional.of(model);
    }
}
