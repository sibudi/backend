package com.yqg.service;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.CardIdUtils;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.NumberUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.ShortMessageData;
import com.yqg.drools.beans.UserCallRecordsData;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.dao.UserMessagesDal;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserMessagesMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.dao.UsrAddressDetailDao;
import com.yqg.user.dao.UsrStudentDetailDao;
import com.yqg.user.dao.UsrWorkDetailDao;
import com.yqg.user.entity.UsrAddressDetail;
import com.yqg.user.entity.UsrStudentDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/6/5
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class CaculateScoreHandler {
    private static BigDecimal BASIS_SCORE = new BigDecimal(478.34);

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;

    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;


    @Autowired
    private UserService userService;

    @Autowired
    private UserCallRecordsDal userCallRecordsDal;// ??????

    @Autowired
    private UserMessagesDal userMessagesDal;

    @Autowired
    private OrdDao ordDao;

    public static void main(String[] args) {
        BigDecimal value = new BigDecimal(478.34);
        BigDecimal ss = value.add(BigDecimal.valueOf(4));
        System.err.println(ss.setScale(2, BigDecimal.ROUND_HALF_UP));
    }



    public boolean doHandler(UsrUser user, OrdOrder order, Map<String, SysAutoReviewRule> codeEntityMap) throws Exception {
        BigDecimal score = new BigDecimal(0);
        /** ???1???2??? **/
        if (user.getSex() == 1) {
            score = BASIS_SCORE.subtract(BigDecimal.valueOf(2));
            log.info(order.getUuid() + "===============>sex - 2");
        } else if (user.getSex() == 2) {
            score = BASIS_SCORE.add(BigDecimal.valueOf(8));
            log.info(order.getUuid() + "===============>sex + 8");
        } else {
            log.info(order.getUuid() + "===============>sex ??");
        }


        /** ?? **/
        String idCardNoStr = user.getIdCardNo();
        Integer age = 0;// ????
        if (StringUtils.isNotEmpty(idCardNoStr)) {
            age = CardIdUtils.getAgeByIdCard(idCardNoStr.trim());
        }
        if (age >= 18 && age <= 25) {
            score = score.subtract(BigDecimal.valueOf(3));
            log.info(order.getUuid() + "===============>age - 3");
        } else if (age > 25 && age <= 30) {
            score = score.add(BigDecimal.valueOf(1));
            log.info(order.getUuid() + "===============>age + 1");
        } else if (age > 30 && age <= 35) {
            score = score.add(BigDecimal.valueOf(6));
            log.info(order.getUuid() + "===============>age + 6");
        } else if (age > 35 && age <= 40) {
            score = score.add(BigDecimal.valueOf(8));
            log.info(order.getUuid() + "===============>age + 8");
        } else if (age > 40 && age <= 45) {
            score = score.add(BigDecimal.valueOf(3));
            log.info(order.getUuid() + "===============>age + 3");
        } else if (age > 45 && age <= 50) {
            score = score.add(BigDecimal.valueOf(5));
            log.info(order.getUuid() + "===============>age + 5");
        }


        /** ???? **/
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(user.getUuid());
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetailList = usrWorkDetailDao.scan(usrWorkDetail);
        if (!CollectionUtils.isEmpty(usrWorkDetailList)) {
            Integer maritalStatus = usrWorkDetailList.get(0).getMaritalStatus();
            if (maritalStatus == 0) {
                score = score.add(BigDecimal.valueOf(6));
                log.info(order.getUuid() + "===============>maritalStatus + 6");
            } else if (maritalStatus == 1) {
                score = score.add(BigDecimal.valueOf(14));
                log.info(order.getUuid() + "===============>maritalStatus + 14");
            } else if (maritalStatus == 2) {
                score = score.add(BigDecimal.valueOf(3));
                log.info(order.getUuid() + "===============>maritalStatus + 3");
            } else if (maritalStatus == 3) {
                score = score.subtract(BigDecimal.valueOf(2));
                log.info(order.getUuid() + "===============>maritalStatus - 2");
            }
        }


        /** ?? **/
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(user.getUuid());
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetailList = usrStudentDetailDao.scan(usrStudentDetail);
        if (!CollectionUtils.isEmpty(usrStudentDetailList)) {
            String academic = usrStudentDetailList.get(0).getAcademic();
            if (academic.equals("Sekolah dasar")) {
                score = score.subtract(BigDecimal.valueOf(4));
                log.info(order.getUuid() + "===============>academic - 4");
            } else if (academic.equals("Sekolah Menengah Pertama")) {
                score = score.subtract(BigDecimal.valueOf(2));
                log.info(order.getUuid() + "===============>academic - 2");
            } else if (academic.equals("Sekolah Menengah Atas")) {
                score = score.add(BigDecimal.valueOf(14));
                log.info(order.getUuid() + "===============>academic + 14");
            } else if (academic.equals("Diploma")) {
                score = score.add(BigDecimal.valueOf(18));
                log.info(order.getUuid() + "===============>academic + 18");
            } else if (academic.equals("Sarjana") || academic.equals("Pascasarjana") || academic.equals("Doktor")) {
                score = score.add(BigDecimal.valueOf(27));
                log.info(order.getUuid() + "===============>academic + 27");
            }
        }


        /** ???? **/
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(UsrAddressEnum.HOME.getType());
        usrAddressDetail.setUserUuid(order.getUserUuid());
        List<UsrAddressDetail> usrAddressDetailList = usrAddressDetailDao.scan(usrAddressDetail);
        if (!CollectionUtils.isEmpty(usrAddressDetailList)) {
            String province = usrAddressDetailList.get(0).getProvince();
            if (province.equalsIgnoreCase("DKI JAKARTA")) {
                score = score.add(BigDecimal.valueOf(6));
                log.info(order.getUuid() + "===============>province + 6");
            } else if (province.equalsIgnoreCase("JAWA BARAT")) {
                score = score.add(BigDecimal.valueOf(4));
                log.info(order.getUuid() + "===============>province + 4");
            } else if (province.equalsIgnoreCase("BANTEN")) {
                score = score.add(BigDecimal.valueOf(8));
                log.info(order.getUuid() + "===============>province + 8");
            } else if (province.equalsIgnoreCase("BALI")) {
                score = score.add(BigDecimal.valueOf(15));
                log.info(order.getUuid() + "===============>province + 15");
            }
        }


        /** ??????????? **/
        String firstContact = userService.getUserLinkManPhoneNumber(1, order.getUserUuid());
        String secondContact = userService.getUserLinkManPhoneNumber(2, order.getUserUuid());

        UserCallRecordsMongo search = new UserCallRecordsMongo();
        search.setUserUuid(order.getUserUuid());
        search.setOrderNo(order.getUuid());
        List<UserCallRecordsMongo> orderUserCallRecordsLists = this.userCallRecordsDal.find(search);
        Boolean firstContactBoolean = false;
        Boolean secondContactBoolean = false;
        if (!CollectionUtils.isEmpty(orderUserCallRecordsLists)) {
            UserCallRecordsMongo userCallRecordsMongo = orderUserCallRecordsLists.get(0);
            String dataStr = userCallRecordsMongo.getData();
            if (StringUtils.isNotEmpty(dataStr)) {
                List<UserCallRecordsData> userCallRecordsList = JsonUtil.toList(dataStr, UserCallRecordsData.class);
                if (!CollectionUtils.isEmpty(userCallRecordsList)) {
                    List<UserCallRecordsData> validRecords = userCallRecordsList.stream().filter(elem -> elem.getMsgDate() != null).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(validRecords)) {
                        for (UserCallRecordsData item : validRecords) {
                            String itemNumber = item.getNumber();
                            if(StringUtils.isEmpty(firstContact)||StringUtils.isEmpty(secondContact)){
                                continue;
                            }
                            if (StringUtils.isNotEmpty(CheakTeleUtils.telephoneNumberValid2(firstContact)) && StringUtils.isNotEmpty(CheakTeleUtils.telephoneNumberValid2(secondContact))
                                && StringUtils.isNotEmpty(CheakTeleUtils.telephoneNumberValid2(itemNumber))) {
                                if (CheakTeleUtils.telephoneNumberValid2(itemNumber).equals(firstContact)) {
                                    firstContactBoolean = true;
                                }
                                if (CheakTeleUtils.telephoneNumberValid2(itemNumber).equals(secondContact)) {
                                    secondContactBoolean = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!firstContactBoolean && !secondContactBoolean) {// ??????????
            score = score.subtract(BigDecimal.valueOf(7));
            log.info(order.getUuid() + "===============>Contact - 7");
        } else if (firstContactBoolean || secondContactBoolean) {// ????????????
            score = score.add(BigDecimal.valueOf(3));
            log.info(order.getUuid() + "===============>Contact + 3");
        } else if (firstContactBoolean && secondContactBoolean) {// ?2??????????
            score = score.add(BigDecimal.valueOf(15));
            log.info(order.getUuid() + "===============>Contact + 15");
        }


        /** ?30???????? **/
        Date before30Days = DateUtils.getDiffTime(order.getUpdateTime(), -30);
        SysAutoReviewRule sysAutoReviewRule4 = codeEntityMap.get(BlackListTypeEnum.SMS_REFUSE_COUNT.getMessage());
        UserMessagesMongo entity = new UserMessagesMongo();
        entity.setUserUuid(order.getUserUuid());
        entity.setOrderNo(order.getUuid());
        List<UserMessagesMongo> scanList = this.userMessagesDal.find(entity);
        if (!CollectionUtils.isEmpty(scanList)) {
            UserMessagesMongo userMessagesMongo = scanList.get(0);
            String data = userMessagesMongo.getData();
            if (StringUtils.isNotEmpty(data)) {
                List<ShortMessageData> shortMessageList = JsonUtil.toList(data, ShortMessageData.class);
                if (!CollectionUtils.isEmpty(shortMessageList)) {
                    //?30?
                    List<ShortMessageData> recent30SmsList = shortMessageList.stream()
                        .filter(elem -> !org.springframework.util.StringUtils.isEmpty(elem.getSmsBody())
                            && DateUtil.filterLimitDate(before30Days, elem.getMsgDate(), order.getUpdateTime()))
                        .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(recent30SmsList)) {
                        Long sms30Reject = recent30SmsList.stream()
                            .filter(elem -> RuleUtils.distinctContainsCount(sysAutoReviewRule4.getRuleData(),
                                Arrays.asList(elem.getSmsBody())) > 0).count();
                        if (sms30Reject == 0) {
                            score = score.add(BigDecimal.valueOf(10));
                            log.info(order.getUuid() + "===============>sms30Reject + 10");
                        } else if (sms30Reject >= 1 && sms30Reject <= 3) {
                            score = score.add(BigDecimal.valueOf(2));
                            log.info(order.getUuid() + "===============>sms30Reject + 2");
                        } else if (sms30Reject >= 4 && sms30Reject <= 10) {
                            score = score.subtract(BigDecimal.valueOf(5));
                            log.info(order.getUuid() + "===============>sms30Reject - 5");
                        } else if (sms30Reject > 10) {
                            score = score.subtract(BigDecimal.valueOf(20));
                            log.info(order.getUuid() + "===============>sms30Reject - 20");
                        }
                    }
                }
            }


        }


        /** 90?????????????? **/
        SysAutoReviewRule sysAutoReviewRule9 = codeEntityMap.get(BlackListTypeEnum.SMS_OVERDUE_COUNT_NINETY_DAY.getMessage());
        Date before90Days = DateUtils.getDiffTime(order.getUpdateTime(), -90);
        if (!CollectionUtils.isEmpty(scanList)) {
            UserMessagesMongo userMessagesMongo = scanList.get(0);
            String data = userMessagesMongo.getData();
            if (StringUtils.isNotEmpty(data)) {
                List<ShortMessageData> shortMessageList = JsonUtil.toList(data, ShortMessageData.class);
                if (!CollectionUtils.isEmpty(shortMessageList)) {
                    List<ShortMessageData> recent90SmsList = shortMessageList.stream()
                        .filter(elem -> !org.springframework.util.StringUtils.isEmpty(elem.getSmsBody())
                            && DateUtil.filterLimitDate(before90Days, elem.getMsgDate(), order.getUpdateTime())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(recent90SmsList)) {
                        Long sms90Reject = recent90SmsList.stream()
                            .filter(elem -> RuleUtils.distinctContainsCount(sysAutoReviewRule9.getRuleData(), Arrays.asList(elem.getSmsBody())) > 0).map(ShortMessageData::getPhoneNumber)
                            .distinct().count();
                        if (sms90Reject == 0) {
                            score = score.add(BigDecimal.valueOf(10));
                            log.info(order.getUuid() + "===============>sms90Reject + 10");
                        } else if (sms90Reject >= 1 && sms90Reject <= 3) {
                            score = score.subtract(BigDecimal.valueOf(2));
                            log.info(order.getUuid() + "===============>sms90Reject - 2");
                        } else if (sms90Reject >= 4 && sms90Reject <= 7) {
                            score = score.subtract(BigDecimal.valueOf(7));
                            log.info(order.getUuid() + "===============>sms90Reject - 7");
                        } else if (sms90Reject > 7) {
                            score = score.subtract(BigDecimal.valueOf(14));
                            log.info(order.getUuid() + "===============>sms90Reject - 14");
                        }
                    }
                }
            }
        }


        /** ????????? **/
        if (!CollectionUtils.isEmpty(orderUserCallRecordsLists)) {
            UserCallRecordsMongo userCallRecordsMongo = orderUserCallRecordsLists.get(0);
            String dataStr = userCallRecordsMongo.getData();
            if (StringUtils.isNotEmpty(dataStr)) {
                List<UserCallRecordsData> userCallRecordsList = JsonUtil.toList(dataStr, UserCallRecordsData.class);
                if (!CollectionUtils.isEmpty(userCallRecordsList)) {
                    List<UserCallRecordsData> validRecords = userCallRecordsList.stream()
                        .filter(elem -> elem.getMsgDate() != null).collect(
                            Collectors.toList());
                    if (!CollectionUtils.isEmpty(validRecords)) {
                        Optional<UserCallRecordsData> earliestMsg = validRecords.stream()
                            .min(Comparator.comparing(UserCallRecordsData::getMsgDate));
                        Long usedDay = DateUtil.getDiffDays(earliestMsg.get().getMsgDate(), order.getApplyTime());
                        if (usedDay == 0) {
                            score = score.subtract(BigDecimal.valueOf(7));
                            log.info(order.getUuid() + "===============>sms90Reject - 7");
                        } else if (usedDay >= 1 && usedDay <= 30) {
                            score = score.add(BigDecimal.valueOf(5));
                            log.info(order.getUuid() + "===============>sms90Reject + 5");
                        } else if (usedDay >= 31 && usedDay <= 90) {
                            score = score.add(BigDecimal.valueOf(9));
                            log.info(order.getUuid() + "===============>sms90Reject + 9");
                        } else if (usedDay >= 91 && usedDay <= 180) {
                            score = score.add(BigDecimal.valueOf(15));
                            log.info(order.getUuid() + "===============>sms90Reject + 15");
                        } else if (usedDay >= 181 && usedDay <= 365) {
                            score = score.add(BigDecimal.valueOf(20));
                            log.info(order.getUuid() + "===============>sms90Reject + 20");
                        } else if (usedDay > 365) {
                            score = score.add(BigDecimal.valueOf(30));
                            log.info(order.getUuid() + "===============>sms90Reject + 30");
                        }
                    }
                }
            }
        }


        /** ?30???????? NIGHT_CALL_RATE **/
        Date recent30Day = DateUtils.getDiffTime(order.getUpdateTime(), -30);// ??30?
        if (!CollectionUtils.isEmpty(orderUserCallRecordsLists)) {
            UserCallRecordsMongo userCallRecordsMongo = orderUserCallRecordsLists.get(0);
            String dataStr = userCallRecordsMongo.getData();
            if (StringUtils.isNotEmpty(dataStr)) {
                List<UserCallRecordsData> userCallRecordsList = JsonUtil.toList(dataStr, UserCallRecordsData.class);
                if (!CollectionUtils.isEmpty(userCallRecordsList)) {
                    List<UserCallRecordsData> validRecords = userCallRecordsList.stream()
                        .filter(elem -> elem.getMsgDate() != null).collect(
                            Collectors.toList());
                    if (!CollectionUtils.isEmpty(validRecords)) {
                        List<UserCallRecordsData> recent30RecordList = validRecords.stream().filter(
                            elem -> DateUtil.filterLimitDate(recent30Day, elem.getMsgDate(), order.getUpdateTime())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(recent30RecordList)) {
                            Long recent30AllCall = recent30RecordList.stream().filter(elem -> elem.getDuration() != null).count();
                            Long recent30NightCall = recent30RecordList.stream().filter(elem -> elem.isCallInEvening()).count();
                            Float nightCallRate = NumberUtils.division(recent30NightCall, recent30AllCall);
                            if (nightCallRate == 0) {
                                score = score.add(BigDecimal.valueOf(13));
                                log.info(order.getUuid() + "===============>nightCallRate + 13");
                            } else if (nightCallRate > 0 && nightCallRate <= 0.01) {
                                score = score.add(BigDecimal.valueOf(6));
                                log.info(order.getUuid() + "===============>nightCallRate + 6");
                            } else if (nightCallRate > 0.01 && nightCallRate <= 0.017) {
                                score = score.add(BigDecimal.valueOf(2));
                                log.info(order.getUuid() + "===============>nightCallRate + 2");
                            } else if (nightCallRate > 0.017 && nightCallRate <= 0.036) {
                                score = score.add(BigDecimal.valueOf(0));
                                log.info(order.getUuid() + "===============>nightCallRate + 0");
                            } else if (nightCallRate > 0.036 && nightCallRate <= 0.5) {
                                score = score.subtract(BigDecimal.valueOf(2));
                                log.info(order.getUuid() + "===============>nightCallRate - 2");
                            } else if (nightCallRate > 0.5) {
                                score = score.subtract(BigDecimal.valueOf(5));
                                log.info(order.getUuid() + "===============>nightCallRate - 5");
                            }
                        }
                    }
                }
            }
        }


        /** ?30???????? **/
        if (!CollectionUtils.isEmpty(orderUserCallRecordsLists)) {
            UserCallRecordsMongo userCallRecordsMongo = orderUserCallRecordsLists.get(0);
            String dataStr = userCallRecordsMongo.getData();
            if (StringUtils.isNotEmpty(dataStr)) {
                List<UserCallRecordsData> userCallRecordsList = JsonUtil.toList(dataStr, UserCallRecordsData.class);
                if (!CollectionUtils.isEmpty(userCallRecordsList)) {
                    List<UserCallRecordsData> validRecords = userCallRecordsList.stream()
                        .filter(elem -> elem.getMsgDate() != null).collect( Collectors.toList());
                    if (!CollectionUtils.isEmpty(validRecords)) {
                        List<UserCallRecordsData> recent30RecordList = validRecords.stream().filter(
                            elem -> DateUtil.filterLimitDate(recent30Day, elem.getMsgDate(), order.getUpdateTime())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(recent30RecordList)) {
                            Long recent30CallNoConnectCount = recent30RecordList.stream().
                                filter(elem -> elem.getDuration() != null && elem.getDuration() == 0).
                                count();
                            Float recent30CallRate = NumberUtils.division(recent30CallNoConnectCount, recent30RecordList.size());
                            if (recent30CallRate == 0) {
                                score = score.add(BigDecimal.valueOf(20));
                                log.info(order.getUuid() + "===============>recent30CallRate + 20");
                            } else if (recent30CallRate > 0 && recent30CallRate <= 0.029) {
                                score = score.add(BigDecimal.valueOf(13));
                                log.info(order.getUuid() + "===============>recent30CallRate + 13");
                            } else if (recent30CallRate > 0.029 && recent30CallRate <= 0.099) {
                                score = score.add(BigDecimal.valueOf(7));
                                log.info(order.getUuid() + "===============>recent30CallRate + 7");
                            } else if (recent30CallRate > 0.099 && recent30CallRate <= 0.3) {
                                score = score.subtract(BigDecimal.valueOf(2));
                                log.info(order.getUuid() + "===============>recent30CallRate -2");
                            } else if (recent30CallRate > 0.3) {
                                score = score.subtract(BigDecimal.valueOf(5));
                                log.info(order.getUuid() + "===============>recent30CallRate -5");
                            }
                        }
                    }
                }
            }
        }
        // ??????
        updateOrderScore(order.getUuid(), score.setScale(2, BigDecimal.ROUND_HALF_UP));
        return true;
    }

    /**
     * ??????
     *
     * @param uuid
     * @param score
     */
    private void updateOrderScore(String uuid, BigDecimal score) {
        OrdOrder order = new OrdOrder();
        order.setUuid(uuid);
        order.setDisabled(0);
        List<OrdOrder> ordOrders = ordDao.scan(order);
        if (!CollectionUtils.isEmpty(ordOrders)) {
            order = ordOrders.get(0);
            order.setScore(score);
            ordDao.update(order);
        }
    }
}
