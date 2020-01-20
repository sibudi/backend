package com.yqg.service.system.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.order.OrdBillStatusEnum;
import com.yqg.common.enums.order.OrderTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.enums.order.OrdShowStateEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdRepayAmoutRecordDao;
import com.yqg.order.entity.*;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.response.*;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.pay.RepayService;
import com.yqg.service.risk.service.OrderModelScoreService;
import com.yqg.service.signcontract.ContractSignService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysProductChannelDao;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.CollectionOrderDetail;
import com.yqg.system.entity.StagingProductWhiteList;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Service
@Slf4j
public class IndexService {
    // ????
    private static final String MONEY = "money";
    // ????
    private static final String DATE = "date";
    // ????
    private static final String ARRIVED = "arrived";
    // ???
    private static final String DUEFEE = "dueFee";
    // ??????
    private static final String MATURE = "mature";
    // ??id
    private static final String PRODUCT = "productId";

    // ?
    private static String KEY = "orderStatus";
    // ?
    private static String VALUE = "orderStatusMsg";

    @Autowired
    private OrdDao orderDao;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private OrdService ordService;

    @Autowired
    private SysProductDao sysProductDao;

    @Autowired
    private OrdBlackDao ordBlackDao;

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private ManSysUserDao manUserDao;

    @Autowired
    private ManCollectionOrderDetailDao collectionOrderDetailDao;

    @Autowired
    private P2PService p2PService;
    @Autowired
    private ContractSignService contractSignService;

    @Autowired
    private RepayRateService repayRateService;

    @Autowired
    private StagingProductWhiteListService stagingProductWhiteListService;

    @Autowired
    private OrdBillDao ordBillDao;

    @Autowired
    private RepayService repayService;

    @Autowired
    private OrdRepayAmoutRecordDao ordRepayAmoutRecordDao;

    @Autowired
    private OrderModelScoreService orderModelScoreService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UsrQuestionnaireAttactDao usrQuestionnaireAttactDao;

    @Autowired
    private UsrQuestionnaireDao usrQuestionnaireDao;

    @Autowired
    private SysProductChannelDao sysProductChannelDao;

    private BigDecimal defaultOverDueRate1 = BigDecimal.valueOf(0.01);
    private BigDecimal defaultOverDueRate2 = BigDecimal.valueOf(0.01);

    /**
     *   首先判断用户是否在分期白名单表中
     *   如果在，走分期产品逻辑
     *   不在，走普通订单逻辑
     *
     * @param baseRequest
     * @return
     */
    public Object initHomeView(BaseRequest baseRequest) {

        try {
            String version=baseRequest.getClient_version();
            String clientType=baseRequest.getClient_type();
//        boolean tempFlag=true;
            if(clientType.equals("iOS") || (clientType.equals("android") && (version.compareTo("1.7.1")<0))){
                return  getIndexConfig(baseRequest,1);
            }else {
                // 判断有没有在借的非分期订单
                List<OrdOrder> list = orderDao.hasOldOrder(baseRequest.getUserUuid());
                if (!CollectionUtils.isEmpty(list)){
                    // 正常订单
                    return  getIndexConfig(baseRequest,1);
                }else {
                    StagingProductWhiteList productWhiteList = stagingProductWhiteListService.getProductListByUserUuid(baseRequest.getUserUuid());
                    if (productWhiteList == null) {
                        // 正常订单
                        return getIndexConfig(baseRequest, 1);
                    } else {
                        // 分期订单
                        //  用户在分期白名单中 查询是否有分期订单
                        return getIndexConfig(baseRequest, 2);
                    }
                }
            }
        }catch (Exception e){
            log.error("初始化首页异常",e);
        }
        return null;
    }


    /**
     *  获取首页配置
     *  type： 1  正常订单  2 分期订单
     * */
    public Object getIndexConfig(BaseRequest baseRequest,int type) throws ServiceException{

        List<OrdOrder> list = new ArrayList<OrdOrder>();
        if (type == 1){
            list = orderDao.hasOrder(baseRequest.getUserUuid());
        }else {
            list = orderDao.hasStagingOrder(baseRequest.getUserUuid());
        }

        List<Map<String,String>> confList = new ArrayList<>();
        Map<String,String> confMap = new HashMap<>();

        if(CollectionUtils.isEmpty(list)){
            JSONObject config = boxConfigData(baseRequest,type);
            config.put("showState", ShowStatusEnum.INIT_STAGE.getCode());
            return config;
        }else{
            OrdOrder orderObj = list.get(0);
            Integer status = orderObj.getStatus();
            switch(status) {
                case 1:
                    JSONObject config = boxConfigData(baseRequest,type);
                    config.put("showState",ShowStatusEnum.SUBMITTING_STAGE.getCode());
                    config.put(KEY, ordService.boxShowOrderStatus(status).get(KEY));
                    config.put(VALUE, ordService.boxShowOrderStatus(status).get(VALUE));
                    config.put("productUuid",orderObj.getProductUuid().toString());
                    return config;
                case 2:
                case 3:
                case 4:
                case 17: // 待电核
                case 18: //初审后待外呼
                {
                    HomeOrdResponse loadingResponse = boxResponseFunc(orderObj,type);
                    loadingResponse.setBorrowingAmount(StringUtils.formatMoney(orderObj.getAmountApply().doubleValue()).replaceAll(",","."));// ?????????????

                    loadingResponse.setBorrowingTerm(orderObj.getBorrowingTerm().toString());// ????????????
                    loadingResponse.setShowState(ShowStatusEnum.REVIEWING_STAGE.getCode());
                    loadingResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
                    loadingResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));
                    
                    confMap.put("date", "");
                    confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                    confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                    confList.add(confMap);
                    loadingResponse.setConfList(confList);

                    return loadingResponse;
                }
                case 19: //等待用户确认
                {
                    HomeOrdWithTimeResponse backNoOverResponse = boxResponseFunc2(orderObj,type);
                    //预估还款日期和还款金额
                    Date refundDate = DateUtils.addDate(new Date(),orderObj.getBorrowingTerm()-1);
                    BigDecimal shouldPay = orderObj.getAmountApply().add(orderObj.getInterest());
                    backNoOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPay.doubleValue()).replaceAll(",", "."));
                    backNoOverResponse.setShouldPayTime(DateUtils.DateToString5(refundDate));
                    backNoOverResponse.setShowState(ShowStatusEnum.LOAN_CONFIRMING_STAGE.getCode());

                    confMap.put("date", "");
                    confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                    confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                    confList.add(confMap);
                    backNoOverResponse.setConfList(confList);

