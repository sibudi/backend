package com.yqg.drools.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.FraudUserOrderInfoDao;
import com.yqg.risk.entity.FraudUserInfo;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrBlackList;
import com.yqg.user.entity.UsrBlackList.BlackUserCategory;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/***
 * 用户黑名单信息
 */

@Service
@Slf4j
public class UsrBlackListService {

    @Autowired
    private UsrBlackListDao usrBlackListDao;

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private FraudUserOrderInfoDao fraudUserOrderInfoDao;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UserRiskService userRiskService;

    /***
     * 手机号是"某一类黑名单用户"的紧急联系人电话
     * @param mobileDes
     * @Param category
     * @return
     */
    public Boolean isMobileAsEmergencyTelForBlackUserCategoryN(String mobileDes, BlackUserCategory category) {
        if (StringUtils.isEmpty(mobileDes)) {
            return null;
        }
        String mobileNumber = DESUtils.decrypt(mobileDes);
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.mobileIsEmergencyTelForBlackListUser(userService.getAllTypesMobile(mobileNumber), types);
        return count != null && count > 0;
    }

    /***
     * 判定身份证号是否命中某类黑名单用户的身份证号
     * @param idCardNo
     * @return
     */
    public Boolean isIdCardNoInBlackUserCategoryN(String idCardNo, BlackUserCategory category) {
        if (StringUtils.isEmpty(idCardNo)) {
            return false;
        }

        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.idCardNoInBlackList(idCardNo, types);
        return count != null && count > 0;
    }


