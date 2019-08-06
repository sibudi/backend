package com.yqg.manage.controller.order;

import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.enums.CouponTypeEnum;
import com.yqg.manage.service.order.ManCouponService;
import com.yqg.manage.service.order.request.CouponBaseRequest;
import com.yqg.manage.service.order.request.CouponRequest;
import com.yqg.manage.service.order.request.CouponSendRequest;
import com.yqg.service.third.upload.response.UploadResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:29 PM
 */
@RestController
@Api(tags = "优惠券管理")
@RequestMapping("/manage")
public class ManCouponController {

    @Autowired
    private ManCouponService manCouponService;

    @ApiOperation("添加优惠券")
    @RequestMapping(value = "/addOrUpdateCoupon", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> addOrUpdateCoupon(@RequestBody CouponBaseRequest request) throws Exception{

        return ResponseEntitySpecBuilder.success(manCouponService.addOrUpdateCoupon(request));
    }

    @ApiOperation("删除优惠券")
    @RequestMapping(value = "/deleteOrUpdateCoupon", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> deleteOrUpdateCoupon(@RequestBody CouponBaseRequest request) throws Exception{

        manCouponService.deleteOrUpdateCoupon(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("获取优惠券列表")
    @RequestMapping(value = "/listCouponConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> listCouponConfig(@RequestBody CouponBaseRequest request) throws Exception{

        return ResponseEntitySpecBuilder.success(manCouponService.listCouponConfig(request));
    }

    @ApiOperation("手动发放优惠券")
    @RequestMapping(value = "/sendCoupons", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> sendCoupons(@RequestBody CouponSendRequest request) throws Exception{

        return ResponseEntitySpecBuilder.success(manCouponService.sendCoupons(request));
    }

    @ApiOperation("获取优惠券统计记录列表")
    @RequestMapping(value = "/listCouponRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> listCouponRecord(@RequestBody CouponRequest request) throws Exception{

        return ResponseEntitySpecBuilder.success(manCouponService.listCouponRecord(request));
    }


    @ApiOperation("自动发放优惠券")
    @RequestMapping(value = "/sendCouponsAuto/managerTask", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntitySpec<Integer> sendCouponsAuto(@RequestParam("request") CouponTypeEnum request) throws Exception{

        return ResponseEntitySpecBuilder.success(manCouponService.sendCouponsAuto(request));
    }

    /**
     * twilio定时任务跑外呼提醒发送优惠券
     * @return
     */
    @RequestMapping(value = "/sendTwilioCallCouponAuto/managerTask", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> sendCouponAutoTask(@RequestParam("request") CouponTypeEnum request){

        manCouponService.sendCouponAutoTask(request);
        return ResponseEntitySpecBuilder.success();
    }

    /**
     * 定时将过期优惠状态进行改变
     * @return
     */
    @RequestMapping(value = "/updateCouponStatus/managerTask", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> updateCouponStatus(){

        manCouponService.updateCouponStatus();
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("手动发送批量优惠券")
    @RequestMapping(value = "/sendCouponsExcel", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> sendCouponsExcel(@RequestParam MultipartFile file) throws Exception {

        return ResponseEntitySpecBuilder.success(manCouponService.sendCouponsExcel(file));
    }

    @ApiOperation("下载批量发送优惠券模板")
    @RequestMapping(value = "/downloadCouponTemplateExcel", method = RequestMethod.GET)
    public void downloadCouponTemplateExcel(HttpServletResponse response) throws Exception {

        manCouponService.downloadCouponTemplateExcel(response);
    }

}
