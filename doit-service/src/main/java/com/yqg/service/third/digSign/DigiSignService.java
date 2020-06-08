package com.yqg.service.third.digSign;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.dao.DigiSignRequestMongoDal;
import com.yqg.mongo.entity.DigiSignRequestMongo;
import com.yqg.mongo.entity.DigiSignRequestMongo.RequestTypeEnum;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.utils.CustomHttpResponse;
import com.yqg.service.externalChannel.utils.HttpUtil;
import com.yqg.service.order.OrdService;
import com.yqg.service.signcontract.UsrSignContractStepService;
import com.yqg.service.third.asli.AsliService;
import com.yqg.service.third.asli.request.AsliPlusVerificationRequest;
import com.yqg.service.third.asli.response.AsliPlusVerificationResponse;
import com.yqg.service.third.digSign.reqeust.ActivationRequest;
import com.yqg.service.third.digSign.reqeust.DigiRequest;
import com.yqg.service.third.digSign.reqeust.RegisterRequest;
import com.yqg.service.third.digSign.reqeust.RegisterRequestKYC;
import com.yqg.service.third.digSign.reqeust.SendDocumentRequest;
import com.yqg.service.third.digSign.response.Base64FileResponse;
import com.yqg.service.third.digSign.response.DigiResponse;
import com.yqg.service.third.digSign.response.DigiSignUserStatusEnum;
import com.yqg.service.third.digSign.response.DocumentStatusResponse;
import com.yqg.service.user.service.UserAttachmentInfoService;
import com.yqg.service.user.service.UserDetailService;
import com.yqg.service.user.service.UsrService;
import com.yqg.signcontract.entity.OrderContract;
import com.yqg.signcontract.entity.OrderContract.DocumentStatus;
import com.yqg.signcontract.entity.UsrSignContractStep;
import com.yqg.signcontract.entity.UsrSignContractStep.SignStepEnum;
import com.yqg.signcontract.entity.UsrSignContractStep.StepResultEnum;
import com.yqg.user.entity.UsrAddressDetail;
import com.yqg.user.entity.UsrAttachmentInfo;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;

/***
 * DigSign related service
 */
@Service
@Slf4j
public class DigiSignService {
    public static final BigDecimal DEFAULT_SCORE_LIMIT = new BigDecimal("75.0");
    @Autowired
    private DigiSignConfig digiSignConfig;
    @Autowired
    private DigiSignRequestMongoDal digiSignRequestMongoDal;
    @Autowired
    private DigSignParamService digSignParamService;
    @Autowired
    private UserAttachmentInfoService userAttachmentInfoService;
    @Autowired
    private UsrSignContractStepService usrSignContractStepService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private AsliService asliService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private RedisClient redisClient;

    // p2p url (curently not used)
    @Value("${p2p.host}")
    private String HOST_URL;
    @Value("${p2p.url.findOneByCreditorNo}")
    private String FIND_BY_CREDITOR_NO;
    @Value("${p2p.url.userBasicInfoView}")
    private String USER_BASIC_INFO;

    /**
     * 调用asli 进行身份验证，自拍照验证
     * @param userId
     * @param orderNo
     * @return
     */
    public boolean verifyEkycFromAsli(String userId, String orderNo) {
        //是否已经成功做过该步骤
        UsrSignContractStep stepInfo = usrSignContractStepService.getSignContractStepResult(userId, SignStepEnum.EKYC_PLUS_VERIFICATION);
        if (UsrSignContractStep.isStepSuccess(stepInfo)) {
            //成功
            log.info("order already ekyc identity success. orderNo: {}", orderNo);
            return true;
        }else{
            //检查是否有上一笔记录
            if(stepInfo!=null){
                log.info("asli ekyc failed. go to loaning directly. orderNo: {}", orderNo);
                return false;
            }
        }

        String invokeAsliSwitch = redisClient.get(RedisContants.INVOKE_ASLI_SWITCH);
        if("false".equals(invokeAsliSwitch)){
            log.info("the invoke asli switch is false, userUuid: {}",userId);
            return false;
        }
        Optional<AsliPlusVerificationRequest> request = digSignParamService.getEkycIdentityValidationData(userId);
        if (!request.isPresent()) {
            log.info("the param is empty for ekyc ,userUuid: {}", userId);
            return false;
        }
        AsliPlusVerificationRequest reqData = request.get();
        reqData.setOrderNo(orderNo);
        reqData.setUserUuid(userId);
        Optional<AsliPlusVerificationResponse> ekycIdentityResponse = asliService.plusVerification(reqData);
        boolean ekycIdentitySuccess = false;

        String jsonResponse = null;
        if (ekycIdentityResponse.isPresent()) {
            ekycIdentitySuccess = ekycIdentityResponse.get().isEkycVerifySuccess(getSelfieScoreLimit());
            jsonResponse = JsonUtils.serialize(ekycIdentityResponse.get());
        } else {
            log.info("order ekyc return empty. orderNo: {}", orderNo);
        }
        //保存步骤结果
        usrSignContractStepService.saveSignContractStep(userId, orderNo, SignStepEnum.EKYC_PLUS_VERIFICATION, ekycIdentitySuccess ?
                StepResultEnum.SUCCESS
                : StepResultEnum.FAILED, jsonResponse);

        return ekycIdentitySuccess;
    }

