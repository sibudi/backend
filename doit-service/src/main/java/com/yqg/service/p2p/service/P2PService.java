package com.yqg.service.p2p.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrderTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.*;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdDelayRecordDao;
import com.yqg.order.entity.OrdBill;
import com.yqg.order.entity.OrdDelayRecord;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdOrder.P2PLoanStatusEnum;
import com.yqg.service.loan.request.RepayPlan;
import com.yqg.service.loan.request.RepayPlanRequest;
import com.yqg.service.loan.response.CheckRepayResponse;
import com.yqg.service.loan.response.LoanResponse;
import com.yqg.service.loan.service.LoanInfoService;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.OrderCheckService;
import com.yqg.service.p2p.request.*;
import com.yqg.service.p2p.response.*;
import com.yqg.service.p2p.response.P2PResponseDetail.RepaySuccessStatusDetail;
import com.yqg.service.p2p.utils.P2PMD5Util;
import com.yqg.service.risk.service.OrderModelScoreService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.model.AttachmentModel;
import com.yqg.service.user.service.UserAttachmentInfoService;
import com.yqg.service.user.service.UserDetailService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysBankDao;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysBankBasicInfo;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Didit Dwianto on 2018/3/1.
 */
@Service
@Slf4j
public class P2PService {

    @Autowired
    private UsrDao usrDao;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;
    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;
    @Autowired
    private UsrHouseWifeDetailDao usrHouseWifeDetailDao;
    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;
    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;
    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private SysBankDao sysBankDao;
    @Autowired
    private SysProductDao sysProductDao;
    @Autowired
    private OrdService ordService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private UserAttachmentInfoService userAttachmentInfoService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private OrderModelScoreService orderModelScoreService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrderCheckService orderCheckService;
    @Autowired
    private OrdBillDao ordBillDao;
    @Autowired
    private OrdDelayRecordDao ordDelayRecordDao;

    // p2p url
    @Value("${p2p.host}")
    private String HOST_URL;
    @Value("${p2p.url.sendCreditorInfo}")
    private String SEND_ORDER_INFO_URL;
    @Value("${p2p.url.haveInvesting}")
    private String CHECK_IS_HAVE_INVEST_URL;
    @Value("${p2p.url.checkOrderStatus}")
    private String CHECK_ORDER_STATUS_URL;
    @Value("${p2p.url.userRepay}")
    private String USER_REPAY_URL;

    @Autowired
    private OkHttpClient httpClient;

    //流标
    private final static List<String> NEED_RE_ISSUING_LOAN_STATUS = Arrays.asList(P2PLoanStatusEnum.MISS_FUNDING.getStatusCode());

    //p2p进行放款的状态
    private final static List<String> P2P_ISSUING_LOAN_STATUS = Arrays.asList(P2PLoanStatusEnum.ISSUING.getStatusCode(),
            P2PLoanStatusEnum.ISSUED.getStatusCode(),
            P2PLoanStatusEnum.ISSUE_FAILED.getStatusCode(),
            P2PLoanStatusEnum.REPAYMENT_PENDING.getStatusCode(),
            P2PLoanStatusEnum.REPAYMENT_SUCCESS.getStatusCode(),
            P2PLoanStatusEnum.REPAYMENT_FAILED.getStatusCode());

