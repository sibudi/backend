package com.yqg.drools.extract;

import java.util.Optional;

import com.yqg.drools.model.ContactInfo;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.order.entity.OrdOrder;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/22
 * @Email zengxiangcai@yishufu.com
 * 通讯录信息抓取
 *
 ****/

@Service
@Slf4j
public class ContactInfoExtractor implements BaseExtractor<ContactInfo> {

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
//        return RuleSetEnum.CONTACT_INFO.equals(ruleSet) || RuleSetEnum.RE_BORROWING_CONTACT
//            .equals(ruleSet);
        return false;
    }

    @Override
    public Optional<ContactInfo> extractModel(OrdOrder order, KeyConstant keyConstant) {

        //ahalim: TODO return empty now until found out how to delete this
        return Optional.empty();
        //ContactInfo contactInfo = new ContactInfo();
    }



}