    private BigDecimal getSelfieScoreLimit(){
        String limitScore = redisClient.get(RedisContants.DIGITAL_SIGN_SELFIE_SCORE_LIMIT);
        if(StringUtils.isNotEmpty(limitScore)){
            return new BigDecimal(limitScore);
        }
        return DEFAULT_SCORE_LIMIT;
    }

    private boolean digiSignInvokeSwitchNotOpen(){
        String invokeAsliSwitch = redisClient.get(RedisContants.INVOKE_DIGI_SIGN_SWITCH);
        if("false".equals(invokeAsliSwitch)){
            log.info("the invoke digiSign switch is false");
            return true;
        }
        return false;
    }
    /***
     * 调用digiSign 注册
     * @param userUuid
     * @param orderNo
     * @return
     */
    public boolean register(String userUuid, String orderNo) {
        //检查是否注册成功

        log.info("register digisign with userId: {}", userUuid);

        UsrSignContractStep stepInfo = usrSignContractStepService.getSignContractStepResult(userUuid, SignStepEnum.DIGI_SIGN_REGISTER);
        if (UsrSignContractStep.isStepSuccess(stepInfo)) {
            //成功
            log.info("user already register. userId: {}", userUuid);
            return true;
        }
        if(digiSignInvokeSwitchNotOpen()){
            return false;
        }
        //查询相关数据
        Optional<RegisterRequest> requestData = digSignParamService.getRegisterData(userUuid);
        if (!requestData.isPresent()) {
            log.error("the register data for digisign is empty userId: {}", userUuid);
            return false;
        }
        List<UsrAttachmentInfo> attachmentInfoList = userAttachmentInfoService.getAttachmentListByUserId(userUuid);
        Optional<UsrAttachmentInfo> idCardInfo =
                attachmentInfoList.stream().filter(elem -> UsrAttachmentEnum.ID_CARD.getType() == elem.getAttachmentType()).findFirst();
        Optional<UsrAttachmentInfo> selfieInfo =
                attachmentInfoList.stream().filter(elem -> UsrAttachmentEnum.SELFIE.getType() == elem.getAttachmentType()).findFirst();

        if (!idCardInfo.isPresent() || !selfieInfo.isPresent()) {
            log.error("the attachment file is empty, userId: {}", userUuid);
            return false;
        }
        //下载文件流

        byte[] idCardFileStream = userAttachmentInfoService.getAttachmentStream(idCardInfo.get());
        byte[] selfieFile = userAttachmentInfoService.getAttachmentStream(selfieInfo.get());
        if (idCardFileStream == null || selfieFile == null) {
            log.info("file stream is empty, userUuid: {}", userUuid);
            return false;
        }
        Map<String, String> dataMap = new HashMap<>();
        DigiRequest digiRequest = new DigiRequest<RegisterRequest>().withJsonFile(requestData.get());
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));
        Map<String, byte[]> fileMap = new HashMap<>();
        fileMap.put("fotoktp", idCardFileStream);
        fileMap.put("fotodiri", selfieFile);
        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getRegisterUrl(), dataMap, fileMap, buildDigiSignHeaderMap());

        //查询最新一笔申请中的订单
        Optional<OrdOrder> currentOrder = ordService.getCurrentOrder(userUuid);
        boolean isRegisterSuccess = false;
        //如果返回是成功的
        if (response.isResponseOk()) {
            DigiResponse digResponse = JsonUtils.deserialize(response.getContent(), DigiResponse.class);
            isRegisterSuccess = "00".equals(digResponse.getJSONFile().getResult());
        }
        //保存结果到mongo
        saveRequest(currentOrder.get().getUuid(), userUuid, JsonUtils.serialize(dataMap), JsonUtils.serialize(response),
                RequestTypeEnum.Registration, String.valueOf(isRegisterSuccess));
        usrSignContractStepService.saveSignContractStep(userUuid, currentOrder.get().getUuid(), SignStepEnum.DIGI_SIGN_REGISTER, isRegisterSuccess ? StepResultEnum.SUCCESS
                : StepResultEnum.FAILED, JsonUtils.serialize(response));


        return isRegisterSuccess;
    }

    /***
     * 调用digiSign 注册
     * @param userUuid
     * @param orderNo
     * @return
     */
    public boolean registerWithKYC(String userUuid, String orderNo) {

        log.info("register digisign WithKYC with userId: {}", userUuid);

        //检查是否注册成功
        UsrSignContractStep stepInfo = usrSignContractStepService.getSignContractStepResult(userUuid, SignStepEnum.DIGI_SIGN_REGISTER);
        if (UsrSignContractStep.isStepSuccess(stepInfo)) {
            //成功
            log.info("user already register. userId: {}", userUuid);
            return true;
        }
        if(digiSignInvokeSwitchNotOpen()){
            return false;
        }
        //查询相关数据
        Optional<RegisterRequestKYC> requestData = digSignParamService.getRegisterDataWithKYC(userUuid);
        if (!requestData.isPresent()) {
            log.error("the register data for digisign is empty userId: {}", userUuid);
            return false;
        }
        List<UsrAttachmentInfo> attachmentInfoList = userAttachmentInfoService.getAttachmentListByUserId(userUuid);
        Optional<UsrAttachmentInfo> idCardInfo =
                attachmentInfoList.stream().filter(elem -> UsrAttachmentEnum.ID_CARD.getType() == elem.getAttachmentType()).findFirst();
        Optional<UsrAttachmentInfo> selfieInfo =
                attachmentInfoList.stream().filter(elem -> UsrAttachmentEnum.SELFIE.getType() == elem.getAttachmentType()).findFirst();

        if (!idCardInfo.isPresent() || !selfieInfo.isPresent()) {
            log.error("the attachment file is empty, userId: {}", userUuid);
            return false;
        }
        //下载文件流

        byte[] idCardFileStream = userAttachmentInfoService.getAttachmentStream(idCardInfo.get());
        byte[] selfieFile = userAttachmentInfoService.getAttachmentStream(selfieInfo.get());
        if (idCardFileStream == null || selfieFile == null) {
            log.info("file stream is empty, userUuid: {}", userUuid);
            return false;
        }
        Map<String, String> dataMap = new HashMap<>();
        DigiRequest digiRequest = new DigiRequest<RegisterRequestKYC>().withJsonFile(requestData.get());
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));
        Map<String, byte[]> fileMap = new HashMap<>();
        fileMap.put("fotoktp", idCardFileStream);
        fileMap.put("fotodiri", selfieFile);
        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getRegisterUrl(), dataMap, fileMap, buildDigiSignHeaderMap());

        //查询最新一笔申请中的订单
        Optional<OrdOrder> currentOrder = ordService.getCurrentOrder(userUuid);
        boolean isRegisterSuccess = false;
        //如果返回是成功的
        if (response.isResponseOk()) {
            DigiResponse digResponse = JsonUtils.deserialize(response.getContent(), DigiResponse.class);
            isRegisterSuccess = "00".equals(digResponse.getJSONFile().getResult());
        }
        //保存结果到mongo
        saveRequest(currentOrder.get().getUuid(), userUuid, JsonUtils.serialize(dataMap), JsonUtils.serialize(response),
                RequestTypeEnum.Registration, String.valueOf(isRegisterSuccess));
        usrSignContractStepService.saveSignContractStep(userUuid, currentOrder.get().getUuid(), SignStepEnum.DIGI_SIGN_REGISTER, isRegisterSuccess ? StepResultEnum.SUCCESS
                : StepResultEnum.FAILED, JsonUtils.serialize(response));


        return isRegisterSuccess;
    }

    /**
     * 调用api获取用户激活的html
     * @param userUuid
     * @param orderNo
     * @return
     */
    public boolean activation(String userUuid, String orderNo) {
        //
        UsrSignContractStep stepInfo = usrSignContractStepService.getSignContractStepResult(userUuid, SignStepEnum.DIGI_SIGN_ACTIVATION_API);
        if (UsrSignContractStep.isStepSuccess(stepInfo)) {
            //成功
            log.info("user already invoke activation api. userId: {}", userUuid);
            return true;
        }
        if(digiSignInvokeSwitchNotOpen()){
            return false;
        }
        UsrUser user = usrService.getUserByUuid(userUuid);
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setEmailUser(userDetailService.getUserDetailInfo(user).getEmail());
        activationRequest.setUserid(digiSignConfig.getDoitAdminEmail());
        DigiRequest digiRequest = new DigiRequest<ActivationRequest>().withJsonFile(activationRequest);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));

        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getActivationUrl(), dataMap, null, buildDigiSignHeaderMap());

        //查询最新一笔申请中的订单
        Optional<OrdOrder> currentOrder = ordService.getCurrentOrder(userUuid);

        boolean isActivateApiSuccess = false;
        //如果返回是成功的
        if (response.isResponseOk()) {
            //返回纯html页面
            isActivateApiSuccess = StringUtils.isNotEmpty(response.getContent());
        }
        //保存结果到mongo
        saveRequest(currentOrder.get().getUuid(), userUuid, JsonUtils.serialize(dataMap), JsonUtils.serialize(response), RequestTypeEnum.ACTIVATION
                , String.valueOf(isActivateApiSuccess));
        usrSignContractStepService.saveSignContractStep(userUuid, currentOrder.get().getUuid(), SignStepEnum.DIGI_SIGN_ACTIVATION_API, isActivateApiSuccess ?
                StepResultEnum.SUCCESS
                : StepResultEnum.FAILED, JsonUtils.serialize(response));
        return isActivateApiSuccess;
    }

    /***
     * 发送协议文档
     * @param userUuid
     * @param orderNo
     * @return
     */
    public boolean sendDocument(String userUuid, String orderNo) {
        try {
            //检查签约文档是否已经发送过，如果已经发送则无需再次发送
            Optional<OrderContract> document = documentService.getOrderContract(orderNo);
            if (document.isPresent() && document.get().getStatus() != DocumentStatus.SEND_FAILED.getCode()) {
                log.info("the contract document already send for orderNo: {}", orderNo);
                return true;
            }
            UsrUser user = usrService.getUserByUuid(userUuid);
            UserDetailService.UserDetailInfo detailInfo = userDetailService.getUserDetailInfo(user);

            String automaticSignKUser = redisClient.get(RedisContants.DIGITAL_SIGN_AUTOMATICSIGNKUSER);
            String automaticSignEmail = redisClient.get(RedisContants.DIGITAL_SIGN_AUTOMATICSIGNEMAIL);
            String automaticSignRealName = redisClient.get(RedisContants.DIGITAL_SIGN_AUTOMATICSIGNREALNAME);

            SendDocumentRequest sendDocumentRequest = new SendDocumentRequest();
            sendDocumentRequest.setDocumentId(orderNo); //协议文档id自定义，以orderNo为文档id
            sendDocumentRequest.setUserid(digiSignConfig.getDoitAdminEmail());
            List<SendDocumentRequest.SendToDetail> sendDetailList = new ArrayList<>();
            sendDetailList.add(new SendDocumentRequest.SendToDetail(user.getRealName(), detailInfo.getEmail()));
            sendDetailList.add(new SendDocumentRequest.SendToDetail(automaticSignRealName, automaticSignEmail));
            sendDocumentRequest.setSendToList(sendDetailList); //发送用户列表

            List<SendDocumentRequest.RequestSign> reqSignList = new ArrayList<>();
            SendDocumentRequest.RequestSign userSign = new SendDocumentRequest.RequestSign();
            userSign.setEmail(detailInfo.getEmail());
            userSign.setName(user.getRealName());
            userSign.setPage("15");
            userSign.setLlx("440");
            userSign.setLly("300");
            userSign.setUrx("520");
            userSign.setUry("370");
            userSign.setAutoOrManual("mt");
            reqSignList.add(userSign);
            SendDocumentRequest.RequestSign adminSign = new SendDocumentRequest.RequestSign();
            adminSign.setEmail(automaticSignEmail);
            adminSign.setName(automaticSignRealName);
            adminSign.setPage("15");
            adminSign.setLlx("50");
            adminSign.setLly("300");
            adminSign.setUrx("150");
            adminSign.setUry("370");
            adminSign.setAutoOrManual("at");
            adminSign.setKuser(automaticSignKUser);
            reqSignList.add(adminSign);
            sendDocumentRequest.setRequestSignList(reqSignList);

            Map<String, String> dataMap = new HashMap<>();
            DigiRequest digiRequest = new DigiRequest<SendDocumentRequest>().withJsonFile(sendDocumentRequest);
            dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));
            Map<String, byte[]> fileMap = new HashMap<>();
            byte[] fileBytes = createContractPdf(userUuid, orderNo);
            if (fileBytes == null) {
                log.info("cannot create contract pdf for orderNo: " + orderNo);
                return false;
            }
            fileMap.put("file", fileBytes);
            CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getSendDocumentUrl(), dataMap, fileMap, buildDigiSignHeaderMap());

            boolean sendSuccess = false;
            //如果返回是成功的
            if (response.isResponseOk()) {
                DigiResponse digResponse = JsonUtils.deserialize(response.getContent(), DigiResponse.class);
                sendSuccess = "00".equals(digResponse.getJSONFile().getResult());
            }
            //保存结果到mongo
            saveRequest(orderNo, userUuid, JsonUtils.serialize(dataMap), JsonUtils.serialize(response), RequestTypeEnum.SEND_DOCUMENT,
                    String.valueOf(sendSuccess));
            documentService.saveDocument(orderNo, userUuid, sendSuccess ? DocumentStatus.SEND_SUCCESS : DocumentStatus.SEND_FAILED, null,
                    JsonUtils.serialize(response));

            return sendSuccess;
        } catch (Exception e) {
            log.error("send document error, orderNo: " + orderNo, e);
        }
        return false;
    }


    /***
     * 调用api 进行签约（生成签约html）
     * @param userUuid
     * @param orderNo
     * @return
     */
    public boolean signContract(String userUuid, String orderNo) {
        //检查是否已经有相关的签约页面
        Optional<DigiSignRequestMongo> existData = getDigiSignRequestResult(orderNo, userUuid, RequestTypeEnum.SIGN);
        if (existData.isPresent() && "true".equals(existData.get().getResponseValid())) {
            log.info("order : {} already have signContract info", orderNo);
            return true;
        }
        UsrUser user = usrService.getUserByUuid(userUuid);
        UserDetailService.UserDetailInfo userDetailInfo = userDetailService.getUserDetailInfo(user);
        Optional<OrderContract> orderContract = documentService.getOrderContract(orderNo);
        String documentId = orderNo;  //documentId默认使用orderNo
        if (orderContract.isPresent()) {
            documentId = orderContract.get().getDocumentId();
        }
        Map<String, String> jsonFileMap = new HashMap<>();
        jsonFileMap.put("userid", digiSignConfig.getDoitAdminEmail());
        jsonFileMap.put("document_id", documentId);
        jsonFileMap.put("email_user", userDetailInfo.getEmail());
        DigiRequest digiRequest = new DigiRequest<Map<String, String>>().withJsonFile(jsonFileMap);
        //
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));

        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getSignUrl(), dataMap, null, buildDigiSignHeaderMap());
        boolean signSuccess = false;
        //如果返回是成功的
        if (response.isResponseOk()) {
            signSuccess = StringUtils.isNotEmpty(response.getContent());
            //正常的话返回纯html页面，异常返回：{"JSONFile":{"notif":"Request API tidak lengkap.","result":"28"}}
            if (response.getContent() != null && response.getContent().contains("\"notif\"")) {
                DigiResponse remoteResp = JsonUtils.deserializeIgnoreError(response.getContent(), DigiResponse.class);
                if (remoteResp != null && !remoteResp.getJSONFile().getResult().equals("00")) {
                    signSuccess = false;
                }
            }
        }
        //保存结果到mongo
        saveRequest(orderNo, userUuid, JsonUtils.serialize(dataMap), JsonUtils.serialize(response), RequestTypeEnum.SIGN, String.valueOf(signSuccess));
        return signSuccess;
    }

    /***
     * 下载签约后的pdf文件
     * @param documentId
     */
    public String downloadDocument(String documentId) {
        Map<String, String> jsonFileMap = new HashMap<>();
        jsonFileMap.put("userid", digiSignConfig.getDoitAdminEmail());
        jsonFileMap.put("document_id", documentId);
        DigiRequest digiRequest = new DigiRequest<Map<String, String>>().withJsonFile(jsonFileMap);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));
        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getApiDocumentDownloadUrl(), dataMap, null, buildDigiSignHeaderMap(), true);

        try {
            String currentDateDirectory = createCurrentDateDir(digiSignConfig.getContractDir());
            String filePath = currentDateDirectory + File.separator + documentId + ".pdf";
            File f = new File(filePath);
            if (response.isResponseOk()) {
                Base64FileResponse base64Resp = JsonUtils.deserialize(response.getContent(), Base64FileResponse.class);
                FileOutputStream outputStream = new FileOutputStream(f);
                outputStream.write(Base64.decodeBase64(base64Resp.getJSONFile().getFile()));
                outputStream.flush();
                outputStream.close();
                return filePath;
            }
        } catch (Exception e) {
            log.error("down load file from digiSign error,documentId: " + documentId, e);
        }
        return null;
    }

    private String createCurrentDateDir(String rootDir) {
        String dateStr = DateUtils.formDate(new Date(), DateUtils.FMT_YYYY_MM_DD);
        String currentDateDirectory = rootDir + File.separator + dateStr;
        try {
            File f = new File(currentDateDirectory);
            if (!f.exists()) {
                f.mkdir();
            }
        } catch (Exception e) {
            log.error("create current date dir error", e);
        }
        return currentDateDirectory;
    }


    /***
     * 文档是否签约
     * @param documentId
     * @return
     */
    public boolean isDocumentSigned(String documentId) {
        CustomHttpResponse response = checkDocumentStatus(documentId);
        if (response.isResponseOk()) {
            DocumentStatusResponse documentResponse = JsonUtils.deserialize(response.getContent(), DocumentStatusResponse.class);
            if ("Complete".equalsIgnoreCase(documentResponse.getJSONFile().getStatus())) {
                return true;
            }
        }
        return false;
    }

    /***
     * 检查文档的状态
     * @param documentId
     */
    public CustomHttpResponse checkDocumentStatus(String documentId) {
        Map<String, String> jsonFileMap = new HashMap<>();
        jsonFileMap.put("userid", digiSignConfig.getDoitAdminEmail());
        jsonFileMap.put("document_id", documentId);
        DigiRequest digiRequest = new DigiRequest<Map<String, String>>().withJsonFile(jsonFileMap);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));
        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getApiDocumentStautsUrl(), dataMap, null, buildDigiSignHeaderMap());
        return response;
    }


    /***
     * 根据email检查用户状态
     * @param userUuid
     */
    public DigiSignUserStatusEnum checkUserStatus(String userUuid) {
        Map<String, String> jsonFileMap = new HashMap<>();
        jsonFileMap.put("userid", digiSignConfig.getDoitAdminEmail());

        UsrUser user = usrService.getUserByUuid(userUuid);
        UserDetailService.UserDetailInfo detailInfo = userDetailService.getUserDetailInfo(user);
        jsonFileMap.put("email", detailInfo.getEmail());

        DigiRequest digiRequest = new DigiRequest<Map<String, String>>().withJsonFile(jsonFileMap);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("jsonfield", JsonUtils.serialize(digiRequest));

        CustomHttpResponse response = HttpUtil.sendMultiPartRequest(digiSignConfig.getApiUserStatusUrl(), dataMap, null, buildDigiSignHeaderMap());
        if (response.isResponseOk()) {
            String content = response.getContent().trim().replaceAll("\n", "");
            DigiResponse checkUserStatusResp = JsonUtils.deserialize(content, DigiResponse.class);
            return DigiSignUserStatusEnum.getEnumFromValue(checkUserStatusResp.getJSONFile().getNotif());
        }
        return DigiSignUserStatusEnum.UNKNOWN;
    }


    public void saveRequest(String orderNo, String userUuid, String requestData, String responseData, RequestTypeEnum requestType,
                            String responseValid) {
        DigiSignRequestMongo mongoEntity = new DigiSignRequestMongo();
        mongoEntity.setCreateTime(new Date());
        mongoEntity.setUpdateTime(new Date());
        mongoEntity.setOrderNo(orderNo);
        mongoEntity.setUserUuid(userUuid);
        mongoEntity.setReqeustData(requestData);
        mongoEntity.setRequestType(requestType.name());
        mongoEntity.setResponseData(responseData);
        mongoEntity.setResponseValid(responseValid);
        mongoEntity.setDisabled(0);
        digiSignRequestMongoDal.insert(mongoEntity);
    }




    public Optional<DigiSignRequestMongo> getDigiSignRequestResult(String orderNo, String userUuid, RequestTypeEnum requestType) {
        DigiSignRequestMongo searchParam = new DigiSignRequestMongo();
        searchParam.setOrderNo(orderNo);
        searchParam.setUserUuid(userUuid);
        searchParam.setRequestType(requestType.name());
        searchParam.setDisabled(0);
        List<DigiSignRequestMongo> dbList = digiSignRequestMongoDal.find(searchParam);
        if (CollectionUtils.isEmpty(dbList)) {
            return Optional.empty();
        }
        return dbList.stream().max(Comparator.comparing(DigiSignRequestMongo::getUpdateTime));
    }

    public Map<String, String> buildDigiSignHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + digiSignConfig.getToken());
        return headerMap;
    }

    public byte[] createContractPdf(String userUuid, String orderNo) {
        try {
            OrdOrder order = ordService.getOrderByOrderNo(orderNo);
            //生成协议pdf文件(暂时测试固定一个html后续慢慢处理)TODO
            Map<String, String> fillData = new HashMap<>();
            String amountStr = getAmountStr(order);
            fillData.put("amount1", amountStr); //金额阿拉伯表示
            String expressionInId =  moneyMap.get(amountStr);
            fillData.put("amount2", StringUtils.isEmpty(expressionInId) ? amountStr : expressionInId);//金额印尼文表示
            String borrowingTerm = order.getBorrowingTerm().toString();
            if ("3".equals(order.getOrderType())) {
                //分期
                fillData.put("borrowingTerm1", borrowingTerm+" bulan"); //期限
                fillData.put("borrowingTerm2", borrowingTerm+" months"); //期限
            }else{
                fillData.put("borrowingTerm1", borrowingTerm+" hari"); //期限
                fillData.put("borrowingTerm2", borrowingTerm+" days"); //期限
            }

            List<UsrAddressDetail> addressList  = userDetailService.getUserAddressList(userUuid, UsrAddressEnum.HOME);
            String liveCity = "";
            if(!CollectionUtils.isEmpty(addressList)){
                liveCity = addressList.get(0).getCity();
            }
            UsrUser user = usrService.getUserByUuid(userUuid);

            fillData.put("orderNo", orderNo);
            // hardcoded data
            String kuasaName = redisClient.get(RedisContants.DIGITAL_SIGN_KUASANAME);
            String lenderName = redisClient.get(RedisContants.DIGITAL_SIGN_LENDERNAME);
            String kuasaDom = redisClient.get(RedisContants.DIGITAL_SIGN_KUASADOM);
            String kuasaIDCard = redisClient.get(RedisContants.DIGITAL_SIGN_KUASAIDCARD);
            fillData.put("kuasaName", kuasaName);
            fillData.put("lenderName", lenderName);
            fillData.put("kuasaDom", kuasaDom);
            fillData.put("kuasaIDCard", kuasaIDCard);

            fillData.put("realName",user.getRealName());
            fillData.put("liveCity",liveCity);
            fillData.put("idCardNo",user.getIdCardNo());
            fillData.put("dayRate","0.64%");

            Date now = new Date();
            fillData.put("signDateEn",DateUtils.formatDateWithLocale(now,"dd MMM,yyyy",Locale.ENGLISH));//英文
            fillData.put("signDateID",DateUtils.formatDateWithLocale(now,"dd MMM,yyyy",IndonesiaLocale)) ; //签约时间印尼文

            String[] daysArray = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            fillData.put("signDayEn", daysArray[cal.get(Calendar.DAY_OF_WEEK)-1]);
            fillData.put("signDayId", daysArray[cal.get(Calendar.DAY_OF_WEEK)+6]);

            String html = PDFService.fillHtmlTemplate(fillData);
            return PDFService.html2Pdf(html).toByteArray();
        } catch (Exception e) {
            log.error("create contract pdf error for orderNo: " + orderNo, e);
        }
        return null;
    }

    // public static void test() {

    //     //生成协议pdf文件(暂时测试固定一个html后续慢慢处理)TODO
    //     Map<String, String> fillData = new HashMap<>();
    //     String amountStr = "1200000";
    //     fillData.put("amount1", amountStr); //金额阿拉伯表示
    //     String expressionInId =  moneyMap.get(amountStr);
    //     fillData.put("amount2", StringUtils.isEmpty(expressionInId) ? amountStr : expressionInId);//金额印尼文表示
    //     String borrowingTerm = "3";
    //     if ("3".equals("3")) {
    //         //分期
    //         fillData.put("borrowingTerm1", borrowingTerm+" bulan"); //期限
    //         fillData.put("borrowingTerm2", borrowingTerm+" months"); //期限
    //     }else{
    //         fillData.put("borrowingTerm1", borrowingTerm+" hari"); //期限
    //         fillData.put("borrowingTerm2", borrowingTerm+" days"); //期限
    //     }


    //     String liveCity = "ShangHai";

    //     fillData.put("realName","zengxiangcai");
    //     fillData.put("liveCity",liveCity);
    //     fillData.put("idCardNo","4202221980000111");
    //     fillData.put("dayRate","0.64%");

    //     Date now = new Date();
    //     fillData.put("signDateEn",DateUtils.formatDateWithLocale(now,"dd MMM,yyyy",Locale.ENGLISH));//英文
    //     fillData.put("signDateID",DateUtils.formatDateWithLocale(now,"dd MMM,yyyy",IndonesiaLocale)) ; //签约时间印尼文

    //     String html = null;
    //     try {
    //         html = PDFService.fillHtmlTemplate(fillData);
    //         File newPdf = PDFService.html2Pdf(html, "C:\\Users\\zxc20\\Desktop\\zxc_test.pdf");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }


    // }

    public final static Locale IndonesiaLocale = new Locale("id","ID");

    static Map<String, String> moneyMap = new HashMap<>();
    static {
        moneyMap.put("100000", "seratus ribu");
        moneyMap.put("160000","seratus enam puluh ribu");
        moneyMap.put("200000", "dua ratus ribu");
        moneyMap.put("300000","tiga ratus ribu");
        moneyMap.put("400000", "empat ratus ribu");
        moneyMap.put("600000", "enam ratus ribu");
        moneyMap.put("800000", "delapan ratus ribu");
        moneyMap.put("1000000", "satu juta");
        moneyMap.put("1200000", "satu juta dua ratus ribu");
        moneyMap.put("1500000", "satu juta lima ratus ribu");
        moneyMap.put("2000000", "dua juta");


        moneyMap.put("2400000", "dua juta empat ratus ribu");
        moneyMap.put("3000000", "tiga juta");
        moneyMap.put("3600000", "tiga juta enam ratus ribu");
        moneyMap.put("4000000", "empat juta");
        moneyMap.put("4500000", "empat juta lima ratus ribu");
        moneyMap.put("6000000", "enam juta");


    }

    private static String getAmountStr(OrdOrder order) {
        BigDecimal amount = order.getAmountApply();
        int indexOfPoint = amount.toPlainString().indexOf(".");
        String amountStr = amount.toPlainString().substring(0, indexOfPoint);
        return amountStr;
    }


}
