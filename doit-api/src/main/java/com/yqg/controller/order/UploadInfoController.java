package com.yqg.controller.order;

import javax.servlet.http.HttpServletRequest;

import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.order.request.UploadContactRequest;
import com.yqg.service.order.request.UploadMsgsRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wanghuaizhou on 2017/11/24.
 */
@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadInfoController {

    @ApiOperation("上传用户通讯录")
    @RequestMapping(value = "/uploadContacts", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadContacts(HttpServletRequest request, @RequestBody UploadContactRequest uploadContactRequest) throws Exception {
        //ahalim remove usercontact
        log.info("Deprecated - uploadContacts, order number {}, client type {}, version number",uploadContactRequest.getOrderNo(),uploadContactRequest.getClient_type(),uploadContactRequest.getClient_version());
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("上传用户短信")
    @RequestMapping(value = "/uploadMsgs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadMsgs(HttpServletRequest request, @RequestBody UploadMsgsRequest uploadMsgsRequest) throws Exception {
        //ahalim remove usermessage
        log.info("Deprecated - uploadMsgs, order number {}, client type {}, version number{}",uploadMsgsRequest.getOrderNo(),uploadMsgsRequest.getClient_type(),uploadMsgsRequest.getClient_version());
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("上传用户安装的app")
    @RequestMapping(value = "/uploadApps", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> uploadApps(HttpServletRequest request, @RequestBody UploadAppsRequest uploadAppsRequest) throws Exception {
        //ahalim remove userapps
        log.info("Deprecated - uploadApps, order number {}, client type {}, version number{}",uploadAppsRequest.getOrderNo(),uploadAppsRequest.getClient_type(),uploadAppsRequest.getClient_version());
        return ResponseEntityBuilder.success();
    }
}
