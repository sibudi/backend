package com.yqg.controller.order;

import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.order.UploadInfoService;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.order.request.UploadCallRecordsRequest;
import com.yqg.service.order.request.UploadContactRequest;
import com.yqg.service.order.request.UploadMsgsRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wanghuaizhou on 2017/11/24.
 */
@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadInfoController {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UploadInfoService uploadInfoService;

    @ApiOperation("上传用户通讯录")
    @RequestMapping(value = "/uploadContacts", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadContacts(HttpServletRequest request, @RequestBody UploadContactRequest uploadContactRequest) throws Exception {

        //UserSessionUtil.filter(request,this.redisClient,uploadContactRequest);
        log.info("-----上传通讯录,订单号{},客户端类型{},版本号{}",uploadContactRequest.getOrderNo(),uploadContactRequest.getClient_type(),uploadContactRequest.getClient_version());
        this.uploadInfoService.uploadContacts(uploadContactRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("上传用户短信")
    @RequestMapping(value = "/uploadMsgs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadMsgs(HttpServletRequest request, @RequestBody UploadMsgsRequest uploadMsgsRequest) throws Exception {

        //UserSessionUtil.filter(request,this.redisClient,uploadMsgsRequest);
        log.info("-----上传短信,订单号{},客户端类型{},版本号{}",uploadMsgsRequest.getOrderNo(),uploadMsgsRequest.getClient_type(),uploadMsgsRequest.getClient_version());
        this.uploadInfoService.uploadMsgs(uploadMsgsRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("上传用户安装的app")
    @RequestMapping(value = "/uploadApps", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadApps(HttpServletRequest request, @RequestBody UploadAppsRequest uploadAppsRequest) throws Exception {

        //UserSessionUtil.filter(request,this.redisClient,uploadAppsRequest);
        log.info("-----上传安装的app,订单号{},客户端类型{},版本号{}",uploadAppsRequest.getOrderNo(),uploadAppsRequest.getClient_type(),uploadAppsRequest.getClient_version());
        this.uploadInfoService.uploadApps(uploadAppsRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("上传用户通话记录")
    @RequestMapping(value = "/uploadCallRecords", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadCallRecords(HttpServletRequest request, @RequestBody UploadCallRecordsRequest uploadCallRecordsRequest) throws Exception {

        UserSessionUtil.filter(request,this.redisClient,uploadCallRecordsRequest);
        log.info("-----上传用户通话记录,订单号{},客户端类型{},版本号{}",uploadCallRecordsRequest.getOrderNo(),uploadCallRecordsRequest.getClient_type(),uploadCallRecordsRequest.getClient_version());
        this.uploadInfoService.uploadCallRecords(uploadCallRecordsRequest);
        return ResponseEntityBuilder.success();
    }
}
