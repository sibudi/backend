package com.yqg.drools.extract;

import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.order.entity.OrdOrder;
import java.util.Optional;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/19
 * @Email zengxiangcai@yishufu.com
 *
 ****/

public interface BaseExtractor<T> {

    boolean filter(RuleSetEnum ruleSet);

    Optional<T> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception;

}
