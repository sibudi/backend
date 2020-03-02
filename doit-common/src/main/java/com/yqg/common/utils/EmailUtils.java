package com.yqg.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailUtils 
{ 
    // private static String notificationServiceEmailURL = "";
    // public static String notificationServiceEmailToken = "";

    // @Value("${notificationservice.emailUrl}")
    // public void setEmailURL(String url) {
    //     notificationServiceEmailURL = url;
    // }

    // @Value("${notificationservice.token}")
    // public void setToken(String token) {
    //     notificationServiceEmailToken = token;
    // }
    
    public static boolean isValid(String email) 
    { 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                            "[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                            "A-Z]{2,7}$"; 
                              
        Pattern pat = Pattern.compile(emailRegex); 
        if (email == null) 
            return false; 
        return pat.matcher(email).matches(); 
    }

    // janhsen: deprecated please use notificationservice
    // public static String sendEmail(String email, String message, String subject) throws Exception{
    //     if(isValid(email)){
    //         Map<String, String> headers = new HashMap<>();
    //         headers.put("x-authorization-token", notificationServiceEmailToken);
    //         headers.put("Content-Type", "application/json");
    //         JSONObject json = new JSONObject();
    //         json.put("message", message);
    //         json.put("to", email);
    //         json.put("subject", subject);
    //         json.put("attachments", new JSONArray());

    //         String resultRes = HttpTools.post(notificationServiceEmailURL, headers, json.toString(), 30000, 30000);

    //         return resultRes;
    //     }
    //     else{
    //         throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_EMAIL);
    //     }
        
    // }

    public static String maskEmail(String email){
        if(isValid(email)){
            String[] emailAddress = email.split("@", 2);
            char[] emailPrefix = emailAddress[0].toCharArray();
            if(emailPrefix.length > 3){
                for (int i=0; i<emailPrefix.length; i++){
                    if(i > 1 && i < emailPrefix.length-1){
                        emailPrefix[i] = '*';
                    }
                }
            }
            else{
                for (int i=0; i<emailPrefix.length; i++){
                    if(i < emailPrefix.length-1){
                        emailPrefix[i] = '*';
                    }
                }
            }
        
            return String.copyValueOf(emailPrefix) + "@" + emailAddress[1];
        }

        return "";
    }
} 