package com.yqg.drools.extract;

import com.github.pagehelper.StringUtil;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.DeviceModel;
import com.yqg.drools.model.InstalledAppInfo;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.model.ScoreModel;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.dao.OrdStepDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdStep;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.service.user.service.UsrCertificationService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/***
 * to get the fact for score model calculation
 */
@Service
@Slf4j
public class RiskScoreExtractor {

    @Autowired
    private UsrCertificationService usrCertificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private OrdHistoryDao ordHistoryDao;
    @Autowired
    private OrdStepDao ordStepDao;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;

    public Optional<ScoreModel> extractModel(OrdOrder order, List<Object> facts) throws Exception {
        ScoreModel scoreModel = new ScoreModel();
        RUserInfo userModel = null;
        InstalledAppInfo appModel = null;
        for (Object fact : facts) {
            if (fact instanceof RUserInfo) {
                RUserInfo rUserInfo = (RUserInfo) fact;
                scoreModel.setGender(rUserInfo.getSex());
                scoreModel.setAcademic(rUserInfo.getAcademic());
                scoreModel.setPositionName(rUserInfo.getPositionName());
                scoreModel.setHasDrivingLicense(rUserInfo.isHasDriverLicense());
                if (rUserInfo.getIziPhoneAgeResult() != null) {
                    Integer iziAge = rUserInfo.getIziPhoneAgeResult().getAge();
                    scoreModel.setIziOnlineAge(iziAge != null ? iziAge.toString() : null);
                }
                scoreModel.setAge(rUserInfo.getAge());
                scoreModel.setHomeCity(rUserInfo.getHomeAddrCity());
                if(StringUtils.isNotEmpty(rUserInfo.getWhatsappAccount())){
                    scoreModel.setWhatsappAndOwnerTelSame(rUserInfo.getMobileNumber().equals(rUserInfo.getWhatsappAccount()));
                }

                scoreModel.setHomeProvinceMarriageAcademic(getHomeProvinceMarriageAcademicType(rUserInfo));
                userModel = rUserInfo;
            }
            if (fact instanceof InstalledAppInfo) {
                InstalledAppInfo appInfo = (InstalledAppInfo) fact;
                Long count = appInfo.getAppCountForEcommerce() == null ? 0 : appInfo.getAppCountForEcommerce();
                count += appInfo.getAppCountForTicket() == null ? 0 : appInfo.getAppCountForTicket();
                count += appInfo.getAppCountForBank() == null ? 0 : appInfo.getAppCountForBank();
                count += appInfo.getAppCountForCreditCard() == null ? 0 : appInfo.getAppCountForCreditCard();
                scoreModel.setEcommerceTicketBankCredit(count);
                scoreModel.setAppCount(appInfo.getTotalApps());
                scoreModel.setCreditCardAppCount(appInfo.getAppCountForCreditCard());
                scoreModel.setEcommerceAppCount(appInfo.getAppCountForEcommerce());
                appModel = appInfo;
            }
            if (fact instanceof DeviceModel) {
                DeviceModel deviceModel = (DeviceModel) fact;
                scoreModel.setMobileLanguage(deviceModel.getMobileLanguage());
                scoreModel.setPhoneBrand(deviceModel.getPhoneBrand());
            }
        }

        UsrCertificationInfo gojekCertification = usrCertificationService.getCertificationByUserUuidAndType(order.getUserUuid(),
                CertificationEnum.GOJECK_IDENTITY.getType());
        scoreModel.setGojekVerified(gojekCertification != null);

        scoreModel.setBorrowingPurpose(userService.getBorrowingPurpose(order.getUserUuid()));

        UsrAddressDetail orderAddress = userService.getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.ORDER);


