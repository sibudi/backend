package com.yqg.manage.controller.review;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.order.request.AssignableOrderRequest;
import com.yqg.manage.service.order.request.CompletedOrderRequest;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.order.request.OrderAssignmentRequest;
import com.yqg.manage.service.order.request.ReviewerOrderApplicationParam;
import com.yqg.manage.service.order.response.AssignableOrderResponse;
import com.yqg.manage.service.order.response.CompletedOrderResponse;
import com.yqg.manage.service.order.response.ManOrderListResponse;
import com.yqg.manage.service.review.ReviewService;
import com.yqg.manage.service.user.ManUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/


@Api(tags = "ReviewController")
@RestController
@RequestMapping("/manage")
@H5Request
public class ReviewController {

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private ReviewService reviewService;

    @ApiOperation("分配待审核订单 Author: zengxiangcai")
    @RequestMapping(value = "/orders/review-assignment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> assignOrder(@RequestParam("sessionId") String sessionId,
                                                  @RequestBody OrderAssignmentRequest request) {
        reviewService
                .assignOrder(manUserService.getUserIdBySession(sessionId), request.getReviewerId(),
                        request.getOrderUUIDs());
        return ResponseEntitySpecBuilder.success();
    }


    @ApiOperation("查询分配给自己的审核订单任务列表  Author: zengxiangcai")
    @RequestMapping(value = "/orders/reviewer/userSelf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<ManOrderListResponse>>> getReviewableOrderList(
            @RequestParam("sessionId") String sessionId,
            @RequestBody ManOrderListSearchResquest request
    ) {
        return ResponseEntitySpecBuilder.success(reviewService
                .getReviewableOrderList(manUserService.getUserIdBySession(sessionId),
                        request.getStatus(), request.getPageNo(), request.getPageSize()));
    }


    @ApiOperation("查询待分配订单  Author: zengxiangcai")
    @RequestMapping(value = "/orders/review-assignment-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<AssignableOrderResponse>>> getAssignableOrderList(
            @RequestBody AssignableOrderRequest request
    ) {
        return ResponseEntitySpecBuilder.success(reviewService
                .getAssignableOrderList(request));

    }


    @ApiOperation("查询审核完成订单(审核岗)  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/review-finished", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<CompletedOrderResponse>>> getReviewFinishedOrderList(
            @RequestParam("sessionId") String sessionId,
            @RequestBody CompletedOrderRequest request
    ) {
        return ResponseEntitySpecBuilder.success(
                reviewService
                        .getCompletedOrderList(manUserService.getUserIdBySession(sessionId),
                                request, false));

    }

    @ApiOperation("查询审核完成订单(管理岗)  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/admin-review-finished", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<CompletedOrderResponse>>> getReviewFinishedOrderListForAdmin(
            @RequestParam("sessionId") String sessionId,
            @RequestBody CompletedOrderRequest request
    ) {
        return ResponseEntitySpecBuilder.success(
                reviewService
                        .getCompletedOrderList(manUserService.getUserIdBySession(sessionId),
                                request, true));

    }

    @ApiOperation("审核人员申请分配审核订单  Author: zengxiangcai ")
    @RequestMapping(value = "/orders/review-application", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> applyReviewOrder(@RequestParam("sessionId") String sessionId,
                                                       @RequestBody ReviewerOrderApplicationParam param) {
        reviewService.applyOrderReview(manUserService.getUserIdBySession(sessionId), param);
        return ResponseEntitySpecBuilder.success();
    }
}
