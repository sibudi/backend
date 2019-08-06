package com.yqg.drools.model.base;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class RuleResult {

    private boolean pass = true; //命中拒绝的规则
    private String ruleName;
    private String realValue;
    private String desc;//结果描述
    private boolean isDataEmpty;//数据为空

    public void addToResultList(List list) {
        list.add(this);
    }


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
