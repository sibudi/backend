package com.yqg.drools.extract;

import com.github.pagehelper.StringUtil;
import com.yqg.service.AutoCallService;
import com.yqg.service.NonManualReviewService;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.AutoCallModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.DeviceService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.util.RuleConstants;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrHouseWifeDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AutoCallExtractor implements BaseExtractor<AutoCallModel> {

    @Autowired
    private AutoCallService autoCallService;

    @Autowired
    private NonManualReviewService nonManualReviewService;
    @Autowired
    private UserRiskService userRiskService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserService userService;
    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private IziService iziService;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;


    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.AUTO_CALL.equals(ruleSet);
    }

    @Override
    public Optional<AutoCallModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        
        List<CallResult> callResultList = autoCallService.getTelCallList(order.getUuid(), null);
        if (CollectionUtils.isEmpty(callResultList)) {
            return Optional.empty();
        }

        AutoCallModel autoCallModel = new AutoCallModel();
        autoCallModel.setBorrowingCount(order.getBorrowingCount());
        autoCallModel.setIsCashCahOrder(order.getThirdType() != null && 1 == order.getThirdType());


        //公司电话相关规则：
        autoCallModel.setCompanyTelAutoCallResult(getCompanyCallResult(callResultList));
        OrdDeviceInfo ordDeviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());
        if(ordDeviceInfo!=null){
            BigDecimal totalMemory = OrdDeviceInfo.getFormatStorageCapacity(ordDeviceInfo.getTotalMemory());
            autoCallModel.setTotalMemory(totalMemory);
            autoCallModel.setDeviceType(ordDeviceInfo.getDeviceType());
            autoCallModel.setMobileLanguage(ordDeviceInfo.getMobileLanguage());
        }
        UsrBank usrBank = usrBankDao.getUserBankInfoById(order.getUserUuid(),order.getUserBankUuid());
        if(usrBank!=null){
            autoCallModel.setBankCode(usrBank.getBankCode());
        }

        //是否是100rmb产品
        boolean isDecreasedLimitProduct =  userRiskService.histSpecifiedProductWithDecreasedCreditLimit(order.getUuid());
        autoCallModel.setIs100RmbProduct(isDecreasedLimitProduct);


        //紧急联系人完全有效个数
        Long emergencyValidCount =
                callResultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode())
                        .filter(elem -> elem.isCallFinished())
                        .filter(elem -> elem.isCallValid()).count();
