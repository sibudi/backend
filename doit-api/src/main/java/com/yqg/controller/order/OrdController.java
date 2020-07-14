package com.yqg.controller.order;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.service.order.DelayOrdService;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.GetOrdRepayAmoutRequest;
import com.yqg.service.order.request.GetOrderStatusRequest;
import com.yqg.service.order.request.LoanConfirmRequest;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.order.response.*;
import com.yqg.service.pay.request.DelayOrderRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by wanghuaizhou on 2017/11/25.
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrdController {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private OrdService ordService;

    @Autowired
    private DelayOrdService delayOrdService;

    @ApiOperation("下单操作")
    @RequestMapping(value = "/toOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<OrderOrderResponse> toOrder(HttpServletRequest request, @RequestBody OrdRequest orderRequest)
            throws ServiceException, InvocationTargetException, IllegalAccessException {
//        UserSessionUtil.filter(request, this.redisClient, orderRequest);
//        log.info("request body{}", JsonUtils.serialize(orderRequest));
        // 测试
        log.info("用户下单");
        //获取客户端ip地址
        orderRequest.setIPAdress(GetIpAddressUtil.getIpAddr(request));
        return ResponseEntityBuilder.success(ordService.toOrder(orderRequest,redisClient));
    }

    @ApiOperation("订单列表")
    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<OrderListResponse>> getOrderList(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
            throws ServiceException, InvocationTargetException, IllegalAccessException {
//        UserSessionUtil.filter(request, this.redisClient, baseRequest);
//        log.info("request body{}", JsonUtils.serialize(baseRequest));
        // 测试
//        baseRequest.setUserUuid("1505939CA73647D381777D380921FCD33");
        log.info("获取订单列表");
        return ResponseEntityBuilder.success(ordService.getOrderList(baseRequest,redisClient));
    }

    @ApiOperation("获取代还款订单实时的代还款金额")
    @RequestMapping(value = "/getOrderRepayAmout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<GetOrdRepayAmoutResponse> getOrderRepayAmout(HttpServletRequest request, @RequestBody GetOrdRepayAmoutRequest repayAmoutRequest)
            throws Exception{
        log.info("获取代还款订单实时的代还款金额");
        return ResponseEntityBuilder.success(ordService.getOrderActualRepayAmout(repayAmoutRequest));
    }


    @ApiOperation("获取延期订单相关配置")
    @RequestMapping(value = "/getDelayOrderConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
//    public ResponseEntity<DelayOrdResponse> getDelayOrderConfig(HttpServletRequest request, @RequestBody SaveOrderUserUuidRequest saveOrderUserUuidRequest)
//            throws Exception{
    public ResponseEntity<ExtendFormulaResponse> getDelayOrderConfig(HttpServletRequest request, @RequestBody SaveOrderUserUuidRequest saveOrderUserUuidRequest)
            throws Exception{
        log.info("获取延期订单相关配置");

        //return ResponseEntityBuilder.success(delayOrdService.delayOrderInfo(saveOrderUserUuidRequest));

	// New Formula for Extension
	return ResponseEntityBuilder.success(delayOrdService.delayOrderInfoV2(saveOrderUserUuidRequest));
    }

    @ApiOperation("提交延期申请")
    @RequestMapping(value = "/repayDelayOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<DelayOrdResponse> repayDelayOrder(HttpServletRequest request, @RequestBody DelayOrderRequest delayOrderRequest)
            throws Exception{
        log.info("提交延期申请");
        delayOrdService.repayToDelayOrder(delayOrderRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("获取打款凭证相关信息")
    @RequestMapping(value = "/getPaymentProof", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<PaymentProofResponse> getPaymentProof(HttpServletRequest request, @RequestBody GetOrdRepayAmoutRequest getOrdRepayAmoutRequest)
            throws Exception{
        log.info("获取打款凭证相关信息");
        return ResponseEntityBuilder.success(ordService.getPaymentProof(getOrdRepayAmoutRequest));
    }

    @ApiOperation("贷款确认")
    @RequestMapping(value = "/confirmation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> confirmLoan(@RequestBody LoanConfirmRequest request) throws Exception {
        try {
            ordService.confirmLoan(request, redisClient);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e1) {
            log.info("confirmation exception,orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("更新订单地址信息")
    @RequestMapping(value = "/order-address", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> updateDeviceInfo(@RequestBody OrdRequest request) throws Exception {
        try {
            ordService.updateOrderAddress(request);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e1) {
            log.info("confirmation exception,orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("订单列表")
    @RequestMapping(value = "/getOrderStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@RequestBody GetOrderStatusRequest request) throws Exception {
        OrderStatusResponse orderResponse = new OrderStatusResponse();
        try {
            orderResponse = ordService.getOrderStatus(request);
            log.info("Get Order Status api");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e1) {
            log.info("getOrderStatus exception, orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
        return ResponseEntityBuilder.success(orderResponse);
    }
}
