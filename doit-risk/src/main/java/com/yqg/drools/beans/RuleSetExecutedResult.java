package com.yqg.drools.beans;

import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.system.entity.SysParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/2/27
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleSetExecutedResult {

    public RuleSetExecutedResult(boolean ruleSetResult, SysAutoReviewRule rule){
        this.preExecuteResult = true;
        this.ruleSetResult = ruleSetResult;
        this.firstRejectRule = rule;
    }
    private boolean ruleSetResult;//规则集执行结果:true 通过  false: 拒绝

    private SysAutoReviewRule firstRejectRule;//第一条被拒的规则【如果ruleSetResult=false该字段非空】

    private boolean preExecuteResult = true;
}