//        //inforbip 5603 and twilio  resultType=2
//        Long mayAvailableEmergencyCount = mayAvailableEmergencyCount(callResultList);
//        log.info("availableCount: {}, orderNo: {}",mayAvailableEmergencyCount,order.getUuid());
        Long backupValidCount = callResultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.BACKUP_LINKMAN.getCode())
                .filter(elem -> elem.isCallFinished())
                .filter(elem -> elem.isCallValid()).count();

        autoCallModel.setEmergencyPassCount(emergencyValidCount);
        autoCallModel.setBackupPassCount(backupValidCount);
        autoCallModel.setNeedLinkmanCall(needLinkmanCallFinished(order));

        Long linkmanValidCountWithInforbip =
                callResultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode()
                                               && elem.getCallChannel() == CallResult.CallResultEnum.INFORBIP)
                .filter(elem -> elem.isCallFinished())
                .filter(elem -> elem.isCallValid()).count();
        autoCallModel.setValidLinkmanCallCountWithInforbip(linkmanValidCountWithInforbip);

        List<OrdRiskRecord> recordList = orderRiskRecordRepository.getOrderRiskRecordList(order.getUuid());
        if(!CollectionUtils.isEmpty(recordList)){
            Optional<OrdRiskRecord> eCommerce =
                    recordList.stream().filter(elem->elem.getRuleDetailType().equals(BlackListTypeEnum.APP_COUNT_FOR_E_COMMERCE.getMessage())).findFirst();
            if(eCommerce.isPresent()){
                String realValue = eCommerce.get().getRuleRealValue();
                Long appCountForEcommerce =
                        "null".equalsIgnoreCase(realValue)|| StringUtil.isEmpty(realValue)? null:
                                Long.valueOf(realValue);
                autoCallModel.setAppCountForEcommerce(appCountForEcommerce);
            }
            Optional<OrdRiskRecord> creditCard =
                    recordList.stream().filter(elem->elem.getRuleDetailType().equals(BlackListTypeEnum.APP_COUNT_FOR_CREDIT_CARD.getMessage())).findFirst();
            if(creditCard.isPresent()){
                String realValue = creditCard.get().getRuleRealValue();
                Long appCountForCreditCard =
                        "null".equalsIgnoreCase(realValue)|| StringUtil.isEmpty(realValue)? null:
                                Long.valueOf(realValue);
                autoCallModel.setAppCountForCreditCard(appCountForCreditCard);
            }

        }

        UsrUser user = userService.getUserInfo(order.getUserUuid());
        autoCallModel.setSex(user.getSex());

        if (user.getUserRole() != null && user.getUserRole() == 2) {
            UsrWorkDetail usrWorkDetail = userService.getUserWorkDetail(order.getUserUuid());
            if (usrWorkDetail == null) {
                log.warn("auto call user work detail is empty orderNo: {}", order.getUuid());
            } else {
                if(StringUtils.isNotEmpty(usrWorkDetail.getMonthlyIncome())){
                    autoCallModel.setMonthlyIncome(RuleUtils.formatIncome(usrWorkDetail.getMonthlyIncome()));
                }
                autoCallModel.setChildrenCount(usrWorkDetail.getChildrenAmount());
                autoCallModel.setAcademic(usrWorkDetail.getAcademic());
            }
        }
        if(user.getUserRole()!=null && user.getUserRole()==3){
            UsrHouseWifeDetail usrHouseWifeDetail = userService.getUserHouseWifeDetail(order.getUserUuid());
            if (usrHouseWifeDetail == null) {
                log.warn("auto call user house wife detail is empty orderNo: {}", order.getUuid());
            } else {
                if(StringUtils.isNotEmpty(usrHouseWifeDetail.getMouthIncome())){
                    autoCallModel.setMonthlyIncome(RuleUtils.formatIncome(usrHouseWifeDetail.getMouthIncome()));
                }
                autoCallModel.setChildrenCount(usrHouseWifeDetail.getChildrenAmount());
                autoCallModel.setAcademic(usrHouseWifeDetail.getAcademic());
            }
        }

        //取izi结果：
        try {
            String strIziPhoneAgeResponse = iziService.getIziResponse(order.getUuid(), "1");
            String strIziPhoneVerifyResponse = iziService.getIziResponse(order.getUuid(), "2");
            if (!StringUtil.isEmpty(strIziPhoneAgeResponse)) {
                IziResponse phoneAgeResponse = JsonUtil.toObject(strIziPhoneAgeResponse, IziResponse.class);
                autoCallModel.setIziPhoneAgeResult(RUserInfo.IziPhoneAgeResult.parseResultFromResponse(phoneAgeResponse));
            }
            if (!StringUtil.isEmpty(strIziPhoneVerifyResponse)) {
                IziResponse phoneVerifyResponse = JsonUtil.toObject(strIziPhoneVerifyResponse, IziResponse.class);
                autoCallModel.setIziPhoneVerifyResult(RUserInfo.IziPhoneVerifyResult.parseResultFromResponse(phoneVerifyResponse));
            }

        } catch (Exception e) {
            log.info("fetch izi response failed", e);
        }

        if (order.getBorrowingCount() > 1) {
            //复借，查询第一笔订单的公司电话外呼情况
            List<OrdOrder> successOrders = ordDao.getLastSuccessOrder(order.getUserUuid());
            if (!CollectionUtils.isEmpty(successOrders)) {
                Optional<OrdOrder> firstOrder =
                        successOrders.stream().filter(elem -> !"1".equals(elem.getOrderType())).min(Comparator.comparing(OrdOrder::getCreateTime));
                List<CallResult> firstOrderCallResultList = autoCallService.getTelCallList(firstOrder.get().getUuid(), null);
                autoCallModel.setFirstOrderCompanyTelCallResult(getCompanyCallResult(firstOrderCallResultList));
            }

        }


        return Optional.of(autoCallModel);

    }

    private Integer getCompanyCallResult(List<CallResult> callResults) {
        if (CollectionUtils.isEmpty(callResults)) {
            return null;
        }
        //公司电话相关规则：
        List<CallResult> companyCallList = callResults.stream().filter(elem -> TeleCallResult.CallTypeEnum.COMPANY.getCode().equals(elem
                .getCallType())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(companyCallList)) {
            //如果有多条记录取最新的记录
            Optional<CallResult> lastCall = companyCallList.stream().max(Comparator.comparing(CallResult::getCreateTime));
            if (lastCall.isPresent()) {
                return lastCall.get().getCallResultType();
            }

        }
        return null;
    }


    public static Long mayAvailableEmergencyCount(List<CallResult> callResultList){
        List<CallResult> inforbipListFor5603 =
                callResultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode())
                        .filter(elem -> elem.isCallFinished())
                        .filter(elem->elem.getCallChannel().equals(CallResult.CallResultEnum.INFORBIP))
                        .filter(elem -> elem.getErrorId()!=null&&elem.getErrorId()==5603).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(inforbipListFor5603)){
            return 0L;
        }
        // List<CallResult> twilioListForType2 =  callResultList.stream().filter(elem -> elem.getCallType() == TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode())
        //         .filter(elem -> elem.isCallFinished()).filter(elem->elem.getCallResultType()!=null&& elem.getCallResultType()==2)
        //         .filter(elem->elem.getCallChannel().equals(CallResult.CallResultEnum.TWILIO)).collect(Collectors.toList());
        // if(CollectionUtils.isEmpty(twilioListForType2)){
        //     return 0L;
        // }

        //inforbip 中6503的号码在twilio中的数量
        List<String> numbers = inforbipListFor5603.stream().map(elem->elem.getTellNumber()).collect(Collectors.toList());
        // List<String> twilioNumbers = twilioListForType2.stream().map(elem->elem.getTellNumber()).collect(Collectors.toList());

        //return numbers.stream().filter(elem->twilioNumbers.contains(elem)).count();
        return numbers.stream().count();
    }

    public static void main(String[] args) {
        String str = "[{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null," +
                "\"_likeField\":null,\"_likeKeyword\":null,\"id\":1791833,\"disabled\":0,\"uuid\":\"F2DDF95E640C443BAF4FF58EB5B922CA\",\"createUser\":0,\"createTime\":1545228127000,\"updateUser\":0,\"updateTime\":1545228190000,\"remark\":\"\",\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6287772905218\",\"callNode\":1,\"callType\":1,\"callState\":2,\"callBulkId\":\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\",\"callMsgId\":\"c1a13b36-46d6-4603-acf0-768d53f08071\",\"callResult\":2,\"callResultType\":1,\"errorGroupId\":0,\"errorId\":5000,\"callBeginTime\":\"2018-12-19T14:02:07.000+0000\",\"callEndTime\":\"2018-12-19T14:02:21.816+0000\",\"callDuration\":3,\"callResponse\":\"{\\\"bulkId\\\":\\\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\\\",\\\"messageId\\\":\\\"c1a13b36-46d6-4603-acf0-768d53f08071\\\",\\\"to\\\":\\\"6287772905218\\\",\\\"sentAt\\\":\\\"2018-12-19T14:02:07.880+0000\\\",\\\"doneAt\\\":\\\"2018-12-19T14:02:21.817+0000\\\",\\\"startTime\\\":\\\"2018-12-19T14:02:07.000+0000\\\",\\\"endTime\\\":\\\"2018-12-19T14:02:21.816+0000\\\",\\\"answerTime\\\":\\\"2018-12-19T14:02:18.000+0000\\\",\\\"duration\\\":3,\\\"fileDuration\\\":3.21305,\\\"mccMnc\\\":\\\"51011\\\",\\\"callbackData\\\":\\\"\\\",\\\"dtmfCodes\\\":\\\"\\\",\\\"recordedAudioFileUrl\\\":null,\\\"price\\\":{\\\"pricePerSecond\\\":\\\"15.0\\\",\\\"currency\\\":\\\"IDR\\\"},\\\"status\\\":{\\\"groupId\\\":3,\\\"groupName\\\":\\\"DELIVERED\\\",\\\"id\\\":5,\\\"name\\\":\\\"DELIVERED_TO_HANDSET\\\",\\\"description\\\":\\\"Message delivered to handset\\\"},\\\"error\\\":{\\\"groupId\\\":0,\\\"groupName\\\":\\\"OK\\\",\\\"id\\\":5000,\\\"name\\\":\\\"VOICE_ANSWERED\\\",\\\"description\\\":\\\"Call answered by human\\\",\\\"permanent\\\":true}}\",\"callChannel\":\"INFORBIP\",\"callValid\":true,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":true},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":1791834,\"disabled\":0,\"uuid\":\"0680E406F84C49B08D8B576993CA54C4\",\"createUser\":0,\"createTime\":1545228127000,\"updateUser\":0,\"updateTime\":1545228182000,\"remark\":\"\",\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6281293643907\",\"callNode\":1,\"callType\":3,\"callState\":2,\"callBulkId\":\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\",\"callMsgId\":\"612c21d5-6421-4f32-b461-47e1b82f5b6a\",\"callResult\":2,\"callResultType\":1,\"errorGroupId\":0,\"errorId\":5000,\"callBeginTime\":\"2018-12-19T14:02:07.000+0000\",\"callEndTime\":\"2018-12-19T14:02:45.254+0000\",\"callDuration\":3,\"callResponse\":\"{\\\"bulkId\\\":\\\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\\\",\\\"messageId\\\":\\\"612c21d5-6421-4f32-b461-47e1b82f5b6a\\\",\\\"to\\\":\\\"6281293643907\\\",\\\"sentAt\\\":\\\"2018-12-19T14:02:07.897+0000\\\",\\\"doneAt\\\":\\\"2018-12-19T14:02:45.255+0000\\\",\\\"startTime\\\":\\\"2018-12-19T14:02:07.000+0000\\\",\\\"endTime\\\":\\\"2018-12-19T14:02:45.254+0000\\\",\\\"answerTime\\\":\\\"2018-12-19T14:02:42.000+0000\\\",\\\"duration\\\":3,\\\"fileDuration\\\":3.21305,\\\"mccMnc\\\":\\\"51010\\\",\\\"callbackData\\\":\\\"\\\",\\\"dtmfCodes\\\":\\\"\\\",\\\"recordedAudioFileUrl\\\":null,\\\"price\\\":{\\\"pricePerSecond\\\":\\\"15.0\\\",\\\"currency\\\":\\\"IDR\\\"},\\\"status\\\":{\\\"groupId\\\":3,\\\"groupName\\\":\\\"DELIVERED\\\",\\\"id\\\":5,\\\"name\\\":\\\"DELIVERED_TO_HANDSET\\\",\\\"description\\\":\\\"Message delivered to handset\\\"},\\\"error\\\":{\\\"groupId\\\":0,\\\"groupName\\\":\\\"OK\\\",\\\"id\\\":5000,\\\"name\\\":\\\"VOICE_ANSWERED\\\",\\\"description\\\":\\\"Call answered by human\\\",\\\"permanent\\\":true}}\",\"callChannel\":\"INFORBIP\",\"callValid\":true,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":true},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":1791835,\"disabled\":0,\"uuid\":\"C33DAFB07C0F406DBCBD168BCE718347\",\"createUser\":0,\"createTime\":1545228127000,\"updateUser\":0,\"updateTime\":1545228184000,\"remark\":\"\",\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6281212234092\",\"callNode\":1,\"callType\":3,\"callState\":2,\"callBulkId\":\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\",\"callMsgId\":\"beac3e59-6adc-4e3a-8161-71fc0d4a3e8a\",\"callResult\":26,\"callResultType\":2,\"errorGroupId\":3,\"errorId\":5603,\"callBeginTime\":\"\",\"callEndTime\":\"\",\"callDuration\":0,\"callResponse\":\"{\\\"bulkId\\\":\\\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\\\",\\\"messageId\\\":\\\"beac3e59-6adc-4e3a-8161-71fc0d4a3e8a\\\",\\\"to\\\":\\\"6281212234092\\\",\\\"sentAt\\\":\\\"2018-12-19T14:02:07.912+0000\\\",\\\"doneAt\\\":\\\"2018-12-19T14:02:53.348+0000\\\",\\\"startTime\\\":null,\\\"endTime\\\":null,\\\"answerTime\\\":null,\\\"duration\\\":0,\\\"fileDuration\\\":null,\\\"mccMnc\\\":\\\"0\\\",\\\"callbackData\\\":\\\"null\\\",\\\"dtmfCodes\\\":null,\\\"recordedAudioFileUrl\\\":null,\\\"price\\\":{\\\"pricePerSecond\\\":\\\"15.0\\\",\\\"currency\\\":\\\"IDR\\\"},\\\"status\\\":{\\\"groupId\\\":4,\\\"groupName\\\":\\\"EXPIRED\\\",\\\"id\\\":15,\\\"name\\\":\\\"EXPIRED_EXPIRED\\\",\\\"description\\\":\\\"Message expired\\\"},\\\"error\\\":{\\\"groupId\\\":3,\\\"groupName\\\":\\\"OPERATOR_ERRORS\\\",\\\"id\\\":5603,\\\"name\\\":\\\"EC_DECLINE\\\",\\\"description\\\":\\\"Decline.\\\",\\\"permanent\\\":false}}\",\"callChannel\":\"INFORBIP\",\"callValid\":false,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":false},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":1791836,\"disabled\":0,\"uuid\":\"2E34ED616DE5458DB3D57DE03C445669\",\"createUser\":0,\"createTime\":1545228127000,\"updateUser\":0,\"updateTime\":1545228187000,\"remark\":\"\",\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6283873361765\",\"callNode\":1,\"callType\":3,\"callState\":2,\"callBulkId\":\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\",\"callMsgId\":\"df6bc3dc-7a7c-47df-b428-f95932b1ea0f\",\"callResult\":16,\"callResultType\":2,\"errorGroupId\":3,\"errorId\":5480,\"callBeginTime\":\"\",\"callEndTime\":\"\",\"callDuration\":0,\"callResponse\":\"{\\\"bulkId\\\":\\\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\\\",\\\"messageId\\\":\\\"df6bc3dc-7a7c-47df-b428-f95932b1ea0f\\\",\\\"to\\\":\\\"6283873361765\\\",\\\"sentAt\\\":\\\"2018-12-19T14:02:07.926+0000\\\",\\\"doneAt\\\":\\\"2018-12-19T14:02:25.977+0000\\\",\\\"startTime\\\":null,\\\"endTime\\\":null,\\\"answerTime\\\":null,\\\"duration\\\":0,\\\"fileDuration\\\":null,\\\"mccMnc\\\":\\\"0\\\",\\\"callbackData\\\":\\\"null\\\",\\\"dtmfCodes\\\":null,\\\"recordedAudioFileUrl\\\":null,\\\"price\\\":{\\\"pricePerSecond\\\":\\\"15.0\\\",\\\"currency\\\":\\\"IDR\\\"},\\\"status\\\":{\\\"groupId\\\":4,\\\"groupName\\\":\\\"EXPIRED\\\",\\\"id\\\":15,\\\"name\\\":\\\"EXPIRED_EXPIRED\\\",\\\"description\\\":\\\"Message expired\\\"},\\\"error\\\":{\\\"groupId\\\":3,\\\"groupName\\\":\\\"OPERATOR_ERRORS\\\",\\\"id\\\":5480,\\\"name\\\":\\\"EC_VOICE_ERROR_TEMPORARILY_NOT_AVAILABE\\\",\\\"description\\\":\\\"Callee currently unavailable\\\",\\\"permanent\\\":false}}\",\"callChannel\":\"INFORBIP\",\"callValid\":false,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":false},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":1791837,\"disabled\":0,\"uuid\":\"53967A489AA540738F2AAF052CE2D995\",\"createUser\":0,\"createTime\":1545228127000,\"updateUser\":0,\"updateTime\":1545228242000,\"remark\":\"\",\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"622155721573\",\"callNode\":1,\"callType\":2,\"callState\":2,\"callBulkId\":\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\",\"callMsgId\":\"b086374f-2d45-4970-a565-ed610ab96360\",\"callResult\":4,\"callResultType\":1,\"errorGroupId\":2,\"errorId\":5003,\"callBeginTime\":\"\",\"callEndTime\":\"\",\"callDuration\":0,\"callResponse\":\"{\\\"bulkId\\\":\\\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\\\",\\\"messageId\\\":\\\"b086374f-2d45-4970-a565-ed610ab96360\\\",\\\"to\\\":\\\"622155721573\\\",\\\"sentAt\\\":\\\"2018-12-19T14:02:07.941+0000\\\",\\\"doneAt\\\":\\\"2018-12-19T14:04:01.273+0000\\\",\\\"startTime\\\":null,\\\"endTime\\\":null,\\\"answerTime\\\":null,\\\"duration\\\":0,\\\"fileDuration\\\":null,\\\"mccMnc\\\":\\\"0\\\",\\\"callbackData\\\":\\\"null\\\",\\\"dtmfCodes\\\":null,\\\"recordedAudioFileUrl\\\":null,\\\"price\\\":{\\\"pricePerSecond\\\":\\\"15.0\\\",\\\"currency\\\":\\\"IDR\\\"},\\\"status\\\":{\\\"groupId\\\":4,\\\"groupName\\\":\\\"EXPIRED\\\",\\\"id\\\":15,\\\"name\\\":\\\"EXPIRED_EXPIRED\\\",\\\"description\\\":\\\"Message expired\\\"},\\\"error\\\":{\\\"groupId\\\":2,\\\"groupName\\\":\\\"USER_ERRORS\\\",\\\"id\\\":5003,\\\"name\\\":\\\"EC_VOICE_NO_ANSWER\\\",\\\"description\\\":\\\"User was notified, but did not answer call\\\",\\\"permanent\\\":true}}\",\"callChannel\":\"INFORBIP\",\"callValid\":true,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":false},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":1791838,\"disabled\":0,\"uuid\":\"B825257161F1452DB9211CEA76DED907\",\"createUser\":0,\"createTime\":1545228127000,\"updateUser\":0,\"updateTime\":1545228188000,\"remark\":\"\",\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6283804367435\",\"callNode\":1,\"callType\":3,\"callState\":2,\"callBulkId\":\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\",\"callMsgId\":\"87d37915-15c4-4b06-aae0-5af825638e36\",\"callResult\":4,\"callResultType\":1,\"errorGroupId\":2,\"errorId\":5003,\"callBeginTime\":\"\",\"callEndTime\":\"\",\"callDuration\":0,\"callResponse\":\"{\\\"bulkId\\\":\\\"c5f3f582-bfe9-484a-9f01-4c051156d7e4\\\",\\\"messageId\\\":\\\"87d37915-15c4-4b06-aae0-5af825638e36\\\",\\\"to\\\":\\\"6283804367435\\\",\\\"sentAt\\\":\\\"2018-12-19T14:02:07.956+0000\\\",\\\"doneAt\\\":\\\"2018-12-19T14:02:54.640+0000\\\",\\\"startTime\\\":null,\\\"endTime\\\":null,\\\"answerTime\\\":null,\\\"duration\\\":0,\\\"fileDuration\\\":null,\\\"mccMnc\\\":\\\"0\\\",\\\"callbackData\\\":\\\"null\\\",\\\"dtmfCodes\\\":null,\\\"recordedAudioFileUrl\\\":null,\\\"price\\\":{\\\"pricePerSecond\\\":\\\"15.0\\\",\\\"currency\\\":\\\"IDR\\\"},\\\"status\\\":{\\\"groupId\\\":4,\\\"groupName\\\":\\\"EXPIRED\\\",\\\"id\\\":15,\\\"name\\\":\\\"EXPIRED_EXPIRED\\\",\\\"description\\\":\\\"Message expired\\\"},\\\"error\\\":{\\\"groupId\\\":2,\\\"groupName\\\":\\\"USER_ERRORS\\\",\\\"id\\\":5003,\\\"name\\\":\\\"EC_VOICE_NO_ANSWER\\\",\\\"description\\\":\\\"User was notified, but did not answer call\\\",\\\"permanent\\\":true}}\",\"callChannel\":\"INFORBIP\",\"callValid\":true,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":false},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":null,\"disabled\":null,\"uuid\":null,\"createUser\":null,\"createTime\":null,\"updateUser\":null,\"updateTime\":null,\"remark\":null,\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6283873361765\",\"callNode\":1,\"callType\":3,\"callState\":2,\"callBulkId\":null,\"callMsgId\":null,\"callResult\":null,\"callResultType\":0,\"errorGroupId\":null,\"errorId\":0,\"callBeginTime\":null,\"callEndTime\":null,\"callDuration\":null,\"callResponse\":null,\"callChannel\":\"TWILIO\",\"callValid\":false,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":false},{\"_start\":null,\"_pageSize\":null,\"_orderBy\":null,\"_condition\":null,\"_inField\":null,\"_inValues\":null,\"_likeField\":null,\"_likeKeyword\":null,\"id\":null,\"disabled\":null,\"uuid\":null,\"createUser\":null,\"createTime\":null,\"updateUser\":null,\"updateTime\":null,\"remark\":null,\"orderNo\":\"011812132339228610\",\"userUuid\":\"C0178DA2775946A19241135AE4B4EA92\",\"tellNumber\":\"6281212234092\",\"callNode\":1,\"callType\":3,\"callState\":2,\"callBulkId\":null,\"callMsgId\":null,\"callResult\":null,\"callResultType\":0,\"errorGroupId\":null,\"errorId\":0,\"callBeginTime\":null,\"callEndTime\":null,\"callDuration\":null,\"callResponse\":null,\"callChannel\":\"TWILIO\",\"callValid\":false,\"callFinished\":true,\"callInvalid\":false,\"callReceived\":false}]";
        List<CallResult> list = JsonUtil.toList(str,CallResult.class);
        mayAvailableEmergencyCount(list);
    }
    private boolean needLinkmanCallFinished(OrdOrder order) {
        if (CollectionUtils.isEmpty(autoCallService.getLinkmanAutoCallRequest(order))) {
            return false;
        }
        return true;
    }
}
