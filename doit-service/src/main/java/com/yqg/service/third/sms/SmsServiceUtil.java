package com.yqg.service.third.sms;

import com.alibaba.fastjson.JSON;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.HttpTools;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.user.response.SmsCheckInforbipResponse;
import com.yqg.service.user.response.SmsSendInforbipResponse;
import com.yqg.service.user.response.SmsVeriResponse;
import com.yqg.system.dao.SysSmsCodeDao;
import com.yqg.system.entity.SysSmsCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Didit Dwianto on 2017/11/29.
 */
@Component
@Slf4j
public class SmsServiceUtil {

    @Value("${HttpUrl.smsUrl}")
    private String smsUrl;
    @Value("${HttpUrl.smsUrlV2}")
    private String smsUrlV2;
    @Value("${HttpUrl.smsUrlTwilio}")
    private String smsUrlTwilio;
    @Value("${HttpUrl.smsUrlTwilioVerify}")
    private String smsUrlTwilioVerify;
    @Value("${SmsChannelType.type}")
    private String channelType;

    @Value("${HttpUrl.smsUrlInforbip}")
    private String smsUrlInforbip;
    @Value("${HttpUrl.smsUrlInforbipV2}")
    private String smsUrlInforbipV2;
    @Value("${HttpUrl.smsUrlInforbipVerify}")
    private String smsUrlInforbipVerify;
    @Value("${HttpUrl.smsUrlInforbipVerifyV2}")
    private String smsUrlInforbipVerifyV2;
    // ??????
    @Autowired
    private SysThirdLogsService sysThirdLogsService;

    @Autowired
    private SysSmsCodeDao sysSmsCodeDao;

    @Autowired
    private RedisClient redisClient;

    public enum SmsTypeEnum {
        EMERGENCY_LINKMAN_NUMBER_ERROR_REMINDER, //紧急联系人号码错误外呼后短信提醒
    }

    static Map<SmsTypeEnum,String> smsContent = new HashMap<>();
    static {
        smsContent.put(SmsTypeEnum.EMERGENCY_LINKMAN_NUMBER_ERROR_REMINDER,"<Do-It> Terdapat nomor yang tidak valid di kontak Anda, mohon untuk mengganti dan mengisi ulang");
    }

