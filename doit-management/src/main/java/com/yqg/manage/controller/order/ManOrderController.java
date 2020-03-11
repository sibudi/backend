package com.yqg.manage.controller.order;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.order.ManOrderOrderService;
import com.yqg.manage.service.order.request.*;
import com.yqg.manage.service.order.response.*;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.GetOrdRepayAmoutRequest;
import com.yqg.service.order.response.PaymentProofResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author alan
 */
@RestController
@H5Request
@RequestMapping("/manage")
@Api(tags = "订单管理类")
public class ManOrderController {

    @Autowired
    private ManOrderOrderService manOrderOrderService;

    @Autowired
    private OrdService ordService;

    @ApiOperation("查询全部订单列表")
    @RequestMapping(value = "/AllOrderList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> AllOrderList(
            @RequestBody ManAllOrdListSearchResquest ordListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder
                .success(this.manOrderOrderService.getOrderList(ordListSearchResquest));
    }

    @ApiOperation("根据订单uuid查询订单详情")
    @RequestMapping(value = "/orderInfoByUuid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<ManOrderDetailResponse> orderInfoByUuid(
            @RequestBody ManAllOrdListSearchResquest orderSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder
                .success(this.manOrderOrderService.orderInfoByUuid(orderSearchResquest));
    }

    @ApiOperation("Get Order Info with simple format")
    @RequestMapping(value = "/orderSimpleInfoByUuid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<OrderSimpleInfoResponse> orderSimpleInfoByUuid(String orderUuid, Integer outsourceId) throws Exception {
        return ResponseEntitySpecBuilder
            .success(this.manOrderOrderService.orderSimpleInfoByUuid(orderUuid, outsourceId));
    }

    @ApiOperation("根据用户uuid查询订单历史列表")
    @RequestMapping(value = "/orderHistoryListByUserUuid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<OrdOrder>> orderHistoryListByUserUuid(
            @RequestBody ManOrderListSearchResquest orderSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder
                .success(this.manOrderOrderService.orderHistoryListByUserUuid(orderSearchResquest));
    }


    @ApiOperation("D0订单查询  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/D0-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<D0OrderResponse>>> getD0OrderList(@RequestBody D0OrderRequest param) {
        return ResponseEntitySpecBuilder.success(manOrderOrderService.getD0OrderList(param));
    }


    @ApiOperation("逾期订单查询  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/overdue-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<OverdueOrderResponse>>> getOverdueOrderList(
            @RequestBody OverdueOrderRequest param) {
        return ResponseEntitySpecBuilder.success(manOrderOrderService.getOverdueOrderList(param));
    }


    @ApiOperation("已还款订单查询  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/paid-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<PaidOrderResponse>>> getPaidOrderList(@RequestBody PaidOrderRequest param) {
        return ResponseEntitySpecBuilder.success(manOrderOrderService.getPaidOrderList(param));
    }

    @ApiOperation("放款失败订单列表  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/issueFailed-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<IssueFailedOrderResponse>>> getIssueFailedOrderList(
            @RequestBody OrderSearchRequest param) {
        return ResponseEntitySpecBuilder
                .success(manOrderOrderService.getIssueFailedOrderList(param));
    }


    @ApiOperation("新增或更新订单复审和催收打标签备注")
    @RequestMapping(value = "/inserOrUpdateManOrderRemark", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> inserOrUpdateTeleReviewRemark(@RequestBody ManOrderRemarkRequest request)
            throws Exception {
        Integer id = manOrderOrderService.inserOrUpdateTeleReviewRemark(request);
        return ResponseEntitySpecBuilder.success(id);
    }

    @ApiOperation("修复展期订单数据")
    @RequestMapping(value = "/repairDelayOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> repairDelayOrder(@RequestBody RepairDelayOrderRequest delayOrderRequest)
            throws Exception {
        manOrderOrderService.copyDataToDelayOrder(delayOrderRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("使淼科那边订单无效化")
    @RequestMapping(value = "/makeMkOrderDisable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> makeMkOrderDisable(@RequestBody MakeMKOrderRequest makeMKOrderRequest) throws Exception{

        this.manOrderOrderService.makeMkOrderDisable(makeMKOrderRequest);
        return ResponseEntitySpecBuilder
                .success();
    }

    @ApiOperation("修复因为资金路由余额不足的订单")
    @RequestMapping(value = "/repairFaildOrderWithNoBalance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> repairFaildOrderWithNoBalance(@RequestBody MakeMKOrderRequest makeMKOrderRequest) throws Exception{

        this.manOrderOrderService.repairFaildOrderWithNoBalance();
        return ResponseEntitySpecBuilder
                .success();
    }


    @ApiOperation("获取打款凭证相关信息")
    @RequestMapping(value = "/getPaymentProof", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<PaymentProofResponse> getPaymentProof(@RequestBody GetOrdRepayAmoutRequest getOrdRepayAmoutRequest)
            throws Exception{
        return ResponseEntityBuilder.success(ordService.getPaymentProof(getOrdRepayAmoutRequest));
    }


    @ApiOperation("通过订单号取消订单")
    @RequestMapping(value = "/cancleOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> cancleOrder(@RequestBody GetOrdRepayAmoutRequest repayOrderRequest)
            throws  Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderOrderService.cancleOrder(repayOrderRequest));
    }


    @ApiOperation("查询分期订单还款计划（账单信息）")
    @RequestMapping(value = "/byStagesBillInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> byStagesBillInfo(
            @RequestBody OrderSearchRequest searchRequest)
            throws Exception {
        return ResponseEntitySpecBuilder
                .success(this.manOrderOrderService.byStagesBillInfo(searchRequest));
    }
}
