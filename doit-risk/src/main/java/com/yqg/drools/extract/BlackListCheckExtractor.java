package com.yqg.drools.extract;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.MD5Util;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.BlackListUserCheckModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.DeviceService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.service.UsrBlackListService;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.service.user.service.UserDetailService;
import com.yqg.service.user.service.UserLinkManService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.entity.UsrBlackList;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BlackListCheckExtractor implements BaseExtractor<BlackListUserCheckModel> {

    @Autowired
    private UsrBlackListService usrBlackListService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UserLinkManService userLinkManService;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;



    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.BLACK_LIST_USER.equals(ruleSet);
    }


    @Override
    public Optional<BlackListUserCheckModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        long startTime = System.currentTimeMillis();
        try {

            BlackListUserCheckModel model = new BlackListUserCheckModel();
            UsrUser user = userService.getUserInfo(order.getUserUuid());
            OrdDeviceInfo ordDeviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());

            model.setIdCardNoInOverdue15BlackList(usrBlackListService.isIdCardNoInBlackUserCategoryN(user.getIdCardNo(),
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            log.info("cost part 01: {} ms.", (System.currentTimeMillis()-startTime));
            model.setIdCardNoInOverdue15BlackList(usrBlackListService.isMobileInBlackUserCategoryN(user.getMobileNumberDES(),
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            log.info("cost part 02: {} ms.", (System.currentTimeMillis()-startTime));
            model.setEmergencyTelInOverdue15BlackListEmergencyTel(usrBlackListService.isEmergencyTelAsEmergencyTelForBlackUserCategoryN(user,
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            log.info("cost part 03: {} ms.", (System.currentTimeMillis()-startTime));
            model.setBankcardNoInOverdue15BlackList(usrBlackListService.isBankCardInBlackUserCategoryN(user,
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            log.info("cost part 04: {} ms.", (System.currentTimeMillis()-startTime));
            if (ordDeviceInfo != null) {
                if(!StringUtils.isEmpty(ordDeviceInfo.getIMEI())){
                    model.setImeiInOverdue15BlackList(usrBlackListService.isImeiInBlackListForCategoryN(ordDeviceInfo.getIMEI(),
                            UsrBlackList.BlackUserCategory.OVERDUE15));
                    log.info("cost part 05: {} ms.", (System.currentTimeMillis()-startTime));
                    model.setImeiInOverdue7BlackList(usrBlackListService.isImeiInBlackListForCategoryN(ordDeviceInfo.getIMEI(),
                            UsrBlackList.BlackUserCategory.OVERDUE15));
                    log.info("cost part 06: {} ms.", (System.currentTimeMillis()-startTime));
                }
                if(!StringUtils.isEmpty(ordDeviceInfo.getDeviceId())){
                    //设备号是否在逾期30天及以上黑名单用户的设备中
                    model.setDeviceIdInOverdue30DaysUser(usrBlackListService.isDeviceIdInBlackListForCategoryN(ordDeviceInfo.getDeviceId(),
                            UsrBlackList.BlackUserCategory.OVERDUE30));
                    log.info("cost part 07: {} ms.", (System.currentTimeMillis()-startTime));
                }
            }
            log.info("cost part1: {} ms.", (System.currentTimeMillis()-startTime));
            Integer countOfEmergencyTelInOverdue15BlackList = usrBlackListService.countOfEmergencyTelForBlackListUserCategoryN(order.getUserUuid(),
                    UsrBlackList.BlackUserCategory.OVERDUE15);
            model.setEmergencyTelInOverdue15BlackList(countOfEmergencyTelInOverdue15BlackList != null && countOfEmergencyTelInOverdue15BlackList > 0);

            model.setMobileInOverdue15BlackListContacts(usrBlackListService.isMobileInContactForBlackUserCategoryN(user.getMobileNumberDES(),
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            model.setMobileInOverdue15BlackListRedis(usrBlackListService.isMobileInRedisBlacklist(user.getMobileNumberDES(),
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            model.setMobileInOverdue15BlackListShortMsg(usrBlackListService.isMobileInShortMessageForBlackUserCategoryN(user.getMobileNumberDES(),
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            model.setContactInOverdue15BlackList(false);
            model.setEmergencyContactInOverdue15BlackList(false);
            model.setMobileIsOverdue15BlackListEmergencyTel(usrBlackListService.isMobileAsEmergencyTelForBlackUserCategoryN(user.getMobileNumberDES(),
                    UsrBlackList.BlackUserCategory.OVERDUE15));
            model.setContactInOverdue15Count(0);
            model.setEmergenctContactInOverdue15Count(0);
            log.info("cost part2: {} ms.", (System.currentTimeMillis()-startTime));
            model.setMobileIsFraudUserEmergencyTel(usrBlackListService.isMobileAsEmergencyTelForFraudUser(user.getMobileNumberDES()));


            model.setMobileInFraudUser(usrBlackListService.isMobileInFraudUser(user.getMobileNumberDES()));
            model.setIdCardNoInFraudUser(usrBlackListService.isIdCardNoInFraudUser(user.getIdCardNo()));

            model.setHitFraudUserInfo(usrBlackListService.hitFraudUserInfo(user, order));

            model.setSmsContactOverdue15DaysCount(0);
            log.info("cost part3: {} ms.", (System.currentTimeMillis()-startTime));
            //7 days overdue
            model.setEmergencyContactInOverdue7BlackList(false);
            model.setIdCardNoInOverdue7BlackList(usrBlackListService.isIdCardNoInBlackUserCategoryN(user.getIdCardNo(),
                    UsrBlackList.BlackUserCategory.OVERDUE7));
            model.setMobileInOverdue7BlackList(usrBlackListService.isMobileInBlackUserCategoryN(order.getUserUuid(),
                    UsrBlackList.BlackUserCategory.OVERDUE7));
            model.setBankcardInOverdue7BlackList(usrBlackListService.isBankCardInBlackUserCategoryN(user,
                    UsrBlackList.BlackUserCategory.OVERDUE7));
            model.setEmergencyTelInOverdue7BlackListEmergencyTel(usrBlackListService.isEmergencyTelAsEmergencyTelForBlackUserCategoryN(user,
                    UsrBlackList.BlackUserCategory.OVERDUE7));
            model.setMobileInOverdue7BlackListEmergencyTel(usrBlackListService.isMobileAsEmergencyTelForBlackUserCategoryN(user.getMobileNumberDES(),
                    UsrBlackList.BlackUserCategory.OVERDUE7));
            Integer countOfEmergencyTelInOverdue7BlackList = usrBlackListService.countOfEmergencyTelForBlackListUserCategoryN(order.getUserUuid(),
                    UsrBlackList.BlackUserCategory.OVERDUE7);
            model.setEmergencyTelInOverdue7BlackList(countOfEmergencyTelInOverdue7BlackList != null && countOfEmergencyTelInOverdue7BlackList > 0);

            model.setHitSensitiveUserInfo(usrBlackListService.hitIdCardOrMobileForCategoryN(user,UsrBlackList.BlackUserCategory.SENSITIVE));

            model.setHitCollectorBlackUserInfo(usrBlackListService.hitIdCardOrMobileForCategoryN(user,UsrBlackList.BlackUserCategory.COLLECTOR_BLACK_LIST));

            model.setHitComplaintUserInfo(usrBlackListService.hitIdCardOrMobileForCategoryN(user,UsrBlackList.BlackUserCategory.COMPLAINT));

            log.info("cost part4: {} ms.", (System.currentTimeMillis()-startTime));
            //


            String whatsapp = userDetailService.getWhatsAppAccount(user.getUuid(), false);
            if (!StringUtils.isEmpty(whatsapp)) {
                String whatsappDes = DESUtils.encrypt(whatsapp);
                model.setWhatsappInOverdue7BlackList(usrBlackListService.isMobileInBlackUserCategoryN(whatsappDes, UsrBlackList.BlackUserCategory.OVERDUE7));
                model.setWhatsappInOverdue7BlackListEmergencyTel(usrBlackListService.isMobileAsEmergencyTelForBlackUserCategoryN(whatsappDes, UsrBlackList.BlackUserCategory.OVERDUE7));

                model.setWhatsappInOverdue7BlackListRedis(usrBlackListService.isMobileInRedisBlacklist(whatsappDes,
                        UsrBlackList.BlackUserCategory.OVERDUE7));
                model.setWhatsappInOverdue7BlackListContact(usrBlackListService.isMobileInContactForBlackUserCategoryN(whatsappDes, UsrBlackList.BlackUserCategory.OVERDUE7));
                model.setWhatsappInOverdue7BlackListSms(usrBlackListService.isMobileInShortMessageForBlackUserCategoryN(whatsappDes, UsrBlackList.BlackUserCategory.OVERDUE7));
            }


            List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(user.getUuid());
            List<String> emergencyTels = linkmanList.stream().map(elem->{
                String mobile = elem.getContactsMobile();
                if(StringUtils.isEmpty(mobile)){
                    return null;
                }else{
                    return CheakTeleUtils.telephoneNumberValid2(mobile);
                }
            }).distinct().filter(item->!StringUtils.isEmpty(item)).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(emergencyTels)){
                model.setEmergencyTelInFraudUserEmergencyTel(emergencyTelsInFraudUserEmergencyTel(emergencyTels));
                model.setEmergencyTelInRedisBlacklist(emergencyTelsInFraudUserRedis(emergencyTels));
                model.setEmergencyTelInFraudUserContact(emergencyTelsInFraudUserContact(emergencyTels));
                model.setEmergencyTelInFraudUserSms(emergencyTelsInFraudUserSms(emergencyTels));
                model.setEmergencyTelInFraudUserWhatsapp(emergencyTelsInFraudUserWhatsapp(emergencyTels));
            }

            String companyTel = userDetailService.getCompanyPhone(user.getUuid());
            if(StringUtils.isNotEmpty(companyTel)){
                model.setCompanyTelInOverdue7BlackList(usrBlackListService.isCompanyTelInBlackUserCategoryN(companyTel,
                        UsrBlackList.BlackUserCategory.OVERDUE7));
                model.setCompanyTelInFraudBlackList(usrBlackListService.isCompanyTelInBlackUserCategoryN(companyTel,
                        UsrBlackList.BlackUserCategory.FRAUD));
            }

            String companyAddress = userDetailService.getAddressUpperCaseWithSeparator(user.getUuid(), UsrAddressEnum.COMPANY);
            if(StringUtils.isNotEmpty(companyAddress)){
                String md5CompanyAddress = MD5Util.md5LowerCase(companyAddress);
                model.setCompanyAddressInOverdue7BlackList(usrBlackListService.isCompanyAddressInBlackUserCategoryN(md5CompanyAddress,
                        UsrBlackList.BlackUserCategory.OVERDUE7));
                model.setCompanyAddressInFraudBlackList(usrBlackListService.isCompanyAddressInBlackUserCategoryN(md5CompanyAddress,
                        UsrBlackList.BlackUserCategory.FRAUD));
            }

            String homeAddress = userDetailService.getAddressUpperCaseWithSeparator(user.getUuid(), UsrAddressEnum.HOME);
            if(StringUtils.isNotEmpty(homeAddress)){
                String md5HomeAddress = MD5Util.md5LowerCase(homeAddress);
                model.setHomeAddressInOverdue7BlackList(usrBlackListService.isHomeAddressInBlackUserCategoryN(md5HomeAddress,
                        UsrBlackList.BlackUserCategory.OVERDUE7));
                model.setHomeAddressInFraudBlackList(usrBlackListService.isHomeAddressInBlackUserCategoryN(md5HomeAddress,
                        UsrBlackList.BlackUserCategory.FRAUD));
            }


            String ownerMobile = DESUtils.decrypt(user.getMobileNumberDES());

//            model.setMobileIsEmergencyTelForOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(Arrays.asList(ownerMobile), 1));
//
//
//            model.setEmergencyTelIsEmergencyTelForOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 1));
//            model.setEmergencyTelIsOverdue7UserWith1th(usrBlackListService.mobileIsOverdue7UserWithBorrowingCountN(emergencyTels, 1));
//
//            model.setMobileIsEmergencyTelForOverdue7UserWith2th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(Arrays.asList(ownerMobile), 2));
//            model.setEmergencyTelIsEmergencyTelForOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 2));
//
//
//            model.setEmergencyTelIsOverdue7UserWith2th(usrBlackListService.mobileIsOverdue7UserWithBorrowingCountN(emergencyTels, 2));
//
//            Boolean isInThirdOrder = usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 3);
//            Boolean isInForthOrder = usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 4);
//
//            model.setEmergencyTelIsEmergencyTelForOverdue7UserWith3Or4th((isInThirdOrder != null && isInForthOrder) || (isInForthOrder != null && isInForthOrder));

            model.setMobileIsEmergencyTelForUnSettledOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(Arrays.asList(ownerMobile), 1, false));
            model.setEmergencyTelIsUnSettledOverdue7UserWith1th(usrBlackListService.mobileIsOverdue7UserWithBorrowingCountN(emergencyTels,1,false));
            model.setEmergencyTelIsEmergencyTelForUnSettledOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 1, false));
            model.setEmergencyTelIsUnSettledOverdue7UserWith2th(usrBlackListService.mobileIsOverdue7UserWithBorrowingCountN(emergencyTels,2,false));
            model.setEmergencyTelIsEmergencyTelForUnSettledOverdue7UserWith2th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 2, false));
            model.setMobileIsEmergencyTelForUnSettledOverdue7UserWith2th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(Arrays.asList(ownerMobile), 2, false));
            Boolean isInUnSettledThirdOrder = usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 3,false);
            Boolean isInUnSettledForthOrder = usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 4,false);
            model.setEmergencyTelIsEmergencyTelForUnSettledOverdue7UserWith3Or4th(isInUnSettledThirdOrder||isInUnSettledForthOrder);

            model.setMobileIsEmergencyTelForSettledOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(Arrays.asList(ownerMobile), 1, true));
            model.setEmergencyTelIsSettledOverdue7UserWith1th(usrBlackListService.mobileIsOverdue7UserWithBorrowingCountN(emergencyTels,1,true));
            model.setEmergencyTelIsEmergencyTelForSettledOverdue7UserWith1th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 1, true));
            model.setEmergencyTelIsSettledOverdue7UserWith2th(usrBlackListService.mobileIsOverdue7UserWithBorrowingCountN(emergencyTels,2,true));
            model.setEmergencyTelIsEmergencyTelForSettledOverdue7UserWith2th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 2, true));
            model.setMobileIsEmergencyTelForSettledOverdue7UserWith2th(usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(Arrays.asList(ownerMobile), 2, true));
            Boolean isInSettledThirdOrder = usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 3,true);
            Boolean isInSettledForthOrder = usrBlackListService.mobileIsLinkmanForOverdue7UserWithBorrowingCountN(emergencyTels, 4,true);
            model.setEmergencyTelIsEmergencyTelForSettledOverdue7UserWith3Or4th(isInSettledThirdOrder||isInSettledForthOrder);

            return Optional.of(model);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("the cost of extract blackList model is: {} ms", (endTime - startTime));
        }
    }

    private Boolean emergencyTelsInFraudUserEmergencyTel(List<String> emergencyTels) {
        if (CollectionUtils.isEmpty(emergencyTels)) {
            return false;
        }
        for (String emergencyTel : emergencyTels) {
            String emergencyTelDes = DESUtils.encrypt(emergencyTel);
            boolean result = usrBlackListService.isMobileAsEmergencyTelForBlackUserCategoryN(emergencyTelDes, UsrBlackList.BlackUserCategory.FRAUD);
            if (result) {
                return true;
            }
        }
        return false;
    }
    private Boolean emergencyTelsInFraudUserRedis(List<String> emergencyTels) {
        if (CollectionUtils.isEmpty(emergencyTels)) {
            return false;
        }
        for (String emergencyTel : emergencyTels) {
            String emergencyTelDes = DESUtils.encrypt(emergencyTel);
            boolean result = usrBlackListService.isMobileInRedisBlacklist(emergencyTelDes, UsrBlackList.BlackUserCategory.FRAUD);
            if (result) {
                return true;
            }
        }
        return false;
    }
    private Boolean emergencyTelsInFraudUserContact(List<String> emergencyTels) {
        if (CollectionUtils.isEmpty(emergencyTels)) {
            return false;
        }
        for (String emergencyTel : emergencyTels) {
            String emergencyTelDes = DESUtils.encrypt(emergencyTel);
            boolean result = usrBlackListService.isMobileInContactForBlackUserCategoryN(emergencyTelDes, UsrBlackList.BlackUserCategory.FRAUD);
            if (result) {
                return true;
            }
        }
        return false;
    }
    private Boolean emergencyTelsInFraudUserSms(List<String> emergencyTels) {
        if (CollectionUtils.isEmpty(emergencyTels)) {
            return false;
        }
        for (String emergencyTel : emergencyTels) {
            String emergencyTelDes = DESUtils.encrypt(emergencyTel);
            boolean result = usrBlackListService.isMobileInShortMessageForBlackUserCategoryN(emergencyTelDes, UsrBlackList.BlackUserCategory.FRAUD);
            if (result) {
                return true;
            }
        }
        return false;
    }
    private Boolean emergencyTelsInFraudUserWhatsapp(List<String> emergencyTels) {
        if (CollectionUtils.isEmpty(emergencyTels)) {
            return false;
        }
        for (String emergencyTel : emergencyTels) {
            String emergencyTelDes = DESUtils.encrypt(emergencyTel);
            boolean result = usrBlackListService.isMobileInBlackUserWhatsappCategoryN(emergencyTelDes, UsrBlackList.BlackUserCategory.FRAUD);
            if (result) {
                return true;
            }
        }
        return false;
    }
}
