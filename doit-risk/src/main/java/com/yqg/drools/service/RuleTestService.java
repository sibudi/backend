package com.yqg.drools.service;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.drools.executor.firstBorrowing.Product100ExecutionChain;
import com.yqg.drools.executor.firstBorrowing.Product50ExecutionChain;
import com.yqg.drools.executor.firstBorrowing.Product50ExtendRuleExecutionChain;
import com.yqg.drools.executor.reBorrowing.ReBorrowingProduct600ExtendExecutionChain;
import com.yqg.drools.executor.reBorrowing.ReBorrowingUniversalExecutionChain;
import com.yqg.risk.dao.OrderScoreDao;
import com.yqg.risk.dao.OrderScoreDetailDao;
import com.yqg.risk.dao.RiskResultDao;
import com.yqg.risk.dao.ScoreTemplateDao;
import com.yqg.risk.entity.OrderScore;
import com.yqg.risk.entity.OrderScoreDetail;
import com.yqg.risk.entity.ScoreTemplate;
import com.yqg.drools.beans.RuleSetExecutedResult;
import com.yqg.drools.executor.ExecutorUtil;
import com.yqg.drools.executor.base.FlowEnum;
import com.yqg.drools.executor.firstBorrowing.LabelExecutionChain;
import com.yqg.drools.model.*;
import com.yqg.drools.model.base.*;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/28
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class RuleTestService {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleResultAnalysisService ruleResultAnalysisService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrdService ordService;

    @Autowired
    private RuleApplicationService ruleApplicationService;
    @Autowired
    private ScoreTemplateDao scoreTemplateDao;
    @Autowired
    private OrderScoreDetailDao orderScoreDetailDao;
    @Autowired
    private OrderScoreDao orderScoreDao;
    @Autowired
    private ExecutorUtil executorUtil;
    @Autowired
    private RiskResultDao riskResultDao;


    public void testLoanLimit() {
        List<OrdOrder> orderList = riskResultDao.getTestOrders();
        for (OrdOrder o : orderList) {
            try {
                applicationService.checkUserLoanLimit(o.getUserUuid());
            } catch (Exception e) {
                log.error("error, " + o.getUuid(), e);
            }
        }
    }

    public List<RuleResult> test(List<Object> facts) {
        OrdOrder order = new OrdOrder();
        order.setStatus(2);
        order.setUuid("zxc-test-190304-order");
        order.setUserUuid("zxc-test-190304");

        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();

        Optional<RuleConditionModel> ruleCondition = executorUtil.buildRuleCondition(allRules, order);
        facts.add(ruleCondition.get());
        List<RuleResult> resultList = ruleService
                .executeRules(RuleSetEnum.TEST, facts, allRules);


        ruleResultAnalysisService.batchRecordRuleResult(order, allRules, resultList);
        return resultList;
    }

    public void testScoreModel() {
        OrdOrder order = new OrdOrder();
        order.setUuid("011901081610404681");
        order.setUserUuid("68FB35EFD32C40EA8C6ABB3620BE27E4");
        List<OrdOrder> ordOrders = ordService.orderInfo(order);

        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();
        try {
            applicationService.apply(ordOrders.get(0), allRules);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Autowired
    Product100ExecutionChain product100ExecutionChain;
    @Autowired
    Product50ExecutionChain product50ExecutionChain;

    public void testRuleWith100Pass() {
        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();
        OrdOrder order = ordService.getOrderByOrderNo("011906191029451230");
        try {
            RuleSetExecutedResult ruleSetResult = product50ExecutionChain.execute(order, allRules, getRuleWith100PassFacts());
            System.err.println(JsonUtils.serialize(ruleSetResult));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<Object> getRuleWith100PassFacts(){
        //011906271430563990
        List<Object> facts = new ArrayList<>();
        String str1 = "{\n" +
                "\t\t\"isInYQGBlackListSize\": 0,\n" +
                "\t\t\"isIMEIInYQGBlackList\": false\n" +
                "\t}";
        facts.add(JsonUtil.toObject(str1,InnerBlackList.class));

        str1 = "{\n" +
                "\t\t\"idCard\": \"3210184501810021\",\n" +
                "\t\t\"age\": 38,\n" +
                "\t\t\"homeAddress\": \"Kabupaten Majalengka\",\n" +
                "\t\t\"userRole\": 2,\n" +
                "\t\t\"schoolAddress\": null,\n" +
                "\t\t\"companyAddress\": \"Kabupaten Majalengka\",\n" +
                "\t\t\"orderAddress\": \"Kabupatén Majalengka\",\n" +
                "\t\t\"isCompanyTelphoneNotInJarkat\": true,\n" +
                "\t\t\"homeAddressNotBelongToJarkat\": false,\n" +
                "\t\t\"schoolAddressNotBelongToJarkat\": null,\n" +
                "\t\t\"companyAddressNotBelongToJarkat\": false,\n" +
                "\t\t\"orderAddressNotBelongToJarkat\": true,\n" +
                "\t\t\"companyAddressNotBelongToJarkatNormal\": true,\n" +
                "\t\t\"companyAddressNotBelongToJarkatIOS\": false,\n" +
                "\t\t\"yituScore\": 85.34073002543059,\n" +
                "\t\t\"facePlusPlusScore\": null,\n" +
                "\t\t\"advanceVerifyResult\": {\n" +
                "\t\t\t\"advanceVerifyResultType\": \"IZI_REAL_NAME_VERIFY_ALREADY_SUCCESS\",\n" +
                "\t\t\t\"desc\": \"IZI实名认证已经认证成功\",\n" +
                "\t\t\t\"xenditFailed\": false,\n" +
                "\t\t\t\"advanceFailed\": false\n" +
                "\t\t},\n" +
                "\t\t\"sex\": 2,\n" +
                "\t\t\"hasDriverLicense\": true,\n" +
                "\t\t\"companyAddrProvice\": \"JAWA BARAT\",\n" +
                "\t\t\"companyAddrCity\": \"Kabupaten Majalengka\",\n" +
                "\t\t\"homeAddrProvice\": \"JAWA BARAT\",\n" +
                "\t\t\"homeAddrCity\": \"Kabupaten Majalengka\",\n" +
                "\t\t\"childrenAmount\": 2,\n" +
                "\t\t\"academic\": \"Sekolah Menengah Atas\",\n" +
                "\t\t\"maritalStatus\": 1,\n" +
                "\t\t\"thirdType\": null,\n" +
                "\t\t\"mobileAsEmergencyTelCount\": 0,\n" +
                "\t\t\"hasInsuranceCard\": false,\n" +
                "\t\t\"hasFamilyCard\": false,\n" +
                "\t\t\"hasTaxNumber\": false,\n" +
                "\t\t\"hasPayroll\": true,\n" +
                "\t\t\"positionName\": \"Staf perusahaan\",\n" +
                "\t\t\"hitOverDuePositionMan\": false,\n" +
                "\t\t\"hitOverDuePositionFeMen\": false,\n" +
                "\t\t\"hitHomeProviceMan\": false,\n" +
                "\t\t\"hitHomeProviceFeMen\": false,\n" +
                "\t\t\"hitHomeProviceMan150\": false,\n" +
                "\t\t\"hitHomeProviceFeMen150\": false,\n" +
                "\t\t\"hitHomeProviceMan80\": false,\n" +
                "\t\t\"dependentBusiness\": \"Busana, Tekstil & Fashion\",\n" +
                "\t\t\"religionName\": \"Islam\",\n" +
                "\t\t\"monthlyIncome\": 2750000,\n" +
                "\t\t\"firstLinkmanExists\": false,\n" +
                "\t\t\"firstLinkmanExistsRelatedUserSettled\": null,\n" +
                "\t\t\"emailExists\": false,\n" +
                "\t\t\"firstEmailExistsRelatedUserSettled\": false,\n" +
                "\t\t\"linkmanExists\": false,\n" +
                "\t\t\"existsLinkmanHasSettledOrder\": null,\n" +
                "\t\t\"existsLinkmanWithoutSuccessOrder\": null,\n" +
                "\t\t\"workAddressExists\": false,\n" +
                "\t\t\"orderAddressExists\": true,\n" +
                "\t\t\"homeAddressExists\": false,\n" +
                "\t\t\"iziPhoneAgeResult\": {\n" +
                "\t\t\t\"status\": \"OK\",\n" +
                "\t\t\t\"age\": 2\n" +
                "\t\t},\n" +
                "\t\t\"iziPhoneVerifyResult\": {\n" +
                "\t\t\t\"status\": \"NOT_FOUND\",\n" +
                "\t\t\t\"message\": \"This phone and id can't be found in our system\"\n" +
                "\t\t},\n" +
                "\t\t\"companyTel\": \"023-3663105\",\n" +
                "\t\t\"companyName\": \"PT LEETEX GARMENT INDONESIA\",\n" +
                "\t\t\"whatsappAccount\": \"85324888205\",\n" +
                "\t\t\"whatsappAccountStr\": \"085324888205\",\n" +
                "\t\t\"mobileNumber\": \"85314888205\",\n" +
                "\t\t\"idCardSex\": 2,\n" +
                "\t\t\"idCardBirthday\": \"05/01/1981\",\n" +
                "\t\t\"birthday\": \"05/01/1981\",\n" +
                "\t\t\"countOfOverdueLessThan5UsersByEmergencyTel\": 0,\n" +
                "\t\t\"hasCreditCard\": false,\n" +
                "\t\t\"gojekVerified\": true,\n" +
                "\t\t\"bankCode\": \"BNI\",\n" +
                "\t\t\"whatsAppCheckResult\": \"yes\",\n" +
                "\t\t\"whatsAppYesRadio\": \"50%\",\n" +
                "\t\t\"linkmanInMultiBankcardUser\": false,\n" +
                "\t\t\"mobileInMultiBankcardUserEmergencyTel\": false,\n" +
                "\t\t\"sameBankcardNumberWithOthersCount\": 0,\n" +
                "\t\t\"orderSmallDirectIsNull\": false,\n" +
                "\t\t\"mobileInOverdueLessThan5UserEmergencyTel\": false,\n" +
                "\t\t\"linkmanWhatsAppCheckWithNoCount\": 2,\n" +
                "\t\t\"ownerWhatsAppDetail\": {\n" +
                "\t\t\t\"whatsAppOpenStatus\": \"yes\",\n" +
                "\t\t\t\"statusUpdateTime\": \"20190511\",\n" +
                "\t\t\t\"avatar\": \"yes\"\n" +
                "\t\t}\n" +
                "\t}";

        facts.add(JsonUtil.toObject(str1,RUserInfo.class));

        str1 = "{\n" +
                "\t\t\"appForLoanCount\": 1,\n" +
                "\t\t\"appForLoanRatio\": 0.0714,\n" +
                "\t\t\"incrementalAppForLoanCount\": null,\n" +
                "\t\t\"appForLoanRatioChange\": null,\n" +
                "\t\t\"hasLatestOrder\": false,\n" +
                "\t\t\"totalApps\": 14,\n" +
                "\t\t\"diffDaysBetweenLatestUpdateTimeAndCommitTime\": 0,\n" +
                "\t\t\"diffDaysBetweenEarliestUpdateTimeAndCommitTime\": 57,\n" +
                "\t\t\"diffDaysBetweenForEarliestAndLatestUpdateTime\": 57,\n" +
                "\t\t\"appCountForNews\": 0,\n" +
                "\t\t\"appCountForEnterprise\": 1,\n" +
                "\t\t\"appCountForBeauty\": 0,\n" +
                "\t\t\"appCountForGambling\": 1,\n" +
                "\t\t\"appCountForCreditCard\": 0,\n" +
                "\t\t\"appCountForBeautyPicture\": 0,\n" +
                "\t\t\"appCountForPhotography\": 0,\n" +
                "\t\t\"appCountForEcommerce\": 2,\n" +
                "\t\t\"appCountForGame\": 0,\n" +
                "\t\t\"appCountForSocial\": 5,\n" +
                "\t\t\"appCountForTaxBPJS\": 0,\n" +
                "\t\t\"appCountForBank\": 0,\n" +
                "\t\t\"appCountForCinema\": 0,\n" +
                "\t\t\"appCountForTicket\": 0\n" +
                "\t}";

        facts.add(JsonUtil.toObject(str1,InstalledAppInfo.class));

        str1 = "{\n" +
                "\t\t\"deviceId\": \"\",\n" +
                "\t\t\"matchedForOthersCount\": null,\n" +
                "\t\t\"matchedIMEIForOthersCount\": null,\n" +
                "\t\t\"sameIpApplyCount\": 1,\n" +
                "\t\t\"mobileLanguage\": \"in\",\n" +
                "\t\t\"netType\": \"4G\",\n" +
                "\t\t\"pictureNumber\": \"0\",\n" +
                "\t\t\"totalMemory\": 1.37,\n" +
                "\t\t\"totalSpace\": 3.55,\n" +
                "\t\t\"isJailBreak\": false,\n" +
                "\t\t\"phoneBrand\": \"samsung\",\n" +
                "\t\t\"isIOS\": false,\n" +
                "\t\t\"matchedIMSIForOthersCount\": null,\n" +
                "\t\t\"matchedCustomDeviceFingerprintWithApps\": 0,\n" +
                "\t\t\"matchedCustomDeviceFingerprintWithIp\": 0,\n" +
                "\t\t\"matchedCustomDeviceFingerprintWithPhoneBrand\": 0,\n" +
                "\t\t\"matchedCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType\": 0,\n" +
                "\t\t\"matchedCustomDeviceFingerprintWithoutAndroidId\": 0,\n" +
                "\t\t\"hitSameExtendDeviceCount\": 0\n" +
                "\t}";
        facts.add(JsonUtil.toObject(str1,DeviceModel.class));

        str1 = "{\n" +
                "\t\t\"idCardNoInOverdue15BlackList\": false,\n" +
                "\t\t\"mobileInOverdue15BlackList\": null,\n" +
                "\t\t\"emergencyTelInOverdue15BlackListEmergencyTel\": false,\n" +
                "\t\t\"bankcardNoInOverdue15BlackList\": false,\n" +
                "\t\t\"imeiInOverdue15BlackList\": null,\n" +
                "\t\t\"mobileIsOverdue15BlackListEmergencyTel\": false,\n" +
                "\t\t\"emergencyTelInOverdue15BlackList\": false,\n" +
                "\t\t\"mobileInOverdue15BlackListContacts\": false,\n" +
                "\t\t\"mobileInOverdue15BlackListCallRecords\": false,\n" +
                "\t\t\"mobileInOverdue15BlackListShortMsg\": false,\n" +
                "\t\t\"contactInOverdue15BlackList\": false,\n" +
                "\t\t\"callRecordInOverdue15BlackList\": false,\n" +
                "\t\t\"contactInOverdue15Count\": 0,\n" +
                "\t\t\"callRecordInOverdue15Count\": 0,\n" +
                "\t\t\"mobileIsFraudUserEmergencyTel\": false,\n" +
                "\t\t\"mobileInFraudUserCallRecordsCount\": 0,\n" +
                "\t\t\"imeiInFraudUser\": null,\n" +
                "\t\t\"mobileInFraudUser\": false,\n" +
                "\t\t\"idCardNoInFraudUser\": false,\n" +
                "\t\t\"hitFraudUserInfo\": false,\n" +
                "\t\t\"deviceIdInOverdue30DaysUser\": false,\n" +
                "\t\t\"smsContactOverdue15DaysCount\": null,\n" +
                "\t\t\"callRecordInOverdue7BlackList\": false,\n" +
                "\t\t\"imeiInOverdue7BlackList\": null,\n" +
                "\t\t\"idCardNoInOverdue7BlackList\": false,\n" +
                "\t\t\"mobileInOverdue7BlackList\": false,\n" +
                "\t\t\"bankcardInOverdue7BlackList\": false,\n" +
                "\t\t\"emergencyTelInOverdue7BlackListEmergencyTel\": false,\n" +
                "\t\t\"mobileInOverdue7BlackListEmergencyTel\": false,\n" +
                "\t\t\"emergencyTelInOverdue7BlackList\": false,\n" +
                "\t\t\"hitSensitiveUserInfo\": false,\n" +
                "\t\t\"hitCollectorBlackUserInfo\": false,\n" +
                "\t\t\"hitComplaintUserInfo\": false,\n" +
                "\t\t\"whatsappInOverdue7BlackList\": false,\n" +
                "\t\t\"whatsappInOverdue7BlackListEmergencyTel\": false,\n" +
                "\t\t\"whatsappInOverdue7BlackListCallRecord\": false,\n" +
                "\t\t\"whatsappInOverdue7BlackListContact\": false,\n" +
                "\t\t\"whatsappInOverdue7BlackListSms\": false,\n" +
                "\t\t\"emergencyTelInFraudUserEmergencyTel\": false,\n" +
                "\t\t\"emergencyTelInFraudUserCallRecord\": false,\n" +
                "\t\t\"emergencyTelInFraudUserContact\": false,\n" +
                "\t\t\"emergencyTelInFraudUserSms\": false,\n" +
                "\t\t\"emergencyTelInFraudUserWhatsapp\": false,\n" +
                "\t\t\"companyTelInOverdue7BlackList\": false,\n" +
                "\t\t\"companyAddressInOverdue7BlackList\": false,\n" +
                "\t\t\"homeAddressInOverdue7BlackList\": false,\n" +
                "\t\t\"companyTelInFraudBlackList\": false,\n" +
                "\t\t\"companyAddressInFraudBlackList\": false,\n" +
                "\t\t\"homeAddressInFraudBlackList\": false,\n" +
                "\t\t\"mobileIsEmergencyTelForUnSettledOverdue7UserWith1th\": false,\n" +
                "\t\t\"emergencyTelIsUnSettledOverdue7UserWith1th\": false,\n" +
                "\t\t\"emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith1th\": false,\n" +
                "\t\t\"emergencyTelIsUnSettledOverdue7UserWith2th\": false,\n" +
                "\t\t\"emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith2th\": false,\n" +
                "\t\t\"mobileIsEmergencyTelForUnSettledOverdue7UserWith2th\": false,\n" +
                "\t\t\"emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith3Or4th\": false,\n" +
                "\t\t\"mobileIsEmergencyTelForSettledOverdue7UserWith1th\": false,\n" +
                "\t\t\"emergencyTelIsSettledOverdue7UserWith1th\": false,\n" +
                "\t\t\"emergencyTelIsEmergencyTelForSettledOverdue7UserWith1th\": false,\n" +
                "\t\t\"emergencyTelIsSettledOverdue7UserWith2th\": false,\n" +
                "\t\t\"emergencyTelIsEmergencyTelForSettledOverdue7UserWith2th\": false,\n" +
                "\t\t\"mobileIsEmergencyTelForSettledOverdue7UserWith2th\": false,\n" +
                "\t\t\"emergencyTelIsEmergencyTelForSettledOverdue7UserWith3Or4th\": false\n" +
                "\t}";

        facts.add(JsonUtil.toObject(str1,BlackListUserCheckModel.class));

        str1 = " {\n" +
                "\t\t\"provinceInEarthquakeArea\": false,\n" +
                "\t\t\"cityInEarthquakeArea\": false\n" +
                "\t}";

        facts.add(JsonUtil.toObject(str1,SpecialModel.class));

        str1 = "{\n" +
                "\t\t\"applyTimeHour\": 14,\n" +
                "\t\t\"currentBorrowCount\": 1,\n" +
                "\t\t\"historySubmitCount\": 0,\n" +
                "\t\t\"diffMinutesOfUserCreateTimeAndOrderSubmitTime\": 14,\n" +
                "\t\t\"diffMinutesOfStepOne2StepTwo\": 1,\n" +
                "\t\t\"borrowingPurpose\": \"Modal Usaha\"\n" +
                "\t}";


        facts.add(JsonUtil.toObject(str1,LoanInfo.class));
        str1 = "{\n" +
                "\t\t\"companyAddressNotBelongToJarkat\": false\n" +
                "\t}";

        facts.add(JsonUtil.toObject(str1,SpecifiedProduct100RMBModel.class));

        str1= " {\n" +
                "\t\t\"product600Score\": 484.544,\n" +
                "\t\t\"product100Score\": 709.078,\n" +
                "\t\t\"product50Score\": null,\n" +
                "\t\t\"product600ScoreV2\": 160.312\n" +
                "\t}";

        facts.add(JsonUtil.toObject(str1,ModelScoreResult.class));

        return facts;
    }

    public void allRuleTest() {
        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();
        Optional<KeyConstant> keyConstant = ruleService.getKeyConstants(allRules);
        System.err.println(keyConstant.get().getSmsRuleBody());
        String sms = "terlambat asdsd 10 hari";
        Arrays.asList(keyConstant.get().getSmsRuleBody().split("#")).stream().forEach(elem -> System.err.println(RuleUtils.extractNumber(sms, elem)));
    }

    public void testRiskScore() {
        List<Object> facts = new ArrayList<>();
        ScoreModel model = new ScoreModel();
        model.setGojekVerified(true);
        model.setEcommerceTicketBankCredit(40L);
        model.setMobileLanguage("in");
        model.setGender(1);
        model.setPhoneBrand("huawei");
        model.setAppCount(60L);
        model.setHasDrivingLicense(true);
        model.setPositionName("Guru");
        model.setAcademic("Sekolah Menengah Atas");
        model.setBorrowingPurpose("Pernikahan");
        model.setOrderProvince("kepulauan riau");
        model.setBigDirectSameForOrderAndHome(true);
        model.setBankName("BCA");
        model.setPeriodOfCreateAndCommitTime(40L);

        List<ScoreTemplate> list = scoreTemplateDao.getAllAvailableTemplate();
        RiskScoreCondition condition = new RiskScoreCondition();
        Map<String, ScoreTemplate> templateMap = list.stream().collect(Collectors.toMap(ScoreTemplate::getThresholdName, Function.identity()));
        condition.setScoreTemplateMap(templateMap);
        facts.add(model);
        facts.add(condition);
        List<ScoreRuleResult> resultList =  ruleService.executeRiskScore(FlowEnum.RISK_SCORE,facts);
        List<OrderScoreDetail> scoreDetalList = resultList.stream().map(elem->{
            OrderScoreDetail detail = new OrderScoreDetail();
            detail.setOrderNo("zxc-test-orderNo");
            detail.setUserUuid("zxc-test");
            detail.setModelName(elem.getModelName());
            detail.setVariableName(elem.getVariableName());
            detail.setVariableThresholdName(elem.getVariableThresholdName());
            detail.setScore(elem.getScore());
            detail.setRealValue(elem.getRealValue());
            detail.setCreateTime(new Date());
            detail.setUpdateTime(new Date());
            detail.setUuid(UUIDGenerateUtil.uuid());
            detail.setVersion(list.get(0).getVersion());
            return detail;
        }).collect(Collectors.toList());

        BigDecimal totalScore = resultList.stream().map(elem->elem.getScore()).reduce(BigDecimal.ZERO,BigDecimal::add);

        OrderScore orderScore = new OrderScore();
        orderScore.setOrderNo("zxc-test-orderNo");
        orderScore.setUserUuid("zxc-test");
        orderScore.setModelName(resultList.get(0).getModelName());
        orderScore.setVersion(list.get(0).getVersion());
        orderScore.setScorePass(new BigDecimal("490").compareTo(totalScore)<0?1:0);
        orderScore.setRulePass(0);
        orderScore.setManualReview(1);
        orderScore.setTotalScore(totalScore.add(new BigDecimal("479.290")));
        orderScore.setUuid(UUIDGenerateUtil.uuid());
        orderScore.setCreateTime(new Date());
        orderScore.setUpdateTime(new Date());


        //插入明细
        orderScoreDetailDao.addScoreDetail(scoreDetalList);
        //插入概要
        orderScoreDao.insert(orderScore);
    }

    public List<Object>  buildReborrwoRule(){
        List<Object> facts = new ArrayList<>();
        LastLoan lastLoan = JsonUtil.toObject("{\"overdueDays\":-3,\"currentBorrowCount\":2}",LastLoan.class);
        facts.add(lastLoan);

        ContactInfo contactInfo = JsonUtil.toObject("{\"phoneCount\":90,\"sensitiveWordCount\":0,\"interrelatedWordCount\":0,\"relativeWordCount\":0,\"relativeWordRejectedByProbability\":true," +
                "\"firstLinkManNotIn\":false,\"secondLinkManNotIn\":false,\"firstLinkManNumber\":\"81908780048\",\"secondLinkManNumber\":\"87883316892\"}",ContactInfo.class);
        facts.add(contactInfo);

        UserCallRecordsModel callRecord = JsonUtil.toObject("\n" +
                "{\n" +
                "\t\"diffTime\": 133,\n" +
                "\t\"recent180Time\": 35173,\n" +
                "\t\"recent90Time\": 26788,\n" +
                "\t\"recent30Time\": 1954,\n" +
                "\t\"recent180Count\": 176,\n" +
                "\t\"recent90Count\": 98,\n" +
                "\t\"recent30Count\": 33,\n" +
                "\t\"recent180InTime\": 18353,\n" +
                "\t\"recent90InTime\": 11905,\n" +
                "\t\"recent30InTime\": 1946,\n" +
                "\t\"recent180InCount\": 124,\n" +
                "\t\"recent90InCount\": 75,\n" +
                "\t\"recent30InCount\": 32,\n" +
                "\t\"recent180OutTime\": 16820,\n" +
                "\t\"recent90OutTime\": 14883,\n" +
                "\t\"recent30OutTime\": 8,\n" +
                "\t\"recent180OutCount\": 52,\n" +
                "\t\"recent90OutCount\": 23,\n" +
                "\t\"recent30OutCount\": 1,\n" +
                "\t\"firstContactDiffDay\": null,\n" +
                "\t\"secondContactDiffDay\": 109,\n" +
                "\t\"lastContactDiffDay\": 1,\n" +
                "\t\"recent90InRate\": 0.25167784,\n" +
                "\t\"recent90OutRate\": 0.19127516,\n" +
                "\t\"recent30InRate\": 0.3478261,\n" +
                "\t\"recent30OutRate\": 0.10869565,\n" +
                "\t\"recent180InNoCount\": 237,\n" +
                "\t\"recent90InNoCount\": 166,\n" +
                "\t\"recent30InNoCount\":50,\n" +
                "\t\"recent180InNoRate\": 0.6565097,\n" +
                "\t\"recent90InNoRate\": 0.6887967,\n" +
                "\t\"recent30InNoRate\": 0.6097561,\n" +
                "\t\"nightCallRate\": 0.02173913,\n" +
                "\t\"recent180CallRate\": 0.648,\n" +
                "\t\"recent90CallRate\": 0.67114097,\n" +
                "\t\"recent30CallRate\": 0.6413044,\n" +
                "\t\"recent30NotConnectedCallCount\": 59,\n" +
                "\t\"hasContact\": true,\n" +
                "\t\"contacts\": 9,\n" +
                "\t\"contactsRate\": 0.1,\n" +
                "\t\"recent90CallRelaCount\": 0,\n" +
                "\t\"recent30CallRelaCount\": 0,\n" +
                "\t\"recent90NoCallDay\": 47,\n" +
                "\t\"recent30NoCallDay\": 14,\n" +
                "\t\"recent180NoCallDay\": 105,\n" +
                "\t\"firstCall180Time\": 0,\n" +
                "\t\"firstCall180Count\": 0,\n" +
                "\t\"firstCall90Time\": 0,\n" +
                "\t\"firstCall90Count\": 0,\n" +
                "\t\"firstCall30Time\": 0,\n" +
                "\t\"firstCall30Count\": 0,\n" +
                "\t\"secondCall180Time\": 40,\n" +
                "\t\"secondCall180Count\": 1,\n" +
                "\t\"secondCall90Time\": 0,\n" +
                "\t\"secondCall90Count\": 0,\n" +
                "\t\"secondCall30Time\": 0,\n" +
                "\t\"secondCall30Count\": 0,\n" +
                "\t\"firstLinkManIn\": true,\n" +
                "\t\"secondLinkManIn\": true,\n" +
                "\t\"recent15EveningActiveRatio\": 0.041667,\n" +
                "\t\"recent30CallOutPhones\": 10,\n" +
                "\t\"recent30CallInPhones\": 60,\n" +
                "\t\"recent30DistinctCallNumbers\": 44,\n" +
                "\t\"recent90DistinctCallNumbers\": 120,\n" +
                "\t\"recent30DistinctCallInNumbers\": 41,\n" +
                "\t\"recent90DistinctCallInNumbers\": 114,\n" +
                "\t\"recent90StrangeNumberMissedCallRatio\": 0.000000\n" +
                "}",UserCallRecordsModel.class);

        facts.add(callRecord);

        ShortMessage msg = JsonUtil.toObject("{\n" +
                "\t\"overdueWordsCount\": 9,\n" +
                "\t\"negativeWordsCount\": 79,\n" +
                "\t\"interrelatedWordsCount\": 21,\n" +
                "\t\"totalCount\": 758,\n" +
                "\t\"recent90OverdueMsgDistinctCountByNumber\": 2,\n" +
                "\t\"recent30OverdueMsgDistinctCountByNumber\": 1,\n" +
                "\t\"recent30RejectedMsgCount\": 3,\n" +
                "\t\"recent90TotalMsgCount\": 489,\n" +
                "\t\"recent30TotalMsgCount\": 223,\n" +
                "\t\"recent30TotalMsgW\n" +
                "\tithPhoneCount\": 93,\n" +
                "\t\"recent90TotalMsgWithPhoneCount\": 107,\n" +
                "\t\"diffDaysForEarliestMsgAndApplyTime\": 149,\n" +
                "\t\"recent30OverdueMsgTotalCount\": 2,\n" +
                "\t\"recent15TotalMsgWithPhoneCount\": 22,\n" +
                "\t\"morethan15Count\": 1,\n" +
                "\t\"lessthan15AndMoreThan10Count\": 0,\n" +
                "\t\"smsOverdueMaxDays\": 33\n" +
                "}",ShortMessage.class);
        facts.add(msg);

        InstalledAppInfo appInfo = JsonUtil.toObject("{\n" +
                "\t\"appForLoanCount\": 3,\n" +
                "\t\"appForLoanRatio\": 0.1250,\n" +
                "\t\"incrementalAppForLoanCount\": 0,\n" +
                "\t\"appForLoanRatioChange\": 0.0096,\n" +
                "\t\"hasLatestOrder\": true,\n" +
                "\t\"totalApps\": 24\n" +
                "}",InstalledAppInfo.class);
        facts.add(appInfo);

        FaceBookModel faceBookModel = JsonUtil.toObject("{\n" +
                "\t\"totalCommentCount\": 60,\n" +
                "\t\"recent2MonthCommentCount\": 2,\n" +
                "\t\"currentMonthCommentCount\": 0,\n" +
                "\t\"totalLikesCount\": 286,\n" +
                "\t\"recent2MonthLikesCount\": 55,\n" +
                "\t\"currentMonthLikesCount\": 0,\n" +
                "\t\"totalPostCount\": 24,\n" +
                "\t\"recent2MonthPostCount\": 5,\n" +
                "\t\"currentMonthPostCount\": 0,\n" +
                "\t\"monthAverageCommentCount\": 15.000000,\n" +
                "\t\"monthAverageLikesCount\": 71.500000,\n" +
                "\t\"monthAveragePostCount\": 6.000000,\n" +
                "\t\"monthsWithPost\": 6,\n" +
                "\t\"monthsWithoutPost\": 2,\n" +
                "\t\"academicDegreeNotSame\": null,\n" +
                "\t\"diffDaysBetweenWorkStartAndOrderApply\": null,\n" +
                "\t\"companyNameNotContain\": null\n" +
                "}",FaceBookModel.class);
        facts.add(faceBookModel);

        DeviceModel deviceModel = JsonUtil.toObject("{\n" +
                "\t\"deviceId\": \"357202071849096\",\n" +
                "\t\"matchedForOthersCount\": 0,\n" +
                "\t\"matchedIMEIForOthersCount\": 0,\n" +
                "\t\"sameIpApplyCount\": 1,\n" +
                "\t\"mobileLanguage\": \"in\",\n" +
                "\t\"netType\": \"wifi\",\n" +
                "\t\"pictureNumber\": \"1699\",\n" +
                "\t\"totalMemory\": 1.85,\n" +
                "\t\"totalSpace\": 11.09,\n" +
                "\t\"isJailBreak\": false,\n" +
                "\t\"phoneBrand\": \"samsung\",\n" +
                "\t\"isIOS\": false\n" +
                "}",DeviceModel.class);
        facts.add(deviceModel);

        BlackListUserCheckModel blackList = JsonUtil.toObject("{\n" +
                "\t\"idCardNoInOverdue15BlackList\": null,\n" +
                "\t\"mobileInOverdue15BlackList\": false,\n" +
                "\t\"emergencyTelInOverdue15BlackListEmergencyTel\": null,\n" +
                "\t\"bankcardNoInOve\n" +
                "\trdue15BlackList\": null,\n" +
                "\t\"imeiInOverdue15BlackList\": null,\n" +
                "\t\"mobileIsOverdue15BlackListEmergencyTel\": null,\n" +
                "\t\"emergencyTelInOverdue15BlackList\": null,\n" +
                "\t\"mobileInOverdue15BlackListContacts\": null,\n" +
                "\t\"mobileInOverdue15BlackListCallRecords\": null,\n" +
                "\t\"mobileInOverdue15BlackListShortMsg\": null,\n" +
                "\t\"contactInOverdue15BlackList\": null,\n" +
                "\t\"callRecordInOverdue15BlackList\": null,\n" +
                "\t\"contactInOverdue15Count\": 0,\n" +
                "\t\"callRecordInOverdue15Count\": 0,\n" +
                "\t\"mobileIsFraudUserEmergencyTel\": null,\n" +
                "\t\"mobileInFraudUserCallRecordsCount\": null,\n" +
                "\t\"imeiInFraudUser\": false,\n" +
                "\t\"mobileInFraudUser\": null,\n" +
                "\t\"idCardNoInFraudUser\": null,\n" +
                "\t\"hitFraudUserInfo\": false,\n" +
                "\t\"deviceIdInOverdue30DaysUser\": false,\n" +
                "\t\"smsContactOverdue15DaysCount\": 0,\n" +
                "\t\"callRecordInOverdue7BlackList\": null,\n" +
                "\t\"imeiInOverdue7BlackList\": null,\n" +
                "\t\"idCardNoInOverdue7BlackList\": null,\n" +
                "\t\"mobileInOverdue7BlackList\": null,\n" +
                "\t\"bankcardInOverdue7BlackList\": null,\n" +
                "\t\"emergencyTelInOverdue7BlackListEmergencyTel\": null,\n" +
                "\t\"mobileInOverdue7BlackListEmergencyTel\": null,\n" +
                "\t\"emergencyTelInOverdue7BlackList\": null,\n" +
                "\t\"hitSensitiveUserInfo\": false\n" +
                "}",BlackListUserCheckModel.class);

        facts.add(blackList);

        LoanHistory history = JsonUtil.toObject("{\n" +
                "\t\"applyCount\": 1,\n" +
                "\t\"successLoanCount\": 1,\n" +
                "\t\"successRatio\": 1.0000,\n" +
                "\t\"overdueCount\": 0,\n" +
                "\t\"overdueSuccessLoanRatio\": 0.0000,\n" +
                "\t\"averageOverdueDays\": -3.0000,\n" +
                "\t\"averageIntervalDays\": 0.0000,\n" +
                "\t\"overdue1Count\": 0,\n" +
                "\t\"overdue2Count\": 0,\n" +
                "\t\"overdue3Count\": 0,\n" +
                "\t\"overdue4Count\": 0,\n" +
                "\t\"overdue5Count\": 0,\n" +
                "\t\"overdue6Count\": 0,\n" +
                "\t\"overdueMoreThan6Count\": 0,\n" +
                "\t\"overdue1Ratio\": 0,\n" +
                "\t\"overdue2Ratio\": 0,\n" +
                "\t\"overdue3Ratio\": 0,\n" +
                "\t\"overdue4Ratio\": 0,\n" +
                "\t\"overdue5Ratio\": 0,\n" +
                "\t\"overdue6Ratio\":0,\n" +
                "\t\"overdueMoreThan6Ratio\": 0\n" +
                "}",LoanHistory.class);
        facts.add(history);

        SpecialModel specialModel = JsonUtil.toObject("{\n" +
                "\t\"provinceInEarthquakeArea\": false,\n" +
                "\t\"cityInEarthquakeArea\": false\n" +
                "}",SpecialModel.class);
        facts.add(specialModel);
        return facts;
    }


    public List<Object> buildFirstBorrowingFacts(){
        List<Object> facts = new ArrayList<>();
        InnerBlackList innerBlackList = JsonUtil.toObject("{\n" +
                "\t\t\"isInYQGBlackListSize\": 0,\n" +
                "\t\t\"isIMEIInYQGBlackList\":false\n" +
                "\t}",InnerBlackList.class);
        facts.add(innerBlackList);
        RUserInfo rUserInfo = JsonUtil.toObject("{\n" +
                "\t\t\"idCard\": \"3209330303860004\",\n" +
                "\t\t\"age\": 32,\n" +
                "\t\t\"homeAddress\": \"Kabupaten Cirebon\",\n" +
                "\t\t\"userRole\": 2,\n" +
                "\t\t\"schoolAddress\": null,\n" +
                "\t\t\"companyAddress\": \"Kota Jakarta Selatan\",\n" +
                "\t\t\"orderAddress\": \"Cirebon\",\n" +
                "\t\t\"isCompanyTelphoneNotInJarkat\": false,\n" +
                "\t\t\"homeAddressNotBelongToJarkat\": false,\n" +
                "\t\t\"schoolAddressNotBelongToJarkat\": null,\n" +
                "\t\t\"companyAddressNotBelongToJarkat\": false,\n" +
                "\t\t\"orderAddressNotBelongToJarkat\": true,\n" +
                "\t\t\"yituScore\": 88.26102007953853,\n" +
                "\t\t\"facePlusPlusScore\": null,\n" +
                "\t\t\"advanceVerifyResult\": {\n" +
                "\t\t\t\"advanceVerifyResultType\": \"JUXINLI_MATCH_SUCCESS\",\n" +
                "\t\t\t\"desc\": \"聚信立实名认证已经认证成功\"\n" +
                "\t\t},\n" +
                "\t\t\"sex\": 1,\n" +
                "\t\t\"hasDriverLicense\": false,\n" +
                "\t\t\"companyAddrProvice\": \"DKI JAKARTA\",\n" +
                "\t\t\"companyAddrCity\": \"Kota Jakarta Selatan\",\n" +
                "\t\t\"homeAddrProvice\": \"JAWA BARAT\",\n" +
                "\t\t\"homeAddrCity\": \"Kabupaten Cirebon\",\n" +
                "\t\t\"childrenAmount\": 0,\n" +
                "\t\t\"academic\": \"Sekolah Menengah Atas\",\n" +
                "\t\t\"maritalStatus\": 0,\n" +
                "\t\t\"thirdType\": null,\n" +
                "\t\t\"mobileAsEmergencyTelCount\": 0,\n" +
                "\t\t\"hasInsuranceCard\": true,\n" +
                "\t\t\"hasFamilyCard\": true,\n" +
                "\t\t\"hasTaxNumber\": true,\n" +
                "\t\t\"positionName\": \"Pekerja\",\n" +
                "\t\t\"religionName\": \"Islam\",\n" +
                "\t\t\"monthlyIncome\": 2200000,\n" +
                "\t\t\"firstLinkmanExists\": false,\n" +
                "\t\t\"emailExists\": true,\n" +
                "\t\t\"workAddressExists\": false,\n" +
                "\t\t\"orderAddressExists\": true,\n" +
                "\t\t\"homeAddressExists\": false\n" +
                "\t}",RUserInfo.class);
        facts.add(rUserInfo);

        ContactInfo contactInfo = JsonUtil.toObject("{\n" +
                "\t\t\"phoneCount\": 136,\n" +
                "\t\t\"sensitiveWordCount\": 0,\n" +
                "\t\t\"interrelatedWordCount\": 0,\n" +
                "\t\t\"relativeWordCount\": 1,\n" +
                "\t\t\"relativeWordRejectedByProbability\": false,\n" +
                "\t\t\"firstLinkManNotIn\": false,\n" +
                "\t\t\"secondLinkManNotIn\": false,\n" +
                "\t\t\"firstLinkManNumber\": \"81214229343\",\n" +
                "\t\t\"secondLinkManNumber\": \"83824512566\"\n" +
                "\t}",ContactInfo.class);
        facts.add(contactInfo);

        UserCallRecordsModel userCall = JsonUtil.toObject("{\n" +
                "\t\t\"diffTime\": 253,\n" +
                "\t\t\"recent180Time\": 13505,\n" +
                "\t\t\"recent90Time\": 7452,\n" +
                "\t\t\"recent30Time\": 2663,\n" +
                "\t\t\"recent180Count\": 208,\n" +
                "\t\t\"recent90Count\": 130,\n" +
                "\t\t\"recent30Count\": 46,\n" +
                "\t\t\"recent180InTime\": 10673,\n" +
                "\t\t\"recent90InTime\": 6129,\n" +
                "\t\t\"recent30InTime\": 2155,\n" +
                "\t\t\"recent180InCount\": 164,\n" +
                "\t\t\"recent90InCount\": 101,\n" +
                "\t\t\"recent30InCount\": 34,\n" +
                "\t\t\"recent180OutTime\": 2832,\n" +
                "\t\t\"recent90OutTime\": 1323,\n" +
                "\t\t\"recent30OutTime\": 508,\n" +
                "\t\t\"recent180OutCount\": 44,\n" +
                "\t\t\"recent90OutCount\": 29,\n" +
                "\t\t\"recent30OutCount\": 12,\n" +
                "\t\t\"firstContactDiffDay\": null,\n" +
                "\t\t\"secondContactDiffDay\": 18,\n" +
                "\t\t\"lastContactDiffDay\": 0,\n" +
                "\t\t\"recent90InRate\": 0.49753696,\n" +
                "\t\t\"recent90OutRate\": 0.33004925,\n" +
                "\t\t\"recent30InRate\": 0.4473684,\n" +
                "\t\t\"recent30OutRate\": 0.27631578,\n" +
                "\t\t\"recent180InNoCount\": 61,\n" +
                "\t\t\"recent90InNoCount\": 35,\n" +
                "\t\t\"recent30InNoCount\": 21,\n" +
                "\t\t\"recent180InNoRate\": 0.2711111,\n" +
                "\t\t\"recent90InNoRate\": 0.25735295,\n" +
                "\t\t\"recent30InNoRate\": 0.38181818,\n" +
                "\t\t\"nightCallRate\": 0.0,\n" +
                "\t\t\"recent180CallRate\": 0.38643068,\n" +
                "\t\t\"recent90CallRate\": 0.3596059,\n" +
                "\t\t\"recent30CallRate\": 0.39473686,\n" +
                "\t\t\"recent30NotConnectedCallCount\": 30,\n" +
                "\t\t\"hasContact\": true,\n" +
                "\t\t\"contacts\": 13,\n" +
                "\t\t\"contactsRate\": 0.09558824,\n" +
                "\t\t\"recent90CallRelaCount\": 1,\n" +
                "\t\t\"recent30CallRelaCount\": 1,\n" +
                "\t\t\"recent90NoCallDay\": 28,\n" +
                "\t\t\"recent30NoCallDay\": 8,\n" +
                "\t\t\"recent180NoCallDay\": 77,\n" +
                "\t\t\"firstCall180Time\": 0,\n" +
                "\t\t\"firstCall180Count\": 0,\n" +
                "\t\t\"firstCall90Time\": 0,\n" +
                "\t\t\"firstCall90Count\": 0,\n" +
                "\t\t\"firstCall30Time\": 0,\n" +
                "\t\t\"firstCall30Count\": 0,\n" +
                "\t\t\"secondCall180Time\": 462,\n" +
                "\t\t\"secondCall180Count\": 4,\n" +
                "\t\t\"secondCall90Time\": 462,\n" +
                "\t\t\"secondCall90Count\": 4,\n" +
                "\t\t\"secondCall30Time\": 234,\n" +
                "\t\t\"secondCall30Count\": 1,\n" +
                "\t\t\"firstLinkManIn\": true,\n" +
                "\t\t\"secondLinkManIn\": true,\n" +
                "\t\t\"recent15EveningActiveRatio\": 0.000000,\n" +
                "\t\t\"recent30CallOutPhones\": 21,\n" +
                "\t\t\"recent30CallInPhones\": 42,\n" +
                "\t\t\"recent30DistinctCallNumbers\": 42,\n" +
                "\t\t\"recent90DistinctCallNumbers\": 81,\n" +
                "\t\t\"recent30DistinctCallInNumbers\": 34,\n" +
                "\t\t\"recent90DistinctCallInNumbers\": 66,\n" +
                "\t\t\"recent90StrangeNumberMissedCallRatio\": 0.000000\n" +
                "\t}",UserCallRecordsModel.class);


        facts.add(userCall);

        ShortMessage msg = JsonUtil.toObject("{\n" +
                "\t\t\"overdueWordsCount\": 0,\n" +
                "\t\t\"negativeWordsCount\": 0,\n" +
                "\t\t\"interrelatedWordsCount\": 1,\n" +
                "\t\t\"totalCount\": 38,\n" +
                "\t\t\"recent90OverdueMsgDistinctCountByNumber\": 0,\n" +
                "\t\t\"recent30OverdueMsgDistinctCountByNumber\": 0,\n" +
                "\t\t\"recent30RejectedMsgCount\": 0,\n" +
                "\t\t\"recent90TotalMsgCount\": 33,\n" +
                "\t\t\"recent30TotalMsgCount\": 13,\n" +
                "\t\t\"recent30TotalMsgWithPhoneCount\": 9,\n" +
                "\t\t\"recent90TotalMsgWithPhoneCount\": 20,\n" +
                "\t\t\"diffDaysForEarliestMsgAndApplyTime\": 253,\n" +
                "\t\t\"recent30OverdueMsgTotalCount\": 0,\n" +
                "\t\t\"recent15TotalMsgWithPhoneCount\": 4,\n" +
                "\t\t\"morethan15Count\": 0,\n" +
                "\t\t\"lessthan15AndMoreThan10Count\": 0,\n" +
                "\t\t\"smsOverdueMaxDays\": 0\n" +
                "\t}",ShortMessage.class);
        facts.add(msg);

        InstalledAppInfo appInfo = JsonUtil.toObject("{\n" +
                "\t\t\"appForLoanCount\": 5,\n" +
                "\t\t\"appForLoanRatio\": 0.1724,\n" +
                "\t\t\"incrementalAppForLoanCount\": null,\n" +
                "\t\t\"appForLoanRatioChange\": null,\n" +
                "\t\t\"hasLa\n" +
                "\t\ttestOrder\": false,\n" +
                "\t\t\"totalApps\": 29\n" +
                "\t}",InstalledAppInfo.class);

        facts.add(appInfo);
        DeviceModel deviceModel=JsonUtil.toObject("{\n" +
                "\t\t\"deviceId\": \"353609067385055\",\n" +
                "\t\t\"matchedForOthersCount\": 0,\n" +
                "\t\t\"matchedIMEIForOthersCount\": 0,\n" +
                "\t\t\"sameIpApplyCount\": 1,\n" +
                "\t\t\"mobileLanguage\": \"in\",\n" +
                "\t\t\"netType\": \"4G\",\n" +
                "\t\t\"pictureNumber\": \"841\",\n" +
                "\t\t\"totalMemory\": 1.73,\n" +
                "\t\t\"totalSpace\": 11.57,\n" +
                "\t\t\"isJailBreak\": false,\n" +
                "\t\t\"phoneBrand\": \"Sony\",\n" +
                "\t\t\"isIOS\": false\n" +
                "\t}",DeviceModel.class);
        facts.add(deviceModel);

        FaceBookModel faceBookModel = JsonUtil.toObject("{\n" +
                "\t\t\"totalCommentCount\": 63,\n" +
                "\t\t\"recent2MonthCommentCount\": 0,\n" +
                "\t\t\"currentMonthCommentCount\": 0,\n" +
                "\t\t\"totalLikesCount\": 818,\n" +
                "\t\t\"recent2MonthLikesCount\": 0,\n" +
                "\t\t\"currentMonthLikesCount\": 0,\n" +
                "\t\t\"totalPostCount\": 15,\n" +
                "\t\t\"recent2MonthPostCount\": 0,\n" +
                "\t\t\"currentMonthPostCount\": 0,\n" +
                "\t\t\"monthAverageCommentCount\": 21.000000,\n" +
                "\t\t\"monthAverageLikesCount\": 204.500000,\n" +
                "\t\t\"monthAveragePostCount\": 3.750000,\n" +
                "\t\t\"monthsWithPost\": 6,\n" +
                "\t\t\"monthsWithoutPost\": 2,\n" +
                "\t\t\"academicDegreeNotSame\": null,\n" +
                "\t\t\"diffDaysBetweenWorkStartAndOrderApply\": null,\n" +
                "\t\t\"companyNameNotContain\": null\n" +
                "\t}",FaceBookModel.class);
        facts.add(faceBookModel);

        BlackListUserCheckModel blackListUserCheckModel = JsonUtil.toObject("{\n" +
                "\t\t\"idCardNoInOverdue15BlackList\": false,\n" +
                "\t\t\"mobileInOverdue15BlackList\": null,\n" +
                "\t\t\"emergencyTelInOverdue15BlackListEmergencyTel\": false,\n" +
                "\t\t\"bankcardNoInOverdue15BlackList\": false,\n" +
                "\t\t\"imeiInOverdue15BlackList\": false,\n" +
                "\t\t\"mobileIsOverdue15BlackListEmergencyTel\": false,\n" +
                "\t\t\"emergencyTelInOverdue15BlackList\": false,\n" +
                "\t\t\"mobileInOverdue15BlackListContacts\": false,\n" +
                "\t\t\"mobileInOverdue15BlackListCallRecords\": false,\n" +
                "\t\t\"mobileInOverdue15BlackListShortMsg\": false,\n" +
                "\t\t\"contactInOverdue15BlackList\": false,\n" +
                "\t\t\"callRecordInOverdue15BlackList\": false,\n" +
                "\t\t\"contactInOverdue15Count\": 0,\n" +
                "\t\t\"callRecordInOverdue15Count\": 0,\n" +
                "\t\t\"mobileIsFraudUserEmergencyTel\": false,\n" +
                "\t\t\"mobileInFraudUserCallRecordsCount\": 0,\n" +
                "\t\t\"imeiInFraudUser\": null,\n" +
                "\t\t\"mobileInFraudUser\": false,\n" +
                "\t\t\"idCardNoInFraudUser\": false,\n" +
                "\t\t\"hitFraudUserInfo\": false,\n" +
                "\t\t\"deviceIdInOverdue30DaysUser\": false,\n" +
                "\t\t\"smsContactOverdue15DaysCount\": 0,\n" +
                "\t\t\"callRecordInOverdue7BlackList\": false,\n" +
                "\t\t\"imeiInOverdue7BlackList\": false,\n" +
                "\t\t\"idCardNoInOverdue7BlackList\": false,\n" +
                "\t\t\"mobileInOverdue7BlackList\": false,\n" +
                "\t\t\"bankcardInOverdue7BlackList\": false,\n" +
                "\t\t\"emergencyTelInOverdue7BlackListEmergencyTel\": false,\n" +
                "\t\t\"mobileInOverdue7BlackListEmergencyTel\": false,\n" +
                "\t\t\"emergencyTelInOverdue7BlackList\": false,\n" +
                "\t\t\"hitSensitiveUserInfo\": false\n" +
                "\t}",BlackListUserCheckModel.class);
        facts.add(blackListUserCheckModel);

        SpecialModel specialModel = JsonUtil.toObject("{\n" +
                "\t\t\"provinceInEarthquakeArea\": false,\n" +
                "\t\t\"cityInEarthquakeArea\": false\n" +
                "\t}",SpecialModel.class);
        facts.add(specialModel);
        return facts;
    }



    @Autowired
    LabelExecutionChain labelExecutionChain;


    public void testWithPrdData(){

        List<Object> facts = buildFirstBorrowingFacts();


        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();


        OrdOrder order = ordService.getOrderByOrderNo("011902061311091771");
//        Optional<RuleConditionModel> conditionModel =  ruleApplicationService.buildRuleCondition(allRules,order);
//        if(conditionModel.isPresent()){
//            facts.add(conditionModel.get());
//        }
//        List<RuleResult> resultList = ruleService
//                .executeRules(RuleSetEnum.FIRST_BORROWING, facts, allRules);

        try {
            RuleSetExecutedResult result = labelExecutionChain.execute(order, allRules, facts);
            log.info("result: "+ JsonUtils.serialize(result));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void testReborrowingFlow(){
        List<Object> facts = new ArrayList<>();
        String str1 = "{\n" +
                "\t\"overdueDays\": 1,\n" +
                "\t\"borrowingAmount\": 1200000.00,\n" +
                "\t\"lastLoanModelScore\":{\n" +
                "\t   \"product600Score\":400,\n" +
                "\t   \"product100Score\":100,\n" +
                "\t   \"product50Score\":null,\n" +
                "\t   \"product600ScoreV2\":500\n" +
                "\t},\n" +
                "\t\"diffHoursBetweenFirstCollectionAndRefundTime\":3,\n" +
                "\t\"loanPassType\":1,\n" +
                "\t\"noCollectionRecord\":true,\n" +
                "\t\"intervalDays\":3\n" +
                "}";
        LastLoan lastLoan = JsonUtil.toObject(str1,LastLoan.class);
        facts.add(lastLoan);

        str1 = " {\n" +
                "\t\"appForLoanCount\": 1,\n" +
                "\t\"appForLoanRatio\": 0.0130,\n" +
                "\t\"incrementalAppForLoanCount\": 1,\n" +
                "\t\"appForLoanRatioChange\": 0.0130,\n" +
                "\t\"hasLatestOrder\": true,\n" +
                "\t\"totalApps\": 77,\n" +
                "\t\"diffDaysBetweenLatestUpdateTimeAndCommitTime\": 0,\n" +
                "\t\"diffDaysBetweenEarliestUpdateTimeAndCommitTime\": 3843,\n" +
                "\t\"diffDaysBetweenForEarliestAndLatestUpdateTime\": 3843,\n" +
                "\t\"appCountForNews\": 0,\n" +
                "\t\"appCountForEnterprise\": 2,\n" +
                "\t\"appCountForBeauty\": 0,\n" +
                "\t\"appCountForGambling\": 1,\n" +
                "\t\"appCountForCreditCard\": 2,\n" +
                "\t\"appCountForBeautyPicture\": 0,\n" +
                "\t\"appCountForPhotography\": 1,\n" +
                "\t\"appCountForEcommerce\": 3,\n" +
                "\t\"appCountForGame\": 0,\n" +
                "\t\"appCountForSocial\": 9,\n" +
                "\t\"appCountForTaxBPJS\": 0,\n" +
                "\t\"appCountForBank\": 2,\n" +
                "\t\"appCountForCinema\": 2,\n" +
                "\t\"appCountForTicket\": 1\n" +
                "}";
        InstalledAppInfo appInfo = JsonUtil.toObject(str1,InstalledAppInfo.class);
        facts.add(appInfo);

        str1 = "{\n" +
                "\t\"totalDistanceFor180\": 50.952,\n" +
                "\t\"totalFareFor180\": 68000,\n" +
                "\t\"totalCountFor180\": 7,\n" +
                "\t\"totalPickUpAddressCountFor180\": 3,\n" +
                "\t\"totalTaxiTypeCountFor180\": 1,\n" +
                "\t\"totalSpecialTaxiTypeCountFor180\": 0,\n" +
                "\t\"totalDistanceFor30\": 50.952,\n" +
                "\t\"totalFareFor30\": 68000,\n" +
                "\t\"totalCountFor30\": 7,\n" +
                "\t\"totalPickUpAddressCountFor30\": 3,\n" +
                "\t\"totalTaxiTypeCountFor30\": 1,\n" +
                "\t\"totalSpecialTaxiTypeCountFor30\": 0,\n" +
                "\t\"diffDaysForFirstRideAndApplyTime\": 18,\n" +
                "\t\"diffDaysForLastRideAndApplyTime\": 6,\n" +
                "\t\"averageFarePerMonth\": 68000.00,\n" +
                "\t\"averageRideCountPerMonth\": 7.00,\n" +
                "\t\"averageFare\": 9714.29,\n" +
                "\t\"averageDistance\": 7.2789,\n" +
                "\t\"maxFare\": 17000,\n" +
                "\t\"maxDistance\": 10.852,\n" +
                "\t\"paymentMethodCount\": 1,\n" +
                "\t\"cashPayCount\": 0,\n" +
                "\t\"homeAddrBoolean\": true,\n" +
                "\t\"schoolAddrBoolean\": true,\n" +
                "\t\"companyAddrBoolean\": false,\n" +
                "\t\"diffDaysForLatestRideContainHomeAndApplyTime\": null,\n" +
                "\t\"diffDaysForLatestRideContainSchoolAndApplyTime\": null,\n" +
                "\t\"diffDaysForLatestRideContainCompanyAndApplyTime\": 9,\n" +
                "\t\"diffDaysForFirstRideContainHomeAndApplyTime\": 0,\n" +
                "\t\"diffDaysForFirstRideContainSchoolAndApplyTime\": 0,\n" +
                "\t\"diffDaysForFirstRideContainCompanyAndApplyTime\": 9,\n" +
                "\t\"personBaseInfo\": {\n" +
                "\t\t\"phone\": \"6281310827710\",\n" +
                "\t\t\"email\": \"mandewsopian@gmail.com\"\n" +
                "\t},\n" +
                "\t\"mobilePhoneNotSame\": false,\n" +
                "\t\"emailNotSame\": true,\n" +
                "\t\"matchCompanyNum\": 0\n" +
                "}";

        GojekModel gojekModel = JsonUtil.toObject(str1,GojekModel.class);
        facts.add(gojekModel);
        str1 = "{\n" +
                "\t\"deviceId\": \"\",\n" +
                "\t\"matchedForOthersCount\": null,\n" +
                "\t\"matchedIMEIForOthersCount\": null,\n" +
                "\t\"sameIpApplyCount\": 1,\n" +
                "\t\"mobileLanguage\": \"in\",\n" +
                "\t\"netType\": \"4G\",\n" +
                "\t\"pictureNumber\": \"0\",\n" +
                "\t\"totalMemory\": 1.93,\n" +
                "\t\"totalSpace\": 27.32,\n" +
                "\t\"isJailBreak\": false,\n" +
                "\t\"phoneBrand\": \"samsung\",\n" +
                "\t\"isIOS\": false,\n" +
                "\t\"matchedIMSIForOthersCount\": null,\n" +
                "\t\"matchedCustomDeviceFingerprintWithApps\": 0,\n" +
                "\t\"matchedCustomDeviceFingerprintWithIp\": 0,\n" +
                "\t\"matchedCustomDeviceFingerprintWithPhoneBrand\": 0,\n" +
                "\t\"matchedCustomDeviceFingerprintNotEmptyWithDeviceNameCpuType\": 0,\n" +
                "\t\"matchedCustomDeviceFingerprintWithoutAndroidId\": 0,\n" +
                "\t\"hitSameExtendDeviceCount\": 0\n" +
                "}";
        DeviceModel deviceModel = JsonUtil.toObject(str1,DeviceModel.class);
        facts.add(deviceModel);

        str1 = "{\n" +
                "\t\"idCardNoInOverdue15BlackList\": null,\n" +
                "\t\"mobileInOverdue15BlackList\": false,\n" +
                "\t\"emergencyTelInOverdue15BlackListEmergencyTel\": null,\n" +
                "\t\"bankcardNoInOverdue15BlackList\": null,\n" +
                "\t\"imeiInOverdue15BlackList\": null,\n" +
                "\t\"mobileIsOverdue15BlackListEmergencyTel\": null,\n" +
                "\t\"emergencyTelInOverdue15BlackList\": null,\n" +
                "\t\"mobileInOverdue15BlackListContacts\": null,\n" +
                "\t\"mobileInOverdue15BlackListCallRecords\": null,\n" +
                "\t\"mobileInOverdue15BlackListShortMsg\": null,\n" +
                "\t\"contactInOverdue15BlackList\": null,\n" +
                "\t\"callRecordInOverdue15BlackList\": null,\n" +
                "\t\"contactInOverdue15Count\": 0,\n" +
                "\t\"callRecordInOverdue15Count\": 0,\n" +
                "\t\"mobileIsFraudUserEmergencyTel\": null,\n" +
                "\t\"mobileInFraudUserCallRecordsCount\": null,\n" +
                "\t\"imeiInFraudUser\": false,\n" +
                "\t\"mobileInFraudUser\": null,\n" +
                "\t\"idCardNoInFraudUser\": null,\n" +
                "\t\"hitFraudUserInfo\": false,\n" +
                "\t\"deviceIdInOverdue30DaysUser\": false,\n" +
                "\t\"smsContactOverdue15DaysCount\": null,\n" +
                "\t\"callRecordInOverdue7BlackList\": null,\n" +
                "\t\"imeiInOverdue7BlackList\": null,\n" +
                "\t\"idCardNoInOverdue7BlackList\": null,\n" +
                "\t\"mobileInOverdue7BlackList\": null,\n" +
                "\t\"bankcardInOverdue7BlackList\": null,\n" +
                "\t\"emergencyTelInOverdue7BlackListEmergencyTel\": null,\n" +
                "\t\"mobileInOverdue7BlackListEmergencyTel\": null,\n" +
                "\t\"emergencyTelInOverdue7BlackList\": null,\n" +
                "\t\"hitSensitiveUserInfo\": false,\n" +
                "\t\"hitCollectorBlackUserInfo\": false,\n" +
                "\t\"hitComplaintUserInfo\": false,\n" +
                "\t\"whatsappInOverdue7BlackList\": null,\n" +
                "\t\"whatsappInOverdue7BlackListEmergencyTel\": null,\n" +
                "\t\"whatsappInOverdue7BlackListCallRecord\": null,\n" +
                "\t\"whatsappInOverdue7BlackListContact\": null,\n" +
                "\t\"whatsappInOverdue7BlackListSms\": null,\n" +
                "\t\"emergencyTelInFraudUserEmergencyTel\": null,\n" +
                "\t\"emergencyTelInFraudUserCallRecord\": null,\n" +
                "\t\"emergencyTelInFraudUserContact\": null,\n" +
                "\t\"emergencyTelInFraudUserSms\": null,\n" +
                "\t\"emergencyTelInFraudUserWhatsapp\": null,\n" +
                "\t\"companyTelInOverdue7BlackList\": null,\n" +
                "\t\"companyAddressInOverdue7BlackList\": null,\n" +
                "\t\"homeAddressInOverdue7BlackList\": null,\n" +
                "\t\"companyTelInFraudBlackList\": null,\n" +
                "\t\"companyAddressInFraudBlackList\": null,\n" +
                "\t\"homeAddressInFraudBlackList\": null,\n" +
                "\t\"mobileIsEmergencyTelForUnSettledOverdue7UserWith1th\": null,\n" +
                "\t\"emergencyTelIsUnSettledOverdue7UserWith1th\": null,\n" +
                "\t\"emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith1th\": null,\n" +
                "\t\"emergencyTelIsUnSettledOverdue7UserWith2th\": null,\n" +
                "\t\"emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith2th\": null,\n" +
                "\t\"mobileIsEmergencyTelForUnSettledOverdue7UserWith2th\": null,\n" +
                "\t\"emergencyTelIsEmergencyTelForUnSettledOverdue7UserWith3Or4th\": null,\n" +
                "\t\"mobileIsEmergencyTelForSettledOverdue7UserWith1th\": null,\n" +
                "\t\"emergencyTelIsSettledOverdue7UserWith1th\": null,\n" +
                "\t\"emergencyTelIsEmergencyTelForSettledOverdue7UserWith1th\": null,\n" +
                "\t\"emergencyTelIsSettledOverdue7UserWith2th\": null,\n" +
                "\t\"emergencyTelIsEmergencyTelForSettledOverdue7UserWith2th\": null,\n" +
                "\t\"mobileIsEmergencyTelForSettledOverdue7UserWith2th\": null,\n" +
                "\t\"emergencyTelIsEmergencyTelForSettledOverdue7UserWith3Or4th\": null\n" +
                "}";

        BlackListUserCheckModel checkModel = JsonUtil.toObject(str1,BlackListUserCheckModel.class);
        facts.add(checkModel);

        str1 = "{\n" +
                "\t\"provinceInEarthquakeArea\": false,\n" +
                "\t\"cityInEarthquakeArea\": false\n" +
                "}";

        SpecialModel specialModel = JsonUtil.toObject(str1,SpecialModel.class);
        facts.add(specialModel);

        str1 = "{\n" +
                "\t\"applyCount\": 2,\n" +
                "\t\"successLoanCount\": 2,\n" +
                "\t\"successRatio\": 1.0000,\n" +
                "\t\"overdueCount\": 1,\n" +
                "\t\"overdueSuccessLoanRatio\": 0.5000,\n" +
                "\t\"averageOverdueDays\": -11.0000,\n" +
                "\t\"averageIntervalDays\": 7.0000,\n" +
                "\t\"overdue1Count\": 1,\n" +
                "\t\"overdue2Count\": 0,\n" +
                "\t\"overdue3Count\": 0,\n" +
                "\t\"overdue4Count\": 0,\n" +
                "\t\"overdue5Count\": 0,\n" +
                "\t\"overdue6Count\": 0,\n" +
                "\t\"overdueMoreThan6Count\": 0,\n" +
                "\t\"overdue1Ratio\": 0.5000,\n" +
                "\t\"overdue2Ratio\": 0.0000,\n" +
                "\t\"overdue3Ratio\": 0.0000,\n" +
                "\t\"overdue4Ratio\": 0.0000,\n" +
                "\t\"overdue5Ratio\": 0.0000,\n" +
                "\t\"overdue6Ratio\": 0.0000,\n" +
                "\t\"overdueMoreThan6Ratio\": 0.0000\n" +
                "}";
        LoanHistory loanHistory  = JsonUtil.toObject(str1,LoanHistory.class);
        facts.add(loanHistory);

        str1 = " {\n" +
                "\t\"applyTimeHour\": 14,\n" +
                "\t\"currentBorrowCount\": 3,\n" +
                "\t\"historySubmitCount\": 2,\n" +
                "\t\"diffMinutesOfUserCreateTimeAndOrderSubmitTime\": 72334,\n" +
                "\t\"diffMinutesOfStepOne2StepTwo\": null,\n" +
                "\t\"borrowingPurpose\": \"Modal Usaha\",\n" +
                "\t\"borrowingAmount\": 1200000.00,\n" +
                "\t\"firstBorrowingAmount\": 1200000.00\n" +
                "}";
        LoanInfo loanInfo = JsonUtil.toObject(str1,LoanInfo.class);
        facts.add(loanInfo);

        str1 = "{\n" +
                "\t\"idCard\": null,\n" +
                "\t\"age\": 27,\n" +
                "\t\"homeAddress\": null,\n" +
                "\t\"userRole\": null,\n" +
                "\t\"schoolAddress\": null,\n" +
                "\t\"companyAddress\": null,\n" +
                "\t\"orderAddress\": null,\n" +
                "\t\"isCompanyTelphoneNotInJarkat\": null,\n" +
                "\t\"homeAddressNotBelongToJarkat\": null,\n" +
                "\t\"schoolAddressNotBelongToJarkat\": null,\n" +
                "\t\"companyAddressNotBelongToJarkat\": null,\n" +
                "\t\"orderAddressNotBelongToJarkat\": null,\n" +
                "\t\"companyAddressNotBelongToJarkatNormal\": null,\n" +
                "\t\"companyAddressNotBelongToJarkatIOS\": null,\n" +
                "\t\"yituScore\": null,\n" +
                "\t\"facePlusPlusScore\": null,\n" +
                "\t\"advanceVerifyResult\": null,\n" +
                "\t\"sex\": 2,\n" +
                "\t\"hasDriverLicense\": false,\n" +
                "\t\"companyAddrProvice\": null,\n" +
                "\t\"companyAddrCity\": null,\n" +
                "\t\"homeAddrProvice\": null,\n" +
                "\t\"homeAddrCity\": null,\n" +
                "\t\"childrenAmount\": 0,\n" +
                "\t\"academic\": null,\n" +
                "\t\"maritalStatus\": null,\n" +
                "\t\"thirdType\": null,\n" +
                "\t\"mobileAsEmergencyTelCount\": null,\n" +
                "\t\"hasInsuranceCard\": null,\n" +
                "\t\"hasFamilyCard\": null,\n" +
                "\t\"hasTaxNumber\": null,\n" +
                "\t\"hasPayroll\": null,\n" +
                "\t\"positionName\": null,\n" +
                "\t\"hitOverDuePositionMan\": null,\n" +
                "\t\"hitOverDuePositionFeMen\": null,\n" +
                "\t\"hitHomeProviceMan\": null,\n" +
                "\t\"hitHomeProviceFeMen\": null,\n" +
                "\t\"hitHomeProviceMan150\": null,\n" +
                "\t\"hitHomeProviceFeMen150\": null,\n" +
                "\t\"hitHomeProviceMan80\": null,\n" +
                "\t\"dependentBusiness\": null,\n" +
                "\t\"religionName\": null,\n" +
                "\t\"monthlyIncome\": null,\n" +
                "\t\"firstLinkmanExists\": null,\n" +
                "\t\"firstLinkmanExistsRelatedUserSettled\": null,\n" +
                "\t\"emailExists\": null,\n" +
                "\t\"firstEmailExistsRelatedUserSettled\": null,\n" +
                "\t\"linkmanExists\": null,\n" +
                "\t\"existsLinkmanHasSettledOrder\": null,\n" +
                "\t\"existsLinkmanWithoutSuccessOrder\": null,\n" +
                "\t\"workAddressExists\": null,\n" +
                "\t\"orderAddressExists\": null,\n" +
                "\t\"homeAddressExists\": null,\n" +
                "\t\"iziPhoneAgeResult\": {\n" +
                "\t\t\"status\": \"OK\",\n" +
                "\t\t\"age\": 6\n" +
                "\t},\n" +
                "\t\"iziPhoneVerifyResult\": {\n" +
                "\t\t\"status\": \"OK\",\n" +
                "\t\t\"message\": \"MATCH\"\n" +
                "\t},\n" +
                "\t\"companyTel\": null,\n" +
                "\t\"companyName\": null,\n" +
                "\t\"whatsappAccount\": null,\n" +
                "\t\"whatsappAccountStr\": null,\n" +
                "\t\"mobileNumber\": null,\n" +
                "\t\"idCardSex\": null,\n" +
                "\t\"idCardBirthday\": null,\n" +
                "\t\"birthday\": null,\n" +
                "\t\"countOfOverdueLessThan5UsersByEmergencyTel\": null,\n" +
                "\t\"hasCreditCard\": null,\n" +
                "\t\"gojekVerified\": true,\n" +
                "\t\"bankCode\": null,\n" +
                "\t\"whatsAppCheckResult\": null,\n" +
                "\t\"whatsAppYesRadio\": null,\n" +
                "\t\"linkmanInMultiBankcardUser\": null,\n" +
                "\t\"mobileInMultiBankcardUserEmergencyTel\": null,\n" +
                "\t\"sameBankcardNumberWithOthersCount\": null,\n" +
                "\t\"orderSmallDirectIsNull\": null,\n" +
                "\t\"mobileInOverdueLessThan5UserEmergencyTel\": null,\n" +
                "\t\"linkmanWhatsAppCheckWithNoCount\": null,\n" +
                "\t\"ownerWhatsAppDetail\": null\n" +
                "}";

        RUserInfo rUserInfo = JsonUtil.toObject(str1,RUserInfo.class);
        facts.add(rUserInfo);


        String str = "{\n" +
                "\t\t\"overdueDays\": 30,\n" +
                "\t\t\"borrowingAmount\": 1200000.00,\n" +
                "\t\t\"lastLoanModelScore\": {\n" +
                "\t\t\t\"product600Score\": 465.704,\n" +
                "\t\t\t\"product100Score\": null,\n" +
                "\t\t\t\"product50Score\": null,\n" +
                "\t\t\t\"product600ScoreV2\": 441.174\n" +
                "\t\t},\n" +
                "\t\t\"diffHoursBetweenFirstCollectionAndRefundTime\": null,\n" +
                "\t\t\"loanPassType\": 3,\n" +
                "\t\t\"noCollectionRecord\": true,\n" +
                "\t\t\"intervalDays\": 3\n" +
                "\t}";
        LastLoanForExtend lastLoanForExtend = JsonUtil.toObject(str,LastLoanForExtend.class);
        facts.add(lastLoanForExtend);


        Map<String, SysAutoReviewRule> allRules = ruleService.getAllRules();
        OrdOrder order = ordService.getOrderByOrderNo("011907111545554141");
        try {
            reBorrowingProduct600ExtendExecutionChain.execute(order,allRules,facts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Autowired
    ReBorrowingProduct600ExtendExecutionChain reBorrowingProduct600ExtendExecutionChain;



}
