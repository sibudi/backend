package com.yqg.manage.service.check;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.order.ManOrderCheckRemarkDao;
import com.yqg.manage.dal.order.ManOrderCheckRuleDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.order.ManOrderRemarkDao;
import com.yqg.manage.dal.order.ManTeleReviewRecordDao;
import com.yqg.manage.dal.user.ManSecondCheckRecordDao;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.dal.user.ReviewerOrderTaskDAO;
import com.yqg.manage.dal.user.UserUserDao;
import com.yqg.manage.entity.check.ManOrderCheckRemark;
import com.yqg.manage.entity.check.ManOrderCheckRule;
import com.yqg.manage.entity.check.ManSecondCheckRecord;
import com.yqg.manage.entity.check.ManTeleReviewRecord;
import com.yqg.manage.entity.system.ManOrderRemark;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.EnumUtils;
import com.yqg.manage.enums.ManAutoReviewRuleEnum;
import com.yqg.manage.enums.ManOrderCheckRemarkEnum;
import com.yqg.manage.enums.ManOrderRemarkTypeEnum;
import com.yqg.manage.enums.ReviewerPostEnum;
import com.yqg.manage.scheduling.check.request.SaveTeleReviewRequest;
import com.yqg.manage.scheduling.check.request.TeleReviewRequest;
import com.yqg.manage.scheduling.check.request.TeleReviewResultRequest;
import com.yqg.manage.service.check.request.CheckRequest;
import com.yqg.manage.service.check.response.FirstCheckRemarkResponse;
import com.yqg.manage.service.check.response.ManOrderRemarkResponse;
import com.yqg.manage.service.check.response.ManSecondRecordResponse;
import com.yqg.manage.service.check.response.TeleReviewCheckResponse;
import com.yqg.manage.service.check.response.TeleReviewQuestionResponse;
import com.yqg.manage.service.mongo.OrderUserContactMongoService;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;
import com.yqg.manage.service.mongo.response.OrderEmergencyContactResponse;
import com.yqg.manage.service.order.ManOrderBlackService;
import com.yqg.manage.service.order.ManOrderHistoryService;
import com.yqg.manage.service.order.ManOrderOrderService;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.manage.service.user.response.ManSysLoginResponse;
import com.yqg.mongo.dao.OrderUserDataDal;
import com.yqg.mongo.entity.OrderUserDataMongo;
import com.yqg.order.entity.OrdHistory;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdCheckResultEnum;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.risk.service.AutoCallSendService;
import com.yqg.service.risk.service.RiskReviewService;
import com.yqg.service.signcontract.ContractSignService;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import com.yqg.service.task.AsyncTaskService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.service.util.RuleConstants;
import com.yqg.system.dao.SysAutoReviewRuleDao;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.UsrAddressDetailDao;
import com.yqg.user.dao.UsrAttachmentInfoDao;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.dao.UsrWorkDetailDao;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


/**
 * @author alan
 */
@Component
public class ManOrderCheckRemarkService {

    @Autowired
    private ManOrderOrderService manOrderOrderService;

    @Autowired
    private ManOrderCheckRuleService manOrderCheckRuleService;

    @Autowired
    private ManOrderHistoryService manOrderHistoryService;

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UserUserService userUserService;

    @Autowired
    private SmsServiceUtil smsServiceUtil;

    @Autowired
    private SysAutoReviewRuleDao sysAutoReviewRuleDao;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private ReviewerOrderTaskDAO reviewerOrderTaskDao;

    @Autowired
    private ManTeleReviewRecordDao manTeleReviewRecordDao;

    @Autowired
    private ManOrderRemarkDao manOrderRemarkDao;

    @Autowired
    private AutoCallSendService autoCallSendService;

    @Autowired
    private ManOrderBlackService manOrderBlackService;

    @Autowired
    private ManOrderCheckRemarkDao manOrderCheckRemarkDao;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private ManSecondCheckRecordDao manSecondCheckRecordDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private Cash2OrderService cash2OrderService;

    @Autowired
    private OrderUserDataDal orderUserDataDal;

    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;

    @Autowired
    private ManOrderCheckRuleDao manOrderCheckRuleDao;

    @Autowired
    private UserUserDao usrUserDao;

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;

    @Autowired
    private TeleCallResultDao teleCallResultDao;

    @Autowired
    private OrderUserContactMongoService orderUserContactMongoService;

    @Autowired
    private UserRiskService userRiskService;

    @Autowired
    private OrdService ordService;

    @Autowired
    private CheetahOrderService cheetahOrderService;
    @Autowired
    private ContractSignService contractSignService;
    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private RiskReviewService riskReviewService;

    private static Logger logger = LoggerFactory.getLogger(ManOrderCheckRemarkService.class);

    /**
     * 通过订单uuid查询审核备注记录*/
    @ReadDataSource
    public List<FirstCheckRemarkResponse> getCheckRemarkListByOrderNo(ManOrderListSearchResquest request) throws ServiceExceptionSpec {
        String orderNo = request.getUuid();
        if(StringUtils.isEmpty(orderNo)){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        List<ManOrderCheckRemark> result = this.orderCheckRemarkListByOrderNo(orderNo);
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }

        //封装返回数据
        List<FirstCheckRemarkResponse> rList = new ArrayList<>();
        for (ManOrderCheckRemark manOrderCheckRemark : result) {
            FirstCheckRemarkResponse response = new FirstCheckRemarkResponse();
            if (manOrderCheckRemark.getType() == 1) {
                if (manOrderCheckRemark.getCheckSuggest() > ManOrderCheckRemarkEnum.PASS_COMMONLY.getType()
                        && !manOrderCheckRemark.getCheckSuggest().equals(ManOrderCheckRemarkEnum.NOT_PASS_INFO_QUESTION.getType())) {
                    if (request.getLangue() == 1) {
                        response.setSuggest("建议拒绝," +
                                EnumUtils.valueOfManOrderCheckRemarkEnum(manOrderCheckRemark.getCheckSuggest()).getMessage());
                    } else {
                        response.setSuggest("lebih baik ditolak," +
                                EnumUtils.valueOfManOrderCheckRemarkEnum(manOrderCheckRemark.getCheckSuggest()).getMessageInn());
                    }
                } else if (manOrderCheckRemark.getCheckSuggest() > 0){
                    if (request.getLangue() == 1) {
                        response.setSuggest("建议通过," +
                                EnumUtils.valueOfManOrderCheckRemarkEnum(manOrderCheckRemark.getCheckSuggest()).getMessage());
                    } else {
                        response.setSuggest("Rekomendasi," +
                                EnumUtils.valueOfManOrderCheckRemarkEnum(manOrderCheckRemark.getCheckSuggest()).getMessageInn());
                    }

                }
            }
            response.setType(manOrderCheckRemark.getType());
            response.setRemark(manOrderCheckRemark.getRemark());
            rList.add(response);
        }
        return rList;
    }

    public List<ManOrderCheckRemark> orderCheckRemarkListByOrderNo(String orderNo) throws ServiceExceptionSpec {
        ManOrderCheckRemark search = new ManOrderCheckRemark();
        search.setOrderNo(orderNo);
        search.setDisabled(0);
        List<ManOrderCheckRemark> result = this.manOrderCheckRemarkDao.scan(search);
        return result;
    }

