package com.yqg.service.loan.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.*;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.OrderNoCreator;
import com.yqg.mongo.dao.OrderUserDataDal;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.entity.OrderUserDataMongo;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.order.dao.*;
import com.yqg.order.entity.*;
import com.yqg.service.loan.request.RepayPlan;
import com.yqg.service.loan.request.RepayPlanRequest;
import com.yqg.service.loan.response.CheckRepayResponse;
import com.yqg.service.loan.response.LoanResponse;
import com.yqg.service.notification.request.NotificationRequest;
import com.yqg.service.notification.service.EmailNotificationService;
import com.yqg.service.notification.service.FcmNotificationService;
import com.yqg.service.order.OrdBillService;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.p2p.utils.P2PMD5Util;
import com.yqg.service.pay.RepayService;
import com.yqg.service.system.service.CouponService;
import com.yqg.service.system.service.SmsRemindService;
import com.yqg.service.system.service.StagingProductWhiteListService;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.ManCollectionOrderHistory;
import com.yqg.system.entity.StagingProductWhiteList;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.ManCollectionOrderDetailDao;
import com.yqg.user.dao.ManCollectionOrderHistorysDao;
import com.yqg.user.dao.RegisterDeviceInfoDao;
import com.yqg.user.dao.UsrProductRecordDao;
import com.yqg.user.entity.UsrProductRecord;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class LoanInfoService {
    @Autowired
    private OrdService ordService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private RepayService repayService;
    @Autowired
    private SmsRemindService smsRemindService;
    @Autowired
    private EmailNotificationService emailNotificationService;
    @Autowired
    private FcmNotificationService fcmNotificationService;
    @Autowired
    private OrdLoanAmoutRecordDao ordLoanAmoutRecordDao;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private OrdPaymentCodeDao ordPaymentCodeDao;
    @Autowired
    private OrdRepayAmoutRecordDao ordRepayAmoutRecordDao;
    @Autowired
    private OrdDelayRecordDao ordDelayRecordDao;
    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrderUserDataDal orderUserDataDal;
    @Autowired
    private RegisterDeviceInfoDao registerDeviceInfoDao;
    @Autowired
    private UserCallRecordsDal userCallRecordsDal;
    @Autowired
    private UserContactsDal userContactsDal;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private OrdBillDao ordBillDao;

    @Autowired
    private SysProductDao sysProductDao;

    @Autowired
    private OrdBillService ordBillService;

    @Autowired
    private StagingProductWhiteListService stagingProductWhiteListService;

    @Autowired
    private OrdServiceOrderDao ordServiceOrderDao;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ManCollectionOrderDetailDao manCollectionOrderDetailDao;

    @Autowired
    private ManCollectionOrderHistorysDao manCollectionOrderHistorysDao;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private P2PService p2PService;

    // 提交还款申请url
    @Value("${risk.checkUserLevel}")
    private String CHECK_USER_LEVEL_URL;

    @Autowired
    private UsrProductRecordDao usrProductRecordDao;

    // p2p url
    @Value("${p2p.host}")
    private String HOST_URL;
    @Value("${p2p.url.sendRepayPlan}")
    private String SEND_REPAY_PLAN_URL;

    @Transactional(rollbackFor = Exception.class)
    public void issuedSuccess(OrdOrder order, LoanResponse response) {

        log.info("实际打款成功=====================》订单号：" + order.getUuid());
        //防止多次并发记录history表和资金信息，该方法加锁处理
        boolean updateLock = redisClient.lock(RedisContants.ORDER_LOAN_ISSUED_SUCCESS_UPDATE);
        if (!updateLock) {
            return;
        }
        try {
            OrdOrder currentDbOrder = ordService.getOrderByOrderNo(order.getUuid());
            SysProduct product = this.sysProductDao.getProductInfo(order.getProductUuid());

            if(currentDbOrder.getStatus()==OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()||currentDbOrder.getStatus()==OrdStateEnum.RESOLVING_OVERDUE.getCode()){
                log.info("the loan status already update. orderNo: {}",order.getUuid());
                return;
            }
            OrdOrder entity = new OrdOrder();
            entity.setUuid(order.getUuid());
            entity.setStatus(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode());
            entity.setUpdateTime(new Date());
            entity.setLendingTime(new Date());
            entity.setRefundTime(DateUtils.addDate(new Date(),order.getBorrowingTerm()-1));
            this.ordService.updateOrder(entity);

            order.setStatus(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode());
            order.setUpdateTime(new Date());
            order.setLendingTime(new Date());
            // 应还款时间
            order.setRefundTime(DateUtils.addDate(new Date(),order.getBorrowingTerm()-1));
            this.ordService.addOrderHistory(order);

            OrdLoanAmoutRecord record = new OrdLoanAmoutRecord();
            record.setOrderNo(order.getUuid());
            record.setUserUuid(order.getUserUuid());
            record.setActualLoanAmout(this.calculateLoanAmount(order));
            record.setLoanChannel(OrdLoanChannelEnum.XENDIT.getMessage());
            if (!StringUtils.isEmpty(response.getDisburseChannel())) {
                record.setLoanChannel(response.getDisburseChannel());
            }
            record.setServiceFee(order.getServiceFee());
            this.ordLoanAmoutRecordDao.insert(record);

            // 如果是分期产品 生成账单
            // 生成分期账单
            if (order.getOrderType().equals(OrderTypeEnum.STAGING.getCode())){
                try {
                    int term = product.getBorrowingTerm();
                    Date nowDate = new Date();
                    List<OrdBill> billList = new ArrayList<>();
                    // 还款计划   期数  应还款时间  还款金额
                    for (int i = 1; i <= term; i++) {
                        OrdBill bill = new OrdBill();
                        bill.setUuid(OrderNoCreator.createOrderNo3());
                        bill.setOrderNo(order.getUuid());
                        bill.setUserUuid(order.getUserUuid());
                        bill.setStatus(OrdBillStatusEnum.RESOLVING.getCode());
                        bill.setBillTerm(i+"");
                        bill.setBillAmout(product.getTermAmount());
                        bill.setInterest(product.getInterest());
                        bill.setOverdueFee(product.getOverdueFee());
                        bill.setOverdueRate(product.getOverdueRate1());
                        bill.setProductUuid(product.getUuid());
                        Date  refundTime =  DateUtils.getRefundDate(product.getProductType(),i, nowDate);
                        bill.setRefundTime(refundTime);
                        //  如果是分期订单 则设置其应还款时间为 第一笔账单的应还款时间
                        if (i == 1){
                            entity.setRefundTime(refundTime);
                            this.ordService.updateOrder(entity);
                        }
                        this.ordBillDao.insert(bill);
                        billList.add(bill);
                    }

//                    TODO: p2p 待上线
//                    // 推送还款计划到p2p
//                    sendRepayPlanToP2P(order,billList);

                }catch (Exception e){
                    log.error("分期订单生成还款计划失败,orderNo: " + order.getUuid(), e);
                }
            }

            //  查询是否有服务费订单 如果有，改变服务费状态为待打款
            checkServiceOrder(order);

        }catch (Exception e){
            log.error("loan issued status update error ,orderNo=" + order.getUuid(), e);
        }finally {
            redisClient.unLock(RedisContants.ORDER_LOAN_ISSUED_SUCCESS_UPDATE);
        }
        sendIssuedSuccessMsg(order);
    }

    /**
     *   推送还款计划到p2p
     * */
    private void sendRepayPlanToP2P(OrdOrder order,List<OrdBill> billList) throws Exception{
        RepayPlanRequest repayPlanRequest = new RepayPlanRequest();

        List<RepayPlan>  repayPlanList = new ArrayList<>();
        for (OrdBill bill:billList){
            RepayPlan plan = new RepayPlan();
            plan.setPeriodNo(Integer.valueOf(bill.getBillTerm()));
            plan.setLendingTime(bill.getCreateTime());
            plan.setRefundIngTime(bill.getRefundTime());
            plan.setRefundIngAmount(bill.getBillAmout());
            repayPlanList.add(plan);
        }
        repayPlanRequest.setList(repayPlanList);
        repayPlanRequest.setCreditorNo(order.getUuid());

        // 签名 签名  (来源+债权编号+来源)进行md5编码 ,并转成大写 (channel+creditorNo+channel)
        String sign = P2PMD5Util.md5UpCase("Do-It" + order.getUuid() + "Do-It");
        repayPlanRequest.setSign(sign);

        String requestMsg = JsonUtils.serialize(repayPlanRequest);

        log.info("推送分期订单还款计划 请求的参数为{}", requestMsg);

        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestMsg);

        Request request = new Request.Builder()
                .url(HOST_URL + SEND_REPAY_PLAN_URL)
                .post(requestBody)
                .build();

        log.info("推送分期订单还款计划的订单号为：" + order.getUuid());

        Response response = httpClient.newCall(request).execute();
        log.info("推送分期订单还款计划 返回的response: " + response);
        if (response.isSuccessful()) {

            String responseStr = response.body().string();
            // 返回
            log.info("推送分期订单还款计划的具体返回:{}", JsonUtils.serialize(responseStr));
        }else {
            log.info("推送分期订单还款计划失败，订单号: {}", order.getUuid());
        }
    }

    /**
     *   查询订单对应的服务费订单
     * */
    public void checkServiceOrder(OrdOrder order){

        OrdServiceOrder scan = new OrdServiceOrder();
        scan.setDisabled(0);
        scan.setOrderNo(order.getUuid());
        List<OrdServiceOrder> scanList = this.ordServiceOrderDao.scan(scan);
        if (!CollectionUtils.isEmpty(scanList)){
            // 找到对应的服务费订单，更改状态为待打款
            OrdServiceOrder update = new OrdServiceOrder();
            if (scanList.get(0).getStatus() ==  OrdServiceOrderEnum.INIT.getCode()){
                update.setUuid(scanList.get(0).getUuid());
                update.setStatus(OrdServiceOrderEnum.LOAN.getCode());
                update.setUpdateTime(new Date());
                this.ordServiceOrderDao.update(update);
            }

        }else {
            log.info("未查询到该订单对应的服务费订单，对应订单号为:{}",order.getUuid());
        }
    }


    /**
     * recycle collection order.
     * @param orderNo
     */
    private void recycleCollection(String orderNo) {

        int count = manCollectionOrderDetailDao.recycleCollection(orderNo);
        log.info("reycle collection count is {}",count);

        ManCollectionOrderHistory history = new ManCollectionOrderHistory();
        history.setDisabled(1);
        history.setRemark("recycle bill order");
        history.setOrderUUID(orderNo);
        history.setSourceType(0);
        manCollectionOrderHistorysDao.insert(history);
    }

    public void repaymentSuccess(Object object, CheckRepayResponse response) {
        //防止并发插入多条记录，加锁
        boolean lock = redisClient.lock(RedisContants.ORDER_LOAN_PAYMENT_SUCCESS_UPDATE);
        if(!lock){
            return;
        }
        String orderNo = "";
        String userUuid = "";
        try {

            if (object instanceof OrdOrder){
                OrdOrder order = (OrdOrder) object;
                orderNo = order.getUuid();
                userUuid = order.getUserUuid();
                //检查订单当前状态,如果状态已经更新为还款成功，则退出[防止重复处理]
                OrdOrder currentDbOrder = ordService.getOrderByOrderNo(orderNo);
                if (currentDbOrder.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode() || currentDbOrder.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()) {
                    return;
                }
                if (order.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()) {
                    // 说明是正常还款
                    log.info("正常订单 实际还款成功=====================》订单号：" + orderNo);
                    OrdOrder entity = new OrdOrder();
                    entity.setUuid(orderNo);
                    entity.setStatus(OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode());
                    entity.setActualRefundTime(new Date());
                    entity.setUpdateTime(new Date());
                    this.ordService.updateOrder(entity);
                    order.setStatus(OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode());
                    order.setUpdateTime(new Date());
                    order.setActualRefundTime(new Date());
                    this.ordService.addOrderHistory(order);
                    repaySuccessRecord(order, response);
                } else {
                    log.info("逾期订单 实际还款成功=====================》订单号：" + orderNo);
                    OrdOrder entity = new OrdOrder();
                    entity.setUuid(orderNo);
                    entity.setStatus(OrdStateEnum.RESOLVED_OVERDUE.getCode());
                    entity.setActualRefundTime(new Date());
                    entity.setUpdateTime(new Date());
                    this.ordService.updateOrder(entity);
                    order.setStatus(OrdStateEnum.RESOLVED_OVERDUE.getCode());
                    order.setActualRefundTime(new Date());
                    order.setActualRefundTime(new Date());
                    this.ordService.addOrderHistory(order);

                    // 如果是逾期还款 则检查这个用户是不是在白名单中  如果存在 disables掉
                    checkUserWhiteList(order.getUserUuid());
                    // 根据逾期天数  进行用户降额
                    checkOrderWithDerating(order);

                    repaySuccessRecord(order, response);
                }
            }else if(object instanceof OrdBill){

                OrdBill bill = (OrdBill) object;
                orderNo = bill.getUuid();
                userUuid = bill.getUserUuid();
                //检查账单当前状态,如果状态已经更新为还款成功，则退出[防止重复处理]
                OrdBill currentDbOrder = this.ordBillService.getBillByBillNo(orderNo);
                if (currentDbOrder.getStatus() == OrdBillStatusEnum.RESOLVED.getCode() || currentDbOrder.getStatus() == OrdBillStatusEnum.RESOLVED_OVERDUE.getCode()) {
                    return;
                }
                if (bill.getStatus() == OrdBillStatusEnum.RESOLVING.getCode()) {
                    // 说明是正常还款
                    log.info("正常账单 实际还款成功=====================》账单号：" + orderNo);
                    OrdBill entity = new OrdBill();
                    entity.setUuid(orderNo);
                    entity.setStatus(OrdBillStatusEnum.RESOLVED.getCode());
                    entity.setActualRefundTime(new Date());
                    entity.setUpdateTime(new Date());
                    this.ordBillDao.update(entity);

                    repaySuccessRecord(bill, response);
                } else {
                    log.info("逾期账单 实际还款成功=====================》账单号：" + orderNo);
                    OrdBill entity = new OrdBill();
                    entity.setUuid(orderNo);
                    entity.setStatus(OrdBillStatusEnum.RESOLVED_OVERDUE.getCode());
                    entity.setActualRefundTime(new Date());
                    entity.setUpdateTime(new Date());
                    this.ordBillDao.update(entity);

                    repaySuccessRecord(bill, response);
                }

                //如果是最后一笔账单还款 还要更改订单的状态
                if (bill.getBillTerm().equals("3")){

                    OrdOrder orderEntity = new OrdOrder();
                    orderEntity.setUuid(bill.getOrderNo());
                    orderEntity.setStatus(OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode());
                    orderEntity.setActualRefundTime(new Date());
                    orderEntity.setUpdateTime(new Date());
                    this.ordService.updateOrder(orderEntity);

                    //  最后一笔账单逾期超过3天 再还款 取消这个用户的分期资格
                    if (bill.getStatus() == OrdBillStatusEnum.RESOLVING_OVERDUE.getCode()){
                        int dayNum = (int) DateUtils.daysBetween(DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"),DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                        log.info("最后一笔账单逾期还款，逾期天数为"+dayNum);
                        if (dayNum > 3){
                            checkUserWhiteList(bill.getUserUuid());
                        }
                    }

                }else {
                    // 不是最后一笔账单  更新订单的应还款时间为下一笔待还账单的应还款时间, 同时更新订单状态为待还款

                    OrdBill nextBill = this.ordBillDao.getRepayBillWithOrderNo(bill.getOrderNo(), orderNo);
                    if (nextBill != null){
                        log.info("next bill uuid is {}", nextBill.getUuid());
                        OrdOrder orderEntity = new OrdOrder();
                        orderEntity.setUuid(bill.getOrderNo());
                        orderEntity.setUpdateTime(new Date());
                        orderEntity.setRefundTime(nextBill.getRefundTime());
                        orderEntity.setStatus(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode() == nextBill.getStatus() ?
                                OrdStateEnum.RESOLVING_OVERDUE.getCode() : OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode());
                        this.ordService.updateOrder(orderEntity);
                    } else {
                        log.error("next bill not found");
                    }
                }
                recycleCollection(bill.getOrderNo());
            }

        } catch (Exception e) {
            log.error("订单还款成功后，修改订单状态,添加资金流水失败,orderNo: " + orderNo, e);
        }finally {
            redisClient.unLock(RedisContants.ORDER_LOAN_PAYMENT_SUCCESS_UPDATE);
        }
        sendRepaySuccessMsg(userUuid);
    }

    /**
     *   如果是逾期还款 则检查这个用户是不是在白名单中  如果存在 disables掉
     * */
    public void checkUserWhiteList(String userUuid){

        StagingProductWhiteList productWhiteList = this.stagingProductWhiteListService.getProductListByUserUuid(userUuid);
        if (productWhiteList != null) {
            StagingProductWhiteList update = new StagingProductWhiteList();
            update.setUuid(productWhiteList.getUuid());
            update.setDisabled(1);
            update.setRemark("有逾期还款记录，取消分期资格");
            this.stagingProductWhiteListService.updateWhiteList(update);
        }
    }

    /**
     *   判断逾期订单逾期天数  进行降额处理
     * */
    public void checkOrderWithDerating(OrdOrder order){

      try {

          /**
           *   判断是否手动降额过 如果有操作 则不进行降额处理
           * */
          UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());

          Integer borrowCount = order.getBorrowingCount();
          //  逾期天数
          int dayNum = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));
          BigDecimal amount = order.getAmountApply();
          // 判断复借次数
          if (borrowCount == 1){
              // 首借逾期
              if (dayNum > 0 || order.getOrderType().equals("1") || order.getOrderType().equals("2")){
//            通用_第2笔, 首借逾期,  首借逾期天数>0 , 首借额度 80&150的降80,600的降400
                 if (amount.compareTo(BigDecimal.valueOf(160000.00)) == 0 || amount.compareTo(BigDecimal.valueOf(300000.00)) == 0 ){
                     updateUserProductLevel(user,order,-5,"通用_第2笔");
                 }else if(amount.compareTo(BigDecimal.valueOf(1200000.00)) == 0){
                     updateUserProductLevel(user,order,-4,"通用_第2笔");
                     UsrUser newUser = this.usrService.getUserByUuid(order.getUserUuid());
                     updateUserToLowLevel(newUser,order,"通用_第2笔(第二次降额)");
                 }
              }
          }else if (borrowCount > 1){
              //   首先判断通用提额规则  ，提额未转化&上一笔逾期 ,取消提额

              // 判断是否是提额用户
              if (user.getProductLevel() > 0 && dayNum > 0 && checkUserIsUpUserButNotConversion(user,order) || order.getOrderType().equals("1") || order.getOrderType().equals("2") ){
                  // 查询提额记录  获取之前的额度
                  List<UsrProductRecord> records = this.usrProductRecordDao.getUpRecord(user.getUuid(),order.getUuid(),user.getProductLevel());
                  if (!CollectionUtils.isEmpty(records)){
                      UsrProductRecord record = records.get(0);
                      updateUserProductLevel(user,order,record.getLastProductLevel(),"通用_提额");
                  }else {
                      log.error("提额用户未查询对应的提额记录");
                  }
              }

              if (user.getProductLevel() == -4  && dayNum > 0 && checkUserIsUpUserButNotConversion(user,order) || order.getOrderType().equals("1") || order.getOrderType().equals("2")){
                  // 查询提额记录  获取之前的额度
                  List<UsrProductRecord> records = this.usrProductRecordDao.getUpRecord(user.getUuid(),order.getUuid(),user.getProductLevel());
                  if (!CollectionUtils.isEmpty(records)){
                      UsrProductRecord record = records.get(0);
                      updateUserProductLevel(user,order,record.getLastProductLevel(),"通用_提额");
                  }else {
                      log.error("提额用户未查询对应的提额记录");
                  }
              }

              //     通用_第3笔及以后 , 上一笔逾期天数>1, 当前产品级别上降1级
              //     2019-07-22修改：  上一笔逾期天数>1  修改为   上一笔逾期天数>0或上一笔订单是展期订单(ordeType 1 / 2)
              if (dayNum > 0 || order.getOrderType().equals("1") || order.getOrderType().equals("2")){
                  if (amount.compareTo(BigDecimal.valueOf(400000.00)) == 0 || amount.compareTo(BigDecimal.valueOf(300000.00)) == 0 ){
                      updateUserProductLevel(user,order,-5,"通用_第3笔及以后");
                  }else {
                      updateUserToLowLevel(user,order,"通用_第3笔及以后");
                      UsrUser newUser = this.usrService.getUserByUuid(order.getUserUuid());
                      updateUserToLowLevel(newUser,order,"通用_第3笔及以后(第二次降额)");
                  }
              }
          }

          if (dayNum == 1){

              if (amount.compareTo(BigDecimal.valueOf(400000.00)) == 0){
                  // 200降额  上一笔逾期天数=1 , 当前产品级别上降1级
//                  updateUserToLowLevel(user,order,"200降额");
                  updateUserProductLevel(user,order,-5,"200降额");
              }
          }

      }catch (Exception e){
             log.error("降额处理异常",e);
      }
    }

    public static void main(String[] args) {
        if (1 > 0){
            log.info("312321");
//            return;
        }
        log.info("3213123");
    }

    /**
     *  判断用户是否是提额  未转换
     */
    public Boolean  checkUserIsUpUserButNotConversion(UsrUser user,OrdOrder order){
        Boolean isNotConversion = false;
        Integer productLevel = user.getProductLevel();
        switch (productLevel) {
            case -4: // 80w  400
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(800000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
            case 3: // 150w  750
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(1500000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
            case 4: // 200w  1000
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(2000000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
            case 5: // 240w  1200
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(2400000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
            case 6: // 300w  1500
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(3000000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
            case 7: // 360w  1800
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(3600000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
            case 8: // 400w  2000
                if (order.getAmountApply().compareTo((BigDecimal.valueOf(4000000.00))) < 0) {
                    isNotConversion = true;
                }
            break;
        }
            return isNotConversion;
    }


    // 在当前用户的级别上再降一个级别，目前最低为-5-》16w 级别
    public void updateUserToLowLevel(UsrUser user,OrdOrder order,String ruleName){
        Integer productLevel = user.getProductLevel();
        switch (productLevel){
            case -5: // 16w  80
                break;
            case -3: // 40w  200
                updateUserProductLevel(user,order,-5,ruleName);
                break;
            case -4: // 80w  400
                updateUserProductLevel(user,order,-3,ruleName);
                break;
            case 0: // 120w  600
                updateUserProductLevel(user,order,-4,ruleName);
                break;
            case 3: // 150w  750
                updateUserProductLevel(user,order,0,ruleName);
                break;
            case 4: // 200w  1000
                updateUserProductLevel(user,order,3,ruleName);
                break;
            case 5: // 240w  1200
                updateUserProductLevel(user,order,4,ruleName);
                break;
            case 6: // 300w  1500
                updateUserProductLevel(user,order,5,ruleName);
                break;
            case 7: // 360w  1800
                updateUserProductLevel(user,order,6,ruleName);
                break;
            case 8: // 400w  2000
                updateUserProductLevel(user,order,7,ruleName);
                break;
        }

    }

    // 更新用户级别
    public void updateUserProductLevel(UsrUser userUser,OrdOrder order,Integer productLevel,String ruleName){

        if (userUser.getProductLevel() != productLevel){
            // 1.添加用户提额记录
            UsrProductRecord record = new UsrProductRecord();
            record.setUserUuid(userUser.getUuid());
            record.setOrderNo(order.getUuid());
            record.setRuleName(ruleName);
            record.setLastProductLevel(userUser.getProductLevel());
            record.setCurrentProductLevel(productLevel);
            this.usrProductRecordDao.insert(record);

            // 2.更新用户额度
            userUser.setUpdateTime(new Date());
            userUser.setProductLevel(productLevel);
            this.usrService.updateUser(userUser);
        }
    }


    /***
     *  打款成功 发送短信 【异步单独线程发送】
     * @param order
     */
    private void sendIssuedSuccessMsg(OrdOrder order) {
        executorService.submit(()->{
            try {
                //budi: add fcm & email notif when disburse completed
                NotificationRequest notificationRequest = new NotificationRequest();
                UsrUser userUser = usrService.getUserByUuid(order.getUserUuid());
                String fcmToken = registerDeviceInfoDao.getFcmTokenByUserUuid(order.getUserUuid());
                String content = String.format("Selamat! Dana pinjaman sudah dicairkan ke rekening Anda, silakan cek aplikasi untuk info jatuh tempo. Dukung kami dengan rating bintang 5 di Playstore!");
                ArrayList<String> registration_ids = new ArrayList<String>();
                registration_ids.add(fcmToken);                
                notificationRequest.setRegistration_ids(registration_ids);
                notificationRequest.setSubject("Dana Anda sudah cair!");
                notificationRequest.setMessage(content);
                fcmNotificationService.SendNotification(notificationRequest);
                notificationRequest.setTo(DESUtils.decrypt(userUser.getEmailAddress()));
                emailNotificationService.SendNotification(notificationRequest);
            } catch (Exception e) {
                log.error("send LOAN_SUCCESS_REMIND message error ,orderNo: " + order.getUuid(), e);
            }
        });

    }

    /**
     * 还款成功短信【异步单独线程发送】
     * @param userUuid
     */
    private void sendRepaySuccessMsg(String userUuid){
       executorService.submit(()->{
           UsrUser userUser  = usrService.getUserByUuid(userUuid);
           // 发送提醒短信
           String content = "<Do-It> Halo, tagihan Anda sudah lunas. kredit anda karena bagus, pinjaman Anda selanjutnya akan dibebaskan dari verif. Terima ini uang di cap goo.gl/RLfJ8Z.";
           this.smsRemindService.sendSmsToUser(content,userUser,7);
       });
    }

    /**
     * 计算放款金额
     */
    public String calculateLoanAmount(OrdOrder ordOrder) {

        if (ordOrder.getOrderType().equals("3")){
            // 分期账单
            Date nowDate = new Date();
            SysProduct product = this.sysProductDao.getProductInfo(ordOrder.getProductUuid());
            int term = product.getBorrowingTerm();
            Date lastDate = new Date();

            // int totalNum = 0; //DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;
            // BigDecimal serviceFee = BigDecimal.ZERO; //product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).setScale(2);
            // 还款计划   期数  应还款时间  还款金额
            for (int i = 1; i <= term; i++) {
                // Date  refundTime =  DateUtils.addDate(DateUtils.addDateWithMonth(nowDate,i),-1);
                Date  refundTime = DateUtils.getRefundDate(product.getProductType(), i, nowDate); 

                // totalNum = DateUtils.differentDaysByMillisecond(nowDate,refundTime)+1;
                // serviceFee = product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).add(serviceFee).setScale(2);
                
                if (i == term){
                    lastDate = refundTime;
                }
            }

            int totalNum = DateUtils.differentDaysByMillisecond(nowDate,lastDate)+1;
            BigDecimal serviceFee = product.getTermAmount().multiply(product.getDueFeeRate()).multiply(BigDecimal.valueOf(totalNum)).setScale(2);

            log.info("当前时间:"+DateUtils.DateToString(nowDate)+"   最后还款时间:"+DateUtils.DateToString(lastDate) + "   总借款天数:" + totalNum);
            log.info("分期订单实际打款 服务费为"+serviceFee);

            // 更新总还款天数 和 服务费
            OrdOrder update = new OrdOrder();
            update.setUuid(ordOrder.getUuid());
            update.setDisabled(0);
            update.setTotalTerm(totalNum);
            update.setServiceFee(serviceFee);
            update.setApprovedAmount(ordOrder.getAmountApply().subtract(serviceFee).setScale(2).toString());
            this.ordService.updateOrder(update);

            BigDecimal totolAmout = ordOrder.getAmountApply();
            return totolAmout.subtract(serviceFee) + "";

        }else {
            BigDecimal totolAmout = ordOrder.getAmountApply();
            BigDecimal serviceFee = ordOrder.getServiceFee();
            return totolAmout.subtract(serviceFee) + "";
        }
    }




    /**
     * 还款成功，插入还款资金流水
     */
    @Transactional
    private void repaySuccessRecord(Object object, CheckRepayResponse response) throws Exception {
        String orderNo = "";
        String userUuid = "";
        BigDecimal actualDisbursedAmount = BigDecimal.ZERO;
        BigDecimal serviceFee = BigDecimal.ZERO;
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal interestRatio = new BigDecimal("0.001");     //0.1%
       
        if (object instanceof OrdOrder){
            OrdOrder order = (OrdOrder) object;
            orderNo = order.getUuid();
            userUuid = order.getUserUuid();
            actualDisbursedAmount = new BigDecimal(order.getApprovedAmount());
            //Interest is calculated based on amount apply
            interest = order.getAmountApply().multiply(interestRatio).multiply(BigDecimal.valueOf(order.getBorrowingTerm()));
            //Actual serviceFee is ordOrder serviceFee - Interest
            serviceFee = order.getServiceFee().subtract(interest);
        }else if(object instanceof OrdBill){
            OrdBill bill = (OrdBill) object;
            orderNo = bill.getUuid();
            userUuid = bill.getUserUuid();
            //Get disbursed amount and service fee from parent order, divided by bill term
            OrdOrder billOrder = new OrdOrder();
            billOrder.setUuid(bill.getOrderNo());
            billOrder.setDisabled(0);
            List<OrdOrder> scanList = this.ordDao.scan(billOrder);
            if (!CollectionUtils.isEmpty(scanList)) {
                BigDecimal outstandingAmount = scanList.get(0).getAmountApply().subtract(
                    (
                        new BigDecimal(bill.getBillTerm()).subtract(BigDecimal.ONE)   //billTerm-1
                    ).multiply(bill.getBillAmout()) //(billTerm-1)*bill amount -> ex: 0, 400k, 800k
                ); //amountApply - ((billTerm-1)*billAmount) -> ex: 1200k, 800k, 400k
                //Interest is calculated based on amount apply
                //TotalTerm is only available for installment
                log.info("Outstanding amount: {}", outstandingAmount);
                interest = outstandingAmount
                    .multiply(interestRatio)
                    .multiply(BigDecimal.valueOf(scanList.get(0).getTotalTerm())
                        .divide(BigDecimal.valueOf(scanList.get(0).getBorrowingTerm()),2,BigDecimal.ROUND_HALF_UP));
                log.info("Interest: {}", interest);
                
                //For installment, borrowing term in ordOrder contains installment count (ex: 3 times), borrowing duration is stored in TotalTerm
                //Notes that for normal order, borrowingTerm in ordOrder contains the borrowing duration (ex: 30 days), TotalTerm is null
                actualDisbursedAmount = new BigDecimal(scanList.get(0).getApprovedAmount())
                    .divide(BigDecimal.valueOf(scanList.get(0).getBorrowingTerm()),2,BigDecimal.ROUND_HALF_UP);
                log.info("Actual disburse amount: {}", actualDisbursedAmount);
                //There is no service fee on ordBill. Service Fee can be calculated from ordOrder serviceFee divided by borrowingTerm
                //This service fee comprise of the actual serviceFee + interest
                BigDecimal billTermServiceFee = scanList.get(0).getServiceFee()
                    .divide(BigDecimal.valueOf(scanList.get(0).getBorrowingTerm()),2,BigDecimal.ROUND_HALF_UP);
                log.info("Bill term service fee: {}", billTermServiceFee);
                //Actual serviceFee is ordBill serviceFee - interest
                serviceFee = billTermServiceFee.subtract(interest);
                log.info("Actual service fee: {}", serviceFee);
                //budi: diskon 50ribu if no overdue in all term
                if(ordBillDao.getOverdueBillWithOrderNo(bill.getOrderNo()) == null && bill.getBillTerm().equals("3")) {
                    serviceFee = serviceFee.subtract(BigDecimal.valueOf(50000));
                    log.info("Final service fee: {}", serviceFee);
                }
            }
        }
        OrdRepayAmoutRecord record = new OrdRepayAmoutRecord();
        record.setOrderNo(orderNo);
        record.setUserUuid(userUuid);
        if (!StringUtils.isEmpty(response.getDepositMethod())){
            record.setRepayMethod(response.getDepositMethod());
        }
        if (!StringUtils.isEmpty(response.getTransactionId())){
            record.setTransactionId(response.getTransactionId());
        }

        List<OrdPaymentCode> codeList = new ArrayList<OrdPaymentCode>();
        //bug fix ahalim: OVO doesn't have payment code, so will need to get by orderno
        if (response.getDepositMethod().equals(OrdPaymentCode.DepositChannel.OVO.name())) {
            codeList = this.ordPaymentCodeDao.getOrderPaymentCodeByOrderNoDesc(orderNo);
        }
        else if (!StringUtils.isEmpty(response.getPaymentCode())){
            // bug fix budi 20191119: memperbaiki isi dari tbl ordRepayAmoutRecord 
            // agar mengambil record yg created date-nya paling akhir.
            codeList = this.ordPaymentCodeDao.getOrderPaymentCodeDesc(orderNo, response.getPaymentCode());
        }
        if (!CollectionUtils.isEmpty(codeList)){
            OrdPaymentCode paymentCode = codeList.get(0);
            if (!StringUtils.isEmpty(paymentCode.getActualRepayAmout())){
                record.setActualRepayAmout(paymentCode.getActualRepayAmout());
            }
            //ahalim: Calculate interest based on outstanding amount
            //if (!StringUtils.isEmpty(paymentCode.getInterest())){
            //    record.setInterest(paymentCode.getInterest());;
            //}
            if (!StringUtils.isEmpty(paymentCode.getOverDueFee())){
                record.setOverDueFee(paymentCode.getOverDueFee());;
            }
            if (!StringUtils.isEmpty(paymentCode.getPenaltyFee())){
                record.setPenaltyFee(paymentCode.getPenaltyFee());;
            }
            if (!StringUtils.isEmpty(paymentCode.getCodeType())){
                record.setRepayChannel(paymentCode.getCodeType());;
            }
            record.setActualDisbursedAmount(actualDisbursedAmount);
            record.setInterest(interest.toString());
            record.setServiceFee(serviceFee);
            record.setStatus(OrdRepayAmountRecordStatusEnum.WAITING_REPAYMENT_TO_RDN.toString());

            if (object instanceof OrdOrder){
                OrdOrder order = (OrdOrder) object;
                doLoanExtension(order,paymentCode,response.getAmount());

                //如果是订单还款 确定是否使用了优惠券
                if (!StringUtils.isEmpty(paymentCode.getCouponUuid())){
                    CouponRecord couponRecord = this.couponService.getCouponInfoWithUuid(paymentCode.getCouponUuid());
                    if (couponRecord != null){
                        couponRecord.setStatus(1);
                        couponRecord.setUpdateTime(new Date());
                        couponRecord.setUsedDate(new Date());
                        this.couponService.updateCoupon(couponRecord);
                    }
                }

                // 查询判断用户是否需要降额 适用对象为首借50rmb 80rmb 100rmb或150rmb，截止规则上线还没有进行复借的用户
                if (order.getBorrowingCount() == 1
                        && (order.getAmountApply().compareTo(BigDecimal.valueOf(100000)) == 0 ||
                        order.getAmountApply().compareTo(BigDecimal.valueOf(160000)) == 0 ||
                order.getAmountApply().compareTo(BigDecimal.valueOf(200000)) == 0  ||
                order.getAmountApply().compareTo(BigDecimal.valueOf(300000)) == 0))
                checkUserProductLevel(order,order.getUserUuid());
            }


        }else {
            log.error("paymentCode not found");
            throw new ServiceException(ExceptionEnum.ORDER_REPAYMENT_CODE_NOT_FOUND);
        }
        this.ordRepayAmoutRecordDao.insert(record);
    }

    /**
     *  调用risk服务，检查用户是否需要降额
     * */
    public void checkUserProductLevel(OrdOrder order,String userUuid){

      try {
          String requestUrl = CHECK_USER_LEVEL_URL+"?userUuid="+userUuid;
          log.info("查询用户是否降额 请求: {}", requestUrl);
          Request request = new Request.Builder()
                  .url(requestUrl)
                  .get()
                  .build();
          Response response = httpClient.newCall(request).execute();
          if(response.isSuccessful())
          {
              String  responseStr = response.body().string();
              // 查询用户是否降额
              log.info("查询用户是否降额 请求后返回:{}", JsonUtils.serialize(responseStr)+"订单号为："+order.getUuid());
              JSONObject object = JSONObject.parseObject(responseStr);
              if(object.get("data") != null){
                  JSONObject data  = (JSONObject)object.get("data");
                  if (data != null){
                      String result = (String) data.get("result");

                      // 1.更新用户额度
                      UsrUser userUser = this.usrService.getUserByUuid(userUuid);
                      userUser.setUpdateTime(new Date());

                      // 2.添加用户提额记录
                      UsrProductRecord record = new UsrProductRecord();
                      record.setUserUuid(userUuid);
                      record.setOrderNo(order.getUuid());
                      record.setRuleName("rule hit change");
                      record.setLastProductLevel(userUser.getProductLevel());

                      if (result.equals("HIT")){

                          //  逾期天数
                          int dayNum = (int) DateUtils.daysBetween(DateUtils.formDate(order.getRefundTime(), "yyyy-MM-dd"), DateUtils.formDate(new Date(), "yyyy-MM-dd"));
                          if (order.getAmountApply().compareTo(BigDecimal.valueOf(160000)) == 0 ){
                              if (dayNum == 0){
//                          首借80rmb,命中复借提额至200rmb规则,且首借80rmb的逾期天数=0的用户 全部降额至80rmb(第二笔借80rmb);
                                  userUser.setProductLevel(-5);
                                  this.usrService.updateUser(userUser);
                                  record.setCurrentProductLevel(-5);
                                  record.setRuleName("首借80rmb,命中复借提额至200rmb规则,且首借80rmb的逾期天数=0");
                                  this.usrProductRecordDao.insert(record);
                                  log.info("命中降额规则，用户降额到16W，用户id为:"+userUuid);
                              }else if(dayNum < 0){
//                           首借80rmb,命中复借提额至200rmb规则,且首借80rmb的逾期天数<0的用户 全部复借200rmb(第二笔借200rmb);
                                  userUser.setProductLevel(-3);
                                  this.usrService.updateUser(userUser);
                                  record.setCurrentProductLevel(-3);
                                  record.setRuleName(" 首借80rmb,命中复借提额至200rmb规则,且首借80rmb的逾期天数<0");
                                  this.usrProductRecordDao.insert(record);
                                  log.info("命中提额规则，用户提额到40W，用户id为:"+userUuid);
                              }
                          }else {
                              //  命中 将用户提额到40w（200RMB)  添加提额记录
                              userUser.setProductLevel(-3);
                              this.usrService.updateUser(userUser);
                              record.setCurrentProductLevel(-3);
                              this.usrProductRecordDao.insert(record);
                              log.info("命中提额规则，用户提额到40W，用户id为:"+userUuid);
                          }

                      }else if (result.equals("NOT_HIT")){
                          //  未命中 将用户降额到16w（80RMB)  添加降额记录
                          userUser.setProductLevel(-5);
                          this.usrService.updateUser(userUser);
                          record.setCurrentProductLevel(-5);
                          this.usrProductRecordDao.insert(record);
                          log.info("命中降额规则，用户降额到16W，用户id为:"+userUuid);
                      }
                  }
              }

          }else {
              log.error("查询用户是否需要降额失败："+response.message());
          }
      }catch (Exception e){
          log.error("查询用户是否需要降额异常",e);
      }
    }

    /***
     * 还款后展期处理
     */
    @Transactional
    private void doLoanExtension(OrdOrder order, OrdPaymentCode paymentCode,String actualRepayAmount) throws Exception{

        try {
            if (!StringUtils.isEmpty(paymentCode.getAmountApply())){
                if (!order.getOrderType().equals("1")){
                    // 展期订单记录表
                    OrdDelayRecord deleyRecord = new OrdDelayRecord();
                    deleyRecord.setOrderNo(order.getUuid());
                    deleyRecord.setDisabled(0);
                    List<OrdDelayRecord> deleyRecordList =  this.ordDelayRecordDao.scan(deleyRecord);
                    if (!CollectionUtils.isEmpty(deleyRecordList)){

                        BigDecimal actualNum  = new BigDecimal(actualRepayAmount);
                        if (!StringUtils.isEmpty(paymentCode.getCouponUuid())){
                            CouponRecord couponRecord = this.couponService.getCouponInfoWithUuid(paymentCode.getCouponUuid());
                            if (couponRecord != null){
                                actualNum = new BigDecimal(paymentCode.getActualRepayAmout()).add(couponRecord.getMoney()).setScale(2);
                            }
                        }
                        // Comparison of actual repayment amount and repayable amount
                        if (actualNum.compareTo(new BigDecimal(this.repayService.calculateRepayAmountWithDate(order,paymentCode.getCreateTime()))) < 0 ){

                            // 生成展期订单
                            OrdOrder newOrder = new OrdOrder();
                            newOrder.setChannel(order.getChannel());// ????
                            String newOrderNo = OrderNoCreator.createOrderNo();
                            newOrder.setUuid(newOrderNo);
                            newOrder.setCreateTime(new Date());
                            newOrder.setUpdateTime(new Date());
                            newOrder.setLendingTime(new Date());
                            newOrder.setChannel(order.getChannel());
                            newOrder.setPayChannel(order.getPayChannel());
                            newOrder.setBorrowingCount(order.getBorrowingCount());
                            newOrder.setOrderPositionId(order.getOrderPositionId());
                            newOrder.setUserBankUuid(order.getUserBankUuid());
                            newOrder.setOrderStep(order.getOrderStep());
                            newOrder.setStatus(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode());
                            newOrder.setInterest(BigDecimal.valueOf(0));// ??????? ??
                            newOrder.setUserUuid(order.getUserUuid());// ????uuid
                            newOrder.setApplyTime(new Date());// ????
                            newOrder.setAmountApply(order.getAmountApply()
                                    .subtract(deleyRecordList.get(0).getRepayNum()));// ????
                            //  BUGFIX 20190114:
//                        展期后 生成50w的paymentCode  又生成一笔新的金额展期记录  uodate掉了之前的展期记录  导致展期订单生成金额错误
                            if(!StringUtils.isEmpty(paymentCode.getPrincipal())){
                                //  如果是展期 用 借款金额 减去 实际还款本金
                                newOrder.setAmountApply(order.getAmountApply()
                                        .subtract(new BigDecimal(paymentCode.getPrincipal())));
                            }
                            Integer day =  deleyRecordList.get(0).getDelayDay();
                            newOrder.setBorrowingTerm(day);// ????
                            // 应还款时间
                            newOrder.setRefundTime(DateUtils.addDate(new Date(),day-1));
                            newOrder.setServiceFee(BigDecimal.valueOf(0));// ???
                            newOrder.setOrderType("1");
                            this.ordDao.insert(newOrder);

                            // 原始订单改为 部分结清状态
                            OrdOrder entity = new OrdOrder();
                            entity.setUuid(order.getUuid());
                            entity.setUpdateTime(new Date());
                            entity.setOrderType("2");
                            this.ordDao.update(entity);

                            OrdDelayRecord update = new OrdDelayRecord();
                            update.setUuid(deleyRecordList.get(0).getUuid());
                            update.setType("2");
                            update.setDelayOrderNo(newOrderNo);
                            update.setUpdateTime(new Date());
                            this.ordDelayRecordDao.update(update);

                            // 处理展期订单数据
                            copyDataToDelayOrder(order,newOrderNo);

//                          TODO: p2p 待上线
//                          UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
//                          this.p2PService.sendOrderInfoToFinancial(newOrder, user);
                        }

                    }else {
                        log.error("展期订单记录表中未找到该还款订单");
                    }
                }
            }
        }catch (Exception e){
            log.info("展期异常",e);
        }

    }

    public void copyDataToDelayOrder(OrdOrder ordOrder,String delayOrderNo) {

        // ????
        OrderUserDataMongo mongo = new OrderUserDataMongo();
        mongo.setOrderNo(ordOrder.getUuid());
        mongo.setUserUuid(ordOrder.getUserUuid());
        List<OrderUserDataMongo> mongoList = this.orderUserDataDal.find(mongo);
        if (!CollectionUtils.isEmpty(mongoList)) {
            for (OrderUserDataMongo entity : mongoList) {
                OrderUserDataMongo newMongo = new OrderUserDataMongo();
                newMongo.setUserUuid(ordOrder.getUserUuid());
                newMongo.setOrderNo(delayOrderNo);
                newMongo.setInfoType(entity.getInfoType());
                newMongo.setData(entity.getData());
                newMongo.setStatus(entity.getStatus());
                this.orderUserDataDal.insert(newMongo);
            }
        }

        // ????
        UserCallRecordsMongo callRecordsMongo = new UserCallRecordsMongo();
        callRecordsMongo.setOrderNo(ordOrder.getUuid());
        callRecordsMongo.setUserUuid(ordOrder.getUserUuid());
        List<UserCallRecordsMongo> callRecordsMongoList = this.userCallRecordsDal.find(callRecordsMongo);
        if (!CollectionUtils.isEmpty(callRecordsMongoList)) {
            for (UserCallRecordsMongo entity : callRecordsMongoList) {
                UserCallRecordsMongo newMongo = new UserCallRecordsMongo();
                newMongo.setUserUuid(ordOrder.getUserUuid());
                newMongo.setOrderNo(delayOrderNo);
                newMongo.setData(entity.getData());
                this.userCallRecordsDal.insert(newMongo);
            }
        }

        // ???
        UserContactsMongo userContactsMongo = new UserContactsMongo();
        userContactsMongo.setOrderNo(ordOrder.getUuid());
        userContactsMongo.setUserUuid(ordOrder.getUserUuid());
        List<UserContactsMongo> userContactsMongoList = this.userContactsDal.find(userContactsMongo);
        if (!CollectionUtils.isEmpty(userContactsMongoList)) {
            for (UserContactsMongo entity : userContactsMongoList) {
                UserContactsMongo newMongo = new UserContactsMongo();
                newMongo.setUserUuid(ordOrder.getUserUuid());
                newMongo.setOrderNo(delayOrderNo);
                newMongo.setData(entity.getData());
                this.userContactsDal.insert(newMongo);
            }
        }
    }

}
