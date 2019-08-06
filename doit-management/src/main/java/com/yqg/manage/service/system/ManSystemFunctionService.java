package com.yqg.manage.service.system;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.*;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.service.system.request.CheakMobileNoRequest;
import com.yqg.manage.util.ExcellPoiUtils;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.entity.UsrBlackList;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Didit Dwianto on 2018/1/7.
 */
@Slf4j
@Component
public class ManSystemFunctionService {

    private Logger logger = LoggerFactory.getLogger(ManSystemFunctionService.class);

    @Autowired
//    @Qualifier("managementExecutorService")
    private ExecutorService executorService;

    @Value("${downlaod.writerPath}")
    private String writerPath;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SmsServiceUtil smsServiceUtil;

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ManUserDao manUserDao;

    // 查询账户余额url
    @Value("${pay.cheakBcaBalanceUrl}")
    private String CHEAK_BCA_BALANCE_URL;
    @Value("${pay.cheakXenditBalanceUrl}")
    private String CHEAK_XENDIT_BALANCE_URL;
    @Value("${pay.token}")
    private String PAY_TOKEN;

    @Autowired
    private OkHttpClient httpClient;


    /**
     * 将用户加密手机号脱敏
     */
    public String cheakUserMobileNunberWithDecryption(CheakMobileNoRequest request) {

        String mobileNumber = request.getMobileNumber();
        if (!StringUtils.isEmpty(mobileNumber)) {
            return DESUtils.decrypt(mobileNumber);
        }
        return "";
    }

    /**
     * 手机号加密
     *
     * @param cheakMobileNoRequest
     * @return
     */
    public String encryptUserMobile(CheakMobileNoRequest cheakMobileNoRequest) {

        if (StringUtils.isEmpty(cheakMobileNoRequest.getEncryptMobile())) {
            return "";
        }

        return DESUtils.encrypt(cheakMobileNoRequest.getEncryptMobile());
    }


