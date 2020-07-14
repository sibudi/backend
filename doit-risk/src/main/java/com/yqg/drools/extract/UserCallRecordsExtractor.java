package com.yqg.drools.extract;

import java.util.Optional;

import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.UserCallRecordsModel;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.order.entity.OrdOrder;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by luhong on 2018/1/23.
 */
@Service
@Slf4j
public class UserCallRecordsExtractor implements BaseExtractor<UserCallRecordsModel> {

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
//        return RuleSetEnum.USER_CALL_RECORDS.equals(ruleSet) || RuleSetEnum.RE_BORROWING_CALL_RECORD
//            .equals(ruleSet);
        return false;
    }

    @Override
    public Optional<UserCallRecordsModel> extractModel(OrdOrder order, KeyConstant keyConstant)
        throws Exception {
        //ahalim: TODO return empty now until found out how to delete this
        return Optional.empty();
    }

}
