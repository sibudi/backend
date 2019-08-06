package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.dao.ExternalOrderRelationDao;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.entity.OrdHistory;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.enums.RepayBankEnum;
import com.yqg.service.externalChannel.enums.RepayStoreEnum;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2GetOrdStatusRequest;
import com.yqg.service.externalChannel.request.Cash2GetRepayInfoRequest;
import com.yqg.service.externalChannel.request.Cash2ManualRequest;
import com.yqg.service.externalChannel.response.Cash2OrdStatusFeedbackResponse;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.utils.Cash2OrdCheckResultEnum;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import com.yqg.service.externalChannel.utils.HttpUtil;
import com.yqg.service.externalChannel.utils.SendDataBuiler;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.LoanConfirmRequest;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.pay.response.RepayResponse;
import com.yqg.service.system.service.SysThirdLogsService;
import java.math.BigDecimal;
import java.util.*;

import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Created by wanghuaizhou on 2018/3/8.
 */
@Service
@Slf4j
public class Cash2OrderService {

    // 订单状态反馈 url
    @Value("${third.cashcash.orderStatusFeedback}")
    private String STATUS_FEEDBACK_URL;
    // 订单审核状态反馈 url
    @Value("${third.cashcash.orderCheckResultFeedback}")
    private String RESULT_FEEDBACK_URL;
    // 提交还款申请url
    @Value("${pay.commitRepayUrl}")
    private String COMMIT_REPAY_URL;
    @Value("${pay.token}")
    private String PAY_TOKEN;


    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SysThirdLogsService sysThirdLogsService;

    @Autowired
    private ExternalOrderRelationDao externalOrderRelationDao;
    @Autowired
    private ExternalChannelDataService externalChannelDataService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private RepayService repayService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private Cash2Config cash2Config;

    @Autowired
    private OrdHistoryDao ordHistoryDao;

    @Autowired
    private OkHttpClient httpClient;

    /***
     * 订单状态反馈
     * @param order
     * @return
     */
    public void ordStatusFeedback(OrdOrder order, Cash2OrdStatusEnum ordStatusEnum) {

        ExternalOrderRelation externalOrder = this.externalOrderRelationDao
            .selectByOrderNo(order.getUuid());
        try {

            Map<String, Object> postMap = new HashMap<>();
            postMap.put("order_no", externalOrder.getExternalOrderNo()); // 订单号
            postMap.put("order_status", ordStatusEnum.getCode() + "");  // 订单状态
            postMap.put("update_time", getSecondTimestamp(new Date()) + ""); // 时间戳 单位:秒
            // 加密请求
            Cash2ApiParam sendData = new SendDataBuiler().buildParam(postMap,cash2Config);
            // 响应
            String response = HttpUtil.postJson(JsonUtils.serialize(sendData), STATUS_FEEDBACK_URL);
            log.info("订单状态反馈 请求后返回:{}", JsonUtils.serialize(response));

            Cash2OrdStatusFeedbackResponse feedbackResponse = JsonUtils
                .deserialize(response, Cash2OrdStatusFeedbackResponse.class);
            if (feedbackResponse.getCode() == 0) {
                log.info("订单状态反馈成功");
            } else {
                log.info("订单状态反馈失败，返回的message为:{}", feedbackResponse.getMessage());
            }

        } catch (Exception e) {
            log.error("订单状态反馈异常,异常原因:{}", e.getMessage());
        }


    }

    /***
     * 订单状态反馈--手动调用
     * @param cash2ManualRequest
     * @return
     */
    public void ordStatusFeedbackManual(Cash2ManualRequest cash2ManualRequest) {

        if (cash2ManualRequest.getOrderNo() != null){
            OrdOrder order = this.ordService.getOrderByOrderNo(cash2ManualRequest.getOrderNo());
            Cash2OrdStatusEnum cash2OrdStatusEnum =  Cash2OrdStatusEnum.CANCLE;
            cash2OrdStatusEnum.setCode(cash2ManualRequest.getOrderStatus());
            ordStatusFeedback(order,cash2OrdStatusEnum);
        }
    }

