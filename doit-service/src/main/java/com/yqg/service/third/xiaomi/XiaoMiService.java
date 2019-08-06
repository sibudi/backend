package com.yqg.service.third.xiaomi;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.XiaoMiRecordEnum;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.SignUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.XiaoMiRecord;
import com.yqg.service.order.OrdDeviceInfoService;
import com.yqg.service.pay.RepayService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrHouseWifeDetailDao;
import com.yqg.user.dao.UsrStudentDetailDao;
import com.yqg.user.dao.UsrWorkDetailDao;
import com.yqg.user.entity.UsrHouseWifeDetail;
import com.yqg.user.entity.UsrStudentDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by wanghuaizhou on 2018/7/25.
 */
@Service
@Slf4j
public class XiaoMiService {

    public static final String PRD_XIAOMIN = "prd-xiaomin-";
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdDeviceInfoService ordDeviceInfoService;
    @Autowired
    private RepayService repayService;
    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;
    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;
    @Autowired
    private UsrHouseWifeDetailDao usrHouseWifeDetailDao;

    @Value("${third.xiaomi.url}")
    private String xiaomiUrl;
    @Value("${third.xiaomi.appId}")
    private String appId;
    @Value("${third.xiaomi.appKey}")
    private String appKey;


    private void deleteHistoryFile() {
        try {
            File directory = new File("/LOG/xiaomi");
            String currentDate = DateUtils.formDate(new Date(), DateUtils.FMT_YYYYMMDD);
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                //不是今天创建的删除
                if (!file.getName().contains(PRD_XIAOMIN + currentDate)) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            log.error("delete file error", e);
        }
    }

    // 获取 小米订单相关信息
    public void getUserAndOrderDayDataToMi() {

        try {
            //删除前一天的文件
            deleteHistoryFile();
            Date endDate = new Date();
            // 获取相关的订单,防止文件过大，每次1000单
            int pageNum = 1;
            int pageSize = 1000;
            PageHelper.startPage(pageNum, pageSize);
            List<OrdOrder> orderList = this.ordDao.getXiaoMiOrderList(endDate);
            PageInfo page = new PageInfo(orderList);
            if (CollectionUtils.isEmpty(orderList)) {
                log.info("the orderList is empty");
                return;
            }
            //发送第一页数据
            sendToXiaoMi(orderList, pageNum);

            int totalPage = page.getPages();

            for (int i = pageNum + 1; i <= totalPage; i++) {
                PageHelper.startPage(i, pageSize);
                orderList = this.ordDao.getXiaoMiOrderList(endDate);
                if (CollectionUtils.isEmpty(orderList)) {
                    log.info("the orderList is empty");
                    return;
                }
                sendToXiaoMi(orderList, i);
            }


        } catch (Exception e) {
            log.error("推送订单信息异常" + e.getMessage(), e);
        }
    }


    private void sendToXiaoMi(List<OrdOrder> orderList, int pageNum) {
        try {
            log.info("推送给xiaomi的订单个数" + orderList.size());
            List<XiaoMiRecord> recordsList = new ArrayList<>();
            for (OrdOrder order : orderList) {
                UsrUser usrUser = usrService.getUserByUuid(order.getUserUuid());
                XiaoMiRecord record = new XiaoMiRecord();
                // 获取imei
                String imei = ordDeviceInfoService.getUserDeviceImei(order.getUserUuid(), order.getUuid());
                record.setImeimd5(SignUtils.generateMd5(imei));
                // 姓名
                record.setUserName(usrUser.getRealName());
                // 手机号
                record.setPhoneNo(DESUtils.decrypt(usrUser.getMobileNumberDES()));
                // 生日
                record.setDateOfBirth(getUserBirthday(usrUser));
                // 订单号
                record.setOrderId(order.getUuid());
                //手机品牌：
                record.setPhoneBrand(ordDeviceInfoService.getPhoneBrand(order.getUserUuid(), order.getUuid()));
                //  订单状态和时间相关
                formattOrderStatusAndTime(order, record);
                recordsList.add(record);
            }

            Map<String, List<XiaoMiRecord>> data = new HashMap<>();
            data.put("records", recordsList);
            String jsonStr = JsonUtils.serialize(data);
            sendStrDataAsFile(jsonStr, pageNum);
        } catch (Exception e) {
            log.error("send error,pageNum=" + pageNum, e);
        }
    }

