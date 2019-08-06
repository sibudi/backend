package com.yqg.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.enums.order.OrdServiceOrderEnum;
import com.yqg.config.GlobalAccount;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.LoanDisburseTypeEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.order.dao.OrdLoanAmoutRecordDao;
import com.yqg.order.dao.OrdServiceOrderDao;
import com.yqg.order.entity.OrdLoanAmoutRecord;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdServiceOrder;
import com.yqg.service.loan.response.LoanResponse;
import com.yqg.service.system.service.SysThirdLogsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class LoanCoverChargeService {

    @Value("${pay.loanExpenseUrl}")
    private String LOAN_EXPENSE_URL;
    @Value("${pay.cheakLoanUrl}")
    private String LOAN_URL_CHEAK;
    @Value("${pay.transferOrderUrl}")
    private String TRANSFER_URL;
    @Value("${pay.token}")
    private String PAY_TOKEN;


    @Autowired
    private SysThirdLogsService sysThirdLogsService;
    @Autowired
    private OkHttpClient httpClient;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrdServiceOrderDao ordServiceOrderDao;
    @Autowired
    private OrdLoanAmoutRecordDao loanAmoutRecordDao;
    @Autowired
    private PayService payService;

    /**
     * 生成服务费订单
     */
    public String createServiceOrder(OrdOrder order, String disburseChannel) {

        String serviceOrderNo = "04" + order.getUuid().substring(2);

        // 防止打款失败的订单再次打款的时候，会再次生成服务费订单，所以生成订单前，再次检查数据库
        OrdServiceOrder scan = new OrdServiceOrder();
        scan.setDisabled(0);
        scan.setUuid(serviceOrderNo);
        scan.setOrderNo(order.getUuid());
        List<OrdServiceOrder> scanList = this.ordServiceOrderDao.scan(scan);
        if (CollectionUtils.isEmpty(scanList)){
            // 首次打款 未生成对应的服务费订单
            OrdServiceOrder entity = new OrdServiceOrder();
            try {
                entity.setUuid(serviceOrderNo);
                entity.setCreateTime(new Date());
                entity.setUpdateTime(new Date());
                entity.setOrderNo(order.getUuid());
                entity.setUserUuid(order.getUserUuid());
                entity.setServiceFee(order.getServiceFee());
                entity.setDisburseChannel(disburseChannel);
                entity.setStatus(OrdServiceOrderEnum.INIT.getCode());//等待推送
                ordServiceOrderDao.insert(entity);
                log.info("生成服务费订单号为:{}，对应放款订单号为:{}", serviceOrderNo, order.getUuid());
                return entity.getUuid();
            } catch (Exception e) {
                log.error("生成服务费订单失败,对应放款订单号为:"+ order.getUuid(), e);
            }

        }else {
            log.info("非首次打款，服务费订单已生成，服务费订单号为:{}，对应放款订单号为:{}", serviceOrderNo, order.getUuid());
            return scanList.get(0).getUuid();
        }
        return "";
    }

    /**
     * 服务费订单打款
     */
    public void loanCoverCharge() {

        List<OrdServiceOrder> orderList = ordServiceOrderDao.queryServiceChargeList();
        for (OrdServiceOrder serviceOrder : orderList) {

            if (this.queryLoanCoverCharge(serviceOrder)) {
                continue;
            }

            String s = this.redisClient.get(RedisContants.ORDER_LOAN_LOCK_NEW + serviceOrder.getUuid());
            if (!StringUtils.isEmpty(s)) {
                log.info("已经放款一次：{}", serviceOrder.getUuid());
                continue;
            }

            // HTTP请求参数
            Map<String, String> contents = new HashMap<String, String>();
            contents.put("externalId", serviceOrder.getUuid()); // 订单号
            contents.put("amount", String.valueOf(serviceOrder.getServiceFee())); // 放款所收取的服务费
            contents.put("description", "DO-IT service Charge"); // 描述
            contents.put("disburseType", LoanDisburseTypeEnum.PRE_SERVICE_FEE.getType()); // 支付类型
            contents.put("disburseChannel", serviceOrder.getDisburseChannel()); // 支付通道

            FormBody.Builder builder = new FormBody.Builder();
                    builder.add("externalId", serviceOrder.getUuid())  // 手续费订单号
                    .add("amount", String.valueOf(serviceOrder.getServiceFee())) // 放款所收取的服务费
                    .add("description", "DO-IT service Charge")
                    .add("disburseType", LoanDisburseTypeEnum.PRE_SERVICE_FEE.getType())
                    .add("disburseChannel", serviceOrder.getDisburseChannel());
//                    .build();
            if (GlobalAccount.BANK_CODE_BCA.contentEquals(serviceOrder.getDisburseChannel())) {
                contents.put("bankCode", GlobalAccount.BANK_CODE_BCA); // 银行卡code
                contents.put("accountNumber", GlobalAccount.ACCOUNT_NUMBER_BCA); // 银行卡号
                contents.put("accountHolderName", GlobalAccount.HOLDER_NAME_BCA); // 银行卡开户人姓名
                builder.add("bankCode", GlobalAccount.BANK_CODE_BCA)  // 银行卡code
                        .add("accountNumber", GlobalAccount.ACCOUNT_NUMBER_BCA) // 银行卡号
                        .add("accountHolderName", GlobalAccount.HOLDER_NAME_BCA) // 银行卡开户人姓名
                        ;
            } else if (GlobalAccount.BANK_CODE_BNI.contentEquals(serviceOrder.getDisburseChannel())) {
                contents.put("bankCode", GlobalAccount.BANK_CODE_BNI); // 银行卡code
                contents.put("accountNumber", GlobalAccount.ACCOUNT_NUMBER_BNI); // 银行卡号
                contents.put("accountHolderName", GlobalAccount.HOLDER_NAME_BNI); // 银行卡开户人姓名
                builder.add("bankCode", GlobalAccount.BANK_CODE_BNI)  // 银行卡code
                        .add("accountNumber", GlobalAccount.ACCOUNT_NUMBER_BNI) // 银行卡号
                        .add("accountHolderName", GlobalAccount.HOLDER_NAME_BNI) // 银行卡开户人姓名
                        ;
            } else if (GlobalAccount.BANK_CODE_CIMB.contentEquals(serviceOrder.getDisburseChannel())) {
                contents.put("bankCode", GlobalAccount.BANK_CODE_CIMB); // 银行卡code
                contents.put("accountNumber", GlobalAccount.ACCOUNT_NUMBER_CIMB); // 银行卡号
                contents.put("accountHolderName", GlobalAccount.HOLDER_NAME_CIMB); // 银行卡开户人姓名
                builder.add("bankCode", GlobalAccount.BANK_CODE_CIMB)  // 银行卡code
                        .add("accountNumber", GlobalAccount.ACCOUNT_NUMBER_CIMB) // 银行卡号
                        .add("accountHolderName", GlobalAccount.HOLDER_NAME_CIMB) // 银行卡开户人姓名
                        ;
            }

            Request request = new Request.Builder()
                    .url(LOAN_EXPENSE_URL)
                    .post(builder.build())//requestBody
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();

            log.info("该笔订单放款服务费金额为" + serviceOrder.getServiceFee());
            String lockKey = RedisContants.ORDER_LOAN_COVER_CHARGE_LOCK + serviceOrder.getUserUuid();
            if (this.redisClient.lock(lockKey)) {

                // 请求数据落库，SysThirdLogs
                this.sysThirdLogsService.addSysThirdLogs(serviceOrder.getUuid(), serviceOrder.getUserUuid(), SysThirdLogsEnum.COMMIT_LOAN.getCode(), 0, JsonUtils.serialize(contents), null);
                try {
                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        // 放款响应
                        log.info("放款 请求后返回:{}", JsonUtils.serialize(responseStr));
                        LoanResponse loanResponse = JsonUtils.deserialize(responseStr, LoanResponse.class);
                        // 响应数据落库，sysThirdLogs
                        sysThirdLogsService.addSysThirdLogs(serviceOrder.getUuid(), serviceOrder.getUserUuid(), SysThirdLogsEnum.COMMIT_LOAN.getCode(), 0, null, responseStr);

                        if (loanResponse != null && !StringUtils.isEmpty(loanResponse.getCode())) {
                            if (loanResponse.getCode().equals("0")) {
                                loanServiceTempSuccess(serviceOrder);
                            } else {
                                log.info("放款失败,订单号:{},失败原因：{}", serviceOrder.getUuid(), loanResponse.getErrorMessage());
                                loanServiceTempFaild(serviceOrder,loanResponse);
                            }
                        } else {
                            log.error("放款失败,接口返回为空,订单号:{}", serviceOrder.getUuid());
                            loanServiceTempFaild(serviceOrder,loanResponse);
                        }
                    }else {
                        log.error("服务费放款失败,订单号:" + serviceOrder.getUuid(),response.body().toString(),response.toString());
                    }
                } catch (Exception e) {
                    log.error("服务费放款失败,订单号:" + serviceOrder.getUuid(), e);
                } finally {
                    this.redisClient.unLock(lockKey);
                }
            }
            this.redisClient.set(RedisContants.ORDER_LOAN_LOCK_NEW + serviceOrder.getUuid(), serviceOrder.getUuid(), 3600 * 24 * 30);
        }
    }


    /**
     * 检查对方手续费是否已在第三发打款--mK
     *
     * @param entity
     * @return
     */
    public boolean queryLoanCoverCharge(OrdServiceOrder entity) {
        try {
            log.info("查询订单号:{}", entity.getUuid());
            LoanResponse response = this.payService.queryLoanResult(entity.getUuid(), entity.getUuid());

            if (response != null) {
                //
                if (response.getCode().equals("0")|| response.getCode().equals("1")) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("查询订单异常,单号: " + entity.getUuid(), e);
        }
        return false;
    }


    /**
     *  服务费订单预打款成功
     * */
    public void loanServiceTempSuccess(OrdServiceOrder serviceOrder){

        log.info("服务费预打款成功=====================》服务费订单号：" + serviceOrder.getUuid());

        OrdServiceOrder entity = new OrdServiceOrder();
        entity.setUuid(serviceOrder.getUuid());
        entity.setStatus(OrdServiceOrderEnum.LOANING.getCode());
        entity.setUpdateTime(new Date());
        this.ordServiceOrderDao.update(entity);

    }


    /**
     *  服务费订单预打款失败
     * */
    public void loanServiceTempFaild(OrdServiceOrder serviceOrder,LoanResponse response){

        log.info("服务费预打款失败=====================》服务费订单号：" + serviceOrder.getUuid());

        OrdServiceOrder entity = new OrdServiceOrder();
        entity.setUuid(serviceOrder.getUuid());
        entity.setStatus(OrdServiceOrderEnum.LOAN_FAILD.getCode());
        entity.setUpdateTime(new Date());
        if (response != null){
            if (!StringUtils.isEmpty(response.getErrorMessage())){
                entity.setRemark(response.getErrorMessage());
            }
        }
        this.ordServiceOrderDao.update(entity);

    }

    @Transactional
    public void cheakLoanCoverChargeByOrder() {

        //  放款中的服务费订单
        List<OrdServiceOrder> chargeOrders = this.ordServiceOrderDao.queryServiceCharge();

        log.info("查询放款中服务费订单个数:", chargeOrders.size());
        for (OrdServiceOrder order : chargeOrders) {

            try {
                LoanResponse response = this.payService.queryLoanResult(order.getUuid(), order.getUserUuid());
                if (response != null) {
                    if (response.getCode().equals("0") || response.getCode().equals("1")) {
                        if (response.getDisburseStatus().equals("COMPLETED")) {
                            loanServiceSuccess(order);

                        } else if (response.getDisburseStatus().equals("FAILED")) {
                            log.info("放款失败,订单号:{},失败原因：{}", order.getUuid(), response.getErrorMessage());
                            // 放款失败
                            loanServiceFaild(order,response);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("查询订单异常,单号: " + order.getUuid(), e);
            }
        }
    }


    /**
     *  服务费订单实际打款成功
     * */
    public void loanServiceSuccess(OrdServiceOrder serviceOrder){

        log.info("服务费实际打款成功=====================》服务费订单号：" + serviceOrder.getUuid());

        OrdServiceOrder entity = new OrdServiceOrder();
        entity.setUuid(serviceOrder.getUuid());
        entity.setStatus(OrdServiceOrderEnum.LOAN_SUCCESS.getCode());
        entity.setUpdateTime(new Date());
        entity.setLoanTime(new Date());
        this.ordServiceOrderDao.update(entity);

        OrdLoanAmoutRecord record = new OrdLoanAmoutRecord();
        record.setOrderNo(serviceOrder.getUuid());
        record.setUserUuid(serviceOrder.getUserUuid());
        record.setActualLoanAmout(serviceOrder.getServiceFee()+"");
        record.setLoanChannel(serviceOrder.getDisburseChannel());
        record.setServiceFee(BigDecimal.ZERO);
        this.loanAmoutRecordDao.insert(record);
    }

    /**
     *  服务费订单实际打款失败
     * */
    public void loanServiceFaild(OrdServiceOrder serviceOrder,LoanResponse response){

        log.info("服务费实际打款失败=====================》服务费订单号：" + serviceOrder.getUuid());

        OrdServiceOrder entity = new OrdServiceOrder();
        entity.setUuid(serviceOrder.getUuid());
        entity.setStatus(OrdServiceOrderEnum.LOAN_FAILD.getCode());
        entity.setUpdateTime(new Date());
        if (response != null){
            if (!StringUtils.isEmpty(response.getErrorMessage())){
                entity.setRemark(response.getErrorMessage());
            }
        }
        this.ordServiceOrderDao.update(entity);

    }



    /**
     * 放款服务费转账（直接转到指定账户)
     */
    public void transferCoverCharge() {

        List<OrdServiceOrder> orderList = ordServiceOrderDao.queryServiceChargeList();
        for (OrdServiceOrder serviceOrder : orderList) {

            log.info("服务费订单号为:" + serviceOrder.getUuid());

            Request request = new Request.Builder()
                    .url(TRANSFER_URL + serviceOrder.getOrderNo())
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();

            String lockKey = RedisContants.ORDER_LOAN_COVER_CHARGE_LOCK + serviceOrder.getUserUuid();
            if (this.redisClient.lock(lockKey)) {
                try {
                    Response response = httpClient.newCall(request).execute();
                    if(response.isSuccessful())
                    {
                        String  responseStr = response.body().string();
                        // 服务费transfer 响应
                        log.info("服务费transfer 请求后返回:{}", responseStr);
                        if (!StringUtils.isEmpty(responseStr)){
                            if (responseStr.equals("Success")){
                                loanServiceTransferSuccess(serviceOrder);
                            }else {
                                log.info("服务费transfer失败,订单号:{}", serviceOrder.getUuid());
                            }
                        }else {
                            log.info("服务费transfer失败,订单号:{}", serviceOrder.getUuid());
                        }

                    }else {
                        log.error("服务费transfer失败,订单号:" + serviceOrder.getUuid(),response.body().toString(),response.toString());
                    }

                } catch (Exception e) {
                    log.error("服务费transfer异常,订单号:" + serviceOrder.getUuid(), e);
                } finally {
                    this.redisClient.unLock(lockKey);
                }
            }
        }
    }

    /**
     *  服务费订单转账成功
     * */
    public void loanServiceTransferSuccess(OrdServiceOrder serviceOrder){

        log.info("服务费转账成功=====================》服务费订单号：" + serviceOrder.getUuid());

        OrdServiceOrder entity = new OrdServiceOrder();
        entity.setUuid(serviceOrder.getUuid());
        entity.setStatus(OrdServiceOrderEnum.TRANS_SUCCESS.getCode());
        entity.setUpdateTime(new Date());
        this.ordServiceOrderDao.update(entity);

    }
}
