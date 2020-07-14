package com.yqg.drools.extract;

import java.util.Optional;

import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.ShortMessage;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.order.entity.OrdOrder;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class ShortMessageExtractor implements BaseExtractor<ShortMessage> {

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
//        return RuleSetEnum.SHORT_MESSAGE.equals(ruleSet) || RuleSetEnum.RE_BORROWING_SHORT_MESSAGE
//            .equals(ruleSet);
        return false;
    }

    @Override
    public Optional<ShortMessage> extractModel(OrdOrder order, KeyConstant keyConstant) {
        //ahalim: TODO return empty now until found out how to delete this
         return Optional.empty();
    }

}
