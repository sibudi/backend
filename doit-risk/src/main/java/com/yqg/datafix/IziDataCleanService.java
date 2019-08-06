package com.yqg.datafix;

import com.github.pagehelper.StringUtil;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.scheduling.RiskDataSynService;
import com.yqg.service.third.izi.Client;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.config.IziConfig;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.service.user.service.UserDetailService;
import com.yqg.user.entity.UsrIziVerifyResult;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.util.JSONUtils;
import org.drools.core.io.impl.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/***
 * izi数据清洗服务
 */
@Slf4j
@Service
public class IziDataCleanService {

    @Autowired
    private OrdService ordService;
    @Autowired
    private UserService userService;
    @Autowired
    private IziConfig iziConfig;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private IziService iziService;
    @Autowired
    private RiskDataSynService riskDataSynService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;

    public void iziPhoneAge() {
        List<String> orders = getDataCleanOrders("maojinglin.txt");
        List<String> orders2 = getDataCleanOrders("sherry.txt");
        if(CollectionUtils.isEmpty(orders)){
            orders = orders2;
        }else{
            if(!CollectionUtils.isEmpty(orders2)){
                orders.addAll(orders2);
            }
        }
        if (CollectionUtils.isEmpty(orders)) {
            return;
        }
        for (String orderNo : orders) {
            try {
                OrdOrder order = ordService.getOrderByOrderNo(orderNo);
                if (order == null) {
                    continue;
                }
                UsrUser user = userService.getUserInfo(order.getUserUuid());
                String phone = DESUtils.decrypt(user.getMobileNumberDES());
                testIziPhoneAge(order.getUserUuid(), orderNo, phone);
            } catch (Exception e) {
                log.error("error to get iziPhoneAge for " + orderNo, e);
            }
        }
    }

    private void testIziPhoneAge(String userUuid, String orderNo, String mobileNumber) {

        Client api = new Client(this.iziConfig.getAccessKey(), this.iziConfig.getSecretKey());
        api.setConnectionTimeoutInMillis(60000);
        api.setSocketTimeoutInMillis(60000);
        Map<String, String> data = new HashMap<>();
        data.put("phone", mobileNumber); // 必选，字符串，需要验证的电话号
        String responseStr = api.Request(this.iziConfig.getPhoneAgeUrl(), data);
        IziResponse response = JsonUtils.deserialize(responseStr, IziResponse.class);
        if (response != null && StringUtil.isEmpty(response.getStatus())) {
            response.setStatus(responseStr);
        }
        //保存结果
        UsrIziVerifyResult result = new UsrIziVerifyResult();
        result.setIziVerifyType("1");
        result.setIziVerifyResult(response.getStatus());
        result.setIziVerifyResponse(responseStr);
        result.setOrderNo(orderNo);
        result.setUserUuid(userUuid);
        riskDataSynService.addIziDataClean(result);
        log.info("orderNo: {} , response: {}", orderNo, response.toString());

    }

    private void testIziWhatsApp(String userUuid, String orderNo) {
        try {
            log.info("check izi whatsapp for user: {}", userUuid);
            //查询whatsapp账号
            UsrUser user = userService.getUserInfo(userUuid);
            String ownerWhatsapp = DESUtils.decrypt(user.getMobileNumberDES());

            if(!StringUtils.isEmpty(ownerWhatsapp)){
                boolean existsData = riskDataSynService.hasSuccessWhatsappResult(ownerWhatsapp, orderNo);
                if(!existsData){
                    IziResponse iziResponse = iziService.whatsAppIsOpen(ownerWhatsapp, orderNo, userUuid);
                    saveIziWhatsApp(orderNo,userUuid, JsonUtils.serialize(iziResponse),ownerWhatsapp,"0");
                }
            }
            List<UsrLinkManInfo> usrList = userBackupLinkmanService.getLinkManInfo(userUuid);
            //检查相同号码是否已经跑过而且而且成功
            for (UsrLinkManInfo elem : usrList) {
                if (StringUtils.isEmpty(elem.getContactsMobile())) {
                    continue;
                }
                String number = CheakTeleUtils.telephoneNumberValid2(elem.getContactsMobile());
                if(StringUtils.isEmpty(number)){
                    number = elem.getContactsMobile();
                }
                if (StringUtils.isEmpty(number)) {
                    continue;
                }

                boolean existsData = riskDataSynService.hasSuccessWhatsappResult(number, orderNo);
                if(!existsData){
                    IziResponse elemResponse = iziService.whatsAppIsOpen(number, orderNo, userUuid);
                    saveIziWhatsApp(orderNo,userUuid, JsonUtils.serialize(elemResponse),number,elem.getSequence().toString());
                }
            }
        } catch (Exception e) {
            log.info("invoke izi error for whatsapp,userUuid: " + userUuid, e);
        }
    }