    /**
     * 推标
     */
    public P2PResponse sendOrderInfoToFinancial(OrdOrder order, UsrUser user) {
        P2PResponse loanResponse = new P2PResponse();
        try {

            SendOrderInfoRequest loanRequest = new SendOrderInfoRequest();

            loanRequest.setBorrowingPurposes(getUserBorrowUse(user)); // 借款用途
            loanRequest.setRiskLevel(0);  // 风险等级
            loanRequest.setCreditorNo(order.getUuid()); // 债权编号
            loanRequest.setLenderId(user.getUuid()); // 借款人用户uuid
            loanRequest.setAmountApply(order.getAmountApply()); // 申请金额
            String unit = "3".equals(order.getOrderType()) ? "m" : "d";
            loanRequest.setTerm(order.getBorrowingTerm() + unit); // 申请期限

            // 从产品表中查出来
            if (!StringUtils.isEmpty(order.getProductUuid())){
                SysProduct product = this.sysProductDao.getProductInfoIgnorDisabled(order.getProductUuid());
                loanRequest.setBorrowerYearRate(product.getInterestRate()); // 借款年化利率
            }

            loanRequest.setServiceFee(order.getServiceFee()); //前置服务费
            loanRequest.setBiddingTime(new Date().toString()); // 发标时间
            loanRequest.setChannel(1); //；来源 1 Do-It

            // 银行卡相关信息
            UsrBank usrBank = this.usrBankDao.getUserBankInfoById(user.getUuid(), order.getUserBankUuid());
            loanRequest.setBankCode(usrBank.getBankCode()); // 银行code
            loanRequest.setBankNumber(usrBank.getBankNumberNo());  // 银行卡号
            loanRequest.setBankCardholder(usrBank.getBankCardName()); // 银行卡持卡人姓名
            SysBankBasicInfo bankBasic = this.sysBankDao.getBankBasicInfoById(usrBank.getBankId());
            loanRequest.setBankName(bankBasic.getBankName());  // 银行名称

            //用户信息：
            loanRequest.setName(user.getRealName());
            loanRequest.setIdCardNo(user.getIdCardNo());
            loanRequest.setSex(String.valueOf(user.getSex()));
            int age = CardIdUtils.getAgeByIdCard(user.getIdCardNo().trim());
            loanRequest.setAge(String.valueOf(age));
            loanRequest.setIdentity(String.valueOf(user.getUserRole()));
            loanRequest.setMobile(DESUtils.decrypt(user.getMobileNumberDES()));

            UserDetailService.UserDetailInfo detailInfo = userDetailService.getUserDetailInfo(user);
            loanRequest.setIsMarried(detailInfo.getMarriageStatus());
            loanRequest.setEmail(detailInfo.getEmail());
            loanRequest.setAcademic(detailInfo.getAcademic());
            loanRequest.setBirthday(detailInfo.getBirthday());
            loanRequest.setReligion(detailInfo.getReligionName());
            //居住地址
            List<UsrAddressDetail> addressDetail = userDetailService.getUserAddressList(user.getUuid(), UsrAddressEnum.HOME);
            if (!CollectionUtils.isEmpty(addressDetail)) {
                UsrAddressDetail homeAddress = addressDetail.get(0);
                loanRequest.setAddress(homeAddress.getProvince() + "#" + homeAddress.getCity() + "#" + homeAddress.getBigDirect() + "#" + homeAddress.getSmallDirect());
                loanRequest.setInhabit(homeAddress.getDetailed());
            }
            loanRequest.setIsIdentidyAuth("1");
            loanRequest.setIsBankCardAuth("1");

            List<UsrAttachmentInfo> attachmentList = userAttachmentInfoService.getAttachmentListByUserId(user.getUuid());
            if (!CollectionUtils.isEmpty(attachmentList)) {
                loanRequest.setIsFamilyCardAuth(attachmentList.stream().filter(elem -> elem.getAttachmentType() == UsrAttachmentEnum.KK.getType()).findFirst().isPresent() ? "1" : "0");
                loanRequest.setIsInsuranceCardAuth(attachmentList.stream().filter(elem -> elem.getAttachmentType() == UsrAttachmentEnum.INSURANCE_CARD.getType()).findFirst().isPresent() ? "1" : "0");
            } else {
                loanRequest.setIsFamilyCardAuth("0");
                loanRequest.setIsInsuranceCardAuth("0");
            }

            loanRequest.setIsLindManAuth("1");
            BigDecimal score = orderModelScoreService.getScoreByOrderNo(user.getUuid());
            loanRequest.setCreditScore(score != null ? score.toPlainString() : null);

            // 签名 签名  (来源+债权编号+来源)进行md5编码 ,并转成大写 (channel+creditorNo+channel)
            String sign = P2PMD5Util.md5UpCase("Do-It" + order.getUuid() + "Do-It");
            loanRequest.setSign(sign);
            String requestMsg = JsonUtils.serialize(loanRequest);

            log.info("推标请求的参数为{}", requestMsg);

            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestMsg);

            Request request = new Request.Builder()
                    .url(HOST_URL + SEND_ORDER_INFO_URL)
                    .post(requestBody)
                    .build();

            log.info("推标的订单号为：" + order.getUuid());

            Response response = httpClient.newCall(request).execute();
            log.info("推标返回的response: " + response);
            if (response.isSuccessful()) {

                // 更新redis里面的次数
                // 不同的银行 不同的推单条数限制
                if (usrBank.getBankCode().equals("BCA")){
                    int countP2pBca = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT_BCA));
                    this.sysParamService.setSysParamValue(SysParamContants.LOAN_P2P_COUNT_BCA, String.valueOf(countP2pBca - 1));
                }else if (usrBank.getBankCode().equals("BNI")){
                    int countP2pBni = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT_BNI));
                    this.sysParamService.setSysParamValue(SysParamContants.LOAN_P2P_COUNT_BNI, String.valueOf(countP2pBni - 1));
                }else if (!usrBank.getBankCode().equals("BCA") && !usrBank.getBankCode().equals("BNI")) {

                    int countP2pCimb = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT_CIMB));
                    this.sysParamService.setSysParamValue(SysParamContants.LOAN_P2P_COUNT_CIMB, String.valueOf(countP2pCimb - 1));
                }
