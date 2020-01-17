package com.yqg.service.order;

import com.yqg.P2P.dao.P2PCreditRightInfoDao;
import com.yqg.P2P.entity.P2PCreditRightInfo;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.*;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.*;
import com.yqg.mongo.dao.UserWifiListDal;
import com.yqg.mongo.entity.UserWifiListMongo;
import com.yqg.order.dao.*;
import com.yqg.order.entity.*;
import com.yqg.service.order.request.GetOrdRepayAmoutRequest;
import com.yqg.service.order.request.LoanConfirmRequest;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.order.response.GetOrdRepayAmoutResponse;
import com.yqg.service.order.response.OrderListResponse;
import com.yqg.service.order.response.OrderOrderResponse;
import com.yqg.service.order.response.PaymentProofResponse;
import com.yqg.service.pay.RepayService;
import com.yqg.service.system.service.CouponService;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.user.service.UsrCertificationService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Service
@Slf4j
public class OrdService {

    @Autowired
    private OrdDao orderDao;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private SysProductDao sysProductDao;

    @Autowired
    private OrdHistoryDao ordHistoryDao;

    @Autowired
    private OrdStepDao ordStepDao;

    @Autowired
    private UserWifiListDal userWifiListDal;

    @Autowired
    private OrdBlackDao ordBlackDao;

    @Autowired
    private UsrCertificationService usrCertificationService;

    @Autowired
    private OrdLoanAmoutRecordDao ordLoanAmoutRecordDao;

    // ?
    private static String KEY = "orderStatus";
    // ?
    private static String VALUE = "orderStatusMsg";

    @Autowired
    private UsrService usrService;

    @Autowired
    private P2PUserDao p2PUserDao;

    @Autowired
    private OrdDeviceInfoService mobileDeviceInfoService;

    @Autowired
    private P2PCreditRightInfoDao p2PCreditRightInfoDao;

    @Autowired
    private RepayService repayService;

    @Autowired
    private LoanUserDao loanUserDao;

    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private OrderChangeHistoryDao orderChangeHistoryDao;

    @Autowired
    private UserRiskService userRiskService;

    @Autowired
    private OrdBillService ordBillService;

    @Autowired
    private CouponService couponService;

