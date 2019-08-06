package com.yqg.drools.service;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.risk.dao.OrderScoreDao;
import com.yqg.risk.dao.OrderScoreDetailDao;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.entity.OrderScoreDetail;
import com.yqg.drools.model.base.RiskScoreCondition;
import com.yqg.drools.model.base.ScoreRuleResult;
import com.yqg.enums.ScoreModelEnum;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderScoreService {
    @Autowired
    private OrderScoreDao orderScoreDao;
    @Autowired
    private OrderScoreDetailDao orderScoreDetailDao;

    @Transactional(rollbackFor = Exception.class)
    public void saveScoreInfo(List<ScoreRuleResult> scoreList, OrdOrder order, RiskScoreCondition condition) {
        //明细
        List<OrderScoreDetail> scoreDetailList = scoreList.stream().map(elem -> {
            OrderScoreDetail detail = new OrderScoreDetail();
            detail.setOrderNo(order.getUuid());
            detail.setUserUuid(order.getUserUuid());
            detail.setModelName(elem.getModelName());
            detail.setVariableName(elem.getVariableName());
            detail.setVariableThresholdName(elem.getVariableThresholdName());
            detail.setScore(elem.getScore());
            detail.setRealValue(elem.getRealValue());
            detail.setCreateTime(new Date());
            detail.setUpdateTime(new Date());
            detail.setUuid(UUIDGenerateUtil.uuid());
            detail.setVersion(condition.getVersion());
            return detail;
        }).collect(Collectors.toList());

        //总分
        BigDecimal totalScore = scoreDetailList.stream().map(elem -> elem.getScore()).reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderScore orderScore = new OrderScore();
        orderScore.setOrderNo(order.getUuid());
        orderScore.setUserUuid(order.getUserUuid());
        orderScore.setModelName(scoreList.get(0).getModelName());
        orderScore.setVersion(condition.getVersion());
        orderScore.setScorePass(null);
        orderScore.setRulePass(null);
        orderScore.setManualReview(null);
        orderScore.setTotalScore(totalScore.add(condition.getBaseScore()));
        orderScore.setUuid(UUIDGenerateUtil.uuid());
        orderScore.setCreateTime(new Date());
        orderScore.setUpdateTime(new Date());


        //插入明细
        orderScoreDetailDao.addScoreDetail(scoreDetailList);
        //插入概要
        orderScoreDao.insert(orderScore);
    }

    public void setRulePassFlag(OrdOrder order, ScoreModelEnum scoreModel, boolean flag) {
        OrderScore dbScore = getLatestScoreWithModel(order.getUuid(), scoreModel);
        if (dbScore == null) {
            return;
        }
        dbScore.setRulePass(flag ? 1 : 0);
        orderScoreDao.update(dbScore);
    }

    public void setScorePassFlag(OrdOrder order, ScoreModelEnum scoreModel, boolean flag) {
        OrderScore dbScore = getLatestScoreWithModel(order.getUuid(), scoreModel);
        if (dbScore == null) {
            return;
        }
        dbScore.setScorePass(flag ? 1 : 0);
        orderScoreDao.update(dbScore);
    }

    public void setManualReviewFlag(OrdOrder order, ScoreModelEnum scoreModel, boolean flag) {
        OrderScore dbScore = getLatestScoreWithModel(order.getUuid(), scoreModel);
        if (dbScore == null) {
            return;
        }
        dbScore.setManualReview(flag ? 1 : 0);
        orderScoreDao.update(dbScore);
    }

    public void setFlags(String orderNo, ScoreModelEnum scoreModel, boolean ruleFlag, boolean scoreFlag, boolean manualReviewFlag) {
        OrderScore dbScore = getLatestScoreWithModel(orderNo, scoreModel);
        if (dbScore == null) {
            return;
        }
        dbScore.setRulePass(ruleFlag?1:0);
        dbScore.setScorePass(scoreFlag?1:0);
        dbScore.setManualReview(manualReviewFlag?1:0);
        orderScoreDao.update(dbScore);
    }

    public OrderScore getLatestScoreWithModel(String orderNo, ScoreModelEnum scoreModelEnum) {
        OrderScore dbScore = orderScoreDao.getLatestResult(orderNo, scoreModelEnum.name());
        return dbScore;
    }

    public boolean needManualReview(String orderNo, ScoreModelEnum scoreModelEnum) {
        OrderScore orderScore = getLatestScoreWithModel(orderNo, scoreModelEnum);
        return orderScore != null && orderScore.getManualReview() == 1;
    }

}
