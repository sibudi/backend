package com.yqg.drools.service;

import ai.advance.sdk.client.OpenApiClient;
import com.alibaba.fastjson.JSON;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.RUserInfo.IdentityVerifyResult;
import com.yqg.drools.model.RUserInfo.IdentityVerifyResultType;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.service.third.advance.AdvanceService;
import com.yqg.service.third.izi.Client;
import com.yqg.service.user.service.UsrCertificationService;
import com.yqg.service.util.RuleConstants;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.utils.HttpUtil;
import com.yqg.service.order.OrdThirdDataService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.third.advance.config.AdvanceConfig;
import com.yqg.service.third.advance.response.AdvanceResponse;
import com.yqg.service.third.advance.response.IdentityCheckResultData;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrCertificationInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 实名验证服务
 */

@Service
@Slf4j
public class RealNameVerificationService {

    @Autowired
    private AdvanceConfig advanceConfig;

    @Autowired
    private SysThirdLogsService sysThirdLogsService;

//    @Autowired
//    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Autowired
    private UsrCertificationService usrCertificationService;

    @Autowired
    private OrdThirdDataService ordThirdDataService;
    @Autowired
    private AdvanceService advanceService;

    @Value("${jxl.taxNumberVerify}")
    private String taxNumberCheckUrl;

    @Value("${xendit.ktpValidationUrl}")
    private String xenditKtpValidationUrl;

    @Value("${xendit.apiKey}")
    private String apiKey;

    private static final String TAX_NUMBER_INVALID = "account_number invalid";


    /***
     * advance 实名验证
     */
    public IdentityVerifyResult advanceVerification(String realName, String idCardNo, OrdOrder order) {
        // 走advance实名认证

        // 参数
        Map<String, String> identityCheck = new HashMap<>();
        identityCheck.put("name", realName);
        identityCheck.put("idNumber", idCardNo);
        // 记录请求的数据
        log.info("the sysThirdLogs request: " + JSON.toJSONString(identityCheck));
        sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(),
                SysThirdLogsEnum.ADVANC.getCode(), null, JSON.toJSONString(identityCheck), null);

        String  response = advanceService.identityCheck(identityCheck);

