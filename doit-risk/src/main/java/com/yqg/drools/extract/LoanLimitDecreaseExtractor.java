package com.yqg.drools.extract;

import com.github.pagehelper.StringUtil;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.CardIdUtils;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.LoanLimitModel;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.order.OrdDeviceInfoService;
import com.yqg.service.order.OrderCheckService;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.user.service.*;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/***
 * 50&100是否好用户规则判断
 */
@Service
@Slf4j
public class LoanLimitDecreaseExtractor {
    @Autowired
    private UserService userService;
    @Autowired
    private UserLinkManService userLinkManService;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;
    @Autowired
    private OrderCheckService orderCheckService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private TeleCallResultDao teleCallResultDao;
    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;
    @Autowired
    private UsrCertificationService usrCertificationService;
    @Autowired
    private IziService iziService;
    @Autowired
    private OrdHistoryDao ordHistoryDao;
    @Autowired
    private OrdDeviceInfoService ordDeviceInfoService;
    @Autowired
    private UsrBankService usrBankService;


    /**
     * @param order 首借订单
     * @return
     */
    public Optional<LoanLimitModel> extract(OrdOrder order) {

        if (order.getStatus() != OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() && order.getStatus() != OrdStateEnum.RESOLVED_OVERDUE.getCode()) {
            return Optional.empty();
        }
        LoanLimitModel model = new LoanLimitModel();
        model.setIsProduct50(RuleConstants.PRODUCT50.equals(order.getAmountApply()) || RuleConstants.PRODUCT50_V0.equals(order.getAmountApply()));
        model.setIsProduct100(RuleConstants.PRODUCT100.equals(order.getAmountApply()) || RuleConstants.PRODUCT100_V0.equals(order.getAmountApply()));
        UsrUser user = userService.getUserInfo(order.getUserUuid());
        model.setSex(user.getSex());
        //
        Long overdueDays = DateUtil.getDiffDaysIgnoreHours(order.getRefundTime(), order.getActualRefundTime());
        model.setLastLoanOverdueDays(overdueDays);
        model.setMobileIsEmergencyTelForMoreThan3TimesUser(isEmergencyTelForThreeTimesLoanedUser(user.getMobileNumberDES()));
        model.setEmergencyTelIsUpLimitUser(isLinkmanInUpLimitUser(user.getUuid()));
        model.setUserRole(user.getUserRole());

        String industry = null;
        String workCity = null;
        String postName = null;
        if (user.getUserRole() == 2) {
            //工作人员
            UsrWorkDetail userWorkDetail = userDetailService.getUserWorkDetail(user.getUuid());
            industry = userWorkDetail == null ? null : userWorkDetail.getDependentBusiness();
            UsrAddressDetail usrAddressDetail = userService.getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.COMPANY);
            workCity = usrAddressDetail == null ? null : usrAddressDetail.getCity();

            postName = userWorkDetail!=null? userWorkDetail.getPositionName():null;

            model.setAcademic(userWorkDetail.getAcademic());

        } else if (user.getUserRole() == 3) {
            //家庭主妇
            UsrHouseWifeDetail userHouseWifeDetail = userDetailService.getUserHouseWifeDetail(user.getUuid());
            model.setAcademic(userHouseWifeDetail.getAcademic());

        } else if (user.getUserRole() == 1) {
            //学生
            UsrStudentDetail usrStudentDetail = userDetailService.getUserStudentDetail(user.getUuid());
            model.setAcademic(usrStudentDetail.getAcademic());

        }

        if (user.getSex() == 1) {
            //男
            model.setUserInHighQualityIndustry(RuleUtils.containsStringIgnoreCase(industry, LoanLimitModel.highQualityIndustryForMalePRD100));
            model.setWorkCityInHighQualityCity(RuleUtils.containsStringIgnoreCase(workCity, LoanLimitModel.highQualityWorkCityForMalePRD100));
        } else {
            //女
            model.setUserInHighQualityIndustry(RuleUtils.containsStringIgnoreCase(industry, LoanLimitModel.highQualityIndustryForFemalePRD100));
            model.setWorkCityInHighQualityCity(RuleUtils.containsStringIgnoreCase(workCity, LoanLimitModel.highQualityWorkCityForFemalePRD100));
        }

        TeleCallResult companyCallResult = teleCallResultDao.getLatestTelCallResultByType(order.getUuid(),
                TeleCallResult.CallTypeEnum.COMPANY.getCode());
        model.setCompanyTelResultInFirstBorrow(companyCallResult != null ? companyCallResult.getCallResultType() : null);