//                int countP2p = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT));
//                // 次数减一
//                this.sysParamService.setSysParamValue(SysParamContants.LOAN_P2P_COUNT, String.valueOf(countP2p - 1));

                String responseStr = response.body().string();
                // 返回
                log.info("推标的具体返回:{}", JsonUtils.serialize(responseStr));
                loanResponse = JsonUtils.deserialize(responseStr, P2PResponse.class);

                //成功后记录状态
                this.flowMarker(order, OrdOrder.P2PLoanStatusEnum.SEND_2_P2P_SUCCESS.getStatusCode());
                return loanResponse;
            }
            //失败后记录数据
            log.info("order send failed: {}", order.getUuid());
            this.flowMarker(order, P2PLoanStatusEnum.SEND_2_P2P_FAILED.getStatusCode());
            return loanResponse;
        } catch (Exception e) {
            log.info("send loanInfo to p2p error, orderNo: " + order.getUuid(), e);
            this.flowMarker(order, P2PLoanStatusEnum.SEND_2_P2P_FAILED.getStatusCode());
            return loanResponse;
        }
    }

    // 获取用户的相关信息
    public String getUserBorrowUse(UsrUser user) {

        String borrowUse = "";
        if (user.getUserRole() == 1) {
            // 学生
            UsrStudentDetail studentDetail = new UsrStudentDetail();
            studentDetail.setDisabled(0);
            studentDetail.setUserUuid(user.getUuid());
            List<UsrStudentDetail> detailList = this.usrStudentDetailDao.scan(studentDetail);
            if (!CollectionUtils.isEmpty(detailList)) {
                borrowUse = detailList.get(0).getBorrowUse();
            }

        } else if (user.getUserRole() == 2) {
            // 工作
            UsrWorkDetail workDetail = new UsrWorkDetail();
            workDetail.setDisabled(0);
            workDetail.setUserUuid(user.getUuid());
            List<UsrWorkDetail> detailList = this.usrWorkDetailDao.scan(workDetail);
            if (!CollectionUtils.isEmpty(detailList)) {
                borrowUse = detailList.get(0).getBorrowUse();
            }
        } else if (user.getUserRole() == 3) {
            // 家庭主妇
            UsrHouseWifeDetail houseWifeDetail = new UsrHouseWifeDetail();
            houseWifeDetail.setDisabled(0);
            houseWifeDetail.setUserUuid(user.getUuid());
            List<UsrHouseWifeDetail> detailList = this.usrHouseWifeDetailDao.scan(houseWifeDetail);
            if (!CollectionUtils.isEmpty(detailList)) {
                borrowUse = detailList.get(0).getBorrowUse();
            }
        }
        return borrowUse;
    }

    /**
     * 查看用户是否有在投资
     */
    public P2PResponse checkUserIsHaveInvest(UsrUser user) throws Exception {

        P2PResponse loanResponse = new P2PResponse();

        CheckUserIsHaveInvestRequest scan = new CheckUserIsHaveInvestRequest();
        scan.setIdCardNo(user.getIdCardNo());
        scan.setMobileNumber(DESUtils.decrypt(user.getMobileNumberDES()));

        String requestMsg = JsonUtils.serialize(scan);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestMsg);

        log.info("查看用户是否有在投资的id为：" + user.getUuid());

        Request request = new Request.Builder()
                .url(HOST_URL + CHECK_IS_HAVE_INVEST_URL)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        log.info("查看用户是否有在投资 返回的response" + response);
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            // 返回
            log.info("查看用户是否有在投资 具体返回:{}", JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr, P2PResponse.class);
        }

        return loanResponse;
    }

    /**
     * 用户还款
     */
    public P2PUserPayResponse userRepay(UserRepayRequest repayRequest) throws Exception {

        P2PUserPayResponse loanResponse = new P2PUserPayResponse();

        String requestMsg = JsonUtils.serialize(repayRequest);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestMsg);

        Request request = new Request.Builder()
                .url(HOST_URL + USER_REPAY_URL)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        log.info("用户还款 返回的response" + response);
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            // 返回
            log.info("用户还款 具体返回:{}", JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr, P2PUserPayResponse.class);
        }
        return loanResponse;
    }

    /**
     * 查询标的状态
     */
    public P2PResponse checkOrderInfo(OrdOrder order) throws Exception {

        P2PResponse loanResponse = new P2PResponse();

        CheckOrderInfoRequest checkOrderInfoRequest = new CheckOrderInfoRequest();
        checkOrderInfoRequest.setCreditorNo(order.getUuid());
        String requestMsg = JsonUtils.serialize(checkOrderInfoRequest);


        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestMsg);

        log.info("查询标的id为：" + checkOrderInfoRequest.getCreditorNo());

        Request request = new Request.Builder()
                .url(HOST_URL + CHECK_ORDER_STATUS_URL)
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        log.info("order: {} , 查询标的response: {}", order.getUuid(), response);
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            // 返回
            log.info("order: {} , 查询标的具体返回:{}", order.getUuid(), JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr, P2PResponse.class);
        }

        return loanResponse;
    }


    /***
     * 处理p2p标的相应的状态--》更新到doit平台
     * @param orderNo
     * @param status
     */

    @Transactional(rollbackFor = Exception.class)
    public void handleP2PLoanStatus(String orderNo, String status, RepaySuccessStatusDetail detail) throws Exception {
        OrdOrder scan = new OrdOrder();
        scan.setUuid(orderNo);
        List<OrdOrder> orderList = this.ordDao.scan(scan);
        if (CollectionUtils.isEmpty(orderList)) {
            log.error("查询的订单不存在");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrdOrder order = orderList.get(0);
        OrdOrder.P2PLoanStatusEnum loanStatus = P2PLoanStatusEnum.getEnumFromValue(status);
        switch (loanStatus) {
            case ISSUING: //放款中
                updateOrderStatusAndAddOrderHistory(order, OrdStateEnum.LOANING_DEALING, status);
                break;
            case ISSUED: // 放款成功 -- 代还款
                LoanResponse loanResponse = new LoanResponse();
                loanInfoService.issuedSuccess(order, loanResponse);
                flowMarker(order, status);
                break;
            case ISSUE_FAILED: // 放款失败 -- 放款失败
                updateOrderStatusAndAddOrderHistory(order, OrdStateEnum.LOAN_FAILD, status, "BANK_CARD_ERROR");
                break;
            case REPAYMENT_SUCCESS: // 还款成功
            {
                if (detail == null) {
                    break;
                }
                CheckRepayResponse repayResponse = new CheckRepayResponse();
                repayResponse.setDepositStatus(detail.getDepositStatus());
                repayResponse.setAmount(detail.getAmountActual());
                repayResponse.setDepositChannel(detail.getDepositChannel());
                repayResponse.setDepositMethod(detail.getDepositMethod());
                repayResponse.setExternalId(detail.getExternalId());
                repayResponse.setTransactionId(detail.getTransactionId());
                repayResponse.setPaymentCode(detail.getPaymentcode());
                loanInfoService.repaymentSuccess(order, repayResponse);
                flowMarker(order, status);
                break;
            }
            default: //其他状态
            {
                flowMarker(order, status);
            }
        }
    }


    /**
     * 更新订单状态 添加订单历史
     */
    public void updateOrderStatusAndAddOrderHistory(OrdOrder order, OrdStateEnum ordState, String markStatus, String... remark) {
        if (order.getStatus() == ordState.getCode()) {
            return;
        }
        Integer affectRow = this.ordDao.updateOrderInfoWithOldStatus(order.getUuid(), ordState.getCode(), markStatus, order.getStatus());
        if (affectRow == null || affectRow < 1) {
            //更新失败
            log.info("update mark status error, orderNo: {}", order.getUuid());
            return;
        }
        if (remark != null && remark.length >= 1) {
            order.setRemark(remark[0]);
        }
        order.setStatus(ordState.getCode());
        order.setUpdateTime(new Date());
        this.ordService.addOrderHistory(order);
    }

    /**
     * 流标重新打款
     */
    public void flowMarker(OrdOrder order, String markStatus) {

        OrdOrder update = new OrdOrder();
        update.setUuid(order.getUuid());
        update.setUpdateTime(new Date());
        update.setMarkStatus(markStatus);

        this.ordDao.update(update);

    }

    /**
     * 查看用户是否有在借款
     */
    public CheckUserIsHaveInvestResponse checkUserIsHaveLoan(CheckUserIsHaveInvestRequest request) throws Exception {

        CheckUserIsHaveInvestResponse response = new CheckUserIsHaveInvestResponse();

        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setIdCardNo(request.getIdCardNo());
        List<UsrUser> users = this.usrDao.scan(user);
        if (!CollectionUtils.isEmpty(users)) {
            UsrUser user1 = users.get(0);
            // 查询是否有在贷
            List<OrdOrder> orderList = this.ordDao.hasOldOrder(user1.getUuid());
            if (!CollectionUtils.isEmpty(orderList)){
                response.setIsExit("1");
                return response;
            }
        }

        UsrUser user2 = new UsrUser();
        user2.setDisabled(0);
        user2.setMobileNumberDES(DESUtils.encrypt(request.getMobileNumber()));
        List<UsrUser> users2 = this.usrDao.scan(user2);
        if (!CollectionUtils.isEmpty(users2)) {
            UsrUser user1 = users2.get(0);
            // 查询是否有在贷
            List<OrdOrder> orderList = this.ordDao.hasOldOrder(user1.getUuid());
            if (!CollectionUtils.isEmpty(orderList)){
                response.setIsExit("1");
                return response;
            }
        }

        return response;
    }


    /**
     * 通过借款人ID查询借款用户信息
     */
    public QueryUserInfoResponse queryUserInfoByUserId(QueryUserInfoReqeust reqeust) throws Exception {

        QueryUserInfoResponse response = new QueryUserInfoResponse();
        // 查询用户
        UsrUser user = new UsrUser();
        user.setUuid(reqeust.getUserUuid());
        user.setDisabled(0);
        user.setStatus(1);
        List<UsrUser> users = this.usrDao.scan(user);
        if (CollectionUtils.isEmpty(users)) {
            log.error("查询的借款人不存在");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        user = users.get(0);
        // 查询的用户是否有借款  5,7,8,9,10,11
        List<OrdOrder> orderList = this.ordDao.hasLoanOrder(user.getUuid());
        if (CollectionUtils.isEmpty(orderList)) {
            log.error("查询的借款人没有借款信息");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }

        response.setUserUuid(user.getUuid());
        response.setTeleNumber(DESUtils.decrypt(user.getMobileNumberDES()));
        response.setRealName(user.getRealName());
        response.setIdCardNumber(user.getIdCardNo());
        response.setAge(user.getAge());
        response.setSex(user.getSex());
        response.setUserRole(user.getUserRole());

        // 学历 邮箱 宗教 生日 婚姻状况
        Map<String, Object> infoMap = getUserInfo(user);
        response.setAcademic(infoMap.get("academic").toString());
        response.setEmail(infoMap.get("email").toString());
        response.setReligion(infoMap.get("religion").toString());
        response.setBirthday(infoMap.get("birthday").toString());
        response.setMaritalStatus(Integer.valueOf(infoMap.get("maritalStatus").toString()));

        // 居住地址
        response.setLiveAddress(getUserLiveAddress(user));

        // 相关认证信息  有借款肯定认证过 身份 银行卡 联系人
        response.setHasIdentity("1");
        response.setHasBank("1");
        response.setHasContact("1");

        // 保险卡 和家庭卡
        AttachmentModel attachmentModel = new AttachmentModel();
        attachmentModel.setUserUuid(user.getUuid());
        attachmentModel.setAttachmentType(UsrAttachmentEnum.INSURANCE_CARD.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList.isEmpty()) {
            response.setHasInsuranceCard("1");
        }

        attachmentModel.setAttachmentType(UsrAttachmentEnum.KK.getType());
        List<UsrAttachmentInfo> usrAttachmentInfoList2 = this.getAttachment(attachmentModel);
        if (!usrAttachmentInfoList2.isEmpty()) {
            response.setHasFamilyCard("1");
        }

        return response;
    }

    // 获取用户的相关信息
    public Map<String, Object> getUserInfo(UsrUser user) {

        Map<String, Object> map = new HashMap<>();
        if (user.getUserRole() == 1) {
            // 学生
            UsrStudentDetail studentDetail = new UsrStudentDetail();
            studentDetail.setDisabled(0);
            studentDetail.setUserUuid(user.getUuid());
            List<UsrStudentDetail> detailList = this.usrStudentDetailDao.scan(studentDetail);
            if (!CollectionUtils.isEmpty(detailList)) {
                map.put("academic", detailList.get(0).getAcademic());
                map.put("email", detailList.get(0).getEmail());
                map.put("birthday", detailList.get(0).getBirthday());
                map.put("maritalStatus", 0);
            }

        } else if (user.getUserRole() == 2) {
            // 工作
            UsrWorkDetail workDetail = new UsrWorkDetail();
            workDetail.setDisabled(0);
            workDetail.setUserUuid(user.getUuid());
            List<UsrWorkDetail> detailList = this.usrWorkDetailDao.scan(workDetail);
            if (!CollectionUtils.isEmpty(detailList)) {
                map.put("academic", detailList.get(0).getAcademic());
                map.put("email", detailList.get(0).getEmail());
                map.put("religion", detailList.get(0).getReligion());
                map.put("birthday", detailList.get(0).getBirthday());
                map.put("maritalStatus", detailList.get(0).getMaritalStatus());
            }
        } else if (user.getUserRole() == 3) {
            // 家庭主妇
            UsrHouseWifeDetail houseWifeDetail = new UsrHouseWifeDetail();
            houseWifeDetail.setDisabled(0);
            houseWifeDetail.setUserUuid(user.getUuid());
            List<UsrHouseWifeDetail> detailList = this.usrHouseWifeDetailDao.scan(houseWifeDetail);
            if (!CollectionUtils.isEmpty(detailList)) {
                map.put("academic", detailList.get(0).getAcademic());
                map.put("email", detailList.get(0).getEmail());
                map.put("religion", detailList.get(0).getReligion());
                map.put("birthday", detailList.get(0).getBirthday());
                map.put("maritalStatus", detailList.get(0).getMaritalStatus());
            }
        }
        return map;
    }

    // 获取用户居住地址
    public String getUserLiveAddress(UsrUser user) {

        UsrAddressDetail addressDetail = new UsrAddressDetail();
        addressDetail.setAddressType(0);
        addressDetail.setDisabled(0);
        addressDetail.setUserUuid(user.getUuid());
        List<UsrAddressDetail> detailList = this.usrAddressDetailDao.scan(addressDetail);
        if (!CollectionUtils.isEmpty(detailList)) {
            return detailList.get(0).getDetailed();
        }
        return "";
    }

    // 查询用户附件信息
    private List<UsrAttachmentInfo> getAttachment(AttachmentModel attachmentModel) {
        UsrAttachmentInfo usrAttachmentInfo = new UsrAttachmentInfo();
        usrAttachmentInfo.setUserUuid(attachmentModel.getUserUuid());
        usrAttachmentInfo.setDisabled(0);
        usrAttachmentInfo.setAttachmentType(attachmentModel.getAttachmentType());
        List<UsrAttachmentInfo> usrAttachmentInfoList = this.usrAttachmentInfoDao.scan(usrAttachmentInfo);
        return usrAttachmentInfoList;
    }


    /***
     * 检查是否需要走p2p打款
     * @param usrBank
     * @return
     */
    public boolean isLoanNeedSendToP2P(OrdOrder order, UsrBank usrBank) {

        //目前只有绑定bca银行并且订单非流标状态的才进行p2p通道打款  而且订单不是分期账单
        if (!order.getOrderType().equals("3")) {
            //已经推送了但是流标需要重新走系统通道打款
            if (!StringUtils.isEmpty(order.getMarkStatus()) && NEED_RE_ISSUING_LOAN_STATUS.contains(order.getMarkStatus())) {
                return false;
            }
            String switchValue = sysParamService.getSysParamValue(SysParamContants.PAY_ISSUING_TO_P2P_SWITCH);

            // 不同的银行 不同的推单条数限制
            if (usrBank.getBankCode().equals("BCA")){
                int countP2pBca = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT_BCA));
                if (!"true".equals(switchValue) || countP2pBca <= 0) {
                    if (!order.getMarkStatus().equals("0")) {
                        return true;
                    }
                    //未上线前设置未false
                    return false;
                }
                return true;
            }else if (usrBank.getBankCode().equals("BNI")){
                int countP2pBni = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT_BNI));
                if (!"true".equals(switchValue) || countP2pBni <= 0) {
                    if (!order.getMarkStatus().equals("0")) {
                        return true;
                    }
                    //未上线前设置未false
                    return false;
                }
                return true;
            }else if (!usrBank.getBankCode().equals("BCA") && !usrBank.getBankCode().equals("BNI")){

                int countP2pCimb = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT_CIMB));
                if (!"true".equals(switchValue) || countP2pCimb <= 0) {
                    if (!order.getMarkStatus().equals("0")) {
                        return true;
                    }
                    //未上线前设置未false
                    return false;
                }
                return true;
            }

