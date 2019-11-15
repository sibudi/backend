
package com.yqg.service.user.service;

import com.vdurmont.emoji.EmojiParser;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.entity.FraudUserInfo;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.externalChannel.enums.ExternalChannelEnum;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.order.OrdDeviceInfoService;
import com.yqg.service.order.OrderCheckService;
import com.yqg.service.risk.service.OrderModelScoreService;
import com.yqg.service.system.service.SysAutoReviewRuleService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.advance.AdvanceService;
import com.yqg.service.third.advance.response.BlacklistCheckResponse;
import com.yqg.service.third.advance.response.MultiPlatformResponse;
import com.yqg.service.user.response.FrequentOrderUserCallRecordResponse;
import com.yqg.service.user.response.OrderUserCallRecordResponse;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.dao.UsrProductRecordDao;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRiskService {


    private static Logger logger = LoggerFactory.getLogger(UserRiskService.class);

    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;

    @Autowired
    private UsrDao usrDao;
    @Autowired
    private OrdBlackDao ordBlackDao;
    @Autowired
    private SysAutoReviewRuleService sysAutoReviewRuleService;

    @Autowired
    private UserCallRecordsDal userCallRecordsDal;

    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;
    @Autowired
    private OrdDeviceInfoService ordDeviceInfoService;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private ExternalChannelDataService externalChannelDataService;
    @Autowired
    private OrderCheckService orderCheckService;

    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UsrProductRecordDao usrProductRecordDao;
    @Autowired
    private UsrService usrService;


    /***
     * 订单是否被仅仅联系人拒绝过
     * @param orderNo
     * @return
     */
    public Integer getEmergencyAutoCallRejectedTimes(String orderNo) {
        Integer rejectTimes = 0;
        OrdBlack searchEntity = new OrdBlack();
        searchEntity.setOrderNo(orderNo);
        searchEntity.setRuleHitNo("25-" + BlackListTypeEnum.MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT.getMessage());
        List<OrdBlack> dbResultList = ordBlackDao.scan(searchEntity);
        if (!CollectionUtils.isEmpty(dbResultList)) {
            rejectTimes = dbResultList.size();
            return rejectTimes;
        }
        searchEntity.setRuleHitNo("25-" + BlackListTypeEnum.AUTO_CALL_REJECT_LINKMAN_VALID_COUNT.getMessage());
        dbResultList = ordBlackDao.scan(searchEntity);
        if (!CollectionUtils.isEmpty(dbResultList)) {
            rejectTimes = dbResultList.size();
        }
        return rejectTimes;
    }

    public List<String> getUppercaseRelativeWords() {
        SysAutoReviewRule rule = sysAutoReviewRuleService.getRuleConfigByName(BlackListTypeEnum.CONTACT_RELATIVE_COUNT.getMessage());
        if (rule == null) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(rule.getRuleData().toUpperCase().split("#"));
        }
    }

    /***
     * 判定用户是否命中人脸识别比对分规则
     * @param orderNo
     */
    public boolean isUserHitRuleForVerifyScore(String orderNo) {
        List<OrdRiskRecord> riskRecordList = orderRiskRecordRepository.getFaceVerifyScoreRuleResult(orderNo);
        if (CollectionUtils.isEmpty(riskRecordList)) {
            return false;
        }

        return riskRecordList.stream().filter(elem -> {
            try {
                SysAutoReviewRule rule = sysAutoReviewRuleService.getRuleConfigByName(BlackListTypeEnum.CONTACT_RELATIVE_COUNT.getMessage());
                if (rule == null) {
                    return false;
                }
                if ("null".equals(elem.getRuleRealValue()) || StringUtils.isEmpty(elem.getRuleRealValue())) {
                    return true;
                }
                BigDecimal realValue = new BigDecimal(elem.getRuleRealValue());
                BigDecimal thresholdValue = new BigDecimal(rule.getRuleValue());
                if (realValue.compareTo(thresholdValue) < 0) {
                    //真实值小于阈值--》命中规则
                    return true;
                }
                return false;
            } catch (Exception e) {
                //出现异常可能是没有值之类的，当作命中规则
                log.error("判定是否命中人脸比对规则出错", e);
                return true;
            }
        }).count() > 0;
    }

    /*****
     * 判断订单仅仅实名失败并且有保险卡
     * @Param orderNo
     * @return
     */
    public boolean onlyRealNameVerifyFailedWithInsuranceCard(String orderNo) {
        Integer count = ordBlackDao.advanceVerifyFailedWithSpecialRemark(orderNo);
        if (count == null || count <= 0) {
            return false;
        }
        return hasSpecifiedRule(orderNo, BlackListTypeEnum.HAS_INSURANCE_CARD);
    }

    /*****
     * 判断订单仅仅实名失败并且有家庭卡
     * @Param orderNo
     * @return
     */
    public boolean onlyRealNameVerifyFailedWithFamilyCard(String orderNo) {
        Integer count = ordBlackDao.advanceVerifyFailedWithSpecialRemark(orderNo);
        if(count==null||count<=0){
            return false;
        }
        return hasSpecifiedRule(orderNo,BlackListTypeEnum.HAS_FAMILY_CARD);
    }


    /***
     * 统计实名失败但是有保险卡放款的单量
     * @return
     */
    public Integer onlyRealNameVerifyFailedWithInsuranceCardCount(){
        Integer count = ordBlackDao.totalAdvanceVerifyFailedWithInsuranceCard();
        return count;
    }

    private boolean hasSpecifiedRule(String orderNo, BlackListTypeEnum ruleName) {
        OrdRiskRecord record = orderRiskRecordRepository.getRuleResultByRuleName(ruleName.getMessage(), orderNo);
        if (record == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(record.getRuleRealValue())) {
            return true;
        }
        return false;
    }


    /***
     * 姓名和税卡相同并且之前已经实名通过
     * @param realName
     * @param taxNumber
     * @Param userUuid
     * @return
     */
    public boolean hasSameRealNameAndTaxNumber(String realName, String taxNumber,String userUuid) {
        if(StringUtils.isEmpty(realName)||StringUtils.isEmpty(taxNumber)){
            return false;
        }
        Map<String, String> map = new HashMap<>();
        map.put("account", taxNumber);
        List<String> userList = usrDao.userWithSameRealNameAndTaxNumber(realName,JsonUtils.serialize(map),userUuid);
        if(CollectionUtils.isEmpty(userList)){
            return false;
        }
        Integer affectRow = usrDao.existsSuccessOrder(userList);
        return affectRow != null && affectRow > 0;
    }


    /***
     * 返回某一类型失败数据
     * @param remark
     * @return
     */
    public Integer getABTestOrderIssuedCount(String remark) {
        Integer count = ordBlackDao.totalIssuedABTestOrders(remark);
        return count;
    }

    /***
     *  获得免核数量
     * @param remark
     * @return
     */
    public Integer getABTestNotManualReviewCount(String remark) {
        Integer count = ordBlackDao.getABTestNotManualReviewCount(remark);
        return count;
    }

    /***
     * 订单是降额产品
     * @param orderNo
     * @return
     */
    public boolean histSpecifiedProductWithDecreasedCreditLimit(String orderNo) {
       return orderCheckService.histSpecifiedProductWithDecreasedCreditLimit(orderNo);
    }

    public boolean isSuitableFor100RMBProduct(OrdOrder order) {
        if ("3".equals(order.getOrderType())) {
            return false;
        }
        boolean switchOpen = is100RMBProductSwitchOpen(order.getUuid());
        //boolean isNotCashCashOrder = !isCashCashOrder(order);
        //CashCash 放开
        boolean isNotCashCashOrder = true;

        boolean isNotCheetahOrder = !isCheetahOrder(order);
        //非40w and 非80w产品
        boolean isAmountSuitable = !order.getAmountApply().equals(new BigDecimal("400000.00"))
                && !order.getAmountApply().equals(new BigDecimal("800000.00"));
        return switchOpen && isNotCashCashOrder && isAmountSuitable && isNotCheetahOrder;
    }

    /**
     * 是否是cashcash的订单
     *
     * @param order
     * @return
     */
    public boolean isCashCashOrder(OrdOrder order) {
        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderNoByRealOrderNoRelation(order.getUuid());
        if (externalOrderRelation != null) {
            return externalOrderRelation.getChannel() != null && ExternalChannelEnum.CASHCASH.name().equals(externalOrderRelation.getChannel());
        }
        return false;
    }
    /**
     * 是否是cheetah的订单
     *
     * @param order
     * @return
     */
    public boolean isCheetahOrder(OrdOrder order) {
        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderNoByRealOrderNoRelation(order.getUuid());
        if (externalOrderRelation != null) {
            return externalOrderRelation.getChannel() != null && ExternalChannelEnum.CHEETAH.name().equals(externalOrderRelation.getChannel());
        }
        return false;
    }


    private boolean is100RMBProductSwitchOpen(String orderNo) {

        Optional<OrdDeviceInfo> ordDeviceInfo = ordDeviceInfoService.getDeviceInfoByOrderNo(orderNo);

        if (!ordDeviceInfo.isPresent()) {
            log.info("the device info is empty, orderNo: {}", orderNo);
            return false;
        }
        String switchOpen;
        if ("iOS".equals(ordDeviceInfo.get().getDeviceType())) {
            //iOS 因版本审核问题一直没上
            switchOpen = sysParamService.getSysParamValue(SysParamContants.RISK_100RMB_PRODUCT_SWITCH_IOS);
        } else {
            switchOpen = sysParamService.getSysParamValue(SysParamContants.RISK_100RMB_PRODUCT_SWITCH_ANDROID);
        }
        return "true".equals(switchOpen);
    }



    /***
     * 取和欺诈用户比较的数据
     * @param user
     * @param order
     * @return
     */
    public FraudUserInfo getOrderFraudCompareData(UsrUser user, OrdOrder order){
        FraudUserInfo targetUser = new FraudUserInfo();
        targetUser.setRealName(user.getRealName());
        targetUser.setAge(user.getAge());

        //查询订单地址、家庭地址，公司地址，用户明细、设备信息
        if (user.getUserRole() != null && user.getUserRole() == 2) {
            UsrWorkDetail usrWorkDetail = getUsrWorkDetail(order.getUserUuid());
            if (usrWorkDetail != null) {
                targetUser.setCompanyTel(usrWorkDetail.getCompanyPhone());
                targetUser.setCompanyName(usrWorkDetail.getCompanyName());
                targetUser.setMotherName(usrWorkDetail.getMotherName());
            }
        } else if (user.getUserRole() != null && user.getUserRole() == 3) {
            UsrHouseWifeDetail houseWifeDetail = getUsrHouseWifeDetail(order.getUserUuid());
            if (houseWifeDetail != null) {
                targetUser.setCompanyTel(houseWifeDetail.getCompanyPhone());
                targetUser.setCompanyName(houseWifeDetail.getCompanyName());
                targetUser.setMotherName(houseWifeDetail.getMotherName());
            }
        }
        List<UsrAddressDetail> addressList = getUserAddressDetailList(order.getUserUuid());
        if(!CollectionUtils.isEmpty(addressList)){
            Optional<UsrAddressDetail> companyAddress = addressList.stream().filter(elem->elem.getAddressType()==UsrAddressEnum.COMPANY.getType()).findFirst();
            Optional<UsrAddressDetail> homeAddress = addressList.stream().filter(elem->elem.getAddressType()==UsrAddressEnum.HOME.getType()).findFirst();
            Optional<UsrAddressDetail> orderAddress =  addressList.stream().filter(elem->elem.getAddressType()==UsrAddressEnum.ORDER.getType()).findFirst();
            if (companyAddress.isPresent()) {
                targetUser.setCompanyAddress(companyAddress.get().getProvince() + "#" + companyAddress.get().getCity() + "#" + companyAddress.get().getBigDirect()
                        + "#" + companyAddress.get().getSmallDirect());
            }
            if (homeAddress.isPresent()) {
                targetUser.setLiveAddress(homeAddress.get().getProvince() + "#" + homeAddress.get().getCity() + "#" + homeAddress.get().getBigDirect()
                        + "#" + homeAddress.get().getSmallDirect());
            }
            if (orderAddress.isPresent()) {
                targetUser.setOrderAddressDetail(orderAddress.get().getDetailed());
            }
        }

        List<UsrLinkManInfo> usrLinkManList = userBackupLinkmanService.getLinkManInfo(order.getUserUuid());
        if (!CollectionUtils.isEmpty(usrLinkManList)) {
            Optional<UsrLinkManInfo> firstLinkMan = usrLinkManList.stream().filter(elem -> elem.getDisabled() == 0 && elem.getSequence() == 1).findFirst();
            if (firstLinkMan.isPresent()) {
                targetUser.setEmergencyTel(firstLinkMan.get().getContactsMobile());
                //targetUser.setEmergencyName(firstLinkMan.get().getContactsName());
            }

            Optional<UsrLinkManInfo> secondLinkMan = usrLinkManList.stream().filter(elem -> elem.getDisabled() == 0 && elem.getSequence() == 2).findFirst();
            if (secondLinkMan.isPresent()) {
                targetUser.setEmergencyTel2(secondLinkMan.get().getContactsMobile());
                // targetUser.setEmergencyName2(secondLinkMan.get().getContactsName());
            }
        }

        Optional<OrdDeviceInfo> ordDeviceInfo  = ordDeviceInfoService.getDeviceInfoByOrderNo(order.getUuid());
        if (ordDeviceInfo.isPresent()) {
            targetUser.setIpAddress(ordDeviceInfo.get().getIPAddress());
            targetUser.setPictureCount(ordDeviceInfo.get().getPictureNumber());
        }
        try {
            List<FrequentOrderUserCallRecordResponse> commonContactUsers = this.frequentOrderUserCallRecordMongo1(order.getUuid());
            if (!CollectionUtils.isEmpty(commonContactUsers)) {
                targetUser.setFirstCommonContactTel(commonContactUsers.get(0).getMobile());
                // targetUser.setFirstCommonContactName(commonContactUsers.get(0).getRealName());
                if (commonContactUsers.size() > 1) {
                    targetUser.setSecondCommonContactTel(commonContactUsers.get(1).getMobile());
                    //targetUser.setSecondCommonContactName(commonContactUsers.get(1).getRealName());
                }
            }
        } catch (Exception e) {
            log.error("get common contact user error", e);
        }
        return targetUser;
    }

    private UsrWorkDetail getUsrWorkDetail(String userUuid){
        return userDetailService.getUserWorkDetail(userUuid);
    }
    private UsrHouseWifeDetail getUsrHouseWifeDetail(String userUuid){
       return userDetailService.getUserHouseWifeDetail(userUuid);
    }

    private List<UsrAddressDetail> getUserAddressDetailList(String userUuid) {
        return userDetailService.getUserAddressList(userUuid);
    }



    /**
     * 获得经常使用的两个联系人，若小于两个使用紧急联系人  （祥才专用)
     *
     * @param orderNo
     * @return
     */
    public List<FrequentOrderUserCallRecordResponse> frequentOrderUserCallRecordMongo1(String orderNo) throws ServiceExceptionSpec {
        if (StringUtils.isEmpty(orderNo)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        UserCallRecordsMongo search = new UserCallRecordsMongo();
        search.setOrderNo(orderNo);
        search.setDisabled(0);
        List<UserCallRecordsMongo> resultMongo = this.userCallRecordsDal.find(search);
        //全部数据集合
        List<FrequentOrderUserCallRecordResponse> response = new ArrayList<>();
        if (!CollectionUtils.isEmpty(resultMongo)) {
            UserCallRecordsMongo dataResult = resultMongo.get(0);
            //获得电话记录
            String callRecordStr = dataResult.getData();
            if (!StringUtils.isEmpty(callRecordStr)) {
                List<LinkedHashMap> callRecordList = JsonUtils.deserialize(callRecordStr, List.class);
                //全部有效数据集合
                List<OrderUserCallRecordResponse> allCallRecord = new ArrayList<>();
                //封装数据到list中
                if (!CollectionUtils.isEmpty(callRecordList)) {
                    for (LinkedHashMap callRecord : callRecordList) {
                        OrderUserCallRecordResponse orderUser = new OrderUserCallRecordResponse();

                        if (StringUtils.isEmpty(String.valueOf(callRecord.get("number")))
                                || "0".equals(callRecord.get("duration"))
                                || "Unreported contacts".equals(String.valueOf(callRecord.get("name")))
                                || "未备注联系人".equals(String.valueOf(callRecord.get("name")))
                                || judgeOver30Days(String.valueOf(callRecord.get("date")),dataResult.getCreateTime())) {
                            continue;
                        }
                        String duration = String.valueOf(callRecord.get("duration"));
                        orderUser.setMobile(String.valueOf(callRecord.get("number")));
                        orderUser.setRealName(String.valueOf(callRecord.get("name")));
                        orderUser.setCallTime(String.valueOf(callRecord.get("date")));
                        orderUser.setDuration("null".equals(duration) || StringUtils.isEmpty(duration) ? 0 : Integer.parseInt(duration));
                        allCallRecord.add(orderUser);
                    }
                    //将电话号码相同的时长相加，并且其他字段取第一条
                    List<OrderUserCallRecordResponse> result = new ArrayList<>();
                    //用于去重判断
                    List<String> phones = new ArrayList<>();
                    for (OrderUserCallRecordResponse callRecord : allCallRecord) {
                        if (phones.contains(callRecord.getMobile())) {
                            continue;
                        }
                        OrderUserCallRecordResponse recordResponse = new OrderUserCallRecordResponse();
                        //这里复制最新的数据
                        BeanUtils.copyProperties(callRecord, recordResponse);
                        //判断重复加上当前手机号
                        phones.add(callRecord.getMobile());
                        Integer count = 0;
                        for (OrderUserCallRecordResponse record : allCallRecord) {
                            if (callRecord.getMobile().equals(record.getMobile())) {
                                count += record.getDuration();
                            }
                        }
                        recordResponse.setDuration(count);
                        result.add(recordResponse);
                    }
                    //电话号码只能是 628 +628 08 开头的长度>9 <=12
                    result = result.stream().filter(elem -> {
                        String phoneStr = elem.getMobile();
                        return phoneStr.length() > 9 && phoneStr.length() <= 15 &&
                                (phoneStr.startsWith("628") || phoneStr.startsWith("+628") || phoneStr.startsWith("08"));
                    }).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(result)) {
                        this.sortOrderUserCallRecordByDuration(result);
                        FrequentOrderUserCallRecordResponse response1 = new FrequentOrderUserCallRecordResponse();
                        String realName = filterEmoji(result.get(0).getRealName());
                        response1.setRealName(realName);
                        response1.setMobile(result.get(0).getMobile());
                        response.add(response1);

                        if (!CollectionUtils.isEmpty(result) && result.size() > 1) {
                            //处理用户姓名一样，手机号码不同的情况（08和+628和628是同一个电话号码)
                            if (!StringUtils.isEmpty(result.get(1).getRealName()) &&
                                    result.get(1).getRealName().equals(result.get(0).getRealName())
                                    && judgePhoneNum(result.get(0).getMobile(), result.get(1).getMobile())) {
                                if (result.size() > 2) {
                                    FrequentOrderUserCallRecordResponse response2 = new FrequentOrderUserCallRecordResponse();
                                    String realName1 = filterEmoji(result.get(2).getRealName());
                                    response2.setRealName(realName1);
                                    response2.setMobile(result.get(2).getMobile());
                                    response.add(response2);
                                }
                            } else {
                                FrequentOrderUserCallRecordResponse response2 = new FrequentOrderUserCallRecordResponse();
                                String realName2 = filterEmoji(result.get(1).getRealName());
                                response2.setRealName(realName2);
                                response2.setMobile(result.get(1).getMobile());
                                response.add(response2);
                            }

                        }

                    }
                }

            }
        }
        logger.info("通话记录中的联系人：" + response.size());

        for (FrequentOrderUserCallRecordResponse res : response) {
            String mobile = res.getMobile();
            if (!StringUtils.isEmpty(mobile)) {
                if (mobile.startsWith("62")) {
                    res.setMobile(mobile.replaceFirst("62", "0"));
                } else if (mobile.startsWith("+62")) {
                    res.setMobile(mobile.replaceFirst("\\+62", "0"));
                }
            }
        }
        return response;
    }

    /**
     * 判断日期是否超过30天
     *
     * @param date
     * @param compareDate
     * @return
     */
    private boolean judgeOver30Days(String date,Date compareDate) {
        if (StringUtils.isEmpty(date)||compareDate==null) {
            return false;
        }
        if("null".equals(date)){
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(compareDate);
        calendar.add(Calendar.MONTH, -1);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return calendar.getTime().compareTo(sf.parse(date)) > 0 ?
                    true : false;
        } catch (ParseException e) {
            log.error("parse error with date: " + date, e);
        }
        return false;
    }

    private void sortOrderUserCallRecordByDuration(List<OrderUserCallRecordResponse> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        Collections.sort(result, new Comparator<OrderUserCallRecordResponse>() {
            @Override
            public int compare(OrderUserCallRecordResponse ord1, OrderUserCallRecordResponse ord2) {
                return 0 - ord1.getDuration().compareTo(ord2.getDuration());
            }
        });
    }

    /**
     * 过滤表情
     */
    private String filterEmoji(String text) {
        if (!StringUtils.isEmpty(text)) {
            return EmojiParser.removeAllEmojis(text);
        }
        return "";
    }

    /**
     * 判断两个电话号码，是否相同 08，628，+628
     * @param mobile1
     * @param mobile2
     * @return
     */
    private boolean judgePhoneNum(String mobile1, String mobile2) {

        mobile1 = mobile1.replaceAll(" ", "").replaceAll("-", "");
        mobile2 = mobile2.replaceAll(" ", "").replaceAll("-", "");
        if (mobile1.startsWith("08")) {
            mobile1 = mobile1.substring(2);
        }
        if (mobile1.startsWith("628")) {
            mobile1 = mobile1.substring(3);
        }
        if (mobile1.startsWith("+628")) {
            mobile1 = mobile1.substring(4);
        }

        if (mobile2.startsWith("08")) {
            mobile2 = mobile2.substring(2);
        }
        if (mobile2.startsWith("628")) {
            mobile2 = mobile2.substring(3);
        }
        if (mobile2.startsWith("+628")) {
            mobile2 = mobile2.substring(4);
        }

        return mobile1.equals(mobile2);
    }

    /***
     * 返回某一类型失败数据
     * @param remark
     * @return
     */
    public Integer getAutoReviewPassABTestOrderCount(String remark) {
        Integer count = ordBlackDao.totalAutoReviewPassABTestOrders(remark);
        return count;
    }


    public void decreaseUserLoanLimit(OrdOrder order, Integer toProductLevel, String ruleName) {
        try {
            UsrUser dbUser = usrService.getUserByUuid(order.getUserUuid());
            if (dbUser == null) {
                return;
            }
            Integer oldProductLevel = dbUser.getProductLevel();
            dbUser.setProductLevel(toProductLevel);
            usrService.updateUser(dbUser);
            UsrProductRecord record = new UsrProductRecord();
            record.setUserUuid(order.getUserUuid());
            record.setOrderNo(order.getUuid());
            record.setLastProductLevel(oldProductLevel);
            record.setCurrentProductLevel(toProductLevel);
            record.setRuleName(ruleName);
            usrProductRecordDao.insert(record);
        } catch (Exception e) {
            log.error("decrease loan limit error, userUuid: " + order.getUserUuid(), e);
        }
    }

}