    @Transactional
    public OrderOrderResponse toOrder(OrdRequest orderRequest, RedisClient redisClient) throws ServiceException, InvocationTargetException, IllegalAccessException {

       // throw new ServiceException(ExceptionEnum.ORDER_CAN_NOT_COMMIT);
        // ???????????
        String lockKey = "toOrder" + orderRequest.getUserUuid();
        if (!redisClient.lockRepeatWithSeconds(lockKey, 60)) {
            log.error("???????");
            throw new ServiceException(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }

    try {

        UsrUser checkUser = this.usrService.getUserByUuid(orderRequest.getUserUuid());
        String mobileNumber = DESUtils.decrypt(checkUser.getMobileNumberDES());
        if (StringUtils.isEmpty(mobileNumber)){
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }

        checkUserLoanable(orderRequest.getUserUuid());

        // ?????p2p?? ??
        P2PUser p2PUser = new P2PUser();
        p2PUser.setMobileNumber(mobileNumber);
        p2PUser.setDisabled(0);
        List<P2PUser> p2pUserList =  this.p2PUserDao.scan(p2PUser);
        if (!CollectionUtils.isEmpty(p2pUserList)){

            P2PCreditRightInfo p2pOrder = new P2PCreditRightInfo();
            p2pOrder.setDisabled(0);
            p2pOrder.setUserUuid(p2pUserList.get(0).getUuid());
            List<P2PCreditRightInfo> p2pOrderList =  this.p2PCreditRightInfoDao.scan(p2pOrder);
            if (!CollectionUtils.isEmpty(p2pOrderList)){
                throw new ServiceException(ExceptionEnum.USER_IS_P2P_USER);
            }
        }

        // ??????
        OrderOrderResponse orderResponse = new OrderOrderResponse();
        // ?????????????????
        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setStatus(1);
        user.setUuid(orderRequest.getUserUuid());
        List<UsrUser> users = usrDao.scan(user);
        if(CollectionUtils.isEmpty(users)){
            log.error("?????");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        orderResponse.setUserRole(users.get(0).getUserRole().toString());

        // userid???????????????????????
        OrdOrder ord = new OrdOrder();
        ord.setDisabled(0);
        ord.setStatus(OrdStateEnum.SUBMITTING.getCode());
        ord.setUserUuid(orderRequest.getUserUuid());
        List<OrdOrder> orderList = orderDao.scan(ord);

        // ??????productUuid?????????????????????????????????????????????
        SysProduct sysProduct = sysProductDao.getProductInfo(orderRequest.getProductUuid());
        if (orderRequest.getClient_type().equals("iOS")){
            sysProduct = sysProductDao.getProductInfoIgnorDisabled(orderRequest.getProductUuid());
        }
        if(sysProduct==null){
            log.error("???????");
            throw new ServiceException(ExceptionEnum.ORDER_PRODUCT_CONFIG_IS_NULL);
        }

        // 如果最近一笔订单订单状态 是待审核 待打款 待还款 打款失败 的订单 不允许继续下单
        List<OrdOrder> processingOrderList = orderDao.hasProcessingOrder(orderRequest.getUserUuid());
        if (!CollectionUtils.isEmpty(processingOrderList)){
            log.error("有未处理完的订单！");
            throw new ServiceException(ExceptionEnum.ORDER_UN_FINISH);
        }

        // 如果没有正在进行中的订单
        if(CollectionUtils.isEmpty(orderList)){
            // ??????
            UsrAddressDetail usrAddressDetail = saveUsrAddressDetail(orderRequest);
            OrdOrder order = new OrdOrder();
            // ??10=?????,11=?????,12=?????,13=?????,14=????? 15??  ??????????(???????????????????)
            List<OrdOrder> orders =  orderDao.isNewOrder(orderRequest.getUserUuid());
            order.setStatus(OrdStateEnum.SUBMITTING.getCode());
            // ??????
            if(orders.size()==0){// ??
                order.setOrderStep(OrdStepTypeEnum.CREAT_ORDER.getType());
                order.setBorrowingCount(1);//
            }else{// ????????????????
                order.setOrderStep(OrdStepTypeEnum.IDENTITY.getType());
                // ?????????????10?11???????(??????+1)????12?13?14?15??????????????????
                Integer count = 0;
                for (OrdOrder item : orders) {
                    Integer status = item.getStatus();
                    if (status == 10 || status == 11) {
                        count++;// ??????????
                    }
                }
                order.setBorrowingCount(++count);
            }
            order.setChannel(orderRequest.getClient_type().equals("iOS") ? "2" : "1");// ????
            order.setUuid(OrderNoCreator.createOrderNo());
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setInterest(sysProduct.getInterest());// ??????? ??
            order.setUserUuid(orderRequest.getUserUuid());// ????uuid
            //  ???????uuid addrUuid ??????
            order.setOrderPositionId(usrAddressDetail.getUuid());// ????
            order.setProductUuid(sysProduct.getProductCode());// ??id
            order.setApplyTime(new Date());// ????
            order.setAmountApply(sysProduct.getBorrowingAmount());// ????
            order.setBorrowingTerm(sysProduct.getBorrowingTerm());// ????
            order.setServiceFee(sysProduct.getDueFee());// ???
            order.setPaymentFrequencyType(PaymentFrequencyTypeEnum.DAILY.getCode());// ??????
            order.setPaymentType(paymentTypeEnum.AMORTIZATION.getCode());// ????
            order.setLoanStatus(1);// 1???? 2????
            if (orderRequest.getOrderType() != null){
                order.setThirdType(orderRequest.getOrderType());
            }
            if (sysProduct.getId() > 1000){
                // 分期产品
                order.setOrderType(OrderTypeEnum.STAGING.getCode());
                // 服务费这里没有必要实时计算因为 打款的时候会实际计算服务费金额

            }else {
                order.setOrderType(OrderTypeEnum.NORMAL.getCode());
            }
            orderDao.insert(order);
            // ????????
            OrdHistory ordHistory = new OrdHistory();
            ordHistory.setUserUuid(order.getUserUuid());
            ordHistory.setStatus(order.getStatus());
            ordHistory.setProductUuid(order.getProductUuid());
            ordHistory.setOrderId(order.getUuid());
            ordHistory.setStatusChangeTime(new Date());
            ordHistoryDao.insert(ordHistory);
            // ????????
            OrdStep ordStep = new OrdStep();
            ordStep.setUserUuid(order.getUserUuid());
            ordStep.setStep(order.getOrderStep());
            ordStep.setProductUuid(order.getProductUuid());
            ordStep.setOrderId(order.getUuid());
            ordStep.setStepChangeTime(new Date());
            ordStepDao.insert(ordStep);

            // ?????????  ??????????????
            //  2018-02-09  fix-bug : ??????,?????????
            if (users.get(0).getUserRole() != 0 && orders.size()==0){

                //  ?????????
                ord.setUpdateTime(new Date());
                order.setOrderStep(OrdStepTypeEnum.CHOOSE_ROLE.getType());
                this.orderDao.update(order);

                OrdStep ordStep2 = new OrdStep();
                ordStep2.setUserUuid(order.getUserUuid());
                ordStep2.setStep(order.getOrderStep());
                ordStep2.setProductUuid(order.getProductUuid());
                ordStep2.setOrderId(order.getUuid());
                ordStep2.setStepChangeTime(new Date());
                ordStepDao.insert(ordStep2);
            }

            // ????????
            orderResponse.setOrderNo(order.getUuid());
            orderResponse.setOrderStep(order.getOrderStep().toString());
            orderResponse.setOrderStatus(order.getStatus().toString());
            // ????????
            addUserMobileDeviceInfo(orderRequest,order.getUuid());

        }else{// ???????
            // ??????
            UsrAddressDetail usrAddressDetail = saveUsrAddressDetail(orderRequest);
            OrdOrder order = orderList.get(0);
//            OrdOrder od = new OrdOrder();
            order.setUuid(order.getUuid());// ????id??
            order.setServiceFee(sysProduct.getDueFee());//???
            order.setInterest(sysProduct.getInterest());//??
            order.setProductUuid(sysProduct.getProductCode());// ??id
            //  ???????uuid addrUuid ??????
            order.setOrderPositionId(usrAddressDetail.getUuid());// ????
//            od.setApplyTime(new Date());// ????
            order.setAmountApply(sysProduct.getBorrowingAmount());// ????
            order.setBorrowingTerm(sysProduct.getBorrowingTerm());// ????.
            order.setServiceFee(sysProduct.getDueFee());// ???
            order.setChannel(orderRequest.getClient_type().equals("iOS") ? "2" : "1");// ????
            if (orderRequest.getOrderType() != null){
                order.setThirdType(orderRequest.getOrderType());
            }
            if (sysProduct.getId() > 1000){
                // 分期产品
                order.setOrderType(OrderTypeEnum.STAGING.getCode());
                // 服务费这里没有必要实时计算因为 打款的时候会实际计算服务费金额

            }else {
                order.setOrderType(OrderTypeEnum.NORMAL.getCode());
            }
            orderDao.update(order);
            orderResponse.setOrderNo(order.getUuid());
            orderResponse.setOrderStep(order.getOrderStep().toString());
            orderResponse.setOrderStatus(order.getStatus().toString());
            // ????????
            addUserMobileDeviceInfo(orderRequest,order.getUuid());

        }
        //Invite
        //budi: add isinvited_switch
        List<OrdOrder> scanList = this.orderDao.isLoanAgain(orderRequest.getUserUuid());
        String is_invitedSwitch = redisClient.get(RedisContants.IS_INVITED_SWITCH);
        if ("true".equals(is_invitedSwitch)) {
            List<Integer> scanInvitedList = this.orderDao.isInvited(orderRequest.getUserUuid());
            if (CollectionUtils.isEmpty(scanInvitedList)) {
                throw new ServiceException(ExceptionEnum.USER_NOT_INVITED);
            } else {
                if (CollectionUtils.isEmpty(scanList)) {
                    orderResponse.setIsAgain("0");
                } else {
                    orderResponse.setIsAgain("1");
                }
            }
        } else {
            if (CollectionUtils.isEmpty(scanList)) {
                orderResponse.setIsAgain("0");
            } else {
                orderResponse.setIsAgain("1");
            }
        }

        //判断是否跳转到联系人页面[当前订单是否外呼被拒绝过]
        Integer rejectTimes = userRiskService.getEmergencyAutoCallRejectedTimes(orderResponse.getOrderNo());
        orderResponse.setForwardStep(rejectTimes != null && rejectTimes > 0 && rejectTimes<=2 ? OrderOrderResponse.ForwardStepEnum.LINKMAN :
                OrderOrderResponse.ForwardStepEnum.DEFAULT);

        return orderResponse;
    }finally {
        redisClient.unlockRepeat(lockKey);
    }
    }

    /***
     * 单独更新下单地址
     * @param request
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderAddress(OrdRequest request) throws ServiceException {
        if (StringUtils.isEmpty(request.getOrderNo())) {
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        UsrAddressDetail orderAddress = saveUsrAddressDetail(request);
        //更新订单表位置
        OrdOrder order = getOrderByOrderNo(request.getOrderNo());
        if (order == null) {
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        order.setOrderPositionId(orderAddress.getUuid());
        orderDao.update(order);
    }

    /**
     * ???????????? 3?
     * @param orderRequest
     */
    private UsrAddressDetail saveUsrAddressDetail(OrdRequest orderRequest) {
        // ????????
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(orderRequest.getUserUuid());
        usrAddressDetail.setAddressType(UsrAddressEnum.ORDER.getType());// ????? 3
        // ?? useruuid ??????????????????????????
        List<UsrAddressDetail> usrAddressDetails =  usrAddressDetailDao.scan(usrAddressDetail);

        if(usrAddressDetails.size()>0){//??????

            UsrAddressDetail addressDetail = usrAddressDetails.get(0);
            addressDetail.setDetailed(orderRequest.getDetailed());//??????
            addressDetail.setLbsX(orderRequest.getLbsX());//??
            addressDetail.setLbsY(orderRequest.getLbsY());//??
            addressDetail.setProvince(orderRequest.getProvince());
            addressDetail.setCity(orderRequest.getCity());
            addressDetail.setBigDirect(orderRequest.getBigDirect());
            addressDetail.setSmallDirect(orderRequest.getSmallDirect());
            String region = AddressUtils.getRegionByCity(orderRequest.getCity());
            if(StringUtils.isNotEmpty(region)){
                usrAddressDetail.setRegion(region);
            }
            usrAddressDetailDao.update(addressDetail);
        }else {//?????
            usrAddressDetail.setDetailed(orderRequest.getDetailed());//??????
            usrAddressDetail.setLbsX(orderRequest.getLbsX());//??
            usrAddressDetail.setLbsY(orderRequest.getLbsY());//??
            usrAddressDetail.setProvince(orderRequest.getProvince());
            usrAddressDetail.setCity(orderRequest.getCity());
            usrAddressDetail.setBigDirect(orderRequest.getBigDirect());
            usrAddressDetail.setSmallDirect(orderRequest.getSmallDirect());
            usrAddressDetail.setUuid(UUIDGenerateUtil.uuid());// ?????uuid
            String region = AddressUtils.getRegionByCity(orderRequest.getCity());
            if(StringUtils.isNotEmpty(region)){
                usrAddressDetail.setRegion(region);
            }
            usrAddressDetailDao.insert(usrAddressDetail);
        }
        return usrAddressDetail;
    }


    public void addUserMobileDeviceInfo(OrdRequest request, String orderNo){

        OrdDeviceInfo mobileDeviceInfo = new OrdDeviceInfo();
        mobileDeviceInfo.setDeviceType(request.getDeviceType());
        mobileDeviceInfo.setDeviceName(request.getDeviceName());
        mobileDeviceInfo.setDeviceId("");
        mobileDeviceInfo.setSystemVersion(request.getSystem_version());
        mobileDeviceInfo.setPhoneBrand(request.getPhoneBrand());
        mobileDeviceInfo.setTotalMemory(request.getTotalMemory());
        mobileDeviceInfo.setRemainMemory(request.getRemainMemory());
        mobileDeviceInfo.setTotalSpace(request.getTotalSpace());
        mobileDeviceInfo.setRemainSpace(request.getRemainSpace());
        mobileDeviceInfo.setIMEI("");
        mobileDeviceInfo.setIMSI("");
        mobileDeviceInfo.setSimNo(request.getSimNumber());
        mobileDeviceInfo.setCpuType(request.getCpuType());
        mobileDeviceInfo.setOrderNo(orderNo);
        mobileDeviceInfo.setUserUuid(request.getUserUuid());

        mobileDeviceInfo.setLastPowerOnTime(request.getLastPowerOnTime());
        mobileDeviceInfo.setDNS(request.getDnsStr());
        mobileDeviceInfo.setIsRoot(request.getIsRoot());
        mobileDeviceInfo.setNetType(request.getNet_type());
        mobileDeviceInfo.setMemoryCardCapacity(request.getMemoryCardCapacity());
//        mobileDeviceInfo.setWifiList(request.getWifiList());
        mobileDeviceInfo.setMacAddress(request.getMac());
        mobileDeviceInfo.setMobileLanguage(request.getMobileLanguage());
        mobileDeviceInfo.setIPAddress(request.getIPAdress());
        mobileDeviceInfo.setIsSimulator(request.getIsSimulator());
        mobileDeviceInfo.setBattery(request.getBattery());
        mobileDeviceInfo.setPictureNumber(request.getPictureNumber());
        mobileDeviceInfo.setAndroidId(request.getAndroidId());
        this.mobileDeviceInfoService.saveMobileDeviceInfo(mobileDeviceInfo);

        if (!StringUtils.isEmpty(request.getWifiList())){

            // wifiList?? ???mongo
            UserWifiListMongo mongo = new UserWifiListMongo();
            mongo.setUserUuid(request.getUserUuid());
            mongo.setDisabled(0);
            mongo.setOrderNo(orderNo);

                // ??
            List<UserWifiListMongo> scanList = userWifiListDal.find(mongo);
            if (CollectionUtils.isEmpty(scanList)){

                mongo.setData(request.getWifiList());
                mongo.setUpdateTime(new Date());
                mongo.setCreateTime(new Date());
                mongo.setUuid(UUIDGenerateUtil.uuid());
                userWifiListDal.insert(mongo);
            }else {

                // ???mongo
                UserWifiListMongo wifiListMongo = scanList.get(0);
                wifiListMongo.setUpdateTime(new Date());
                wifiListMongo.setData(request.getWifiList());
                userWifiListDal.updateById(wifiListMongo);
            }
        }

    }

    /**
     * ????
     * @param baseRequest
     * @param redisClient
     */
    public List<OrderListResponse> getOrderList(BaseRequest baseRequest, RedisClient redisClient) throws InvocationTargetException, IllegalAccessException {
        List<OrdOrder> orders = orderDao.getOrder(baseRequest.getUserUuid());
        List<OrderListResponse> orderResponse = new ArrayList<>();
        OrderListResponse orderObj = null;
        for(OrdOrder item :orders){
            orderObj = new OrderListResponse();
            orderObj.setOrderStep(item.getOrderStep().toString());
            orderObj.setOrderNo(item.getUuid());
            orderObj.setAmountApply(StringUtils.formatMoney(item.getAmountApply().doubleValue()).replaceAll(",",".").toString());// ??
            orderObj.setBorrowingTerm(item.getBorrowingTerm().toString());
            Map<String,String> map = boxShowOrderStatus(item.getStatus());
            orderObj.setApplyTime(DateUtils.DateToString2(item.getApplyTime()));
            orderObj.setOrderStatus(map.get(KEY));
            orderObj.setOrderStatusMsg(map.get(VALUE));
            orderResponse.add(orderObj);
        }
        return orderResponse;
    }


    /**
     * ??????
     */
    public Map<String,String> boxShowOrderStatus(Integer orderStateCode) {
        Map<String,String> map = new HashMap<>();
        switch(orderStateCode) {
            case 1:
                //???=1
                map.put(KEY, OrdShowStateEnum.APPLICATION.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.APPLICATION.getMessage());
                return map;
            case 2:
            case 3:
            case 4:
            case 17:
            case 18:
            case 19:
            case 20:
                //???=2
                map.put(KEY, OrdShowStateEnum.CHECKING.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.CHECKING.getMessage());
                return map;
            case 5:
            case 6:
                //???=3
                map.put(KEY, OrdShowStateEnum.LOANING.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.LOANING.getMessage());
                return map;
            case 7:
                //???=4
                map.put(KEY, OrdShowStateEnum.TOPAY.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.TOPAY.getMessage());
                return map;
            case 8:
                //???=5
                map.put(KEY, OrdShowStateEnum.OVERDUE.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.OVERDUE.getMessage());
                return map;
            case 9:
                //???=6
                map.put(KEY, OrdShowStateEnum.PAYING.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.PAYING.getMessage());
                return map;
            case 10:
            case 11:
                //???=7
                map.put(KEY, OrdShowStateEnum.PAYED.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.PAYED.getMessage());
                return map;
            case 12:
            case 13:
            case 14:
                //?????=8
                map.put(KEY, OrdShowStateEnum.CHECKNOTPASS.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.CHECKNOTPASS.getMessage());
                return map;
            case 15:
                //??=15
                map.put(KEY, OrdShowStateEnum.CANCEL.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.CANCEL.getMessage());
                return map;
            case 16:
                //????=16
                map.put(KEY, OrdShowStateEnum.LOAN_FAILD.getCode().toString());
                map.put(VALUE, OrdShowStateEnum.LOAN_FAILD.getMessage());
                return map;
        }
        return map;
    }


    //  ????
    public List<OrdOrder> orderInfo(OrdOrder orderOrder) {
        List<OrdOrder> orderList = orderDao.scan(orderOrder);
        return orderList;
    }

    // ????????
    public List<OrdOrder> scanReviewOrder(){
        List<OrdOrder> orderList = orderDao.getRiskOrderList();
//        List<OrdOrder> orderList = orderDao.getRiskOrderTestList();
        return orderList;
    }

    // ???????? ??id
    public List<OrdOrder> scanReviewOrderById(Integer num){

        List<OrdOrder> orderList = orderDao.getRiskOrderListById(num);
        return orderList;
    }



    public void updateOrder(OrdOrder order) {

        this.orderDao.update(order);
    }

    // ???????
    public void addOrderHistory(OrdOrder ordOrder){

        OrdHistory ordHistory = new OrdHistory();
        ordHistory.setUserUuid(ordOrder.getUserUuid());
        ordHistory.setStatus(ordOrder.getStatus());
        ordHistory.setProductUuid(ordOrder.getProductUuid());
        ordHistory.setOrderId(ordOrder.getUuid());
        ordHistory.setStatusChangeTime(new Date());
        this.ordHistoryDao.insert(ordHistory);
    }

    /***
     *   ????????????
     */
    public GetOrdRepayAmoutResponse getOrderActualRepayAmout(GetOrdRepayAmoutRequest request)
            throws Exception {

        String orderNo = request.getOrderNo();
        log.info("订单号"+orderNo);
        GetOrdRepayAmoutResponse response = new GetOrdRepayAmoutResponse();
        if (!StringUtils.isEmpty(orderNo)){

            if (request.getType().equals("3")){
                // 分期账单试算
                OrdBill scan = new OrdBill();
                scan.setDisabled(0);
                scan.setUuid(orderNo);
                scan.setUserUuid(request.getUserUuid());
                List<OrdBill> bills = this.ordBillService.billInfo(scan);
                if (CollectionUtils.isEmpty(bills)) {
                    log.info("the repayment order is not exist.");
                    throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
                }
                OrdBill bill = bills.get(0);
                if( bill.getStatus() != OrdBillStatusEnum.RESOLVING.getCode() && bill.getStatus() != OrdBillStatusEnum.RESOLVING_OVERDUE.getCode() ){
                    log.info("the order status is invalid for repayment");
                    throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
                }
                if( bill.getStatus() == OrdBillStatusEnum.RESOLVED.getCode() || bill.getStatus() == OrdBillStatusEnum.RESOLVED_OVERDUE.getCode()){
                    log.info("the order is already paid.");
                    throw new ServiceException(ExceptionEnum.SYSTEM_IS_REFUND);
                }
                String repayAmount = StringUtils.formatMoney(Double.valueOf(this.repayService.calculateBillRepayAmount(bill).replace(".00",""))).replaceAll(",",".");
                response.setRepayAmout(repayAmount);

            }else {

                OrdOrder orderOrder = new OrdOrder();
                orderOrder.setDisabled(0);
                orderOrder.setUuid(orderNo);
                orderOrder.setUserUuid(request.getUserUuid());
                List<OrdOrder> orderList = orderInfo(orderOrder);
                if (CollectionUtils.isEmpty(orderList)) {
                    log.info("?????");
                    throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
                }
                OrdOrder order = orderList.get(0);
                if( order.getStatus() != OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() && order.getStatus() != OrdStateEnum.RESOLVING_OVERDUE.getCode()){
                    log.info("??????,????????");
                    throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
                }
                if( order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() || order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){
                    log.info("??????????????????????");
                    throw new ServiceException(ExceptionEnum.SYSTEM_IS_REFUND);
                }

                String repayAmount = StringUtils.formatMoney(Double.valueOf(this.repayService.calculateRepayAmount(order,request.getType()).replace(".00",""))).replaceAll(",",".");
                response.setRepayAmout(repayAmount);
                if (!order.getOrderType().equals(OrderTypeEnum.STAGING)){
                    CouponRecord couponRecord = this.couponService.getCouponInfoWithOrderNo(orderNo);
                    // 有优惠券且未使用
                    if (couponRecord != null){
                        String subRepayAmout = new BigDecimal(this.repayService.calculateRepayAmount(order,request.getType())).subtract(couponRecord.getMoney()).setScale(2)+"";
                        response.setRepayAmout(StringUtils.formatMoney(Double.valueOf(subRepayAmout.replace(".00",""))).replaceAll(",","."));
                    }
                }
            }

            log.info("订单实时的代还款金额："+response.getRepayAmout());
            return response;
        }else {
            log.info("未查询到订单");
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
    }

     //  获取待放款订单的手机号
    public  void getLoanOrderTele() {
        List<OrdOrder> orderList = this.orderDao.getLoanOrderTwice();
        log.info("待放款的订单号个数为："+orderList.size());
        if (!CollectionUtils.isEmpty(orderList)){

           for (OrdOrder entity: orderList){

               UsrUser orderUser = this.usrService.getUserByUuid(entity.getUserUuid());
               // 插入待放款用户
               LoanUser loanUser = new LoanUser();
               loanUser.setUserUuid(entity.getUserUuid());
               List<LoanUser> scanList =  this.loanUserDao.scan(loanUser);
               if (CollectionUtils.isEmpty(scanList)){

                   loanUser.setUserMobile(DESUtils.decrypt(orderUser.getMobileNumberDES()));
                   this.loanUserDao.insert(loanUser);
               }

           }
        }
    }

    public  void getLoanOrderTele2() {
        List<LoanUser> userList = this.loanUserDao.getLoanUserListWithNotSend();
        log.info("待放款的用户个数为："+userList.size());
        if (!CollectionUtils.isEmpty(userList)){

            for (LoanUser entity: userList){

                UsrUser orderUser = this.usrService.getUserByUuid(entity.getUuid());
                // 插入待放款用户
                entity.setUserUuid(entity.getUuid());
                entity.setUserMobile(DESUtils.decrypt(orderUser.getMobileNumberDES()));
                this.loanUserDao.update(entity);
            }
        }
    }


    public OrdOrder getOrderByOrderNo(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            return null;
        }
        OrdOrder searchInfo = new OrdOrder();
        searchInfo.setUuid(orderNo);
        searchInfo.setDisabled(0);
        List<OrdOrder> orders = orderDao.scan(searchInfo);
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        } else {
            return orders.get(0);
        }
    }

    /***
     * 获取借款金额借款期限对应的系统配置产品信息
     * @param amount
     * @param term
     * @return
     * @throws Exception
     */
    public SysProduct getProductByAmountAndTerm(BigDecimal amount, int term) throws Exception {
        List<SysProduct> sysProducts = sysProductDao.getProductByAmountAndTermWithDuefeeRate(amount, term,"0.192");
        if (CollectionUtils.isEmpty(sysProducts)) {
            sysProducts = sysProductDao.getProductByAmountAndTermWithDuefeeRate(amount, term,"0.24");
            if (CollectionUtils.isEmpty(sysProducts)) {
                throw new Exception("without product configuration");
            }
        }
        return sysProducts.get(0);
    }

    /***
     * 获取借款金额借款期限对应的系统配置产品信息
     * @param amount
     * @param term
     * @return
     * @throws Exception
     */
    public SysProduct getProductByAmountAndTermWithDuefeeRate(BigDecimal amount, int term,String duefeeRate) throws Exception {
        List<SysProduct> sysProducts = sysProductDao.getProductByAmountAndTermWithDuefeeRate(amount, term,duefeeRate);

        // TODO: 下次上线改回来
        if (CollectionUtils.isEmpty(sysProducts)){
            sysProducts = sysProductDao.getProductByAmountAndTermWithDuefeeRate(amount, term,"0.24");
        }

        if (CollectionUtils.isEmpty(sysProducts)) {
            throw new Exception("without product configuration");
        }
        return sysProducts.get(0);
    }

    public boolean checkUserLoanable(String usrUserId) throws ServiceException {
        // 先查2-9，如果有就抛异常，说明有未完成订单，不能下单    [12-14 单独处理,三个审核不通过，也可以下单]
        List<OrdOrder> noAllowOrders = orderDao.hasN0AllowOrder(usrUserId);
        if (!CollectionUtils.isEmpty(noAllowOrders)) {
            log.error("有未处理完的订单！");
            throw new ServiceException(ExceptionEnum.ORDER_UN_FINISH);
        }

        //  查询距离上次拒绝是否过去了 15天
        List<OrdBlack> ordBlacks = ordBlackDao.getOrderBlakByUserUuidAndDescByUpdateTime(usrUserId);

        if (!CollectionUtils.isEmpty(ordBlacks)) {
            //检查是否因为实名验证失败且没有税卡数据,是的话放开限制
                    if(ordBlacks.get(0).getRuleHitNo().endsWith(BlackListTypeEnum.ADVANCE_VERIFY_RULE.getMessage())){
                        String taxNumber = usrCertificationService.getTaxNumber(usrUserId);
                        if(StringUtils.isEmpty(taxNumber)){
                            return true;
                }
            }

            long diffDay =
                    (new Date().getTime() - ordBlacks.get(0).getCreateTime().getTime()) / (1000 * 60
                            * 60 * 24);
            if (diffDay <= ordBlacks.get(0).getRuleRejectDay()) {

                log.error("距离上次审核被拒未超过天数:{}", ordBlacks.get(0).getRuleRejectDay());
                throw new ServiceException(ExceptionEnum.NOT_ARRVIE_DAY);
            }

            //  cashcash/cheetah用户 而且不是复借用户
            UsrUser user = this.usrService.getUserByUuid(usrUserId);
            List<OrdOrder> orderList = this.orderDao.isLoanAgain(usrUserId);
            if ((user.getUserSource() == 28 || user.getUserSource() == 68)
                    && CollectionUtils.isEmpty(orderList)){
                log.error("cashcash(cheetah)用户被拒绝后不允许再次申请");
                throw new ServiceException(ExceptionEnum.NOT_ARRVIE_DAY);
            }
        }

        return true;
    }

    /***
     * 更新订单金额订单期限
     * @param amount
     * @param term
     */
    public void updateOrderAmountAndTerm(String orderNo, BigDecimal amount, int term)
            throws Exception {
        SysProduct sysProduct = getProductByAmountAndTerm(amount, term);
        OrdOrder order = new OrdOrder();
        order.setUuid(orderNo);

        order.setInterest(sysProduct.getInterest());// 新订单需要塞入 利息
        order.setAmountApply(sysProduct.getBorrowingAmount());// 订单金额
        order.setBorrowingTerm(sysProduct.getBorrowingTerm());// 借款期限
        order.setServiceFee(sysProduct.getDueFee());// 服务费

        order.setProductUuid(sysProduct.getProductCode());// 产品id
        orderDao.update(order);

    }

    /***
     * 获取上一笔结清的贷款
     * @param userUuid
     */
    public OrdOrder getLastSettledLoan(String userUuid) {
        List<OrdOrder> orders = orderDao.getLastSuccessOrder(userUuid);
        if (CollectionUtils.isEmpty(orders)) {
            return null;
        } else {
            return orders.get(0);
        }
    }

    /**
     * 通过订单状态统计订单金额*/
    public BigDecimal orderListSumByStatus(String status) throws Exception {
        return this.orderDao.orderListSumByStatus(status);
    }

    /**
     * 通过订单状态统计订单数量*/
    public Integer orderListCountByStatus(String status) throws Exception {
        return this.orderDao.orderListCountByStatus(status);
    }


    /**
     * 通过refundTime和status统计订单数
     * @param orderStatus 订单状态
     * @param refundTime 还款时间*/
    public Integer orderCountByRefundTimeStatus(String orderStatus,String refundTime)
            throws Exception {
        return this.orderDao.orderCountByRefundTimeStatus(orderStatus,refundTime);
    }

    /**
     * 查询累计还款金额*/
    public BigDecimal getRepaymentTotalAmount() throws Exception {
        return this.orderDao.getRepaymentTotalAmount();
    }

    /**
     * 统计累计复借笔数*/
    public List<ManOrderSecondLoanSpec> secondLoanTotalCount() throws Exception {
        return this.orderDao.secondLoanTotalCount();
    }


    /***
     * 将usrBankId更新到订单表
     * @param orderNo
     * @param usrBankId
     */
    public void updateOrderBankId(String orderNo,String usrBankId){
        orderDao.udpateOrderBankId(orderNo,usrBankId);
    }


    /**
     *  获取用户打款凭证相关信息
     * */
    public PaymentProofResponse getPaymentProof(GetOrdRepayAmoutRequest request){
        PaymentProofResponse response = new PaymentProofResponse();
        OrdOrder order = getOrderByOrderNo(request.getOrderNo());
        // 订单号
        response.setOrderNo(request.getOrderNo());
        // 打款日期
        if (order.getLendingTime() != null) {
            response.setLoanDate(DateUtils.DateToString5(order.getLendingTime()));
        }
        // 平台名称
        response.setPlatformName("Do-It");

        UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
        // 用户姓名
        response.setUserName(user.getRealName());
        // 收款账号
        UsrBank userBank = this.usrBankDao.getUserBankInfoById(request.getUserUuid(),order.getUserBankUuid());

        if (userBank != null) {
            response.setUserAccount(userBank.getBankNumberNo());
            //  收款银行
            response.setLoanBank(userBank.getBankCode());
        }
        // 打款金额
        response.setLoanAmout(calculateLoanAmount(order));

        // 放款渠道
        String loanChannel = getLoanChannel(order);
        if (loanChannel.equals(OrdLoanChannelEnum.XENDIT.getMessage())){
            response.setLoanIconUrl("http://h5.do-it.id/DoitAppPage/img/xendit.png");
        }else if (loanChannel.equals(OrdLoanChannelEnum.CIMB.getMessage())){
            response.setLoanIconUrl("http://h5.do-it.id/DoitAppPage/img/cimb.png");
        }else if (loanChannel.equals(OrdLoanChannelEnum.DANAMON.getMessage())){
            response.setLoanIconUrl("http://h5.do-it.id/DoitAppPage/img/danamon.png");
        }else if (loanChannel.equals(OrdLoanChannelEnum.BCA.getMessage())){
            response.setLoanIconUrl("http://h5.do-it.id/DoitAppPage/img/bca.png");
        }

        return response;
    }

    private String calculateLoanAmount(OrdOrder ordOrder) {
        BigDecimal totalAmount = ordOrder.getAmountApply();
        BigDecimal serviceFee = ordOrder.getServiceFee();
        return totalAmount.subtract(serviceFee) + "";
    }


    // 获取放款渠道
    public String getLoanChannel(OrdOrder order){
        // 查询放款记录
        OrdLoanAmoutRecord record = new OrdLoanAmoutRecord();
        record.setOrderNo(order.getUuid());
        record.setUserUuid(order.getUserUuid());
        record.setDisabled(0);
        List<OrdLoanAmoutRecord> recordList = this.ordLoanAmoutRecordDao.scan(record);
        if (!CollectionUtils.isEmpty(recordList)){
            return recordList.get(0).getLoanChannel();
        }
        return "";
    }


    public void changeOrderTo100RMBProduct(OrdOrder order) throws Exception{
        //记录历史表
        orderChangeHistoryDao.copyFromOrder(order.getUuid());
        //更新金额费用
        OrdOrder updateEntity = new OrdOrder();
        SysProduct sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(new BigDecimal("300000.00"),"0.192",order.getBorrowingTerm());
        if (sysProduct == null){
            sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(new BigDecimal("300000.00"),"0.24",order.getBorrowingTerm());
        }
        updateEntity.setUuid(order.getUuid());
        updateEntity.setUpdateTime(new Date());
        updateEntity.setInterest(sysProduct.getInterest());
        updateEntity.setProductUuid(sysProduct.getProductCode());
        updateEntity.setAmountApply(sysProduct.getBorrowingAmount());
        updateEntity.setBorrowingTerm(sysProduct.getBorrowingTerm());
        updateEntity.setServiceFee(sysProduct.getDueFee());
        orderDao.update(updateEntity);

    }
    public void changeOrderTo100RMBProductfixBug(OrdOrder order) throws Exception{
        //记录历史表
        orderChangeHistoryDao.copyFromOrder(order.getUuid());
        //更新金额费用
        OrdOrder updateEntity = new OrdOrder();
        SysProduct sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(new BigDecimal("300000.00"),"0.192",order.getBorrowingTerm());
        updateEntity.setUuid(order.getUuid());
        updateEntity.setUpdateTime(new Date());
        if (sysProduct == null) {
            sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(new BigDecimal("300000.00"),"0.24",order.getBorrowingTerm());
        }
        updateEntity.setInterest(sysProduct.getInterest());
        updateEntity.setProductUuid(sysProduct.getProductCode());
        updateEntity.setAmountApply(sysProduct.getBorrowingAmount());
        updateEntity.setBorrowingTerm(sysProduct.getBorrowingTerm());
        updateEntity.setServiceFee(sysProduct.getDueFee());
        orderDao.update(updateEntity);

    }
    public void changeOrderTo50RMBProduct(OrdOrder order) throws Exception{
        //记录历史表
        orderChangeHistoryDao.copyFromOrder(order.getUuid());
        //更新金额费用
        OrdOrder updateEntity = new OrdOrder();
        SysProduct sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(new BigDecimal("160000.00"),"0.192",order.getBorrowingTerm());
        if (sysProduct == null){
            sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(new BigDecimal("160000.00"),"0.24",order.getBorrowingTerm());
        }
        updateEntity.setUuid(order.getUuid());
        updateEntity.setUpdateTime(new Date());
        updateEntity.setInterest(sysProduct.getInterest());
        updateEntity.setProductUuid(sysProduct.getProductCode());
        updateEntity.setAmountApply(sysProduct.getBorrowingAmount());
        updateEntity.setBorrowingTerm(sysProduct.getBorrowingTerm());
        updateEntity.setServiceFee(sysProduct.getDueFee());
        orderDao.update(updateEntity);

    }

    public void decreaseOrderToFixAmount(OrdOrder order, BigDecimal toAmount) throws Exception {
        log.info("decrease order amount from {} to {}", order.getAmountApply(), toAmount);
        //记录历史表
        orderChangeHistoryDao.copyFromOrder(order.getUuid());
        //更新金额费用
        OrdOrder updateEntity = new OrdOrder();
        SysProduct sysProduct = sysProductDao.getProductByForDecreamentCreditLimit(toAmount, "0.192", order.getBorrowingTerm());
        if (sysProduct == null) {
            log.error("can not get system product config. orderNo: " + order.getUuid());
        }
        updateEntity.setUuid(order.getUuid());
        updateEntity.setUpdateTime(new Date());
        updateEntity.setInterest(sysProduct.getInterest());
        updateEntity.setProductUuid(sysProduct.getProductCode());
        updateEntity.setAmountApply(sysProduct.getBorrowingAmount());
        updateEntity.setBorrowingTerm(sysProduct.getBorrowingTerm());
        updateEntity.setServiceFee(sysProduct.getDueFee());
        orderDao.update(updateEntity);
    }



    @Transactional(rollbackFor = Exception.class)
    public void changeOrderStatus(OrdOrder order ,OrdStateEnum toStatus){
        log.info("start to change order status, order: {} ,toStatus: {}", order.getUuid(), toStatus.name());

        //Janhsen: skip first check then put status machine not allow
        OrdStateEnum status = toStatus;
        if(toStatus.equals(OrdStateEnum.FIRST_CHECK)) {
            status = OrdStateEnum.MACHINE_CHECK_NOT_ALLOW;
        }

        OrdOrder currentDbOrder = getOrderByOrderNo(order.getUuid());
        Integer affectRow = orderDao.updateOrderInfoWithOldStatus(currentDbOrder.getUuid(), status.getCode(), currentDbOrder.getMarkStatus(),
                currentDbOrder.getStatus());

        if (affectRow == null || affectRow != 1) {
            log.info("update order: {} conflict or error", order.getUuid());
            return;
        }
        
        if(!toStatus.equals(OrdStateEnum.MACHINE_CHECK_NOT_ALLOW)) {
            OrdOrder updateEntity = new OrdOrder();
            updateEntity.setUuid(order.getUuid());
            updateEntity.setApprovedAmount(calculateLoanAmount(currentDbOrder));
            if(toStatus.equals(OrdStateEnum.FIRST_CHECK)) {
                updateEntity.setRemark(order.getRemark() + " | " + "Change first check to machine not allow");
            }
            //更新approvedAmount
            this.updateOrder(updateEntity);
        }

        order.setUpdateTime(new Date());
        order.setStatus(toStatus.getCode());
        order.setApprovedAmount(calculateLoanAmount(currentDbOrder));
        //记录订单状态历史
        this.addOrderHistory(order);
    }

    /**
     *  //如果cashcash 调用 降额接口调用成功，订单会变成待打款状态，审批状态为通过，
     *  但是！！！给到cashcash那边的状态还是待确认，所以这里不需要回调
     **/
    public void confirmLoan(LoanConfirmRequest request, RedisClient redisClient) throws ServiceException {
        //防止重复提交
        String lockKey = "confirmLoan:" + request.getOrderNo();
        boolean lock = redisClient.lockRepeatWithSeconds(lockKey, 5);
        if (!lock) {
            log.error("confirm loan repeat commit,orderNo: " + request.getOrderNo());
            throw new ServiceException(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }
        try {
            OrdOrder order = this.getOrderByOrderNo(request.getOrderNo());
            if (order.getStatus() == OrdStateEnum.LOANING.getCode()) {
                //已经待放开状态，直接返回
                return;
            }
            if (order.getStatus() != OrdStateEnum.WAITING_CONFIRM.getCode()) {
                log.error("the order is invalid,orderNo: " + order.getUuid());
                throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
            }
            this.changeOrderStatus(order, OrdStateEnum.LOANING);

            //归档
            SaveOrderUserUuidRequest saveMongo = new SaveOrderUserUuidRequest();
            saveMongo.setOrderNo(order.getUuid());
            saveMongo.setUserUuid(order.getUserUuid());
            redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST, saveMongo);
        } finally {
            redisClient.unLock(lockKey);
        }
    }




    public List<OrdOrder> getNeedConfirmationOrders(){
        return orderDao.getNeedConfirmationOrders();
    }

    public Optional<OrdOrder> getCurrentOrder(String userUuid) {
        List<OrdOrder> orderList = orderDao.getOrder(userUuid);
        if (CollectionUtils.isEmpty(orderList)) {
            return Optional.empty();
        }
        Optional<OrdOrder> currentOrder =
                orderList.stream().max(Comparator.comparing(OrdOrder::getUpdateTime));
        return currentOrder;
    }



}