                    return backNoOverResponse;
                }
                case 20: {
                    //等待用户激活签约
                    HomeOrdWithTimeResponse backNoOverResponse = boxResponseFunc2(orderObj,type);
                    //预估还款日期和还款金额
                    Date refundDate = DateUtils.addDate(new Date(),orderObj.getBorrowingTerm()-1);
                    BigDecimal shouldPay = orderObj.getAmountApply().add(orderObj.getInterest());
                    backNoOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPay.doubleValue()).replaceAll(",", "."));
                    backNoOverResponse.setShouldPayTime(DateUtils.DateToString5(refundDate));
                    backNoOverResponse.setShowState(ShowStatusEnum.DIGI_SIGN_STAGE.getCode());
                    backNoOverResponse.setSignInfo(contractSignService.getUserCurrentSignData(baseRequest.getUserUuid(),orderObj.getUuid()));
                    
                    confMap.put("date", "");
                    confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                    confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                    confList.add(confMap);
                    backNoOverResponse.setConfList(confList);

                    return backNoOverResponse;
                }
                case 5:
                case 6:
                {
                    HomeOrdResponse loadingResponse = boxResponseFunc(orderObj,type);
                    loadingResponse.setBorrowingAmount(StringUtils.formatMoney(orderObj.getAmountApply().doubleValue()).replaceAll(",", "."));// ?????????????

                    loadingResponse.setBorrowingTerm(orderObj.getBorrowingTerm().toString());// ????????????
                    loadingResponse.setShowState(ShowStatusEnum.REVIEWING_STAGE.getCode());
                    loadingResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
                    loadingResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));

                    //   TODO:???????
                    if (status == 5){
                        UsrBank usrBank = new UsrBank();
                        usrBank.setUuid(orderObj.getUserBankUuid());
                        List<UsrBank> bankList = this.usrBankDao.scan(usrBank);
                        if (!CollectionUtils.isEmpty(bankList)) {
                            UsrBank bankEntity = bankList.get(0);
                            // ?????????????3 faild,????,???????????
                            if (bankEntity.getStatus() == UsrBankCardBinEnum.FAILED.getType()){
                                loadingResponse.setIsBankCardFaild("1");
                            }
                        }
                    }

                    confMap.put("date", "");
                    confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                    confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                    confList.add(confMap);
                    loadingResponse.setConfList(confList);
                    
                    return loadingResponse;
                }
                case 7:// 待还款（未逾期）
                    HomeOrdWithTimeResponse backNoOverResponse = notPayOrderAndNotOverDue(orderObj,status,type);
                    this.getCollectorInfo(backNoOverResponse, orderObj.getUuid());

                    confMap.put("date", "");
                    confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                    confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                    confList.add(confMap);
                    backNoOverResponse.setConfList(confList);
                    return backNoOverResponse;
                case 8:// 待还款（已逾期）
                    // ????= ????-?????
                    try {
                        int dayNum = (int) DateUtils.daysBetween(DateUtils.formDate(orderObj.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));
                        if (dayNum <= 0) {
                            dayNum = 0;
                        }
                        HomeOrdWithTimeResponse backOverResponse = notPayOrderAndOverDue(orderObj, status, dayNum,type);
                        this.getCollectorInfo(backOverResponse, orderObj.getUuid());

                        confMap.put("date", "");
                        confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                        confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                        confList.add(confMap);
                        backOverResponse.setConfList(confList);
                        return backOverResponse;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                case 9:// ?????9?????(??????)
                    HomeOrdWithTimeResponse backResponse = new HomeOrdWithTimeResponse();
                    try {
                        // ?????????
                        int dayNum = (int) DateUtils.daysBetween(DateUtils.formDate(orderObj.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                        if(dayNum <= 0){
                            // ??????
                            backResponse = notPayOrderAndNotOverDue(orderObj,status,type);
                        }else {
                            backResponse = notPayOrderAndOverDue(orderObj,status,dayNum,type);
                        }

                        confMap.put("date", "");
                        confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                        confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                        confList.add(confMap);
                        backResponse.setConfList(confList);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return backResponse;
                case 12:// ??????????12???????
                    //    ?????
                {
                    //budi: reject duration (minutes) configurable from redis
                    int rejectDuration = Integer.parseInt(redisClient.get(RedisContants.RISK_REJECT_DURATION));
                    int rejectDurationDay = rejectDuration / 24 / 60;
                    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                    String rejectDate = format.format(orderObj.getUpdateTime());
                    String rejectStatusDescription = OrdShowStateEnum.REJECT_STATUS_DESCRIPTION.getMessage();
                    rejectStatusDescription = rejectStatusDescription.substring(0, 13) 
                        + rejectDurationDay 
                        + rejectStatusDescription.substring(14, rejectStatusDescription.length()) 
                        + rejectDate;
                    long diffMinute = (new Date().getTime() - orderObj.getUpdateTime().getTime()) / (1000 * 60);
                    log.info(diffMinute+"");
                    if (diffMinute >= rejectDuration){
                        return  homeOrdWithRefuse(orderObj,status,baseRequest,type);
                    }else {
                        HomeOrdResponse loadingResponse = boxResponseFunc(orderObj,type);
                        loadingResponse.setBorrowingAmount(StringUtils.formatMoney(orderObj.getAmountApply().doubleValue()).replaceAll(",","."));// ?????????????

                        loadingResponse.setBorrowingTerm(orderObj.getBorrowingTerm().toString());// ????????????
                        loadingResponse.setShowState(ShowStatusEnum.REVIEWING_STAGE.getCode());
                        loadingResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
                        loadingResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));
                        loadingResponse.setRejectStatusDescription(rejectStatusDescription); //budi: add reject description

                        confMap.put("date", "");
                        confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                        confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                        confList.add(confMap);
                        loadingResponse.setConfList(confList);

                        return loadingResponse;
                    }

                }
                case 13:// ?????13???????
                case 14:// ?????14???????

                    return  homeOrdWithRefuse(orderObj,status,baseRequest,type);
                case 16:// ????
                {
//                    TODO??????????? ??????????
                    HomeOrdResponse loadingResponse = boxResponseFunc(orderObj,type);
                    loadingResponse.setBorrowingAmount(StringUtils.formatMoney(orderObj.getAmountApply().doubleValue()).replaceAll(",","."));// ?????????????

                    loadingResponse.setBorrowingTerm(orderObj.getBorrowingTerm().toString());// ????????????
                    loadingResponse.setShowState(ShowStatusEnum.LOAN_FAILED_STAGE.getCode());
                    loadingResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
                    loadingResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));
                    if (orderObj.getRemark().equals("BANK_CARD_ERROR")){
                        loadingResponse.setIsBankCardFaild("1");
                    }

                    // 订单类型
                    loadingResponse.setOrderType(String.valueOf(type));

                    confMap.put("date", "");
                    confMap.put("Tenor", getProductTenor(orderObj.getProductUuid()));
                    confMap.put("Period", getProductPeriod(orderObj.getProductUuid()));
                    confList.add(confMap);
                    loadingResponse.setConfList(confList);

                    return loadingResponse;
                }
            }
        }
        return null;
    }

    /**
     * 通过订单号，查询是否有未使用优惠券
     */
    public CouponRecord getCouponWithOrderNo (String orderNo) {

        CouponRecord couponRecord = this.couponService.getCouponInfoWithOrderNo(orderNo);
        // 有优惠券且未使用
        if (couponRecord != null){
                return couponRecord;
        }
        return null;
    }

    /**
     * 通过订单号，查询到催收人员的信息
     * @param orderNo
     */
    public void getCollectorInfo (HomeOrdWithTimeResponse homeOrdResponse, String orderNo) {

        if (StringUtils.isEmpty(orderNo)) {
            return ;
        }

        //先查询出当前订单的催收人员
        CollectionOrderDetail collectionOrderDetail = new CollectionOrderDetail();
        collectionOrderDetail.setDisabled(0);
        collectionOrderDetail.setOrderUUID(orderNo);
        List<CollectionOrderDetail> collectionOrderDetails =
                collectionOrderDetailDao.scan(collectionOrderDetail);
        if (CollectionUtils.isEmpty(collectionOrderDetails)) {
            return ;
        }
        collectionOrderDetail = collectionOrderDetails.get(0);
        Integer outSourceId = collectionOrderDetail.getSubOutSourceId().equals(0) ?
                collectionOrderDetail.getOutsourceId() : collectionOrderDetail.getSubOutSourceId();
        //李全夏，王健和催收黑名单直接返回
        if (outSourceId.equals(51) || outSourceId.equals(50) || outSourceId.equals(207)) {
            homeOrdResponse.setHasCollectorOrNot(false);
            return ;
        }
        //通过用户Id查询用户信息
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setStatus(0);
        manUser.setId(outSourceId);
        List<ManUser> users = manUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(users)) {
            return ;
        }
        manUser = users.get(0);
        homeOrdResponse.setCollectionWa(manUser.getCollectionWa());
        homeOrdResponse.setCollectionPhone(manUser.getCollectionPhone());
        homeOrdResponse.setCollectionName(manUser.getUsername());
        homeOrdResponse.setEmployeeNumber(manUser.getEmployeeNumber());
        homeOrdResponse.setCollectionUuid(manUser.getUuid());
        homeOrdResponse.setHasCollectorOrNot(true);

    }
    /**
     *   ?????????
     * */
    public Object homeOrdWithRefuse(OrdOrder orderObj,Integer status,BaseRequest baseRequest,int type) throws ServiceException{
        // TODO ?????????????????????=15 ???????showState?? 0 ????????6
        // ?????
        OrderOrderResponse notAllowResponse = new OrderOrderResponse();
        // ??????
        OrdBlack ordBlack = new OrdBlack();
        ordBlack.setOrderNo(orderObj.getUuid());
        ordBlack.setDisabled(0);
        List<OrdBlack> ordBlacks = ordBlackDao.scan(ordBlack);
        if (!CollectionUtils.isEmpty(ordBlacks)){
            long diffDay = (new Date().getTime() - orderObj.getUpdateTime().getTime())/ (1000 * 60 * 60 * 24);
            if(diffDay > ordBlacks.get(0).getRuleRejectDay()){
                JSONObject config2 = boxConfigData(baseRequest,type);// TODO ??????redis???redis??
                config2.put("showState",ShowStatusEnum.INIT_STAGE.getCode());
                return config2;
            }else {
                notAllowResponse.setOrderNo(orderObj.getUuid());
                notAllowResponse.setOrderStep(orderObj.getOrderStep().toString());
                notAllowResponse.setShowState(ShowStatusEnum.REJECTED_STAGE.getCode());
                notAllowResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
                notAllowResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));
            }
        }else {
            JSONObject config3 = boxConfigData(baseRequest,type);// TODO ??????redis???redis??
            config3.put("showState",ShowStatusEnum.INIT_STAGE.getCode());
            return config3;
        }

        // 费率
        notAllowResponse.setRate1(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE1));
        notAllowResponse.setRate2(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE2));
        notAllowResponse.setRate3(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE3));
        notAllowResponse.setRate4(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE4));
        notAllowResponse.setRate5(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE5));
        notAllowResponse.setRate6(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE6));

        // 金额回收率
        notAllowResponse.setRepayRate(repayRateService.getRepayRate());

        // 订单类型
        notAllowResponse.setOrderType(String.valueOf(type));

        // 用户评分
        notAllowResponse.setUserScore(orderModelScoreService.getScoreByOrderNo(orderObj.getUserUuid())+"");

        // 用户是否填写过调查问卷
        notAllowResponse.setIsNeedQA(checkUserHasQuestionnaire(orderObj.getUserUuid()));

        return notAllowResponse;
    }

    /**
     *   ????????????
     * */
    public HomeOrdWithTimeResponse notPayOrderAndNotOverDue(OrdOrder orderObj,Integer status,int type){
        HomeOrdWithTimeResponse backNoOverResponse = boxResponseFunc2(orderObj,type);
        backNoOverResponse.setShouldPayTime(DateUtils.DateToString5(orderObj.getRefundTime()));// ???????????????
//                    SysProduct sysProduct = orderDao.getProductInfo(orderObj.getProductUuid());
        BigDecimal shouldPay = orderObj.getAmountApply().add(orderObj.getInterest());// ???????????????+??
        if (orderObj.getOrderType().equals("1")){
            //            展期订单
            shouldPay = orderObj.getAmountApply();
        }
        backNoOverResponse.setShowState(ShowStatusEnum.NORMAL_REPAYMENT_STAGE.getCode());

        backNoOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPay.doubleValue()).replaceAll(",","."));// ?????
        backNoOverResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
        backNoOverResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));

        // 费率
        backNoOverResponse.setRate1(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE1));
        backNoOverResponse.setRate2(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE2));
        backNoOverResponse.setRate3(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE3));
        backNoOverResponse.setRate4(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE4));
        backNoOverResponse.setRate5(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE5));
        backNoOverResponse.setRate6(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE6));

        // 金额回收率
        backNoOverResponse.setRepayRate(repayRateService.getRepayRate());

        // 订单类型
        backNoOverResponse.setOrderType(String.valueOf(type));
        // 用户评分
        backNoOverResponse.setUserScore(orderModelScoreService.getScoreByOrderNo(orderObj.getUserUuid())+"");

        // 查询分期账单
        backNoOverResponse = getOrderBills(orderObj,backNoOverResponse);

        // 优惠券
        CouponRecord record = getCouponWithOrderNo(orderObj.getUuid());
        if (record != null){
            // 订单存在未使用的优惠券
            backNoOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPay.subtract(record.getMoney()).doubleValue()).replaceAll(",","."));// ?????
            backNoOverResponse.setCouponNum(StringUtils.formatMoney(record.getMoney().doubleValue()).replaceAll(",",".").toString());
        }

        // 用户是否填写过调查问卷
        backNoOverResponse.setIsNeedQA(checkUserHasQuestionnaire(orderObj.getUserUuid()));

        return backNoOverResponse;
    }

    /**
     *   ???????????
     * */
    public HomeOrdWithTimeResponse notPayOrderAndOverDue(OrdOrder orderObj,Integer status,int dayNum,int type){

        HomeOrdWithTimeResponse backOverResponse = boxResponseFunc2(orderObj,type);
        backOverResponse.setShouldPayTime(DateUtils.DateToString5(orderObj.getRefundTime()));// ?????

        BigDecimal shouldPayAmount = BigDecimal.valueOf(0);
        
        if (orderObj.getOrderType().equals("0") || orderObj.getOrderType().equals("2")){
            SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(orderObj.getProductUuid());
            // ??<=3??overdueRate1?>3??overdueRate2
            if(dayNum <= 3L){
                // ????? = ????+  ????? + ?? + ????????*??????*??????
                shouldPayAmount =orderObj.getAmountApply().add(orderObj.getInterest()).add(sysProd.getOverdueFee()).add(orderObj.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(dayNum)));
            }else{

                shouldPayAmount =orderObj.getAmountApply().add(orderObj.getInterest())
                        .add(sysProd.getOverdueFee())
                        .add(orderObj.getAmountApply().multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(3)))
                        .add(orderObj.getAmountApply().multiply(sysProd.getOverdueRate2()).multiply(BigDecimal.valueOf(dayNum - 3)));
            }

        }else if (orderObj.getOrderType().equals("1")){

            BigDecimal delayFee = BigDecimal.ZERO;
            if (orderObj.getAmountApply().compareTo(BigDecimal.valueOf(500000)) <= 0){
                delayFee = BigDecimal.valueOf(20000);
            }else if (orderObj.getAmountApply().compareTo(BigDecimal.valueOf(2000000)) > 0){
                delayFee = BigDecimal.valueOf(60000);
            }else {
                delayFee = BigDecimal.valueOf(40000);
            }
            //  展期订单还款
            if(dayNum <= 3L){
                // ????? = ????+  ????? + ?? + ????????*??????*??????
                shouldPayAmount =orderObj.getAmountApply().add(delayFee).add(orderObj.getAmountApply().multiply(defaultOverDueRate1).multiply(BigDecimal.valueOf(dayNum)));
            }else{

                shouldPayAmount =orderObj.getAmountApply()
                        .add(delayFee)
                        .add(orderObj.getAmountApply().multiply(defaultOverDueRate1).multiply(BigDecimal.valueOf(3)))
                        .add(orderObj.getAmountApply().multiply(defaultOverDueRate2).multiply(BigDecimal.valueOf(dayNum - 3)));
            }
        }else if (orderObj.getOrderType().equals(OrderTypeEnum.STAGING.getCode())){
            SysProduct sysProd = sysProductDao.getProductInfoIgnorDisabled(orderObj.getProductUuid());
            // ??<=3??overdueRate1?>3??overdueRate2
            BigDecimal billAmount = orderObj.getAmountApply().divide(
                BigDecimal.valueOf(orderObj.getBorrowingTerm()), 2, RoundingMode.HALF_UP);
            if(dayNum <= 3L){
                // ????? = ????+  ????? + ?? + ????????*??????*??????
                shouldPayAmount = billAmount
                    .add(orderObj.getInterest().divide(BigDecimal.valueOf(orderObj.getBorrowingTerm()), 2, RoundingMode.HALF_UP))
                    .add(sysProd.getOverdueFee())
                    .add(billAmount.multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(dayNum)));
            }else{

                shouldPayAmount =billAmount
                        .add(orderObj.getInterest().divide(BigDecimal.valueOf(orderObj.getBorrowingTerm()),2,RoundingMode.HALF_UP))
                        .add(sysProd.getOverdueFee())
                        .add(billAmount.multiply(sysProd.getOverdueRate1()).multiply(BigDecimal.valueOf(3)))
                        .add(billAmount.multiply(sysProd.getOverdueRate2()).multiply(BigDecimal.valueOf(dayNum - 3)));
            }
        }
        
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.DELAYORDER_OF_NUMBER);
        // 订单是否有未使用的优惠券
        CouponRecord record = getCouponWithOrderNo(orderObj.getUuid());
        if (dayNum > Integer.valueOf(sysParamValue) && orderObj.getOrderType().equals(OrderTypeEnum.NORMAL.getCode())
                && !this.p2PService.isP2PIssuedLoan(orderObj.getUuid())
                && (orderObj.getAmountApply().compareTo(BigDecimal.valueOf(100000)) != 0)
                && (orderObj.getAmountApply().compareTo(BigDecimal.valueOf(200000)) != 0)
                && (orderObj.getAmountApply().compareTo(BigDecimal.valueOf(160000)) != 0)
                && (orderObj.getAmountApply().compareTo(BigDecimal.valueOf(300000)) != 0)
                && record == null){
            // ???????? ?????? (??????????)
            // 如果订单存在优惠券 也不能使用展期还款
            backOverResponse.setIsDelay("1");
        }

        backOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPayAmount.doubleValue()).replaceAll(",",".").toString());// ?????
        backOverResponse.setShowState(ShowStatusEnum.OVERDUE_REPAYMENT_STAGE.getCode());
        backOverResponse.setOrderStatus(ordService.boxShowOrderStatus(status).get(KEY));
        backOverResponse.setOrderStatusMsg(ordService.boxShowOrderStatus(status).get(VALUE));

        // 费率
        backOverResponse.setRate1(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE1));
        backOverResponse.setRate2(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE2));
        backOverResponse.setRate3(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE3));
        backOverResponse.setRate4(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE4));
        backOverResponse.setRate5(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE5));
        backOverResponse.setRate6(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE6));

        // 金额回收率
        backOverResponse.setRepayRate(repayRateService.getRepayRate());

        // 订单类型
        backOverResponse.setOrderType(String.valueOf(type));

        // 用户评分
        backOverResponse.setUserScore(orderModelScoreService.getScoreByOrderNo(orderObj.getUserUuid())+"");

        //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
        BigDecimal limit = BigDecimal.ZERO;
        if(orderObj.getOrderType().equals(OrderTypeEnum.STAGING.getCode())){
            limit = orderObj.getAmountApply().divide(BigDecimal.valueOf(orderObj.getBorrowingTerm()), 2,RoundingMode.HALF_UP)
                .subtract(orderObj.getServiceFee().divide(BigDecimal.valueOf(orderObj.getBorrowingTerm()), 2,RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(2)).setScale(2);
        }
        else{
            limit = orderObj.getAmountApply().subtract(orderObj.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
        }

        if (shouldPayAmount.compareTo(limit) > 0 ){
            shouldPayAmount = limit;
            backOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPayAmount.doubleValue()).replaceAll(",",".").toString());
        }

        // 添加还款计划
        backOverResponse = getOrderBills(orderObj,backOverResponse);

        //re-set shouldPayAmount because getOrderBill has change shouldPayAmount        
        if (shouldPayAmount.compareTo(limit) > 0 ){
            shouldPayAmount = limit;
            backOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPayAmount.doubleValue()).replaceAll(",",".").toString());
        }
        
        // 优惠券
        if (record != null){
            // 订单存在未使用的优惠券
            backOverResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPayAmount.subtract(record.getMoney()).doubleValue()).replaceAll(",","."));// ?????
            backOverResponse.setCouponNum(StringUtils.formatMoney(record.getMoney().doubleValue()).replaceAll(",",".").toString());
        }

        // 用户是否填写过调查问卷
        backOverResponse.setIsNeedQA(checkUserHasQuestionnaire(orderObj.getUserUuid()));

        return  backOverResponse;
    }

    /**
     *  获取分期账单
     * */
    public HomeOrdWithTimeResponse getOrderBills(OrdOrder order,HomeOrdWithTimeResponse homeOrdWithTimeResponse) {
        try {
            // 查询分期账单
            if (order.getOrderType().equals(OrderTypeEnum.STAGING.getCode())){

                 //Janhsen: change 1.2 to 2 because max repayment overdue fee is 200%
                BigDecimal limit = BigDecimal.ZERO;
                if(order.getOrderType().equals(OrderTypeEnum.STAGING.getCode())){
                    limit = order.getAmountApply().divide(BigDecimal.valueOf(order.getBorrowingTerm()), 2, RoundingMode.UP)
                        .subtract(order.getServiceFee().divide(BigDecimal.valueOf(order.getBorrowingTerm()), 0, RoundingMode.UP))
                        .multiply(BigDecimal.valueOf(2)).setScale(2);
                }
                else{
                    limit = order.getAmountApply().subtract(order.getServiceFee()).multiply(BigDecimal.valueOf(2)).setScale(2);
                }

                
                List<OrdBill> bills = this.ordBillDao.billsWithUserUuidAndOrderNo(order.getUserUuid(),order.getUuid());
                if (!CollectionUtils.isEmpty(bills)){
                    List<OrdBillResponse> list = new ArrayList<>();
                    int i = 0;
                    BigDecimal remainAmout = BigDecimal.valueOf(0);
                    for (OrdBill bill: bills)
                    {
                        OrdBillResponse response = new OrdBillResponse();
                        response.setBillNo(bill.getUuid());
                        response.setBillAmount(bill.getBillAmout().toString().replace(".00",""));
                        response.setBillTerm(bill.getBillTerm());
                        response.setRefundTime(DateUtils.DateToString(bill.getRefundTime()));
                        response.setStatus(bill.getStatus());
                        response.setInterest(bill.getInterest().toString().replace(".00",""));

                        BigDecimal  shouldPayAmount = BigDecimal.valueOf(0);
                        if (bill.getStatus().equals(OrdBillStatusEnum.RESOLVING.getCode())){
                            shouldPayAmount = bill.getBillAmout().add(bill.getInterest());
                            // 每期应还款总金额 或者 已还款总金额

                            if (shouldPayAmount.compareTo(limit) > 0 ){
                                shouldPayAmount = limit;
                            }
                            response.setTotalAmount(shouldPayAmount.toString().replace(".00",""));

                        }else if (bill.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode())){
                            int dayNum = (int) DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                            shouldPayAmount = bill.getBillAmout().add(bill.getInterest()).add(bill.getOverdueFee()).add(bill.getBillAmout().multiply(bill.getOverdueRate()).multiply(BigDecimal.valueOf(dayNum))).setScale(2);
                            // 每期应还款总金额 或者 已还款总金额
                            if (shouldPayAmount.compareTo(limit) > 0 ){
                                shouldPayAmount = limit;
                            }
                            response.setTotalAmount(shouldPayAmount.toString().replace(".00",""));
                        }else {
                            // 查询还款记录
                            OrdRepayAmoutRecord record = new OrdRepayAmoutRecord();
                            record.setDisabled(0);
                            record.setOrderNo(bill.getUuid());
                            record.setUserUuid(bill.getUserUuid());
                            List<OrdRepayAmoutRecord> recordList = this.ordRepayAmoutRecordDao.scan(record);
                            if (!CollectionUtils.isEmpty(recordList)){
                                OrdRepayAmoutRecord repayRecord = recordList.get(0);
                                response.setTotalAmount(repayRecord.getActualRepayAmout().replace(".00",""));
                            }else {
                                log.error("未找到账单对应的还款记录，账单号：",bill.getUuid());
                            }
                        }

                        if ((bill.getStatus().equals(OrdBillStatusEnum.RESOLVING.getCode()) ||
                                bill.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode()))
                                && i == 0){
                            i = 1;
                            // 当前代还款订单   当前期数  金额  应还款时间
                            homeOrdWithTimeResponse.setCurrentTerm(bill.getBillTerm());
                            homeOrdWithTimeResponse.setCurrentNum(bill.getBillAmout().toString().replace(".00",""));
                            homeOrdWithTimeResponse.setCurrentRepayTime(DateUtils.DateToString5(bill.getRefundTime()));
                            homeOrdWithTimeResponse.setCurrentBillNo(bill.getUuid());
                            homeOrdWithTimeResponse.setShouldPayAmount(StringUtils.formatMoney(shouldPayAmount.doubleValue()).replaceAll(",",".").toString());
                        }

                        if (bill.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode())){
                            // 账单逾期 计算服务费和滞纳金
                            response.setOverdueFee(bill.getOverdueFee().toString().replace(".00",""));
                            int overdueDay = DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                            BigDecimal penalty = this.repayService.calculatePenaltyFeeByRepayDaysForBills(bill,overdueDay);
                            BigDecimal totalBill = penalty.add(bill.getBillAmout()).add(bill.getOverdueFee());
                            if (totalBill.compareTo(limit) > 0 ){
                                penalty = limit.add(bill.getOverdueFee().multiply(BigDecimal.valueOf(-1))).add(bill.getBillAmout().multiply(BigDecimal.valueOf(-1)));
                            }

                            response.setPenalty(penalty.toString().replace(".00",""));
                        }
                        remainAmout = remainAmout.add(shouldPayAmount).setScale(2);
                        list.add(response);
                    }
                    homeOrdWithTimeResponse.setBillsList(list);

                    //订单剩余应还金额
                    homeOrdWithTimeResponse.setRemainingNum(remainAmout.toString().replace(".00",""));
                }
            }

        }catch (Exception e){
            log.error("查询分期账单异常",e);
        }
        return homeOrdWithTimeResponse;
    }

    /**
     * ?????????????-???????-??????
     *
     * @param orderObj
     * @return
     */
    private HomeOrdResponse boxResponseFunc(OrdOrder orderObj,int type) {
        HomeOrdResponse homeOrdResponse = new HomeOrdResponse();

        UsrBank userBank = usrBankDao.getUserBankInfoById(orderObj.getUserUuid(),orderObj.getUserBankUuid());
        homeOrdResponse.setOrderStatus(orderObj.getStatus().toString());  //????
        homeOrdResponse.setOrderStep(orderObj.getOrderStep().toString()); //????
        homeOrdResponse.setOrderNo(orderObj.getUuid());// ????
        homeOrdResponse.setApplicationTime(DateUtils.DateToString5(orderObj.getApplyTime()));// ?????????????
        homeOrdResponse.setArrivedAmount(StringUtils.formatMoney(orderObj.getAmountApply().subtract(orderObj.getServiceFee()).doubleValue()).replaceAll(",","."));// ????
        homeOrdResponse.setBankCode(userBank.getBankCode());// ????id????sysUserBankInfo???
        homeOrdResponse.setBankNumberNo(userBank.getBankNumberNo());// ????id????sysUserBankInfo???

        // 费率
        homeOrdResponse.setRate1(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE1));
        homeOrdResponse.setRate2(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE2));
        homeOrdResponse.setRate3(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE3));
        homeOrdResponse.setRate4(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE4));
        homeOrdResponse.setRate5(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE5));
        homeOrdResponse.setRate6(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE6));

        // 金额回收率
        homeOrdResponse.setRepayRate(repayRateService.getRepayRate());

        // 订单类型
        homeOrdResponse.setOrderType(String.valueOf(type));

        if (type  == 2){
            // 如果是分期订单 实际到账金额要改变
            StagingProductWhiteList productWhiteList = this.stagingProductWhiteListService.getProductListByUserUuid(orderObj.getUserUuid());
            SysProduct product = this.sysProductDao.getProductInfo(productWhiteList.getProductUuid());
            int term = product.getBorrowingTerm();
            // 还款计划   期数  应还款时间  还款金额
            List<Map<String,String>>  repayPlans = new ArrayList<>();
            Date nowDate = new Date();
            Date lastDate = new Date();
            // int totalNum = 0;
            // BigDecimal serviceFee = BigDecimal.ZERO;
            for (int i = 1; i <= term; i++) {
                Date  refundTime =  DateUtils.getRefundDate(product.getProductType(),i, nowDate); //DateUtils.addDate(DateUtils.addDateWithMonth(nowDate,i),-1);
                // totalNum = DateUtils.differentDaysByMillisecond(nowDate,refundTime)+1;
                // serviceFee = product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).add(serviceFee).setScale(2);
                
                log.info("第"+i+"期还款时间为"+DateUtils.DateToString(refundTime));
                if (i == term){
                    lastDate = refundTime;
                }
            }
            int totalNum = DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;
            log.info("当前时间:"+DateUtils.DateToString(nowDate)+"   最后还款时间:"+DateUtils.DateToString(lastDate) + "   总借款天数:" + totalNum);
            BigDecimal serviceFee =  product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).setScale(2);
            log.info("分期订单实际 服务费为"+serviceFee);

            homeOrdResponse.setArrivedAmount(StringUtils.formatMoney(orderObj.getAmountApply().subtract(serviceFee).doubleValue()).replaceAll(",","."));// ????
        }

        // 用户评分
        homeOrdResponse.setUserScore(orderModelScoreService.getScoreByOrderNo(orderObj.getUserUuid())+"");
        // 用户是否填写过调查问卷
        homeOrdResponse.setIsNeedQA(checkUserHasQuestionnaire(orderObj.getUserUuid()));
        return homeOrdResponse;
    }





    /**
     *   ???-???7 ???-???8
     *
     * @param orderObj
     * @return
     */
    private HomeOrdWithTimeResponse boxResponseFunc2(OrdOrder orderObj,int type) {
        HomeOrdWithTimeResponse homeOrdResponse = new HomeOrdWithTimeResponse();

        UsrBank userBank = usrBankDao.getUserBankInfoById(orderObj.getUserUuid(),orderObj.getUserBankUuid());
        homeOrdResponse.setOrderStatus(orderObj.getStatus().toString());  //????
        homeOrdResponse.setOrderStep(orderObj.getOrderStep().toString()); //????
        homeOrdResponse.setOrderNo(orderObj.getUuid());//????
        homeOrdResponse.setApplicationTime(DateUtils.DateToString5(orderObj.getApplyTime()));// ?????
        homeOrdResponse.setBorrowingAmount(StringUtils.formatMoney(orderObj.getAmountApply().doubleValue()).replaceAll(",","."));// ????
        homeOrdResponse.setBorrowingTerm(orderObj.getBorrowingTerm().toString());// ????
        homeOrdResponse.setArrivedAmount(StringUtils.formatMoney(orderObj.getAmountApply().subtract(orderObj.getServiceFee()).doubleValue()).replaceAll(",","."));// ????
        homeOrdResponse.setBankCode(userBank.getBankCode());// ????id????sysUserBankInfo???
        homeOrdResponse.setBankNumberNo(userBank.getBankNumberNo());// ????id????sysUserBankInfo???
        homeOrdResponse.setOrderStatus(orderObj.getStatus().toString());

        // 费率
        homeOrdResponse.setRate1(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE1));
        homeOrdResponse.setRate2(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE2));
        homeOrdResponse.setRate3(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE3));
        homeOrdResponse.setRate4(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE4));
        homeOrdResponse.setRate5(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE5));
        homeOrdResponse.setRate6(this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE6));


        // 金额回收率
        homeOrdResponse.setRepayRate(repayRateService.getRepayRate());

        // 订单类型
        homeOrdResponse.setOrderType(String.valueOf(type));

        // 用户评分
        homeOrdResponse.setUserScore(orderModelScoreService.getScoreByOrderNo(orderObj.getUserUuid())+"");

        // 用户是否填写过调查问卷
        homeOrdResponse.setIsNeedQA(checkUserHasQuestionnaire(orderObj.getUserUuid()));

        return homeOrdResponse;
    }

    /**
     * 获取基本产品配置
     * @param
     * @return
     */
    private JSONObject boxConfigData(BaseRequest baseRequest,int type) throws ServiceException {

        JSONObject result = new JSONObject();

        // 用户
        UsrUser user = this.usrService.getUserByUuid(baseRequest.getUserUuid());

        int level = 0;
        String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_LEVEL_OFF_NO);
        if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {
            level = user.getProductLevel();
        }

        String clientType = baseRequest.getClient_type();
        List<SysProduct> list = new ArrayList<>();
        if (clientType.equals("iOS")){

            //  TODO: iOS线上版本只支持 多种金额（7种) 多种期限（8种）
            //  先判断是否是新老用户
            List<OrdOrder> hasLoanList = this.orderDao.hasLoan(user.getUuid());
            if (!CollectionUtils.isEmpty(hasLoanList)){
                // 老用户 费率 20%
                list = sysProductDao.getProductWithProductLevelAndUserLevelIgnorDisabled(2,level,"0.2");
            }else {

                // 新用户 费率 30%
                list = sysProductDao.getProductConfWithProductLevelAndDuefeeRate(2,"0.3");
            }

        }else {

            if (type == 1){
                /**
                 *   产品逻辑修改
                 *   1.新用户   120w   7天     30%前置服务费
                 *   2.老用户   120w   7-14天  20%前置服务费
                 *   3.如果用户被降额   降额后的产品是 20w 7-14天
                 *   则降额用户二次申请的时候  能够申请的产品为 40w 7-14天
                 *   三次申请的时候  能够申请的产品为 80w 7-14天
                 * */

                /**
                 *   产品逻辑修改 ------- 20181221修改
                 *   1.新用户   120w   7天     30%前置服务费
                 *   2.老用户   120w   7-14天  20%前置服务费
                 *   3.如果用户被降额   降额后的产品是 20w 7-14天
                 *   三次申请的时候  能够申请的产品为 80w 7-14天
                 * */

                /**
                 *   产品逻辑修改 ------- 20190103修改
                 *   1.新用户   120w   7天     30%前置服务费
                 *     且  第二次借款 费率为 27%  第三次借款 费率为 24%  第四及以后借款 费率为 20%
                 *
                 *   2.老用户   120w   7-14天  20%前置服务费
                 *   3.如果用户被降额   降额后的产品是 20w 7-14天  或者 10w 7-14天
                 *  之后只能申请到的产品为       80w 7-14天
                 *   如果用户借过120w的产品  还是能够借款120w！！！！！！！
                 *
                 *
                 * */


                /**
                 *   产品逻辑修改 ------- 20190108修改
                 *   1.新用户   120w   7天     30%前置服务费
                 *     且  第二次借款 费率为 27%  第三次借款 费率为 24%  第四及以后借款 费率为 20%
                 *
                 *   2.老用户   120w   7-14天  20%前置服务费
                 *   3.如果用户被降额   降额后的产品是 20w 7-14天  之后只能申请到的产品为       80w 7-14天
                 *
                 *   或者 10w 7-14天   之后只能申请到的产品为   40w 7-14天  再到  80w 7-14天
                 *
                 *   如果用户借过120w的产品  还是能够借款120w！！！！！！！
                 *
                 *
                 * */

                /**
                 *   产品逻辑修改 ------- 20190125修改
                 *   借款费率只和借款次数有关  第一次费率30% 第二次费率为 27%  第三次借款 费率为 24%  第四及以后借款 费率为 20%
                 *   1.新用户   120w   7天     30%前置服务费

                 *   2.如果用户被降额过 则只能申请 40w /7-14天产品
                 *     没有被降额，则能申请120w产品
                 * */

                /**
                 *   产品逻辑修改 ------- 20190304修改
                 *   借款金额不变（10w 20w 40w 80w 120w 150w 200w)
                 *   固定期限：30天
                 *   固定服务费：24%
                 * */

                /**
                 *   产品逻辑修改 ------- 20190327修改
                 *   借款金额不变（10w 20w 40w 80w 120w 150w 200w)
                 *   固定期限：35天
                 *   固定服务费：24% + 5x0.8% = 28%
                 * */

                /**
                 *   产品逻辑修改 ------- 20190401修改  改回之前版本
                 *   借款金额不变（10w 20w 40w 80w 120w 150w 200w)
                 *   固定期限：30天
                 *   固定服务费：24%
                 * */

                /**
                 *   产品逻辑修改 ------- 20190408修改
                 *   借款金额不变（10w 20w 40w 80w 120w 150w 200w)
                 *   固定期限：30天
                 *   固定服务费：19.2%( 0.62% x 30 )
                 * */

                /**
                 *   产品逻辑修改 ------- 20190522修改
                 *   借款金额不变（10w 16w 20w 30w 40w 80w 120w 150w 200w)
                 *   固定期限：30天
                 *   固定服务费：19.2%( 0.62% x 30 )
                 *   -3 200-40w  -4 400-80w  -5 80-16w
                 * */

                // 判断借款次数
                String duefeeRate = "0.192";

                // 判断是否是降额用户
                List<OrdOrder> hasLowLoanList = this.orderDao.has10WOR20WOR40WOR80WLoan(user.getUuid());
                if(hasLowLoanList.size() > 0){
                    //
                    if (level <= -4){
                        //如果是降额用户  则只能再借降额产品
                        list = sysProductDao.getProductWithProductLevel(level,duefeeRate);
                    }else {
                        //如果是降额用户  则只能再借40w产品   可能有提额用户 level = -4 则可借 40w和80w
                        list = sysProductDao.getProductWithProductLevelAndUserLevel(-3,level,duefeeRate);
                    }
                }
                else {
                    if (level == 0){
                        // 没有降额和提额过 并且不再分期白名单中 判断渠道
                        List<String> productUuidList = this.sysProductChannelDao.getProductChannel(user.getUserSource());
                        if (!CollectionUtils.isEmpty(productUuidList)){
                            SysProduct product = this.sysProductDao.getProductInfo(productUuidList.get(0));
                            if (product != null) {

                                list.add(product);
                                if (product.getProductType() == 1){

                                int term = product.getBorrowingTerm();
                                  // 还款计划   期数  应还款时间  还款金额
                                List<Map<String,String>>  repayPlans = new ArrayList<>();
                                Date lastDate = new Date();
                                Date nowDate = new Date();
                                // BigDecimal serviceFee = BigDecimal.ZERO;
                                for (int i = 1; i <= term; i++) {
                                    Map<String,String> plan = new HashMap<>();
                                    plan.put("billTerm",i+"");
                                    plan.put("billAmount",product.getTermAmount().toString().replace(".00",""));
                                    Date refundTime =  DateUtils.addDate(DateUtils.addDateWithMonth(nowDate,i),-1);
                                    log.info("第"+i+"期还款时间为"+DateUtils.DateToString(refundTime));
                                    plan.put("refundTime",DateUtils.DateToString(refundTime));
                                    // serviceFee = product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).add(serviceFee).setScale(2);
                                    // log.info("Sum of service fee for installment order for term: "+ i + " service fee" + serviceFee);
                                    if (i == term){
                                        lastDate = refundTime;
                                    }
                                    repayPlans.add(plan);
                                }
                                result.put("billsList",repayPlans);

                                int totalNum = DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;
                                log.info("当前时间:"+DateUtils.DateToString(nowDate)+"   最后还款时间:"+DateUtils.DateToString(lastDate) + "   总借款天数:" + totalNum);

                                BigDecimal serviceFee =  product.getBorrowingAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).setScale(2);
                                log.info("分期订单实际 服务费为"+serviceFee);

                                result.put("totalTerm",totalNum);
                                result.put("actualAmount",product.getBorrowingAmount().subtract(serviceFee).setScale(2).toString().replace(".00",""));
                                // 每月还款金额
                                result.put("termAmount",product.getTermAmount().toString().replace(".00",""));

                                //  用于公式计算  kudo的分期产品  公式里面需要用 本金
                                if (product.getUuid().equals("1004") || product.getUuid().equals("1005")){
                                    result.put("cacuAmount",product.getBorrowingAmount().toString().replace(".00",""));
                                }else {
                                    result.put("cacuAmount",product.getBorrowingAmount().toString().replace(".00",""));
                                }
                            }
                            //   else if (product.getProductType() >= 100){
                            //     BigDecimal serviceFee = BigDecimal.ZERO;
                            //     int totalNum = 0; //DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;

                            //     int term = product.getBorrowingTerm();
                            //     List<Map<String,String>>  repayPlans = new ArrayList<>();
                            //     Date nowDate = new Date();
                            //     // Date lastDate = new Date();
                            //     for (int i = 1; i <= term; i++) {
                            //         Map<String,String> plan = new HashMap<>();
                            //         plan.put("billTerm",i+"");
                            //         plan.put("billAmount",product.getTermAmount().toString().replace(".00",""));
                            //         Date refundTime =  DateUtils.addDate(DateUtils.addDate(nowDate,14 * (term+1-i)),-1);
                            //         log.info("Bill term of "+i+" Due Date: "+DateUtils.DateToString(refundTime));
                            //         plan.put("refundTime",DateUtils.DateToString(refundTime));
                                    
                            //         totalNum = DateUtils.differentDaysByMillisecond(nowDate,refundTime)+1;
                            //         log.info("Today is :"+DateUtils.DateToString(nowDate)+" with last date:"+DateUtils.DateToString(refundTime) + " borrowing days:" + totalNum);
                            //         serviceFee = product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).add(serviceFee).setScale(2);
                            //         log.info("Sum of service fee for installment order for term: "+ i + " service fee" + serviceFee);

                            //         // if (i == term){
                            //         //     lastDate = refundTime;
                            //         // }

                            //         repayPlans.add(plan);
                            //     }
                            //     result.put("billsList",repayPlans);

                            //     //int totalNum = DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;

                            //     //BigDecimal serviceFee =  product.getBorrowingAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).setScale(2);
                                
                            //     Date lastDate = DateUtils.addDate(DateUtils.addDate(nowDate,14 * term),-1);
                            //     result.put("totalTerm", DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1);
                            //     result.put("actualAmount",product.getBorrowingAmount().subtract(serviceFee).setScale(2).toString().replace(".00",""));
                            //     // 每月还款金额
                            //     result.put("termAmount",product.getTermAmount().toString().replace(".00",""));

                            //     //  用于公式计算  kudo的分期产品  公式里面需要用 本金
                            //     if (product.getUuid().equals("1006")){
                            //         result.put("cacuAmount",product.getBorrowingAmount().toString().replace(".00",""));
                            //     }else {
                            //         result.put("cacuAmount",product.getBorrowingAmount().toString().replace(".00",""));
                            //     }
                            //   }
                        }
                        else {
                            //  可能配置表中的产品 在产品表中被disable掉了
                            list = sysProductDao.getProductWithProductLevelAndUserLevel(2,level,duefeeRate);
                        }

                      }else {
                          // 产品配置表中没数据  走默认配置
                          list = sysProductDao.getProductWithProductLevelAndUserLevel(2,level,duefeeRate);
                      }

                    }
                    else  if (level < 0){
                        list = sysProductDao.getProductWithProductLevel(level,duefeeRate);
                    }
                    else {
                        // 如果不是降额  则可以借120w产品
                        list = sysProductDao.getProductWithProductLevelAndUserLevel(2,level,duefeeRate);
                    }
                }

            }else {
                // 分期订单
                StagingProductWhiteList productWhiteList = this.stagingProductWhiteListService.getProductListByUserUuid(baseRequest.getUserUuid());
//            StagingProduct product = this.stagingProductService.getProductByUuid(productWhiteList.getUuid());

                SysProduct product = this.sysProductDao.getProductInfo(productWhiteList.getProductUuid());
                list.add(product);

                int term = product.getBorrowingTerm();
                // 还款计划   期数  应还款时间  还款金额
                List<Map<String,String>>  repayPlans = new ArrayList<>();
                int totalNum = 0;
                Date nowDate = new Date();
                Date lastDate = new Date();
                // BigDecimal serviceFee = BigDecimal.ZERO;
                for (int i = 1; i <= term; i++) {
                    Map<String,String> plan = new HashMap<>();
                    plan.put("billTerm",i+"");
                    plan.put("billAmount",product.getTermAmount().toString().replace(".00",""));
                    Date  refundTime = DateUtils.getRefundDate(product.getProductType(), i, nowDate); // DateUtils.addDate(DateUtils.addDateWithMonth(nowDate,i),-1);
                    log.info("第"+i+"期还款时间为"+DateUtils.DateToString(refundTime));
                    plan.put("refundTime",DateUtils.DateToString(refundTime));
                    // totalNum = DateUtils.differentDaysByMillisecond(nowDate,refundTime)+1;
                    // serviceFee = product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).add(serviceFee).setScale(2);
                    if (i == term){
                        lastDate = refundTime;
                    }
                    repayPlans.add(plan);
                }
                result.put("billsList",repayPlans);

                totalNum = DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;
                log.info("当前时间:"+DateUtils.DateToString(nowDate)+"   最后还款时间:"+DateUtils.DateToString(lastDate) + "   总借款天数:" + totalNum);

                BigDecimal serviceFee =  product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).setScale(2);
                log.info("分期订单实际 服务费为"+serviceFee);

                result.put("totalTerm",totalNum);
                result.put("actualAmount",product.getBorrowingAmount().subtract(serviceFee).setScale(2).toString().replace(".00",""));
                // 每月还款金额
                result.put("termAmount",product.getTermAmount().toString().replace(".00",""));

                result.put("cacuAmount",product.getTermAmount().toString().replace(".00",""));
            }
        }
        result = getProductConfig(list,result);
        // 订单类型
        result.put("orderType",String.valueOf(type));
        //  渠道分期
        if(result.get("totalTerm") != null){
            result.put("orderType","2");
        }
        // 用户评分
        result.put("userScore",orderModelScoreService.getScoreByOrderNo(baseRequest.getUserUuid())+"");
        // 用户是否填写过调查问卷
        result.put("isNeedQA",checkUserHasQuestionnaire(baseRequest.getUserUuid()));
        return result;
    }


    

    // 检查用户是否填写过调查问卷
    public String checkUserHasQuestionnaire(String userUuid){

        UsrQuestionnaire usrQuestionnaire = new UsrQuestionnaire();
        usrQuestionnaire.setDisabled(0);
        usrQuestionnaire.setUserUuid(userUuid);
        usrQuestionnaire.setType(2);
        if (!CollectionUtils.isEmpty(usrQuestionnaireDao.scan(usrQuestionnaire))) {
            return "1";
        }
        UsrQuestionnaireAttach attach = new UsrQuestionnaireAttach();
        attach.setDisabled(0);
        attach.setUserUuid(userUuid);
        attach.setSourceType(0);
        List<UsrQuestionnaireAttach> scanList =  this.usrQuestionnaireAttactDao.scan(attach);
        if (CollectionUtils.isEmpty(scanList)){
            return "0";
        }
        return "1";
    }

    /**
     *   根据产品列表 获取产品配置
     * */
    public JSONObject getProductConfig(List<SysProduct> list,JSONObject result) throws ServiceException{

        if(CollectionUtils.isEmpty(list)){
            log.error("产品列表不存在");
            throw new ServiceException(ExceptionEnum.ORDER_PRODUCT_CONFIG_IS_NULL);
        }
        List<Map<String,String>> confList = new ArrayList<>();
        Map<String,String> confMap = new HashMap<>();
        Set<BigDecimal> money = new HashSet<>();
        Set<String> date = new HashSet<>();
        for(SysProduct item:list){
            money.add(item.getBorrowingAmount());
            date.add(item.getBorrowingTerm().toString());
            confMap = new HashMap<>();
            // ??
            BigDecimal borrowingAmount = item.getBorrowingAmount();
            confMap.put(MONEY,StringUtils.formatMoney(borrowingAmount.doubleValue()).replaceAll(",","."));// ????
            confMap.put(DATE,item.getBorrowingTerm().toString());
            // check product type object for detail explanation of product type
            confMap.put("Tenor", getProductTenor(item.getProductCode()));
            confMap.put("Period", getProductPeriod(item.getProductCode()));
            confMap.put(ARRIVED,StringUtils.formatMoney(borrowingAmount.subtract(item.getDueFee()).doubleValue()).replaceAll(",",".")); // ????
            confMap.put(DUEFEE,StringUtils.formatMoney(item.getDueFee().doubleValue()).replaceAll(",",".")); // ???
            confMap.put(MATURE,StringUtils.formatMoney(borrowingAmount.add(item.getInterest()).doubleValue()).replaceAll(",",".")); // ??????
            confMap.put(PRODUCT,item.getProductCode().toString()); // ????
            confMap.put("interest",StringUtils.formatMoney(Double.valueOf(item.getInterest().toString().replace(".00",""))).replaceAll(",",".")); // ??
            confMap.put("interestRate",item.getInterestRate().toString()); // ????
            confMap.put("dueFeeRate",item.getDueFeeRate().toString()); // ?????
            confMap.put("rate1",item.getRate1());
            confMap.put("rate2",item.getRate2());
            confMap.put("rate3",item.getRate3());
            confMap.put("rate4",item.getRate4());
            confMap.put("rate5",item.getRate5());
            confMap.put("rate6",item.getRate6());
            confList.add(confMap);
        }

        Set<BigDecimal> moneySet = sortByBigDecimalValue(money);
        List<String> moneyString = new ArrayList<>();
        for(BigDecimal item:moneySet){
            String temp = StringUtils.formatMoney(item.doubleValue()).replaceAll(",",".");
            moneyString.add(temp);
        }
        Set<String> dateSort =  sortByValue(date);
        result.put(MONEY,moneyString);
        result.put(DATE,dateSort);
        result.put("confList",confList);
        // 费率
        result.put("rate1",this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE1));
        result.put("rate2",this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE2));
        result.put("rate3",this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE3));
        result.put("rate4",this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE4));
        result.put("rate5",this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE5));
        result.put("rate6",this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_RATE_RATE6));
        // 金额回收率
        result.put("repayRate",repayRateService.getRepayRate());

        return result;
    }


    /**
     * ?? BigDecimal
     * @param set
     * @return
     */
    public static Set<BigDecimal> sortByBigDecimalValue(Set<BigDecimal> set){
        List<BigDecimal> setList= new ArrayList<BigDecimal>(set);
        Collections.sort(setList, new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return o1.compareTo(o2);
            }
        });
        set = new LinkedHashSet<BigDecimal>(setList);//??????LinkedHashSet
        return set;
    }


    /**
     * ?set??
     * @param set
     * @return
     */
    public static Set<String> sortByValue(Set<String> set){
        List<String> setList= new ArrayList<String>(set);
        Collections.sort(setList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Double o1Int = Double.parseDouble(o1);
                Double o2Int = Double.parseDouble(o2);
                if (o1Int < o2Int)
                    return -1;
                else if (o1Int > o2Int)
                    return 1;
                return 0;
            }
        });
        set = new LinkedHashSet<String>(setList);//??????LinkedHashSet
        return set;
    }

    public String getProductTenor(String productUuid){
        
        SysProduct product = new SysProduct();
        product.setDisabled(0);
        product.setUuid(productUuid);
        SysProduct result = sysProductDao.getProductInfo(productUuid);        
        if(result.getProductType() >= 300){
            return Integer.toString((result.getProductType() - 300) * result.getBorrowingTerm())+  " Tahun";
        }
        else if(result.getProductType() >= 200){
            return Integer.toString((result.getProductType() - 200)  * result.getBorrowingTerm()) + " Minggu";
        }
        else if(result.getProductType() >= 100){
            return Integer.toString((result.getProductType() - 100)  * result.getBorrowingTerm()) + " Bulan";
        }
        else if(result.getProductType() == 1){
            return Integer.toString(result.getBorrowingTerm()) + " Bulan";
        }
        else{
            return  Integer.toString(result.getBorrowingTerm()) + " Hari"; 
        }
    }

    public String getProductPeriod(String productUuid){
        
        SysProduct product = new SysProduct();
        product.setDisabled(0);
        product.setUuid(productUuid);
        SysProduct result = sysProductDao.getProductInfo(productUuid);        
        if(result.getProductType() >= 300){
            return Integer.toString((result.getProductType() - 300))+  " Tahun";
        }
        else if(result.getProductType() >= 200){
            return Integer.toString((result.getProductType() - 200)) + " Minggu";
        }
        else if(result.getProductType() >= 100){
            return Integer.toString((result.getProductType() - 100)) + " Bulan";
        }
        else if(result.getProductType() == 1){
            return "Bulan";
        }
        else{
            return  Integer.toString(result.getBorrowingTerm()) + " Hari"; 
        }
    }

}

