package com.yqg.drools.model.base;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 * 规则阈值[以规则名为key保存，对应数据库中ruleDetailType]
 ****/

@Getter
@Setter
public class RuleThresholdValues {

    private Map<String, String> thresholdMap;
    private Map<String, Integer> orderMap; //顺序[db中先执行的顺序值小，和drools规则中的]
    private Map<String, String> ruleDescMap; //规则描述

    public String getThresholdValue(String key) {
        if(thresholdMap==null){
            return null;
        }
        String thresholdValue = thresholdMap.get(key);
        if (StringUtils.isEmpty(thresholdValue)) {
            return "";
        }
        return thresholdValue;
    }

    public Integer getRuleOrder(String key) {
        if (orderMap == null) {
            return 0;
        }
        Integer dbSequence = orderMap.get(key);
        if (dbSequence != null) {
            return dbSequence * -1;
        }
        //没有数据的设置为最后跑
        return Integer.MIN_VALUE;
    }

    public String getRuleDesc(String key) {
        if(ruleDescMap==null){
            return null;
        }
        return ruleDescMap.get(key);
    }
}