    /***
     * 判定手机号是否命中某类黑名单用户的手机号
     * @param mobileDesc
     * @return
     */
    public Boolean isMobileInBlackUserCategoryN(String mobileDesc, BlackUserCategory category) {
        if (StringUtils.isEmpty(mobileDesc)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.countOfMobilesInBlackList(Arrays.asList(mobileDesc), types);
        return count != null && count > 0;
    }

    /***
     * 判定手机号是否命中某类黑名单用户的whatsapp
     * @param whatsapp
     * @return
     */
    public Boolean isMobileInBlackUserWhatsappCategoryN(String whatsapp, BlackUserCategory category) {
        if (StringUtils.isEmpty(whatsapp)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.countOfWhatsappInBlackList(Arrays.asList(whatsapp), types);
        return count != null && count > 0;
    }

    /***
     * 判定公司电话是否命中某类黑名单用户的companyTel
     * @param companyTel
     * @return
     */
    public Boolean isCompanyTelInBlackUserCategoryN(String companyTel, BlackUserCategory category) {
        if (StringUtils.isEmpty(companyTel)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.companyTelInBlackList(companyTel, types);
        return count != null && count > 0;
    }
    /***
     * 判定公司地址是否命中某类黑名单用户的公司地址
     * @param companyAddress
     * @return
     */
    public Boolean isCompanyAddressInBlackUserCategoryN(String companyAddress, BlackUserCategory category) {
        if (StringUtils.isEmpty(companyAddress)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.companyAddressInBlackList(companyAddress, types);
        return count != null && count > 0;
    }
    /***
     * 判定居住地址是否命中某类黑名单用户的居住地址
     * @param homeAddress
     * @return
     */
    public Boolean isHomeAddressInBlackUserCategoryN(String homeAddress, BlackUserCategory category) {
        if (StringUtils.isEmpty(homeAddress)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.homeAddressInBlackList(homeAddress, types);
        return count != null && count > 0;
    }

    /***
     * 銀行卡是某类用户的银行卡号
     * @param user
     * @return
     */
    public Boolean isBankCardInBlackUserCategoryN(UsrUser user, BlackUserCategory category) {
        if (StringUtils.isEmpty(user)) {
            return false;
        }

        List<UsrBank> bankList = usrBankDao.getUserBankList(user.getUuid());
        if (CollectionUtils.isEmpty(bankList)) {
            return false;
        }
        Optional<UsrBank> bank = bankList.stream().filter(elem -> elem.getIsRecent() != null && elem.getIsRecent() == 1).findFirst();
        if (!bank.isPresent()) {
            return false;
        }
        if(StringUtils.isEmpty(bank.get().getBankNumberNo())){
            return false;
        }

        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer count = usrBlackListDao.bankcardNumberInBlackList(bank.get().getBankNumberNo(), types);
        return count != null && count > 0;
    }


    /***
     * 紧急联系人是某类用户的紧急联系人
     * @param user
     * @return
     */
    public Boolean isEmergencyTelAsEmergencyTelForBlackUserCategoryN(UsrUser user, BlackUserCategory category) {
        if (user == null) {
            return false;
        }
        String firstContact = userService.getUserLinkManPhoneNumber(1, user.getUuid());
        String secondContact = userService.getUserLinkManPhoneNumber(2, user.getUuid());
        boolean firstContactIsEmergencyTelForOverdueUsers = com.yqg.common.utils.StringUtils.isNotEmpty(firstContact) && this
                .isMobileAsEmergencyTelForBlackUserCategoryN(DESUtils
                        .encrypt(firstContact), category) ? true : false;

        boolean secondContactIsEmergencyTelForOverdueUsers = com.yqg.common.utils.StringUtils.isNotEmpty(secondContact) && this
                .isMobileAsEmergencyTelForBlackUserCategoryN(DESUtils
                        .encrypt(secondContact), category) ? true : false;
        return firstContactIsEmergencyTelForOverdueUsers || secondContactIsEmergencyTelForOverdueUsers;
    }


    private List<Integer> filterTypesOfMongoData(BlackUserCategory category) {
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        return types;
    }


    /****
     * 手机号码是某类用户的通讯录中号码
     * @param mobileDesc  des加密后
     * @param category
     * @return
     */
    public Boolean isMobileInContactForBlackUserCategoryN(String mobileDesc, BlackUserCategory category) {
        String mobile = DESUtils.decrypt(mobileDesc);
        String key = "";
        switch (category){
            case OVERDUE7:
                key = RedisContants.RISK_BLACKLIST_OVERDUE7_CONTACT;
                break;
            case FRAUD:
                key = RedisContants.RISK_BLACKLIST_FRAUD_CONTACT;
                break;
            default:
                key = RedisContants.RISK_BLACKLIST_OVERDUE15_CONTACT;
                break;
        }
        return redisClient.sisMember(key, mobile);
    }

    /****
     * The mobile phone number is the number in the call record of a certain type of user
     * @param mobileDesc  After des encryption
     * @param category
     * @return
     */
    public Boolean isMobileInRedisBlacklist(String mobileDesc, BlackUserCategory category) {
        String mobile = DESUtils.decrypt(mobileDesc);
        String key = "";
        switch (category){
            case OVERDUE7:
                key = RedisContants.RISK_BLACKLIST_OVERDUE7_CALL_RECORD;
                break;
            case FRAUD:
                key = RedisContants.RISK_BLACKLIST_FRAUD_CALL_RECORD;
                break;
            default:
                key = RedisContants.RISK_BLACKLIST_OVERDUE15_CALL_RECORD;
                break;
        }
        return redisClient.sisMember(key, mobile);
    }

    /****
     * 手机号码是某类用户的短信记录中号码
     * @param mobileDesc  des加密后
     * @param category
     * @return
     */
    public Boolean isMobileInShortMessageForBlackUserCategoryN(String mobileDesc, BlackUserCategory category) {
        String mobile = DESUtils.decrypt(mobileDesc);
        String key = "";
        switch (category){
            case OVERDUE7:
                key = RedisContants.RISK_BLACKLIST_OVERDUE7_SHORT_MESSAGE;
                break;
            case FRAUD:
                key = RedisContants.RISK_BLACKLIST_FRAUD_SHORT_MESSAGE;
                break;
            default:
                key = RedisContants.RISK_BLACKLIST_OVERDUE15_SHORT_MESSAGE;
                break;
        }
        return redisClient.sisMember(key, mobile);
    }

    /***
     * 手机号是逾期15天及以上用户的紧急联系人电话
     * @param mobileDes
     * @return
     */
    public boolean isMobileAsEmergencyTelForOverdue15DaysUser(String mobileDes) {
        if (StringUtils.isEmpty(mobileDes)) {
            return false;
        }
        String mobileNumber = DESUtils.decrypt(mobileDes);
        List<Integer> types = Arrays.asList(UsrBlackList.UsrBlackTypeEnum.UANG2_OVERDUE_30.getCode(), UsrBlackList.UsrBlackTypeEnum
                .DOIT_OVERDUE_30.getCode(),UsrBlackList.UsrBlackTypeEnum.UANG2_OVERDUE_15.getCode(), UsrBlackList.UsrBlackTypeEnum
                .DOIT_OVERDUE_15.getCode());
        Integer count = usrBlackListDao.mobileIsEmergencyTelForBlackListUser(userService.getAllTypesMobile(mobileNumber),types);
        return count != null && count > 0;
    }


    /***
     * 紧急联系人号码在逾期30天及以上黑名单用户中
     * @param userUuid
     * @return
     */
    public boolean isEmergencyTelInOverdue30DaysUser(String userUuid) {


        List<String> phones = getLinkManPhonesDes(userUuid);
        if(CollectionUtils.isEmpty(phones)){
            return false;
        }
        List<Integer> types = Arrays.asList(UsrBlackList.UsrBlackTypeEnum.UANG2_OVERDUE_30.getCode(), UsrBlackList.UsrBlackTypeEnum
                .DOIT_OVERDUE_30.getCode());
        Integer affectCount = usrBlackListDao.countOfMobilesInBlackList(phones,types);
        return affectCount != null && affectCount > 0;
    }

    /***
     * 紧急联系人号码属于CategoryN"的号码的次数
     * eg: 紧急联系人号码属于逾期15天用户的号码的次数
     * @param userUuid
     * @return
     */
    public Integer countOfEmergencyTelForBlackListUserCategoryN(String userUuid, BlackUserCategory category) {

        List<String> phones = getLinkManPhonesDes(userUuid);
        if (CollectionUtils.isEmpty(phones)) {
            return 0;
        }

        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        return usrBlackListDao.countOfMobilesInBlackList(phones, types);
    }


    /***
     * deviceId在某一个类黑名单用户的deviceId列表中
     * @param deviceId
     * @return
     */
    public boolean isDeviceIdInBlackListForCategoryN(String deviceId, BlackUserCategory category) {
        if (StringUtils.isEmpty(deviceId)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer affectRow = usrBlackListDao.deviceIdInBlackList(deviceId, types);
        return affectRow != null && affectRow > 0;
    }

    /***
     * imei在某一个类黑名单用户的imei列表中
     * @param imei
     * @return
     */
    public boolean isImeiInBlackListForCategoryN(String imei, BlackUserCategory category) {
        if (StringUtils.isEmpty(imei)) {
            return false;
        }
        List<Integer> types = UsrBlackList.getBlackTypesByCategory(category);
        Integer affectRow = usrBlackListDao.imeiInBlackList(imei, types);
        return affectRow != null && affectRow > 0;
    }

    /***
     * 手机号是欺诈用户的紧急联系人
     * @param mobileDes
     * @return
     */
    public Boolean isMobileAsEmergencyTelForFraudUser(String mobileDes) {
        if (StringUtils.isEmpty(mobileDes)) {
            return false;
        }
        String mobileNumber = DESUtils.decrypt(mobileDes);
        if (StringUtils.isEmpty(mobileNumber)) {
            return false;
        }
        List<Integer> types = Arrays.asList(UsrBlackList.UsrBlackTypeEnum.FRAUDULENT_USER.getCode());
        Integer count = usrBlackListDao.mobileIsEmergencyTelForBlackListUser(userService.getAllTypesMobile(mobileNumber), types);
        return count != null && count > 0;
    }

    /***
     * imei属于欺诈用户
     * @param imei
     * @return
     */
    public Boolean isImeiInFraudUser(String imei) {
        if (StringUtils.isEmpty(imei)) {
            return false;
        }
        List<Integer> types = Arrays.asList(UsrBlackList.UsrBlackTypeEnum.FRAUDULENT_USER.getCode());
        Integer count = usrBlackListDao.imeiInBlackList(imei, types);
        return count != null && count > 0;
    }


    /***
     * 手机号属于欺诈用户
     * @param mobileDes
     * @return
     */
    public Boolean isMobileInFraudUser(String mobileDes) {
        if (StringUtils.isEmpty(mobileDes)) {
            return false;
        }
        List<Integer> types = Arrays.asList(UsrBlackList.UsrBlackTypeEnum.FRAUDULENT_USER.getCode());
        Integer count = usrBlackListDao.countOfMobilesInBlackList(Arrays.asList(mobileDes), types);
        return count != null && count > 0;
    }

    /***
     * 身份证号属于欺诈用户
     * @param idCardNo
     * @return
     */
    public Boolean isIdCardNoInFraudUser(String idCardNo) {
        if (StringUtils.isEmpty(idCardNo)) {
            return false;
        }
        List<Integer> types = Arrays.asList(UsrBlackList.UsrBlackTypeEnum.FRAUDULENT_USER.getCode());
        Integer count = usrBlackListDao.idCardNoInBlackList(idCardNo, types);
        return count != null && count > 0;
    }


    public void addFraudUserBlackList(String userUuid) {
        usrBlackListDao.addFraudUser(userUuid, "");
    }

    private List<String> getLinkManPhonesDes(String userUuid) {
        List<UsrLinkManInfo> linkManInfos = usrLinkManDao.getUsrLinkManWithUserUuid(userUuid);
        if (CollectionUtils.isEmpty(linkManInfos)) {
            return new ArrayList<>();
        }

        //获取第一第二联系人号码并进行加密
        List<String> phones = linkManInfos.stream().filter(elem -> elem.getSequence() != 3)
                .map(elem -> {
                    String tmp = CheakTeleUtils.telephoneNumberValid2(elem.getContactsMobile());
                    if (StringUtils.isEmpty(tmp)) {
                        return null;
                    }
                    return DESUtils.encrypt(tmp);
                }).filter(elem -> !StringUtils.isEmpty(elem))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(phones)) {
            return new ArrayList<>();
        }
        return phones;
    }

    /***
     * 检查用户是否命中欺诈用户信息
     * @param currentUser
     * @param currentOrder
     * @return
     */
    public Boolean hitFraudUserInfo(UsrUser currentUser, OrdOrder currentOrder) {

        FraudUserInfo targetUser = userRiskService.getOrderFraudCompareData(currentUser, currentOrder);
        log.info("the target user need to compare is: {}", JsonUtils.serialize(targetUser));
        //查询欺诈用户订单信息
        List<FraudUserInfo> fraudList = fraudUserOrderInfoDao.getAllFraudUserOrderInfo();
        if (CollectionUtils.isEmpty(fraudList)) {
            return false;
        }
        Optional<FraudUserInfo> matched = fraudList.stream().filter(elem -> elem.matchAttributeGroupA(targetUser) && elem.matchAttributeGroupB
                (targetUser)).findFirst();
        if (matched.isPresent()) {
            log.info("the matched user: {}", JsonUtils.serialize(matched.get()));
            return true;
        }
        return false;
    }

    /***
     * 命中敏感人员的身份证号或者手机号
     * @param user
     * @return
     */
    public Boolean hitIdCardOrMobileForCategoryN(UsrUser user,BlackUserCategory  category) {
        //命中身份证号
        boolean hitIdCard = isIdCardNoInBlackUserCategoryN(user.getIdCardNo(), category);
        //命中手机号
        boolean hitMobile = isMobileInBlackUserCategoryN(user.getMobileNumberDES(), category);
        return hitIdCard || hitMobile;
    }


    /***
     * 欺诈用户基础数据保存
     * [每次新增欺诈用户需要通过该方式统计订单信息]
     */
    public void addFraudBaseInfo() {

        List<OrdOrder> orders = usrBlackListDao.getFraudOrders();

        if (CollectionUtils.isEmpty(orders)) {
            return;
        }
        log.warn("total count of fraud userList... {} ", orders.size());
        for (OrdOrder order : orders) {
            if (StringUtils.isEmpty(MDC.get("X-Request-Id"))) {
                MDC.put("X-Request-Id", order.getUuid());
            }
            try {
                UsrUser user = userService.getUserInfo(order.getUserUuid());
                FraudUserInfo info = userRiskService.getOrderFraudCompareData(user, order);
                info.setOrderNo(order.getUuid());
                info.setUserUuid(order.getUserUuid());
                fraudUserOrderInfoDao.insert(info);
            } catch (Exception e) {
                log.error("add fraudBaseInfo error", e);
            }
            MDC.remove("X-Request-Id");
        }
        log.warn("finished to add FraudBaseInfo");
    }


    public Boolean mobileIsLinkmanForOverdue7UserWithBorrowingCountN(List<String> formatMobileList, int borrowingCount, boolean checkSettledOrder) {
        if (CollectionUtils.isEmpty(formatMobileList)) {
            return false;
        }
        if (checkSettledOrder) {
            Integer selectedCount = usrBlackListDao.mobileIsLinkmanForSettledOverdue7UserWithBorrowingCountN(formatMobileList, borrowingCount);
            return selectedCount != null && selectedCount > 0;
        } else {
            Integer selectedCount = usrBlackListDao.mobileIsLinkmanForUnSettledOverdue7UserWithBorrowingCountN(formatMobileList, borrowingCount);
            return selectedCount != null && selectedCount > 0;
        }

    }

    public Boolean mobileIsOverdue7UserWithBorrowingCountN(List<String> formatMobileList, int borrowingCount, boolean checkSettledOrder) {
        if (CollectionUtils.isEmpty(formatMobileList)) {
            return false;
        }

        List<String> mobileDesList = formatMobileList.stream().map(elem -> DESUtils.encrypt(elem)).collect(Collectors.toList());
        if(checkSettledOrder){
            Integer selectedCount = usrBlackListDao.mobileIsSettledOverdue7UserWithBorrowingCountN(mobileDesList, borrowingCount);
            return selectedCount != null && selectedCount > 0;
        }else{
            Integer selectedCount = usrBlackListDao.mobileIsUnSettledOverdue7UserWithBorrowingCountN(mobileDesList, borrowingCount);
            return selectedCount != null && selectedCount > 0;
        }

    }


}