        UsrAddressDetail homeAddress = userService.getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.HOME);

        if (orderAddress != null && StringUtil.isNotEmpty(orderAddress.getProvince())) {
            scoreModel.setOrderProvince(orderAddress.getProvince());
        } else {
            scoreModel.setOrderProvince(homeAddress.getProvince());
        }
        scoreModel.setLiveProvice(homeAddress.getProvince());

        if (orderAddress != null || homeAddress != null && orderAddress.getBigDirect() != null && homeAddress.getBigDirect() != null) {
            scoreModel.setBigDirectSameForOrderAndHome(orderAddress.getBigDirect().equalsIgnoreCase(homeAddress.getBigDirect()));
        }

        scoreModel.setBigDirect(homeAddress.getBigDirect());


        UsrBank usrBank = usrBankDao.getUserBankInfoById(order.getUserUuid(), order.getUserBankUuid());
        if (usrBank != null) {
            String bankCode = usrBank.getBankCode();
            if (StringUtil.isNotEmpty(bankCode) && bankCode.toUpperCase().endsWith("_SYR")) {
                scoreModel.setBankName(bankCode.toUpperCase().replaceAll("_SYR", ""));
            } else {
                scoreModel.setBankName(usrBank.getBankCode());
            }

        }
        Date submitDate = ordHistoryDao.getSubmitDate(order.getUuid());
        scoreModel.setPeriodOfCreateAndCommitTime(DateUtil.getDiffMinutes(order.getCreateTime(), submitDate));

        scoreModel.setChildrenCountHasSalaryPicEnterpriseAppCount(getChildrenCountHasSalaryPicEnterpriseAppCountType(userModel, appModel));

        //封装600产品V2版本没有的数据
        scoreModel.setIziAgeWhatsappSameAsMobile(getIziAgeWhatsapp(scoreModel, userModel));
        scoreModel.setIziVerifyResponseWhatsappSameAsMobile(getIziVerifyWhatsappSameAsMobile(userModel));
        scoreModel.setEmergeInIQorGood600(getEmergeInIQorGood600(order));
        scoreModel.setDependentBusiness(userModel.getDependentBusiness());
        scoreModel.setTimeForFillingIdentifyInfo(getFillingIdentifyInfoTime(order));
        scoreModel.setMonthlyIncome(getMonthlyIncome(userModel.getMonthlyIncome()));


        UsrUser user = userService.getUserInfo(order.getUserUuid());
        scoreModel.setLnOfdiffTimeForRegisterAndOrderCreate(calculateDiffTimeOfRegisterAndOrderCreateTime(order,user));

        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(order.getUserUuid());
        if(!CollectionUtils.isEmpty(linkmanList)){
            Optional<UsrLinkManInfo> firstLinkman = linkmanList.stream().filter(elem->elem.getSequence()==1).findFirst();
            scoreModel.setLinkmanRelation1(firstLinkman.isPresent()?firstLinkman.get().getRelation():null);
        }

        return Optional.of(scoreModel);
    }

    private String getMonthlyIncome (BigDecimal money) {

        if (money == null) {
            return "-inf#4000000";
        }
        if (money.compareTo(BigDecimal.valueOf(4000000)) <= 0) {
            return "-inf#4000000";
        } else if (money.compareTo(BigDecimal.valueOf(4000000)) > 0 && money.compareTo(BigDecimal.valueOf(6000000)) <= 0) {
            return "4000000#6000000";
        } else if (money.compareTo(BigDecimal.valueOf(6000000)) > 0 && money.compareTo(BigDecimal.valueOf(10000000)) <= 0) {
            return "6000000#10000000";
        } else {
            return "10000000#inf";
        }
    }
    private String getFillingIdentifyInfoTime(OrdOrder ordOrder) {

        List<OrdStep> start = ordStepDao.getOrderSteps(ordOrder.getUuid(), 1);
        if (CollectionUtils.isEmpty(start)) {
            return "-inf#-1";
        }
        List<OrdStep> end = ordStepDao.getOrderSteps(ordOrder.getUuid(), 2);
        if (CollectionUtils.isEmpty(end)) {
            return "-inf#-1";
        }
        Date stateTime = start.get(0).getCreateTime();
        Date endTime = end.get(0).getCreateTime();
        long diff = DateUtils.getDateTimeDifference2(endTime, stateTime);

        if (diff <= -1) {
            return "-inf#-1";
        } else if (diff > -1 && diff <= 10) {
            return "-1#10";
        } else if (diff > 10 && diff <= 15) {
            return "10#15";
        } else {
            return "15#inf";
        }

    }
    private Integer getEmergeInIQorGood600(OrdOrder ordOrder) {

        //先判断新户有紧急联系人在数据库里已有的首借600未逾期用户
        Integer count = userService.countOfOverdueLessThanNUsersByLinkman(ordOrder.getUserUuid(), 1);
        if (count > 0) {
            return 1;
        }
        //再判断usrProductRecord表中是否有已提额用户
        if (userService.countemergeInIQorGood600(ordOrder) > 0) {
            return 1;
        }
        return 0;
    }
    private String getIziAgeWhatsapp(ScoreModel scoreModel, RUserInfo userModel) {

        String iziAge = scoreModel.getIziOnlineAge();
        if (StringUtils.isEmpty(iziAge)) {
            iziAge = "0";
        }
        return iziAge + whatsappAndOwnerTelSameOrNot(userModel);
    }
    private String getIziVerifyWhatsappSameAsMobile(RUserInfo userModel) {

        String result = "";
        RUserInfo.IziPhoneVerifyResult response = userModel.getIziPhoneVerifyResult();
        if (response != null) {
            if (RUserInfo.IziPhoneVerifyResult.PhoneVerifyStatusEnum.NOT_FOUND.equals(response.getStatus())) {
                result = "notfound";
            }
            if ("MATCH".equals(response.getMessage())) {
                result = "MATCH";
            } else if ("NOT_MATCH".equals(response.getMessage())) {
                result = "NOT_MATCH";
            }
        }
        return result + whatsappAndOwnerTelSameOrNot(userModel);
    }
    private String whatsappAndOwnerTelSameOrNot(RUserInfo userModel) {
        String whatsappAndOwnerTelSame = "2";
        if (StringUtils.isNotEmpty(userModel.getWhatsappAccountStr())) {
            if (userModel.getMobileNumber().equals(userModel.getWhatsappAccount())) {
                whatsappAndOwnerTelSame = "1";
            } else {
                whatsappAndOwnerTelSame = "0";
            }
        }
        return whatsappAndOwnerTelSame;
    }

    private String getChildrenCountHasSalaryPicEnterpriseAppCountType(RUserInfo rUserInfo, InstalledAppInfo appInfo) {
        String childrenType = "";
        if (rUserInfo.getChildrenAmount() != null && rUserInfo.getChildrenAmount() == 0) {
            childrenType = "0";
        } else if (rUserInfo.getChildrenAmount() != null && rUserInfo.getChildrenAmount() == 1) {
            childrenType = "1";
        } else {
            childrenType = "2";
        }

        String salaryPicType = "";
        if (rUserInfo.getHasPayroll() != null && rUserInfo.getHasPayroll()) {
            salaryPicType = "1";
        } else {
            salaryPicType = "0";
        }
        String enterpriceAppCountType = "";
        if (appInfo == null || appInfo.getAppCountForEnterprise() == null) {
            enterpriceAppCountType = "2";
        } else if (appInfo.getAppCountForEnterprise() == 0) {
            enterpriceAppCountType = "0";
        } else if (appInfo.getAppCountForEnterprise() == 1) {
            enterpriceAppCountType = "1";
        } else {
            enterpriceAppCountType = "2";
        }

        return childrenType + salaryPicType + enterpriceAppCountType;
    }

    private String getHomeProvinceMarriageAcademicType(RUserInfo rUserInfo) {

        String provinceType = "";
        for (Map.Entry<String, String> province : provinceCategory.entrySet()) {
            if (RuleUtils.containsStringIgnoreCase(rUserInfo.getHomeAddrProvice(), province.getValue())) {
                provinceType = province.getKey();
            }
        }
        if (StringUtils.isNotEmpty(provinceType)) {
            provinceType = "2";
        }

        String maritalType = "";
        if (rUserInfo.getMaritalStatus() != null && RUserInfo.MarriageEnum.Single.getCode() == rUserInfo.getMaritalStatus()) {
            maritalType = "0";
        } else {
            maritalType = "1";
        }

        String academicType = "";
        for (Map.Entry<String, String> academic : academicCategory.entrySet()) {
            if (RuleUtils.containsStringIgnoreCase(rUserInfo.getAcademic(), academic.getValue())) {
                academicType = academic.getKey();
            }
        }

        return provinceType + maritalType + academicType;
    }


    public Integer calculateDiffTimeOfRegisterAndOrderCreateTime(OrdOrder order, UsrUser user) {
        Long diffTime = order.getCreateTime().getTime() - user.getCreateTime().getTime();
        double logDiffTime = Math.log(diffTime / 1000.0);
        return (int) Math.ceil(logDiffTime);
    }

    public static void main(String[] args) {
        Date d1 = DateUtil.stringToDate("2019-04-12 20:29:45",DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
        Date d2 = DateUtil.stringToDate("2019-04-12 20:29:45",DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
        Long diffTime = d1.getTime() - d2.getTime();
        double logDiffTime = Math.log(diffTime / 1000.0);
        System.err.println(Math.ceil(logDiffTime));
    }


    private static Map<String, String> provinceCategory = new ConcurrentHashMap<>();
    private static Map<String, String> academicCategory = new ConcurrentHashMap<>();

    static {
        provinceCategory.put("1", "Nanggroe Aceh Darussalam (NAD)#KALIMANTAN TENGAH#BENGKULU#BANTEN#KALIMANTAN UTARA#SUMATERA BARAT#GORONTALO#DI " +
                "YOGYAKARTA#ACEH#SULAWESI TENGGARA#BANGKA BELITUNG#PAPUA#NUSA TENGGARA TIMUR#PAPUA BARAT#MALUKU UTARA#SULAWESI BARAT#MALUKU#BALI#DI Yogyakarta#JAWA BARAT#DKI JAKARTA#Banten#JAWA TENGAH#KALIMANTAN TIMUR#SULAWESI SELATAN");

        provinceCategory.put("2", "JAWA TIMUR#Sumatera Barat#KALIMANTAN SELATAN#JAMBI#KEPULAUAN RIAU#LAMPUNG#SULAWESI UTARA#RIAU#SUMATERA " +
                "UTARA#KALIMANTAN BARAT#SUMATERA SELATAN");

        academicCategory.put("0", RUserInfo.EducationEnum.JuniorMiddleSchool.getCode() + "#" + RUserInfo.EducationEnum.HighSchool.getCode());
        academicCategory.put("1",
                RUserInfo.EducationEnum.PrimarySchool.getCode() + "#" + RUserInfo.EducationEnum.Specialty.getCode() + "#" + RUserInfo.EducationEnum.GraduateStudent.getCode() + "#" +
                        RUserInfo.EducationEnum.Undergraduate.getCode() + "#" + RUserInfo.EducationEnum.Doctor.getCode());

    }

}
