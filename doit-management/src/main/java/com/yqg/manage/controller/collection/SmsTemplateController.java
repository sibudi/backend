package com.yqg.manage.controller.collection;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.collection.SmsTemplateService;
import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import com.yqg.service.system.service.SmsRemindService;
import com.yqg.system.entity.SysParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Author: tonggen
 * Date: 2018/3/26
 * time: 下午3:32
 */
@RestController
@RequestMapping("/manage/smsTemplate")
@Api("催收短信模板")
@Log4j
public class SmsTemplateController {

    @Autowired
    private SmsTemplateService smsTemplateService;
    @Autowired
    private SmsRemindService smsRemindService;

    @ApiOperation("更新或者新增模板")
    @RequestMapping(value = "/insertOrUpdateSmsTemp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> insertOrUpdateSmsTemp(@RequestBody SmsTemplateRequest request){
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.insertOrUpdateSmsTemp(request));
    }

    @ApiOperation("删除模板")
    @RequestMapping(value = "/deleteSmsTemp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> deleteSmsTemp(@RequestBody SmsTemplateRequest request){
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.deleteSmsTemp(request));
    }

    @ApiOperation("获得短信模板列表")
    @RequestMapping(value = "/smsTemplateList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> smsTemplateList(@RequestBody SmsTemplateRequest request){
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.smsTemplateList(request));
    }

    @ApiOperation("给待放款用户发送短信")
    @RequestMapping(value = "/sendSmsToLoanUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> sendSmsToLoanUser(HttpServletRequest request){

        log.info("sendSmsToLoanUser begin");
        this.smsRemindService.sendLoanOrderSms();
        log.info("sendSmsToLoanUser end");
        return ResponseEntitySpecBuilder
                .success();
    }

    @ApiOperation("管理催收一键短信开关查询")
    @RequestMapping(value = "/listCollectionSmsSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> listCollectionSmsSwitch() {
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.listCollectionSmsSwitch());
    }

    @ApiOperation("备用联系人开关查询")
    @RequestMapping(value = "/listContactSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> listContactSwitch() {
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.listContactSwitch());
    }

    @ApiOperation("管理催收一键短信开关条件查询")
    @RequestMapping(value = "/getCollectionSmsSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> getCollectionSmsSwitch(@RequestBody SmsTemplateRequest request) {
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.getCollectionSmsSwitch(request));
    }

    @ApiOperation("查询备用联系人是否展示")
    @RequestMapping(value = "/getContactSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> getContactSwitch(@RequestBody SmsTemplateRequest request) {
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.getContactSwitch(request));
    }

    @ApiOperation("管理催收一键短信开关修改")
    @RequestMapping(value = "/updateCollectionSmsSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> updateCollectionSmsSwitch(@RequestBody SysParam sysParam) {
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.updateCollectionSmsSwitch(sysParam));
    }

    @ApiOperation("管理联系人开关修改")
    @RequestMapping(value = "/updateContactSwitch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> updateContactSwitch(@RequestBody SysParam sysParam) {
        return ResponseEntitySpecBuilder
                .success(smsTemplateService.updateContactSwitch(sysParam));
    }
}