        //app个数
        List<OrdRiskRecord> orderRiskRecord = orderRiskRecordRepository.getOrderRiskRecordList(order.getUuid());
        if (!CollectionUtils.isEmpty(orderRiskRecord)) {
            model.setAppCount(getNumberFromRealValue(orderRiskRecord, BlackListTypeEnum.INSTALL_APP_COUNT.getMessage()));
            model.setAppCountForCreditCard(getNumberFromRealValue(orderRiskRecord, BlackListTypeEnum.APP_COUNT_FOR_CREDIT_CARD.getMessage()));
            model.setAppCountForBank(getNumberFromRealValue(orderRiskRecord, BlackListTypeEnum.APP_COUNT_FOR_BANK.getMessage()));
            model.setAppCountForEcommerce(getNumberFromRealValue(orderRiskRecord, BlackListTypeEnum.APP_COUNT_FOR_E_COMMERCE.getMessage()));
        }

        try {
            UsrCertificationInfo gojekCertification = usrCertificationService.getCertificationByUserUuidAndType(order.getUserUuid(),
                    CertificationEnum.GOJECK_IDENTITY.getType());
            model.setGojekVerifed(gojekCertification != null);
        } catch (Exception e) {
            log.error("get gojek verify info error orderNo: " + order.getUuid(), e);
        }

        //iziPhoneAge
        String strIziPhoneAgeResponse = iziService.getIziResponse(order.getUuid(), "1");
        if (StringUtil.isNotEmpty(strIziPhoneAgeResponse)) {
            IziResponse phoneAgeResponse = JsonUtil.toObject(strIziPhoneAgeResponse, IziResponse.class);
            RUserInfo.IziPhoneAgeResult phoneAgeResult = RUserInfo.IziPhoneAgeResult.parseResultFromResponse(phoneAgeResponse);
            Integer phoneAge = phoneAgeResult != null ? phoneAgeResult.getAge() : null;
            model.setIziPhoneAge(phoneAge);
        }

        Date submitTime = ordHistoryDao.getSubmitDate(order.getUuid());
        Long diffMinutesForCreateAndSubmitTime = DateUtil.getDiffMinutes(order.getCreateTime(), submitTime);
        model.setDiffMinutesForCreateAndSubmitTime(diffMinutesForCreateAndSubmitTime);
        String whatsapp = userDetailService.getWhatsAppAccount(user.getUuid(), false);
        String phoneNumber = DESUtils.decrypt(user.getMobileNumberDES());
        model.setSameForWhatsappAndMobile(phoneNumber.equals(whatsapp));
        if (StringUtils.isNotEmpty(user.getIdCardNo())) {
            model.setAge(CardIdUtils.getAgeByIdCard(user.getIdCardNo().trim()));
        }
        model.setHasSalaryPic(userService.getAttachmentInfo(UsrAttachmentEnum.PAYROLL, order.getUserUuid()) != null);
        model.setHasDriverLicense(userService.getAttachmentInfo(UsrAttachmentEnum.SIM, order.getUserUuid()) != null);
        String strIziPhoneVerifyResponse = iziService.getIziResponse(order.getUuid(), "2");
        if (StringUtils.isNotEmpty(strIziPhoneVerifyResponse)) {
            IziResponse phoneVerifyResponse = JsonUtil.toObject(strIziPhoneVerifyResponse, IziResponse.class);
            model.setIziPhoneVerifyResult(RUserInfo.IziPhoneVerifyResult.parseResultFromResponse(phoneVerifyResponse));
        }

        Optional<OrdDeviceInfo> ordDeviceInfo = ordDeviceInfoService.getDeviceInfoByOrderNo(order.getUuid());
        if (ordDeviceInfo.isPresent()) {
            model.setTotalSpace(OrdDeviceInfo.getFormatStorageCapacity(ordDeviceInfo.get().getTotalSpace()));
        }

        model.setEmergencyTelIsFirstBorrowNotOverdueUser(emergencyTelIsFirstBorrowNotOverdueUser(order.getUserUuid()));

        model.setDiffMinutesForRegisterAddOrderCreate(DateUtil.getDiffMinutes(user.getCreateTime(), order.getCreateTime()));
        model.setFirstLinkmanName(linkmanNameBySequence(user.getUuid(), 1));

        List<UserIziVerifyResultMongo> lastRecordList = iziService.getLatestWhatsAppCheckResultList(order.getUuid());
        if (!CollectionUtils.isEmpty(lastRecordList)) {
            Optional<UserIziVerifyResultMongo> result = lastRecordList.stream().filter(elem -> StringUtils.isNotEmpty(elem.getWhatsAppNumberType()) &&
                    "0".equals(elem.getWhatsAppNumberType())).findFirst();

            if (result.isPresent()) {
                model.setIziWhatsappOpenResult(result.get().getWhatsapp());
            }
        }

        //livecity
        UsrAddressDetail homeAddress = userService.getUserAddressDetailByType(user.getUuid(),UsrAddressEnum.HOME);
        if(homeAddress!=null){
            model.setUserInHighQualityLiveCity(RuleUtils.containsStringIgnoreCase(homeAddress.getCity(), LoanLimitModel.highQualityLiveCityPRD50));
        }

        List<UsrBank> successBankList = usrBankService.getSuccessBankList(user.getUuid());
        model.setValidBankCount(successBankList.size());

