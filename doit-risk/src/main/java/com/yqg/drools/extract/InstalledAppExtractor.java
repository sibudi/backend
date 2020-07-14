package com.yqg.drools.extract;

import java.util.Optional;

import com.yqg.drools.model.InstalledAppInfo;
import com.yqg.drools.model.KeyConstant;
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
public class InstalledAppExtractor implements BaseExtractor<InstalledAppInfo> {

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.INSTALL_APP.equals(ruleSet) || RuleSetEnum.RE_BORROWING_INSTALLED_APP
            .equals(ruleSet);
    }

    @Override
    public Optional<InstalledAppInfo> extractModel(OrdOrder order, KeyConstant keyConstant) {
        //ahalim: TODO return empty now until found out how to delete this
        return Optional.empty();
    }
}