        // 记录响应的数据
        log.info("the sysThirdLogs response: " + response);
        sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(),
                SysThirdLogsEnum.ADVANC.getCode(), null, null, response);

        AdvanceResponse advanceResponse = JSON.parseObject(response, AdvanceResponse.class);
        if (advanceResponse == null) {
            log.error("identity verification exception:" + response);
            updateUsrCertificationInfo(order.getUserUuid(),
                    CertificationResultEnum.AUTH_FAILD.getType(), CertificationEnum.USER_IDENTITY.getType());
            return new IdentityVerifyResult(IdentityVerifyResultType.EXCEPTION, "实名认证异常：advacen返回信息为空");
            //return markFun("实名认证异常：advacen返回信息为空", order, codeEntityMap, BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage(), false);//  advance拒绝
        }
        if (!"SUCCESS".equalsIgnoreCase(advanceResponse.getCode())) {
            updateUsrCertificationInfo(order.getUserUuid(),
                    CertificationResultEnum.AUTH_FAILD.getType(),CertificationEnum.USER_IDENTITY.getType());
            return new IdentityVerifyResult(IdentityVerifyResultType.INTERFACE_FAILED, "实名认证失败：" + advanceResponse.getCode());

        }
        //接口返回成功
        IdentityCheckResultData data = JSON
                .parseObject(advanceResponse.getData(), IdentityCheckResultData.class);
        // 1、身份证号码未查到
        if (StringUtils.isEmpty(data.getIdNumber())) {
            updateUsrCertificationInfo(order.getUserUuid(),
                    CertificationResultEnum.AUTH_FAILD.getType(),CertificationEnum.USER_IDENTITY.getType());
            return new IdentityVerifyResult(IdentityVerifyResultType.ID_NUMBER_EMPTY, "身份证号码未查到");
        } else {
            String name1 = filterAdvanceName(realName).toUpperCase();
            String name2 = filterAdvanceName(data.getName()).toUpperCase();
            String longName = Arrays.asList(name1, name2).stream()
                    .max(Comparator.comparing(String::length)).get();// 较长的名字
            String shortName = Arrays.asList(name1, name2).stream()
                    .min(Comparator.comparing(String::length)).get();// 较短的名字
            //如果两个长度相同,按长度比较可能得到相同的数据，需要做更正,取name1，name2中不同的
            if (name1.length() == name2.length() && shortName.equals(longName)) {
                shortName = longName.equalsIgnoreCase(name1) ? name2 : name1;
            }

            // 2、身份证号码查到，姓名完全匹配
            if (StringUtils.isEmpty(longName) || StringUtils.isEmpty(shortName)) {
                updateUsrCertificationInfo(order.getUserUuid(),
                        CertificationResultEnum.AUTH_FAILD.getType(),CertificationEnum.USER_IDENTITY.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_EMPTY, "姓名为空");
            }
            if (longName.equals(shortName)) {
                //认证成功存储到mogon中
                this.ordThirdDataService.add(advanceResponse.getData(), order.getUuid(), order.getUserUuid(),
                        ThirdDataTypeEnum.IDENTITY_CHECK_DATA, 1);
                updateUsrCertificationInfo(order.getUserUuid(),
                        CertificationResultEnum.AUTH_SUCCESS.getType(),CertificationEnum.USER_IDENTITY.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_FULL_MATCH_SUCCESS, "姓名完全匹配成功");
            }
            // 3、姓名完全匹配失败，姓名模糊匹配
            String[] showNameArr = shortName.split("\\s");
            String longNameStr = longName.replaceAll("\\s", "");
            Integer shortMatch = 0;
            for (String item : showNameArr) {
                if (longNameStr.contains(item)) {
                    shortMatch++;
                }
            }
            // 匹配上  原来是：shortMatch == showNameArr.length
            if (shortMatch > 0) {
                updateUsrCertificationInfo(order.getUserUuid(),
                        CertificationResultEnum.AUTH_SUCCESS.getType(),CertificationEnum.USER_IDENTITY.getType());
                //认证成功存储到mogon中
                this.ordThirdDataService.add(advanceResponse.getData(), order.getUuid(), order.getUserUuid(),
                        ThirdDataTypeEnum.IDENTITY_CHECK_DATA, 1);
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_FUZZY_MATCH_SUCCESS, "姓名模糊匹配成功");
            } else {
                updateUsrCertificationInfo(order.getUserUuid(),
                        CertificationResultEnum.AUTH_FAILD.getType(),CertificationEnum.USER_IDENTITY.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_FUZZY_MATCH_FAILED, "姓名模糊匹配失败");
            }
        }
    }

    public IdentityVerifyResult advanceTest(String realName,String idCardNo){
        // 走advance实名认证

        // 参数
        Map<String, String> identityCheck = new HashMap<>();
        identityCheck.put("name", realName);
        identityCheck.put("idNumber", idCardNo);


        String  response = advanceService.identityCheck(identityCheck);

        AdvanceResponse advanceResponse = JSON.parseObject(response, AdvanceResponse.class);
        if (advanceResponse == null) {
            return new IdentityVerifyResult(IdentityVerifyResultType.EXCEPTION, "实名认证异常：advacen返回信息为空");
        }
        if (!"SUCCESS".equalsIgnoreCase(advanceResponse.getCode())) {

            return new IdentityVerifyResult(IdentityVerifyResultType.INTERFACE_FAILED, "实名认证失败：" + advanceResponse.getCode());

        }
        //接口返回成功
        IdentityCheckResultData data = JSON
                .parseObject(advanceResponse.getData(), IdentityCheckResultData.class);
        // 1、身份证号码未查到
        if (StringUtils.isEmpty(data.getIdNumber())) {
            return new IdentityVerifyResult(IdentityVerifyResultType.ID_NUMBER_EMPTY, "身份证号码未查到");
        } else {
            String name1 = filterAdvanceName(realName).toUpperCase();
            String name2 = filterAdvanceName(data.getName()).toUpperCase();
            String longName = Arrays.asList(name1, name2).stream()
                    .max(Comparator.comparing(String::length)).get();// 较长的名字
            String shortName = Arrays.asList(name1, name2).stream()
                    .min(Comparator.comparing(String::length)).get();// 较短的名字
            //如果两个长度相同,按长度比较可能得到相同的数据，需要做更正,取name1，name2中不同的
            if (name1.length() == name2.length() && shortName.equals(longName)) {
                shortName = longName.equalsIgnoreCase(name1) ? name2 : name1;
            }

            // 2、身份证号码查到，姓名完全匹配
            if (StringUtils.isEmpty(longName) || StringUtils.isEmpty(shortName)) {
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_EMPTY, "姓名为空");
            }
            if (longName.equals(shortName)) {
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_FULL_MATCH_SUCCESS, "姓名完全匹配成功");
            }
            // 3、姓名完全匹配失败，姓名模糊匹配
            String[] showNameArr = shortName.split("\\s");
            String longNameStr = longName.replaceAll("\\s", "");
            Integer shortMatch = 0;
            for (String item : showNameArr) {
                if (longNameStr.contains(item)) {
                    shortMatch++;
                }
            }
            // 匹配上  原来是：shortMatch == showNameArr.length
            if (shortMatch > 0) {
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_FUZZY_MATCH_SUCCESS, "姓名模糊匹配成功");
            } else {
                return new IdentityVerifyResult(IdentityVerifyResultType.NAME_FUZZY_MATCH_FAILED, "姓名模糊匹配失败");
            }
        }
    }

    /***
     * 税卡实名验证
     * @param taxNumber
     * @param realName
     * @return
     */
    public IdentityVerifyResult taxNumberVerification(String taxNumber,String realName,String userUuid,String orderNo) {
        long startTime = System.currentTimeMillis();
        try {
            Map<String, String> param = new HashMap<>();
            param.put("account_number", taxNumber);
            param.put("name", realName);
            String jsonParm = JsonUtils.serialize(param);
            String response = sendTaxVerificationRequest(orderNo, userUuid, jsonParm);

            if (needRetryForTaxNumberVerification(response)) {
                log.warn("need reRun param: {}", jsonParm);
                return new  IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_NEED_RETRY, "税卡实名需要重试");
            }

            if (StringUtils.isEmpty(response)) {
                log.info("taxNumber verify response is null");
                updateUsrCertificationInfo(userUuid,
                        CertificationResultEnum.AUTH_FAILD.getType(),CertificationEnum.STEUERKARTED.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_RESPONSE_EMPTY, "税卡验证返现信息为空");
            }
            TaxNumberVerifyResult taxVerifyResponse = JsonUtil.toObject(response, TaxNumberVerifyResult.class);

            if (taxVerifyResponse == null || StringUtils.isEmpty(taxVerifyResponse.getName()) || "null".equals(taxVerifyResponse.getName())) {
                log.info("taxNumber verify response without name");
                updateUsrCertificationInfo(userUuid,
                        CertificationResultEnum.AUTH_FAILD.getType(), CertificationEnum.STEUERKARTED.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_RESPONSE_EMPTY, "税卡验证姓名为空");
            }
            //返回姓名和实名进行比较
            String name1 = filterAdvanceName(realName).toUpperCase();
            String name2 = filterAdvanceName(taxVerifyResponse.getName()).toUpperCase();
            String longName = Arrays.asList(name1, name2).stream()
                    .max(Comparator.comparing(String::length)).get();// 较长的名字
            String shortName = Arrays.asList(name1, name2).stream()
                    .min(Comparator.comparing(String::length)).get();// 较短的名字
            //如果两个长度相同,按长度比较可能得到相同的数据，需要做更正,取name1，name2中不同的
            if (name1.length() == name2.length() && shortName.equals(longName)) {
                shortName = longName.equalsIgnoreCase(name1) ? name2 : name1;
            }
            if (longName.equals(shortName)) {
                this.ordThirdDataService.add(response, orderNo, userUuid,
                        ThirdDataTypeEnum.TAX_NUMBER_VERIFY_DATA, 1);
                updateUsrCertificationInfo(userUuid,
                        CertificationResultEnum.AUTH_SUCCESS.getType(),CertificationEnum.STEUERKARTED.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_FULL_MATCH, "税卡姓名完全匹配");
            }
            // 3、姓名完全匹配失败，姓名模糊匹配
            String[] showNameArr = shortName.split("\\s");
            String longNameStr = longName.replaceAll("\\s", "");
            long shortMatch = Arrays.asList(showNameArr).stream().filter(elem -> longNameStr.contains(elem)).count();

            if (shortMatch > 0 && StringUtils.isNotEmpty(longName) && StringUtils.isNotEmpty(shortName)) {
                this.ordThirdDataService.add(response, orderNo, userUuid,
                        ThirdDataTypeEnum.TAX_NUMBER_VERIFY_DATA, 1);
                updateUsrCertificationInfo(userUuid,
                        CertificationResultEnum.AUTH_SUCCESS.getType(), CertificationEnum.STEUERKARTED.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_FUZZY_MATCH_SUCCESS, "税卡姓名模糊匹配成功");
            } else {
                updateUsrCertificationInfo(userUuid,
                        CertificationResultEnum.AUTH_FAILD.getType(), CertificationEnum.STEUERKARTED.getType());
                return new IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_FUZZY_MATCH_FAILED, "税卡姓名模糊匹配失败");
            }
        } catch (Exception e) {
            log.error("verify realName with taxNumber error", e);
            return new IdentityVerifyResult(IdentityVerifyResultType.TAX_VERIFY_ERROR, "税卡验证接口异常");
        }finally {
            log.info("taxNumber verify cost: {} ms",(System.currentTimeMillis()-startTime));
        }
    }


    /***
     * xendit 实名
     * @param realName
     * @param idCardNo
     * @param order
     * @return
     */
    public IdentityVerifyResult xenditRealNameVerification(String realName, String idCardNo, OrdOrder order) {
        //https://docs.iluma.ai/#official-ktp-validation
        Header authorizationHeader = new BasicHeader("Authorization", "Basic " + Base64Utils.encode((apiKey + ":").getBytes()));
        Map<String, String> params = new HashMap<>();
        params.put("nik", idCardNo);
        params.put("name", realName);
        params.put("address", "1");//必须包含该字段
        String response = HttpUtil.postJson(JsonUtils.serialize(params), xenditKtpValidationUrl, authorizationHeader);
        if (StringUtils.isEmpty(response)) {
            //返回为空，异常
            updateUsrCertificationInfo(order.getUserUuid(),
                    CertificationResultEnum.AUTH_FAILD.getType(), CertificationEnum.USER_IDENTITY.getType());
            return new IdentityVerifyResult(IdentityVerifyResultType.XENDIT_REALNAME_VERIFY_RESPONSE_EMPTY, "xendit实名返回数据为空");
        }
        //记录第三方返回数据
        this.ordThirdDataService.add(response, order.getUuid(), order.getUserUuid(),
                ThirdDataTypeEnum.XENDIT_IDENTITY_CHECK, 1);

        XenditVerifyResult result = JsonUtil.toObject(response, XenditVerifyResult.class);
        if (StringUtils.isNotEmpty(result.getStatus()) && "FOUND".equals(result.getStatus()) && "true".equals(result.getName_matches())) {
            //实名匹配
            updateUsrCertificationInfo(order.getUserUuid(),
                    CertificationResultEnum.AUTH_SUCCESS.getType(), CertificationEnum.USER_IDENTITY.getType(), "XENDIT");
            return new IdentityVerifyResult(IdentityVerifyResultType.XENDIT_REALNAME_VERIFY_MATCH, "XENDIT实名匹配");
        } else {
            //实名不匹配
            String msg = StringUtils.isEmpty(result.getError_code()) ? "XENDIT实名不匹配" : result.getError_code();
            if("NOT_FOUND".equals(result.getStatus())){
                msg = RuleConstants.PERSON_NOT_FOUND;
            }
            updateUsrCertificationInfo(order.getUserUuid(),
                    CertificationResultEnum.AUTH_FAILD.getType(), CertificationEnum.USER_IDENTITY.getType());
            return new IdentityVerifyResult(IdentityVerifyResultType.XENDIT_REALNAME_VERIFY_DISMATCH, msg);
        }
    }



    @Getter
    @Setter
    public static class TaxNumberVerifyResult {
        private String name;
        private String code;
        private String message;
    }

    private boolean needRetryForTaxNumberVerification(String response) {
        if (StringUtils.isEmpty(response)) {
            return true;
        }
        TaxNumberVerifyResult taxVerifyResponse = JsonUtil.toObject(response, TaxNumberVerifyResult.class);
        if (taxVerifyResponse != null && "20000".equals(taxVerifyResponse.getCode())) {
            //已经明确的成功赶回，重试也是失败的，不考虑重试
            return false;
        }
        if (taxVerifyResponse != null
                && (StringUtils.isEmpty(taxVerifyResponse.getName()) || "null".equals(taxVerifyResponse.getName()))
                && !TAX_NUMBER_INVALID.equals(taxVerifyResponse .getMessage())) {
            //name为空且不是账号非法
            return true;
        }
        return false;
    }


    private String sendTaxVerificationRequest(String orderNo, String userUuid, String jsonParam) {
        sysThirdLogsService.addSysThirdLogs(orderNo, userUuid,
                SysThirdLogsEnum.TAX_NUMBER_VERIFY.getCode(), null, jsonParam, null);

        String response = HttpUtil.postJson(jsonParam, taxNumberCheckUrl);

        sysThirdLogsService.addSysThirdLogs(orderNo, userUuid,
                SysThirdLogsEnum.TAX_NUMBER_VERIFY.getCode(), null, null, response);
        return response;
    }


    /***
     * 记录验证信息表
     * @param userUuid
     * @param result
     * @param certiType
     */
    private void updateUsrCertificationInfo(String userUuid, int result,Integer certiType,String... remark) {
        usrCertificationService.updateUsrCertificationInfo(userUuid,result,certiType,remark);
    }

    /**
     * 只筛选出空格和字母，并且多个空格替换成一个空格 去掉逗号之后的学历
     */
    private static String filterAdvanceName(String realName) {
        if (StringUtils.isEmpty(realName)) {
            return "";
        }
        realName = realName.replace(".", " ");
        if (realName.contains(",")) {
            String[] resultArr = realName.split(",");
            realName = resultArr[0];
        }
        String reg = "[^A-Za-z\\s]";// 只筛选出空格和字母
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(realName);
        String result = m.replaceAll("").trim();
        return result.replaceAll("\\s+", " ");
    }


    @Getter
    @Setter
    public static class XenditVerifyResult{
        private String nik;
        private String name_matches;
        private String address_matches;
        private String status;

        //错误的情况下：
        private String error_code;
        private String message;
    }


}
