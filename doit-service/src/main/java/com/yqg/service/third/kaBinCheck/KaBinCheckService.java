package com.yqg.service.third.kaBinCheck;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.HttpTools;
import com.yqg.common.utils.JsonUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.externalChannel.utils.HttpUtil;
import com.yqg.service.externalChannel.utils.SendDataBuiler;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.user.entity.UsrBank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ?bin???service
 * Created by Didit Dwianto on 2017/11/28.
 */
@Service
@Slf4j
public class KaBinCheckService {

    @Value("${third.pay.cardBinUrl}")
    private String cardBinUrl;

    @Value("${third.pay.cardBinToken}")
    private String cardBinToken;

    @Value("${third.cashcash.cardCash2Url}")
    private String cardCash2Url;

    // bank short name
    private static String BANKCODE = "bankCode";
    // bank account
    private static String BANKCARDNUMBER = "bankCardNumber";
    // TOKEN
    private static String TOKEN = "X-AUTH-TOKEN";


    // third interface log
    @Autowired
    private SysThirdLogsService sysThirdLogsService;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private Cash2Config cash2Config;

    /**
     *
     * @param userBankRequest
     * @return
     * @throws ServiceException
     * @throws IOException
     */
    public JSONObject sendCardBinHttpPost(UsrBankRequest userBankRequest) throws ServiceException {
        // HTTP?????
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put(TOKEN, cardBinToken);//
        System.err.println(cardBinToken);
        // HTTP????
        Map<String,String> contents = new HashMap<String, String>();
        contents.put(BANKCODE,userBankRequest.getBankCode());
        contents.put(BANKCARDNUMBER,userBankRequest.getBankNumberNo());
        contents.put("paymentChannel","XENDIT");
        int connectTimeout=30000;
        int readTimeout=10000;
        // ??????
        Map<String, Object> requestObj = new HashMap<String, Object>();
        requestObj.put("headers",headers);
        requestObj.put("contents",contents);
        requestObj.put("connectTimeout",connectTimeout);
        requestObj.put("readTimeout",readTimeout);
        // ???????SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(userBankRequest.getOrderNo(),userBankRequest.getUserUuid(), SysThirdLogsEnum.KABIN_CHECK.getCode(),null,JSON.toJSONString(requestObj),null);
        String cardBinUrlResponse = "";
        try {
            String name = userBankRequest.getBankCardName();
            //ahalim: Use cardbin service
            /*
            String str = "{\n" +
                    "  \"bankCardVerifyStatus\" : \"SUCCESS\",\n" +
                    "  \"bankHolderName\" : \""+name+"\",\n" +
                    "  \"code\" : \"0\",\n" +
                    "}";
            cardBinUrlResponse =str;
            */
            cardBinUrlResponse = HttpTools.post(cardBinUrl,headers,contents,connectTimeout,readTimeout);
            // ???????sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(userBankRequest.getOrderNo(),userBankRequest.getUserUuid(), SysThirdLogsEnum.KABIN_CHECK.getCode(),null,null,cardBinUrlResponse);
        }catch (Exception e){
            log.error("????,?????????");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
        JSONObject obj = JSON.parseObject(cardBinUrlResponse);
        // code?0????
        if(null==obj||!obj.get("code").toString().equals("0")){
            log.error("绑卡异常");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
        return obj;
    }

    /**
     * 调取cash2接口  推送银行卡绑卡状态信息
     * @param item
     * @param message
     * @throws ServiceException
     */
    public void kaBinCheckPost(UsrBank item, String message) throws ServiceException {
        if (item.getThirdType().equals(1)) {
            List<OrdOrder> orders = ordDao.getLatestOrder(item.getUserUuid());
            if(CollectionUtils.isEmpty(orders)){
                log.error("订单不存在");
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
            String orderNo = orders.get(0).getUuid();
            log.info("{}:订单推送cash2银行卡状态" + orderNo);
            String status = "";
            // (0=未验证，1=待验证,2=成功,3=失败)',
            if(item.getStatus()==2){
                status = "1";
            }else if(item.getStatus()==3){
                status ="2";
            }
            postBankCardResult2CashCash(orderNo,status,message);
        }
    }


    public void postBankCardResult2CashCash(String orderNo,String status,String reason) throws ServiceException{
        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderNoByRealOrderNoRelation(orderNo);
        if(externalOrderRelation == null){
            //非cashcash 银行卡不需要推送
            return;
        }
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("order_no",externalOrderRelation.getExternalOrderNo());
        paramMap.put("bind_status",status);
        paramMap.put("reason", reason);
        Cash2ApiParam sendData = new SendDataBuiler().buildParam(paramMap,cash2Config);
        String cardBinUrlResponse = "";
        try {
            cardBinUrlResponse = HttpUtil.postJson(JsonUtils.serialize(sendData), cardCash2Url);
        } catch (Exception e) {
            log.error("推送cash2绑卡状态信息接口异常");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
        JSONObject obj = JSON.parseObject(cardBinUrlResponse);
        if (null == obj || !obj.get("code").toString().equals("0")) {
            log.error("推送cash2绑卡状态信息接口异常");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
    }


}
