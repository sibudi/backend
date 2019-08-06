package com.yqg.drools.extract;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.drools.beans.ShortMessageData;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.ShortMessage;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.ShortMessageService;
import com.yqg.drools.service.UsrBlackListService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.dao.UserMessagesDal;
import com.yqg.mongo.entity.UserMessagesMongo;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class ShortMessageExtractor implements BaseExtractor<ShortMessage> {

    @Autowired
    private ShortMessageService shortMessageService;

    @Autowired
    private UsrBlackListService usrBlackListService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
//        return RuleSetEnum.SHORT_MESSAGE.equals(ruleSet) || RuleSetEnum.RE_BORROWING_SHORT_MESSAGE
//            .equals(ruleSet);
        return false;
    }

    @Override
    public Optional<ShortMessage> extractModel(OrdOrder order, KeyConstant keyConstant) {

         List<ShortMessageData> shortMessageList = shortMessageService.getShortMessageList(order.getUserUuid(),order.getUuid());

         if(CollectionUtils.isEmpty(shortMessageList)){
             return Optional.empty();
         }

        List<String> smsBodyList = shortMessageList.stream()
                .filter(elem -> !StringUtils.isEmpty(elem.getSmsBody()))
                .map(elem -> elem.getSmsBody().toUpperCase()).collect(
                        Collectors.toList());
        if (CollectionUtils.isEmpty(smsBodyList)) {
            return Optional.empty();
        }
        ShortMessage shortMessage = new ShortMessage();
        //逾期词
        shortMessage.setOverdueWordsCount(
            RuleUtils.distinctContainsCount(keyConstant.getOverdueWords(), smsBodyList));

        //负面词
        shortMessage.setNegativeWordsCount(
            RuleUtils.distinctContainsCount(keyConstant.getNegativeWords(), smsBodyList));
        //同业词
        shortMessage.setInterrelatedWordsCount(
            RuleUtils.distinctKeyWordsCount(keyConstant.getInterrelatedWords(), smsBodyList));

        //总条数
        shortMessage.setTotalCount(smsBodyList.stream().count());

        Date before90Days = DateUtils.getDiffTime(order.getUpdateTime(), -90);
        Date before30Days = DateUtils.getDiffTime(order.getUpdateTime(), -30);

        //近90天
        List<ShortMessageData> recent90SmsList = shortMessageList.stream()
            .filter(
                elem -> !StringUtils.isEmpty(elem.getSmsBody())
                    && DateUtil
                    .filterLimitDate(before90Days, elem.getMsgDate(), order.getUpdateTime()))
            .collect(Collectors.toList());

        //近30天
        List<ShortMessageData> recent30SmsList = shortMessageList.stream()
            .filter(
                elem -> !StringUtils.isEmpty(elem.getSmsBody())
                    && DateUtil
                    .filterLimitDate(before30Days, elem.getMsgDate(), order.getUpdateTime()))
            .collect(Collectors.toList());

        //近90天逾期短信号码数量(根据手机号去重)
        shortMessage.setRecent90OverdueMsgDistinctCountByNumber(recent90SmsList.stream()
            .filter(elem -> RuleUtils.distinctContainsCount(keyConstant.getOverdueWords(),
                Arrays.asList(elem.getSmsBody())) > 0).map(ShortMessageData::getPhoneNumber)
            .distinct().count());
        //近30天逾期短信号码数量
        shortMessage.setRecent30OverdueMsgDistinctCountByNumber(recent30SmsList.stream()
            .filter(elem -> RuleUtils.distinctContainsCount(keyConstant.getOverdueWords(),
                Arrays.asList(elem.getSmsBody())) > 0).map(ShortMessageData::getPhoneNumber)
            .distinct().count());

        //近90天手机发送的短信条数[手机号码发送的]
        shortMessage.setRecent90TotalMsgWithPhoneCount(recent90SmsList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getPhoneNumber()) && CheakTeleUtils
                .telephoneNumberValid(elem.getPhoneNumber())).count());
        //近30天手机发送的短信条数
        shortMessage.setRecent30TotalMsgWithPhoneCount(recent30SmsList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getPhoneNumber()) && CheakTeleUtils
                .telephoneNumberValid(elem.getPhoneNumber())).count());

        //近90天短信条数
        shortMessage.setRecent90TotalMsgCount(recent90SmsList.stream().count());
        //近30天短信条数
        shortMessage.setRecent30TotalMsgCount(recent30SmsList.stream().count());

        //近30天被拒短信
        shortMessage.setRecent30RejectedMsgCount(recent30SmsList.stream()
            .filter(elem -> RuleUtils.distinctContainsCount(keyConstant.getRejectWords(),
                Arrays.asList(elem.getSmsBody())) > 0).count());
        //最早的一条短信距离申请的时间
        Optional<ShortMessageData> earliestMsg = shortMessageList.stream()
            .filter(elem -> elem.getMsgDate() != null).min(
                Comparator.comparing(ShortMessageData::getMsgDate));

        if (earliestMsg.isPresent()) {
            shortMessage.setDiffDaysForEarliestMsgAndApplyTime(
                DateUtil.getDiffDays(earliestMsg.get().getMsgDate(), order.getApplyTime()));
        }

        //近30逾期短信累计条数
        shortMessage.setRecent30OverdueMsgTotalCount(recent30SmsList.stream()
            .filter(elem -> RuleUtils.distinctContainsCount(keyConstant.getOverdueWords(),
                Arrays.asList(elem.getSmsBody())) > 0).count());

        //近15天手机号码发送的短信条数
        Date before15Days = DateUtils.getDiffTime(order.getUpdateTime(), -15);

        //近15天
        List<ShortMessageData> recent15SmsList = shortMessageList.stream()
            .filter(
                elem -> !StringUtils.isEmpty(elem.getSmsBody())
                    && DateUtil
                    .filterLimitDate(before15Days, elem.getMsgDate(), order.getUpdateTime()))
            .collect(Collectors.toList());
        //非空手机号
        List<String> phoneList = recent15SmsList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getPhoneNumber()))
            .map(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getPhoneNumber())).distinct()
            .filter(elem -> !StringUtils.isEmpty(elem)).collect(Collectors.toList());

        shortMessage.setRecent15TotalMsgWithPhoneCount(phoneList.stream().count());

        // 短信平台逾期词（初借和复借都有）
        String ruleBodyStr = keyConstant.getSmsRuleBody();//
        String[] ruleBodyArr = ruleBodyStr.split("#");
        Set<String> morethan15Count = new HashSet<>();// 命中逾期大于15天的短信条数 SMS_OVERDUE_MORETHAN_15DAYS_COUNT
        Set<String> lessthan15AndMoreThan10Count = new HashSet<>();// 短信出现逾期10-15（含）天的平台个数 SMS_OVERDUE_LESSTHAN_15DAYS_COUNT
        Integer smsOverdueMaxDays = 0;// // 短信中出现的最大逾期天数 SMS_OVERDUE_MAX_DAYS
        if(!CollectionUtils.isEmpty(smsBodyList)){
            for (String smsBody : smsBodyList) {
                for (String ruleBody : ruleBodyArr) {
                    String num = RuleUtils.extractNumber(smsBody, ruleBody);
                    if (!StringUtils.isEmpty(num) && Integer.parseInt(num) > 15) {
                        morethan15Count.add(smsBody);
                    }
                    if (!StringUtils.isEmpty(num) && Integer.parseInt(num) <= 15 && Integer.parseInt(num) >= 10) {
                        //记录平台信息
                        String platform = RuleUtils.extractPatternData("\\[(.*)\\]#【(.*)】",smsBody);
                        lessthan15AndMoreThan10Count.add(platform);
                    }
                    if (!StringUtils.isEmpty(num) && Integer.parseInt(num) > smsOverdueMaxDays) {
                        smsOverdueMaxDays = Integer.parseInt(num);
                    }
                }
            }
        }
        shortMessage.setMorethan15Count(morethan15Count.size());
        shortMessage.setLessthan15AndMoreThan10Count(lessthan15AndMoreThan10Count.size());
        shortMessage.setSmsOverdueMaxDays(smsOverdueMaxDays);

        return Optional.of(shortMessage);
    }

}
