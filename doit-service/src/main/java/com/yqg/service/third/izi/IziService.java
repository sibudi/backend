package com.yqg.service.third.izi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pagehelper.StringUtil;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.dao.UserIziVerifyResultMongoDal;
import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.izi.config.IziConfig;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.third.izi.response.IziResponse.IziBlackListResponse;
import com.yqg.service.third.izi.response.IziResponse.IziMultiInquiriesV1Response;
import com.yqg.user.dao.UsrIziVerifyResultDao;
import com.yqg.user.entity.UsrIziVerifyResult;
import com.yqg.user.entity.UsrUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wanghuaizhou on 2018/12/5.
 * https://docs.izi.credit/?java#9ea07c6cb3-13
 */
@Service
@Slf4j
public class IziService {

    @Autowired
    private IziConfig iziConfig;

    @Autowired
    UsrIziVerifyResultDao usrIziVerifyResultDao;

    @Autowired
    UserIziVerifyResultMongoDal userIziVerifyResultMongoDal;
    @Autowired
    private IziWhatsAppService iziWhatsAppService;

    public Client setParam(int connectionTimeoutInMillis, int socketTimeoutInMillis) {
        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        api.setConnectionTimeoutInMillis(connectionTimeoutInMillis);
        api.setSocketTimeoutInMillis(socketTimeoutInMillis);
        return api;
    }

    /**
     * 身份认证，详情2
     * <p>
     * OK	计费，查找成功，库中存在查询的NIK，且姓名比对一致
     * NOT	计费，查找成功，库中存在查询的NIK，但姓名比对不一致
     * PERSON_NOT_FOUND	免费，我们库中查不到此NIK
     * INVALID_ID_NUMBER	免费，输入的NIK不合法
     * RETRY_LATER	免费，系统错误，请稍后重试
     * </p>
     *
     * @return IziHttpResponse
     */
    public IziResponse getIdentityCheck3(String name, String id, String orderNo, String userUuid) {
        Client api = setParam(30000, 60000);
        Map<String, String> data = new HashMap<>();
        data.put("name", name);// 可选，字符串，认证人员的姓名
        data.put("id", id);// 字符串，认证人员的身份证号NIK，格式为16位阿拉伯数字
        String responseStr = api.Request(this.iziConfig.getIdentityCheck3Url(), data);
        IziResponse response = JsonUtils.deserialize(responseStr, IziResponse.class);
        if (response != null && StringUtil.isEmpty(response.getStatus())) {
            response.setStatus(responseStr);
        }

        IziRequestLog requestLog = new IziRequestLog(orderNo,userUuid,IziInvokeType.REALNAME_IDENTITY_CHECK,response.getStatus(),responseStr,
                JsonUtils.serialize(data));
        saveIziVerifyResult(requestLog);
        log.info("orderNo: {}, response: {}", orderNo, responseStr);
        return response;
    }

    /**
     * 查询用户是否开通whatsapp
     * RIs
     *
     * @param number   查询人员的电话号码
     * @param orderNo  订单编号
     * @param userUuid userUuid
     * @return IziHttpResponse
     */
    public IziResponse whatsAppIsOpen(String number, String orderNo, String userUuid) {
        Client api = setParam(30000, 60000);
        Map<String, String> data = new HashMap<>();
        data.put("number", number);
        log.info("whatsapp requestParam: {}",JsonUtils.serialize(data));
        String responseStr = api.Request(this.iziConfig.getMonitorWhatsAppUrl(), data);
        IziResponse response = JsonUtils.deserialize(responseStr, IziResponse.class);
        if (response != null && StringUtil.isEmpty(response.getStatus())) {
            response.setStatus(responseStr);
        }
//        saveIziVerifyResult(response, "4", responseStr, orderNo, userUuid);
        log.info("orderNo: {}, response: {}", orderNo, response.toString());
        return response;
    }

