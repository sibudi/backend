package com.yqg.service.scheduling;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import com.yqg.service.order.OrdThirdDataService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.operators.OpratorsService;
import com.yqg.service.third.operators.request.OpratorsRequest;
import com.yqg.service.user.model.UserCertificationInfoInRedis;
import com.yqg.service.user.service.UserVerifyResultService;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrCertificationInfo;
import com.yqg.user.entity.UsrVerifyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by wanghuaizhou on 2017/12/20.
 */
@Component
@Slf4j
public class CheakThirdDataScheduling {

    @Value("${xl.url}")
    private  String xlUrl;
    @Value("${tokopedia.url}")
    private  String tokopediaUrl;
    @Value("${telk.url}")
    private  String telkurl;
    @Value("${telk.url2}")
    private  String telkurl2;
    @Value("${jxl.getReportDataurl}")
    private  String getGojekDataUrl;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OpratorsService opratorsService;
    @Autowired
    private OrdThirdDataService ordThirdDataService;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Autowired
    private UserVerifyResultService userVerifyResultService;
    /**
     *   获取爬虫第三方数据
     * */
    public void getThirdData() throws Exception{

        String number = this.sysParamService.getSysParamValue(SysParamContants.NUMBER_OF_CERTIFICATE);

        for (int count = Integer.parseInt(number) ;count > 0;count --){


            UserCertificationInfoInRedis certRedisEntity =  redisClient.listGetTail(RedisContants.USER_CERTIFICATION_LIST,UserCertificationInfoInRedis.class);

            try {
                if (certRedisEntity != null){
                    log.info("本次抓取的数据-----userUuid:{},orderNo:{},type:{}",certRedisEntity.getUserUuid(),certRedisEntity.getOrderNo(),certRedisEntity.getCertType());

                    OpratorsRequest request = new OpratorsRequest();

                    request.setBatchId(certRedisEntity.getBatchId());

                    String resultStr = "";
                    // ?????
                    String certType = certRedisEntity.getCertType();
                    if (certType.equals("tokoPedia")){
                        String pwdDes = DESUtils.decrypt(certRedisEntity.getPwd());
                        request.setPwd(pwdDes);
                    }else {
                        request.setPwd(certRedisEntity.getPwd());
                    }
                    if (certType.equals("xl")){

                        request.setPhoneNo(certRedisEntity.getPhoneNo());
//                        resultStr = saveDataToMongo(certRedisEntity,request,xlUrl,ThirdDataTypeEnum.XL_IDENTITY_DATA);
                        resultStr = this.opratorsService.getData(xlUrl,request);
                        log.info("获取xl数据================================"+resultStr);
                    }else if (certType.equals("tokoPedia")){

                        request.setEmail(certRedisEntity.getEmail());
                        request.setReport_task_token(certRedisEntity.getReport_task_token());
// ??
                        resultStr =  this.opratorsService.getGojekData(getGojekDataUrl,request);
                        log.info("获取tokoPedia数据================================"+resultStr);
                        if (!StringUtils.isEmpty(resultStr)){

                            JSONObject res = JSONObject.parseObject(resultStr);

                            String code = res.get("code").toString();
                            if (code.equals("20000")){
                                // TODO: ???????
                                OrderThirdDataMongo scan =  this.ordThirdDataService.getThridDataByOrderNo(certRedisEntity.getOrderNo(),ThirdDataTypeEnum.TOKOPEDIA_IDENTITY_DATA);
                                if (scan != null){

                                    this.ordThirdDataService.update(scan,resultStr);
                                }else {

                                    this.ordThirdDataService.add(resultStr,certRedisEntity.getOrderNo(),certRedisEntity.getUserUuid(), ThirdDataTypeEnum.TOKOPEDIA_IDENTITY_DATA,1);
                                }
                            }else  {
                                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                                log.info("***************获取tokoPedia数据，code不等于20000*************************");
                            }
                        }

                    }else if (certType.equals("telk1")){

                        request.setPhoneNo(certRedisEntity.getPhoneNo());
//                        resultStr = saveDataToMongo(certRedisEntity,request,telkurl,ThirdDataTypeEnum.TELK_IDENTITY_DATA);
                        resultStr = this.opratorsService.getData(telkurl,request);
                        log.info("获取telk1数据================================"+resultStr);
                    }else if (certType.equals("telk2")){

                        request.setPhoneNo(certRedisEntity.getPhoneNo());
//                        resultStr = saveDataToMongo(certRedisEntity,request,telkurl2,ThirdDataTypeEnum.TELK2_IDENTITY_DATA);
                        resultStr = this.opratorsService.getData(telkurl2,request);
                        log.info("获取telk2数据================================"+resultStr);
                    }else if (certType.equals("gojek")){

                        request.setReport_task_token(certRedisEntity.getReport_task_token());
//                        saveDataToMongo(certRedisEntity,request,getGojekDataUrl,ThirdDataTypeEnum.GOJECK_IDENTITY_DATA);
                        resultStr =  this.opratorsService.getGojekData(getGojekDataUrl,request);
                        log.info("获取gojek数据================================"+resultStr);
                        if (!StringUtils.isEmpty(resultStr)){

                            JSONObject res = JSONObject.parseObject(resultStr);

                            String code = res.get("code").toString();
                            if (code.equals("20000")){
                                // TODO: ???????
                                OrderThirdDataMongo scan =  this.ordThirdDataService.getThridDataByOrderNo(certRedisEntity.getOrderNo(),ThirdDataTypeEnum.GOJECK_IDENTITY_DATA);
                                if (scan != null){

                                    this.ordThirdDataService.update(scan,resultStr);
                                }else {

                                    this.ordThirdDataService.add(resultStr,certRedisEntity.getOrderNo(),certRedisEntity.getUserUuid(), ThirdDataTypeEnum.GOJECK_IDENTITY_DATA,1);
                                }
                            }else  {
                                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                                log.info("***************获取gojek数据异常，code不等于20000**************************");
                            }
                        }
                    }else if (certType.equals("golife")){


                        request.setReport_task_token(certRedisEntity.getReport_task_token());
                        request.setWebsite("golife");
                        request.setPhoneNo(certRedisEntity.getMobile());
//                        saveDataToMongo(certRedisEntity,request,getGojekDataUrl,ThirdDataTypeEnum.GOJECK_IDENTITY_DATA);
// ??
                        resultStr =  this.opratorsService.getGojekData(getGojekDataUrl,request);
                        log.info("获取golife数据================================"+resultStr);
                        if (!StringUtils.isEmpty(resultStr)){

                            JSONObject res = JSONObject.parseObject(resultStr);

                            String code = res.get("code").toString();
                            if (code.equals("20000")){
                                // TODO: 第三方数据保存
                                OrderThirdDataMongo scan =  this.ordThirdDataService.getThridDataByOrderNo(certRedisEntity.getOrderNo(),ThirdDataTypeEnum.GOLIFE_IDENTITY_DATA);
                                if (scan != null){

                                    this.ordThirdDataService.update(scan,resultStr);
                                }else {

                                    this.ordThirdDataService.add(resultStr,certRedisEntity.getOrderNo(),certRedisEntity.getUserUuid(), ThirdDataTypeEnum.GOLIFE_IDENTITY_DATA,1);
                                }
                            }else  {
                                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                                log.info("***************获取golife数据，code不等于20000*************************");
                            }
                        }
                    }else if (certType.equals("facebook")){


                        request.setReport_task_token(certRedisEntity.getReport_task_token());
                        request.setWebsite("facebook");

                        resultStr =  this.opratorsService.getGojekData(getGojekDataUrl,request);
                        log.info("获取facebook数据================================"+resultStr);
                        if (!StringUtils.isEmpty(resultStr)){

                            JSONObject res = JSONObject.parseObject(resultStr);

                            String code = res.get("code").toString();
                            if (code.equals("20000")){
                                // TODO: 第三方数据保存
                                OrderThirdDataMongo scan =  this.ordThirdDataService.getThridDataByOrderNo(certRedisEntity.getOrderNo(),ThirdDataTypeEnum.FACEBOOK_IDENTITY_DATA);
                                if (scan != null){

                                    this.ordThirdDataService.update(scan,resultStr);
                                }else {

                                    this.ordThirdDataService.add(resultStr,certRedisEntity.getOrderNo(),certRedisEntity.getUserUuid(), ThirdDataTypeEnum.FACEBOOK_IDENTITY_DATA,1);
                                }
                            }else  {
                                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                                log.info("***************获取facebook数据，code不等于20000*************************");
                            }
                        }
                    }else if (certType.equals("identity")){
                        request.setReport_task_token(certRedisEntity.getReport_task_token());
                        request.setWebsite("ktp");

                        resultStr =  this.opratorsService.getGojekData(getGojekDataUrl,request);
                        log.info("identity================================token: " + certRedisEntity.getReport_task_token() + "|" + resultStr);
                        if (!StringUtils.isEmpty(resultStr)){

                            JSONObject res = JSONObject.parseObject(resultStr);

                            String code = res.get("code").toString();
                            if (code.equals("20000")){

                                JSONObject data = (JSONObject) res.get("data");
                                JSONObject report_data = (JSONObject) data.get("report_data");
                                JSONObject identity = (JSONObject) report_data.get("identity");
                                JSONObject id_card = (JSONObject) identity.get("id_card");
                                boolean ktpVerifyResult = id_card!=null&&"SUCCESS".equals(id_card.get("verify_result").toString());
                                if (ktpVerifyResult) {
                                    log.info("聚信立实名认证成功");
                                    userVerifyResultService.updateVerifyResult(certRedisEntity.getOrderNo(), certRedisEntity.getUserUuid(),
                                            id_card.toJSONString(), UsrVerifyResult.VerifyResultEnum.SUCCESS, UsrVerifyResult.VerifyTypeEnum.KTP);

                                    // 实名认证成功
                                    // 更新认证信息表
                                    UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
                                    usrCertificationInfo.setUserUuid(certRedisEntity.getUserUuid());
                                    usrCertificationInfo.setDisabled(0);
                                    usrCertificationInfo.setCertificationType(CertificationEnum.USER_IDENTITY.getType());
                                    List<UsrCertificationInfo> usrCertificationInfoList = this.usrCertificationInfoDao.scan(usrCertificationInfo);

                                    if (usrCertificationInfoList.isEmpty()) {
                                        usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                                        this.usrCertificationInfoDao.insert(usrCertificationInfo);
                                    } else {
                                        usrCertificationInfo = usrCertificationInfoList.get(0);
                                        if (usrCertificationInfo.getCertificationResult().equals(CertificationResultEnum.NOT_AUTH.getType())) {
                                            usrCertificationInfo.setRemark("JXL");
                                            usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                                            this.usrCertificationInfoDao.update(usrCertificationInfo);
                                        }
                                    }
                                } else {
                                    log.info("聚信立实名认证失败，id_card: {}", id_card==null?null:id_card.toJSONString());
                                    userVerifyResultService.updateVerifyResult(certRedisEntity.getOrderNo(), certRedisEntity.getUserUuid(),
                                            id_card.toJSONString(), UsrVerifyResult.VerifyResultEnum.FAILED, UsrVerifyResult.VerifyTypeEnum.KTP);
                                }

                            }else  {
                                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                                log.info("***************获取聚信立实名数据，code不等于20000*************************");
                            }
                        }
                    }

                    //
                    if (StringUtils.isEmpty(resultStr)){

                        this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                        log.info("****************获取聚信立实名数据，code不等于20000*************************");
                    }else {

                        if (!certType.equals("gojek")&&!certType.equals("golife")&&!certType.equals("tokoPedia")&&!certType.equals("facebook")&&!certType.equals("identity")){

                            JSONObject res = JSONObject.parseObject(resultStr);
                            String code = res.get("code").toString();
                            if (code.equals("0")){

                                if (certType.equals("xl")){

                                    saveUserData(certRedisEntity,ThirdDataTypeEnum.XL_IDENTITY_DATA,resultStr);
                                }
//                                else if (certType.equals("tokoPedia")){
//
//                                    saveUserData(certRedisEntity,ThirdDataTypeEnum.TOKOPEDIA_IDENTITY_DATA,resultStr);
//                                }
                                else if (certType.equals("telk1")){

                                    saveUserData(certRedisEntity,ThirdDataTypeEnum.TELK_IDENTITY_DATA,resultStr);
                                }else if (certType.equals("telk2")){

                                    saveUserData(certRedisEntity,ThirdDataTypeEnum.TELK2_IDENTITY_DATA,resultStr);
                                }

                            }else {

                                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
                                log.info("***************获取数据异常，code不等于0*************************");
                            }
                        }
                    }
                }

            }catch (Exception e){
                log.error("***************获取数据异常************************",e);

                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
            }
        }
    }