    /***
     * 订单状态拉取
     * @param request
     * @return
     */
    public Cash2Response getOrdStatus(Cash2GetOrdStatusRequest request) {

        if (StringUtils.isEmpty(request.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_EMPTY);
        }
        ExternalOrderRelation relation = externalChannelDataService
            .getExternalOrderRelationByExternalOrderNo(request.getOrderNo());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR).withData("the order cannot find");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("order_no", request.getOrderNo());
        data.put("order_status", getOrdStatusFromUang2ToCash2(ordOrder) + "");
        data.put("update_time", ordOrder.getUpdateTime());

        return Cash2ResponseBuiler.buildResponse(
            Cash2ResponseCode.CODE_OK_1).withData(data);
    }


    /**
     * 将Do-It的订单状态 映射到 CashCash 的订单状态
     */
    public int getOrdStatusFromUang2ToCash2(OrdOrder order) {

        switch (order.getStatus()) {
            case 1:// 待提交 	(待补充资料)
                return 80;
            case 2:// 待机审    （审批中）
            case 3:// 待初审    （审批中）
            case 4:// 待复审    （审批中）
            case 17:// 待电核   （审批中）
            case 18:// 待电核   （审批中）
                return 90;
            case 5:// 待放款    （审批通过）
                // 查询是否有 降额待确认的记录  有 ：仍然返回待确认  没有：返回待放款
                List<OrdHistory> histories =  ordHistoryDao.getDeratingRecord(order.getUuid());
            if (CollectionUtils.isEmpty(histories)){
                return 100;
            }else {
                return 109;
            }

            case 6:// 放款处理中 （审批通过）
                return 100;
            case 7:// 待还款-未逾期7（放款成功）
            case 9:// 还款处理中9（待还款）(放款成功)
                return 170;
            case 8:// 待还款-已逾期8（逾期）
                return 180;
            case 10:// 正常已还款 （贷款结清）
            case 11:// 逾期已还款 （贷款结清）
                return 200;
            case 12:// 规则审核、机审不通过12（审批不通过）
            case 13:// 初审不通过13（审批不通过）
            case 14:// 复审不通过14（审批不通过）
                return 110;
            case 15:// 取消      （贷款取消）
                return 161;
            case 16:// 放款失败 （放款失败）
                return 169;
            case 19:// 降额待确认
                return 109;
        }
        return 80;
    }


    /***
     * 订单审核结果反馈
     * @param order
     * @return
     */
    public void ordCheckResultFeedback(OrdOrder order, Cash2OrdCheckResultEnum cheakResult) {

        ExternalOrderRelation externalOrder = externalOrderRelationDao
            .selectByOrderNo(order.getUuid());

        try {

            Map<String, Object> postMap = new HashMap<>();
            if (cheakResult.equals(Cash2OrdCheckResultEnum.CHECK_PASS) || cheakResult.equals(Cash2OrdCheckResultEnum.WAITING_CONFIRM)) {
                // 审批通过  /  降额待确认
                postMap.put("order_no", externalOrder.getExternalOrderNo());  // 订单号
                postMap.put("conclusion", cheakResult.getCode() + "");  // 审批结论
                postMap.put("approval_time", getSecondTimestamp(new Date()) + ""); // 审批通过时间
                postMap.put("amount_type", 0 + "");  // 审批金额是否固定
                postMap.put("approval_amount", order.getAmountApply() + "");  // 审批金额
                postMap.put("term_unit", 1 + "");  // 期限类型
                postMap.put("term_type", 0 + ""); // 审批期限是否固定
                postMap.put("approval_term", order.getBorrowingTerm() + "");  // 审批天（月）数-固定
                postMap.put("pay_amount",
                    order.getAmountApply().add(order.getInterest()) + "");  // 总还款额
                postMap.put("remark",
                    "本金:" + order.getAmountApply() + "利息:" + order.getInterest());  // 总还款额组成说明

                postMap.put("interest_amount", order.getInterest() + "");  // 总利息

                postMap.put("interest_rate", order.getInterest().multiply(new BigDecimal("100"))
                    .divide(order.getAmountApply(), 2, BigDecimal.ROUND_HALF_UP) + "");  // 总利率

                if(cheakResult.equals(Cash2OrdCheckResultEnum.WAITING_CONFIRM)){
                    // 降额待确认
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(order.getUpdateTime());
                    // 将时分秒清零
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    postMap.put("dropexpire_date", getSecondTimestamp(DateUtils.addDate(calendar.getTime(),7)));  // 降额确认到期时间
                }

            }else if (cheakResult.equals(Cash2OrdCheckResultEnum.CHECK_NEED_DATA)) {
                // 需要重新填写审批资料

            } else if (cheakResult.equals(Cash2OrdCheckResultEnum.CHECK_NOT_PASS)) {
                // 审批不通过
                postMap.put("order_no", externalOrder.getExternalOrderNo());  // 订单号
                postMap.put("conclusion", 40 + "");  // 审批结论
                postMap.put("amount_type", 0 + "");  // 审批金额是否固定
                postMap.put("remark", "填写资料不符合"); // remark
                postMap.put("approval_amount", 0 + "");  // 审批金额

                postMap.put("term_unit", 1 + "");  // 期限类型
                postMap.put("term_type", 0 + ""); // 审批期限是否固定

                postMap.put("approval_term", 0 + "");  // 审批天（月）数-固定
                postMap.put("interest_rate", 0 + "");  // 总利率

                postMap.put("admin_amount",
                        0 + "");  // 总管理费
                postMap.put("interest_amount",
                        0 + "");  // 总利息
                postMap.put("pay_amount",
                        0 + "");  // 总还款额

            }

            log.info("订单审核结果反馈，订单号:{},审批结果:{}", order.getUuid(), cheakResult.getMessage());
            // 加密请求
            Cash2ApiParam sendData = new SendDataBuiler().buildParam(postMap,cash2Config);
            // 响应
            String response = HttpUtil
                .postJson(JsonUtils.serialize(sendData), RESULT_FEEDBACK_URL);
            // 响应
            log.info("订单审核结果反馈 请求后返回:{}", JsonUtils.serialize(response));

            Cash2OrdStatusFeedbackResponse feedbackResponse = JsonUtils
                .deserialize(response, Cash2OrdStatusFeedbackResponse.class);
            if (feedbackResponse.getCode() == 0) {
                log.info("订单审核结果反馈成功");
            } else {
                log.info("订单审核结果反馈失败，返回的message为:{}", feedbackResponse.getMessage());
            }

        } catch (Exception e) {

            log.error("订单审核结果反馈异常,异常原因:{}", e);
        }
    }

    /***
     * 订单审核结果反馈 -- 手动调用
     * @param cash2ManualRequest
     * @return
     */
    public void ordCheckResultFeedbackManual(Cash2ManualRequest cash2ManualRequest) {

        if (cash2ManualRequest.getOrderNo() != null){
            OrdOrder order = this.ordService.getOrderByOrderNo(cash2ManualRequest.getOrderNo());
            Cash2OrdCheckResultEnum cash2OrdCheckResultEnum =  Cash2OrdCheckResultEnum.CHECK_PASS;
            cash2OrdCheckResultEnum.setCode(cash2ManualRequest.getOrderStatus());
            ordCheckResultFeedback(order,cash2OrdCheckResultEnum);
        }
    }


    /***
     * 订单审核结果拉取
     * @param request
     * @return
     */
    public Cash2Response getOrdCheckResult(Cash2GetOrdStatusRequest request) {

        if (StringUtils.isEmpty(request.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_EMPTY);
        }
        ExternalOrderRelation relation = externalChannelDataService
            .getExternalOrderRelationByExternalOrderNo(request.getOrderNo());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR).withData("the order cannot find");
        }

        if (ordOrder.getStatus() == OrdStateEnum.MACHINE_CHECKING.getCode() ||
            ordOrder.getStatus() == OrdStateEnum.FIRST_CHECK.getCode() ||
            ordOrder.getStatus() == OrdStateEnum.SECOND_CHECK.getCode()||
                ordOrder.getStatus() == OrdStateEnum.WAIT_CALLING.getCode()
        ||ordOrder.getStatus() ==OrdStateEnum.WAITING_CALLING_AFTER_FIRST_CHECK.getCode()) {
            // 审核中
            Map<String, Object> data = new HashMap<>();
            data.put("order_no", request.getOrderNo());  //订单号
            data.put("conclusion", 20 + "");  // 审批结论
            data.put("approval_time", getSecondTimestamp(new Date()) + ""); // 审批通过时间
            data.put("amount_type", 0 + "");  // 审批金额是否固定
            data.put("approval_amount", ordOrder.getAmountApply() + "");  // 审批金额
            data.put("term_unit", 1 + "");  // 期限类型
            data.put("term_type", 0 + "");  // 审批期限是否固定
            data.put("approval_term", ordOrder.getBorrowingTerm() + "");  // 审批天（月）数-固定
            data.put("pay_amount",
                ordOrder.getAmountApply().add(ordOrder.getInterest()) + "");  // 总还款额
            data.put("loan_time", getSecondTimestamp(ordOrder.getApplyTime()));  // 借款时间
            data.put("interest_amount", ordOrder.getInterest() + "");  // 利息总额
            data.put("admin_amount", ordOrder.getServiceFee());  // 管理费总额
            data.put("remark",
                "本金:" + ordOrder.getAmountApply() + "利息:" + ordOrder.getInterest());  // 总还款额组成说明
            data.put("actual_amount", ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()));  // 实际到账金额
            data.put("interest_rate", ordOrder.getInterest().multiply(new BigDecimal("100"))
                .divide(ordOrder.getAmountApply(), 2, BigDecimal.ROUND_HALF_UP) + "");  // 总利率

            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.CODE_OK_1).withData(data);

        } else if (ordOrder.getStatus() == OrdStateEnum.LOANING.getCode() ||
            ordOrder.getStatus() == OrdStateEnum.LOANING_DEALING.getCode() ||
                ordOrder.getStatus() == OrdStateEnum.LOAN_FAILD.getCode()  ||
                ordOrder.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() ||
                ordOrder.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode() ||
                ordOrder.getStatus() == OrdStateEnum.WAITING_CONFIRM.getCode() ) {
            // 审核通过
            Map<String, Object> data = new HashMap<>();
            data.put("order_no", request.getOrderNo());  //订单号
            if (ordOrder.getStatus() == OrdStateEnum.LOAN_FAILD.getCode()){
                data.put("conclusion", 40 + "");  // 审批结论
            }else if(ordOrder.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()){
                data.put("conclusion", 10 + "");  // 审批结论
            }else if(ordOrder.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()){
                data.put("conclusion", 10 + "");  // 审批结论
            }else if(ordOrder.getStatus() == OrdStateEnum.LOANING.getCode()){

                // 查询是否有 降额待确认的记录   有 ：返回待审批  没有：返回待放款
                List<OrdHistory> histories =  ordHistoryDao.getDeratingRecord(ordOrder.getUuid());
                if (CollectionUtils.isEmpty(histories)){
                    data.put("conclusion", 10 + "");  // 审批结论
                }else {
                    data.put("conclusion", 15 + "");  // 审批结论
                }

            }else if(ordOrder.getStatus() == OrdStateEnum.WAITING_CONFIRM.getCode()){
                // 降额待确认
                data.put("conclusion", 15 + "");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ordOrder.getUpdateTime());
                // 将时分秒清零
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                data.put("dropexpire_date", getSecondTimestamp(DateUtils.addDate(calendar.getTime(),7)));  // 降额确认到期时间
            }  else {
                data.put("conclusion", 10 + "");  // 审批结论
            }


            data.put("approval_time", getSecondTimestamp(new Date()) + ""); // 审批通过时间
            data.put("amount_type", 0 + "");  // 审批金额是否固定
            data.put("approval_amount", ordOrder.getAmountApply() + "");  // 审批金额
            data.put("term_unit", 1 + "");  // 期限类型
            data.put("term_type", 0 + "");  // 审批期限是否固定
            data.put("approval_term", ordOrder.getBorrowingTerm() + "");  // 审批天（月）数-固定
            data.put("pay_amount",
                ordOrder.getAmountApply().add(ordOrder.getInterest()) + "");  // 总还款额
            data.put("loan_time", getSecondTimestamp(ordOrder.getApplyTime()));  // 借款时间
            data.put("interest_amount", ordOrder.getInterest() + "");  // 利息总额
            data.put("admin_amount", ordOrder.getServiceFee());  // 管理费总额
            data.put("remark",
                "本金:" + ordOrder.getAmountApply() + "利息:" + ordOrder.getInterest());  // 总还款额组成说明
            data.put("actual_amount", ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()));  // 实际到账金额
            data.put("interest_rate", ordOrder.getInterest().multiply(new BigDecimal("100"))
                .divide(ordOrder.getAmountApply(), 2, BigDecimal.ROUND_HALF_UP) + "");  // 总利率

            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.CODE_OK_1).withData(data);

        } else if (ordOrder.getStatus() == OrdStateEnum.MACHINE_CHECK_NOT_ALLOW.getCode() ||
            ordOrder.getStatus() == OrdStateEnum.FIRST_CHECK_NOT_ALLOW.getCode() ||
            ordOrder.getStatus() == OrdStateEnum.SECOND_CHECK_NOT_ALLOW.getCode()) {
            // 审核不通过
            Map<String, Object> data = new HashMap<>();
            data.put("order_no", request.getOrderNo());
            data.put("order_status", getOrdStatusFromUang2ToCash2(ordOrder) + "");
            data.put("update_time", ordOrder.getUpdateTime());
            data.put("conclusion", 40 + "");  // 审批结论
            data.put("approval_amount", 0 + "");  // 审批金额
            data.put("amount_type", 0 + "");  // 审批金额是否固定
            data.put("admin_amount",
                    0 + "");  // 总管理费
            data.put("interest_amount",
                    0 + "");  // 总利息
            data.put("pay_amount",
                    0 + "");  // 总还款额
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.CODE_OK_1).withData(data);

        }else {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR).withData("订单状态不在审核流程中");
        }
    }


    /**
     * 拉取还款计划接口 接口说明：cashcsah调用合作机构提供的还款计划拉取接口，将告知对应的订单号。 最终合作机构根据订单号返回其最新的还款计划给到cashcash。
     */
    public Cash2Response getOrdRepayPlan(Cash2GetOrdStatusRequest request) {

        if (StringUtils.isEmpty(request.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_EMPTY);
        }
        ExternalOrderRelation relation = externalChannelDataService
            .getExternalOrderRelationByExternalOrderNo(request.getOrderNo());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR).withData("the order cannot find");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("order_no", request.getOrderNo());  //订单号
        data.put("loan_amount", ordOrder.getAmountApply());  // 贷款金额
        data.put("term_unit", 1 + ""); // 周期单位
        data.put("loan_term", ordOrder.getBorrowingTerm() + "");  // 贷款周期
        data.put("loan_time", getSecondTimestamp(ordOrder.getLendingTime()));  // 贷款时间
        data.put("expiry_time", getSecondTimestamp(ordOrder.getRefundTime()));  // 到期时间
        data.put("admin_amount", 0.00 + "");  // 管理费
        data.put("interest_amount", ordOrder.getInterest() + "");  // 应还利息
        data.put("repay_amount", repayService.calculateRepayAmount(ordOrder,"1"));  //还款总额

        // 新增
        data.put("repay_bank_is", 1+"");  //还款
        // 1 BNI 2 BRI 3 MANDIRI  4 BCA 5 OTHER BANKS    （4和5 实际上都是转BNI支付）
        List<Integer> bankList = new ArrayList<>();
        bankList.add(RepayBankEnum.BNI.getType());