    /**
     * 获取号码在网时长
     * <p>
     * status	说明
     * OK	检测成功计费，message为检测结论
     * NVALID_PHONE_NUMBER	免费，输入的电话号码不合法
     * PHONENUMBER_NOT_FOUND	免费，未查询到结果
     * RETRY_LATER	免费，系统错误，请稍后重试
     */
    public IziResponse getPhoneAge(String phoneNum, String orderNo, String userUuid) {

        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        api.setConnectionTimeoutInMillis(30000);
        api.setSocketTimeoutInMillis(60000);
        Map<String, String> data = new HashMap<>();
        data.put("phone", phoneNum); // 必选，字符串，需要验证的电话号
        String responseStr = api.Request(this.iziConfig.getPhoneAgeUrl(), data);
        IziResponse response = JsonUtils.deserialize(responseStr, IziResponse.class);
        if (response != null && StringUtil.isEmpty(response.getStatus())) {
            response.setStatus(responseStr);
        }

        IziRequestLog requestLog = new IziRequestLog(orderNo,userUuid,IziInvokeType.PHONE_AGE,response.getStatus(),responseStr,
                JsonUtils.serialize(data));
        saveIziVerifyResult(requestLog);
        log.info("orderNo: {} , response: {}", orderNo, response.toString());
        return response;
    }


    /**
     * 获取手机号码实名认证
     * <p>
     * status	说明
     * OK	查询成功计费，message为查询结论
     * NOT_FOUND	免费，未查询到
     * INVALID_PHONE_NUMBER	免费，输入的电话号码不合法
     * INVALID_ID_NUMBER	免费，输入的身份证号码不合法
     */
    public IziResponse getPhoneVerify(String phoneNum, String idNum, String orderNo, String userUuid) {
        Map<String, String> data = new HashMap<>();
        data.put("phone", phoneNum); // 必选，字符串，需要验证的电话号
        data.put("id", idNum); // 必选，字符串，需要验证号码对应的身份证

        //如果入参和原来一样且返回的非NOT_FOUND则不需要重新调用,直接用原来的结果
        // UserIziVerifyResultMongo iziLog = getLatestIziResponseFromMongoByUserUuid(userUuid, IziService.IziInvokeType.PHONE_VERIFY.getType());
        // if (iziLog != null && StringUtils.isNotEmpty(iziLog.getRequestParam())
        //         && JsonUtils.serialize(data).equals(iziLog.getRequestParam()) && !"NOT_FOUND".equals(iziLog.getIziVerifyResult())) {
        //     log.info("get data from mongo: {}", iziLog.getIziVerifyResponse());
        //     return JsonUtils.deserialize(iziLog.getIziVerifyResponse(), IziResponse.class);
        // }

        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        api.setConnectionTimeoutInMillis(30000);
        api.setSocketTimeoutInMillis(60000);

        String responseStr = api.Request(this.iziConfig.getPhoneVerifyUrl(), data);
        IziResponse response = JsonUtils.deserialize(responseStr, IziResponse.class);
        if (response != null && StringUtil.isEmpty(response.getStatus())) {
            response.setStatus(responseStr);
        }
        IziRequestLog requestLog = new IziRequestLog(orderNo,userUuid,IziInvokeType.PHONE_VERIFY,response.getStatus(),responseStr,
                JsonUtils.serialize(data));
        saveIziVerifyResult(requestLog);
        log.info("orderNo: {} , response: {}", orderNo, response.toString());
        return response;
    }