    /**
     *   保存第三方数据
     * */
    public void saveUserData(UserCertificationInfoInRedis certRedisEntity,ThirdDataTypeEnum type,String resultStr){

        // TODO: 第三方数据保存
        OrderThirdDataMongo scan =  this.ordThirdDataService.getThridDataByOrderNo(certRedisEntity.getOrderNo(),type);
        if (scan != null){

            this.ordThirdDataService.update(scan,resultStr);
        }else {

            this.ordThirdDataService.add(resultStr,certRedisEntity.getOrderNo(),certRedisEntity.getUserUuid(), type,1);
        }
    }

//
//    /**
//     *    ??tokopedia????
//     * */
//    public void dealWithTokopediaData() throws Exception{
//
//
//        UserCertificationInfoInRedis certRedisEntity =  redisClient.listGetTail(RedisContants.USER_CERTIFICATION_LIST,UserCertificationInfoInRedis.class);
//
//        try {
//           for (int i = 0; i<=100;i++){
//            log.info("======================"+certRedisEntity.toString()+"=====================");
//               if (certRedisEntity != null){
//
//                   // ?????
//                   String certType = certRedisEntity.getCertType();
//                   if (certType.equals("tokoPedia")){
//                       OpratorsRequest request = new OpratorsRequest();
//                       request.setEmail(certRedisEntity.getEmail());
//                       request.setPwd(DESUtils.decrypt(certRedisEntity.getPwd()));
//                       String requestStr = this.opratorsService.getInfo("http://47.74.190.108:8080/spider/toko/getDetail",request);
//                       JSONObject res = JSONObject.parseObject(requestStr);
////                   String code = res.get("code").toString();
//                       // ?????batchId
//                       if(res.get("batchId") != null){
//                           certRedisEntity.setBatchId(res.get("batchId").toString());
//                           this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
//                       }
//                   }
//               }
//           }
//       }catch (Exception e){
//           log.info(e.getMessage());
//           this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,certRedisEntity);
//       }
//
//    }
}
