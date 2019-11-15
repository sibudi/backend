package com.yqg.service.third.infinity;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ThirdExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.third.InfinityApp;
import com.yqg.common.utils.*;
import com.yqg.management.dao.InfinityBillDao;
import com.yqg.management.dao.InfinityExtNumberDao;
import com.yqg.management.entity.InfinityBillEntity;
import com.yqg.management.entity.InfinityExtnumberEntity;
import com.yqg.order.dao.ManOrderDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.infinity.request.InfinityRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InfinityService {

    @Autowired
    RedisClient redisClient;
    @Autowired
    InfinityExtNumberDao infinityExtNumberDao;
    @Autowired
    InfinityBillDao infinityBillDao;

    @Autowired
    private ManOrderDao manOrderDao;

    public String getToken() throws Exception {
        log.info("..........getToken start..........");

        String token = redisClient.get(RedisContants.THIRD_INFINITY_TOKEN);
        if (!StringUtils.isBlank(token)) {
            return token;
        }

        log.info("..........getToken end..........");
        return getInterfaceToken().get("token");
    }

    public Map<String, String> getInterfaceToken() throws Exception {
        log.info("..........getInterfaceToken start..........");

        //http请求头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
//        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("service", InfinityApp.SERVICE_TOKEN);
        contentMap.put("appid", InfinityApp.APP_ID);
        contentMap.put("accesskey", InfinityApp.ACCESS_KEY);
        String resultRes = HttpTools.post(InfinityApp.SERVICE_URL, headers, contentMap, 30000, 30000);
        log.info("-------------------token:" + resultRes);
        Map<String, String> map = null;
        if (JsonUtils.isJSONValid(resultRes)) {
            map = this.ResultToMap(resultRes);
        }
        if (map != null && !StringUtils.isEmpty(map.get("errorCode"))) {
            this.throwException(map);
        }
        redisClient.set(RedisContants.THIRD_INFINITY_TOKEN, map.get("token"), RedisContants.EXPIRES_COUNT_SECOND);
        log.info("调用接口获取token---------------:", map.get("token"));

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", map.get("token"));
        log.info("..........getInterfaceToken end..........");

        return tokenMap;
    }

    /**
     * 从数据库里查询分机在线分机数量
     *
     * @param contentMap
     * @return
     */
    public List<InfinityExtnumberEntity> getExtNumberList(Map<String, String> contentMap) {
        log.info("..........getExtNumberList..........");
        return infinityExtNumberDao.getExtnumberList();
    }

    /**
     * 给定时任务的服务
     * <p>
     * 查询分机在线状态
     *
     * @param contentMap
     */
    public void updateExtNumber(Map<String, String> contentMap) {
        log.info("..........updateExtNumber start..........");

        Map<String, String> map = null;
        //HTTP请求参数
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
        contentMap.put("service", InfinityApp.SERVICE_NUMBER_INFO);

        String resultRes = HttpTools.post(InfinityApp.SERVICE_URL, headers, contentMap, 30000, 30000);
        if (JsonUtils.isJSONValid(resultRes)) {
            List<InfinityExtnumberEntity> entityList = JsonUtils.toList(this.ResultToMap(resultRes).get("resultJson"), InfinityExtnumberEntity.class);
            if (entityList != null | entityList.size() > 0) {
                for (InfinityExtnumberEntity entity : entityList) {
                    log.info("更新分机数据" + entity.getExtnumber() + ":" + entity.getStatus());
                    int num = infinityExtNumberDao.updateStatusByExtnumber(entity.getExtnumber(), entity.getStatus());
                    if (num < 1) {
                        entity.setUuid(UUIDGenerateUtil.uuid());
                        infinityExtNumberDao.insertExtNumber(entity);
                    }
                }
            }
        }

        log.info("..........updateExtNumber end..........");
    }

    public List<InfinityExtnumberEntity> getCallStatus(Map<String, String> contentMap) throws Exception {
        log.info("..........getCallStatus..........");

        Map<String, String> map = null;
        //http请求头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
        //HTTP请求参数
        contentMap.put("service", InfinityApp.SERVICE_CALL_STATUS);
        String resultRes = HttpTools.post(InfinityApp.SERVICE_URL, headers, contentMap, 30000, 30000);
        if (JsonUtils.isJSONValid(resultRes)) {
            map = this.ResultToMap(resultRes);
        }
        if (!StringUtils.isEmpty(map.get("errorCode"))) {
            this.throwException(map);
        }

        if (JsonUtils.isJSONValid(map.get("resultJson"), List.class)) {
            log.info("..........:{}", map.get("resultJson"));

            return JsonUtils.toList(map.get("resultJson"), InfinityExtnumberEntity.class);
        }
        log.info("..........updateExtNumber end return null..........");

        return null;
    }

    public Map<String, String> makeCall(InfinityRequest req) throws Exception {

        //规范号码格式(公司电话不需要格式化）
        if (req.getCallType() == null || !req.getCallType().equals(2)) {
            req.setDestnumber(CheakTeleUtils.telephoneNumberValid2(req.getDestnumber()));
        }
        Map<String, String> map = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("token", this.getToken());
        contentMap.put("extnumber", req.getExtnumber());
        contentMap.put("destnumber", req.getDestnumber());
        String userId = UUIDGenerateUtil.uuid();
        contentMap.put("userid", userId);
        List<InfinityExtnumberEntity> entityList = this.getCallStatus(contentMap);
        if (entityList == null || entityList.size() < 1) {
            this.throwException(map);
        }
        if (!"1201".contentEquals(entityList.get(0).getStatus())) {
            InfinityExtnumberEntity entity = entityList.get(0);
            map.put("errorCode", entity.getStatus());
            this.throwException(map);
        }
        //http请求头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
        //HTTP请求参数
        contentMap.put("service", InfinityApp.SERVICE_MAKE_CALL);
        String resultRes = HttpTools.post(InfinityApp.SERVICE_URL, headers, contentMap, 30000, 30000);
        if (JsonUtils.isJSONValid(resultRes)) {
            map = this.ResultToMap(resultRes);
        }
        if (!StringUtils.isEmpty(map.get("errorCode"))) {
            this.throwException(map);
        }
        //查询订单申请期限和金额
        InfinityBillEntity entity = new InfinityBillEntity();
        if (StringUtils.isNotEmpty(req.getOrderNo())) {
            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setUuid(req.getOrderNo());
            ordOrder.setDisabled(0);
            List<OrdOrder> ordOrders = manOrderDao.scan(ordOrder);
            if (CollectionUtils.isEmpty(ordOrders)) {
                return map;
            }
            entity.setApplyAmount(ordOrders.get(0).getAmountApply());
            entity.setApplyDeadline(ordOrders.get(0).getBorrowingTerm());
            entity.setRealName(req.getRealName());
            entity.setUserName(req.getUserName());
            entity.setCallNode(req.getCallNode());
            entity.setCallType(req.getCallType());
            entity.setOrderNo(req.getOrderNo());
            entity.setSourceType(0);
        } else {
            //调查问卷
            entity.setSourceType(1);
            entity.setRealName("-");
            entity.setUserName("-");
            entity.setApplyAmount(BigDecimal.ZERO);
            entity.setApplyDeadline(0);
            entity.setCallNode(0);
            entity.setCallType(0);
            entity.setOrderNo(req.getUuid());
        }
        entity.setUuid(UUIDGenerateUtil.uuid());
        entity.setExtnumber(req.getExtnumber());
        entity.setDestnumber(req.getDestnumber());
        entity.setUserid(userId);

        infinityBillDao.saveBill(entity);
        return map;
    }

    public List<InfinityBillEntity> getBill(Map<String, String> contentMap) throws Exception {
        log.info("..........getBill start..........");

        //http请求头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
        //HTTP请求参数
        contentMap.put("service", InfinityApp.SERVICE_GET_BILL);
        log.info("bill param is {}", contentMap);
        String resultRes = HttpTools.post(InfinityApp.SERVICE_URL, headers, contentMap, 30000, 30000);
        log.info("bill result is :" + resultRes);
        List<InfinityBillEntity> entityList = null;
        if (JsonUtils.isJSONValid(resultRes)) {
            Map<String, String> resultMap = this.ResultToMap(resultRes);
            if (!StringUtils.isEmpty(resultMap.get("errorCode"))) {
                this.throwException(resultMap);
            }
            Object bills = JsonUtils.toMap(resultMap.get("resultJson")).get("bills");
            log.info("getBill:" + bills);
            if (bills != null && !bills.toString().isEmpty()) {
                entityList = JsonUtils.toList(JsonUtils.serialize(bills), InfinityBillEntity.class);
            }

            if (entityList != null && entityList.size() > 0) {
                log.info("更新分机数据");
//                infinityBillDao.saveBillList(entityList);
//                List<InfinityRecodeFileEntity> fileList = JsonUtils.toList(JsonUtils.serialize(bills), InfinityRecodeFileEntity.class);
//                infinityRecodeFileDao.saveRecodeFilelList(fileList);
            }
        }
        log.info("..........getBill end..........");
        return entityList;
    }

    public Map<String, String> getRecodeFile(Map<String, String> contentMap) {
        log.info("..........getRecodeFile start..........");

        Map<String, String> map = null;
        //http请求头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
        //HTTP请求参数
        contentMap.put("service", InfinityApp.SERVICE_RECODE_FILE);
        String resultRes = HttpTools.post(InfinityApp.SERVICE_URL, headers, contentMap, 30000, 30000);
        log.info("..........RecodeFile：" + resultRes);

        if (JsonUtils.isJSONValid(resultRes)) {
            map = this.ResultToMap(resultRes);
        }

        log.info("..........getRecodeFile end..........");
        return map;
    }


    /**
     * 返回目标数据
     *
     * @param resultRes
     * @return
     */
    private Map<String, String> ResultToMap(String resultRes) {
        log.info("..........ResultToMap start..........");

        Map<String, String> map = new HashMap<>();
       /*{
            "ret":200, "data":{
            "status":0, "desc":"授权成功", "result":{
                "companycode":"200401", "companyname":"doit-ivr", "token":"7cf79e1943ca96c109af8d01ed6c2ec5", "authtime":"2019-12-31 00:00:00", "authmodel":""
            },"reqtime":1554278588, "rsptime":1554278588
        },"msg":""
        }*/
        Map<String, Object> returnMap = JsonUtils.toMap(resultRes);
        log.info("the third voice result is : {}" + resultRes);
        if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_200.getCode()).equals(returnMap.get("ret").toString())) {
            Map<String, Object> dataMap = JsonUtils.toMap(JsonUtils.serialize(returnMap.get("data")));
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_0.getCode()).contentEquals(dataMap.get("status").toString())) {
                if (dataMap.get("result") != null) {
                    String resultJson = JsonUtils.serialize(dataMap.get("result"));
                    if (JsonUtils.isJSONValid(resultJson, Map.class)) {
                        if (JsonUtils.toMap(resultJson).get("token") != null) {
                            map.put("token", JsonUtils.toMap(resultJson).get("token").toString());
                        } else {
                            map.put("resultJson", resultJson);
                        }
                    } else if (JsonUtils.isJSONValid(resultJson, List.class)) {
                        map.put("resultJson", resultJson);
                    }
                }

                /*if (resultMap.get("extnumber") != null) {
                    map.put("extnumber", resultMap.get("extnumber").toString());
                }
                if (resultMap.get("password") != null) {
                    map.put("password", resultMap.get("password").toString());
                }
                if (resultMap.get("status") != null) {
                    map.put("status", resultMap.get("status").toString());
                }*/
            } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1.getCode()).contentEquals(dataMap.get("status").toString())) {
                map.put("desc", StringUtils.replaceNull(returnMap.get("desc")));// desc为失败的中文描述
                String errorJson = JsonUtils.serialize(dataMap.get("errors"));
                if (JsonUtils.isJSONValid(errorJson, Map.class)) {
//                    Map<String, Object> errorMap = JsonUtils.toMap(errorJson);
//                    if(String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1001.getCode()).contentEquals(errorMap.get("status").toString())){
//                        map.put("errorCode", StringUtils.replaceNull(returnMap.get("code")));
//                        map.put("errorMsg", StringUtils.replaceNull(returnMap.get("codemsg")));
//                    }
                    map.put("errorCode", StringUtils.replaceNull(JsonUtils.toMap(errorJson).get("code")));
                    map.put("errorMsg", StringUtils.replaceNull(JsonUtils.toMap(errorJson).get("codemsg")));
                }
            }
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_400.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_500.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_600.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_601.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_602.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_603.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        } else if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_604.getCode()).contentEquals(returnMap.get("ret").toString())) {
            map.put("errorCode", returnMap.get("ret").toString());
            map.put("errorMsg", StringUtils.replaceNull(returnMap.get("msg")));
        }
        log.info("..........map:{}" + map.toString());

        log.info("..........ResultToMap end..........");
        return map;
    }

    /**
     * 调用第三方服务异常处理
     * service:App.Sip_
     *
     * @param map
     * @throws Exception
     */
    private void throwException(Map<String, String> map) throws Exception {
        if (!StringUtils.isEmpty(map.get("errorCode"))) {
            /**
             * 请求服务失败
             */
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_400.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_400);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_500.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_500);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_600.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_600);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_601.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_601);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_602.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_602);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_603.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_603);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_604.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_604);
            }

            /**
             * 请求服务成功，获取数据失败
             */
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1001.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1001);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1002.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1002);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1003.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1003);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1010.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1010);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1011.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1011);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1012.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1012);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1013.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1013);
            }
            if (String.valueOf(ThirdExceptionEnum.INFINITY_SIP_RESULT_1014.getCode()).contentEquals(map.get("errorCode"))) {
                throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_RESULT_1014);
            }

            log.error("异常啦:" + map.get("errorCode"));
        }
    }
}
