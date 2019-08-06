package com.yqg.manage.controller.collection;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.collection.CollectionAutoAssignmentService;
import com.yqg.manage.service.collection.request.D0AutoAssignmentRequest;
import com.yqg.manage.service.collection.request.OverdueAssignmentStatisticsRequest;
import com.yqg.manage.service.collection.request.OverdueAutoAssignmentRequest;
import com.yqg.manage.service.collection.response.D0CollectionStatisticsResponse;
import com.yqg.manage.service.collection.response.OverdueCollectionStatisticsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@RestController
@RequestMapping("/manage/collection")
@H5Request
@Api(tags = "催收自动分配相关类")
public class AutoAssignmentController {

    @Autowired
    private CollectionAutoAssignmentService collectionAutoAssignmentService;

    /***
     * d0，d-1,d-2催收订单自动分配 1
     */
    @ApiOperation(value = "d0，d-1,d-2催收订单自动分配，Author: zengxiangcai")
    @RequestMapping(value = "/D0-auto-order-assignment", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> autoAssignCollectionOrderForD0(
            @RequestBody D0AutoAssignmentRequest request) {
        collectionAutoAssignmentService.autoAssignCollectionOrderForD0(request);
        return ResponseEntitySpecBuilder.success();
    }


    /***
     * 逾期催收订单自动分配 1
     */
    @ApiOperation(value = "逾期催收订单自动分配催收订单，Author: zengxiangcai")
    @RequestMapping(value = "/overdue-auto-order-assignment", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> autoAssignCollectionOrderForOverdue(
            @RequestBody OverdueAutoAssignmentRequest request) {
        collectionAutoAssignmentService.autoAssignCollectionOrderForOverdue(request);
        return ResponseEntitySpecBuilder.success();
    }


    /***
     * d0，d-1,d-2催收订单自动分配统计信息 1
     */
    @ApiOperation(value = "d0，d-1,d-2催收订单自动分配统计信息，Author: zengxiangcai")
    @RequestMapping(value = "/D0-assignment-statistics", method = RequestMethod.POST)
    public ResponseEntitySpec<D0CollectionStatisticsResponse> getD0AssignmentStatistics(
            @RequestBody OverdueAssignmentStatisticsRequest request) {
        return ResponseEntitySpecBuilder
                .success(collectionAutoAssignmentService.getD0CollectionStatistics(request));
    }


    /***
     * 逾期催收订单自动分配统计信息 1
     */
    @ApiOperation(value = "逾期催收订单自动分配统计信息，Author: zengxiangcai")
    @RequestMapping(value = "/overdue-assignment-statistics", method = RequestMethod.POST)
    public ResponseEntitySpec<OverdueCollectionStatisticsResponse> getOverdueAssignmentStatistics(
            @RequestBody OverdueAssignmentStatisticsRequest request) {
        return ResponseEntitySpecBuilder
                .success(collectionAutoAssignmentService
                        .getOverdueAssignmentStatistics(request.getPostId(), request.getSection(),
                                request.getSourceType(), request.getAmountApply(),request.getOtherAmount()));
    }

    /**
     * 手动回收订单分配 1
     * @return
     */
    @RequestMapping(value = "/resetCollectOrderByHand", method = RequestMethod.POST)
    public ResponseEntitySpec<OverdueCollectionStatisticsResponse> resetCollectOrderByHand() {
        collectionAutoAssignmentService.recycleCollectionOrder();
        return ResponseEntitySpecBuilder
                .success();
    }


    /***
     * 委外催收订单自动分配统计信息 1
     */
    @ApiOperation(value = "委外催收订单自动分配统计信息，Author: tonggen")
    @RequestMapping(value = "/overdue-assignment-statistics-outSource", method = RequestMethod.POST)
    public ResponseEntitySpec<OverdueCollectionStatisticsResponse> getOutSourceOverdueAssignmentStatistics(
            @RequestBody OverdueAssignmentStatisticsRequest request) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionAutoAssignmentService
                        .getOutSourceOverdueAssignmentStatistics(request.getOutSourceId(), request.getSourceType()));
    }

}
