
package com.yqg.drools.extract;

import com.github.pagehelper.StringUtil;
import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.CardIdUtils;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.YiTuData;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.model.RUserInfo.IdentityVerifyResult;
import com.yqg.drools.model.RUserInfo.IziPhoneAgeResult;
import com.yqg.drools.model.RUserInfo.IziPhoneVerifyResult;
import com.yqg.drools.model.RUserInfo.IziPhoneVerifyResult.PhoneVerifyStatusEnum;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserService;
import com.yqg.drools.service.YiTuVerificationService;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdThirdDataService;
import com.yqg.service.third.advance.AdvanceService;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.IziWhatsAppService;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.user.service.*;
import com.yqg.third.entity.IziWhatsAppDetailEntity;
import com.yqg.user.dao.UsrFaceVerifyResultDao;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class IdentityExtractor implements BaseExtractor<RUserInfo> {


    @Autowired
    private UserService userService;

    @Autowired
    private OrdThirdDataService ordThirdDataService;

    @Autowired
    private YiTuVerificationService yiTuVerificationService;


    @Autowired
    private UsrCertificationService usrCertificationService;
    @Autowired
    private IziService iziService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UserVerifyResultService userVerifyResultService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private UsrBankService usrBankService;
    @Autowired
    private UsrFaceVerifyResultDao usrFaceVerifyResultDao;
    @Autowired
    private IziWhatsAppService iziWhatsAppService;
    @Autowired
    private AdvanceService advanceService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.USER_IDENTITY.equals(ruleSet);
    }

    @Override
    public Optional<RUserInfo> extractModel(OrdOrder order, KeyConstant keyConstant) {

        UsrUser user = userService.getUserInfo(order.getUserUuid());

        RUserInfo userInfo = new RUserInfo();
        userInfo.setIdCard(user.getIdCardNo());
        userInfo.setSex(user.getSex());

        if (StringUtils.isNotEmpty(user.getIdCardNo())) {
            userInfo.setAge(CardIdUtils.getAgeByIdCard(user.getIdCardNo().trim()));
        }


        if(advanceService.isAdvanceSwitchOn()){
            //advance实名认证
            IdentityVerifyResult identityVerifyResult = yiTuVerificationService.verifyIdentity(user.getRealName(), user.getIdCardNo(), order);
            userInfo.setAdvanceVerifyResult(identityVerifyResult);
        }

        userInfo.setUserRole(user.getUserRole());

        UsrAddressDetail homeAddress = userService.getUserAddressDetailByType(order.getUserUuid(),
                UsrAddressEnum.HOME);
        if (homeAddress != null && StringUtils.isNotEmpty(homeAddress.getCity())) {
            userInfo.setHomeAddress(homeAddress.getCity());
            userInfo.setHomeAddressNotBelongToJarkat(
                    !isAddressMatch(keyConstant.getJarkatAddressWords(), userInfo.getHomeAddress()));
        }

        UsrAddressDetail schoolAddress = userService.getUserAddressDetailByType(order.getUserUuid(),
                UsrAddressEnum.SCHOOL);
        if (schoolAddress != null && StringUtils.isNotEmpty(schoolAddress.getCity())) {
            userInfo.setSchoolAddress(schoolAddress.getCity());
            userInfo.setSchoolAddressNotBelongToJarkat(
                    !isAddressMatch(keyConstant.getJarkatAddressWords(), userInfo.getSchoolAddress()));
        }

        UsrAddressDetail companyAddress = userService
                .getUserAddressDetailByType(order.getUserUuid(),
                        UsrAddressEnum.COMPANY);
        if (companyAddress != null && StringUtils.isNotEmpty(companyAddress.getCity())) {
            userInfo.setCompanyAddress(companyAddress.getCity());
            userInfo.setCompanyAddressNotBelongToJarkat(
                    !isAddressMatch(keyConstant.getJarkatAddressWords(), userInfo.getCompanyAddress()));
            userInfo.setCompanyAddressNotBelongToJarkatNormal(
                    !isAddressMatch(keyConstant.getJarkatAddressWordsNormal(), userInfo.getCompanyAddress()));
            userInfo.setCompanyAddressNotBelongToJarkatIOS(
                    !isAddressMatch(keyConstant.getJarkatAddressWords(), userInfo.getCompanyAddress()));
        }

        UsrAddressDetail orderAddress = userService.getUserAddressDetailByType(order.getUserUuid(),
                UsrAddressEnum.ORDER);
        userInfo.setOrderSmallDirectIsNull(!(orderAddress!=null && StringUtils.isNotEmpty(orderAddress.getSmallDirect())));

        if (orderAddress != null && StringUtils.isNotEmpty(orderAddress.getCity())) {
            userInfo.setOrderAddress(orderAddress.getCity());
            userInfo.setOrderAddressNotBelongToJarkat(
                    !isAddressMatch(keyConstant.getJarkatAddressWords(), userInfo.getOrderAddress()));
        }

        //same address
        Boolean existsSameAddressForHome = false;
        if (homeAddress != null && StringUtils.isNotEmpty(homeAddress.getSmallDirect()) && StringUtils.isNotEmpty(homeAddress.getDetailed())) {
            existsSameAddressForHome = userService.existsSameSmallDirectAndDetail(user.getUuid(), homeAddress.getSmallDirect(),
                    homeAddress.getDetailed(), UsrAddressEnum.HOME);
        }

        Boolean existsSameAddressForWork = false;
        if (companyAddress != null && StringUtils.isNotEmpty(companyAddress.getSmallDirect()) && StringUtils.isNotEmpty(companyAddress.getDetailed())) {
            existsSameAddressForWork = userService.existsSameSmallDirectAndDetail(user.getUuid(), companyAddress.getSmallDirect(),
                    companyAddress.getDetailed(), UsrAddressEnum.COMPANY);
        }
        Boolean existsSameAddressForOrder = false;
        if (orderAddress != null && StringUtils.isNotEmpty(orderAddress.getSmallDirect()) && StringUtils.isNotEmpty(orderAddress.getDetailed())) {
            existsSameAddressForOrder = userService.existsSameSmallDirectAndDetail(user.getUuid(), orderAddress.getSmallDirect(),
                    orderAddress.getDetailed(), UsrAddressEnum.ORDER);
        }
        userInfo.setHomeAddressExists(existsSameAddressForHome);
        userInfo.setWorkAddressExists(existsSameAddressForWork);
        userInfo.setOrderAddressExists(existsSameAddressForOrder);


        if (user.getUserRole() != null && user.getUserRole() == 2) {
            UsrWorkDetail usrWorkDetail = userService.getUserWorkDetail(order.getUserUuid());
            if (usrWorkDetail == null) {
                log.warn("user work detail is empty orderNo: {}", order.getUuid());
            } else {
                userInfo.setIsCompanyTelphoneNotInJarkat(
                        !isTelNumberMatch(keyConstant.getJarkatTelNumbers(),
                                usrWorkDetail.getCompanyPhone()));
                userInfo.setChildrenAmount(usrWorkDetail.getChildrenAmount());
                userInfo.setAcademic(usrWorkDetail.getAcademic());
                userInfo.setMaritalStatus(usrWorkDetail.getMaritalStatus());
                if (StringUtils.isNotEmpty(usrWorkDetail.getMonthlyIncome())) {
                    userInfo.setMonthlyIncome(RuleUtils.formatIncome(usrWorkDetail.getMonthlyIncome()));
                }

                userInfo.setReligionName(usrWorkDetail.getReligion());
                userInfo.setPositionName(usrWorkDetail.getPositionName());
                //set 高危职业
                if (StringUtils.isNotEmpty(usrWorkDetail.getPositionName())) {
                    userInfo.setHitOverDuePositionMan(
                            isAddressMatch(keyConstant.getOverDuePositionMan(), usrWorkDetail.getPositionName()));
                    userInfo.setHitOverDuePositionFeMen(
                            isAddressMatch(keyConstant.getOverDuePositionFeMen(), usrWorkDetail.getPositionName()));
                }
                userInfo.setDependentBusiness(usrWorkDetail.getDependentBusiness());
                userInfo.setCompanyName(usrWorkDetail.getCompanyName());
                userInfo.setCompanyTel(usrWorkDetail.getCompanyPhone());
                userInfo.setBirthday(RuleUtils.formatBirthday(usrWorkDetail.getBirthday()));
            }
        }
        if (user.getUserRole() != null && user.getUserRole() == 3) {
            UsrHouseWifeDetail usrHouseWifeDetail = userService.getUserHouseWifeDetail(order.getUserUuid());
            if (usrHouseWifeDetail == null) {
                log.warn("user house wife detail is empty orderNo: {}", order.getUuid());
            } else {
                userInfo.setIsCompanyTelphoneNotInJarkat(
                        !isTelNumberMatch(keyConstant.getJarkatTelNumbers(),
                                usrHouseWifeDetail.getCompanyPhone()));
                userInfo.setChildrenAmount(usrHouseWifeDetail.getChildrenAmount());
                userInfo.setAcademic(usrHouseWifeDetail.getAcademic());
                userInfo.setMaritalStatus(usrHouseWifeDetail.getMaritalStatus());
                if (StringUtils.isNotEmpty(usrHouseWifeDetail.getMouthIncome())) {
                    userInfo.setMonthlyIncome(RuleUtils.formatIncome(usrHouseWifeDetail.getMouthIncome()));
                }
                userInfo.setReligionName(usrHouseWifeDetail.getReligion());
                // userInfo.setPositionName(usrHouseWifeDetail.getPositionName());
                userInfo.setCompanyName(usrHouseWifeDetail.getCompanyName());
                userInfo.setCompanyTel(usrHouseWifeDetail.getCompanyPhone());
                userInfo.setBirthday(RuleUtils.formatBirthday(usrHouseWifeDetail.getBirthday()));

            }
        }

        if (user.getUserRole() != null && user.getUserRole() == 1) {
            UsrStudentDetail usrStudentDetail = userService
                    .getUserStudentDetail(order.getUserUuid());
            if (usrStudentDetail == null) {
                log.warn("user student detail is empty orderNo: {}", order.getUuid());
            } else {
                userInfo.setAcademic(usrStudentDetail.getAcademic());
                userInfo.setBirthday(RuleUtils.formatBirthday(usrStudentDetail.getBirthday()));
            }

        }

        //依图分数
        OrderThirdDataMongo thirdDataMongo = ordThirdDataService
                .getThridDataByUserUuid(user.getUuid(), ThirdDataTypeEnum.YITU_DATA);
        if (thirdDataMongo != null && StringUtils.isNotEmpty(thirdDataMongo.getData())) {
            YiTuData yiTuData = JsonUtil.toObject(thirdDataMongo.getData(), YiTuData.class);
            if (StringUtils.isNotEmpty(yiTuData.getPairVerifySimilarity())) {
                userInfo.setYituScore(new BigDecimal(yiTuData.getPairVerifySimilarity()));
            }
        }


        if (order.getThirdType() != null && 1 == order.getThirdType()) {
            //cashcash 用户
            userInfo.setThirdType(order.getThirdType());
            UsrFaceVerifyResult searchResult = usrFaceVerifyResultDao.getLatestResult(user.getUuid());
            if (searchResult != null) {
                userInfo.setFacePlusPlusScore(searchResult.getScore());
            }
        }

        userInfo.setHasDriverLicense(
                userService.getAttachmentInfo(UsrAttachmentEnum.SIM, order.getUserUuid()) != null);

        /**
         *  combinationRule.drl里面用到的两个规则。
         *      居住地址:userHomeAddress
         *      公司地址:userCompanyAddress
         */
        UsrAddressDetail userHomeAddress = userService
                .getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.HOME);// 居住地址
        // 公司地址
        UsrAddressDetail userCompanyAddress =
                (userInfo != null && userInfo.getUserRole() == 2) ? userService
                        .getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.COMPANY)
                        : null;
        if (userHomeAddress != null && StringUtils.isNotEmpty(userHomeAddress.getProvince())) {
            userInfo.setHomeAddrProvice(userHomeAddress.getProvince());
            //set 高风险地址
            userInfo.setHitHomeProviceMan(
                    isAddressMatch(keyConstant.getHomeProviceMan(), userHomeAddress.getProvince()));
            userInfo.setHitHomeProviceFeMen(
                    isAddressMatch(keyConstant.getHomeProviceFeMen(), userHomeAddress.getProvince()));

            userInfo.setHitHomeProviceMan150(
                    isAddressMatch(keyConstant.getHomeProviceMan150(), userHomeAddress.getProvince()));
            userInfo.setHitHomeProviceFeMen150(
                    isAddressMatch(keyConstant.getHomeProviceFeMen150(), userHomeAddress.getProvince()));
            userInfo.setHitHomeProviceMan80(
                    isAddressMatch(keyConstant.getHomeProviceMan80(), userHomeAddress.getProvince()));
        }
        if (userHomeAddress != null && StringUtils.isNotEmpty(userHomeAddress.getCity())) {
            userInfo.setHomeAddrCity(userHomeAddress.getCity());
        }
        if (userCompanyAddress != null && StringUtils
                .isNotEmpty(userCompanyAddress.getProvince())) {
            userInfo.setCompanyAddrProvice(userCompanyAddress.getProvince());
        }
        if (userCompanyAddress != null && StringUtils.isNotEmpty(userCompanyAddress.getCity())) {
            userInfo.setCompanyAddrCity(userCompanyAddress.getCity());
        }


        userInfo.setMobileAsEmergencyTelCount(userService.getEmergencyTelCountWithMobiles(user.getMobileNumberDES()));

        userInfo.setHasInsuranceCard(userService.getAttachmentInfo(UsrAttachmentEnum.INSURANCE_CARD, order.getUserUuid()) != null);

        userInfo.setHasFamilyCard(userService.getAttachmentInfo(UsrAttachmentEnum.KK, order.getUserUuid()) != null);

        userInfo.setHasPayroll(userService.getAttachmentInfo(UsrAttachmentEnum.PAYROLL, order.getUserUuid()) != null);
        userInfo.setHasCreditCard(userService.getAttachmentInfo(UsrAttachmentEnum.CREDIT_CARD, order.getUserUuid()) != null);

        userInfo.setHasTaxNumber(!StringUtils.isEmpty(usrCertificationService.getTaxNumber(order.getUserUuid())));

        String firstUserIdForFirstLinkmanExists = userService.getTheFirstUserWithSameFirstLinkman(order.getUserUuid());
        String firstUserIdForEmailExits = userService.getFirstEmailExistsUser(user);
        userInfo.setFirstLinkmanExists(StringUtils.isNotEmpty(firstUserIdForFirstLinkmanExists));
        userInfo.setEmailExists(StringUtils.isNotEmpty(firstUserIdForEmailExits));

        userInfo.setFirstEmailExistsRelatedUserSettled(userService.isUserFirstOrderSettledWith600(firstUserIdForFirstLinkmanExists));
        userInfo.setFirstEmailExistsRelatedUserSettled(userService.isUserFirstOrderSettledWith600(firstUserIdForEmailExits));


        String firstSameLinkmanUser = userService.getTheFirstUserWithSameLinkman(order.getUserUuid());
        userInfo.setLinkmanExists(StringUtils.isNotEmpty(firstSameLinkmanUser));

        if (StringUtils.isNotEmpty(firstSameLinkmanUser)) {
            Boolean existsLinkmanHasSettledOrder = userService.isUserFirstOrderSettledWith600(firstSameLinkmanUser);
            if (!existsLinkmanHasSettledOrder) {
                userInfo.setExistsLinkmanWithoutSuccessOrder(userService.isUserWithoutSuccessOrder(firstSameLinkmanUser));
            } else {
                userInfo.setExistsLinkmanWithoutSuccessOrder(false);
            }
            userInfo.setExistsLinkmanHasSettledOrder(existsLinkmanHasSettledOrder);
        }


        String phone = DESUtils.decrypt(user.getMobileNumberDES());

        userInfo.setMobileNumber(CheakTeleUtils.telephoneNumberValid2(phone));

        userInfo.setWhatsappAccount(userDetailService.getWhatsAppAccount(user.getUuid(), false));
        userInfo.setWhatsappAccountStr(userDetailService.getWhatsAppAccount(user.getUuid(), true));

        try {

            String idCard = user.getIdCardNo();

            //超过七天才重新获取
            String strIziPhoneAgeResponse = iziService.getLatestIziResponseByUserUuidWithDateLimit(order.getUserUuid(),
                    IziService.IziInvokeType.PHONE_AGE.getType(),7);
            IziResponse phoneAgeResponse = null;
            if (StringUtil.isNotEmpty(strIziPhoneAgeResponse)) {
                phoneAgeResponse = JsonUtil.toObject(strIziPhoneAgeResponse, IziResponse.class);
            } else {
                phoneAgeResponse = iziService.getPhoneAge(phone, order.getUuid(), order.getUserUuid());
            }
            userInfo.setIziPhoneAgeResult(IziPhoneAgeResult.parseResultFromResponse(phoneAgeResponse));

            IziResponse phoneVerifyResponse = iziService.getPhoneVerify(phone, idCard, order.getUuid(), order.getUserUuid());

            userInfo.setIziPhoneVerifyResult(IziPhoneVerifyResult.parseResultFromResponse(phoneVerifyResponse));

            if (userInfo.getAdvanceVerifyResult() == null || userInfo.getAdvanceVerifyResult().isAdvanceFailed()) {
                userVerifyResultService.initVerifyResult(order.getUuid(), order.getUserUuid(), UsrVerifyResult.VerifyTypeEnum.IZI_PHONE);
                IziPhoneVerifyResult phoneVerifyResult = userInfo.getIziPhoneVerifyResult();
                IdentityVerifyResult iziVerifyResult = null;
                if (phoneVerifyResponse != null
                        && phoneVerifyResult != null
                        && phoneVerifyResult.getStatus() == PhoneVerifyStatusEnum.OK
                        && "MATCH".equals(phoneVerifyResult.getMessage())
                ) {
                    iziVerifyResult = new IdentityVerifyResult(RUserInfo.IdentityVerifyResultType.IZI_PHONE_IDCARD_VERIFY_SUCCESS,
                            "izi验证匹配");
                } else {
                    iziVerifyResult = new IdentityVerifyResult(RUserInfo.IdentityVerifyResultType.IZI_PHONE_IDCARD_VERIFY_FAILED,
                            "izi验证失败");
                }
                userInfo.setAdvanceVerifyResult(iziVerifyResult);
                userVerifyResultService.updateVerifyResult(order.getUuid(), order.getUserUuid(), iziVerifyResult.getDesc(),
                        iziVerifyResult.getAdvanceVerifyResultType() == RUserInfo.IdentityVerifyResultType.IZI_PHONE_IDCARD_VERIFY_SUCCESS ? UsrVerifyResult.VerifyResultEnum.SUCCESS :
                                UsrVerifyResult.VerifyResultEnum.FAILED,
                        UsrVerifyResult.VerifyTypeEnum.IZI_PHONE);
            }

            //invoke izi whatsapp
            //whatsapp检查：
            usrService.checkIziWhatsappOpen(user.getUuid());


        } catch (Exception e) {
            log.error("request to izi error", e);
        }

        userInfo.setIdCardBirthday(extractBirthdayFromIdCardNumber(user.getIdCardNo()));
        userInfo.setIdCardSex(extractSexFromIdCardNumber(user.getIdCardNo()));
        userInfo.setCountOfOverdueLessThan5UsersByEmergencyTel(userService.countOfOverdueLessThanNUsersByLinkman(user.getUuid(), 5));
        userInfo.setGojekVerified(userService.certificationVerified(order.getUserUuid(), CertificationEnum.GOJECK_IDENTITY));


        //bankCode

        UsrBank usrBank = usrBankService.getUserBankById(order.getUserUuid(), order.getUserBankUuid());
        if (usrBank != null) {
            userInfo.setBankCode(usrBank.getBankCode());
        }

        //set whatsApp result for linkman
        List<UserIziVerifyResultMongo> lastRecordList = iziService.getLatestWhatsAppCheckResultList(order.getUuid());
        if (!CollectionUtils.isEmpty(lastRecordList)) {
            //联系人未开通量
            Long noOpenCount =
                    lastRecordList.stream().filter(elem->!"0".equals(elem.getWhatsAppNumberType()) && "no".equals(elem.getWhatsapp())).count();
            userInfo.setLinkmanWhatsAppCheckWithNoCount(noOpenCount);
        }

        //set whatsAppYesRadio
        if (!CollectionUtils.isEmpty(lastRecordList)) {
            int total = 0;
            int yesTotal = 0;
            for (UserIziVerifyResultMongo elem : lastRecordList) {
                if (!"0".equals(elem.getWhatsAppNumberType())) {
                    total ++;
                    UserIziVerifyResultMongo mongo = elem;
                    if ("yes".equals(mongo.getWhatsapp())) {
                        yesTotal ++;
                    }
                }
            }
            log.info("total is " + total + "  yesTotal is " + yesTotal);
            if (total != 0) {
                String result = String.valueOf(BigDecimal.valueOf(yesTotal).divide(BigDecimal.valueOf(total),4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).intValue()) + "%";
                userInfo.setWhatsAppYesRadio(result);
            }
        }

        //银行卡号相同的人数
        userInfo.setSameBankcardNumberWithOthersCount(usrBankService.countOfSameBankcardNumberWithOthers(usrBank.getBankNumberNo(),
                usrBank.getBankCode(), order.getUserUuid()));

        userInfo.setLinkmanInMultiBankcardUser(userService.isLinkmanInMultiBankcardUser(order.getUserUuid()));

        userInfo.setMobileInMultiBankcardUserEmergencyTel(userService.isMobileInMultiBankcardUserEmergencyTel(userInfo.getMobileNumber()));

        userInfo.setMobileInOverdueLessThan5UserEmergencyTel(userService.isEmergencyTelForOverdueLessThanNUsers(user.getMobileNumberDES(),5));

        RUserInfo.IziWhatsAppDetail ownerWhatsAppDetail = getOwnerWhatsAppDetail(user.getUuid(), phone);
        userInfo.setOwnerWhatsAppDetail(ownerWhatsAppDetail);
        //owner whatsApp detail
        userInfo.setWhatsAppCheckResult(ownerWhatsAppDetail == null ? null : ownerWhatsAppDetail.getWhatsAppOpenStatus());

        return Optional.of(userInfo);
    }


    private Integer extractSexFromIdCardNumber(String idCardNumber) {
        if (StringUtils.isNotEmpty(idCardNumber) && idCardNumber.length() >= 8) {
            String strDay = idCardNumber.substring(6, 8);
            if (strDay.startsWith("0")) {
                strDay = idCardNumber.substring(7, 8);
            }
            Integer day = Integer.valueOf(strDay);
            if (day >= 1 && day <= 31) {
                return RUserInfo.SexEnum.MALE.getCode();
            }

            if (day >= 41 && day <= 71) {
                return RUserInfo.SexEnum.FEMALE.getCode();
            }
        }
        return null;
    }

    public String extractBirthdayFromIdCardNumber(String idCardNumber) {
        if (StringUtils.isNotEmpty(idCardNumber) && idCardNumber.length() >= 12) {
            String strDay = idCardNumber.substring(6, 8);
            if (strDay.startsWith("0")) {
                strDay = idCardNumber.substring(7, 8);
            }
            Integer day = Integer.valueOf(strDay);
            if (day >= 41 && day <= 71) {
                day = day-40;
            }
            if (day < 10) {
                strDay = "0" + day;
            }else{
                strDay = day.toString();
            }

            String strMonth = idCardNumber.substring(8, 10);


            String strYear = idCardNumber.substring(10, 12);
            if (Integer.valueOf(strYear) < 20) {
                strYear = "20" + strYear; //小于20设置为20多少年
            } else {
                strYear = "19" + strYear;//大于等于20设置为19多少年
            }

            return strDay + "/" + strMonth + "/" + strYear;

        }
        return null;
    }



    private static boolean isAddressMatch(String originAddress, String compareAddress) {
        boolean matched = false;
        if (originAddress == null) {
            return matched;
        }
        String[] addressArray = originAddress.split("#");

        matched = Arrays.asList(addressArray).stream().filter(elem -> StringUtils.replaceBlank(elem)
                .equalsIgnoreCase(StringUtils.replaceBlank(compareAddress))).count() > 0;

        return matched;
    }

    private boolean isTelNumberMatch(String originTelData, String compareTel) {

        boolean isMatch = false;
        if (originTelData == null) {
            return isMatch;
        }

        compareTel = compareTel.replaceAll("\\+", "").replaceAll(" ", "");
        String[] telNumbers = originTelData.split("#");
        for (String tel : telNumbers) {
            isMatch = compareTel.startsWith(tel);
            if (isMatch) {
                return isMatch;
            }
        }

        return isMatch;
    }

    private RUserInfo.IziWhatsAppDetail getOwnerWhatsAppDetail(String userUuid, String mobile) {
        IziWhatsAppDetailEntity entity = iziWhatsAppService.getLatestIziWhatsDetail(userUuid, 0, mobile);
        if (entity == null) {
            return null;
        }
        return new RUserInfo.IziWhatsAppDetail(entity.getWhatsapp(), entity.getStatusUpdate(), entity.getAvatar());
    }

}