    public String downloadUserMobile(String type, MultipartFile fileResonse) throws ServiceExceptionSpec {

        try {
            List<String> phone = new ArrayList();
            if (!type.equals("3")) {

                Workbook workbook = ExcellPoiUtils.getWorkbok(fileResonse.getInputStream(), fileResonse);
                //Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel2003/2007/2010都是可以处理的
                /**
                 * 设置当前excel中sheet的下标：0开始
                 */
                Sheet sheet = workbook.getSheetAt(0);   // 遍历第一个Sheet
                // 为跳过第一行目录设置count
                int count = 0;
                //获得需要加减密的字段位置
                int index = 0;
                for (Row row : sheet) {
                    // 跳过第一行的目录
                    if (count == 0) {
                        count++;
                        if (row.getCell(0).getRichStringCellValue().getString().trim().toLowerCase().contains("mobile")) {
                            index = 0;
                        } else {
                            index = 1;
                        }
                        continue;
                    }
                    // 如果当前行没有数据，跳出循环
                    if (row.getCell(0).toString().equals("")) {
                        continue;
                    }
//                    String rowValue = row.getCell(index).getRichStringCellValue().getString();
                    String rowValue = (String) SmsServiceUtil.getCellFormatValue(row.getCell(index));
                    if (type.equals("1")) {
                        phone.add(DESUtils.encrypt(rowValue));
                    } else if (type.equals("0")) {
                        phone.add(DESUtils.decrypt(rowValue));
                    }
                }
            }
            File file = new File(writerPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            String[] headerStr = {"phone"};
            CsvWriter csvWriter = new CsvWriter(writerPath, ',', Charset.forName("GBK"));
            csvWriter.writeRecord(headerStr);
            for (String item : phone) {
                String[] itemArr = {item};
                csvWriter.writeRecord(itemArr);
            }
            csvWriter.close();
        } catch (Exception e) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        return writerPath;
    }

    /**
     * 读取文件并发送短信
     * @return
     * @throws ServiceExceptionSpec
     */
    public Boolean readExcellAndSendSms(String smsType, String content,
                                       String type,String code, MultipartFile fileResonse) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(smsType)
                || StringUtils.isEmpty(content) || StringUtils.isEmpty(type) || StringUtils.isEmpty(code)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //判断验证码是否正确
        String rightCode = sysParamService.getSysParamValue("manage:batchSendSms:code");
        if (!code.equals(rightCode)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_CHECK_SMS_CODE_ERROR);
        }
        try {

            //todo 科学计数法问题
            Workbook workbook = ExcellPoiUtils.getWorkbok(fileResonse.getInputStream(), fileResonse);
            //Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel2003/2007/2010都是可以处理的
            /**
             * 设置当前excel中sheet的下标：0开始
             */
            //用来存放表中数据
            if(workbook != null){
                executorService.submit(() -> {
                    //获取第一个sheet
                    Sheet sheet = workbook.getSheetAt(0);
                    Row row = null;
                    String mobile = null;
                    //获取最大行数
                    int rownum = sheet.getPhysicalNumberOfRows();
                    int count = 0;
                    try {
                        for (int i = 1; i<rownum; i++) {
                            row = sheet.getRow(i);
                            if(row !=null) {
                                mobile = (String) SmsServiceUtil.getCellFormatValue(row.getCell(0));
//                                log.info("sss============={}", mobile);
                                if (!StringUtils.isEmpty(mobile)){

                                    // 发送提醒短信
                                    if (StringUtils.isEmpty(mobile)) {
                                        continue;
                                    }
                                    String tempPhone = CheakTeleUtils.telephoneNumberValid2(mobile);
                                    if (StringUtils.isEmpty(tempPhone)) {
                                        continue;
                                    }
                                    tempPhone = "62" + tempPhone;
                                    count ++;
                                    log.info("本次发送的号码在excel中的行数为："+row.getRowNum()+"本次发送的短信号码为："+tempPhone);
                                    smsServiceUtil.sendTypeSmsCodeWithType(smsType, tempPhone,content,type);
                                }
                            }
                        }
                        log.info("sendSmsBatch done =====");
                        //如果没有成功发送一个短信就邮件提醒
                        if (count == 0) {
                            sendCodeToEmail("本次发送短信未成功发送一单，请联系管理员" +
                                    "（出现问题的原因可能是：1.excel中不止一个sheet;2.电话号码不正确, smsType :" + smsType);
                        }
                    } catch (Exception e) {
                        //如果发送失败 发送邮箱提醒
                        sendCodeToEmail("本次发送的短信失败，出现异常，请查询日志!");
                    }
                });
            }
//            让验证码失效
            sysParamService.setSysParamValue("manage:batchSendSms:code", "----");
        } catch (Exception e) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        return true;
    }

    /**
     * 只有固定的IP用户能够下载文件
     */
    public void getXmlFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String ipStr = redisClient.get(RedisContants.CAN_DOWNFILE_IP);
        String ipAddr = GetIpAddressUtil.getIpAddr(request);
        log.info("local ip is :" + ipAddr);
        if (StringUtils.isEmpty(ipAddr)) {
            return;
        }
        String[] ips = ipStr.split(",");
        for (String ip : ips) {
            if (ipAddr.equals(ip)) {
//                boolean flag = false;
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + "StrukturData--Do-It."
                        + ("zip"));// 设置文件名
                OutputStream os = response.getOutputStream();
                InputStream stream = new FileInputStream("/MyUpload/twilio/StrukturData--Do-It.zip");
                try {
                    os.write(IOUtils.toByteArray(stream));
                } catch (FileNotFoundException e) {
                    stream = new FileInputStream("/MyUpload/twilio/StrukturData--Do-It.rar");
                    os.write(IOUtils.toByteArray(stream));
                }
                os.flush();
               os.close();
            }
        }
    }

    /**
     * 发送验证码到邮件
     */
    public boolean sendCodeToEmail(String content) {

        log.info("批量发送短信 验证码 发送");

        //随机生成四位数字
        String code = "";
        if (StringUtils.isEmpty(content)) {
            for (int index = 0 ; index < 4; index ++) {
                code += Double.valueOf(Math.random()*10).intValue();
            }
        } else {
            code = content;
        }
        log.info("发送的验证码为："+code);

        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setId(LoginSysUserInfoHolder.getLoginSysUserId());
        List<ManUser> manUsers = manUserDao.scan(manUser);
        // 接受者
        String receivers = "wanghuaizhou@yinyichina.cn,tonggen@yinyichina.cn,lijiahong@yinyichina.cn,liangdezhao@yinyichina.cn,liquanxia@yinyichina.cn";

        String theme = "批量发送短信-验证码";

        MimeMessage message = null;
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        String[] mails = receivers.split(",");
        try {
            message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("wanghuaizhou@yishufu.com");
            helper.setTo(mails);
            helper.setSubject(theme);
            StringBuffer sb = new StringBuffer();
            // 邮件前面的说明
            sb.append("<h4>本次发送短信的验证码为："+code+"</h4>");
//            String sb = "<html>点击人的id是：" + LoginSysUserInfoHolder.getLoginSysUserId() + "</html>";
            sb.append("<h4>请求发送验证码的是："+ (CollectionUtils.isEmpty(manUsers) ? "" : manUsers.get(0).getUsername()) +"</h4>");
            log.info(sb.toString());
            helper.setText(sb.toString(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(message);
        sysParamService.setSysParamValue("manage:batchSendSms:code", code);

        return true;
    }


    /**
     *  查询用户账户余额
     * */
    public String checkAccountBalance(String channel,String account) throws Exception{

        if (!redisClient.lockRepeatWithSeconds("checkAccountBalance", 5)) {
            log.error("请勿频繁操作");
            throw new ServiceExceptionSpec(ExceptionEnum.FREQUENT_OPERATION);
        }

        String requestUrl = "";
        RequestBody requestBody = null;
        if (channel.equals("BCA")){
            requestUrl = CHEAK_BCA_BALANCE_URL;
            requestBody = new FormBody.Builder()
                    .add("accountNumber",account)  // 账号
                    .build();
        }else if (channel.equals("XENDIT")) {
            requestUrl = CHEAK_XENDIT_BALANCE_URL;
            requestBody = new FormBody.Builder()
                    .add("AccountType","CASH")  // 账户类型
                    .build();
        }
        String loanBalance = "";


        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .header("X-AUTH-TOKEN", PAY_TOKEN)
                .build();

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 查询放款账户余额
            log.info("查询账户余额 请求后返回:{}", JsonUtils.serialize(responseStr));
            JSONObject res = JSONObject.parseObject(responseStr);
            if (res.get("code").equals("0")){
                if (channel.equals("BCA")) {

                    loanBalance = res.get("data").toString();
                }else  if (channel.equals("XENDIT")) {

                    JSONObject date = JSONObject.parseObject(res.get("data").toString());
                    loanBalance = date.get("balance").toString();
                }
            }
        }
        return loanBalance;
    }

    /**
     * 下载文件
     */
    public void getExcelFile(HttpServletResponse response, String path) throws IOException {

        if (StringUtils.isEmpty(path)) {
            return;
        }
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + path);// 设置文件名
        OutputStream os = response.getOutputStream();
        File file = new File(path);
        log.info("file result is " + file.exists());
        try {
            InputStream stream = new FileInputStream(path);
            os.write(IOUtils.toByteArray(stream));
        } catch (Exception e) {
            logger.error("getExcelFile error", e);
        }
        os.flush();
        os.close();
    }

    @Autowired
    private UsrBlackListDao usrBlackListDao;
    /**
     * 读取文件并发送短信
     * @return
     * @throws ServiceExceptionSpec
     */
    public Boolean addUserToBlackList(String type, MultipartFile fileResonse) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(type)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        try {

            Workbook workbook = ExcellPoiUtils.getWorkbok(fileResonse.getInputStream(), fileResonse);
            /**
             * 设置当前excel中sheet的下标：0开始
             */
            //用来存放表中数据
            if(workbook != null){
                executorService.submit(() -> {
                    //获取第一个sheet
                    Sheet sheet = workbook.getSheetAt(0);
                    //获取最大行数
                    int rownum = sheet.getPhysicalNumberOfRows();
                    int count = 0;
                    try {
                        for (int i = 1; i<rownum; i++) {
                            Row row = sheet.getRow(i);
                            if(row != null) {
                                UsrBlackList blackList = new UsrBlackList();
                                String userName = (String) SmsServiceUtil.getCellFormatValue(row.getCell(0));
                                String idCardNo = (String) SmsServiceUtil.getCellFormatValue(row.getCell(1));
                                String mobile = (String) SmsServiceUtil.getCellFormatValue(row.getCell(2));
                                if (StringUtils.isNotEmpty(mobile)) {
                                    String tempPhone = CheakTeleUtils.telephoneNumberValid2(mobile);
                                    mobile = DESUtils.encrypt(tempPhone);
                                    blackList.setMobileDes(mobile);
                                }
                                if (StringUtils.isNotEmpty(userName)) {
                                    blackList.setRemark(userName);
                                }
                                if (StringUtils.isNotEmpty(idCardNo)) {
                                    blackList.setIdCardNo(idCardNo);
                                }
                                blackList.setType(Integer.valueOf(type));
                                usrBlackListDao.insert(blackList);
                                count ++;
                            }
                        }
                        log.info("addUserToBlackList done ===== the count is {}", count);
                    } catch (Exception e) {
                        //如果发送失败 发送邮箱提醒
                        sendCodeToEmail("send addUserToBlackList is error!");
                    }
                });
            }
        } catch (Exception e) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        return true;
    }

    @Autowired
    private SysProductDao sysProductDao;
    /**
     * 查询现在所有的有效产品
     * @return
     */
    public List<BigDecimal> allSysProduct() {

        return sysProductDao.allSysProduct();
    }
}