//        bankList.add(RepayBankEnum.BRI.getType());
//        bankList.add(RepayBankEnum.MANDIRI.getType());
//        bankList.add(RepayBankEnum.BCA.getType());
        bankList.add(RepayBankEnum.Permata.getType());
        bankList.add(RepayBankEnum.OtherBanks.getType());
        data.put("repay_bank_list", bankList);  //支持的银行code码
        data.put("repay_store_is", 1+"");  //是否支持线下便利店或超市
        List<Integer> storeList = new ArrayList<>();
        storeList.add(RepayStoreEnum.ALFAMART.getType());
        data.put("repay_store_list", storeList);  //支持的便利店code码

        // 还款状态
        Integer  billStatus = 0;
        if (ordOrder.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()){
            // 未到期（未逾期)
            billStatus = 1;
        }else  if (ordOrder.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()){
            // 逾期
            billStatus = 3;
        }else  if (ordOrder.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()
                || ordOrder.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){
            // 已还款
            billStatus = 2;
        }else {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.ORDER_NO_ERROR).withData("订单状态有误,非待还款和已还款订单");
        }
        data.put("bill_status", billStatus);  // 还款状态

        return Cash2ResponseBuiler.buildResponse(
            Cash2ResponseCode.CODE_OK_1).withData(data);
    }

    /**
     * 还款详情信息接口 接口说明：cashcash调用合作机构提供的银行还款详情获取接口，将告知对应的订单号。 最终合作机构根据订单号和还款期数返回其还款金额给cashcash。
     */
    public Cash2Response getRepayDetailInfo(Cash2GetRepayInfoRequest repayInfoRequest) {

//        if (repayInfoRequest.getRepType().equals("1")){
            // 银行还款
            // 1001 BNI 1002 BRI 1003 MANDIRI  1004 BCA 1006 OTHER BANKS    （4和5 实际上都是转BNI支付）
            if (StringUtils.isEmpty(repayInfoRequest.getOrderNo())) {
                return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.ORDER_NO_EMPTY);
            }
            ExternalOrderRelation relation = externalChannelDataService
                    .getExternalOrderRelationByExternalOrderNo(repayInfoRequest.getOrderNo());
            if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
                return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.ORDER_NO_ERROR);
            }

            OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
            if (ordOrder == null) {
                return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.ORDER_NO_ERROR).withData("the order cannot find");
            }

            if (ordOrder.getStatus() != OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()
                    && ordOrder.getStatus() != OrdStateEnum.RESOLVING_OVERDUE.getCode()) {
                log.info("订单状态异常,非待还款状态订单");
                return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.ORDER_NO_ERROR).withData("该订单状态不是代还款");
            }
            if (ordOrder.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()
                    || ordOrder.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()) {
                log.info("该订单已还款，返回订单记录查看，请勿重复操作");
                return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.ORDER_NO_ERROR).withData("该订单已还款");
            }

            try {

                String depositMethod = "";
                if (repayInfoRequest.getPayType().equals("1001")) {
                    depositMethod = "BNI";
                } else if (repayInfoRequest.getPayType().equals("1005")) {
                    depositMethod = "PERMATA";
                } else if (repayInfoRequest.getPayType().equals("1006")) {
//                    others 走permata
                    depositMethod = "PERMATA";
                }else if (repayInfoRequest.getPayType().equals("2001")) {
                    depositMethod = "ALFAMART";
                }else {
                    return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.PARAM_TYPE_ERROR_1002).withData("不支持的还款方式");
                }

                UsrUser user = this.usrService.getUserByUuid(relation.getUserUuid());
                String userName = "";
                if (user.getRealName() != null){
                    userName = user.getRealName().replaceAll("\\pP",""); //完全清除标点
                }

                RequestBody requestBody = new FormBody.Builder().build();
                Map<String, String> contents = new HashMap<String, String>();
                String paymentCount = repayService.calculateRepayAmount(ordOrder,"1");
                requestBody = new FormBody.Builder()
                        .add("externalId", ordOrder.getUuid())  // 订单号
                        .add("depositAmount", paymentCount)  // 还款金额
                        .add("depositChannel", "DOKU") // 还款渠道
                        .add("depositMethod", depositMethod) // 还款类型（alfamart,BRI.mandiri,BNI,otherBanks）
                        .add("currency", "IDR")
                        .add("depositType","PAYDAYLOAN")
                        .add("customerName",userName)   //????
                        .build();

                contents.put("externalId", ordOrder.getUuid());  // 订单号
                contents.put("depositAmount", paymentCount);  // 还款金额
                contents.put("depositChannel", "DOKU"); // 还款渠道
                contents
                        .put("depositMethod", depositMethod); // 还款类型（alfamart,BRI.mandiri,BNI,otherBanks）
                contents.put("currency", "IDR"); // 单位
                contents.put("depositType","PAYDAYLOAN");
                contents.put("customerName",userName);   //????

                log.info("该笔订单还款金额为" + paymentCount);

                Request request = new Request.Builder()
                        .url(COMMIT_REPAY_URL)
                        .post(requestBody)
                        .header("X-AUTH-TOKEN", PAY_TOKEN)
                        .build();

                // 请求数据落库，SysThirdLogs
                this.sysThirdLogsService.addSysThirdLogs(ordOrder.getUuid(), ordOrder.getUserUuid(),
                        SysThirdLogsEnum.COMMIT_REPAY.getCode(), 0, JsonUtils.serialize(contents), null);

                Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    // 还款响应
                    log.info("还款 请求后返回:{}", JsonUtils.serialize(responseStr));

                    RepayResponse repayResponse = JsonUtils
                            .deserialize(responseStr, RepayResponse.class);

                    // 记录还款码
                    RepayRequest repayRequest = new RepayRequest();
                    repayRequest.setOrderNo(ordOrder.getUuid());
                    repayRequest.setUserUuid(ordOrder.getUserUuid());
                    repayService
                            .recordOrderPaymentCode(repayRequest, "2", repayResponse.getPaymentCode(),
                                    ordOrder, paymentCount,null);
                    // 响应数据落库，sysThirdLogs
                    sysThirdLogsService
                            .addSysThirdLogs(repayRequest.getOrderNo(), repayRequest.getUserUuid(),
                                    SysThirdLogsEnum.COMMIT_REPAY.getCode(), 0, null, responseStr);

                    Map<String, Object> data = new HashMap<>();
                    data.put("order_no", repayInfoRequest.getOrderNo());  //订单号
                    data.put("amount", paymentCount);  // 当前所需还款额
                    // 还款所属名称
                    if (repayInfoRequest.getPayType().equals("1001")) {
                        data.put("title", "BNI");
                    } else if (repayInfoRequest.getPayType().equals("1002")) {
                        data.put("title", "BRI");
                    } else if (repayInfoRequest.getPayType().equals("1003")) {
                        data.put("title", "MANDIRI");
                    } else if (repayInfoRequest.getPayType().equals("1004")) {
                        data.put("title", "BCA");
                    } else if (repayInfoRequest.getPayType().equals("1006")) {
                        data.put("title", "OTHER BANKS");
                    }else if (repayInfoRequest.getPayType().equals("2001")) {
                        data.put("title", "ALFAMART");
                    }
                    data.put("account", repayResponse.getPaymentCode());  //还款账户号

                    return Cash2ResponseBuiler.buildResponse(
                            Cash2ResponseCode.CODE_OK_1).withData(data);
                } else {
                    log.info("还款 请求失败，订单号:{}", ordOrder.getUuid());
                    sysThirdLogsService.addSysThirdLogs(ordOrder.getUuid(), ordOrder.getUserUuid(),
                            SysThirdLogsEnum.COMMIT_REPAY.getCode(), 0, null, response.body().string());
                    return Cash2ResponseBuiler.buildResponse(
                            Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001).withData("调用第三方还款失败");
                }
            } catch (Exception e) {
                log.info("还款请求异常",e);
                return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001).withData("调用第三方还款异常");
            }
//        }else {
//            // 便利店还款 暂不支持
//            return Cash2ResponseBuiler.buildResponse(
//                    Cash2ResponseCode.PARAM_TYPE_ERROR_1002).withData("不支持的还款方式");
//        }
    }

    /***
     * 降额确认
     * @param request
     * @return
     */
    public Cash2Response confirmation(Cash2GetOrdStatusRequest request) {

        if (StringUtils.isEmpty(request.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.ORDER_NO_EMPTY);
        }
        ExternalOrderRelation relation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(request.getOrderNo());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.ORDER_NO_ERROR);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.ORDER_NO_ERROR).withData("the order cannot find");
        }

        try {

            LoanConfirmRequest loanConfirmRequest = new LoanConfirmRequest();
            loanConfirmRequest.setOrderNo(ordOrder.getUuid());
            ordService.confirmLoan(loanConfirmRequest,redisClient);
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.CODE_OK_1).withData("");

        } catch (ServiceException e) {

            log.error("降额确认异常", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001)
                    .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("降额确认异常", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001);
        }

    }


    /**
     * 获取精确到秒的时间戳
     */
    public static int getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }
    }
}
