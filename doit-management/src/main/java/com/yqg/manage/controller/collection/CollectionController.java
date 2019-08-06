package com.yqg.manage.controller.collection;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.collection.CollectionService;
import com.yqg.manage.service.collection.request.*;
import com.yqg.manage.service.collection.response.CollectionOrderResponse;
import com.yqg.manage.service.collection.response.CollectionPostResponse;
import com.yqg.manage.service.collection.response.CollectorResponseInfo;
import com.yqg.manage.service.collection.response.OutCollectionResponse;
import com.yqg.manage.service.collection.response.PaymentOrderResponse;
import com.yqg.manage.service.order.request.OrderSearchRequest;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.service.order.request.PaidOrderRequest;
import com.yqg.manage.service.order.response.ManOrderRemarkResponse;
import com.yqg.manage.service.system.response.SysDicItemResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@RestController
@RequestMapping("/manage/collection")
@H5Request
@Api(tags = "催收管理类")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @ApiOperation(value = "催收人员列表(所有)（后扩展为包含质检），Author: zengxiangcai", notes = "内部催收人员isThird传1")
    @RequestMapping(value = "/collectors", method = RequestMethod.POST)
    public ResponseEntitySpec<List<CollectorResponseInfo>> getAllCollectionsByThirdType(
            @RequestBody CollectorRequest param) {
        return ResponseEntitySpecBuilder
                .success(collectionService.getCollectorsByThirdType(param.getIsThird(), param.getSourceType()));
    }
    @ApiOperation(value = "获取所有催收人员如果存在委外帐号则获取委外帐号下的人")
    @RequestMapping(value = "/getAllCollectors", method = RequestMethod.POST)
    public ResponseEntitySpec<List<CollectorResponseInfo>> getAllCollectors(
            @RequestBody CollectorRequest param) {
        return ResponseEntitySpecBuilder
                .success(collectionService.getAllCollectors(param.getIsThird(), param.getSourceType()));
    }



    @ApiOperation(value = "所有岗位的催收人员集合，Author: zengxiangcai", notes = "查询当前分配好的所有催收人员")
    @RequestMapping(value = "/post-collectors", method = RequestMethod.POST)
    public ResponseEntitySpec<List<CollectionPostResponse>> getCurrentCollectors(@RequestBody CollectorRequest param) {
        return ResponseEntitySpecBuilder
                .success(collectionService.getCurrentCollectors(param.getSourceType()));
    }

    @ApiOperation(value = "所有岗位的催收人员集合，Author: zengxiangcai", notes = "查询当前分配好的所有休息的催收人员")
    @RequestMapping(value = "/post-rest-collectors", method = RequestMethod.POST)
    public ResponseEntitySpec<List<CollectionPostResponse>> getRestCurrentCollectors(@RequestBody CollectorRequest param) {
        return ResponseEntitySpecBuilder
                .success(collectionService.getRestCurrentCollectors(param.getSourceType()));
    }

    /***
     * 查询某一岗位下的催收人员
     *
     */
    @ApiOperation(value = "某一岗位的当前催收人员列表，Author: zengxiangcai", notes = "/post/collectors数据的子集,传参数postId")
    @RequestMapping(value = "/current-collectors", method = RequestMethod.POST)
    public ResponseEntitySpec<List<CollectorResponseInfo>> getCollectorsByPostId(
            @RequestBody CollectorRequest param) {
        return ResponseEntitySpecBuilder
                .success(collectionService.getCollectorsByPostId(param.getPostId(), param.getSourceType()));

    }


    /***
     * 查询催收岗位列表信息
     */

    @ApiOperation(value = "催收人员岗位列表，Author: zengxiangcai")
    @RequestMapping(value = "/collector-post-list", method = RequestMethod.POST)
    public ResponseEntitySpec<List<SysDicItemResponse>> getCollectorPostList() {
        return ResponseEntitySpecBuilder.success(collectionService.getCollectionPostInfoList());
    }



    @ApiOperation(value = "审核人员编辑(调度)，Author: zengxiangcai")
    @RequestMapping(value = "/collector-scheduling", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> scheduleCollectors(
            @RequestBody CollectionPostRequest request) {
        return ResponseEntitySpecBuilder
                .success(collectionService.scheduleCollector(request));
    }


    /***
     * d0催收列表（后扩展为D-1,D-2）
     */
    @ApiOperation(value = "d0催收列表（后扩展为D-1,D-2）列表，Author: zengxiangcai")
    @RequestMapping(value = "/orders/assignable-D0-list", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<CollectionOrderResponse>>> getAssignableD0OrderList(
            @RequestBody AssignableCollectionOrderReq request) {
        request.setStatus(OrdStateEnum.RESOLVING_NOT_OVERDUE);
        return ResponseEntitySpecBuilder
                .success(collectionService.getAssignableCollectionOrderList(request));
    }

    /***
     * 其他非d0催收列表 （如果是分配质检，订单包括D0之前的)
     */
    @ApiOperation(value = "逾期可分配订单列表，Author: zengxiangcai")
    @RequestMapping(value = "/orders/assignable-overdue-list", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<CollectionOrderResponse>>> getAssignableOverdueOrderList(
            @RequestBody AssignableCollectionOrderReq request) {
        if (request.getSourceType().equals(0)) {
            request.setStatus(OrdStateEnum.RESOLVING_OVERDUE);
        }
        return ResponseEntitySpecBuilder
                .success(collectionService.getAssignableCollectionOrderList(request));
    }

    /***
     * 催收订单分配（包括委外订单分配）1
     */
    @ApiOperation(value = "分配催收订单，Author: zengxiangcai")
    @RequestMapping(value = "/order-assignment", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> assignCollectionOrder(@RequestBody CollectionAssignmentRequest request) {
        for (String s : request.getOrderUUIDs()) {
            collectionService.assignCollectionOrder(request.getOutsourceId(),s, request.getIsThird(), request.getSourceType());
        }
        return ResponseEntitySpecBuilder.success();
    }


    /**
     * 逾期订单列表-内（不含权限）1
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "逾期订单列表-内")
    @RequestMapping(value = "/outCollectionsList", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<OutCollectionResponse>>> outCollectionsList(
            @RequestBody OverdueOrderRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.outCollectionsList(param));
    }

    /**
     * 查出催收列表当天为发薪日的单子 1
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "查出催收列表当天为发薪日的单子")
    @RequestMapping(value = "/payDayOrderCount", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> payDayOrderCount(
            @RequestBody OverdueOrderRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.payDayOrderCount(param));
    }
    /**
     * 逾期订单列表-内（包含权限）1
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "逾期订单列表-内")
    @RequestMapping(value = "/outCollectionsByOutSourceIdList", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<OutCollectionResponse>>> outCollectionsByOutSourceIdList(
            @RequestBody OverdueOrderRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.outCollectionsByOutSourceIdList(param));
    }

    /**
     * 催收订单详情备注 1
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "催收订单详情备注")
    @RequestMapping(value = "/manOrderRemarkList", method = RequestMethod.POST)
    public ResponseEntitySpec<List<ManOrderRemarkResponse>> manOrderRemarkList(
            @RequestBody OrderSearchRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.manOrderRemarkList(param));
    }

    /**
     * 已还款订单列表-内 1
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "已还款订单列表-内")
    @RequestMapping(value = "/repaymentOrderList", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<PaymentOrderResponse>>> repaymentOrderList(
            @RequestBody PaidOrderRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.repaymentOrderList(param));
    }

    /**
     * 已还款订单列表-内 (只查催收人员是自己的) 1
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "已还款订单列表-内 (只查催收人员是自己的)")
    @RequestMapping(value = "/repaymentOrderByOutSourceIdList", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<PaymentOrderResponse>>> repaymentOrderByOutSourceIdList(
            @RequestBody PaidOrderRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.repaymentOrderByOutSourceIdList(param));
    }



    /***
     * 委外催收分配的列表 1
     */
    @ApiOperation(value = "委外催收分配的列表，Author: tonggen")
    @RequestMapping(value = "/orders/assignable-overdue-list-outSource", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<CollectionOrderResponse>>> getAssignableOverdueOutSourceOrderList(
            @RequestBody AssignableCollectionOrderReq request) {
//        request.setStatus(OrdStateEnum.RESOLVING_OVERDUE);
        return ResponseEntitySpecBuilder
                .success(collectionService.getAssignableOverdueOutSourceOrderList(request));
    }

    /***
     * 查询委外人员信息
     */

    @ApiOperation(value = "查询委外人员信息，Author: zengxiangcai")
    @RequestMapping(value = "/collector-outPost-list", method = RequestMethod.POST)
    public ResponseEntitySpec<List<CollectorResponseInfo>> getOutSourceInfo(
            @RequestBody CollectorRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionService.getOutSourceInfo(param.getOutSourceId()));
    }

    /***
     * 根据订单号，将其加入催收黑名单
     */
    @ApiOperation(value = "加入催收黑名单，Author: tonggen")
    @RequestMapping(value = "/addCollectionBlackList", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> addCollectionBlackList(@RequestBody OrderSearchRequest request)
            throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder.success(collectionService.
                addCollectionBlackList(request.getUuid(), request.getOrderType(), request.getSourceType()));
    }

    @ApiOperation(value = "根据postId 获取每个阶段催收的评分，Author: tonggen", notes = "")
    @RequestMapping(value = "/getScoreReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getScoreReport(@RequestParam String sessionId,
                                                     @RequestBody ManQualityRecordRequest param) throws Exception{
        return ResponseEntitySpecBuilder
                .success(collectionService.getScoreReport(param.getPostId(), sessionId));
    }
}
