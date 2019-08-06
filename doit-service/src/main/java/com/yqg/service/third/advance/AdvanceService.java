package com.yqg.service.third.advance;

import ai.advance.sdk.client.OpenApiClient;
import com.alibaba.fastjson.JSON;
import com.yqg.common.utils.JsonUtils;
import com.yqg.mongo.repository.AdvanceRepository;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.AdvanceBlacklistDao;
import com.yqg.risk.dao.AdvanceBlacklistDetailDao;
import com.yqg.risk.dao.AdvanceMultiPlatformDao;
import com.yqg.risk.entity.AdvanceBlacklist;
import com.yqg.risk.entity.AdvanceBlacklistDetail;
import com.yqg.risk.entity.AdvanceMultiPlatform;
import com.yqg.service.third.advance.config.AdvanceConfig;
import com.yqg.service.third.advance.response.BlacklistCheckResponse;
import com.yqg.service.third.advance.response.BlacklistCheckResponse.DataDetail.EventDetail;
import com.yqg.service.third.advance.response.MultiPlatformResponse;
import com.yqg.service.third.advance.response.MultiPlatformResponse.DataDetail.StatisticsDetail.LastTwoWeeksQueryInfoDetail;
import com.yqg.service.third.advance.response.MultiPlatformResponse.DataDetail.StatisticsDetail.StatisticCustomerInfoDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class AdvanceService {
    @Autowired
    private AdvanceConfig advanceConfig;
    @Autowired
    private AdvanceRepository advanceRepository;
    @Autowired
    private AdvanceMultiPlatformDao advanceMultiPlatformDao;
    @Autowired
    private AdvanceBlacklistDao advanceBlacklistDao;
    @Autowired
    private AdvanceBlacklistDetailDao advanceBlacklistDetailDao;

    /***
     * advance实名验证
     * @param params
     * @return
     */
    public String identityCheck(Map<String, String> params) {

        OpenApiClient client = new OpenApiClient(advanceConfig.getApiHost(),
                advanceConfig.getAccessKey(), advanceConfig.getSecretKey());
        //计时开始
        Long startTime = System.currentTimeMillis();
        // 调用接口
        String response = null;
        try {
            response = client
                    .request(advanceConfig.getIdentityCheckApi(), JSON.toJSONString(params));
        } catch (Exception e) {
            log.error("request to advance exception , param：" + JSON.toJSONString(params), e);
        }
        log.info("实名认证耗时:{} ms, response: {} ,param: {}", System.currentTimeMillis() - startTime,
                response, JSON.toJSONString(params));
        return response;
    }


    /****
     * https://doc.advance.ai/production/blacklist.html#blacklist-check
     * @param realName
     * @param idNumber
     * @param phoneNumber
     */
    public BlacklistCheckResponse checkBlacklist(OrdOrder order ,String realName, String idNumber, String phoneNumber) {
        OpenApiClient client = new OpenApiClient(advanceConfig.getApiHost(),
                advanceConfig.getAccessKey(), advanceConfig.getSecretKey());
        Map<String, String> phoneNumberMap = new HashMap<>();
        phoneNumberMap.put("countryCode", "+62");
        phoneNumberMap.put("areaCode", "");
        phoneNumberMap.put("number", phoneNumber);

        Map<String, Object> blacklistCheck = new HashMap<>();
        blacklistCheck.put("name", realName);
        blacklistCheck.put("idNumber", idNumber);
        blacklistCheck.put("phoneNumber", phoneNumberMap);

        String reqStr = JsonUtils.serialize(blacklistCheck);
        String response = client.request(advanceConfig.getBlacklistCheck(), reqStr);
        log.info("the response of blacklist check with param: {} , response {}",reqStr,response);

        advanceRepository.saveData(order,response,advanceConfig.getBlacklistCheck());

        if (StringUtils.isEmpty(response)) {
            return null;
        }
        BlacklistCheckResponse respData = JsonUtils.deserialize(response, BlacklistCheckResponse.class);
        saveBlacklistData(order, respData);
        return respData;
    }


    /***
     * https://doc.advance.ai/production/multi_platform_detect.html#multi-platform-detection
     * @param idNumber
     * @return
     */
    public MultiPlatformResponse checkMultiPlatform(OrdOrder order,String idNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("idNumber", idNumber);
        OpenApiClient client = new OpenApiClient(advanceConfig.getApiHost(),
                advanceConfig.getAccessKey(), advanceConfig.getSecretKey());
        String reqStr = JsonUtils.serialize(params);
        String response = client.request(advanceConfig.getMultiPlatform(), reqStr);
        log.info("the response of multi-platform check with param: {} , response {}",idNumber,response);

        advanceRepository.saveData(order,response,advanceConfig.getMultiPlatform());
        if (StringUtils.isEmpty(response)) {
            return null;
        }
        MultiPlatformResponse respData = JsonUtils.deserialize(response, MultiPlatformResponse.class);
        saveMultiPlatformData(order, respData);
        return respData;
    }


    public void saveMultiPlatformData(OrdOrder order, MultiPlatformResponse response , Date... tmp){
        AdvanceMultiPlatform saveData = new AdvanceMultiPlatform();

        saveData.setCreateTime(new Date());
        saveData.setUpdateTime(new Date());

        saveData.setOrderNo(order.getUuid());
        saveData.setUserUuid(order.getUserUuid());
        saveData.setDisabled(0);
        if(response!=null && response.getData()!=null && response.getData().getStatistics()!=null){
            List<StatisticCustomerInfoDetail> customerInfoDetailList =
                    response.getData().getStatistics().getStatisticCustomerInfo();

            if(CollectionUtils.isEmpty(customerInfoDetailList)){
                return;
            }

            saveData.setTp1To7D(getQueryCountFromList(customerInfoDetailList,"1-7d"));
            saveData.setTp1To14D(getQueryCountFromList(customerInfoDetailList,"1-14d"));
            saveData.setTp1To21D(getQueryCountFromList(customerInfoDetailList,"1-21d"));
            saveData.setTp1To30D(getQueryCountFromList(customerInfoDetailList,"1-30d"));
            saveData.setTp1To60D(getQueryCountFromList(customerInfoDetailList,"1-60d"));
            saveData.setTp1To90D(getQueryCountFromList(customerInfoDetailList,"1-90d"));

            List<LastTwoWeeksQueryInfoDetail> queryInfoDetails = response.getData().getStatistics().getLastTwoWeeksQueryInfo();
            if(!CollectionUtils.isEmpty(queryInfoDetails)){
                saveData.setMqc1H(getQueryCountOfTimeSlice(queryInfoDetails,"1-HOUR"));
                saveData.setMqc3H(getQueryCountOfTimeSlice(queryInfoDetails,"3-HOUR"));
                saveData.setMqc6H(getQueryCountOfTimeSlice(queryInfoDetails,"6-HOUR"));
                saveData.setMqc12H(getQueryCountOfTimeSlice(queryInfoDetails,"12-HOUR"));
                saveData.setMqc24H(getQueryCountOfTimeSlice(queryInfoDetails,"24-HOUR"));
            }
            advanceMultiPlatformDao.insert(saveData);
        }
    }


    public void saveBlacklistData(OrdOrder order, BlacklistCheckResponse response , Date... tmp) {
        if (response == null || response.getData() == null) {
            return;
        }
        AdvanceBlacklist advanceBlacklist = new AdvanceBlacklist();
        advanceBlacklist.setOrderNo(order.getUuid());
        advanceBlacklist.setUserUuid(order.getUserUuid());
        advanceBlacklist.setCreateTime(new Date());
        advanceBlacklist.setUpdateTime(new Date());
        advanceBlacklist.setDisabled(0);
        advanceBlacklist.setRecommendation(response.getData().getRecommendation());

        advanceBlacklistDao.insert(advanceBlacklist);
        List<EventDetail> eventList = response.getData().getDefaultListResult();
        if(CollectionUtils.isEmpty(eventList)){
            return ;
        }
        List<AdvanceBlacklistDetail> saveList = eventList.stream().map(elem->{
            AdvanceBlacklistDetail detail = new AdvanceBlacklistDetail();
            detail.setOrderNo(order.getUuid());
            detail.setUserUuid(order.getUuid());
            detail.setCreateTime(new Date());
            detail.setUpdateTime(new Date());
            detail.setDisabled(0);
            detail.setEventTime(elem.getEventTime());
            detail.setHitReason(elem.getHitReason());
            detail.setReasonCode(elem.getReasonCode());
            detail.setProductType(elem.getProductType());
            return detail;
        }).collect(Collectors.toList());
        advanceBlacklistDetailDao.insertBatch(saveList);
    }


    private Integer getQueryCountFromList(List<StatisticCustomerInfoDetail> detailList,String type){
       Optional<StatisticCustomerInfoDetail> detail =
               detailList.stream().filter(elem->elem.getTimePeriod().equalsIgnoreCase(type)).findFirst();
       if(detail.isPresent()){
           return detail.get().getQueryCount();
       }
       return null;
    }

    private Integer getQueryCountOfTimeSlice(List<LastTwoWeeksQueryInfoDetail> queryInfoDetails, String timeSlice) {

        Optional<LastTwoWeeksQueryInfoDetail> detail =
                queryInfoDetails.stream().filter(elem -> elem.getTimeSlice().equalsIgnoreCase(timeSlice)).findFirst();
        if (detail.isPresent()) {
            return detail.get().getMaxQueryCount();
        }
        return null;
    }


}
