package com.yqg.drools.extract;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.NumberUtils;
import com.yqg.drools.beans.ContactData;
import com.yqg.drools.beans.UserCallRecordsData;
import com.yqg.drools.beans.UserCallRecordsData.CallType;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.UserCallRecordsModel;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserCallRecordService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.service.UsrBlackListService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by luhong on 2018/1/23.
 */
@Service
@Slf4j
public class UserCallRecordsExtractor implements BaseExtractor<UserCallRecordsModel> {


    @Autowired
    private UserCallRecordService userCallRecordService;// 用户通话记录
    @Autowired
    private UserContactsDal userContactsDal;// 用户通讯录

    @Autowired
    private UsrBlackListService usrBlackListService;

    @Autowired
    private UserService userService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
//        return RuleSetEnum.USER_CALL_RECORDS.equals(ruleSet) || RuleSetEnum.RE_BORROWING_CALL_RECORD
//            .equals(ruleSet);
        return false;
    }

    @Override
    public Optional<UserCallRecordsModel> extractModel(OrdOrder order, KeyConstant keyConstant)
        throws Exception {

        List<UserCallRecordsData>  userCallRecordsList =userCallRecordService.getUserCallRecordList(order.getUserUuid(),order.getUuid());
        if(CollectionUtils.isEmpty(userCallRecordsList)){
            return Optional.empty();
        }
        /* 规则需要用到的数据 model */
        UserCallRecordsModel model = new UserCallRecordsModel();

        List<UserCallRecordsData> validRecords = userCallRecordsList.stream()
            .filter(elem -> elem.getMsgDate() != null).collect(
                Collectors.toList());

        if (CollectionUtils.isEmpty(validRecords)) {
            log.error("valid call record is empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }

        //近180天通话记录
        Date recent180Day = DateUtils.getDiffTime(order.getUpdateTime(), -180);// 推近180天
        //近90天通话记录
        Date recent90Day = DateUtils.getDiffTime(order.getUpdateTime(), -90);// 推近90天
        //近30天通话记录
        Date recent30Day = DateUtils.getDiffTime(order.getUpdateTime(), -30);// 推近30天
        List<UserCallRecordsData> recent180RecordList = validRecords.stream().filter(
            elem -> DateUtil
                .filterLimitDate(recent180Day, elem.getMsgDate(), order.getUpdateTime())).collect(
            Collectors.toList());

        List<UserCallRecordsData> recent90RecordList = validRecords.stream().filter(
            elem -> DateUtil
                .filterLimitDate(recent90Day, elem.getMsgDate(), order.getUpdateTime())).collect(
            Collectors.toList());

        List<UserCallRecordsData> recent30RecordList = validRecords.stream().filter(
            elem -> DateUtil
                .filterLimitDate(recent30Day, elem.getMsgDate(), order.getUpdateTime())).collect(
            Collectors.toList());

        // 手机使用时长（单位：天）
        Optional<UserCallRecordsData> earliestMsg = validRecords.stream()
            .min(Comparator.comparing(UserCallRecordsData::getMsgDate));
        if (earliestMsg.isPresent()) {
            //
            model.setDiffTime(
                    DateUtil.getDiffDays(DateUtil.formatDate(earliestMsg.get().getMsgDate(), DateUtil.FMT_YYYY_MM_DD), DateUtil.formatDate(order
                            .getApplyTime(), DateUtil.FMT_YYYY_MM_DD)));
        }

        //近180天duration的统计数据
        IntSummaryStatistics recent180DurationStatistics =
            recent180RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();
        //近90天duration的统计数据
        IntSummaryStatistics recent90DurationStatistics =
            recent90RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        //近30天duration的统计数据
        IntSummaryStatistics recent30DurationStatistics =
            recent30RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        // 近180天通话时长
        model.setRecent180Time(recent180DurationStatistics.getSum());
        // 近90天通话时长
        model.setRecent90Time(recent90DurationStatistics.getSum());
        // 近30天通话时长
        model.setRecent30Time(recent30DurationStatistics.getSum());

        // 近180天通话次数
        model.setRecent180Count(recent180DurationStatistics.getCount());
        // 近90天通话次数
        model.setRecent90Count(recent90DurationStatistics.getCount());
        // 近30天通话次数
        model.setRecent30Count(recent30DurationStatistics.getCount());

        //近180天duration的统计数据
        IntSummaryStatistics recent180DurationStatisticsCallIn =
            recent180RecordList.stream()
                .filter(elem -> elem.getDuration() != null
                    && elem.getDuration() > 0 && CallType.IN
                    .getCode().equals(elem.getType()))
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();
        //近90天duration的统计数据
        IntSummaryStatistics recent90DurationStatisticsCallIn =
            recent90RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0 && CallType.IN
                    .getCode().equals(elem.getType()))
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        //近30天duration的统计数据
        IntSummaryStatistics recent30DurationStatisticsCallIn =
            recent30RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0
                    && elem.getDuration() > 0 && CallType.IN
                    .getCode().equals(elem.getType()))
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        // 近180天打入通话时长
        model.setRecent180InTime(recent180DurationStatisticsCallIn.getSum());
        // 近90天打入通话时长
        model.setRecent90InTime(recent90DurationStatisticsCallIn.getSum());
        // 近30天打入通话时长
        model.setRecent30InTime(recent30DurationStatisticsCallIn.getSum());
        // 近180天打入通话次数
        model.setRecent180InCount(recent180DurationStatisticsCallIn.getCount());
        // 近90天打入通话次数
        model.setRecent90InCount(recent90DurationStatisticsCallIn.getCount());
        // 近30天打入通话次数
        model.setRecent30InCount(recent30DurationStatisticsCallIn.getCount());
        /////////

        //近180天duration的统计数据
        IntSummaryStatistics recent180DurationStatisticsCallOut =
            recent180RecordList.stream()
                .filter(elem -> elem.getDuration() != null
                    && elem.getDuration() > 0 && CallType.OUT
                    .getCode().equals(elem.getType()))
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();
        //近90天duration的统计数据
        IntSummaryStatistics recent90DurationStatisticsCallOut =
            recent90RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0 && CallType.OUT
                    .getCode().equals(elem.getType()))
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        //近30天duration的统计数据
        IntSummaryStatistics recent30DurationStatisticsCallOut =
            recent30RecordList.stream()
                .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0
                    && elem.getDuration() > 0 && CallType.OUT
                    .getCode().equals(elem.getType()))
                .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        // 近180天打出通话时长

        model.setRecent180OutTime(recent180DurationStatisticsCallOut.getSum());
        // 近90天打出通话时长
        model.setRecent90OutTime(recent90DurationStatisticsCallOut.getSum());
        // 近30天打出通话时长
        model.setRecent30OutTime(recent30DurationStatisticsCallOut.getSum());
        // 近180天打出通话次数
        model.setRecent180OutCount(recent180DurationStatisticsCallOut.getCount());
        // 近90天打出通话次数
        model.setRecent90OutCount(recent90DurationStatisticsCallOut.getCount());
        // 近30天打出通话次数
        model.setRecent30OutCount(recent30DurationStatisticsCallOut.getCount());

        // 最后一次跟第一联系人有效通话时间距提交订单的天数
        String firstContact = userService.getUserLinkManPhoneNumber(1, order.getUserUuid());

        Optional<UserCallRecordsData> maxDate = validRecords.stream()
            .filter(elem -> elem.getNumber() != null && CheakTeleUtils
                .telephoneNumberValid2(elem.getNumber()).equals(firstContact))
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .max(Comparator.comparing(UserCallRecordsData::getMsgDate));

        if (maxDate.isPresent()) {
            Long firstContactDiffDay =
                (order.getUpdateTime().getTime() - maxDate.get().getMsgDate().getTime()) / (24 * 60
                    * 60 * 1000);
            model.setFirstContactDiffDay(firstContactDiffDay);
        }
        // 最后一次跟第二联系人有效通话时间距提交订单的天数
        String secondContact = userService.getUserLinkManPhoneNumber(2, order.getUserUuid());
        Optional<UserCallRecordsData> maxDate1 = validRecords.stream()
            .filter(elem -> elem.getNumber() != null && CheakTeleUtils
                .telephoneNumberValid2(elem.getNumber()).equals(secondContact))
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .max(Comparator.comparing(UserCallRecordsData::getMsgDate));

        if (maxDate1.isPresent()) {
            Long secondContactDiffDay =
                (order.getUpdateTime().getTime() - maxDate1.get().getMsgDate().getTime()) / (24 * 60
                    * 60 * 1000);
            model.setSecondContactDiffDay(secondContactDiffDay);
        }
        // 最后一次通话距提交订单日期的天数
        Optional<UserCallRecordsData> maxDate2 = validRecords.stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .max(Comparator.comparing(UserCallRecordsData::getMsgDate));

        if (maxDate2.isPresent()) {
            Long lastContactDiffDay =
                (order.getUpdateTime().getTime() - maxDate2.get().getMsgDate().getTime()) / (24 * 60
                    * 60 * 1000);
            model.setLastContactDiffDay(lastContactDiffDay);
        }
        // 近90天呼入占比： 近90天打入通话次数 recent90InCount / 近90天总通话次数
        //[包含duration=0的情况]

        Long recent90CallIn = recent90RecordList.stream().filter(
            elem -> elem.getDuration() != null && CallType.IN.getCode().equals(elem.getType()))
            .count();
        Long recent90CallOut = recent90RecordList.stream().filter(
            elem -> elem.getDuration() != null && CallType.OUT.getCode().equals(elem.getType()))
            .count();
        Long recent90Call = recent90RecordList.stream().filter(
            elem -> elem.getDuration() != null)
            .count();

        Float recent90InRate = NumberUtils
            .division(recent90CallIn, recent90Call);
        model.setRecent90InRate(recent90InRate);
        // 近90天呼出占比
        Float recent90OutRate = NumberUtils
            .division(recent90CallOut, recent90Call);
        model.setRecent90OutRate(recent90OutRate);

        Long recent30CallIn = recent30RecordList.stream().filter(
            elem -> elem.getDuration() != null && CallType.IN.getCode().equals(elem.getType()))
            .count();
        Long recent30CallOut = recent30RecordList.stream().filter(
            elem -> elem.getDuration() != null && CallType.OUT.getCode().equals(elem.getType()))
            .count();
        Long recent30AllCall = recent30RecordList.stream().filter(
            elem -> elem.getDuration() != null)
            .count();

        // 近30天呼入占比
        Float recent30InRate = NumberUtils
            .division(recent30CallIn, recent30AllCall);
        model.setRecent30InRate(recent30InRate);
        // 近30天呼出占比
        Float recent30OutRate = NumberUtils
            .division(recent30CallOut, recent30AllCall);
        model.setRecent30OutRate(recent30OutRate);

        // 近180天打入未接通通话次数
        Long recent180InNoCount = recent180RecordList.stream().
            filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
            filter(elem -> !CallType.OUT.getCode().equals(elem.getType())).
            count();
        model.setRecent180InNoCount(recent180InNoCount);
        // 近90天打入未接通通话次数
        Long recent90InNoCount = recent90RecordList.stream().
            filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
            filter(elem -> !CallType.OUT.getCode().equals(elem.getType())).
            count();
        model.setRecent90InNoCount(recent90InNoCount);
        // 近30天打入未接通通话次数
        Long recent30InNoCount = recent30RecordList.stream().
            filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
            filter(elem -> !CallType.OUT.getCode().equals(elem.getType())).
            count();
        model.setRecent30InNoCount(recent30InNoCount);

        // 近180天打入未接通占比
        Long recent180TotalInCount = recent180RecordList.stream()
            .filter(elem -> !CallType.OUT.getCode().equals(elem.getType())).count();
        Float recent180InNoRate = NumberUtils
            .division(recent180InNoCount, recent180TotalInCount);
        model.setRecent180InNoRate(recent180InNoRate);

        // 近90天打入未接通占比
        Long recent90TotalInCount = recent90RecordList.stream()
            .filter(elem -> !CallType.OUT.getCode().equals(elem.getType())).count();

        Float recent90InNoRate = NumberUtils.division(recent90InNoCount, recent90TotalInCount);
        model.setRecent90InNoRate(recent90InNoRate);

        // 近30天打入未接通占比
        Long recent30TotalInCount = recent30RecordList.stream()
            .filter(elem -> !CallType.OUT.getCode().equals(elem.getType())).count();

        Float recent30InNoRate = NumberUtils.division(recent30InNoCount, recent30TotalInCount);
        model.setRecent30InNoRate(recent30InNoRate);

        // 近30天内夜间活跃占比
        Long recent30NightCall = recent30RecordList.stream().filter(elem -> elem.isCallInEvening())
            .count();

        Float nightCallRate = NumberUtils.division(recent30NightCall, recent30AllCall);
        model.setNightCallRate(nightCallRate);

        // 近180天内未接通电话占比
        Long recent180CallNoConnectCount = recent180RecordList.stream().
            filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
            count();
        Float recent180CallRate = NumberUtils
            .division(recent180CallNoConnectCount, recent180RecordList.size());
        model.setRecent180CallRate(recent180CallRate);
        // 近90天内未接通电话占比
        Long recent90CallNoConnectCount = recent90RecordList.stream().
            filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
            count();
        Float recent90CallRate = NumberUtils
            .division(recent90CallNoConnectCount, recent90RecordList.size());
        model.setRecent90CallRate(recent90CallRate);
        // 近30天内未接通电话占比
        Long recent30CallNoConnectCount = recent30RecordList.stream().
            filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
            count();
        Float recent30CallRate = NumberUtils
            .division(recent30CallNoConnectCount, recent30RecordList.size());
        model.setRecent30CallRate(recent30CallRate);
        model.setRecent30NotConnectedCallCount(recent30CallNoConnectCount);

        // 近30天的通话记录中，出现在通讯录中的有效去重通话号码个数
        // 通讯录
        List<ContactData> contactList = getContactList(order);
        if (!CollectionUtils.isEmpty(contactList)) {
            model.setHasContact(true);
        }

        // 用户通讯录 去重
        List<String> contactDistinctPhoneList = contactList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getPhone()))
            .map(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getPhone()))
            .filter(elem -> !StringUtils.isEmpty(elem))
            .distinct().collect(
                Collectors.toList());

        // 用户通话记录 去重
        List<String> callRecordDistinctPhoneList = recent30RecordList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getNumber()))
            .map(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber()))
            .filter(elem -> !StringUtils.isEmpty(elem))
            .distinct().collect(
                Collectors.toList());