//        int countP2p = Integer.valueOf(this.sysParamService.getSysParamValue(SysParamContants.LOAN_P2P_COUNT));
//        if (!"true".equals(switchValue) || countP2p <= 0) {
//            if (!order.getMarkStatus().equals("0")) {
//                return true;
//            }
//            //未上线前设置未false
//            return false;
//        }
            return true;
        }
        return false;
    }

    /***
     * 检查是否是已经通过p2p渠道放款的单
     */
    public boolean isP2PIssuedLoan(String orderNo) {
        if(StringUtils.isEmpty(orderNo)){
            log.error("order not isEmpty");
            return false;
        }
        OrdOrder order = ordService.getOrderByOrderNo(orderNo);
        return order.getMarkStatus() != null && P2P_ISSUING_LOAN_STATUS.contains(order.getMarkStatus());
    }


    public List<P2PLoanResponse> getLoanListByMobile(String mobile) {
        //
        String formatMobile = CheakTeleUtils.telephoneNumberValid2(mobile);
        //查询用户
        UsrUser user = usrService.getUserInfoByMobileDesc(DESUtils.encrypt(formatMobile));
        if (user == null) {
            return new ArrayList<>();
        }
        //查询借款成功信息
        List<OrdOrder> orderList = orderCheckService.getSettledOrders(user.getUuid());
        if (CollectionUtils.isEmpty(orderList)) {
            return new ArrayList<>();
        } else {
            String purpose = getUserBorrowUse(user);
            return orderList.stream().map(elem -> new P2PLoanResponse(purpose, elem.getBorrowingTerm() + ("3".equals(elem.getOrderType()) ? "m" :
                    "d"),
                    elem.getAmountApply(), elem.getStatus())).collect(Collectors.toList());
        }

    }

    /**
     *   查询分期订单的还款计划
     * */
    public RepayPlanRequest getRepayPlan(String orderNo) throws Exception{
        log.info("查询的分期订单号为："+orderNo);
        OrdOrder scan = new OrdOrder();
        scan.setUuid(orderNo);
        List<OrdOrder> orderList = this.ordDao.scan(scan);
        if (CollectionUtils.isEmpty(orderList)) {
            log.error("查询的订单不存在");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrdOrder order = orderList.get(0);
        if (!order.getOrderType().equals(OrderTypeEnum.STAGING.getCode())){
            log.error("查询的订单非分期订单");
            throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
        }
        List<OrdBill> ordBillList = this.ordBillDao.billsWithUserUuidAndOrderNo(order.getUserUuid(),order.getUuid());
        if (!CollectionUtils.isEmpty(ordBillList)){

            RepayPlanRequest repayPlanRequest = new RepayPlanRequest();
            List<RepayPlan>  repayPlanList = new ArrayList<>();
            for (OrdBill bill:ordBillList){
                RepayPlan plan = new RepayPlan();
                plan.setPeriodNo(Integer.valueOf(bill.getBillTerm()));
                plan.setLendingTime(bill.getCreateTime());
                plan.setRefundIngTime(bill.getRefundTime());
                plan.setRefundIngAmount(bill.getBillAmout());
                repayPlanList.add(plan);
            }
            repayPlanRequest.setList(repayPlanList);
            repayPlanRequest.setCreditorNo(order.getUuid());
            return repayPlanRequest;
        }else {
            log.error("未查询到对应分期订单的还款计划");
            throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
        }
    }
}
