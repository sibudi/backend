package com.yqg.service.third.quiros;

import com.yqg.common.utils.*;
import com.yqg.common.enums.system.ThirdExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.third.quiros.request.QuirosCallRequest;
import com.yqg.service.third.quiros.request.QuirosReportRequest;
import com.yqg.service.third.quiros.request.QuirosReportRequestByDocId;
import com.yqg.management.dao.InfinityBillDao;
import com.yqg.management.dao.QuirosResultDao;
import com.yqg.management.entity.InfinityBillEntity;
import com.yqg.management.entity.QuirosResultEntity;
import com.yqg.order.dao.ManOrderDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.CallResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import springfox.documentation.spring.web.json.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.json.*;

import static org.mockito.Answers.valueOf;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author user
 */
@Component
@Slf4j
public class QuirosService {

    @Value("${third.quiros.grantType}")
    private String GRANT_TYPE;
    @Value("${third.quiros.clientId}")
    private String CLIENT_ID;
    @Value("${third.quiros.clientSecret}")
    private String CLIENT_SECRET;
    @Value("${third.quiros.baseUrl}")
    private String BASE_URL;

    @Autowired
    private OkHttpClient httpClient;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private QuirosResultDao quirosResultDao;
    @Autowired
    private ManOrderDao manOrderDao;

    private final String redisKeyQuiros = "key_quiros";
    private String token = "";
    private String message;
    private String scope = "*";

