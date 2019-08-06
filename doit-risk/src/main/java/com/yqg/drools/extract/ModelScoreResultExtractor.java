package com.yqg.drools.extract;

import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.service.OrderScoreService;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.entity.OrderScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModelScoreResultExtractor {
    @Autowired
    private OrderScoreService orderScoreService;

    public Optional<ModelScoreResult> extractModel(OrdOrder order) {
        ModelScoreResult scoreResult = new ModelScoreResult();
        OrderScore score = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_600);
        if (score != null) {
            scoreResult.setProduct600Score(score.getTotalScore());
        }
        OrderScore score600V2 = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_600_V2);
        if (score600V2 != null) {
            scoreResult.setProduct600ScoreV2(score600V2.getTotalScore());
        }
        OrderScore score100 = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_100);
        if (score100 != null) {
            scoreResult.setProduct100Score(score100.getTotalScore());
        }
        OrderScore score50 = orderScoreService.getLatestScoreWithModel(order.getUuid(), ScoreModelEnum.PRODUCT_50);
        if (score50 != null) {
            scoreResult.setProduct50Score(score50.getTotalScore());
        }
        if (score == null && score100 == null && score50 == null && score600V2 == null) {
            return Optional.empty();
        }
        return Optional.of(scoreResult);
    }
}
