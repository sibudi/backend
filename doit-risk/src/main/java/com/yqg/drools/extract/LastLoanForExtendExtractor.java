package com.yqg.drools.extract;

import com.yqg.collection.dao.CollectionRemarkDao;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.LastLoan;
import com.yqg.drools.model.LastLoanForExtend;
import com.yqg.drools.model.ModelScoreResult;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.ManualReviewDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/24
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class LastLoanForExtendExtractor implements BaseExtractor<LastLoanForExtend> {

    @Autowired
    private OrdDao ordDao;
    @Autowired
    private ModelScoreResultExtractor modelScoreResultExtractor;
    @Autowired
    private CollectionRemarkDao collectionRemarkDao;
    @Autowired
    private OrdHistoryDao ordHistoryDao;

    @Autowired
    private ManualReviewDao manualReviewDao;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.LAST_LOAN_FOR_EXTEND.equals(ruleSet);
    }

    @Override
    public Optional<LastLoanForExtend> extractModel(OrdOrder order, KeyConstant keyConstant) {

        List<OrdOrder> orders = ordDao.getLastSuccessOrder(order.getUserUuid());
        if (CollectionUtils.isEmpty(orders)) {
            return Optional.empty();
        }

        Optional<OrdOrder> lastNormalOrder =
                orders.stream().filter(elem -> StringUtils.isEmpty(elem.getOrderType()) ||
                                                                   (StringUtils.isNotEmpty(elem.getOrderType()) && !Arrays.asList("1").contains(elem.getOrderType()))
                                      ).max(Comparator.comparing(OrdOrder::getCreateTime));
        if (!lastNormalOrder.isPresent()) {
            return Optional.empty();
        }

        LastLoanForExtend lastLoanForExtend = new LastLoanForExtend();
        OrdOrder ord = orders.get(0);
        // 上一次成功借款的逾期天数：actualRefundTime(实际还款日期)-refundTime(应还款日期)
        Long diffDay = DateUtils
                .getDiffDaysIgnoreHours(ord.getRefundTime(), ord.getActualRefundTime());

        lastLoanForExtend.setOverdueDays(diffDay);
        lastLoanForExtend.setBorrowingAmount(ord.getAmountApply());

        Optional<ModelScoreResult> modelScore = modelScoreResultExtractor.extractModel(ord);
        if (modelScore.isPresent()) {
            lastLoanForExtend.setLastLoanModelScore(modelScore.get());
        }

        //diffDays of collection time and actualRefundTime
        Date firstCollectionTime = collectionRemarkDao.getFirstCollectionTime(ord.getUuid());
        if (firstCollectionTime != null) {
            Long diffMinutes = DateUtil.getDiffMinutes(firstCollectionTime, ord.getActualRefundTime());
            lastLoanForExtend.setDiffHoursBetweenFirstCollectionAndRefundTime(diffMinutes / 60L);
        }

        Integer collectionCount = collectionRemarkDao.getCollectionCount(ord.getUuid());
        lastLoanForExtend.setNoCollectionRecord(collectionCount == 0);

        boolean isNonManPassLoan = ordHistoryDao.orderWithoutManualStatus(ord.getUuid())>=1;
        boolean hitCompanyRuleInManualReview = manualReviewDao.hitCompanyRule(ord.getUuid())>=1;
        if(isNonManPassLoan){
            lastLoanForExtend.setLoanPassType(1);
        }else if(hitCompanyRuleInManualReview){
            lastLoanForExtend.setLoanPassType(2);
        }else {
            lastLoanForExtend.setLoanPassType(3);
        }

        lastLoanForExtend.setIntervalDays(DateUtil.getDiffDays(ord.getActualRefundTime(),order.getApplyTime()));

        return Optional.of(lastLoanForExtend);


    }


}
