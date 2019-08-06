package com.yqg.service.third.operators;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.HttpTools;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.third.operators.request.*;
import com.yqg.service.third.operators.response.GojekSendSmsResponse;
import com.yqg.service.user.model.UserCertificationInfoInRedis;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrCertificationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhuanhuan on 2017/12/16.
 */
@Service
@Slf4j
public class OpratorsService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;
    @Autowired
    private UsrService usrService;

    int connectTimeout = 30000;
    int readTimeout = 10000;

//
//    public String getInfo(String url, OpratorsRequest request)
//            throws Exception {
//
//        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
//        log.info(result);
//
//        if (null!=result){
//
//            JSONObject res = JSONObject.parseObject(result);
//            String code = res.get("code").toString();
//            if (code.equals("0")){
//
//                if (!StringUtils.isEmpty(request.getType())){
//
//                    // ?????batchId
//                    if(res.get("batchId") != null){
//                        // ????
//                        String certType = request.getType();
//                        UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
//                        certificationInfo.setUserUuid(request.getUserUuid());
//
//                        UserCertificationInfoInRedis cerInfoRedis = new UserCertificationInfoInRedis();
//                        cerInfoRedis.setBatchId(res.get("batchId").toString());
//                        cerInfoRedis.setUserUuid(request.getUserUuid());
//                        String pwdDes = DESUtils.encrypt(request.getPwd());
//                        cerInfoRedis.setPwd(pwdDes);
//                        cerInfoRedis.setCertType(certType);
//                        cerInfoRedis.setOrderNo(request.getOrderNo());
//
//                        if (certType.equals("xl")){
//
//                            cerInfoRedis.setPhoneNo(request.getPhoneNo());
//                            certificationInfo.setCertificationType(CertificationEnum.XL_IDENTITY.getType());
//                        }else if (certType.equals("tokoPedia")){
//
//                            cerInfoRedis.setEmail(request.getEmail());
//                            certificationInfo.setCertificationType(CertificationEnum.TOKOPEDIA_IDENTITY.getType());
//                        }else if (certType.equals("telk1")){
//
//                            cerInfoRedis.setPhoneNo(request.getPhoneNo());
//                            certificationInfo.setCertificationType(CertificationEnum.TELK_IDENTITY.getType());
//                        }else if (certType.equals("telk2")){
//
//                            cerInfoRedis.setPhoneNo(request.getPhoneNo());
//                            certificationInfo.setCertificationType(CertificationEnum.TELK2_IDENTITY.getType());
//                        }
//
////            TODO:  ???? ???
//
//                        List<UsrCertificationInfo> scanList =  this.usrCertificationInfoDao.scan(certificationInfo);
//                        if (CollectionUtils.isEmpty(scanList)){
//
//                            certificationInfo.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
//                            certificationInfo.setRemark(res.get("batchId").toString());
//                            certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
//                            this.usrCertificationInfoDao.insert(certificationInfo);
//                        }else  {
//                            UsrCertificationInfo update = scanList.get(0);
//                            update.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
//                            update.setRemark(res.get("batchId").toString());
//                            certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
//                            this.usrCertificationInfoDao.update(update);
//                        }
//
//                        // ???redis ???task??????batchId ??????
//                        this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,cerInfoRedis);
//
//                    }
//                }
//            }else  if(code.equals("-1")){
//
//                //  ??????
//                log.error("???????");
//                throw new ServiceException(ExceptionEnum.USER_PASSWORD_OR_ACCOUNT_ERROR);
//
//            }else  {
//                log.error("?????");
//                throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
//            }
//        }
//        return result;
//    }

    /**
     *    ???????
     * */
    public String getData(String url, OpratorsRequest request)
            throws Exception {

        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
        log.info(result);
        return result;
    }

    /**
     *   tokoPedia ??
     * */

    public JSONObject  tokoPediaAuth(String url , TokoPedaiAuthRequest request) throws ServiceException{

        request.setName(this.usrService.getUserNameByUuid(request.getUserUuid()));

        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
        log.info(result);
        JSONObject res = new JSONObject();
        if (null != result){
            res = JSONObject.parseObject(result);
            String code = res.get("code").toString();
            if (code.equals("10000")) {

                UserCertificationInfoInRedis cerInfoRedis = new UserCertificationInfoInRedis();
                cerInfoRedis.setUserUuid(request.getUserUuid());
                cerInfoRedis.setOrderNo(request.getOrderNo());
                cerInfoRedis.setPwd(DESUtils.encrypt(request.getPwd()));
                cerInfoRedis.setCertType("tokoPedia");
                cerInfoRedis.setEmail(request.getEmail());
                cerInfoRedis.setReport_task_token(res.get("report_task_token").toString());

//            TODO:  ???? ???
                UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
                certificationInfo.setUserUuid(request.getUserUuid());
                certificationInfo.setCertificationType(CertificationEnum.TOKOPEDIA_IDENTITY.getType());

                List<UsrCertificationInfo> scanList =  this.usrCertificationInfoDao.scan(certificationInfo);
                if (CollectionUtils.isEmpty(scanList)){

                    certificationInfo.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
                    certificationInfo.setRemark(res.get("report_task_token").toString());
                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    this.usrCertificationInfoDao.insert(certificationInfo);
                }else  {

                    UsrCertificationInfo update = scanList.get(0);
                    update.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
                    certificationInfo.setRemark(res.get("report_task_token").toString());
                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    this.usrCertificationInfoDao.update(update);
                }

                // ???redis ???task??????batchId ??????
                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,cerInfoRedis);

            }else if(code.equals("10007") || code.equals("10006") || code.equals("10005")){
                //  ??????
                log.error("???????");
                throw new ServiceException(ExceptionEnum.USER_PASSWORD_OR_ACCOUNT_ERROR);
            }else if(code.equals("10008")){
                log.error("?????????");
                throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
            }
        }
        return res;
    }


    /**
     *    ??gojek?golife???
     * */
    public GojekSendSmsResponse sendGojekSms(String url , GojekSendSmsRequest request) throws ServiceException{

        request.setName(this.usrService.getUserNameByUuid(request.getUserUuid()));

        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
        log.info(result);

        GojekSendSmsResponse response = new GojekSendSmsResponse();
        if (null != result){
            JSONObject res = JSONObject.parseObject(result);
            log.info(res.toJSONString());
            String code = res.get("code").toString();
            if (code.equals("10002")) {
                response.setReport_task_token(res.get("report_task_token").toString());
                response.setMessage(res.get("message").toString());
                response.setAuth_token(res.get("auth_token").toString());
            }else if(code.equals("10007") || code.equals("10006") || code.equals("10005")){
                //  ??????
                log.error("???????");
                throw new ServiceException(ExceptionEnum.USER_PASSWORD_OR_ACCOUNT_ERROR);
            }else if(code.equals("10010")){
                log.error("?????????,??????");
                throw new ServiceException(ExceptionEnum.CAPTCHA_ERROR_TO_MANY_TIMES);
            }else if(code.equals("10008")){
                log.error("?????????");
                throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
            }else if(code.equals("10017")){
                log.error("?????????,?????");
                throw new ServiceException(ExceptionEnum.FAILED_CAPTCHA_TOO_MANY_TIMES);
            }
        }
        return response;
    }

    /**
     *    gojek???golife??
     * */
    public JSONObject gojekAuth(String url , GojekAuthRequest request) throws ServiceException{

        request.setName(this.usrService.getUserNameByUuid(request.getUserUuid()));

        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
        log.info(result);
        JSONObject res = new JSONObject();
        if (null != result){
            res = JSONObject.parseObject(result);

            String code = res.get("code").toString();
            // ????
            if (code.equals("10000")) {

                UserCertificationInfoInRedis cerInfoRedis = new UserCertificationInfoInRedis();
                cerInfoRedis.setUserUuid(request.getUserUuid());
                cerInfoRedis.setOrderNo(request.getOrderNo());
                cerInfoRedis.setReport_task_token(request.getReport_task_token());
                cerInfoRedis.setPwd(request.getCaptcha());
                cerInfoRedis.setMobile(request.getPhoneNo());
//            TODO:  ???? ???
                UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
                certificationInfo.setUserUuid(request.getUserUuid());

                if (StringUtils.isEmpty(request.getWebsite())){
                    cerInfoRedis.setCertType("gojek");
                    certificationInfo.setCertificationType(CertificationEnum.GOJECK_IDENTITY.getType());
                }else {
                    cerInfoRedis.setCertType("golife");
                    certificationInfo.setCertificationType(CertificationEnum.GOLIFE_IDENTITY.getType());
                }

                List<UsrCertificationInfo> scanList =  this.usrCertificationInfoDao.scan(certificationInfo);
                if (CollectionUtils.isEmpty(scanList)){

                    certificationInfo.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
                    certificationInfo.setRemark(request.getReport_task_token());
                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    this.usrCertificationInfoDao.insert(certificationInfo);
                }else  {

                    UsrCertificationInfo update = scanList.get(0);
                    update.setCertificationData(JSONObject.toJSON(cerInfoRedis).toString());
                    certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
                    update.setRemark(request.getReport_task_token());
                    this.usrCertificationInfoDao.update(update);
                }

                // ???redis ???task??????batchId ??????
                this.redisClient.listAdd(RedisContants.USER_CERTIFICATION_LIST ,cerInfoRedis);

            }else if(code.equals("10007") || code.equals("10006") || code.equals("10005")){
                //  ??????
                log.error("???????");
                throw new ServiceException(ExceptionEnum.USER_PASSWORD_OR_ACCOUNT_ERROR);
            }else if(code.equals("10003")){
                log.error("????????");
                throw new ServiceException(ExceptionEnum.CAPTCHA_INVALID);
            }else if(code.equals("10004")){
                log.error("?????????????");
                throw new ServiceException(ExceptionEnum.Captcha_INVALID_AND_RESENT);
            }else if(code.equals("10008")){
                log.error("?????????");
                throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
            }else if(code.equals("10009")){
                log.error("???????");
                throw new ServiceException(ExceptionEnum.CAPTCHA_TIMEOUT);
            }else if(code.equals("10010")){
                log.error("?????????,??????");
                throw new ServiceException(ExceptionEnum.CAPTCHA_ERROR_TO_MANY_TIMES);
            }else if(code.equals("10017")){
                log.error("?????????,?????");
                throw new ServiceException(ExceptionEnum.FAILED_CAPTCHA_TOO_MANY_TIMES);
            }

        }else {
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        return res;
    }


    /**
     *    ??gojek??
     * */
    public String getGojekData(String url, OpratorsRequest request)
            throws Exception {

        String result = HttpTools.post(url, null, JSONObject.toJSON(request).toString(), connectTimeout, readTimeout);
        log.info(result);
        return result;
    }

//    /**
//     *    ??im3??
//     * */
//    public void getIM3(IM3Request request){
//
//        String account = request.getPhoneNo();
//        String pwd = request.getPwd();
//        if (account != null && pwd != null){
//
//            Map<String,String> dataMap = new HashMap<>();
//            dataMap.put("account",account);
//            dataMap.put("pwd",pwd);
//            String dataJsonStr =  JSONObject.toJSON(dataMap).toString();
//            log.info("????im3??"+dataJsonStr);
//            UsrCertificationInfo certificationInfo = new UsrCertificationInfo();
//            certificationInfo.setUserUuid(request.getUserUuid());
//            certificationInfo.setCertificationType(CertificationEnum.IM3_IDENTITY.getType());
//            List<UsrCertificationInfo> scanList =  this.usrCertificationInfoDao.scan(certificationInfo);
//            if (CollectionUtils.isEmpty(scanList)){
//
//                certificationInfo.setCertificationData(dataJsonStr);
//                certificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
//                this.usrCertificationInfoDao.insert(certificationInfo);
//            }else  {
//                UsrCertificationInfo update = scanList.get(0);
//                update.setCertificationData(dataJsonStr);
//                update.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
//                this.usrCertificationInfoDao.update(update);
//            }
//        }
//    }

//    /**
//     * GSM Mobile WirelessEdit
//     PT Indosat
//     Matrix prefix:
//     0814
//     0815
//     0816
//     0816
//     0855
//     Mentari prefix:
//     0816
//     0815
//     0858
//     IM3 prefix:
//     0856
//     0857
//     PT Sampoerna Telekomunikasi Indonesia
//     Ceria prefix:
//     0827
//     0828
//
//     PT Telkomsel
//     kartuHALO prefix:
//     0811
//     0812
//     simPATI prefix:
//     0812
//     0813
//     0821
//     LOOP prefix:
//     0822
//     kartu As prefix:
//     0851
//     0852
//     0853
//     0823
//     PT XL Axiata
//     XL Postpaid prefix:
//     0817
//     0818
//     0819
//     0877 (XL Prioritas)
//     0878 (XL Prioritas)
//     XL Prepaid prefix:
//     0817
//     0818
//     0819
//     0859
//     0877
//     0878
//     AXIS (acquired by XL) prefix:
//     0831
//     0832
//     0833
//     0838
//     PT Smartfren Telecom
//     Smarfren prefix
//     0881
//     0882
//     0883
//     0884
//     0885
//     0886
//     0887
//     0888
//     0889
//
//     PT Hutchison 3 Indonesia, formerly PT Hutchison CP Telecommunications
//     3 prefix:
//     0895
//     08953
//     0896
//     0897
//     0898
//     0899
//     * @param phoneNo
//     * @return
//     */
//    public String getOprator(String phoneNo){
//        phoneNo= (phoneNo.charAt(0)+"").equals("0")?phoneNo:"0"+phoneNo;
//        String[] im3={"0814","0815","0816","0855","0856","0857","0858"};
//        String[] telk ={"0811","0812","0813","0821","0822","0823","0851","0852","0853"};
//        String[] xl={"0817","0818","0819","0877","0878","0831","0832","0838","0859"};
//
//        for (String s:telk){
//            if (phoneNo.indexOf(s)==0){
//                return "telk";
//            }
//        }
//        for (String s:xl){
//            if (phoneNo.indexOf(s)==0){
//                return "xl";
//            }
//        }
//        for (String s:im3){
//            if (phoneNo.indexOf(s)==0){
//                return "im3";
//            }
//        }
//
//        return "other";
//    }

}
