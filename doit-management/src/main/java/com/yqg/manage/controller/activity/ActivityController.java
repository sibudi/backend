package com.yqg.manage.controller.activity;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.service.user.response.ManSysLoginResponse;
import com.yqg.service.activity.ActivityAccountRecordService;
import com.yqg.service.activity.request.ActivityAccountRecordReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * @author huanhuan
 */
@RestController
@H5Request
@Api(tags = "ActivityController")
@Slf4j
public class ActivityController {

    @Autowired
    private ActivityAccountRecordService activityAccountRecordService;
    @Autowired
    private RedisClient redisClient;

    @ApiOperation("待转账、已转账 、转账失败 列表")
    @RequestMapping(value = "/manage/activity/withdrawList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> withdrawList(HttpServletRequest request, @RequestBody ActivityAccountRecordReq accountRecordReq)
            throws Exception {
        log.info("待转账、已转账 、转账失败 列表");
        return ResponseEntitySpecBuilder.success(this.activityAccountRecordService.getAccountRecords(accountRecordReq));
    }
    @ApiOperation("提现列表")
    @RequestMapping(value = "/manage/activity/withdrawRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> withdrawRecord(HttpServletRequest request, @RequestBody ActivityAccountRecordReq accountRecordReq)
            throws Exception {
        log.info("提现列表");
        return ResponseEntitySpecBuilder.success(this.activityAccountRecordService.withdrawRecord(accountRecordReq));
    }
    @ApiOperation("转账成功")
    @RequestMapping(value = "/manage/activity/withdrawSuccess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> withdrawSuccess(HttpServletRequest request, @RequestBody ActivityAccountRecordReq accountRecordReq)
            throws Exception {
        log.info("转账成功");
        log.info("request body {}", JsonUtils.serialize(accountRecordReq));
        ManSysLoginResponse response= JsonUtils.deserialize(redisClient.get(RedisContants.MANAGE_SESSION_PREFIX + accountRecordReq.getSessionId()), ManSysLoginResponse.class);
        accountRecordReq.setUid(response.getId());
        this.activityAccountRecordService.withdrawSuccess(accountRecordReq);
        return ResponseEntitySpecBuilder.success();
    }
    @ApiOperation("转账失败")
    @RequestMapping(value = "/manage/activity/withdrawFail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> withdrawFail(HttpServletRequest request, @RequestBody ActivityAccountRecordReq accountRecordReq)
            throws Exception {
        log.info("转账失败");
        log.info("request body {}", JsonUtils.serialize(accountRecordReq));
        ManSysLoginResponse response= JsonUtils.deserialize(redisClient.get(RedisContants.MANAGE_SESSION_PREFIX + accountRecordReq.getSessionId()), ManSysLoginResponse.class);
        accountRecordReq.setUid(response.getId());
        this.activityAccountRecordService.withdrawFail(accountRecordReq);
        return ResponseEntitySpecBuilder.success();
    }


}