//        // 用户通讯录 去重
//        for (ContactData userContact : contactList) {
//            if (com.yqg.common.utils.StringUtils.isNotEmpty(userContact.getPhone())) {
//                userContactSet.add(CheakTeleUtils.telephoneNumberValid2(userContact.getPhone()));
//            }
//        }
//        // 用户通话记录 去重
//        for (UserCallRecordsData userCallRecord : userCallRecordsList) {
//            if (com.yqg.common.utils.StringUtils.isNotEmpty(userCallRecord.getNumber())) {
//                userCallRecordSet
//                    .add(CheakTeleUtils.telephoneNumberValid2(userCallRecord.getNumber()));
//            }
//        }
//        // 有交集的通讯录个数
//        Integer contacts = 0;
//        for (String userContact : userContactSet) {
//            for (String userCallRecord : userCallRecordSet) {
//                if (userContact.equals(userCallRecord)) {
//                    contacts++;
//                }
//            }
//        }

        if (CollectionUtils.isEmpty(contactDistinctPhoneList) || CollectionUtils
            .isEmpty(callRecordDistinctPhoneList)) {
            model.setContacts(null);
            model.setContactsRate(null);
        } else {
            //筛选通讯录手机被通过话记录包含的
            Long contacts = callRecordDistinctPhoneList.stream()
                .filter(elem -> contactDistinctPhoneList.contains(elem)).count();
            model.setContacts(contacts);
            // 近30天的通话记录中，出现在通讯录中的有效去重通话号码占比[交叉的手机号码/总的通讯录号码数量]
            Float contactsRate = NumberUtils.division(contacts, contactDistinctPhoneList.size());
            model.setContactsRate(contactsRate);
        }

        // 近90天内与亲属称谓联系人的通话次数
        List<String> names90 = recent90RecordList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getName()))
            .map(UserCallRecordsData::getName).collect(Collectors.toList());
        //[和之前规则保持一致，统计打电话的亲属个数]
        Long recent90CallRelaCount = RuleUtils
            .distinctEqualsCount(keyConstant.getRelativeWords(), names90);
        model.setRecent90CallRelaCount(recent90CallRelaCount);
        // 近30天内与亲属称谓联系人的通话次数
        List<String> names30 = recent30RecordList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getName()))
            .map(UserCallRecordsData::getName).collect(Collectors.toList());
        Long recent30CallRelaCount = RuleUtils
            .distinctEqualsCount(keyConstant.getRelativeWords(), names30);
        model.setRecent30CallRelaCount(recent30CallRelaCount);
        // 近 30、90、180 天无通话记录总天数
        Set<String> set90 = new HashSet<>();
        Set<String> set30 = new HashSet<>();
        Set<String> set180 = new HashSet<>();
        for (UserCallRecordsData item : userCallRecordsList) {
            if (null != item.getDuration() && item.getDuration() > 0) {
                Date itemDay = DateUtils.stringToDate(item.getDate());
                String reg = " ";
                String[] date = item.getDate().split(reg);
                String strDay = date[0];// 精确到天
                if (itemDay.before(order.getUpdateTime()) && itemDay
                    .after(recent90Day)) {// recentXDay<itemDay<order.getUpdateTime()
                    set90.add(strDay);
                }
                if (itemDay.before(order.getUpdateTime()) && itemDay
                    .after(recent30Day)) {// recentXDay<itemDay<order.getUpdateTime()
                    set30.add(strDay);
                }
                if (itemDay.before(order.getUpdateTime()) && itemDay.after(recent180Day)) {
                    set180.add(strDay);
                }
            }
        }
        // 近90天无通话记录总天数
        Integer recent90NoCallDay = 91 - set90.size();
        model.setRecent90NoCallDay(recent90NoCallDay);
        // 近30天无通话记录总天数
        Integer recent30NoCallDay = 31 - set30.size();
        model.setRecent30NoCallDay(recent30NoCallDay);

        //180天内的无通话天数
        model.setRecent180NoCallDay(181 - set180.size());

        IntSummaryStatistics recent180FirstContactCallRecordStatistics = recent180RecordList
            .stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .filter(
                elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber()).equals(firstContact))
            .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();
        IntSummaryStatistics recent90FirstContactCallRecordStatistics = recent90RecordList.stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .filter(
                elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber()).equals(firstContact))
            .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        IntSummaryStatistics recent30FirstContactCallRecordStatistics = recent30RecordList.stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .filter(
                elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber()).equals(firstContact))
            .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        // 第一联系人,近180天,通话时长
        model.setFirstCall180Time(recent180FirstContactCallRecordStatistics.getSum());
        // 第一联系人,近180天,通话次数
        model.setFirstCall180Count(recent180FirstContactCallRecordStatistics.getCount());

        // 第一联系人,近90天,通话时长
        model.setFirstCall90Time(recent90FirstContactCallRecordStatistics.getSum());
        // 第一联系人,近90天,通话次数
        model.setFirstCall90Count(recent90FirstContactCallRecordStatistics.getCount());
        // 第一联系人,近30天,通话时长
        model.setFirstCall30Time(recent30FirstContactCallRecordStatistics.getSum());
        // 第一联系人,近30天,通话次数
        model.setFirstCall30Count(recent30FirstContactCallRecordStatistics.getCount());
        // 第一联系人,在通话记录中，为true，反之为false
        Long firstCount = validRecords.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getNumber()) &&
                CheakTeleUtils.telephoneNumberValid2(elem.getNumber()).equals(firstContact))
            .count();
        if (firstCount > 0) {
            model.setFirstLinkManIn(true);
        } else {
            model.setFirstLinkManIn(false);
        }
        /*第二联系人*/
        IntSummaryStatistics recent180SecondContactCallRecordStatistics = recent180RecordList
            .stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .filter(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber())
                .equals(secondContact))
            .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();
        IntSummaryStatistics recent90SecondContactCallRecordStatistics = recent90RecordList.stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .filter(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber())
                .equals(secondContact))
            .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        IntSummaryStatistics recent30SecondContactCallRecordStatistics = recent30RecordList.stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0)
            .filter(elem -> CheakTeleUtils.telephoneNumberValid2(elem.getNumber())
                .equals(secondContact))
            .mapToInt(UserCallRecordsData::getDuration).summaryStatistics();

        // 第二联系人,近180天,通话时长
        model.setSecondCall180Time(recent180SecondContactCallRecordStatistics.getSum());
        // 第二联系人,近180天,通话次数
        model.setSecondCall180Count(recent180SecondContactCallRecordStatistics.getCount());
        // 第二联系人,近90天,通话时长
        model.setSecondCall90Time(recent90SecondContactCallRecordStatistics.getSum());
        // 第二联系人,近90天,通话次数
        model.setSecondCall90Count(recent90SecondContactCallRecordStatistics.getCount());
        // 第二联系人,近30天,通话时长
        model.setSecondCall30Time(recent30SecondContactCallRecordStatistics.getSum());
        // 第二联系人,近30天,通话次数
        model.setSecondCall30Count(recent30SecondContactCallRecordStatistics.getCount());
        // 第一联系人,在通话记录中，为true，反之为false
        Long secondCount = validRecords.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getNumber()) &&
                CheakTeleUtils.telephoneNumberValid2(elem.getNumber()).equals(secondContact))
            .count();
        if (secondCount > 0) {
            model.setSecondLinkManIn(true);
        } else {
            model.setSecondLinkManIn(false);
        }

