package com.yqg.drools.extract;

import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.drools.beans.ContactData;
import com.yqg.drools.model.ContactInfo;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserContactService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private UserContactService userContactService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisClient redisClient;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
//        return RuleSetEnum.CONTACT_INFO.equals(ruleSet) || RuleSetEnum.RE_BORROWING_CONTACT
//            .equals(ruleSet);
        return false;
    }

    @Override
    public Optional<ContactInfo> extractModel(OrdOrder order, KeyConstant keyConstant) {

        List<ContactData> contactList = userContactService.getContactList(order.getUserUuid(),order.getUuid());
        if (CollectionUtils.isEmpty(contactList)) {
            log.warn("contact data is empty orderNo: {}", order.getUuid());
            return Optional.empty();
        }
        contactList = contactList.stream().filter(
            elem -> StringUtils.isNotEmpty(elem.getName()) && StringUtils
                .isNotEmpty(elem.getPhone()))
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(contactList)) {
            log.warn("contact info with empty data,orderNo: {}", order.getUuid());
            return Optional.empty();
        }


        //手机号列表
        List<String> phoneNumbers = contactList.stream()
            .map(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getPhone())).distinct()
            .filter(elem -> StringUtils.isNotEmpty(elem))
            .collect(Collectors.toList());
        // contactInfo.setPhoneNumbers(phoneNumbers);
        if (CollectionUtils.isEmpty(phoneNumbers)) {
            log.warn("no valid contact info, orderNo: {}", order.getUuid());
            return Optional.empty();
        }


        ContactInfo contactInfo = new ContactInfo();
        //名称列表
        List<String> names = contactList.stream().map(ContactData::getName).collect(
            Collectors.toList());
        //contactInfo.setNames(names);

        contactInfo.setPhoneCount(RuleUtils.distinctCount(phoneNumbers));

        contactInfo.setSensitiveWordCount(
            RuleUtils.distinctEqualsCount(keyConstant.getSensitiveWords(), names));

        //同业词[所有联系人当中，匹配到同业词的]
        contactInfo.setInterrelatedWordCount(
            RuleUtils.distinctKeyWordsCountWithEqual(keyConstant.getInterrelatedWords(), names));

        contactInfo.setRelativeWordCount(
            RuleUtils.distinctEqualsCount(keyConstant.getRelativeWords(), names));

        //亲属词拒绝概率
        contactInfo.setRelativeWordRejectedByProbability(!RuleUtils.refuseJudge(0.5F, redisClient));
        log.info("the relativeWordRejected by probability result: {} , orderNo: {}",
            contactInfo.isRelativeWordRejectedByProbability(), order.getUuid());

        //第一联系人号码
        contactInfo.setFirstLinkManNumber(userService.getUserLinkManPhoneNumber(1, order.getUserUuid()));
        //第二联系人号码
        contactInfo.setSecondLinkManNumber(userService.getUserLinkManPhoneNumber(2, order.getUserUuid()));

        contactInfo.setFirstLinkManNotIn(
            StringUtils.isNotEmpty(contactInfo.getFirstLinkManNumber()) && !phoneNumbers
                .contains(contactInfo.getFirstLinkManNumber()));

        contactInfo.setSecondLinkManNotIn(
            StringUtils.isNotEmpty(contactInfo.getSecondLinkManNumber()) && !phoneNumbers
                .contains(contactInfo.getSecondLinkManNumber()));

        return Optional.of(contactInfo);
    }



}
