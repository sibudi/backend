package com.yqg.common.utils;

import com.yqg.common.models.MobileData;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PhoneLocationUtils {
    public static String calcMobileCity(String mobileNumber) throws Exception {

        //??????API??
        //        String urlString = "http://virtual.paipai.com/extinfo/GetMobileProductInfo?mobile="
        //                + mobileNumber + "&amount=10000&callname=getPhoneNumInfoExtCallback";
        //????API??
        String urlString = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="
                + mobileNumber;

        StringBuffer sb = new StringBuffer();
        BufferedReader buffer;
        URL url = new URL(urlString);
        String province = "";
        try {
            //??URL????????
            InputStream in = url.openStream();
            // ??????
            buffer = new BufferedReader(new InputStreamReader(in, "gb2312"));
            String line = null;
            //?????????
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            buffer.close();
            String str=sb.toString().split("=")[1];
            //1???JSONObject
            JSONObject jsonObject2 = JSONObject.fromObject(str);
           // String pro1 = jsonObject2.getString("province");
            MobileData stu = (MobileData) JSONObject.toBean(jsonObject2, MobileData.class);
            province = stu.getProvince();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //?JSONObject?????????
        return province;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(PhoneLocationUtils.calcMobileCity("01053912833"));
    }

}