    public String actionLogin() throws ServiceExceptionSpec {
        String responseStr = "";
        String baseUrl = BASE_URL;

        RequestBody requestBody = new FormBody.Builder().add("grant_type", GRANT_TYPE).add("scope", this.scope)
                .add("client_id", CLIENT_ID).add("client_secret", CLIENT_SECRET).build();

        Request request = new Request.Builder().url(baseUrl += "/oauth/token").post(requestBody)
                .header("Content-Type", "application/json").build();

        httpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(75, TimeUnit.SECONDS)
                .writeTimeout(75, TimeUnit.SECONDS).build();
        try {
            Response response = httpClient.newCall(request).execute();
            responseStr = response.body().string();

            log.info("Result action login, {}", responseStr);
            if (!response.isSuccessful()) {
                log.info("Login failed!!!!, {}", responseStr);
            } else {
                redisClient.set(this.redisKeyQuiros, responseStr);
            }
        } catch (Exception e) {
            log.error("send action login error! ", e);
            throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_500);
        }
        return responseStr;
    }

    public String getToken() throws ServiceExceptionSpec {
        if (this.redisClient.exists(this.redisKeyQuiros)) {
            JSONObject responseObj = new JSONObject(this.redisClient.get(this.redisKeyQuiros));
            this.token = responseObj.getString("access_token");
        } else {
            String responseLogin = this.actionLogin();
            JSONObject responseObj = new JSONObject(responseLogin);
            this.token = responseObj.getString("access_token");
        }
        return this.token;
    }

    public String actionCall2Click(QuirosCallRequest callRequest) throws ServiceExceptionSpec {
        String responseStr = "";
        String getToken = this.getToken();
        String baseUrl = BASE_URL;
        String uuid = UUIDGenerateUtil.uuid();

        if (StringUtils.isBlank(callRequest.getExtension())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_EXTNUMBER_NO);
        }
        if (StringUtils.isBlank(callRequest.getOutbound())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        if (StringUtils.isBlank(callRequest.getOrderId())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }

        callRequest.setOutbound(CheakTeleUtils.telephoneNumberValidForQuiros(callRequest.getOutbound()));
        log.info(callRequest.getOutbound());

        RequestBody requestBody = new FormBody.Builder().add("extension", callRequest.getExtension())
                .add("outbound", callRequest.getOutbound()).add("docid", uuid).add("cusid", callRequest.getOrderId())
                .build();

        Request request = new Request.Builder().url(baseUrl += "/click2call").post(requestBody)
                .header("Content-Type", "application/json").header("Authorization", "Bearer " + getToken).build();
        httpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(75, TimeUnit.SECONDS)
                .writeTimeout(75, TimeUnit.SECONDS).build();

        try {
            Response response = httpClient.newCall(request).execute();

            responseStr = response.body().string();
            log.info("Result Call two click !!!!, {}", responseStr);
            log.info("Code Click2Call, {}", response.code());

            if (!response.isSuccessful()) {
                log.info("Call two click failed!!!!, {}", response.code());
            }

            log.info("Insert Data");
            QuirosResultEntity entity = new QuirosResultEntity();
            if (StringUtils.isNotEmpty(callRequest.getOrderId())) {
                OrdOrder ordOrder = new OrdOrder();
                ordOrder.setUuid(callRequest.getOrderId());
                ordOrder.setDisabled(0);
                List<OrdOrder> ordOrders = manOrderDao.scan(ordOrder);
                if (CollectionUtils.isEmpty(ordOrders)) {
                    // return map;
                }
                entity.setApplyAmount(ordOrders.get(0).getAmountApply());
                entity.setApplyDeadline(ordOrders.get(0).getBorrowingTerm());
                entity.setRealName(callRequest.getRealName());
                entity.setUserName(callRequest.getUserName());
                entity.setOrderNo(callRequest.getOrderId());
                entity.setSourceType(0);
                entity.setCallNode(callRequest.getCallNode());
                entity.setCallType(callRequest.getCallType());

            } else {
                entity.setSourceType(1);
                entity.setRealName("-");
                entity.setUserName("-");
                entity.setApplyAmount(BigDecimal.ZERO);
                entity.setApplyDeadline(0);
                entity.setOrderNo(callRequest.getUserUuid());
                entity.setCallNode(0);
                entity.setCallType(0);
            }
            entity.setUuid(uuid);
            entity.setExtnumber(callRequest.getExtension());
            entity.setDestnumber(callRequest.getDestnumber());
            entity.setUserid(callRequest.getUserUuid());

            log.info(entity.toString());
            quirosResultDao.saveBill(entity);
            return responseStr;

        } catch (Exception e) {
            log.error("send action Call two click error! ", e);
            throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_500);
        }
    }

    public String getMessageByCode(String code) {

        switch (code) {
        case "0000":
            this.message = "sukses";
            break;
        case "1000":
            this.message = "Pengguna tidak valid";
            break;
        case "1100":
            this.message = "Pengguna tidak diijinkan";
            break;
        case "1200":
            this.message = "Informasi yang diminta tidak tersedia";
            break;
        case "2000":
            this.message = "Parameter tidak valid";
            break;
        case "3000":
            this.message = "Masalah internal";
            break;
        case "4000":
            this.message = "Penyedia eksternal tidak dapat menyediakan layanan";
            break;
        case "9999":
            this.message = "Tipe data tidak valid";
            break;
        default:
            this.message = "";
            break;
        }
        return this.message;
    }

    public String actionResultCallByDateRange(QuirosReportRequest reportRequest) throws ServiceExceptionSpec {
        String responseStr = "";
        String getToken = this.getToken();
        String baseUrl = BASE_URL;

        String url = HttpUrl.parse(baseUrl += "/getResultCall").newBuilder()
                .addQueryParameter("fromDate", reportRequest.getFromDate())
                .addQueryParameter("toDate", reportRequest.getToDate())
                .addQueryParameter("per_page", reportRequest.getPer_page())
                .addQueryParameter("page", reportRequest.getPage()).build().toString();

        Request request = new Request.Builder().url(url).method("GET", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + getToken).build();

        httpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(75, TimeUnit.SECONDS)
                .writeTimeout(75, TimeUnit.SECONDS).build();
        try {
            Response response = httpClient.newCall(request).execute();
            responseStr = response.body().string();
            log.info("Code status, {}", response.code());
            if (!response.isSuccessful()) {
                log.info("Result call failed!!!!, {}", responseStr);
            }
        } catch (Exception e) {
            log.error("send action Call result call error! ", e);
            throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_500);
        }
        return responseStr;
    }

    public String actionResultCallById(QuirosReportRequest reportRequest) throws ServiceExceptionSpec {
        String responseStr = "";
        String getToken = this.getToken();
        String baseUrl = BASE_URL;

        String url = HttpUrl.parse(baseUrl += "/getCDRByID").newBuilder().addQueryParameter("id", reportRequest.getId())
                .build().toString();

        Request request = new Request.Builder().url(url).method("GET", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + getToken).build();
        httpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(75, TimeUnit.SECONDS)
                .writeTimeout(75, TimeUnit.SECONDS).build();
        try {
            Response response = httpClient.newCall(request).execute();
            responseStr = response.body().string();
            log.info("Code status, {}", response.code());
            if (!response.isSuccessful()) {
                log.info("Result call failed!!!!, {}", responseStr);
            }
        } catch (Exception e) {
            log.error("send action Call result call error! ", e);
            throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_SIP_500);
        }
        return responseStr;
    }

    public String actionResultCallByDocId(QuirosReportRequestByDocId QuirosReportRequestByDocId)
            throws ServiceExceptionSpec {
        String responseStr = "";
        String getToken = this.getToken();
        String baseUrl = BASE_URL;

        String url = HttpUrl.parse(baseUrl += "/getCDRByDocID").newBuilder()
                .addQueryParameter("docid", QuirosReportRequestByDocId.getDocId()).build().toString();

        Request request = new Request.Builder().url(url).method("GET", null)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + getToken).build();
        httpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(75, TimeUnit.SECONDS)
                .writeTimeout(75, TimeUnit.SECONDS).build();
        try {
            Response response = httpClient.newCall(request).execute();
            responseStr = response.body().string();

            log.info("Code status, {}", response.code());

            if (!response.isSuccessful()) {

                if (response.code() != 500) {
                    log.info("Result call failed!!!!, {}", responseStr);

                }
            }
        } catch (Exception e) {
            log.error("send action Call result call error! ", e);
            responseStr = "Kesalahan pada sistem";
        }
        return responseStr;
    }

    public boolean quirosUpdateRecordingUrl() throws ServiceExceptionSpec {

        QuirosReportRequestByDocId entity = new QuirosReportRequestByDocId();
        List<QuirosResultEntity> result = this.getOrderListToUpdate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String downloadURL = "";
        String status = "";
        int duration = 0;
        String responseStr = "";
        Calendar recordBeginTime = Calendar.getInstance();
        Calendar recordEndTime = Calendar.getInstance();

        if (CollectionUtils.isEmpty(result)) {
            log.info("No data to Update");
        } else {
            for (QuirosResultEntity list : result) {
                entity.setDocId(list.getUuid());
                // entity.setDocId("8A86F6694EAC4BA4ACFD4469B6B2E69");
                try {
                    responseStr = this.actionResultCallByDocId(entity);
                    JSONObject jsonObject = new JSONObject(responseStr);
                    log.info(responseStr);
                    downloadURL = jsonObject.getJSONObject("result").get("recordingfile").toString();
                    status = jsonObject.getJSONObject("result").get("status").toString();
                    duration = Integer.parseInt(jsonObject.getJSONObject("result").get("duration").toString());
                    recordBeginTime
                            .setTime(dateFormat.parse(jsonObject.getJSONObject("result").get("created").toString()));
                    recordEndTime
                            .setTime(dateFormat.parse(jsonObject.getJSONObject("result").get("created").toString()));
                    recordBeginTime.add(Calendar.MINUTE, -2);
                    recordEndTime.add(Calendar.MINUTE, -2);
                    recordEndTime.add(Calendar.SECOND, duration);

                    java.sql.Timestamp recordBeginTimeSql = convertUtilToSql(recordBeginTime);
                    java.sql.Timestamp recordEndTimeSql = convertUtilToSql(recordEndTime);

                    // update Database
                    this.updateTableSuccess(entity.getDocId(), downloadURL, status, duration, recordBeginTimeSql,
                            recordEndTimeSql);
                } catch (Exception e) {
                    this.updateTableFailed(entity.getDocId());
                    log.info("Transaction not found for uuid : " + entity.getDocId());
                    continue;
                }

            }
            log.info("Success");
        }
        return true;
    }

    private List<QuirosResultEntity> getOrderListToUpdate() {
        return quirosResultDao.getOrderListToUpdate();
    }

    private void updateTableSuccess(String uuid, String downloadUrl, String status, int duration,
            java.sql.Timestamp recordBeginTime, java.sql.Timestamp recordEndTime) {
        quirosResultDao.updateTableSuccess(downloadUrl, uuid, status, duration, recordBeginTime, recordEndTime);
    }

    private void updateTableFailed(String uuid) {
        quirosResultDao.updateTableFailed(uuid);
    }

    private static Timestamp convertUtilToSql(Calendar uDate) {
        java.sql.Timestamp sDate = new Timestamp(uDate.getTimeInMillis());
        return sDate;
    }

}