        model.setUserInHighQualityIndustryFor50(RuleUtils.containsStringIgnoreCase(industry, LoanLimitModel.highQualityIndustryPRD50));
        model.setUserInHighQualityPostFor50(RuleUtils.containsStringIgnoreCase(postName, LoanLimitModel.highQualityPositionNamePRD50));
        model.setInFirstBorrowingNotOverdueUserList(isEmergencyTelForFirstBorrowingNotOverdueUsers(user.getMobileNumberDES()));

        return Optional.of(model);
    }

    private Integer getNumberFromRealValue(List<OrdRiskRecord> records, String ruleName) {
        Optional<OrdRiskRecord> appCount =
                records.stream().filter(elem -> ruleName.equals(elem.getRuleDetailType())).findFirst();
        if (appCount.isPresent() && StringUtils.isNotEmpty(appCount.get().getRuleRealValue()) && !"null".equalsIgnoreCase(appCount.get().getRuleRealValue())) {
            return Integer.valueOf(appCount.get().getRuleRealValue());
        }
        return null;
    }

    /***
     * 是否是借款3次以上用户的紧急联系人
     * @param mobileDesc
     * @return
     */
    private Boolean isEmergencyTelForThreeTimesLoanedUser(String mobileDesc) {
        String mobile = DESUtils.decrypt(mobileDesc);

        //1、是紧急联系人号码
        List<UsrLinkManInfo> linkmanList = userLinkManService.getLinkmanByFormatMobile(mobile);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return false;
        }


        //2、对应的用户是借款3次或以上的用户
        for (UsrLinkManInfo linkman : linkmanList) {
            Long count = orderCheckService.successLoanCount(linkman.getUserUuid());
            if (count >= 3) {
                return true;
            }
        }
        return false;
    }

    /***
     * 紧急联系人是提额用户
     * @param userUuid
     * @return
     */
    private Boolean isLinkmanInUpLimitUser(String userUuid) {

        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(userUuid);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return false;
        }

        //紧急联系人是注册用户
        //紧急联系人是提额用户
        for (UsrLinkManInfo info : linkmanList) {
            String formatNumber = CheakTeleUtils.telephoneNumberValid2(info.getContactsMobile());
            if (StringUtils.isEmpty(formatNumber)) {
                continue;
            }
            boolean in = userService.isMobileInUpLoanLimitUser(DESUtils.encrypt(formatNumber));
            if (in) {
                return true;
            }
        }
        return false;
    }

    /***
     * 紧急联系人是首借600未逾期用户
     * @param userUuid
     * @return
     */
    private Boolean emergencyTelIsFirstBorrowNotOverdueUser(String userUuid) {
        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(userUuid);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return false;
        }

        //紧急联系人是注册用户
        //紧急联系人是提额用户
        for (UsrLinkManInfo info : linkmanList) {
            String formatNumber = CheakTeleUtils.telephoneNumberValid2(info.getContactsMobile());
            if (StringUtils.isEmpty(formatNumber)) {
                continue;
            }
            boolean in = userService.isMobileInFirstBorrow600NotOverdueUser(DESUtils.encrypt(formatNumber));
            if (in) {
                return true;
            }
        }
        return false;
    }


    private String linkmanNameBySequence(String userUuid, Integer sequence) {
        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(userUuid);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return null;
        }
        Optional<UsrLinkManInfo> linkman = linkmanList.stream().filter(elem -> elem.getSequence().equals(sequence)).findFirst();
        if (linkman.isPresent()) {
            return linkman.get().getContactsName();
        }
        return null;
    }


    public Boolean isEmergencyTelForFirstBorrowingNotOverdueUsers(String mobileDesc){
        String mobile = DESUtils.decrypt(mobileDesc);

        //1、是紧急联系人号码
        List<UsrLinkManInfo> linkmanList = userLinkManService.getLinkmanByFormatMobile(mobile);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return false;
        }
        //对应用户是首借100以上产品正常还款用户
        for(UsrLinkManInfo linkManInfo: linkmanList){
           Boolean isNotOverdue = orderCheckService.isFirstBorrowingNotOverdue(linkManInfo.getUserUuid());
           if(isNotOverdue){
               return true;
           }
        }
        return false;
    }

    public static void main(String[] args) {
//        Date d1 = DateUtil.stringToDate("2018-05-14",DateUtil.FMT_YYYY_MM_DD);
//        Date d2 = DateUtil.stringToDate("2018-05-15",DateUtil.FMT_YYYY_MM_DD);
//        System.err.println( DateUtil.getDiffDaysIgnoreHours(d1,d2));


    }

    public static String getUnixMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // linux下的命令，一般取eth0作为本地主网卡
            process = Runtime.getRuntime().exec("ifconfig eth0");
            // 显示信息中包含有mac地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[hwaddr]
                index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {// 找到了
                    // 取出mac地址并去除2边空格
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("unix/linux方式未获取到网卡地址");
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }
}
