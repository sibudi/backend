package com.yqg.service.externalChannel.utils;

import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.MD5Util;
import com.yqg.service.externalChannel.config.Cash2Config;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.yqg.service.externalChannel.config.CheetahConfig;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Slf4j
public class SecurityUtil {

    /***
     * cashcash数据双重md5签名
     * @param data
     * @return
     */
    public static String cash2DataSignature(Map<String, Object> data, long timestamp, Cash2Config config) {

        TreeMap<String, Object> treeMap = sortMapDataByKey(data);
        String jsonData = JsonUtils.serialize(treeMap);//签名参数

        String md5String = config.getToken() + "*|*" + jsonData + "@!@" + timestamp;

        log.info("md5 加密参数: " + md5String);

        String sign = MD5Util
            .md5LowerCase(md5String);
        sign = MD5Util.md5LowerCase(sign);
        log.info("md5 加密结果: " + sign);
        return sign;
    }

    public static void main(String[] args) {
        String md5s = "RFUF4-CBX25-E3R34-F9M5J-UOM7G-GLTDW*|*{\"admin_amount\":\"0\",\"amount_type\":\"0\",\"appid\":\"10000\"," +
                "\"approval_amount\":\"0\",\"approval_term\":\"0\",\"conclusion\":\"40\",\"interest_amount\":\"0\",\"interest_rate\":\"0\",\"order_no\":\"19020722380004885209\",\"partner_key\":\"vcy4a5mfx26paycud7b1q26bj\",\"partner_name\":\"Do-it\",\"pay_amount\":\"0\",\"remark\":\"填写资料不符合\",\"term_type\":\"0\",\"term_unit\":\"1\",\"version\":\"1\"}@!@1549678523 ";
        System.err.println(MD5Util
                .md5LowerCase(md5s));
    }


    private static TreeMap<String, Object> sortMapDataByKey(Map<String, Object> param) {
        TreeMap<String, Object> treeMap = new TreeMap<>(Comparator.naturalOrder());
        treeMap.putAll(param);
        return treeMap;
    }



    /***
     * 猎豹sign
     * @param signData
     * @return
     */
    public static String cheetahEncryptionSign(String signData, CheetahConfig config) {

        try {
            log.info("加密前的rawData为: " + signData);

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            log.info(config.getSecretKey());
            SecretKeySpec secret_key = new SecretKeySpec(config.getSecretKey().getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String sign = Base64Utils.encode(sha256_HMAC.doFinal(signData.getBytes()));
            log.info("sign 加密结果: " + sign);
            return sign;
        }
        catch (Exception e){
            log.error("加解密异常",e);
        }

       return "";
    }



}
