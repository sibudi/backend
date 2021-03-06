package com.yqg.service.signcontract;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.*;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.mongo.entity.DigiSignRequestMongo;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdOrder.P2PLoanStatusEnum;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdDigisign_error;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.externalChannel.utils.CustomHttpResponse;
import com.yqg.service.order.OrdDeviceInfoService;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.OrderCheckService;
import com.yqg.service.signcontract.response.CallBackResponse;
import com.yqg.service.signcontract.response.SignInfoResponse;
import com.yqg.service.task.AsyncTaskService;
import com.yqg.service.third.digSign.DigiSignService;
import com.yqg.service.third.digSign.DocumentService;
import com.yqg.service.third.digSign.response.DigiSignUserStatusEnum;
import com.yqg.service.third.digSign.response.DocumentStatusResponse;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.signcontract.entity.OrderContract;
import com.yqg.signcontract.entity.OrderContract.DocumentStatus;
import com.yqg.signcontract.entity.UsrSignContractStep;
import com.yqg.signcontract.entity.UsrSignContractStep.SignStepEnum;
import com.yqg.signcontract.entity.UsrSignContractStep.StepResultEnum;
import com.yqg.task.entity.AsyncTaskInfoEntity;
import com.yqg.user.entity.UsrUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class ContractSignService {
    @Autowired
    private ExternalChannelDataService externalChannelDataService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DigiSignService digSignService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private OrderCheckService orderCheckService;
    @Autowired
    private UsrSignContractStepService usrSignContractStepService;
    @Autowired
    private OrdDeviceInfoService ordDeviceInfoService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdDao orderDao;
    @Autowired
    private OrdDigisign_error ordDigisign_error;

    @Autowired
    private ExecutorService executorService;
    @Autowired
    private AsyncTaskService asyncTaskService;

    //private static String DEFAULT_USER_ACTIVATION_CALLBACK_RESPONSE = "{\"rc\":\"00\",\"notif\":\"check from user status api\",
    // \"username\":\"\"}\n";

    private static String DEFAULT_USER_ACTIVATION_CALLBACK_RESPONSE = "\"{\\\"rc\\\":\\\"00\\\",\\\"notif\\\":\\\"check from user status api\\\"," +
            "\\\"username\\\":\\\"none\\\"}\\n\"";

    private static String DEFAULT_SIGN_CALLBACK_RESPONSE = "{\"document_id\":\"DEFAULT_CHECK\",\"result\":\"00\",\"status\":0," +
            "\"notif\":\"Proses tanda tangan berhasil!\"}";



    /***
     *  审核通过准备进入放款（20,5,19 等状态）
     * @param task
     * @return 是否进行签约
     */
    public String changeStatusAfterOrderPass(AsyncTaskInfoEntity task) {
        String signLockKey = RedisContants.REVIEW_SIGN_LOCK + ":" + task.getOrderNo();
        String errorMessage = "";
        redisClient.lockDistributed(signLockKey, "1", 10 * 60);
        if (StringUtils.isEmpty(task.getOrderNo())) {
            log.info("the order is empty");
            return task.getOrderNo();
        }
        asyncTaskService.updateTaskStatus(task, AsyncTaskInfoEntity.TaskStatusEnum.PROCESSING);
        OrdOrder order = ordService.getOrderByOrderNo(task.getOrderNo());
        //executorService.submit(new SignContractTask(digSignService, orderCheckService, ordService, this, order));
        try {
            MDC.put("X-Request-Id", task.getOrderNo());

            boolean activationResult = applyForDigiSign(order);

            // budi: jangan langsung LOANING, di-set sesuai status mula2
            OrdStateEnum orderPassStatus = OrdStateEnum.getEnum(order.getStatus());
            if (activationResult) {
                // Score OK, Digisign success
                // budi: setelah activationResult jangan diubah 20, karena lender belum sign
                // budi: langsung LOANING karena selain status = 2, 17, tidak masuk waitingList digisign lagi

                orderPassStatus = OrdStateEnum.LOANING;
                
            } else if (orderCheckService.histSpecifiedProductWithDecreasedCreditLimit(order.getUuid())) {
                // Score not OK, Digisign skipped
                orderPassStatus = OrdStateEnum.WAITING_CONFIRM;
            } else {
                // budi: jika digisign/asliri error, otomatis hapus table temp_orderrisk (agar tidak ikut terproses risk lagi)  
                // dan otomatis disable asyncTaskInfo dan insert ke table orddigisign_error agar tidak ikut antrean digisign lagi
                orderDao.deleteOrderFromTemp_OrderRisk(order.getUuid());
                asyncTaskService.disableAsyncTaskInfo(task, order); // akan masuk ke P2P lagi
                ordDigisign_error.insertOrddigisign_error(order.getUuid());
                log.info("Digisign for order {} failed.", order.getUuid());
                errorMessage = String.format("Apply for digisign failed. Check log near %s", DateUtils.formDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
            }

            asyncTaskService.updateTaskStatus(task, AsyncTaskInfoEntity.TaskStatusEnum.FINISHED);
            ordService.changeOrderStatus(order, orderPassStatus);
            // budi: remark sms
            //this.sendLoanPassSms(order);
            redisClient.unLock(signLockKey);
        } catch (Exception e) {
            log.error("error for asli & digisign, order: " + order.getUuid(), e);
            //ahalim: Return error message separated by semicolon
            return String.format("%s;%s", order.getUuid(), e.toString());
        } finally {
            MDC.remove("X-Request-Id");
        }
        //ahalim: Return error message separated by semicolon if any
        if (!StringUtils.isEmpty(errorMessage)){
            return String.format("%s;%s", task.getOrderNo(), errorMessage);
        }
        else {
            return task.getOrderNo();
        }
    }

    private boolean applyForDigiSign(OrdOrder order) {
        try {
            //调用ekyc 实名验证
            boolean ekycSuccess = false;
            boolean registerResult = false;
            String invokeAsliSwitch = redisClient.get(RedisContants.INVOKE_ASLI_SWITCH);

            log.info("asliRI switch: {} - digisign switch:{} - bucket: {}, userUuid: {}",
                invokeAsliSwitch, redisClient.get(RedisContants.DIGITAL_SIGN_SWITCH),
                redisClient.get(RedisContants.DIGITAL_SIGN_BUCKET), order.getUserUuid());
            
            if("true".equals(invokeAsliSwitch)){
                log.info("the invoke asli switch is true, userUuid: {}",order.getUserUuid());
                ekycSuccess = digSignService.verifyEkycFromAsli(order.getUserUuid(), order.getUuid());
                if (ekycSuccess) {
                    //调用注册
                    registerResult = digSignService.register(order.getUserUuid(), order.getUuid());
                }
            }
            else{
                registerResult = digSignService.registerWithKYC(order.getUserUuid(), order.getUuid());
            }
            
            boolean activationResult = false;
            if (registerResult) {
                //注册成功调用激活接口
                activationResult = digSignService.activation(order.getUserUuid(), order.getUuid());
                //发送短信
            }
            return activationResult;
        } catch (Exception e) {
            log.error("apply for digiSign error", e);
        }
        return false;
    }

    private void sendLoanPassSms(OrdOrder order) {
        UsrUser user = usrService.getUserByUuid(order.getUserUuid());
        String mobileNumber = "62" + DESUtils.decrypt(user.getMobileNumberDES());
        String contentTemplate = "<Do-It>Nasabah yang terhormat, akun ada mendapat Rp %s , silahkan masuk ke aplikasi Do-It dan ambil uang anda. Semoga" +
                " hari " +
                "anda menyenangkan!";
        final String content = String.format(contentTemplate, order.getAmountApply().toPlainString());
        executorService.submit(new SmsTask(smsServiceUtil, "NOTICE", mobileNumber, content, "ZENZIVA"));


    }


    @AllArgsConstructor
    @Getter
    @Setter
    private class SmsTask implements Runnable {
        private SmsServiceUtil sendService;
        private String smsType;
        private String mobile;
        private String content;
        private String channel;


        @Override
        public void run() {
            try {
                log.info("send review pass sms for: {}", mobile);
                sendService.sendTypeSmsCodeWithType(smsType, mobile, content, channel);
            } catch (Exception e) {
                log.error("send sms error, mobileNumber: " + mobile, e);
            }
        }
    }

    /***
     * 查询用户签约状态，并返回签约数据
     * @param userUuid
     */
    public SignInfoResponse getUserCurrentSignData(String userUuid, String orderNo) throws ServiceException {

        //查询用户最后一步的状态[待激活或者激活成功]
        UsrSignContractStep step = usrSignContractStepService.getLatestStep(userUuid);
        if (step == null) {

            //无相关步骤，异常
            log.error("the sign contract step is empty of orderNo: {},userUuid: {}", orderNo, userUuid);
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        if (step.getSignStep() == SignStepEnum.DIGI_SIGN_ACTIVATION_API.getCode() && step.getStepResult() == StepResultEnum.SUCCESS.getCode()) {
            //待用户激活  --》 需要返回激活页面activation api 接口返回的数据base64后传给app

            Optional<DigiSignRequestMongo> activationRequest = digSignService.getDigiSignRequestResult(orderNo, userUuid,
                    DigiSignRequestMongo.RequestTypeEnum.ACTIVATION);
            if (!activationRequest.isPresent()) {
                log.error("activation data error for orderNo: {},userUuid:{}", orderNo, userUuid);
                throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
            }
            //可能用户已经激活，需要到digiSign check
            DigiSignUserStatusEnum userStatus = digSignService.checkUserStatus(userUuid);
            if (userStatus.equals(DigiSignUserStatusEnum.ACTIVATED)) {
                //已经激活则作激活的相应处理
                confirmUserActivation(userUuid, DEFAULT_USER_ACTIVATION_CALLBACK_RESPONSE, orderNo);
            }

            CustomHttpResponse respData = JsonUtils.deserialize(activationRequest.get().getResponseData(), CustomHttpResponse.class);
            return new SignInfoResponse(SignInfoResponse.SignStepEnum.TO_ACTIVATION.name(), Base64Utils.encode(respData.getContent().getBytes()),
                    -1L);


        } else if (step.getSignStep() == SignStepEnum.DIGI_SIGN_ACTIVATION_CONFIRMED.getCode() && step.getStepResult() == StepResultEnum.SUCCESS.getCode()) {
            //用户已经确认激活--》防止协议文档发送失败，发送文档
            // budi: setelah borrower activation, jangan langsung sendDocument, cek document status dulu
            //boolean documentSuccess = digSignService.sendDocument(userUuid, orderNo);
            boolean documentSuccess = false;
            DocumentStatusResponse documentResponse =  new DocumentStatusResponse();
            try {
                documentResponse = checkDocumentStatus(orderNo);
                if (documentResponse.getJSONFile().getStatus().compareToIgnoreCase("waiting") == 0 && documentResponse.getJSONFile().getWaiting().size() == 1) {
                    documentSuccess = true; // lender signed, borrower not yet
                }
            } catch (Exception e) {
                log.info("check-document-status exception, orderNo: " + orderNo, e);
            }
            
            boolean sendSignSuccess = false;
            if (documentSuccess) { 
                //调用签约返回签约接口，返回签约数据
                // borrower gets the sign url after the lender has signed the docs
                sendSignSuccess = digSignService.signContract(userUuid, orderNo);
            }

            if (documentSuccess && sendSignSuccess) {
                //检查是否已经签约成功
                boolean signCompleted = digSignService.isDocumentSigned(orderNo);
                if (signCompleted) {
                    //已经签章调用相应的信息改状态
                    // if lender and borrower have signed
                    this.confirmSignContract(orderNo, DEFAULT_SIGN_CALLBACK_RESPONSE);
                }
                //已经成功的调用了签约相关api，返回签约页面
                //文档发送失败，提示在发送文档中(可以增加定时任务补)TODO
                //查询签约html，base64处理给到app
                Optional<DigiSignRequestMongo> signApiResult = digSignService.getDigiSignRequestResult(orderNo, userUuid,
                        DigiSignRequestMongo.RequestTypeEnum.SIGN);
                if (!signApiResult.isPresent() || !"true".equals(signApiResult.get().getResponseValid())) {
                    log.error("sign data error for orderNo: {}", orderNo);
                    throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
                }
                //
                CustomHttpResponse respData = JsonUtils.deserialize(signApiResult.get().getResponseData(), CustomHttpResponse.class);
                DocumentStatusResponse savedResponse = JsonUtils.deserialize(respData.getContent(),DocumentStatusResponse.class);
                
                return new SignInfoResponse(SignInfoResponse.SignStepEnum.TO_SIGN_CONTRACT.name(),
                        Base64Utils.encode(savedResponse.getJSONFile().getLink().getBytes()), -1L);
            } else {
                //如果没有成功，进入等待页面
                String interval = redisClient.get(RedisContants.DIGITAL_SIGN_STATUS_CHECK_INTERVAL);
                Long intervalInSecond = 60L;
                if (StringUtils.isNotEmpty(interval)) {
                    intervalInSecond = Long.valueOf(interval);
                }
                return new SignInfoResponse(SignInfoResponse.SignStepEnum.TO_CONTRACT_WAITING.name(), null, intervalInSecond);
            }
        } else {
            //非法状态
            log.info("order status error, orderNo: {}", orderNo);
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
    }


    /**
     * 用户激活确认
     *
     * @param userUuid
     */
    public void confirmUserActivation(String userUuid, String response, String orderNo) {
        //successs: "{\"rc\":\"00\",\"notif\":\"Proses Aktivasi Berhasil\",\"username\":\"Admin111\"}\n"
        boolean success = false;
        if (StringUtils.isNotEmpty(response)) {
            if (response.contains("\n")) {
                response = response.replaceAll("\n", "");
            }
            //格式兼容
            response = JsonUtils.deserialize(response, String.class);
            CallBackResponse callBackResponse = JsonUtils.deserialize(response, CallBackResponse.class);
            success = "00".equals(callBackResponse.getRc());
        }
        UsrSignContractStep dbStep = usrSignContractStepService.getSignContractStepResult(userUuid, SignStepEnum.DIGI_SIGN_ACTIVATION_CONFIRMED);
        if (dbStep != null && UsrSignContractStep.isStepSuccess(dbStep)) {
            //已经确认
            log.info("userUuid: {} already confirmed", userUuid);
            return;
        }
        Optional<OrdOrder> currentOrder = ordService.getCurrentOrder(userUuid);
        usrSignContractStepService.saveSignContractStep(userUuid, currentOrder.get().getUuid(), SignStepEnum.DIGI_SIGN_ACTIVATION_CONFIRMED,
                success ? StepResultEnum.SUCCESS
                        : StepResultEnum.FAILED, response);
        if (success) {
            //激活成功，发送签约文档
            // budi: jika borrower sudah active, jangan langsung sendDocument, cek document status dulu
            //boolean documentSuccess = digSignService.sendDocument(userUuid, currentOrder.get().getUuid());
            boolean documentSuccess = false;
            DocumentStatusResponse documentResponse =  new DocumentStatusResponse();
            try {
                documentResponse = checkDocumentStatus(orderNo);
            } catch (Exception e) {
                log.info("check-document-status exception, orderNo: " + orderNo, e);
            }
            if (documentResponse.getJSONFile().getStatus().compareToIgnoreCase("waiting") == 0 && documentResponse.getJSONFile().getWaiting().size() == 1) {
                documentSuccess = true;
            }

            if (documentSuccess) {
                //调用签约返回签约接口，返回签约数据
                digSignService.signContract(userUuid, currentOrder.get().getUuid());
            }
        }
    }

    public DocumentStatusResponse checkDocumentStatus(String orderNo) throws Exception {

        DocumentStatusResponse documentResponse = new DocumentStatusResponse();
        try {

            CustomHttpResponse response = digSignService.checkDocumentStatus(orderNo);
            documentResponse = JsonUtils.deserialize(response.getContent(), DocumentStatusResponse.class);
            
//            log.info("waiting to signed status: {}, waiting email: {}", documentResponse.getJSONFile().getStatus(), documentResponse.getJSONFile().getWaiting().get(0).getEmail());
        } catch (Exception e1) {

            log.info("check-document-status exception, orderNo: " + orderNo, e1);
//            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }

        return documentResponse;
    }


    /**
     * 用户签约确认
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmSignContract(String orderNo, String response) throws ServiceException {
        log.info("start confirm sign contract orderNo: {}", orderNo);
        //{"document_id":"011904081728510331","result":"00","status":0,"notif":"Proses tanda tangan berhasil!"}
        boolean success = false;
        if (StringUtils.isNotEmpty(response)) {
            response = response.replaceAll("\n", "");
            CallBackResponse callBackResponse = JsonUtils.deserialize(response, CallBackResponse.class);
            success = "00".equals(callBackResponse.getResult());
        }
        if (!success) {
            return;
        }

        //根据前端回调，状态改为待放款,
        OrdOrder order = ordService.getOrderByOrderNo(orderNo);
        if (order.getStatus() != OrdStateEnum.WAITING_SIGN_CONTRACT.getCode()) {
            log.info("the order status error ,orderNo: " + orderNo);
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        // ahalim: Currently normal loan not backward compatible
        ordService.changeOrderStatus(order, OrdStateEnum.LOANING, P2PLoanStatusEnum.WAITING_DISBURSE);             

        //更新签约文档的状态
        documentService.saveDocument(orderNo, order.getUserUuid(), DocumentStatus.SIGN_SUCCESS, null, response);
    }

//    public void checkActivationStatus() {
//        //检查是激活是否ok
//        /****
//         *  查询usrSignContractStep 中step = DIGI_SIGN_ACTIVATION_API 并且当前状态不是DIGI_SIGN_ACTIVATION_CONFIRMED 的用户
//         *  重新调用激活api看是否有返回，有正确返回则认为用户需要去激活，否则更新用户已经激活成功
//         *
//         */
//
//    }

    public Map<String, String> checkSignContractStatus() {
        //检查是否签约成功--》防止网络问题无法知道目前状态
        /****
         *  ，查询orderContract 中SSEND_SUCCESS 状态的订单，调用接口下查看当前文档状态
         *  然后在orderContract 调用 DocumentService.saveDocument 保存结果
         *
         */
        List<OrderContract> documentList = documentService.getNeedToCheckSignStatusContracts();
        if (CollectionUtils.isEmpty(documentList)) {
            return Collections.emptyMap();
        }
        log.info("need to check sign status files: " + documentList.size());
        Map<String, String> errorMap = new HashMap<String, String>();
        for (OrderContract orderContract : documentList) {
            try {
                boolean isDocumentSigned = digSignService.isDocumentSigned(orderContract.getDocumentId());
                if (isDocumentSigned) {
                    OrdOrder order = ordService.getOrderByOrderNo(orderContract.getOrderNo());
                    if (order != null && order.getStatus() == OrdStateEnum.WAITING_SIGN_CONTRACT.getCode()) {
                        // ahalim: Currently normal loan not backward compatible
                        ordService.changeOrderStatus(order, OrdStateEnum.LOANING, P2PLoanStatusEnum.WAITING_DISBURSE);             
                    }
                    documentService.saveDocument(orderContract.getOrderNo(), orderContract.getUserUuid(), DocumentStatus.SIGN_SUCCESS, null,
                            null);
                }
            } catch (Exception e) {
                log.error("check sign status for document: " + orderContract.getDocumentId() + " error", e);
                errorMap.put(orderContract.getDocumentId(), e.toString());
            }
        }
        return errorMap;
    }

    /****
     * 下载已经签约的文档
     */
    public Map<String, String> downloadContract() {
        //下载签约后的文档
        /****
         *  ，查询orderContract 中SIGN_SUCCESS 状态的订单，调用接口下载签约后的pdf 保存到我们自己的空间中
         *  然后在orderContract 调用 DocumentService.saveDocument 保存结果
         *
         */
        List<OrderContract> documentList = documentService.getNeedToDownloadOrderContracts();
        if (CollectionUtils.isEmpty(documentList)) {
            return Collections.emptyMap();
        }
        log.info("need to download files: " + documentList.size());
        Map<String, String> errorMap = new HashMap<String, String>();
        for (OrderContract orderContract : documentList) {
            try {
                String path = digSignService.downloadDocument(orderContract.getDocumentId());
                if (StringUtils.isNotEmpty(path)) {
                    documentService.saveDocument(orderContract.getOrderNo(), orderContract.getUserUuid(), DocumentStatus.DOWNLOAD_SUCCESS, path, null);
                }
            } catch (Exception e) {
                log.error("download for document: " + orderContract.getDocumentId() + " error", e);
                errorMap.put(orderContract.getDocumentId(), e.toString());
            }
        }
        return errorMap;
    }


    public boolean isDigitalSignSwitchOpen(OrdOrder order) {
        //查看redis中开关数据
        String switchOpen = redisClient.get(RedisContants.DIGITAL_SIGN_SWITCH);
        if (!"true".equals(switchOpen)) {
            return false;
        }
        ExternalOrderRelation relation = externalChannelDataService.getExternalOrderNoByRealOrderNoRelation(order.getUuid());
        if (relation != null) {
            //cashcash未打开
            return false;
        }
        //iOS应为版本一直没上线不打开
        Optional<OrdDeviceInfo> deviceInfo = ordDeviceInfoService.getDeviceInfoByOrderNo(order.getUuid());
        if (deviceInfo.isPresent() && deviceInfo.get().getDeviceType().equalsIgnoreCase("iOS")) {
            return false;
        }
        
        // budi: no need check borrowingCount & startTime, all users must be digisign

        // budi: check digisign bucket for today
        int bucket = Integer.parseInt(redisClient.get(RedisContants.DIGITAL_SIGN_BUCKET));
        if( bucket <= 0) {
            if(bucket <= -9999){
                return true;
            }
            log.info("Digisign bucket is already empty.");
            return false;
        }
        bucket -= 1;
        redisClient.set(RedisContants.DIGITAL_SIGN_BUCKET, String.valueOf(bucket));

        return true;
    }

    // budi: only the first x% from yesterday order do digital sign
    public void doDigitalSignReloadBucket() {
        
        int yesterdayOrder = orderDao.countOfYesterdayOrder();
        int percentage = Integer.parseInt(redisClient.get(RedisContants.DIGITAL_SIGN_PERCENTAGE));
        int bucket = ((yesterdayOrder == 0 ? 1 : yesterdayOrder) * percentage/100) + 1; //x% dari jumlah order h-1 dibulatkan ke atas
        int currentBucket = Integer.parseInt(redisClient.get(RedisContants.DIGITAL_SIGN_BUCKET));
        if(currentBucket > -9999) {
            redisClient.set(RedisContants.DIGITAL_SIGN_BUCKET, String.valueOf(bucket));
        }
    }

    public static void main(String[] args) {
        String response = "\"{\\\"rc\\\":\\\"00\\\",\\\"notif\\\":\\\"Proses Aktivasi Berhasil\\\",\\\"username\\\":\\\"xiangcai001\\\"}\\n\"";
        response = JsonUtils.deserialize(response,String.class);
        System.err.println(response);
    }
}
