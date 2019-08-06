package com.yqg.controller.activity;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.service.activity.ActivityAccountRecordService;
import com.yqg.service.activity.InviteService;
import com.yqg.service.activity.UsrActivityBankService;
import com.yqg.service.activity.request.ActivityAccountRecordReq;
import com.yqg.service.activity.request.InviteListReq;
import com.yqg.service.activity.response.InviteListResp;
import com.yqg.service.user.request.InsertOrderPaymentCodeRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Features:
 * Created by huwei on 18.8.15.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private ActivityAccountRecordService activityAccountRecordService;
    @Autowired
    private UsrActivityBankService usrActivityBankService;



    @ApiOperation("查询用户邀请列表")
    @RequestMapping(value = "/scanUserInviteList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<InviteListResp>> scanUserInviteList(HttpServletRequest request, @RequestBody InviteListReq baseRequest) throws Exception {

        UserSessionUtil.filter(request,this.redisClient,baseRequest);
        log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("查询用户邀请列表");
        return ResponseEntitySpecBuilder.success(this.inviteService.scanUserInviteList(baseRequest));
    }

    @ApiOperation("查询佣金记录表")
    @RequestMapping(value = "/getAccountRecords", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> getAccountRecords(HttpServletRequest request, @RequestBody ActivityAccountRecordReq accountRecordReq) throws Exception {

        UserSessionUtil.filter(request,this.redisClient,accountRecordReq);
        log.info("request body {}", JsonUtils.serialize(accountRecordReq));
        log.info("查询佣金记录表");
        return ResponseEntitySpecBuilder.success(this.activityAccountRecordService.getAccountRecords(accountRecordReq));
    }


    @ApiOperation("手动添加还款码")
    @RequestMapping(value = "/insertOrderPaymentCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<JSONObject> insertOrderPaymentCode(HttpServletRequest request, @RequestBody InsertOrderPaymentCodeRequest insertOrderPaymentCodeRequest) throws Exception {
        log.info("手动添加还款码");
        this.usrActivityBankService.insertOrderPaymentCode(insertOrderPaymentCodeRequest);
        return ResponseEntitySpecBuilder.success();
    }

}