    private void saveIziWhatsApp(String orderNo,String userUuid,String iziResponse,String whatsappNumber,String sequence){
        IziService.IziWhatsappDetail detail =  JsonUtils.deserialize(iziResponse,
                IziService.IziWhatsappDetail.class);

        UserIziVerifyResultMongo mongo = new UserIziVerifyResultMongo();
        mongo.setIziVerifyType("4");
        mongo.setIziVerifyResult(detail.getStatus());
        mongo.setIziVerifyResponse(JsonUtils.serialize(detail));
        mongo.setOrderNo(orderNo);
        mongo.setUserUuid(userUuid);
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(new Date());
        mongo.setWhatsAppNumberType(sequence);
        mongo.setWhatsAppNumber(whatsappNumber);
        Boolean isChecking = false;
        if (detail != null && "OK".equals(detail.getStatus())) {
            IziService.IziWhatsappDetail.MessageDetail msgDetal = JsonUtils.deserialize(JsonUtils.serialize(detail.getMessage()),
                    IziService.IziWhatsappDetail.MessageDetail.class);
            isChecking = "checking".equalsIgnoreCase(msgDetal.getWhatsapp());
            mongo.setWhatsapp(msgDetal.getWhatsapp());
        } else {
            mongo.setWhatsapp("");
        }

        if(isChecking){
            //放入redis下次继续处理
            redisClient.listAdd("izi:dataclean:whatsapp",mongo);
        }else{
            riskDataSynService.addIziWhatsApp(mongo);
        }
    }

    public void iziWhatsapp(){
        List<String> orders = getDataCleanOrders("maojinglin.txt");
        List<String> orders2 = getDataCleanOrders("sherry.txt");
        if(CollectionUtils.isEmpty(orders)){
            orders = orders2;
        }else{
            if(!CollectionUtils.isEmpty(orders2)){
                orders.addAll(orders2);
            }
        }

        if (CollectionUtils.isEmpty(orders)) {
            return;
        }

        for (String orderNo : orders) {
            try {
                OrdOrder order = ordService.getOrderByOrderNo(orderNo);
                if (order == null) {
                    continue;
                }
                testIziWhatsApp(order.getUserUuid(), orderNo);
            } catch (Exception e) {
                log.error("error to get iziwhatsapp for " + orderNo, e);
            }
        }

    }

    public void iziWhatsAppRetry() {

        while(true){
            try {
                Thread.sleep(1000 * 60 * 1L);
            } catch (InterruptedException e) {
                log.error("retry sleep error", e);
            }
            UserIziVerifyResultMongo retryData = redisClient.listGetTail("izi:dataclean:whatsapp", UserIziVerifyResultMongo.class);
            int i = 0;
            String firstOrder = ""; //每一轮结束后都停止2min
            while (retryData != null) {
                try {
                    if (0 == i++) {
                        firstOrder = retryData.getOrderNo();
                    }
                    if (firstOrder.equalsIgnoreCase(retryData.getOrderNo())) {
                        try {
                            Thread.sleep(1000 * 60L);
                        } catch (InterruptedException e) {
                            log.error("retry sleep error", e);
                        }
                    }
                    //检查db中是否已经保存，没有的化重更新跑
                    boolean existsData = riskDataSynService.hasWhatsappResult(retryData.getWhatsAppNumber(), retryData.getOrderNo());
                    if (existsData) {
                        //取下一个
                        retryData = redisClient.listGetTail("izi:dataclean:whatsapp", UserIziVerifyResultMongo.class);
                        continue;
                    }
                    //重新发送
                    testIziWhatsAppRetry(retryData);
                    //取下一个
                    retryData = redisClient.listGetTail("izi:dataclean:whatsapp", UserIziVerifyResultMongo.class);
                } catch (Exception e) {
                    log.error("retry error", e);
                    redisClient.listAdd("izi:dataclean:whatsapp",retryData);
                }
            }

            //无数据
            log.info("no data for loop");

        }

    }




    private void testIziWhatsAppRetry(UserIziVerifyResultMongo retryElem) {
        try {
            log.info("check izi whatsapp for user: {}, mobile : {}", retryElem.getUserUuid(),retryElem.getWhatsAppNumber());

            IziResponse iziResponse = iziService.whatsAppIsOpen(retryElem.getWhatsAppNumber(), retryElem.getOrderNo(), retryElem.getUserUuid());

            saveIziWhatsApp(retryElem.getOrderNo(), retryElem.getUserUuid(), JsonUtils.serialize(iziResponse),retryElem.getWhatsAppNumber(),
                    retryElem.getWhatsAppNumberType());

        } catch (Exception e) {
            log.info("invoke izi error for whatsapp,userUuid retry: " + retryElem.getUserUuid(), e);
        }
    }


    public static void main(String[] args) {
     String str = "{\"disabled\":null,\"uuid\":null,\"createTime\":1556163663380,\"updateTime\":1556163663380,\"remark\":null,\"id\":null,\"userUuid\":\"4C28266316DF4A078DA431AAA0E63E5B\",\"orderNo\":\"011806140421431420\",\"iziVerifyType\":\"4\",\"iziVerifyResult\":\"OK\",\"iziVerifyResponse\":\"{\\\"status\\\":\\\"OK\\\",\\\"message\\\":{\\\"number\\\":\\\"+6283806948321\\\",\\\"whatsapp\\\":\\\"checking\\\"}}\",\"whatsapp\":\"checking\",\"whatsAppNumber\":\"83806948321\",\"whatsAppNumberType\":\"2\"}";

        System.err.println(str);
     UserIziVerifyResultMongo mongo = JsonUtils.deserialize(str,UserIziVerifyResultMongo.class);
        System.err.println("finished");

    }

    public static List<String> getDataCleanOrders(String fileName) {
        List<String> resultList = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream in =  resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!StringUtils.isEmpty(line)) {
                    resultList.add(line.trim());
                }
            }

            reader.close();
        } catch (Exception e) {
            log.error("read orders error", e);
        }

        return resultList;
    }


}
