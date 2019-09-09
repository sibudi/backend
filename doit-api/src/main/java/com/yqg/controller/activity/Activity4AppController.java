package com.yqg.controller.activity;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.activity.ActivityAccountRecordService;
import com.yqg.service.activity.ActivityAccountService;
import com.yqg.service.activity.InviteService;
import com.yqg.service.activity.UsrActivityBankService;
import com.yqg.service.activity.request.CaseoutReq;
import com.yqg.service.activity.response.UsrActivityAccountResp;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.request.UsrBankRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/activity")
public class Activity4AppController {

    @Autowired
    private SysParamService sysParamervice;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private ActivityAccountRecordService activityAccountRecordService;
    @Autowired
    private ActivityAccountService activityAccountService;
    @Autowired
    private UsrActivityBankService usrActivityBankService;


    @ApiOperation("佣金排行榜")
    @RequestMapping(value = "/getAccountTop10", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<JSONObject>> getAccountTop10(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws Exception {
        log.info("查询佣金账户表------佣金排行榜");
        return ResponseEntityBuilder.success(this.activityAccountService.getAccountTop10(baseRequest));
    }

    @ApiOperation("活动账户提现")
    @RequestMapping(value = "/caseout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<PageData> caseout(HttpServletRequest request, @RequestBody CaseoutReq caseoutRequest) throws Exception {

        log.info("活动账户提现");
        log.info("request body {}", JsonUtils.serialize(caseoutRequest));
        String caseOutMin = this.sysParamervice.getSysParamValue(SysParamContants.ACTIVITY_CASEOUT_MIN);
        if (new BigDecimal(caseoutRequest.getAmount()).compareTo(new BigDecimal(caseOutMin)) == -1)
            throw new ServiceException(ExceptionEnum.CASE_OUT_MIN);
        this.activityAccountRecordService.caseout(caseoutRequest);
        return ResponseEntityBuilder.success();
    }


    @ApiOperation("我的佣金")
    @RequestMapping(value = "/getMyAccount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<JSONObject> getMyAccount(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws Exception {

        log.info("我的佣金");
        return ResponseEntityBuilder.success(this.activityAccountService.getMyAccount(baseRequest));
    }


    @ApiOperation("获取用户提现账户列表")
    @RequestMapping(value = "/getUsrActivityAccountList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<UsrActivityAccountResp>> getUsrActivityBankList(HttpServletRequest request, @RequestBody BaseRequest baseRequest) throws Exception {
        log.info("查询活动账户银行卡");
        return ResponseEntityBuilder.success(this.inviteService.getUsrActivityAccountList(baseRequest));
    }


    @ApiOperation("用户添加或修改活动银行卡")
    @RequestMapping(value = "/addOrChangeBankCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<JSONObject> addOrChangeBankCard(HttpServletRequest request, @RequestBody UsrBankRequest userBankRequest) throws Exception {
        log.info("用户添加或修改活动银行卡");
        this.usrActivityBankService.addOrChangeBankCard(userBankRequest);
        return ResponseEntityBuilder.success();
    }


}
