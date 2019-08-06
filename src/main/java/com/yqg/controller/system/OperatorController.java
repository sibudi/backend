package com.yqg.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.third.operators.OpratorsService;
import com.yqg.service.third.operators.request.*;
import com.yqg.service.third.operators.response.GojekSendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Created by Didit Dwianto on 2017/12/16.
 */
@RestController
@Slf4j
@RequestMapping("/operator")
public class OperatorController {

//    @Value("${telk.url}")
//    private  String telkurl;
//    @Value("${telk.url2}")
//    private  String telkurl2;
//    @Value("${telk.sendSmsUrl}")
//    private  String sendSmsUrl;
//    @Value("${tokopedia.url}")
//    private  String tokopediaUrl;
//    @Value("${xl.url}")
//    private  String xlUrl;
    @Value("${jxl.beginurl}")
    private  String gojekSmsUrl;
    @Value("${jxl.authurl}")
    private  String gojekAuthurl;
//    @Value("${jxl.getReportDataurl}")
//    private  String gojekReportDataurl;
    @Value("${jxl.tokoPediaAuthurl}")
    private String tokoPediaAuthurl;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OpratorsService operatorService;
//    @Autowired
//    private OpratorsService operatorService;

    /**
     *   获取tokoPedia数据
     * */
    @RequestMapping("/getTokoPedia")
    @ResponseBody
    public ResponseEntity<Object> getTokoPedia(HttpServletRequest request, @RequestBody TokoPedaiAuthRequest tokoPedaiAuthRequest)throws Exception {

//        UserSessionUtil.filter(request, this.redisClient, tokoPedaiAuthRequest);
//        log.info("request body{}", JsonUtils.serialize(tokoPedaiAuthRequest));
        tokoPedaiAuthRequest.setWebsite("tokopedia");
        log.info("获取tokoPedia数据");
        JSONObject result = operatorService.tokoPediaAuth(tokoPediaAuthurl,tokoPedaiAuthRequest);

        if (result.getIntValue("code") == 10000){
            return new ResponseEntityBuilder<>().code(1).message(result.get("message").toString()).build();
        }else {
            return new ResponseEntityBuilder<>().code(result.getIntValue("code")).message(result.get("message").toString()).build();
        }
    }

    /**
     *   gojek认证 -- 填写手机号 发送短信
     * */
    @RequestMapping("/sendGojekSms")
    @ResponseBody
    public ResponseEntity<GojekSendSmsResponse> sendGojekSms(HttpServletRequest request, @RequestBody GojekSendSmsRequest gojekSendSmsRequest)throws ServiceException {

//        UserSessionUtil.filter(request, this.redisClient, gojekSendSmsRequest);
//        log.info("request body{}", JsonUtils.serialize(gojekSendSmsRequest));
        log.info("gojek认证 -- 填写手机号 发送短信");
        return ResponseEntityBuilder.success(operatorService.sendGojekSms(gojekSmsUrl,gojekSendSmsRequest));

    }

    /**
     *   gojek认证 -- 提交短信授权
     * */
    @RequestMapping(value = "/gojekAuth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> gojekAuth(HttpServletRequest request, @RequestBody GojekAuthRequest gojekAuthRequest)throws ServiceException {

//        UserSessionUtil.filter(request, this.redisClient, gojekAuthRequest);
//        log.info("request body{}", JsonUtils.serialize(gojekAuthRequest));
        log.info("gojek认证 -- 提交短信授权");
        JSONObject result= operatorService.gojekAuth(gojekAuthurl,gojekAuthRequest);
        if (result.getIntValue("code") == 10000||result.getIntValue("code") == 10002){
            return new ResponseEntityBuilder<>().code(1).message(result.get("message").toString()).build();
        }else {
            return new ResponseEntityBuilder<>().code(result.getIntValue("code")).message(result.get("message").toString()).build();
        }
    }


//    /**
//     * ????????
//     * @param jsonObject
//     * @return
//     */
//    @RequestMapping("/getOprator")
//    @ResponseBody
//    public ResponseEntity<Object> getOprator(@RequestBody JSONObject jsonObject){
//        String oprator=operatorService.getOprator(jsonObject.get("phoneNo").toString());
//        return ResponseEntityBuilder.success(oprator);
//    }

//    /**
//     *   ??xl??
//     * */
//    @RequestMapping("/getXL")
//    @ResponseBody
//    public ResponseEntity<Object> getXL(HttpServletRequest request, @RequestBody OpratorsRequest opratorsRequest)throws Exception {
//
////        UserSessionUtil.filter(request, this.redisClient, opratorsRequest);
////        log.info("request body{}", JsonUtils.serialize(opratorsRequest));
//
//        opratorsRequest.setType("xl");
//        String result = operatorService.getInfo(xlUrl, opratorsRequest);
//        log.info(result);
//        return ResponseEntityBuilder.success();
//    }


//
//    /**
//     *   ??telk???
//     * */
//    @RequestMapping("/sendTelkSms")
//    @ResponseBody
//    public ResponseEntity<Object> sendTelkSms(HttpServletRequest request, @RequestBody OpratorsRequest opratorsRequest)throws Exception {
//
////        UserSessionUtil.filter(request, this.redisClient, opratorsRequest);
////        log.info("request body{}", JsonUtils.serialize(opratorsRequest));
//
//        String result= operatorService.getInfo(sendSmsUrl,opratorsRequest);
//        log.info(result);
//        return ResponseEntityBuilder.success();
//    }
//
//    /**
//     *   ??telk??????
//     * */
//    @RequestMapping("/getMyTelk")
//    @ResponseBody
//    public ResponseEntity<Object> getMyTelk(HttpServletRequest request, @RequestBody OpratorsRequest opratorsRequest)throws Exception {
//
////        UserSessionUtil.filter(request, this.redisClient, opratorsRequest);
////        log.info("request body{}", JsonUtils.serialize(opratorsRequest));
//
//        opratorsRequest.setType("telk1");
//        String result= operatorService.getInfo(telkurl,opratorsRequest);
//        log.info(result);
//        return ResponseEntityBuilder.success();
//    }
//
//    /**
//     *   ??telk??????
//     * */
//    @RequestMapping("/getMyTelk2")
//    @ResponseBody
//    public ResponseEntity<Object> getMyTelk2(HttpServletRequest request, @RequestBody OpratorsRequest opratorsRequest)throws Exception {
//
////        UserSessionUtil.filter(request, this.redisClient, opratorsRequest);
////        log.info("request body{}", JsonUtils.serialize(opratorsRequest));
//
//        opratorsRequest.setType("telk2");
//        String result= operatorService.getInfo(telkurl2,opratorsRequest);
//        log.info(result);
//        return ResponseEntityBuilder.success();
//    }

//
//    /**
//     *   IM3
//     * */
//    @RequestMapping(value = "/getIM3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntity<Object> getIM3(HttpServletRequest request, @RequestBody IM3Request im3Request)throws ServiceException {
//
////        UserSessionUtil.filter(request, this.redisClient, im3Request);
////        log.info("request body{}", JsonUtils.serialize(im3Request));
//        operatorService.getIM3(im3Request);
//        return ResponseEntityBuilder.success();
//    }
}