    public void sendSms(SmsTypeEnum smsType, String mobileNumber) {
        log.info("send sms for smsType: {} , number: {}", smsType, mobileNumber);

        //http请求头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String, String> map = new HashMap<>();
        map.put("smsChannel", channelType);
        map.put("productType", "DO_IT");
        map.put("smsTrigger", smsType.name());
        map.put("sendFrom", "DO_IT");
        map.put("sendTo", mobileNumber);
        map.put("content", smsContent.get(smsType));

        String smsResponse = HttpTools.post(smsUrl, headers, map, 60000, 60000);
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null, DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(), null,
                JSON.toJSONString(map), null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null, DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(), null, null, smsResponse);
        log.info("send sms end... {} ", mobileNumber);
    }

    //?????
    public void sendTypeSmsCode(String smsType, String mobileNumber,String content) throws Exception {
        sendTypeSmsCodeWithTypeV2(smsType,mobileNumber,content,channelType);
    }

    //?????
    public void sendTypeSmsCodeWithType(String smsType, String mobileNumber,String content,String type) throws Exception {
        log.info("开始发送验证码================================》"+mobileNumber);
        //http请求头信息
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String,String> map = new HashMap<String, String>();
        map.put("smsChannel",type);
        map.put("productType","DO_IT");
        map.put("smsTrigger",smsType);
        map.put("sendFrom","DO_IT");
        map.put("sendTo",mobileNumber);
        map.put("content",content);
        String smsResponse =   HttpTools.post(smsUrl,headers,map,30000,30000);
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);
        log.info("结束发送验证码================================》"+mobileNumber);
    }

    //Janhsen: for sms OTP only
    public void sendTypeSmsCodeWithTypeV2(String smsType, String mobileNumber,String content,String type) throws Exception {
        log.info("开始发送验证码================================》"+mobileNumber);
        //http请求头信息
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String,String> map = new HashMap<String, String>();
        map.put("smsChannel",type);
        map.put("productType","DO_IT");
        map.put("smsTrigger",smsType);
        map.put("sendFrom","DO_IT");
        map.put("sendTo",mobileNumber);
        map.put("content",content);
        String smsResponse =   HttpTools.post(smsUrlV2,headers,map,30000,30000);
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);
        log.info("结束发送验证码================================》"+mobileNumber);
    }


    /**
     * 手机号码的短信验证码落库
     * @param mobile
     * @param smsCode
     * @param smsType
     */
    public void insertSysSmsCode(String mobile, String smsCode, Integer smsType) {
        log.info("手机号码{}的验证码{},类型是{}",mobile,smsCode,smsType);
        SysSmsCode sysSmsCode = new SysSmsCode();
        sysSmsCode.setMobile(DESUtils.encrypt(mobile));
        sysSmsCode.setSmsCode(smsCode);
        sysSmsCode.setSmsType(smsType);
        sysSmsCodeDao.insert(sysSmsCode);
    }


    // 使用Twilio 发送短信 或者语音验证码
    public void sendSmsByTwilio(String viaType, String mobileNumber) throws Exception {

        log.info("使用Twilio 开始发送验证码================================》"+mobileNumber);
        //http请求头信息
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String,String> map = new HashMap<String, String>();
        if (viaType.equals("1")){
            map.put("via","sms");
        }else if (viaType.equals("1")){
            map.put("smsChannel","call");
        };
        map.put("sendTo",mobileNumber);
        String smsResponse =   HttpTools.post(smsUrlTwilio,headers,map,30000,30000);
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);
        log.info("使用Twilio 结束发送验证码================================》"+mobileNumber);
    }

    // 使用Twilio 校验验证码
    public SmsVeriResponse sendSmsByTwilioVerify(String code, String mobileNumber) throws Exception {

        //http请求头信息
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String,String> map = new HashMap<String, String>();
        map.put("code",code);
        map.put("sendTo",mobileNumber);
        String smsResponse =   HttpTools.post(smsUrlTwilioVerify,headers,map,60000,60000);
        log.info("校验验证码的结果为"+smsResponse);
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);

        SmsVeriResponse loanResponse = JsonUtils.deserialize(smsResponse,SmsVeriResponse.class);
        return loanResponse;
    }

    // 使用Inforbip 发送短信
    public void sendSmsByInforbip(String mobileNumber) throws Exception {

        log.info("使用Inforbip 开始发送验证码================================》"+mobileNumber);
        //http请求头信息
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String,String> map = new HashMap<String, String>();
        map.put("phoneNumber",mobileNumber);
        String smsResponse =   HttpTools.post(smsUrlInforbip,headers,map,30000,30000);
        SmsSendInforbipResponse response =  JsonUtils.deserialize(smsResponse,SmsSendInforbipResponse.class);
        if (!StringUtils.isEmpty(response.getPinId())){
            setInforbipPinid(mobileNumber,response.getPinId());
        }
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);
        log.info("使用Inforbip 结束发送验证码================================》"+mobileNumber);
    }

    public void sendSmsByInforbipV2(String mobileNumber) throws Exception {

        log.info("使用Inforbip 开始发送验证码================================》"+mobileNumber);
        //http请求头信息
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type","application/x-www-form-urlencoded");
        headers.put("X-AUTH-TOKEN", "1111");
        //HTTP请求参数
        Map<String,String> map = new HashMap<String, String>();
        map.put("phoneNumber",mobileNumber);
        String smsResponse =   HttpTools.post(smsUrlInforbipV2,headers,map,30000,30000);
        SmsSendInforbipResponse response =  JsonUtils.deserialize(smsResponse,SmsSendInforbipResponse.class);
        if (!StringUtils.isEmpty(response.getPinId())){
            setInforbipPinid(mobileNumber,response.getPinId());
        }
        // 请求数据落库，SysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
        // 响应数据落库，sysThirdLogs
        sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);
        log.info("使用Inforbip 结束发送验证码================================》"+mobileNumber);
    }


    // 使用Inforbip 校验验证码
    public SmsCheckInforbipResponse sendSmsByInforbipVerify(String code, String mobileNumber) throws Exception {

        SmsCheckInforbipResponse loanResponse = null;

        String pinId = getInforbipPinid(mobileNumber);
        if (!StringUtils.isEmpty(pinId)){

            //http请求头信息
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-type","application/x-www-form-urlencoded");
            headers.put("X-AUTH-TOKEN", "1111");
            //HTTP请求参数
            Map<String,String> map = new HashMap<String, String>();
            map.put("pinId",pinId);
            map.put("pin",code);
            String smsResponse =   HttpTools.post(smsUrlInforbipVerify,headers,map,60000,60000);
            log.info("校验验证码的结果为"+smsResponse);
            // 请求数据落库，SysThirdLogs
            sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
            // 响应数据落库，sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);

            loanResponse = JsonUtils.deserialize(smsResponse,SmsCheckInforbipResponse.class);;
        }else {
            throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_TIMEOUT);
        }
        return loanResponse;
    }

    //Janhsen: temporary to make sure sms only works for OTP
    public SmsCheckInforbipResponse sendSmsByInforbipVerify2(String code, String mobileNumber) throws Exception {

        SmsCheckInforbipResponse loanResponse = null;

        String pinId = getInforbipPinid(mobileNumber);
        if (!StringUtils.isEmpty(pinId)){

            //http请求头信息
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-type","application/x-www-form-urlencoded");
            headers.put("X-AUTH-TOKEN", "1111");
            //HTTP请求参数
            Map<String,String> map = new HashMap<String, String>();
            map.put("pinId",pinId);
            map.put("pin",code);
            String smsResponse =   HttpTools.post(smsUrlInforbipVerifyV2,headers,map,60000,60000);
            log.info("校验验证码的结果为"+smsResponse);
            // 请求数据落库，SysThirdLogs
            sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null, JSON.toJSONString(map),null);
            // 响应数据落库，sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(null,DESUtils.encrypt(mobileNumber), SysThirdLogsEnum.SMS_SERVICE.getCode(),null,null,smsResponse);

            loanResponse = JsonUtils.deserialize(smsResponse,SmsCheckInforbipResponse.class);;
        }else {
            throw new ServiceException(ExceptionEnum.USER_CHECK_SMS_CODE_TIMEOUT);
        }
        return loanResponse;
    }

    // 存储 Inforbip 的pinid
    public void setInforbipPinid(String mobileNumber, String pinid){
        StringBuilder stringBuilder=new StringBuilder(RedisContants.SMS_KEY_INFORBIP);
        stringBuilder.append(mobileNumber);
        redisClient.set(stringBuilder.toString(),String.valueOf(pinid), RedisContants.EXPIRES_SECOND);
    }

    // 读取 Inforbip 的pinid
    public String getInforbipPinid(String mobileNumber){
        StringBuilder stringBuilder=new StringBuilder(RedisContants.SMS_KEY_INFORBIP);
        stringBuilder.append(mobileNumber);
        return redisClient.get(stringBuilder.toString());
    }

    //  发送10w营销短信
    public void sendSms(String num) throws Exception{

        String content = "Wow aku kemarin dapat 1,2 juta loh dari aplikasi ini. Coba deh kakak klik disini goo.gl/Urwui4";

        String filePath = "/LOG/excel/sms/t1128-"+num+".xlsx";
        List<Map<String,Object>> mobileList =  getSmsData(filePath);
        for (Map<String,Object> map : mobileList){

           String mobile = map.get("mobile").toString();
           String id = String.valueOf(Integer.valueOf(map.get("id").toString())+1);
           if (!mobile.startsWith("62")){
               mobile = "62" + mobile;
           }
            log.info("本次发送的号码在excel中的行数为："+id+"本次发送的短信号码为："+mobile);
            // 发送提醒短信
            sendTypeSmsCodeWithType("营销短信-20190218",mobile, content,"ZENZIVA");
        }

        String[] strings = {"6281807883260","6281212300428"};
        for (String s: strings){
            sendTypeSmsCodeWithType("营销短信-20190218",s, content,"ZENZIVA");
        }
    }


    // 读取excel文件中的数据
    public  List<Map<String,Object>> getSmsData(String path) throws Exception{

        Workbook wb =null;
        Sheet sheet = null;
        Row row = null;
        String cellData = null;
        String filePath = path;
        wb = readExcel(filePath);
        //用来存放表中数据
        List<Map<String,Object>> stringList = new ArrayList<>();
        if(wb != null){

            //获取第一个sheet
            sheet = wb.getSheetAt(0);
            //获取最大行数
            int rownum = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i<rownum; i++) {
                row = sheet.getRow(i);
                if(row !=null) {
                    Map<String,Object> map = new HashMap<>();
                    cellData = (String) getCellFormatValue(row.getCell(0));
                    map.put("id",row.getRowNum());
                    map.put("mobile",cellData);
                    stringList.add(map);
                }
            }
        }
           return stringList;
    }

    //读取excel
    public static Workbook readExcel(String filePath) throws Exception{
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }


    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
                case Cell.CELL_TYPE_NUMERIC:{
                    NumberFormat nf = NumberFormat.getInstance();
                    String s = nf.format(cell.getNumericCellValue());
                   //这种方法对于自动加".0"的数字可直接解决
                   //但如果是科学计数法的数字就转换成了带逗号的，例如：12345678912345的科学计数法是1.23457E+13，经过这个格式化后就变成了字符串“12,345,678,912,345”，这也并不是想要的结果，所以要将逗号去掉
                    if (s.indexOf(",") >= 0) {
                        s = s.replace(",", "");
                    }
                    cellValue = s;
                    break;
                }
                case Cell.CELL_TYPE_FORMULA:{
                    //判断cell是否为日期格式
                    if(DateUtil.isCellDateFormatted(cell)){
                        //转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    }else{
                        //数字
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:{
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }

    //  发送风控短信短信
    public void sendSmsToRisk(String fileName,String smsContent) throws Exception{



        String filePath = "/LOG/excel/sms/"+fileName;
        List<Map<String,Object>> mobileList =  getSmsData(filePath);
        for (Map<String,Object> map : mobileList){

            String mobile = map.get("mobile").toString();
            String id = String.valueOf(Integer.valueOf(map.get("id").toString())+1);
            if (!mobile.startsWith("62")){
                mobile = "62" + mobile;
            }
            log.info("本次发送的号码在excel中的行数为："+id+"本次发送的短信号码为："+mobile);
            // 发送提醒短信
            sendTypeSmsCodeWithType("风控短信-20181221",mobile, smsContent,"ZENZIVA");
        }

        String[] strings = {"6281807883260","6281212300428"};
        for (String s: strings){
            sendTypeSmsCodeWithType("风控短信-20181221",s, smsContent,"ZENZIVA");
        }
    }

}

