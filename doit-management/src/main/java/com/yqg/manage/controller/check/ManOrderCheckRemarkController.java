package com.yqg.manage.controller.check;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.scheduling.check.request.SaveTeleReviewRequest;
import com.yqg.manage.scheduling.check.request.TeleReviewRequest;
import com.yqg.manage.service.check.ManOrderCheckRemarkService;
import com.yqg.manage.service.check.request.CheckRequest;
import com.yqg.manage.service.check.response.FirstCheckRemarkResponse;
import com.yqg.manage.service.check.response.ManSecondRecordResponse;
import com.yqg.manage.service.check.response.TeleReviewCheckResponse;
import com.yqg.manage.service.order.ManOrderBlackService;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.user.request.ManButtonPermissionRequest;
import com.yqg.order.entity.OrdOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "订单审核管理")
public class ManOrderCheckRemarkController {

    @Autowired
    private ManOrderCheckRemarkService manOrderCheckRemarkService ;

    @Autowired
    private ManOrderBlackService manOrderBlackService;

    @ApiOperation("根据订单uuid查询审核意见")
    @RequestMapping(value = "/manage/CheckRemarkListByOrderNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<FirstCheckRemarkResponse>> getCheckRemarkListByOrderNo(@RequestBody ManOrderListSearchResquest orderListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderCheckRemarkService.getCheckRemarkListByOrderNo(orderListSearchResquest));
    }

    @ApiOperation("订单初审")
    @RequestMapping(value = "/manage/firstCheck", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> firstCheck(@RequestBody CheckRequest orderListSearchResquest)
            throws Exception {
        this.manOrderCheckRemarkService.firstCheck(orderListSearchResquest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("订单复审")
    @RequestMapping(value = "/manage/secondCheck", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> secondCheck(@RequestBody CheckRequest orderListSearchResquest)
            throws Exception {
        this.manOrderCheckRemarkService.secondCheck(orderListSearchResquest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("获得电核问题及答案")
    @RequestMapping(value = "/manage/getTeleReviewQuestion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getTeleReviewQuestion(@RequestBody TeleReviewRequest teleReviewRequest)
            throws Exception {
        TeleReviewCheckResponse response = this.manOrderCheckRemarkService.getTeleReviewQuestion(teleReviewRequest);
        return ResponseEntitySpecBuilder.success(response);
    }

    @ApiOperation("查询禁用哪个电核对象")
    @RequestMapping(value = "/manage/getTeleReviewObj", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<String> getTeleReviewObj(@RequestBody TeleReviewRequest teleReviewRequest)
            throws Exception {
        String response = this.manOrderCheckRemarkService.getTeleReviewObj(teleReviewRequest);
        return ResponseEntitySpecBuilder.success(response);
    }

    @ApiOperation("保存电核问题及答案")
    @RequestMapping(value = "/manage/saveTeleReviewResult", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> saveTeleReviewResult(@RequestBody SaveTeleReviewRequest request)
            throws Exception {
        this.manOrderCheckRemarkService.saveTeleReviewResult(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("回显电核问题")
    @RequestMapping(value = "/manage/getTeleReviewRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getTeleReviewRecords(@RequestBody TeleReviewRequest teleReviewRequest)
            throws Exception {
        TeleReviewCheckResponse response = this.manOrderCheckRemarkService.getTeleReviewRecords(teleReviewRequest);
        return ResponseEntitySpecBuilder.success(response);
    }

    @ApiOperation("查询订单机审拒绝原因")
    @RequestMapping(value = "/manage/searchRejectReason", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<String> searchRejectReason(@RequestBody ManButtonPermissionRequest request)
            throws Exception {
        String response = this.manOrderBlackService.searchRejectReason(request);
        return ResponseEntitySpecBuilder.success(response);
    }

    @ApiOperation("批量归档脚本")
    @RequestMapping(value = "/manage/saveOrderMogo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> saveOrderMogo(@RequestBody ManButtonPermissionRequest request)
            throws Exception {
        manOrderCheckRemarkService.saveOrderMogo(request.getOrderNo());
        return ResponseEntitySpecBuilder.success();
    }


    @ApiOperation("订单号查询复审订单操作标签")
    @RequestMapping(value = "/manage/getOperatorType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<ManSecondRecordResponse>> getOperatorType(@RequestBody TeleReviewRequest teleReviewRequest)
            throws Exception {
        List<ManSecondRecordResponse> response = this.manOrderCheckRemarkService.getOperatorType(teleReviewRequest);
        return ResponseEntitySpecBuilder.success(response);
    }

    @ApiOperation("插入订单复审操作标签")
    @RequestMapping(value = "/manage/saveOperatorType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> saveOperatorType(@RequestBody TeleReviewRequest teleReviewRequest)
            throws Exception {
        Integer response = this.manOrderCheckRemarkService.saveOperatorType(teleReviewRequest);
        return ResponseEntitySpecBuilder.success(response);
    }

    @ApiOperation("手动订单归档")
    @RequestMapping(value = "/manage/saveOrderInfoToMongo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> saveOrderInfoToMongo(@RequestBody OrdOrder request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(this.manOrderCheckRemarkService.saveOrderInfoToMongoByHand(request));
    }

    @ApiOperation("手动删除订单归档")
    @RequestMapping(value = "/manage/deleteOrderDataMongo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> deleteOrderDataMongo(@RequestBody OrdOrder request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(this.manOrderCheckRemarkService.deleteOrderDataMongo(request.getUuid()));
    }
}
