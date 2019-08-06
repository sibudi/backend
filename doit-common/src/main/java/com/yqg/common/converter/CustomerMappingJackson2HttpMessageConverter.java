/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.yqg.common.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqg.common.annotations.CashCashRequest;
import com.yqg.common.annotations.CheetahRequest;
import com.yqg.common.annotations.CompatibleRSA;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.CompatibleResponse;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author
 *
 */

@Slf4j
@Component
public class CustomerMappingJackson2HttpMessageConverter
        extends MappingJackson2HttpMessageConverter {

    private static final Logger logger = LoggerFactory
            .getLogger(CustomerMappingJackson2HttpMessageConverter.class);

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        if (contextClass.getAnnotation(H5Request.class) != null
                || contextClass.getAnnotation(CashCashRequest.class) != null
                || contextClass.getAnnotation(CheetahRequest.class) != null) {

            JavaType javaType = getJavaType(type, contextClass);
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        }

        boolean fromCompatibleRsa = contextClass.getAnnotation(CompatibleRSA.class) != null;


        StringBuilder sBuilder = new StringBuilder();
        byte[] result = new byte[1024];
        int pad = 0;
        while((pad =inputMessage.getBody().read(result)) != -1) {
            sBuilder.append(new String(result, 0, pad));
            result = new byte[1024];
        }

        String json = null;
        try {
            json = getJsonStringFromInput(sBuilder,RSAUtils.privateKeyStr);
        } catch (Exception e) {
            logger.error("Decrypt String error with type:"+type.getTypeName()+"|contextClass: "+contextClass.getName()+"| data:"+sBuilder.toString(), e);
            if(fromCompatibleRsa){
                try {
                    json =  getJsonStringFromInput(sBuilder,RSAUtils.oldPrivateKeyStr);
                } catch (Exception e1) {
                    logger.error("Decrypt String error with old key with type:"+type.getTypeName()+"|contextClass: "+contextClass.getName()+"| " +
                            "data:"+sBuilder.toString(), e);
                }
            }
        }
        JavaType javaType = getJavaType(type, contextClass);
        try {
            return this.objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            log.error("error", e);
        }
        return null;
    }


    private String getJsonStringFromInput(StringBuilder sBuilder, String key) throws Exception {
        byte[] data = Base64Utils.decode(sBuilder.toString());
        byte[] decodedData = RSAUtils.decryptByPrivateKey(data, key);
        String json = new String(decodedData);
        return json;
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        if(object.getClass().getAnnotation(CashCashRequest.class) != null){
            String responseBody = JsonUtils.serialize(object);
            outputMessage.getBody().write(responseBody.getBytes());
            return;
        }

        if(object.getClass().getAnnotation(CheetahRequest.class) != null){
            String responseBody = JsonUtils.serialize(object);
            outputMessage.getBody().write(responseBody.getBytes());
            return;
        }

        if (object.getClass().getAnnotation(H5Request.class) != null) {
            String responseBody = JsonUtils.serialize(object);
            outputMessage.getBody().write(responseBody.getBytes());
            return;
        }



        if (object instanceof ResponseEntitySpec) {
            String responseBody = JsonUtils.serialize(object);
            outputMessage.getBody().write(responseBody.getBytes());
            return;
        }

        byte[] outputMsg = null;
        try {
            String responseBody = JsonUtils.serialize(object);
            //logger.info("response body {}", responseBody);
            outputMsg = responseBody.getBytes();
            //RSAUtils.encryptByPublicKey(responseBody.getBytes(), RSAUtils.publicKeyStr);

            String json = null;
            boolean fromCompatibleRsa = false;
            if(object instanceof ResponseEntity){
                ResponseEntity respObj = (ResponseEntity) object;
                if(respObj.getData()!=null && ( respObj.getData() instanceof CompatibleResponse)){
                    CompatibleResponse appVersionInfo = (CompatibleResponse) respObj.getData();
                    //1.7.0版本之后用心的rsa
                    fromCompatibleRsa = appVersionInfo.getAppVersion().compareTo("1.7.0")<0;
                }

            }
            if(fromCompatibleRsa){
                json = getOutputMsgWithKey(outputMsg, RSAUtils.oldPublicKeyStr);
            }else{
                json = getOutputMsgWithKey(outputMsg,RSAUtils.publicKeyStr);
            }
            outputMessage.getBody().write(json.getBytes());
        } catch (Exception e) {
            logger.error("encrypt String error {}", e);
        }
    }

    public String getOutputMsgWithKey(byte[] outputMsg, String key) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        byte[] output = RSAUtils.encryptByPublicKey(outputMsg, key);
        String json = mapper.writeValueAsString(Base64Utils.encode(output));
        return json;
    }

    public static void main(String[] args) {
        try {
            String d1 = "awz5slmvs4ou3yvsk8L7JHw08haO5oo6ThAmgPGnXMy7ukkqf0I0FwSK89hx0YTzpHNTg2VINu8v3afVkHG6suE5QytWflMQ2Qb1k3tLT5akuHguIC4R+tIxFIOaHyzf3Md2WC5ioJcq0/aTtnzMmLvV7kkaCWPqlBdI6BjilAopYrWqZVScMHfsHrWnnoMp1ISl8XHYB0DA1h255EN9X+NGoTCO2YXckhkyMdtmqbVZ1FoLtKCXkcR45s+EXi/icIWH9lVL0NTXHd8izmWlRXbfoz65vv9Oor1GrQingYHGSbeBX3U8qvkoFpMypPudBYdFSUKqQkISXl3if23HPI3Pn+8kvKxc2doa95FrS+g7q7dJVZPQt2yFpURILRdQXQLUiO8aL7BEg48q0SOxtJIBTNCbTr0Bh/8qAe/+H4pc/5iVhZNwxRBot165fZJySiYVUkGCNVsSY2w1y61mnMQ9lpOTw+7a0HLPPKP512c1OYKrtpG2bMZyqUcnUduuBdYD9Uy8dQxKo3RVBBFOVeWGaJKfG7JlVcARzVCJkZ3JGzakxlx8nebOInwEWKbm/mkD0iPT9DeeyPutb8j3j3GZYZLmJvUX392oC090DabNjkqopBexcMaBdNNMkgcmBMvWQ8kk/1Qt99hTWiOIUIbAEVWYKB53T25GafsBX6QBBUiqfA6kjSS9NxpNxYNXU2Uw0CWGtvyDo6O9ruVgdraiaRr2DijftXOONcHWodUysfmYUhE1REXAuCwkyRTD9pRycNDpNVGq0OGSz2mJyAgKCiSBcgnCjAXIBuTZeYRlhALg6uoto9VYmXGGS9LwyYujnv1DmdOIrHLJ5wmeHw==";

            byte afterdecodeBase64[] = Base64Utils.decode(d1);
            byte[] decodedData = RSAUtils.decryptByPrivateKey(afterdecodeBase64, RSAUtils.oldPrivateKeyStr);
            String json = new String(decodedData);
            System.err.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