    public IziResponse getWhatsAppDetail(String mobile, OrdOrder order, Integer type) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        api.setConnectionTimeoutInMillis(20000);
        api.setSocketTimeoutInMillis(60000);
        Map<String, String> data = new HashMap<>();
        data.put("number", mobile); // 必选，字符串，查询人员的电话号
        String responseStr = api.Request(this.iziConfig.getWhatsAppDetailUrl(), data);
        log.info("the izi whatsAppDetail response of orderNo: {} is: {}", order.getUuid(), responseStr);
        if (StringUtils.isEmpty(responseStr)) {
            log.info("the response of izi whatsAppDetail is empty, orderNo: {}", order.getUuid());
            return null;
        } else {
            IziResponse response = JsonUtils.deserialize(responseStr, IziResponse.class);
//            IziRequestLog requestLog = new IziRequestLog(order.getUuid(),order.getUserUuid(),IziInvokeType.WHATS_APP_DETAIL,response.getStatus(),responseStr,
//                    JsonUtils.serialize(data));
            //saveIziVerifyResult(requestLog);
            //保存结果
            iziWhatsAppService.saveWhatsAppDetail(response, order.getUserUuid(), order.getUuid(), mobile, type);
            return response;
        }

    }

    public IziBlackListResponse checkBlacklist(String name, String idCardNo, String phoneNumberWithoutPrefix, OrdOrder order){
        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("id", idCardNo);
        data.put("phone", "+62" + phoneNumberWithoutPrefix);
        String response = api.Request(this.iziConfig.getBlackListUrl(), data);

        log.info("the izi Blacklist response of orderNo: {} is: {}", order.getUuid(), response);
        if (StringUtils.isEmpty(response)) {
            log.info("the response of izi Blacklist is empty, orderNo: {}", order.getUuid());
            return null;
        } else {
            IziBlackListResponse iziResponse = JsonUtils.deserialize(response, IziBlackListResponse.class);

            IziRequestLog requestLog = new IziRequestLog(order.getUuid(),order.getUserUuid(),IziInvokeType.BLACKLIST,
                iziResponse.getStatus(), response, JsonUtils.serialize(data));
            saveIziVerifyResult(requestLog);

            return iziResponse;
        }
    }

    public IziMultiInquiriesV1Response checkKtpMultiInquiriesV1(String idCardNo, OrdOrder order){
        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        Map<String, String> data = new HashMap<>();
        data.put("id", idCardNo);
        String response = api.Request(this.iziConfig.getKtpMultiInquiriesV1Url(), data);

        log.info("the izi KtpMultiInquiriesV1 response of orderNo: {} is: {}", order.getUuid(), response);
        if (StringUtils.isEmpty(response)) {
            log.info("the response of izi KtpMultiInquiriesV1 is empty, orderNo: {}", order.getUuid());
            return null;
        } else {
            IziMultiInquiriesV1Response iziResponse = JsonUtils.deserialize(response, IziMultiInquiriesV1Response.class);

            IziRequestLog requestLog = new IziRequestLog(order.getUuid(),order.getUserUuid(),IziInvokeType.IZI_MULTIINQUIRIESV1,
                iziResponse.getStatus(), response, JsonUtils.serialize(data));
            saveIziVerifyResult(requestLog);
            return iziResponse;
        }
    }

    public void saveIziWhatsApp(String orderNo, String userUuid, String number, IziWhatsappDetail iziWhatsappDetail,
                                String numberType) {
        UserIziVerifyResultMongo mongo = new UserIziVerifyResultMongo();
        mongo.setIziVerifyType("4");
        mongo.setIziVerifyResult(iziWhatsappDetail.getStatus());
        mongo.setIziVerifyResponse(JsonUtils.serialize(iziWhatsappDetail));
        mongo.setOrderNo(orderNo);
        mongo.setUserUuid(userUuid);
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(new Date());
        mongo.setWhatsAppNumberType(numberType);
        mongo.setWhatsAppNumber(number);
        if (iziWhatsappDetail != null && "OK".equals(iziWhatsappDetail.getStatus())) {
            IziWhatsappDetail.MessageDetail msgDetal = JsonUtils.deserialize(JsonUtils.serialize(iziWhatsappDetail.getMessage()),
                    IziWhatsappDetail.MessageDetail.class);
            mongo.setWhatsapp(msgDetal.getWhatsapp());
        } else {
            mongo.setWhatsapp("");
        }
        userIziVerifyResultMongoDal.insert(mongo);
    }


    // 存储izi结果
    public void saveIziVerifyResult(IziRequestLog requestLog) {

        //2019-05-15将其放回
        UsrIziVerifyResult result = new UsrIziVerifyResult();
        result.setIziVerifyType(requestLog.getType().getType());
        result.setIziVerifyResult(requestLog.getStatus());
        result.setIziVerifyResponse(requestLog.getResponseStr());
        result.setOrderNo(requestLog.getOrderNo());
        result.setUserUuid(requestLog.getUserUuid());
        this.usrIziVerifyResultDao.insert(result);

        /**
         *  20190318 切换到mongo存储izi结果
         * */
        UserIziVerifyResultMongo mongo = new UserIziVerifyResultMongo();
        mongo.setIziVerifyType(requestLog.getType().getType());
        mongo.setIziVerifyResult(requestLog.getStatus());
        mongo.setIziVerifyResponse(requestLog.getResponseStr());
        mongo.setOrderNo(requestLog.getOrderNo());
        mongo.setUserUuid(requestLog.getUserUuid());
        mongo.setRequestParam(requestLog.getRequestParam());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(new Date());
        userIziVerifyResultMongoDal.insert(mongo);

    }

    public String getIziResponse(String orderNo, String type) {
        if (StringUtil.isEmpty(orderNo)) {
            return null;
        }
        UsrIziVerifyResult searchParam = new UsrIziVerifyResult();
        searchParam.setIziVerifyType(type);
        searchParam.setOrderNo(orderNo);
        List<UsrIziVerifyResult> dbResult = this.usrIziVerifyResultDao.scan(searchParam);
        if (CollectionUtils.isEmpty(dbResult)) {
            //从mongo查
            List<UserIziVerifyResultMongo> mongoResultList = getIziResultListFromMongoByOrderNo(orderNo,type);
            if (CollectionUtils.isEmpty(mongoResultList)) {
                return null;
            } else {
                return mongoResultList.get(0).getIziVerifyResponse();
            }
        } else {
            return dbResult.get(0).getIziVerifyResponse();
        }
    }

    public String getLatestIziResponseByUserUuid(String userUuid, String type) {
        if (StringUtil.isEmpty(userUuid)) {
            return null;
        }
        //从mongo查
        UserIziVerifyResultMongo userIziVerifyResultMongo = getLatestIziResponseFromMongoByUserUuid(userUuid, type);
        if (userIziVerifyResultMongo != null) {
            return userIziVerifyResultMongo.getIziVerifyResponse();
        } else {
            //从db取
            UsrIziVerifyResult searchParam = new UsrIziVerifyResult();
            searchParam.setIziVerifyType(type);
            searchParam.setUserUuid(userUuid);
            UsrIziVerifyResult dbResult = this.usrIziVerifyResultDao.getLatestResult(userUuid, type);
            if (dbResult != null) {
                return dbResult.getIziVerifyResponse();
            } else {
                return null;
            }
        }
    }

    private UserIziVerifyResultMongo getLatestIziResponseFromMongoByUserUuid(String userUuid,String type){
        List<UserIziVerifyResultMongo> mongoResultList = getIziResultListFromMongoByUserUuid(userUuid, type);
        if(CollectionUtils.isEmpty(mongoResultList)){
            return null;
        }
        List<UserIziVerifyResultMongo> hasDateResult = mongoResultList.stream().filter(elem -> elem.getCreateTime() != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(hasDateResult)) {
            return mongoResultList.get(0);
        } else {
            return hasDateResult.stream().max(Comparator.comparing(UserIziVerifyResultMongo::getCreateTime)).get();
        }
    }

    public String getLatestIziResponseByUserUuidWithDateLimit(String userUuid, String type,Integer limitDay){
        if (StringUtil.isEmpty(userUuid)) {
            return null;
        }

        UsrIziVerifyResult dbResult = this.usrIziVerifyResultDao.getLatestResult(userUuid,type);
        if (dbResult == null) {
            //从mongo查
            List<UserIziVerifyResultMongo> mongoResultList = getIziResultListFromMongoByUserUuid(userUuid, type);
            if (CollectionUtils.isEmpty(mongoResultList)) {
                return null;
            } else {
                List<UserIziVerifyResultMongo> hasDateResult =
                        mongoResultList.stream().filter(elem -> elem.getCreateTime() != null && DateUtils.getDiffDaysIgnoreHours(elem.getCreateTime(),
                                new Date()) <= limitDay).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(hasDateResult)) {
                    return null;
                } else {
                    return hasDateResult.stream().max(Comparator.comparing(UserIziVerifyResultMongo::getCreateTime)).get().getIziVerifyResponse();
                }

            }

        } else {
            if (DateUtils.getDiffDaysIgnoreHours(dbResult.getCreateTime(), new Date()) <= limitDay) {
                return dbResult.getIziVerifyResponse();
            } else {
                return null;
            }
        }
    }


    private List<UserIziVerifyResultMongo> getIziResultListFromMongoByUserUuid(String userUuid, String type){
        if(StringUtil.isEmpty(userUuid)){
            return new ArrayList<>();
        }
        UserIziVerifyResultMongo mongoSearchParam = new UserIziVerifyResultMongo();
        mongoSearchParam.setIziVerifyType(type);
        mongoSearchParam.setUserUuid(userUuid);
        List<UserIziVerifyResultMongo> mongoResultList = userIziVerifyResultMongoDal.find(mongoSearchParam);
        return mongoResultList;
    }



    public List<UserIziVerifyResultMongo> getIziResultListFromMongoByOrderNo(String orderNo, String type){
        if(StringUtil.isEmpty(orderNo)){
            return new ArrayList<>();
        }
        UserIziVerifyResultMongo mongoSearchParam = new UserIziVerifyResultMongo();
        mongoSearchParam.setIziVerifyType(type);
        mongoSearchParam.setOrderNo(orderNo);
        List<UserIziVerifyResultMongo> mongoResultList = userIziVerifyResultMongoDal.find(mongoSearchParam);
        return mongoResultList;
    }


    public List<UserIziVerifyResultMongo> getLatestWhatsAppCheckResultList(String orderNo) {
        List<UserIziVerifyResultMongo> whatsAppResult = getIziResultListFromMongoByOrderNo(orderNo, "4");
        if (CollectionUtils.isEmpty(whatsAppResult)) {
            return null;
        }
        // //取最后一次
        List<UserIziVerifyResultMongo> lastRecordList = new ArrayList<>();
        List<String> sequenceList = Arrays.asList("1", "2", "4", "5", "0");
        for (String seq : sequenceList) {
            Optional<UserIziVerifyResultMongo> whatsappResult =
                    whatsAppResult.stream().filter(elem -> seq.equals(elem.getWhatsAppNumberType())).max(Comparator.comparing(UserIziVerifyResultMongo::getCreateTime));
            if (whatsappResult.isPresent()) {
                lastRecordList.add(whatsappResult.get());
            }
        }
        return lastRecordList;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class IziRequestLog{
        private String orderNo;
        private String userUuid;
        private IziInvokeType type;
        private String status;
        private String responseStr;
        private String requestParam;
    }

    @Getter
    public enum IziInvokeType{
        PHONE_AGE("1"),
        PHONE_VERIFY("2"),
        REALNAME_IDENTITY_CHECK("3"),
        IZI_WHATSAPP("4"),
        WHATS_APP_DETAIL("5"),
        BLACKLIST("6"),
        IZI_MULTIINQUIRIESV1("7")
        ;
        private IziInvokeType(String type){
            this.type = type;
        }
        private String type ;
    }

    @Getter
    @Setter
    public static class IziWhatsappDetail {

        private String status;
        private Object message;

        @Getter
        @Setter
        public static class MessageDetail {
            private String number;
            private String whatsapp;
        }
    }

    @Getter
    @Setter
    public static class IziRealNameVerifyResult {
        private String status;
        private Object message;

        @Getter
        @Setter
        public static class MessageDetail {
            private String id;
            private String name;
            @JsonProperty(value = "place_of_birth")
            private String placeOfBirth;
            @JsonProperty(value = "date_of_birth")
            private String dateOfBirth;
            private String gender;
            private String address;
            private String province;
            private String city;
            private String district;
            private String village;
            private String rt;
            private String rw;
            private String religion;
            @JsonProperty(value = "marital_status")
            private String maritalStatus;
            private String work;
            private String nationnality;
            private Boolean match;
        }
    }
}