    /**
     * 订单初审
     * */
    @Transactional(rollbackFor = Exception.class)
    public void firstCheck(CheckRequest request) throws Exception {

        if( !this.redisClient.lockRepeat("manFirstCheck:" + request.getOrderNo())){
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }
        String orderNo = request.getOrderNo();
        String remark = request.getRemark();
        String sessionId = request.getSessionId();
        Integer type = request.getType();
        if(StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(sessionId)){
            logger.info("firstCheck 的orderNo:{}, sessionId:{}", orderNo, sessionId);
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        //验证用户和订单状态
        ManSysLoginResponse sysUserInfo = this.manUserService.getUserInfoBySession(sessionId);
        OrdOrder orderResult = this.manOrderOrderService.getOrderInfoByUuid(orderNo);
        if(orderResult.getStatus() != OrdStateEnum.FIRST_CHECK.getCode() || sysUserInfo == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        SysAutoReviewRule firstCheckA = getSysAutoReviewRule(ManAutoReviewRuleEnum.FIRST_CHECK_A.getMessage());
        SysAutoReviewRule firstCheckB = getSysAutoReviewRule(ManAutoReviewRuleEnum.FIRST_CHECK_B.getMessage());
        SysAutoReviewRule firstCheckC = getSysAutoReviewRule(ManAutoReviewRuleEnum.FIRST_CHECK_C.getMessage());


        //计算A型，C型，B型审核条命中件条数
        Integer typeACount = 0;
        Integer typeBCount = 0;
        Integer typeCCount = 0;
        //判断是否需要加入黑名单
        boolean isPutBlackList = false;
        String messageContent ;
        if (orderResult.getThirdType().equals(0)) {
            //短信发送文案   尊敬的用户
            messageContent = "[Do-It] Hai, Do-Iters! Silakan ajukan kembali pinjaman Anda dengan: ";
        } else {
            //【Do-It】您好，请您登陆 goo.gl/RLfJ8Z 下载Do-It官方App
            messageContent = "<Do-It> Unduh aplikasi resmi Do-It di goo.gl/RLfJ8Z. Untuk mendapatkan pinjaman, silakan: ";
        }
        //查询审核规则
        List<ManOrderCheckRule> checkRuleList
                = this.manOrderCheckRuleService.orderCheckRuleCountByNoResult(orderNo,1);
        Integer index = 1;
        Boolean hitSmsFlag = false;
        //命中部分C，需要修改订单步骤
        Integer orderStep = 9999;
        for(ManOrderCheckRule item : checkRuleList){
            //当有直接拉黑（禁用用户的）直接跳出
            if (item.getType() != null && item.getType().equals(1)) {
                isPutBlackList = true;
                break;
            }

            if(item.getRuleLevel() == 1){
                typeACount++;
            }
            if(item.getRuleLevel() == 2){
                typeBCount++;
            }
            if(item.getRuleLevel() == 3){
                typeCCount++;
            }
        }

        //修改订单状态，添加订单历史表记录
        OrdOrder editInfo = new OrdOrder();
        editInfo.setUuid(orderResult.getUuid());
        editInfo.setUpdateTime(new Date());
        editInfo.setFirstChecker(LoginSysUserInfoHolder.getLoginSysUserId());
        OrdHistory orderHistory = new OrdHistory();
        orderHistory.setUuid(UUIDGenerateUtil.uuid());
        orderHistory.setOrderId(orderNo);
        orderHistory.setCreateTime(new Date());
        orderHistory.setUpdateTime(new Date());
        orderHistory.setStatusChangeTime(new Date());
        orderHistory.setStatusChangePerson(sysUserInfo.getRealName());
        orderHistory.setUpdateUser(sysUserInfo.getId());
        orderHistory.setUserUuid(orderResult.getUserUuid());
        orderHistory.setProductUuid(orderResult.getProductUuid());

        //状态改成初审后等待外呼
        Integer orderState = OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode();

        if (isPutBlackList) {
            orderState = OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode();
            //加入ordBlackList表
            manOrderBlackService.addBackList(orderNo, "订单初审被直接拒绝，并且将用户置为无效！",
                    String.valueOf(firstCheckA.getRuleType()) + "-" + String.valueOf(firstCheckA.getRuleDetailType()),
                    null, orderResult.getUserUuid(), firstCheckA.getRuleValue(), firstCheckA.getRuleRejectDay());
            //将usrUser的status置为0（无效）
            userUserService.updateUsrStatusInvalid(orderResult.getUserUuid(), "订单初审不通过，用户置为无效！");

        } else {
            //命中A型即拒绝订单
            if(typeACount >= Integer.valueOf(firstCheckA.getRuleValue())){
                orderState = OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode();
                //加入ordBlackList表
                manOrderBlackService.addBackList(orderNo, "订单初审命中A，初审不通过！",
                        String.valueOf(firstCheckA.getRuleType()) + "-" + String.valueOf(firstCheckA.getRuleDetailType()),
                        String.valueOf(typeACount), orderResult.getUserUuid(), firstCheckA.getRuleValue(), firstCheckA.getRuleRejectDay());
                //命中B型两条拒绝订单
            } else if (typeBCount >= Integer.valueOf(firstCheckB.getRuleValue())) {
                orderState = OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode();
                //加入ordBlackList表
                manOrderBlackService.addBackList(orderNo, "订单初审命中B，初审不通过！",
                        String.valueOf(firstCheckB.getRuleType()) + "-" + String.valueOf(firstCheckB.getRuleDetailType()),
                        String.valueOf(typeBCount), orderResult.getUserUuid(), firstCheckB.getRuleValue(), firstCheckB.getRuleRejectDay());
            } //命中C型1条,取消订单
//            else if(typeCCount >= Integer.valueOf(firstCheckC.getRuleValue())){
//
//            }
            //初审直接手动拒绝
            else if (request.getUnPass() != null && request.getUnPass()) {
                orderState = OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode();
                //加入ordBlackList表（这里的文案给风控统计 暂时不修改了）
                manOrderBlackService.addBackList(orderNo, "初审手动拒绝！",
                        String.valueOf(firstCheckA.getRuleType()) + "-" + String.valueOf(firstCheckA.getRuleDetailType()),
                        null, orderResult.getUserUuid(), firstCheckA.getRuleValue(), firstCheckA.getRuleRejectDay());
            }
            else {
                //防止短信内容发送重复
                Boolean[] messageOrNots = new Boolean[] {false,false,false,false,false,false,false,false,false};
                //命中C型发送短信
                if(typeCCount >= Integer.valueOf(firstCheckC.getRuleValue())
                        && typeACount < Integer.valueOf(firstCheckA.getRuleValue())
                        && typeBCount < Integer.valueOf(firstCheckB.getRuleValue())){
                    //其他渠道直接拒绝 true 其他渠道
                    boolean flag = !orderResult.getThirdType().equals(0);
                    //当不为初审拒绝才进行重新传图片
                    for(ManOrderCheckRule item : checkRuleList){
                        //判断是否进行订单回退
                        if (item.getType() != null && item.getType().equals(2)) {
                            //3.身份证照片非原件（如复印件/翻拍照片/视频截图等）  请更换一张清晰的身份证原件的照片并重新提交
                            if (!messageOrNots[0]) {
                                if (flag) {
                                    //补充一张清晰的身份证原件照片重新提交信息即可获得贷款。
                                    messageContent = messageContent + index + ".foto ulang KTP asli anda.";
                                } else {
                                    messageContent = messageContent + index + ".Foto ulang KTP asli.";
                                }
                                index ++;
                            }
                            messageOrNots[0] = true;
                            hitSmsFlag = true;
                            if (!flag) {
                                if (orderStep > OrdStepTypeEnum.CHOOSE_ROLE.getType()) {
                                    orderStep = OrdStepTypeEnum.CHOOSE_ROLE.getType();
                                }
                                //删除认证和附件数据
                                this.deleteUsrCertificationInfo(orderResult.getUserUuid(), 1);
                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.ID_CARD.getType());
                            }

                        } else if (item.getType() != null && item.getType().equals(3)) {
                            //3.公司地址不详细
//                            if (!messageOrNots[1]) {
//                                if (flag) {
//                                    messageContent = messageContent + index + ".dan mengisi kembali alamat perusahaan untuk mendapatkan pinjaman. ";
//                                } else {
//                                    messageContent = messageContent + index + ".Silakan isi alamat jelas perusahaan (nomor jalan dan bloknya, dll.). ";
//                                }
//                                index ++;
//                            }
//                            messageOrNots[1] = true;
//                            hitSmsFlag = true;
//                            if (!flag) {
//                                if (orderStep > OrdStepTypeEnum.BASIC_INFO.getType()) {
//                                    orderStep = OrdStepTypeEnum.BASIC_INFO.getType();
//                                }
//                                this.deleteUsrAddressDetail(orderResult.getUserUuid(), 1);
//                            }
                        } else if (item.getType() != null && item.getType().equals(4)) {
                            //1.手持照片看不清人脸（非身份证）
                            if (!messageOrNots[2]) {
                                if (flag) {
                                    messageContent = messageContent + index + ".unggah ulang foto untuk mendapatkan pinjaman. ";
                                } else {
                                    messageContent = messageContent + index + ".Harap beri foto yang jelas dari foto anda yang beserta KTP Anda. ";
                                }
                                index ++;
                            }
                            messageOrNots[2] = true;
                            hitSmsFlag = true;
                            if (!flag) {
                                if (orderStep > OrdStepTypeEnum.CHOOSE_ROLE.getType()) {
                                    orderStep = OrdStepTypeEnum.CHOOSE_ROLE.getType();
                                }
                                //删除认证和附件数据
                                this.deleteUsrCertificationInfo(orderResult.getUserUuid(), 1);
                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.HAND_ID_CARD.getType());
                            }
                        } else if (item.getType() != null && item.getType().equals(5)) {
                            //	3.活体照片太模糊无法判断
//                            if (!messageOrNots[3]) {
//                                messageContent = messageContent + index + ".Silakan pilih tempat yang lebih terang,lakukan sertifikasi ulang pengenalan wajah. ";
//                                index ++;
//                            }
//                            messageOrNots[3] = true;
//                            hitSmsFlag = true;
//                            if (!flag) {
//                                if (orderStep > OrdStepTypeEnum.CHOOSE_ROLE.getType()) {
//                                    orderStep = OrdStepTypeEnum.CHOOSE_ROLE.getType();
//                                }
//                                //删除认证和附件数据
//                                this.deleteUsrCertificationInfo(orderResult.getUserUuid(), 2);
//                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.FACE.getType());
//                            }
                        } else if (item.getType() != null && item.getType().equals(6)) {
                            //3.看不清/看不到人脸
//                            if (!messageOrNots[4]) {
//                                messageContent = messageContent + index + ".Silakan coba pilih tempat yang lebih terang, rekam ulang video dan kirimkan. ";
//                                index ++;
//                            }
//                            messageOrNots[4] = true;
//                            hitSmsFlag = true;
//                            if (!flag) {
//                                if (orderStep > OrdStepTypeEnum.CONTACT_INFO.getType()) {
//                                    orderStep = OrdStepTypeEnum.CONTACT_INFO.getType();
//                                }
//                                //删除认证和附件数据
//                                this.deleteUsrCertificationInfo(orderResult.getUserUuid(), 3);
//                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.VIDEO.getType());
//                            }
                            //驾照
                        } else if (item.getType() != null && item.getType().equals(7)) {

                            if (!messageOrNots[5]) {
                                if (flag) {
                                    messageContent = messageContent + index + ".Unggah ulang foto SIM Anda.";
                                } else {
                                    messageContent = messageContent + index + ".Kirim ulang foto SIM.";
                                }
                                index ++;
                            }
                            messageOrNots[5] = true;
                            hitSmsFlag = true;
                            if (!flag) {
                                if (orderStep > OrdStepTypeEnum.CONTACT_INFO.getType()) {
                                    orderStep = OrdStepTypeEnum.CONTACT_INFO.getType();
                                }
                                //删除附件数据
                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.SIM.getType());
                            }
                            //护照
                        } else if (item.getType() != null && item.getType().equals(8)) {

                            if (!messageOrNots[6]) {
                                if (flag) {
                                    messageContent = messageContent + index + ".Kirim ulang foto paspor.";
                                } else {
                                    messageContent = messageContent + index + ".Kirim ulang foto paspor.";
                                }
                                index ++;
                            }
                            messageOrNots[6] = true;
                            hitSmsFlag = true;
                            if (!flag) {
                                if (orderStep > OrdStepTypeEnum.CONTACT_INFO.getType()) {
                                    orderStep = OrdStepTypeEnum.CONTACT_INFO.getType();
                                }
                                //删除附件数据
                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.NPWP.getType());
                            }
                            //保险卡
                        } else if (item.getType() != null && item.getType().equals(9)) {

//                            if (!messageOrNots[7]) {
//                                messageContent = messageContent + index + ".Harap melampirkan foto yang jelas dari kartu jaminan. ";
//                                index ++;
//                            }
//                            messageOrNots[7] = true;
//                            hitSmsFlag = true;
//                            if (!flag) {
//                                if (orderStep > OrdStepTypeEnum.CONTACT_INFO.getType()) {
//                                    orderStep = OrdStepTypeEnum.CONTACT_INFO.getType();
//                                }
//                                //删除附件数据
//                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.INSURANCE_CARD.getType());
//                            }
                            //家庭卡
                        } else if (item.getType() != null && item.getType().equals(10)) {

//                            if (!messageOrNots[8]) {
//                                messageContent = messageContent + index + ".Silahkan kirim ulang kartu keluarga yang jelas. ";
//                                index ++;
//                            }
//                            messageOrNots[8] = true;
//                            hitSmsFlag = true;
//                            if (!flag) {
//                                if (orderStep > OrdStepTypeEnum.CONTACT_INFO.getType()) {
//                                    orderStep = OrdStepTypeEnum.CONTACT_INFO.getType();
//                                }
//                                //删除附件数据
//                                this.deleteUsrAttachmentInfo(orderResult.getUserUuid(), UsrAttachmentEnum.KK.getType());
//                            }
                        }
                    }
                    //发送短信
                    if (!hitSmsFlag) {
                        messageContent = "<Do-It> Maaf, pinjaman Anda gagal karena data tidak lengkap. Terima kasih atas " +
                                "permohonan Anda, kami menantikan kesempatan untuk melayani Anda di kemudian hari.";
                        orderState = OrdStateEnum.CANCEL.getCode();
                        //加入ordBlackList表
                        manOrderBlackService.addBackList(orderNo, "订单初审命中C，取消订单！",
                                String.valueOf(firstCheckC.getRuleType()) + "-" + String.valueOf(firstCheckC.getRuleDetailType()),
                                String.valueOf(typeCCount), orderResult.getUserUuid(), firstCheckC.getRuleValue(), firstCheckC.getRuleRejectDay());
                    } else {
                        if (!flag) {
                            //命中部分C类，进行订单回填
                            orderState = OrdStateEnum.SUBMITTING.getCode();
                            //删除之前归档数据
                            this.usrBaseInfoService.deleteOrderInfoToMango(editInfo.getUuid());
                            //删除之前一次初审的数据
                            this.manOrderCheckRuleDao.deleteManOrderCheckRule(editInfo.getUuid());
                            //其他渠道直接拒绝
                        } else {
                            orderState = OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode();
                            //加入ordBlackList表
                            manOrderBlackService.addBackList(orderNo, "cashCash渠道命中图片等破损，直接拒绝！",
                                "just flag",
                                "0", orderResult.getUserUuid(), "0", firstCheckA.getRuleRejectDay());
                        }
                    }

                    logger.info("初审取消发送短信内容:" + messageContent);
                    ManUserUserRequest userRequest = new ManUserUserRequest();
                    userRequest.setUserUuid(orderResult.getUserUuid());
                    UsrUser userUser = this.userUserService.userMobileByUuid(userRequest);
                    if(userUser != null){
                        logger.info("send SMS checkFirst");
                        this.smsServiceUtil.sendTypeSmsCode("NOTICE","62" + userUser.getMobileNumberDES(),
                                messageContent);
                    }
                }
            }

        }

        if(orderState == OrdStateEnum.CANCEL.getCode()
                || orderState == OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode()){

            // 如果是cashcash的订单 反馈更新订单状态
            if (orderResult.getThirdType() == 1){
                if (orderState == OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode()){
                    // 如果是cashcash的订单 反馈更新订单状态 和 审核状态
                    this.cash2OrderService.ordStatusFeedback(orderResult, Cash2OrdStatusEnum.NOT_PASS_CHECK);
                    this.cash2OrderService.ordCheckResultFeedback(orderResult, Cash2OrdCheckResultEnum.CHECK_NOT_PASS);

                }else {
                    // 如果是cashcash的订单 反馈更新订单状态 和 审核状态
                    this.cash2OrderService.ordStatusFeedback(orderResult, Cash2OrdStatusEnum.CANCLE);
                    this.cash2OrderService.ordCheckResultFeedback(orderResult, Cash2OrdCheckResultEnum.CHECK_NOT_PASS);
                }
            }else if (orderResult.getThirdType() == 2) {
                    // 猎豹金融订单 cheetah
                if (orderState == OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode()){
                    this.cheetahOrderService.ordStatusFeedback(orderResult, CheetahOrdStatusEnum.NOT_PASS_CHECK);
                }else {
                    this.cheetahOrderService.ordStatusFeedback(orderResult, CheetahOrdStatusEnum.CANCLE);
                }
            }

            this.saveOrderInfoToMongo(orderResult);
            //判断是否触发女性免电核规则
        } else if (orderState == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()){
            //归档
            this.saveOrderInfoToMongo(orderResult);
        } else if (orderState == OrdStateEnum.SUBMITTING.getCode()) {
            editInfo.setOrderStep(orderStep);//新加订单回退步骤

        }

        editInfo.setStatus(orderState);
        orderHistory.setStatus(orderState);
        this.manOrderOrderService.editOrder(editInfo);
        this.manOrderHistoryService.addOrderHistory(orderHistory);

        //添加初审备注
        this.addOrderCheckRemark(type, 1, remark,orderNo,sysUserInfo.getId(), request.getBurningTime());

        //更新订单审核完成
        reviewerOrderTaskDao.updateFinishStatus(orderNo, ReviewerPostEnum.JUNIOR_REVIEWER.name());

        if (orderState == OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()) {
            //初审成功后，调用方法进行外呼
            autoCallSendService.sendOwnerCall(orderResult);
        }
        this.redisClient.unlockRepeat("manFirstCheck:" + request.getOrderNo());
    }

    /**
     * 订单复审
     * @param request
     * @throws ServiceExceptionSpec
     */
    @Transactional(rollbackFor = Exception.class)
    public void secondCheck(CheckRequest request) throws Exception {

        String orderNo = request.getOrderNo();
        boolean orderWaitDigiSign =  asyncTaskService.existsDigiSignRecord(orderNo);
        //签约异步锁
        if (orderWaitDigiSign) {
            logger.info("order {} secondCheck is lock.", orderNo);
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }
        //复审方法锁
        if( !this.redisClient.lockRepeat(RedisContants.SECOND_CHECK_LOCK + orderNo)){
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }

        //参数判断
        String sessionId = request.getSessionId();
        String remark = request.getRemark();
        Integer pass = request.getPass();
        if(StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(sessionId) || pass == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        OrdOrder orderResult = this.manOrderOrderService.getOrderInfoByUuid(orderNo);
        if(orderResult.getStatus() != OrdStateEnum.SECOND_CHECK.getCode()){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        //封装相关订单和历史数据
        GetOrderAndHistory getOrderAndHistory = new GetOrderAndHistory(orderNo, sessionId, orderResult).invoke();
        OrdOrder editInfo = getOrderAndHistory.getEditInfo();
        OrdHistory orderHistory = getOrderAndHistory.getOrderHistory();
        ManSysLoginResponse sysUserInfo = getOrderAndHistory.getSysUserInfo();

        Integer orderState = OrdStateEnum.LOANING.getCode();
        //电核通过
        if(pass == 0){
            //黑名单用户拒绝进件
            UsrUser usrUser = userUserService.userInfoByUuid(orderResult.getUserUuid());
            if (usrUser != null && usrUser.getStatus() == 0) {
                orderState = OrdStateEnum.SECOND_CHECK_NOT_ALLOW.getCode();
            }
        }
        //电核拒绝
        if(pass == 1){
            orderState = OrdStateEnum.SECOND_CHECK_NOT_ALLOW.getCode();
        }

        //拒绝加入黑名单
        if (orderState == OrdStateEnum.SECOND_CHECK_NOT_ALLOW.getCode()) {
            orderState = secondCheckAbTest(orderNo, orderResult, orderState);

        }
        logger.info("secondChecker is :{}, orderNo is {}， status is {}." ,
                editInfo.getSecondChecker(), request.getOrderNo(), orderState);
        orderHistory.setStatus(orderState);

        boolean hitAdvanceCheck = false;
        if (orderState == OrdStateEnum.LOANING.getCode()) {
            //放款前先走advance黑名单验证
            hitAdvanceCheck = riskReviewService.hitBlackListOrMultiPlatform(orderResult);
            if (hitAdvanceCheck) {
                orderState = OrdStateEnum.MACHINE_CHECK_NOT_ALLOW.getCode();
            }
        }

        // budi:
        //boolean isDigiSign = false;
        if(hitAdvanceCheck){
            //拒绝
            logger.info("hit advance reject, orderNo: {}",orderNo);
        }else{
            //进入放款的订单先走签约
            // budi
            //boolean toDigiSignSwitchOpen = contractSignService.isDigitalSignSwitchOpen(orderResult);
            //if (toDigiSignSwitchOpen && orderState == OrdStateEnum.LOANING.getCode()) {
            //    isDigiSign = true;
            //    asyncTaskService.addTask(orderResult, AsyncTaskInfoEntity.TaskTypeEnum.CONTRACT_SIGN_TASK);
            //}
        }
        editInfo.setStatus(orderState);
        //如果进入签约就不改状态
        // budi:
        /*if (isDigiSign) {
            logger.info("this sc order not change status.");
            editInfo.setStatus(null);
        } else {
            this.manOrderHistoryService.addOrderHistory(orderHistory);
        }*/
        //如果是签约的 主要就更新secondChecker字段
        this.manOrderOrderService.editOrder(editInfo);
        if (orderState == OrdStateEnum.LOANING.getCode()) {
            // budi: remark sms
            //sendReviewPassMsg(orderResult.getUserUuid());
        }
        //成功则推送第三方数据
        if (orderState == OrdStateEnum.LOANING.getCode() || orderState == OrdStateEnum.MACHINE_CHECK_NOT_ALLOW.getCode()
                || orderState == OrdStateEnum.SECOND_CHECK_NOT_ALLOW.getCode()) {
            boolean reviewPass = orderState == OrdStateEnum.LOANING.getCode();
            // 如果是cashcash的订单 反馈更新订单状态
            if (orderResult.getThirdType() == 1) {
                // 如果是cashcash的订单 反馈更新订单状态 和 审核状态
                this.cash2OrderService.ordStatusFeedback(orderResult, reviewPass ? Cash2OrdStatusEnum.PASS_CHECK : Cash2OrdStatusEnum.NOT_PASS_CHECK);
                this.cash2OrderService.ordCheckResultFeedback(orderResult, reviewPass ? Cash2OrdCheckResultEnum.CHECK_PASS : Cash2OrdCheckResultEnum.CHECK_NOT_PASS);
            } else if (orderResult.getThirdType() == 2) {
                // 猎豹金融 cheetah
                this.cheetahOrderService.ordStatusFeedback(orderResult, reviewPass ? CheetahOrdStatusEnum.PASS_CHECK : CheetahOrdStatusEnum.NOT_PASS_CHECK);
            }
        }

        //复审点击提交可能会没有备注，但是需要记录审核时长
        this.addOrderCheckRemark(0, 2,remark,orderNo,sysUserInfo.getId(), request.getBurningTime());
        //订单归档
        this.saveOrderInfoToMongo(orderResult);
        //更新订单审核完成
        reviewerOrderTaskDao.updateFinishStatus(orderNo, ReviewerPostEnum.SENIOR_REVIEWER.name());

        this.redisClient.unlockRepeat(RedisContants.SECOND_CHECK_LOCK + orderNo);

    }

    private Integer secondCheckAbTest(String orderNo, OrdOrder orderResult, Integer orderState) throws Exception {
        SysAutoReviewRule secondCheck = getSysAutoReviewRule(ManAutoReviewRuleEnum.SECOND_CHECK.getMessage());
        //给风控统计，加上拒绝原因
        String description = getRejectReason(orderNo);
        Boolean flag = StringUtils.isNotBlank(description) && (description.contains("多次被催收且用户借款不还")
                || description.contains("不认识") || description.contains("从未在该公司工作"));
        //如果不是下面这些规则 并且不是cashCash的 走100产品逻辑
        if (!flag && userRiskService.isSuitableFor100RMBProduct(orderResult)) {
            orderState = OrdStateEnum.MACHINE_CHECKING.getCode();
            ordService.changeOrderTo100RMBProductfixBug(orderResult);
            manOrderBlackService.addBackListRemark(orderNo, RuleConstants.PRODUCT600TO150_SENIOR_REVIEW,
                    orderResult.getUserUuid());
        } else {
            //加入ordBlackList表
            manOrderBlackService.addBackList(orderNo, "订单复审拒绝!" + description,
                    String.valueOf(secondCheck.getRuleType()) + "-" + String.valueOf(secondCheck.getRuleDetailType()),
                    null, orderResult.getUserUuid(), secondCheck.getRuleValue(), secondCheck.getRuleRejectDay());
        }
        return orderState;
    }

    private void sendReviewPassMsg(String userUuid) {
        try {
            //您的申请已通过审核，即将放款，请注意查收！登录www，给予五星评价，您的下一次贷款将是免审核的。祝您生活愉快！
            String content = "<Do-It>Anda lulus verifikasi,perhatikan status permohonan anda.kunjungi goo.gl/RLfJ8Z .beri 5 bintang, pinjaman " +
                    "selanjutnya bebas verif Thx.";
            //发送短信
            ManUserUserRequest userRequest = new ManUserUserRequest();
            userRequest.setUserUuid(userUuid);
            UsrUser userUser = this.userUserService.userMobileByUuid(userRequest);
            if (!StringUtils.isEmpty(userUser.getMobileNumberDES())) {
                // budi: remark sms
                //this.smsServiceUtil.sendTypeSmsCode("NOTICE", "62" + userUser.getMobileNumberDES(), content);
            }
        } catch (Exception e) {
            logger.error("发送提醒短信异常, userUuid= " + userUuid, e);
            e.printStackTrace();
        }
    }

    private String getRejectReason(String orderNo) {
        List<ManOrderRemark> lists = manOrderRemarkDao.getManOrderRemark(orderNo);

        if (CollectionUtils.isEmpty(lists)) {
            return "";
        }
        return lists.get(0).getDescription();

    }
    /**
     * 订单归档*/
    public void saveOrderInfoToMongo(OrdOrder order){
        SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
        saveMongo.setOrderNo(order.getUuid());
        saveMongo.setUserUuid(order.getUserUuid());
        this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveMongo);
    }
    /**
     * 手动订单归档
     * */
    public Integer saveOrderInfoToMongoByHand(OrdOrder order) throws Exception{

        if (order.getUuid() == null) {
            return 0;
        }
        List<OrdOrder> lists = manOrderOrderDao.scan(order);
        if (CollectionUtils.isEmpty(lists)) {
            return 0;
        }

        OrderUserDataMongo search = new OrderUserDataMongo();
        search.setOrderNo(order.getUuid());
        //紧急联系人
        search.setInfoType(5);
        search.setStatus(1);
        List<OrderUserDataMongo> result = this.orderUserDataDal.find(search);
        if(!CollectionUtils.isEmpty(result)){
            logger.info("订单" + order.getUuid() + "已被归档！");
            return 0;
        }

        usrBaseInfoService.savaOrderInfoToMango(lists.get(0).getUserUuid(), order.getUuid());
        logger.info("订单" + order.getUuid() + "归档成功！");
        return 1;
    }
//
//    /**
//     *  ???? ??????
//     * */
//    public void sendOrderInfoTo(OrdOrder order) {
//
//        logger.info("???????"+order.getUuid());
//
//        UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
//
//        try {
//            SendOrderInfoResponse response = p2PService.sendOrderInfoToFinancial(order,user);
//            if (response.getCode().equals("1")){
//                // ????
//                OrdOrder entity = new OrdOrder();
//                entity.setUuid(order.getUuid());
//                entity.setUpdateTime(new Date());
//                entity.setMarkStatus("1");
//                entity.setRemark("");
//                this.ordService.updateOrder(entity);
//            }else {
//                // ????
//                OrdOrder entity = new OrdOrder();
//                entity.setUuid(order.getUuid());
//                entity.setUpdateTime(new Date());
//                entity.setMarkStatus("2");
//                entity.setRemark("????");
//                this.ordService.updateOrder(entity);
//            }
//
//        }catch (Exception e){
//            // ????
//            OrdOrder entity = new OrdOrder();
//            entity.setUuid(order.getUuid());
//            entity.setUpdateTime(new Date());
//            entity.setMarkStatus("2");
//            entity.setRemark("????");
//            this.ordService.updateOrder(entity);
//
//            logger.error("????????"+order.getUuid(),e);
//
//        }
//
//    }

    /**
     * 添加订单审核备注*/
    public void addOrderCheckRemark(Integer checkSuggest, Integer type,String remark,
                                    String orderNo,Integer updateUser, String burningTime) throws ServiceExceptionSpec {
        ManOrderCheckRemark checkRemark = new ManOrderCheckRemark();
        checkRemark.setOrderNo(orderNo);
        checkRemark.setType(type);
        checkRemark.setUuid(UUIDGenerateUtil.uuid());
        checkRemark.setUpdateUser(updateUser);
        checkRemark.setRemark(remark);
        checkRemark.setCheckSuggest(checkSuggest);
        checkRemark.setBurningTime(burningTime);
        this.manOrderCheckRemarkDao.insert(checkRemark);
    }

    /**
     * 根据规则类型获得规则阈值
     * @param ruleDetailType
     * @return
     */
    public SysAutoReviewRule getSysAutoReviewRule(String ruleDetailType) {
        SysAutoReviewRule sysAutoReviewRule = new SysAutoReviewRule();
        sysAutoReviewRule.setDisabled(0);
        sysAutoReviewRule.setRuleStatus(1);
        sysAutoReviewRule.setRuleDetailType(ruleDetailType);
        List<SysAutoReviewRule> rules = sysAutoReviewRuleDao.scan(sysAutoReviewRule);

        if (!CollectionUtils.isEmpty(rules)) {
            return rules.get(0);
        }
        return null;
    }

    /**
     * 获得黑名单的code值
     * @return
     * @throws Exception
     */
    public List<String> getBlackList() throws Exception {
        //从字典表中获得需要加入黑名单的code
        DictionaryRequest dictionaryRequest = new DictionaryRequest();
        dictionaryRequest.setDicCode("MANSYSBLACK");
        List<SysDicItemModel> sysDicItemModelList = sysDicService.dicItemListByDicCode(dictionaryRequest);
        if (CollectionUtils.isEmpty(sysDicItemModelList)) {
            return new ArrayList<>();
        }

        List<String> rList = new ArrayList<>();
        for (SysDicItemModel sysDicItemModel : sysDicItemModelList) {
            rList.add(sysDicItemModel.getDicItemValue());
        }
        return rList;
    }

    /**
     * 查询禁用哪个电核对象
     * @param teleReviewRequest
     * @return
     * @throws ServiceExceptionSpec
     */
    public String getTeleReviewObj(TeleReviewRequest teleReviewRequest)
            throws Exception {
        if (StringUtils.isEmpty(teleReviewRequest.getUuid())||
                StringUtils.isEmpty(teleReviewRequest.getOrderNo())||
                teleReviewRequest.getTeleReviewType() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        List<ManOrderRemarkResponse> remarks = getTeleReviewRemark(teleReviewRequest);

        //查询字典，判断是否超过稍后再拨次数
        DictionaryRequest dictionaryRequest = new DictionaryRequest();
        dictionaryRequest.setDicCode("TELEREVIEW_COUNT");
        Integer companyTotalCount = 0, firstTotalCount = 0, secondTotalCount = 0;
        try {
            List<SysDicItemModel> dicItems = sysDicService.dicItemListByDicCode(dictionaryRequest);
            for (SysDicItemModel dicItem : dicItems) {
                if ("COMPANY_COUNT".equals(dicItem.getDicItemName())) {
                    companyTotalCount = Integer.valueOf(dicItem.getDicItemValue());
                } else if ("FIRST_CONTACT_COUNT".equals(dicItem.getDicItemName())) {
                    firstTotalCount = Integer.valueOf(dicItem.getDicItemValue());
                } else if ("SECOND_CONTACT_COUNT".equals(dicItem.getDicItemName())) {
                    secondTotalCount = Integer.valueOf(dicItem.getDicItemValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        String pass = getPassStr(remarks, teleReviewRequest.getUuid(), companyTotalCount, firstTotalCount, secondTotalCount);

        //如果公司没有禁用 判断公司和本人电话是否相同
        if (!(StringUtils.isNotBlank(pass) && pass.contains("1"))) {

            if (companyAndPersionNum(teleReviewRequest.getUuid())) {
                pass += "1,";
            }
        }
        return pass;
    }

    /**
     * 返回需要禁用的对象字符
     * @param remarks
     * @return
     */
    private String getPassStr(List<ManOrderRemarkResponse> remarks, String userUuid, Integer companyTotalCount,
                              Integer firstTotalCount, Integer secondTotalCount) {

        //查询用户角色
        Integer userRole = this.getUserRole(userUuid);
        //查询用户性别
        Integer userSex = this.getUserSex(userUuid);
        String result = "";
        Integer companyCount = 0, firstCount = 0, secondCount = 0;
        for(ManOrderRemarkResponse orderRemark : remarks) {

            String tempStr = "";
            if (orderRemark.getTeleReviewOperationType() == null) {
                if (orderRemark.getTeleReviewObject() != null &&
                        orderRemark.getTeleReviewObject().equals("1")) {
                    if (judgeCallOrNot(orderRemark.getTeleReviewResult())) {
                        tempStr = "1";
                        result += tempStr + ",";
                    }

                }
                continue;
            }
            //公司未接通
            if (orderRemark.getTeleReviewOperationType().equals(1)) {
                //判断是否进行稍后再拨
                if (orderRemark.getTeleReviewResultType().equals(2)) {
                    companyCount ++;
                    if (companyCount < companyTotalCount) {
                        continue;
                    }
                }
                tempStr = "1";
                //公司接通
            } else if (orderRemark.getTeleReviewOperationType().equals(2)) {
                ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
                reviewRecord.setManOrderRemarkId(orderRemark.getId());
                reviewRecord.setDisabled(0);
                reviewRecord.set_orderBy("remark");
                List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
                if (CollectionUtils.isEmpty(recordList) || recordList.size() != 3) {
                    continue;
                }
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(1)) {
                    continue;
                }
                boolean flag = false;
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(3)) {
                    flag = true;
                }
                if (recordList.get(0).getAnswer().equals("2")) {
                    flag = true;
                }
                if (recordList.get(2).getAnswer().equals("2")) {
                    flag = true;
                }

                if (flag) {
                    return "1,2,3";
                }
                //直接通过
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(5)
                        || orderRemark.getTeleReviewResultType().equals(6)
                        || orderRemark.getTeleReviewResultType().equals(7)) {
                    return "1,2,3";
                }
                tempStr = "1";
                //第一联系人未接通
            } else if (orderRemark.getTeleReviewOperationType().equals(3)) {
                if (orderRemark.getTeleReviewResultType().equals(2)
                        || orderRemark.getTeleReviewResultType().equals(4)
                        || orderRemark.getTeleReviewResultType().equals(5)) {
                    firstCount ++;
                    if (firstCount < firstTotalCount) {
                        continue;
                    }
                }
                tempStr = "2";
                //第一联系人接通
            } else if (orderRemark.getTeleReviewOperationType().equals(4)) {
                ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
                reviewRecord.setManOrderRemarkId(orderRemark.getId());
                reviewRecord.setDisabled(0);
                reviewRecord.set_orderBy("remark");
                List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
                if (CollectionUtils.isEmpty(recordList)) {
                    continue;
                }
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(1)) {
                    continue;
                }
                boolean flag = false;
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(3)) {
                    flag = true;
                }

                //回答朋友
                if (recordList.get(0).getAnswer().equals("5")) {
                    if (this.secondCompanyTeleCallAndSex(remarks, 1)) {
                        flag = true;
                    }
                }
                //家庭主妇角色
                if (userRole.equals(3)) {
                    if (recordList.get(0).getAnswer().equals("1")) {
                        flag = true;
                    }
                    if (recordList.get(4).getAnswer().equals("3")) {
                        flag = true;
                    }
                } else {
                    if (recordList.get(3).getAnswer().equals("3")) {
                        flag = true;
                    }
                    if (recordList.get(1).getAnswer().equals("2")) {
                        flag = true;
                    }
                    //回答同事
                    if (recordList.get(0).getAnswer().equals("6")) {
                        if (this.secondCompanyTeleCallAndSex(remarks, null)) {
                            flag = true;
                        }
                    }
                }

                if (flag) {
                    return "1,2,3";
                }
                //直接通过
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(4)) {
                    return "1,2,3";
                }
                if (userRole.equals(3)) {
                    if (recordList.size() > 5 && recordList.get(5).getAnswer().equals("1")) {
                        return "1,2,3";
                    }
                } else {
                    if (recordList.size() > 4 && recordList.get(4).getAnswer().equals("1")) {
                        return "1,2,3";
                    }
                }
                tempStr = "2";
                //第二联系人未接通
            } else if (orderRemark.getTeleReviewOperationType().equals(5)) {
                if (orderRemark.getTeleReviewResultType().equals(2)
                        || orderRemark.getTeleReviewResultType().equals(4)
                        || orderRemark.getTeleReviewResultType().equals(5)) {
                    secondCount ++;
                    if (secondCount < secondTotalCount) {
                        continue;
                    }
                }
                tempStr = "3";

                //第二联系人接通
            } else if (orderRemark.getTeleReviewOperationType().equals(6)) {
                ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
                reviewRecord.setManOrderRemarkId(orderRemark.getId());
                reviewRecord.setDisabled(0);
                reviewRecord.set_orderBy("remark");
                List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
                if (CollectionUtils.isEmpty(recordList)) {
                    continue;
                }
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(1)) {
                    continue;
                }
                boolean flag = false;
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(3)) {
                    flag = true;
                }
                //回答朋友
                if (recordList.get(0).getAnswer().equals("5")) {
                    if (this.secondCompanyTeleCallAndSex(remarks, 1)) {
                        flag = true;
                    }
                }
                if (userRole.equals(3)) {
                    if (recordList.get(4).getAnswer().equals("3")) {
                        flag = true;
                    }
                } else {
                    if (recordList.get(3).getAnswer().equals("3")) {
                        flag = true;
                    }
                    if (recordList.get(1).getAnswer().equals("2")) {
                        flag = true;
                    }
                    //回答同事
                    if (recordList.get(0).getAnswer().equals("6")) {
                        if (this.secondCompanyTeleCallAndSex(remarks, null)) {
                            flag = true;
                        }
                    }
                }

                if (flag) {
                    return "1,2,3";
                }
                //直接通过
                if (orderRemark.getTeleReviewResultType() != null &&
                        orderRemark.getTeleReviewResultType().equals(4)) {
                    return "1,2,3";
                }
                if (userRole.equals(3)) {
                    if (recordList.size() > 5 && recordList.get(5).getAnswer().equals("1")) {
                        return "1,2,3";
                    }
                } else {
                    if (recordList.size() > 4 && recordList.get(4).getAnswer().equals("1")) {
                        return "1,2,3";
                    }
                }
                tempStr = "3";
            }
            if (StringUtils.isNotBlank(tempStr)) {
                result += tempStr + ",";
            }
        }
        return result;
    }


    private Boolean companyAndPersionNum(String userUuid) {
        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setStatus(1);
        usrUser.setUuid(userUuid);
        List<UsrUser> usrUsers = usrUserDao.scan(usrUser);
        if (CollectionUtils.isEmpty(usrUsers)) {
            return false;
        }
        String selfPhone = DESUtils.decrypt(usrUsers.get(0).getMobileNumberDES());
        if (StringUtils.isEmpty(selfPhone)) {
            return false;
        }

        //获取公司电话
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setDisabled(0);
        usrWorkDetail.setUserUuid(userUuid);
        List<UsrWorkDetail> details =  usrWorkDetailDao.scan(usrWorkDetail);
        if (CollectionUtils.isEmpty(details)) {
            return false;
        }
        String compPhone = details.get(0).getCompanyPhone();
        if (StringUtils.isEmpty(compPhone)) {
            return false;
        }

        selfPhone = selfPhone.substring(selfPhone.length() - 4);
        compPhone = compPhone.substring(compPhone.length() - 4);

        if (StringUtils.isNotBlank(selfPhone) && StringUtils.isNotBlank(compPhone)
                && selfPhone.equals(compPhone)) {
            return true;
        }
        return false;
    }

    private boolean judgeCallOrNot(String callResult) {
        if (StringUtils.isEmpty(callResult)) {
            return false;
        }
        if (callResult.equals("号码不存在") || callResult.equals("nomor tidak ada")
                || callResult.equals("号码无效") || callResult.equals("Nomor tersebut tidak valid")
                || callResult.equals("已欠费") || callResult.equals("Tunggakan")
                || callResult.equals("接通-拒绝") || callResult.equals("Terangkat-Tolak")) {
            return true;
        }
        return false;
    }

    /**
     * true 表示直接拒绝
     * @param remarks
     * @param sex
     * @return
     */
    private boolean secondCompanyTeleCallAndSex(List<ManOrderRemarkResponse> remarks, Integer sex) {

        if (CollectionUtils.isEmpty(remarks)) {
            return false;
        }
        //公司外呼结果返回“可能有效”或“无效”
        Optional<ManOrderRemarkResponse> manOrderRemarkResponse = remarks.stream().filter(elem -> ("1".equals(elem
                .getTeleReviewObject()) && (elem.getCallResultType() == 3 || elem.getCallResultType() == 2))).findFirst();
        if (sex != null) {
            if (1 == sex && manOrderRemarkResponse.isPresent()) {
                return true;
            }
        } else {
            if (manOrderRemarkResponse.isPresent()) {
                return true;
            }
        }
        return false;

    }
    /**
     * 根据用户id查询题目及答案
     * @param teleReviewRequest
     * @return
     */
    public TeleReviewCheckResponse getTeleReviewQuestion(TeleReviewRequest teleReviewRequest)
            throws Exception {

        if (StringUtils.isEmpty(teleReviewRequest.getUuid())||
                StringUtils.isEmpty(teleReviewRequest.getOrderNo())||
                teleReviewRequest.getType() == null ||
                teleReviewRequest.getLangue() == null||
                teleReviewRequest.getTeleReviewType() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        //处理公司审核
        if (teleReviewRequest.getTeleReviewType() == ManOrderRemarkTypeEnum.COMPANY_TELE_REVIEW.getType()) {
            TeleReviewCheckResponse result = new TeleReviewCheckResponse();
            List<ManOrderRemarkResponse> remarks = getTeleReviewRemark(teleReviewRequest);
            result.setRemarks(remarks);
            //封装公司审核结果
            setCompanyTeleReviewResult(remarks, result, teleReviewRequest.getUuid());
            return result;
        }
        //判断本人电核
        TeleReviewCheckResponse result = new TeleReviewCheckResponse();

        //获得所有题库的id号
//        List<Integer> ids = manTeleReviewDao.getTeleReviewQuestionId(teleReviewRequest.getType(), teleReviewRequest.getLangue());
//        Integer idSize = ids.size();
//
//        //随机获得其中三条问题
//        List<Integer> showQuestionIds = new ArrayList<>();
//        for (int index = 0; index<3; ++index) {
//            Random random = new Random();
//            Integer id ;
//            do {
//                id = random.nextInt(idSize);
//            } while (showQuestionIds.contains(ids.get(id)));
//            showQuestionIds.add(ids.get(id));
//        }
//        List<ManTeleReviewQuestion> questions
//                = manTeleReviewDao.getTeleReviewQuestion(showQuestionIds, teleReviewRequest.getLangue());
//
//        if (CollectionUtils.isEmpty(questions)) {
//            return new TeleReviewCheckResponse();
//        }
//
//        //调用反射封装数据
//        List<TeleReviewQuestionResponse> rList = new ArrayList<>();
//        for (ManTeleReviewQuestion manTeleReview : questions) {
//            String classAndMethodName = manTeleReview.getAnswer();
//            String answer = phoneReviewAnswer.getPhoneReviewAnswer(classAndMethodName, teleReviewRequest);
//            TeleReviewQuestionResponse response = new TeleReviewQuestionResponse();
//            response.setQuestion(manTeleReview.getQuestion());
//            response.setAnswer(answer);
//            rList.add(response);
//        }
        //查询电话审核日志
        List<ManOrderRemarkResponse> remarks = getTeleReviewRemark(teleReviewRequest);
        result.setRemarks(remarks);
        //封装公司审核结果
        setSelfTeleReviewResult(remarks, result);

//        for (ManOrderRemark remark : remarks) {
//            //这里类型为3表示拒绝
//            if (remark.getOperationType() == OperationTypeEnum.CANNOT_BE_AUDITED.getType()) {
//                result.setPass(1);
//                break;
//            }
//        }

//        result.setQuestions(rList);

        return result;
    }

    /**
     * 查询电话审核日志
     * @return
     */
    public List<ManOrderRemarkResponse> getTeleReviewRemark(TeleReviewRequest teleReviewRequest) throws Exception {

        if (StringUtils.isEmpty(teleReviewRequest.getOrderNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        List<ManOrderRemarkResponse> responses = new ArrayList<>();
        List<ManOrderRemark> rList = null;
        if (teleReviewRequest.getTeleReviewType() == ManOrderRemarkTypeEnum.COMPANY_TELE_REVIEW.getType()) {
            responses.addAll(this.getTeleCallResultList(teleReviewRequest.getUuid(),
                    teleReviewRequest.getOrderNo(), ManOrderRemarkTypeEnum.COMPANY_TELE_REVIEW.getType(), teleReviewRequest.getLangue()));
            rList = manOrderRemarkDao.
                    getManOrderRemarkByOrderNo(ManOrderRemarkTypeEnum.COMPANY_TELE_REVIEW.getType(), teleReviewRequest.getOrderNo());
        } else if (teleReviewRequest.getTeleReviewType() == ManOrderRemarkTypeEnum.TELE_REVIEW.getType()) {
            responses.addAll(this.getTeleCallResultList(teleReviewRequest.getUuid(),
                    teleReviewRequest.getOrderNo(), ManOrderRemarkTypeEnum.TELE_REVIEW.getType(), teleReviewRequest.getLangue()));
            rList = manOrderRemarkDao.
                    getManOrderRemarkByOrderNo(ManOrderRemarkTypeEnum.TELE_REVIEW.getType(), teleReviewRequest.getOrderNo());
        }
        //封装公司电核对象
        for (ManOrderRemark list : rList) {
            ManOrderRemarkResponse response = new ManOrderRemarkResponse();
            BeanUtils.copyProperties(list, response);
            responses.add(response);
        }
        return responses;
    }

    /**
     * 根据类型查询外呼结果
     * @param userUuid
     * @param orderNo
     * @Param type 公司类型包括  公司和第一第二
     * @return
     */
    private List<ManOrderRemarkResponse> getTeleCallResultList(String userUuid,
                                                               String orderNo, Integer type, Integer lanuge) throws Exception {

        if (StringUtils.isEmpty(userUuid) ||
                StringUtils.isEmpty(orderNo) || type == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        TeleCallResult teleCallResult = new TeleCallResult();
        teleCallResult.setDisabled(0);
        teleCallResult.setUserUuid(userUuid);
        teleCallResult.setOrderNo(orderNo);
        List<TeleCallResult> teleCallResults = teleCallResultDao.scan(teleCallResult);
        if (CollectionUtils.isEmpty(teleCallResults)) {
            return new ArrayList<>();
        }
        if (type == ManOrderRemarkTypeEnum.TELE_REVIEW.getType()) {
            teleCallResults = teleCallResults.stream().filter(elem -> elem.getCallType() != null &&
                    elem.getCallType().equals(1)).collect(Collectors.toList());
        } else if (type == ManOrderRemarkTypeEnum.COMPANY_TELE_REVIEW.getType()) {
            teleCallResults = teleCallResults.stream().filter(elem -> elem.getCallType() != null &&
                    (elem.getCallType().equals(2) || elem.getCallType().equals(3)))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(teleCallResults)) {
            return new ArrayList<>();
        }

        //Query two emergency contacts
        OrderMongoRequest request = new OrderMongoRequest();
        request.setOrderNo(orderNo);
        List<OrderEmergencyContactResponse> rList =
                orderUserContactMongoService.getOrderEmergencyContact(request);

        //Mobile phone number format
        String firstUserPhone = CheakTeleUtils.telephoneNumberValid2(rList.get(0).getMobile());
        firstUserPhone = "62" + firstUserPhone;
        String secondUserPhone = CheakTeleUtils.telephoneNumberValid2(rList.get(1).getMobile());
        secondUserPhone = "62" + secondUserPhone;

        //Encapsulated into a new return object
        List<ManOrderRemarkResponse> responses = new ArrayList<>();
        for (TeleCallResult obj : teleCallResults) {

            ManOrderRemarkResponse response = new ManOrderRemarkResponse();
            response.setOrderNo(obj.getOrderNo());
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
            if (StringUtils.isNotBlank(obj.getCallBeginTime())) {
                response.setStartTime(sf.parse(obj.getCallBeginTime()));
            }
            if (StringUtils.isNotBlank(obj.getCallEndTime())) {
                response.setEndTime(sf.parse(obj.getCallEndTime()));
            }
            response.setBurningTime(this.getBurningTime(obj.getCallDuration()));
            if (obj.getCallResult() != null && obj.getCallResult() != 0 && lanuge != null) {
                response.setTeleReviewResult(lanuge.equals(1) ? EnumUtils.valueOfCallResultEnum(obj.getCallResult()).getMessage()
                : EnumUtils.valueOfCallResultEnum(obj.getCallResult()).getMessageInn());
            }
            response.setMobile(obj.getTellNumber());
            response.setCallResultType(obj.getCallResultType());
            //紧急联系人去usrLink表中查询
            if (obj.getCallType().equals(3)) {
                if (obj.getTellNumber().equals(firstUserPhone)) {
                    response.setRealName(rList.get(0).getRealName());
                    response.setRemark(" System calls automatically ");
                    response.setTeleReviewObject("2");
                } else if (obj.getTellNumber().equals(secondUserPhone)) {
                    response.setRealName(rList.get(1).getRealName());
                    response.setRemark(" System calls automatically ");
                    response.setTeleReviewObject("3");
                } else {
                    continue;
                }
            } else {
                //获得公司名称
                UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
                usrWorkDetail.setUserUuid(userUuid);
                usrWorkDetail.setDisabled(0);
                List<UsrWorkDetail> lists = usrWorkDetailDao.scan(usrWorkDetail);
                response.setRealName(CollectionUtils.isEmpty(lists) ? "" : lists.get(0).getCompanyName());
                response.setRemark(" System calls automatically ");
                response.setTeleReviewObject(String.valueOf(obj.getCallType() - 1));
            }
            responses.add(response);

        }
        return responses;
    }

    /**
     * 将整型时间转化为HH:MM:SS
     * @param time
     * @return
     */
    private String getBurningTime(Integer time) {

        return (time/3600) + ":" + ((time - (time/3600) * 3600)/60) + ":" + (time%60);
    }


    /**
     * 保存电核数据
     * @param request
     */
    public void saveTeleReviewResult(SaveTeleReviewRequest request) {

        if (CollectionUtils.isEmpty(request.getResultRequests())) {
            logger.error("电核参数为空！");
            return;
        }
        //删除已经有的电核问题
        manTeleReviewRecordDao.deleteManTeleResult(request.getOrderNo(), request.getUserUuid(), request.getLangue());

        //如果问题答错两题以上，记录原因
        int count = 0;
        ManTeleReviewRecord record = new ManTeleReviewRecord();
        for (TeleReviewResultRequest teleReviewResultRequest : request.getResultRequests()) {
            if (teleReviewResultRequest.getResult() != null &&
                    teleReviewResultRequest.getResult().equals(0)) {
                count ++;
            }
            ManTeleReviewRecord manTeleReviewRecord = new ManTeleReviewRecord();
            manTeleReviewRecord.setUuid(UUIDGenerateUtil.uuid());
            manTeleReviewRecord.setAnswer(teleReviewResultRequest.getAnswer());
            manTeleReviewRecord.setLangue(request.getLangue());
            manTeleReviewRecord.setOrderNo(request.getOrderNo());
            manTeleReviewRecord.setQuestion(teleReviewResultRequest.getQuestion());
            manTeleReviewRecord.setResult(teleReviewResultRequest.getResult());
            manTeleReviewRecord.setUserUuid(request.getUserUuid());
            manTeleReviewRecordDao.insert(manTeleReviewRecord);
            record = manTeleReviewRecord;
        }
        if (count >= 2) {
            record.setDescription("本人审核问题答错两题及以上");
            manTeleReviewRecordDao.update(record);
        }
    }

    /**
     * 回显电核信息
     * @param teleReviewRequest
     * @return
     */
    public TeleReviewCheckResponse getTeleReviewRecords(TeleReviewRequest teleReviewRequest)
            throws Exception {

        if (StringUtils.isEmpty(teleReviewRequest.getUuid())||
                StringUtils.isEmpty(teleReviewRequest.getOrderNo())||
                teleReviewRequest.getLangue() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        //封装数据
        TeleReviewCheckResponse result = new TeleReviewCheckResponse();
        //查询电话审核日志
        result.setRemarks(getTeleReviewRemark(teleReviewRequest));

        if (teleReviewRequest.getTeleReviewType() == ManOrderRemarkTypeEnum.COMPANY_TELE_REVIEW.getType()) {
            return result;
        }
        //根据订单号和用户查询电核记录
        List<TeleReviewQuestionResponse> questions
                = manTeleReviewRecordDao.getTeleRecord(teleReviewRequest.getUuid(),
                teleReviewRequest.getLangue(), teleReviewRequest.getOrderNo());
        result.setQuestions(questions);
        return result;
    }

    /**
     * 通过用户ID，获得用户角色
     * @param uuid
     * @return
     */
    private Integer getUserRole(String uuid) {

        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(uuid);
        List<UsrUser> lists = usrUserDao.scan(usrUser);
        return CollectionUtils.isEmpty(lists) ? 0 : lists.get(0).getUserRole();
    }

    /**
     * 通过用户ID，获得用户性别
     * @param uuid
     * @return
     */
    private Integer getUserSex(String uuid) {

        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(uuid);
        List<UsrUser> lists = usrUserDao.scan(usrUser);
        return CollectionUtils.isEmpty(lists) ? 0 : lists.get(0).getSex();
    }

    /**
     * 通过规则判断是否被拒绝
     * @param remarks
     */
    private void setCompanyTeleReviewResult(List<ManOrderRemarkResponse> remarks,
                                            TeleReviewCheckResponse result, String userUuid) {

        if (CollectionUtils.isEmpty(remarks) ||
                remarks.get(remarks.size() - 1).getTeleReviewOperationType() == null) {
            return;
        }
        //查询用户的userRole
        Integer userRole = this.getUserRole(userUuid);
        List<TeleReviewQuestionResponse> manTeleReviewRecords = new ArrayList<>();
        for (ManOrderRemark orderRemark : remarks) {
            if (orderRemark.getTeleReviewOperationType() == null ||
                    orderRemark.getTeleReviewOperationType().equals(0)) {
                continue;
            }
            if (orderRemark.getTeleReviewOperationType().equals(2)
                    || orderRemark.getTeleReviewOperationType().equals(4)
                    || orderRemark.getTeleReviewOperationType().equals(6)) {
                ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
                reviewRecord.setManOrderRemarkId(orderRemark.getId());
                reviewRecord.setDisabled(0);
                reviewRecord.set_orderBy("remark");
                List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
                if (CollectionUtils.isEmpty(recordList)) {
                    continue;
                }
                //保存公司审核中问题
                manTeleReviewRecords.addAll(recordList.stream().map
                        (elem -> new TeleReviewQuestionResponse(elem.getQuestion(), elem.getAnswer(), elem.getManOrderRemarkId()))
                        .collect(Collectors.toList()));
            }
        }
        result.setQuestions(manTeleReviewRecords);

        //查询字典，判断是否超过稍后再拨次数
        DictionaryRequest dictionaryRequest = new DictionaryRequest();
        dictionaryRequest.setDicCode("TELEREVIEW_COUNT");
        Integer companyTotalCount = 0, firstTotalCount = 0, secondTotalCount = 0;
        Integer companyCount = 0, firstCount = 0, secondCount = 0;
        try {
            List<SysDicItemModel> dicItems = sysDicService.dicItemListByDicCode(dictionaryRequest);
            for (SysDicItemModel dicItem : dicItems) {
                if ("COMPANY_COUNT".equals(dicItem.getDicItemName())) {
                    companyTotalCount = Integer.valueOf(dicItem.getDicItemValue());
                } else if ("FIRST_CONTACT_COUNT".equals(dicItem.getDicItemName())) {
                    firstTotalCount = Integer.valueOf(dicItem.getDicItemValue());
                } else if ("SECOND_CONTACT_COUNT".equals(dicItem.getDicItemName())) {
                    secondTotalCount = Integer.valueOf(dicItem.getDicItemValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ManOrderRemark manOrderRemark : remarks) {
            if (manOrderRemark.getTeleReviewOperationType() == null) {
                continue;
            }
            if (manOrderRemark.getTeleReviewOperationType().equals(1)) {
                //判断是否进行稍后再拨
                if (manOrderRemark.getTeleReviewResultType().equals(2)) {
                    companyCount ++;
                }
            }  else if (manOrderRemark.getTeleReviewOperationType().equals(3)) {
//            //判断是否进行稍后再拨
                if (manOrderRemark.getTeleReviewResultType().equals(2)
                        || manOrderRemark.getTeleReviewResultType().equals(4)
                        || manOrderRemark.getTeleReviewResultType().equals(5)) {
                    firstCount ++;
                }
            } else if (manOrderRemark.getTeleReviewOperationType().equals(5)) {
                //判断是否进行稍后再拨
                if (manOrderRemark.getTeleReviewResultType().equals(2)
                        || manOrderRemark.getTeleReviewResultType().equals(4)
                        || manOrderRemark.getTeleReviewResultType().equals(5)) {
                    secondCount ++;
                }
            }
        }

        //稍后再拨超过次数,风控记录，manOrderRemark随意一条记录description
        if (companyCount.equals(companyTotalCount) && firstCount.equals(firstTotalCount)
                && secondCount.equals(secondTotalCount)) {
            ManOrderRemark manOrderRemark = remarks.get(remarks.size() - 1);
            manOrderRemark.setDescription("稍后再拨达到上线，复审拒绝！");
            manOrderRemarkDao.update(manOrderRemark);
        }
        //判断是否命中其他规则
        ManOrderRemark manOrderRemark = remarks.get(remarks.size() - 1);
        //默认为停止拨打
        result.setPass(2);
        //公司未接通
        if (manOrderRemark.getTeleReviewOperationType().equals(1)) {
            //空号/不存在/无效/错误
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(1)) {
                result.setPass(2);
            }
            // 呼入限制/呼叫转移
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(3)) {
                result.setPass(2);
            }
            //判断是否进行稍后再拨
            if (manOrderRemark.getTeleReviewResultType().equals(2)) {
                if (companyCount >= companyTotalCount) {
                    result.setPass(2);
                    return;
                }
                result.setPass(4);
            }
            //公司接通
        }  else if (manOrderRemark.getTeleReviewOperationType().equals(2)) {
            ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
            reviewRecord.setManOrderRemarkId(manOrderRemark.getId());
            reviewRecord.setDisabled(0);
            reviewRecord.set_orderBy("remark");
            List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
            if (CollectionUtils.isEmpty(recordList) || recordList.size() != 3) {
                return;
            }
            if (recordList.get(0).getAnswer().equals("3")) {
                result.setPass(2);
            }
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(1)) {
                result.setPass(4);
            }
            boolean flag = false;
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(3)) {
                //保存拒绝原因
                manOrderRemark.setDescription("公司接通，手动拒绝");
                flag = true;
            }
            if (recordList.get(0).getAnswer().equals("2")) {
                //保存拒绝原因
                manOrderRemark.setDescription("公司接通，已离职");
                flag = true;
            }
            if (recordList.get(0).getAnswer().equals("5")) {
                //保存拒绝原因
                manOrderRemark.setDescription("公司接通，从未在该公司工作");
                flag = true;
            }
            if (recordList.get(2).getAnswer().equals("2")) {
                //保存拒绝原因
                manOrderRemark.setDescription("公司接通，多次被催收且用户借款不还");
                flag = true;
            }

            if (flag) {
                manOrderRemarkDao.update(manOrderRemark);
                result.setPass(1);
            }
            //直接通过
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(5)
                    || manOrderRemark.getTeleReviewResultType().equals(6)
                    || manOrderRemark.getTeleReviewResultType().equals(7)) {
                result.setPass(5);
            }
            //第一联系人未接通
        } else if (manOrderRemark.getTeleReviewOperationType().equals(3)) {
            result.setPass(3);
//            //判断是否进行稍后再拨
            if (manOrderRemark.getTeleReviewResultType().equals(2)
                    || manOrderRemark.getTeleReviewResultType().equals(4)
                    || manOrderRemark.getTeleReviewResultType().equals(5)) {
                if (firstCount >= firstTotalCount) {
                    result.setPass(2);
                    return;
                }
                result.setPass(4);
            }
            //第一联系人接通
        } else if (manOrderRemark.getTeleReviewOperationType().equals(4)) {
            ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
            reviewRecord.setManOrderRemarkId(manOrderRemark.getId());
            reviewRecord.setDisabled(0);
            reviewRecord.set_orderBy("remark");
            List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
            if (CollectionUtils.isEmpty(recordList)) {
                return;
            }
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(1)) {
                result.setPass(4);
            }
            boolean flag = false;
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(3)) {
                //保存拒绝原因
                manOrderRemark.setDescription("第一联系人接通，手动拒绝");
                flag = true;
            }
            //回答朋友
            if (recordList.get(0).getAnswer().equals("5")) {
                if (this.secondCompanyTeleCallAndSex(remarks, 1)) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("第一联系人接通, 回答朋友，命中复审关系选项组合公司外呼结果和性别");
                    flag = true;
                }
            }
            //角色为家庭主妇，需要分开判断
            if (userRole.equals(3)) {
                if (recordList.get(0).getAnswer().equals("1")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("家庭收入来源者，不认识");
                    flag = true;
                }
                if (recordList.get(4).getAnswer().equals("3")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("家庭收入来源者，多次被催收且用户借款不还");
                    flag = true;
                }
            } else {
                if (recordList.get(3).getAnswer().equals("3")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("第一联系人接通，多次被催收且用户借款不还");
                    flag = true;
                }
                if (recordList.get(1).getAnswer().equals("2")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("第一联系人接通，用户无工作");
                    flag = true;
                }
                //回答同事
                if (recordList.get(0).getAnswer().equals("6")) {
                    if (this.secondCompanyTeleCallAndSex(remarks, null)) {
                        //保存拒绝原因
                        manOrderRemark.setDescription("第一联系人接通, 回答同事，命中复审关系选项组合公司外呼结果和性别");
                        flag = true;
                    }
                }
            }


            if (flag) {
                manOrderRemarkDao.update(manOrderRemark);
                result.setPass(1);
            }
            //直接通过
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(4)) {
                result.setPass(5);
            }
            //新加愿意帮忙还款，直接通过
            if (userRole.equals(3)) {
                if (recordList.size() > 5 && recordList.get(5).getAnswer().equals("1")) {
                    result.setPass(5);
                }
            } else {
                if (recordList.size() > 4 && recordList.get(4).getAnswer().equals("1")) {
                    result.setPass(5);
                }
            }
            //直接拒绝
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(3)) {
                result.setPass(1);
            }
            //第二联系人未接通
        } else if (manOrderRemark.getTeleReviewOperationType().equals(5)) {
            //判断是否进行稍后再拨
            if (manOrderRemark.getTeleReviewResultType().equals(2)
                    || manOrderRemark.getTeleReviewResultType().equals(4)
                    || manOrderRemark.getTeleReviewResultType().equals(5)) {
                if (secondCount >= secondTotalCount) {
                    result.setPass(2);
                    return;
                }
                result.setPass(4);
            }
            //第二联系人接通
        } else if (manOrderRemark.getTeleReviewOperationType().equals(6)) {
            ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
            reviewRecord.setManOrderRemarkId(manOrderRemark.getId());
            reviewRecord.setDisabled(0);
            reviewRecord.set_orderBy("remark");
            List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
            if (CollectionUtils.isEmpty(recordList)) {
                return;
            }
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(1)) {
                result.setPass(4);
            }
            boolean flag = false;
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(3)) {
                //保存拒绝原因
                manOrderRemark.setDescription("第二联系人接通，手动拒绝");
                flag = true;
            }
            //回答朋友
            if (recordList.get(0).getAnswer().equals("5")) {
                if (this.secondCompanyTeleCallAndSex(remarks, 1)) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("第二联系人接通, 回答朋友，命中复审关系选项组合公司外呼结果和性别");
                    flag = true;
                }
            }
            if (userRole.equals(3)) {
                if (recordList.get(4).getAnswer().equals("3")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("常用联系人，多次被催收且用户借款不还");
                    flag = true;
                }
            } else {
                if (recordList.get(3).getAnswer().equals("3")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("第二联系人接通，多次被催收且用户借款不还");
                    flag = true;
                }
                if (recordList.get(1).getAnswer().equals("2")) {
                    //保存拒绝原因
                    manOrderRemark.setDescription("第二联系人接通，用户无工作");
                    flag = true;
                }
                //回答同事
                if (recordList.get(0).getAnswer().equals("6")) {
                    if (this.secondCompanyTeleCallAndSex(remarks, null)) {
                        //保存拒绝原因
                        manOrderRemark.setDescription("第二联系人接通, 回答同事，命中复审关系选项组合公司外呼结果和性别");
                        flag = true;
                    }
                }

            }
            if (flag) {
                manOrderRemarkDao.update(manOrderRemark);
                result.setPass(1);
            }
            //直接通过
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(4)) {
                result.setPass(5);
            }
            //新加愿意帮忙还款，直接通过
            if (userRole.equals(3)) {
                if (recordList.size() > 5 && recordList.get(5).getAnswer().equals("1")) {
                    result.setPass(5);
                }
            } else {
                if (recordList.size() > 4 && recordList.get(4).getAnswer().equals("1")) {
                    result.setPass(5);
                }
            }
            //直接拒绝
            if (manOrderRemark.getTeleReviewResultType() != null &&
                    manOrderRemark.getTeleReviewResultType().equals(3)) {
                result.setPass(1);
            }
        }

    }

    /**
     * 通过规则判断本人审核是否被拒绝
     * @param remarks
     */
    private void setSelfTeleReviewResult(List<ManOrderRemarkResponse> remarks,
                                            TeleReviewCheckResponse result) {

        if (CollectionUtils.isEmpty(remarks)) {
            return;
        }
        List<TeleReviewQuestionResponse> manTeleReviewRecords = new ArrayList<>();
        for (ManOrderRemark orderRemark : remarks) {
            if (orderRemark.getId() == null) {
                continue;
            }
            ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
            reviewRecord.setManOrderRemarkId(orderRemark.getId());
            reviewRecord.setDisabled(0);
            reviewRecord.set_orderBy("remark");
            List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
            if (CollectionUtils.isEmpty(recordList)) {
                continue;
            }
            //保存公司审核中问题
            manTeleReviewRecords.addAll(recordList.stream().map
                    (elem -> new TeleReviewQuestionResponse(elem.getQuestion(), elem.getAnswer(), elem.getManOrderRemarkId()))
                    .collect(Collectors.toList()));
        }
        result.setQuestions(manTeleReviewRecords);

        //判断是否命中其他规则
        ManOrderRemark manOrderRemark = remarks.get(remarks.size() - 1);

        ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
        reviewRecord.setManOrderRemarkId(manOrderRemark.getId());
        reviewRecord.setDisabled(0);
        reviewRecord.set_orderBy("remark");
        List<ManTeleReviewRecord> recordList = manTeleReviewRecordDao.scan(reviewRecord);
        if (!CollectionUtils.isEmpty(recordList)) {
            boolean flag = false;
            if (recordList.get(0).getAnswer() != null) {
                if (recordList.get(0).getAnswer().equals("2")) {
                    manOrderRemark.setDescription("本人审核拒绝，接通问题1否");
                    flag = true;
                } else if (recordList.get(0).getAnswer().equals("3")) {
                    manOrderRemark.setDescription("本人审核拒绝，接通问题1，用户主动请求拒绝");
                    flag = true;
                } else if (recordList.get(0).getAnswer().equals("4")) {
                    manOrderRemark.setDescription("本人审核拒绝，接通问题1，用户不配合审核");
                    flag = true;
                }
            }
            if (recordList.get(1).getAnswer() != null) {
                if (recordList.get(1).getAnswer().equals("2")) {
                    manOrderRemark.setDescription("本人审核拒绝，接通问题2否");
                    flag = true;
                } else if (recordList.get(1).getAnswer().equals("3")) {
                    manOrderRemark.setDescription("本人审核拒绝，接通问题2，记不清了");
                    flag = true;
                }
            }
            if (flag) {
                manOrderRemarkDao.update(manOrderRemark);
                result.setPass(1);
                return ;
            }
            if (recordList.get(0).getAnswer() != null
                    && recordList.get(1).getAnswer() != null) {
                if (recordList.get(0).getAnswer().equals("1") && recordList.get(1).getAnswer().equals("1")) {
                    result.setPass(5);
                    return ;
                }
            }
        }

        if (manOrderRemark.getTeleReviewResultType() != null) {

            boolean flag = false;
            if (manOrderRemark.getTeleReviewResultType().equals(1)) {
                //保存拒绝原因
                manOrderRemark.setDescription("本人审核拒绝，空号/不存在/错误/无效");
                flag = true;
            }
            if (manOrderRemark.getTeleReviewResultType().equals(3)) {
                //保存拒绝原因
                manOrderRemark.setDescription("本人审核拒绝，呼叫转移/呼入限制");
                flag = true;
            }
            if (manOrderRemark.getTeleReviewResultType().equals(6)) {
                //保存拒绝原因
                manOrderRemark.setDescription("本人审核拒绝，停机/已欠费");
                flag = true;
            }

            if (flag) {
                manOrderRemarkDao.update(manOrderRemark);
                result.setPass(1);
                return ;
            }
        }
        result.setPass(4);

    }

    public void saveOrderMogo(String orderNos) {

        String[] orderNoStrs = orderNos.split(",");
        for (String orderNo : orderNoStrs) {
            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setDisabled(0);
            ordOrder.setUuid(orderNo);
            List<OrdOrder> lists = manOrderOrderDao.scan(ordOrder);
            if (CollectionUtils.isEmpty(lists)) {
                continue;
            }
            ordOrder = lists.get(0);
            this.saveOrderInfoToMongo(ordOrder);
        }

    }

    /**
     * 订单号查询复审订单操作标签
     * @param teleReviewRequest
     * @return
     */
    public List<ManSecondRecordResponse> getOperatorType(TeleReviewRequest teleReviewRequest) {

        if (StringUtils.isEmpty(teleReviewRequest.getOrderNo())) {
            return null;
        }
        ManSecondCheckRecord record = new ManSecondCheckRecord();
        record.setDisabled(0);
        record.setOrderNo(teleReviewRequest.getOrderNo());
        List<ManSecondCheckRecord> lists = manSecondCheckRecordDao.scan(record);
        if (CollectionUtils.isEmpty(lists)) {
            return null;
        }

        List<ManSecondRecordResponse> responses = new ArrayList<>();

        lists.stream().forEach(elem -> {
            ManUser manUser = new ManUser();
            manUser.setDisabled(0);
            manUser.setId(elem.getCreateUser());
            List<ManUser> manUsers = manUserDao.scan(manUser);
            if (!CollectionUtils.isEmpty(manUsers)) {
                ManSecondRecordResponse response = new ManSecondRecordResponse();
                response.setCreateTime(elem.getCreateTime());
                response.setOperatorType(elem.getOperatorType());
                response.setUserName(manUsers.get(0).getRealname());
                responses.add(response);
            }

        });

        return responses;
    }

    /**
     * 新增操作标签
     * @param request
     * @return
     */
    public Integer saveOperatorType(TeleReviewRequest request) {

        if (request.getCreateUser() == null ||
                StringUtils.isEmpty(request.getOrderNo())) {
            return 0;
        }
        ManSecondCheckRecord record = new ManSecondCheckRecord();
        record.setDisabled(0);
        record.setOrderNo(request.getOrderNo());
        record.setUserUuid(request.getUuid());
        record.setCreateUser(request.getCreateUser());
        record.setOperatorType(1);
        return manSecondCheckRecordDao.insert(record);
    }

    /**
     * 删除订单之前认证信息
     * @param userUuid
     */
    private void deleteUsrCertificationInfo(String userUuid, Integer certificationType) {

        usrCertificationInfoDao.deleteUsrCertificationInfo(userUuid, certificationType);
    }

    /**
     * 删除订单之前附件信息
     * @param userUuid
     */
    private void deleteUsrAttachmentInfo(String userUuid, Integer attachmentType) {

        usrAttachmentInfoDao.deleteUsrAttachmentInfo(userUuid, attachmentType);
    }

    /**
     * 删除之前订单公司地址
     * @param userUuid
     */
    private void deleteUsrAddressDetail(String userUuid, Integer addressType) {

        usrAddressDetailDao.deleteUsrAddressDetail(userUuid, addressType);
    }

    public Integer deleteOrderDataMongo(String orderNo) throws Exception {
        this.usrBaseInfoService.deleteOrderInfoToMango(orderNo);
        return 0;
    }

    private class GetOrderAndHistory {
        private String orderNo;
        private String sessionId;
        private OrdOrder orderResult;
        private ManSysLoginResponse sysUserInfo;
        private OrdOrder editInfo;
        private OrdHistory orderHistory;

        public GetOrderAndHistory(String orderNo, String sessionId, OrdOrder orderResult) {
            this.orderNo = orderNo;
            this.sessionId = sessionId;
            this.orderResult = orderResult;
        }

        public ManSysLoginResponse getSysUserInfo() {
            return sysUserInfo;
        }

        public OrdOrder getEditInfo() {
            return editInfo;
        }

        public OrdHistory getOrderHistory() {
            return orderHistory;
        }

        public GetOrderAndHistory invoke() throws ServiceExceptionSpec {
            sysUserInfo = ManOrderCheckRemarkService.this.manUserService.getUserInfoBySession(sessionId);
            editInfo = new OrdOrder();
            editInfo.setUuid(orderResult.getUuid());
            editInfo.setUpdateTime(new Date());
            editInfo.setSecondChecker(sysUserInfo.getId());
            orderHistory = new OrdHistory();
            orderHistory.setUuid(UUIDGenerateUtil.uuid());
            orderHistory.setOrderId(orderNo);
            orderHistory.setCreateTime(new Date());
            orderHistory.setUpdateTime(new Date());
            orderHistory.setStatusChangeTime(new Date());
            orderHistory.setStatusChangePerson(sysUserInfo.getRealName());
            orderHistory.setUpdateUser(sysUserInfo.getId());
            orderHistory.setUserUuid(orderResult.getUserUuid());
            orderHistory.setProductUuid(orderResult.getProductUuid());
            return this;
        }
    }
}
