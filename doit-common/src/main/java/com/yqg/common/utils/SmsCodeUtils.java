package com.yqg.common.utils;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.BatchSmsAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.yqg.common.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Jacob
 *?????
 */

public class SmsCodeUtils {

    private static Logger logger = LoggerFactory.getLogger(SmsCodeUtils.class);

    private static String serverUrl = "http://1099291791829501.mns.cn-hangzhou.aliyuncs.com/";

    private static String appcessId = "LTAIIAqDg3WYD6it";

    private static String accessKey = "alfvknc8PfwLopG0TNYLG7DpjtKJR2";






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
        //13522144014
        new SmsCodeUtils().sendLoginSmsCode("13522144014");

    }




    /**
     * ?????
     * @param
     * @param
     * @param mobileNumber
     * @param smsTemplateCode
     */
    public static void sendCodeSample(Map<String,Object> batchSmsMap, String mobileNumber,
            String smsTemplateCode) throws BadRequestException {
        CloudAccount account = new CloudAccount(appcessId, accessKey, serverUrl);
        MNSClient client = account.getMNSClient();
        CloudTopic topic = client.getTopicRef("sms.topic-cn-hangzhou");
        RawTopicMessage msg = new RawTopicMessage();
        msg.setMessageBody(DateUtils.DateToString3(new Date()));
        MessageAttributes messageAttributes = new MessageAttributes();
        BatchSmsAttributes batchSmsAttributes = new BatchSmsAttributes();
        // 3.1 ??????????SMSSignName?
        batchSmsAttributes.setFreeSignName("???");
        // 3.2 ????????????SMSTempateCode?
        batchSmsAttributes.setTemplateCode(smsTemplateCode);
        // 3.3 ???????????????????????????????????????
        BatchSmsAttributes.SmsReceiverParams smsReceiverParams = new BatchSmsAttributes.SmsReceiverParams();
        for(String key:batchSmsMap.keySet()){
            smsReceiverParams.setParam(key, batchSmsMap.get(key).toString());
        }
        // 3.4 ?????????
        batchSmsAttributes.addSmsReceiver(mobileNumber, smsReceiverParams);
        messageAttributes.setBatchSmsAttributes(batchSmsAttributes);
        try {
            TopicMessage ret = topic.publishMessage(msg, messageAttributes);
            logger.info("MessageId: " + ret.getMessageId());
            logger.info("MessageMD5: " + ret.getMessageBodyMD5());
        } catch (ServiceException se) {
            logger.debug(se.getErrorCode() + se.getRequestId());
            logger.debug(se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();
    }

    /**
     * ?????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static String sendRegisterSmsCode(String code, String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("product","???");
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_63780375");
        return code;
    }

    /**
     * ?????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static String sendLoginSmsCode(String code, String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("product","???");
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_63780377");
        return code;
    }



    /**
     * ???????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static String sendPayPasswordSmsCode(String code, String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("product","???");
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_65940192");
        return code;
    }

    /**
     * ??????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static String sendIdentityCheckSmsCode(String code, String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("product","???");
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_63780379");
        return code;
    }

    /**
     * ????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static String sendRemindSmsCode(String code, String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", code);
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_69035710");
        return code;
    }

    /**
     * ????
     * @param code
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static String sendMarketingSmsCode(String code, String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", code);
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_73235005");
        return code;
    }

    /**
     * ??????3
     * @param
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static void sendSmsCodeOrderBefore03(String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_82045062");
    }

    /**
     * ??????2
     * @param
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static void sendSmsCodeOrderBefore02(String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_82070066");
    }

    /**
     * ??????1
     * @param
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static void sendSmsCodeOrderBefore01(String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_82195059");
    }

    /**
     * ??????0
     * @param
     * @param mobiles
     * @return
     * @throws BadRequestException
     */
    public static void sendSmsCodeOrderBefore00(String... mobiles)
            throws BadRequestException {
        Map<String, Object> map = new HashMap<>();
        sendCodeSample(map, SmsCodeUtils.phones(mobiles), "SMS_82200074");
    }

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

    /**
     * ??Do-It ??or??4??????
     * @return
     */
    public static String sendSmsCode() {
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
        }
        logger.info("???????:" + sRand);
        return String.valueOf(sRand);
    }

}
