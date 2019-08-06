package com.yqg.common.utils;


import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.BadRequestException;
import com.yqg.common.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 *  @author  Jacob
 *  ????
 */
@Component
public class SmsCodeNewUtils {

    private static Logger logger = LoggerFactory.getLogger(SmsCodeUtils.class);

    private final static String serverUrl = "http://send.18sms.com/msg/HttpBatchSendSM";//????

    private final static String account = "gga573";//??

    private final static String pswd = "7YkrfG80";//??



    private RestTemplate restTemplate = new RestTemplate();




    /**
     * ???????????
     * @param number
     * @return
     */
    public static int randSmsCode(int number) {
        Random random = new Random();
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < number; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
        }
        logger.info("???????:" + sRand);
        return Integer.valueOf(sRand.toString());
    }

    /**
     * ???????,","???
     * @param phones
     * @return
     */
    public static String phones(String[] phones) {
        StringBuilder buffer = new StringBuilder();
        for (String phone : phones) {
            if (phone.equals(phones[phones.length - 1])) {
                buffer.append(phone);
            } else {
                buffer.append(phone + ",");
            }
        }
        return buffer.toString();
    }


    public static void main(String[] args) throws BadRequestException {
        //13522144014 ???????10?????100??
        String content="???;???????10?????100?????????????????????!";
        //new SmsCodeNewUtils().sendCodeSample("13522144014,15070576989",content);
        String [] mobiles=new String[]{"13522144014","15070576989"};

    }





    /**
     * ?????
     * @param
     * @param
     * @param
     * @param
     */
    public  void sendCodeSample(String mobiles,String content) throws BadRequestException {
        try {
            //content= URLEncoder.encode(content, "UTF-8");
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(serverUrl+"?");
            stringBuilder.append("account="+this.account);
            stringBuilder.append("&pswd="+this.pswd);
            stringBuilder.append("&mobile="+mobiles);
            stringBuilder.append("&msg="+content);
            stringBuilder.append("&needstatus=true");
            stringBuilder.append("&extno=1234");
            System.out.println(content);
            System.out.println(stringBuilder.toString());
            String exchange = exchange(stringBuilder.toString(), null, HttpMethod.GET);
            logger.info(exchange);
        } catch (ServiceException e) {
            logger.debug("????");
        }

        }

    /**
     * ?????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public  String sendRegisterSmsCode(String code, String... mobiles)
            throws BadRequestException {
        String content="???"+code+"????????????????????!";
        sendCodeSample(SmsCodeUtils.phones(mobiles),content);
        return code;
    }

    /**
     * ?????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public  String sendLoginSmsCode(String code, String... mobiles)
            throws BadRequestException {
        String content="???"+code+"??????????????????????";
        sendCodeSample( SmsCodeUtils.phones(mobiles),content);
        return code;
    }



    /**
     * ???????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public  String sendPayPasswordSmsCode(String code, String... mobiles)
            throws BadRequestException {
        String content="???"+code+"????????????????????????";
        sendCodeSample( SmsCodeUtils.phones(mobiles),content);
        return code;
    }


    /**
     * ?????????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public  String sendKoudaiRefundPasswordSmsCode(String code, String... mobiles)
            throws BadRequestException {
        String content="???"+code+"????????????????????";
        sendCodeSample( SmsCodeUtils.phones(mobiles),content);
        return code;
    }

    /**
     * ????????????????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public  String sendLoanSmsCode(String code, String... mobiles)
            throws BadRequestException {
        String content="???"+code+"????????????????????????";
        sendCodeSample( SmsCodeUtils.phones(mobiles),content);
        return code;
    }

    /**
     * ?????????boss??????????????
     * @param
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public  String loanRemindRelventPerson(String content, String... mobiles)
            throws BadRequestException {
        sendCodeSample( SmsCodeUtils.phones(mobiles),content);
        return content;
    }


//    /**
//     * ??????
//     * @param code
//     * @param mobiles
//     * @return
//     * @throws BadRequestException
//     */
//    public static String sendIdentityCheckSmsCode(String code, String... mobiles)
//            throws BadRequestException {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        map.put("product","???");
//        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_63780379");
//        return code;
//    }

    /**
     * ???????
     * @return
     */
    public static String sendRandSmsCode() {
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < 6; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        logger.info("???????:" + sRand);
        return String.valueOf(sRand);
    }

    public String exchange(String url, LinkedMultiValueMap<String, Object> entityMap,
                           HttpMethod method) throws ServiceException {
        ResponseEntity<String> response = null;
        HttpEntity<Object> requestEntity = new HttpEntity<>(entityMap);

        response = this.restTemplate.exchange(url, method, requestEntity, String.class);
        if (HttpStatus.OK.value() == response.getStatusCode().value()) {
            return response.getBody();
        }
        logger.error("response status {} ??????", response.getStatusCode());
        throw new ServiceException(ExceptionEnum.SYSTEM_TONGDUN_APPLY_ERROR);
    }

}