//        //通话记录号码命中逾期30天以上黑名单次数
//        model.setCallRecordContactOverdueDays30Times(0);
        //近15天内夜间活跃占比【0~5点】
        model.setRecent15EveningActiveRatio(BigDecimal.ZERO);

        Date recent15Day = DateUtils.getDiffTime(order.getUpdateTime(), -15);// 推近180天
        List<UserCallRecordsData> recent15Records = userCallRecordsList.stream()
            .filter(elem -> elem.getMsgDate() != null && DateUtil
                .filterLimitDate(recent15Day, elem.getMsgDate(), order.getUpdateTime()))
            .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(recent15Records)) {
            Long eveningCount = recent15Records.stream().filter(elem -> elem.isCallInEvening())
                .count();
            Long totalRecent15Count = recent15Records.stream().count();
            model.setRecent15EveningActiveRatio(new BigDecimal(eveningCount.toString())
                .divide(new BigDecimal(totalRecent15Count.toString()), 6,
                    BigDecimal.ROUND_HALF_UP));
        }

        // 最后一次通话距提交订单的天数（单位：天）
        Optional<UserCallRecordsData> latestCallRecord = validRecords.stream()
            .filter(elem -> elem.getDuration() != null && elem.getDuration() > 0).max(
                Comparator.comparing(UserCallRecordsData::getMsgDate));
        if (latestCallRecord.isPresent()) {
            model.setLastContactDiffDay(
                DateUtil.getDiffDays(latestCallRecord.get().getMsgDate(), order.getUpdateTime()));
        }

        //近30天主叫号码数
        Long callOutPhones = recent30RecordList.stream()
            .filter(elem -> CallType.OUT.getCode().equals(elem.getType()))
            .map(UserCallRecordsData::getNumber).count();
        //近30天被叫号码数
        Long callInPhones = recent30RecordList.stream()
            .filter(elem -> !CallType.IN.getCode().equals(elem.getType()))
            .map(UserCallRecordsData::getNumber).count();
        model.setRecent30CallOutPhones(callOutPhones);
        model.setRecent30CallInPhones(callInPhones);

        Long recent30DistinctCallNumbers = recent30RecordList.stream()
                .filter(elem -> !StringUtils.isEmpty(elem.getNumber()))
                .map(UserCallRecordsData::getNumber).distinct().count();

        Long recent30DistinctCallInNumbers = recent30RecordList.stream()
                .filter(elem -> !CallType.OUT.getCode().equals(elem.getType()) && !StringUtils.isEmpty(elem.getNumber()))
                .map(UserCallRecordsData::getNumber).distinct().count();

        Long recent90DistinctCallNumbers = recent90RecordList.stream()
                .filter(elem -> !StringUtils.isEmpty(elem.getNumber()))
                .map(UserCallRecordsData::getNumber).distinct().count();

        Long recent90DistinctCallInNumbers = recent90RecordList.stream()
                .filter(elem -> !CallType.OUT.getCode().equals(elem.getType()) && !StringUtils.isEmpty(elem.getNumber()))
                .map(UserCallRecordsData::getNumber).distinct().count();

        //近90天陌生人打入未接
        Long recent90StrangeCallInMissed = recent90RecordList.stream()
                .filter(elem -> {
                    if(elem.getDuration()!=null&&elem.getDuration()>0){
                        return false;
                    }
                    if(CallType.OUT.getCode().equals(elem.getType())){
                        return false;
                    }
                    if(StringUtils.isEmpty(elem.getName())){
                        return true;
                    }
                    if(elem.getNumber().startsWith("Unreported contacts")||elem.getNumber().startsWith("未备注联系人")){
                        return true;
                    }
                    return false;
                }).count();

        //近90天陌生人打入
        Long recent90StrangeCallIn = recent90RecordList.stream()
                .filter(elem -> {
                    if(CallType.OUT.getCode().equals(elem.getType())){
                        return false;
                    }
                    if(StringUtils.isEmpty(elem.getName())){
                        return true;
                    }
                    if(elem.getName().startsWith("Unreported contacts")||elem.getName().startsWith("未备注联系人")){
                        return true;
                    }
                    return false;
                }).count();

        model.setRecent30DistinctCallNumbers(recent30DistinctCallNumbers);
        model.setRecent90DistinctCallNumbers(recent90DistinctCallNumbers);
        model.setRecent30DistinctCallInNumbers(recent30DistinctCallInNumbers);
        model.setRecent90DistinctCallInNumbers(recent90DistinctCallInNumbers);
        BigDecimal ratio = recent90StrangeCallIn == null || recent90StrangeCallIn == 0 ? null : new BigDecimal(recent90StrangeCallInMissed.toString())
                .divide(new BigDecimal(recent90StrangeCallIn.toString()), 6, BigDecimal.ROUND_HALF_UP);
        model.setRecent90StrangeNumberMissedCallRatio(ratio);

        return Optional.of(model);
    }


    /***
     * 查询通讯录信息
     * @param order
     * @return
     */
    private List<ContactData> getContactList(OrdOrder order) {
        UserContactsMongo search1 = new UserContactsMongo();
        search1.setUserUuid(order.getUserUuid());
        search1.setOrderNo(order.getUuid());
        List<UserContactsMongo> orderUserContacts = this.userContactsDal.find(search1);
        if (CollectionUtils.isEmpty(orderUserContacts)) {
            log.warn("mongodb contact data is empty orderNo: {}", order.getUuid());
            return new ArrayList<>();
        }

        UserContactsMongo userContactsMongo = orderUserContacts.get(0);
        String userContactData = userContactsMongo.getData();
        List<ContactData> contactList = JsonUtil.toList(userContactData, ContactData.class);
        if (CollectionUtils.isEmpty(contactList)) {
            log.warn("contact data is empty orderNo: {}", order.getUuid());
            return new ArrayList<>();
        }
        return contactList;
    }

    private Map<String, Date> boxNightDate(String date) throws ParseException {
        Map<String, Date> map = new HashedMap();
        String regex = "\\s";
        String[] dateStr = date.split(regex);
        String day = dateStr[0];
        Date startDayDate = null;
        startDayDate = DateUtils.stringToDate4(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDayDate);
        calendar.add(Calendar.HOUR, 5);
        Date endDayDate = calendar.getTime();
        map.put("startDayDate", startDayDate);
        map.put("endDayDate", endDayDate);
        return map;
    }


}