    private void sendStrDataAsFile(String str, int index) {
        if (StringUtil.isEmpty(str)) {
            log.info("the data is empty");
            return;
        }
        //发送文件

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            String dateStr = DateUtils.formDate(new Date(), DateUtils.FMT_YYYYMMDD);
            String fileName = PRD_XIAOMIN + dateStr + "-" + index + ".json";
            File temp = new File("/LOG/xiaomi/" + fileName);
            if(!temp.exists()){
               temp.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(str);
            out.close();

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", temp);
            builder.addTextBody("fileName", fileName, ContentType.APPLICATION_JSON);
            HttpEntity entity = builder.build();


            RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setConnectTimeout(120000).build();
            HttpPost post = new HttpPost(xiaomiUrl);
            post.setConfig(config);
            post.setEntity(entity);
            post.setHeader("appId", appId);
            post.setHeader("appKey", appKey);

            HttpResponse response = httpClient.execute(post);

            log.info("response: {}, for file {}", EntityUtils.toString(response.getEntity()), temp.getName());
        } catch (IOException e) {
            log.info("send data to xiaomi error", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.info("close http client error", e);
                }
            }
        }
    }


    // 将订单状态和时间映射为xiaomi 要求的格式
    private void formattOrderStatusAndTime(OrdOrder order, XiaoMiRecord record) throws Exception {
        /**
         * 1.贷款成功，2.授信/贷款拒绝，3.授信成功，4.进人工，5.用户流失
         */
        if (order.getStatus() == OrdStateEnum.SUBMITTING.getCode()) {


        } else if (order.getStatus() == OrdStateEnum.MACHINE_CHECKING.getCode()
                || order.getStatus() == OrdStateEnum.FIRST_CHECK.getCode()
                || order.getStatus() == OrdStateEnum.SECOND_CHECK.getCode()
                || order.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()
                || order.getStatus() == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()) {
            // 进人工
            record.setOrderStatus(XiaoMiRecordEnum.MANUAL.getCode());
            record.setOrderTime(String.valueOf(order.getUpdateTime().getTime()));
        } else if (order.getStatus() == OrdStateEnum.LOANING.getCode()
                || order.getStatus() == OrdStateEnum.LOANING_DEALING.getCode()) {

            // 授信成功
            record.setOrderStatus(XiaoMiRecordEnum.ELIGIBLE.getCode());
            record.setOrderTime(String.valueOf(order.getUpdateTime().getTime()));
        } else if (order.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()
                || order.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()
                || order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()
                || order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()) {

            // 贷款成功
            record.setOrderStatus(XiaoMiRecordEnum.APPROVED.getCode());
            // 借款金额
            record.setOrderAmount(order.getAmountApply().toString());
            // 借款期限
            record.setOrderTenure(order.getBorrowingTerm().toString());
            // 还款时间
            record.setOrderDueTime(String.valueOf(order.getLendingTime().getTime()));
            record.setOrderRepayTime(String.valueOf(order.getRefundTime().getTime()));


            if (order.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()) {
                int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));
                record.setOverdueDays(String.valueOf(overdueDay));
                record.setOverdueAmount(this.repayService.calculateRepayAmount(order, "1"));
            } else if (order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()) {
                int overdueDay = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(order.getActualRefundTime(), "yyyy-MM-dd"));
                record.setOverdueDays(String.valueOf(overdueDay));
                record.setOverdueAmount(this.repayService.calculateRepayAmountWithRepayOrder(order));
            }
            record.setOrderTime(String.valueOf(order.getUpdateTime().getTime()));

        } else if (order.getStatus() == OrdStateEnum.MACHINE_CHECK_NOT_ALLOW.getCode()
                || order.getStatus() == OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode()
                || order.getStatus() == OrdStateEnum.SECOND_CHECK_NOT_ALLOW.getCode()
                || order.getStatus() == OrdStateEnum.CANCEL.getCode()) {

            // 授信/贷款拒绝
            record.setOrderStatus(XiaoMiRecordEnum.REJECTED.getCode());
            record.setOrderTime(String.valueOf(order.getUpdateTime().getTime()));
        }
    }

    // 获取生日
    private String getUserBirthday(UsrUser user) {
        if (user.getUserRole() != null) {
            if (user.getUserRole() == 1) {
                // 学生
                UsrStudentDetail detail = new UsrStudentDetail();
                detail.setDisabled(0);
                detail.setUserUuid(user.getUuid());
                List<UsrStudentDetail> detailList = this.usrStudentDetailDao.scan(detail);
                if (!CollectionUtils.isEmpty(detailList)) {
                    return detailList.get(0).getBirthday();
                }

            } else if (user.getUserRole() == 2) {
                //  工作者
                UsrWorkDetail detail = new UsrWorkDetail();
                detail.setDisabled(0);
                detail.setUserUuid(user.getUuid());
                List<UsrWorkDetail> detailList = this.usrWorkDetailDao.scan(detail);
                if (!CollectionUtils.isEmpty(detailList)) {
                    return detailList.get(0).getBirthday();
                }
            } else if (user.getUserRole() == 3) {
                //  家庭主妇
                UsrHouseWifeDetail detail = new UsrHouseWifeDetail();
                detail.setDisabled(0);
                detail.setUserUuid(user.getUuid());
                List<UsrHouseWifeDetail> detailList = this.usrHouseWifeDetailDao.scan(detail);
                if (!CollectionUtils.isEmpty(detailList)) {
                    return detailList.get(0).getBirthday();
                }
            }
        }
        return "";

    }

}
