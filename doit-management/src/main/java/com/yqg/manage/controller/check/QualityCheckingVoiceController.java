package com.yqg.manage.controller.check;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.check.ManQualityCheckingVoiceService;
import com.yqg.manage.service.check.request.QualityCheckingVoiceRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

/**
 * @program: microservice
 * @description: 质检语音管理
 * @author: 许金泉
 * @create: 2019-04-03 15:29
 **/
@H5Request
@RestController
@Api(tags = "质检语音管理")
@Slf4j
public class QualityCheckingVoiceController {

    @Autowired
    private ManQualityCheckingVoiceService qualityCheckingVoiceService;

    @ApiOperation("质检语音列表")
    @RequestMapping(value = "/manage/QualityCheckingVoiceList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> getCheckRemarkListByOrderNo(@RequestBody QualityCheckingVoiceRequest qualityCheckingVoiceRequest) throws Exception {
        return ResponseEntitySpecBuilder.success(this.qualityCheckingVoiceService.getQualityCheckingVoiceList(qualityCheckingVoiceRequest));
    }

    @ApiOperation("质检语音下载")
    @RequestMapping(value = "/manage/downloadVoiceFile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public void downloadVoiceFile(@RequestParam String uuidList, HttpServletResponse response) throws Exception {
        this.qualityCheckingVoiceService.downloadVoiceFile(Arrays.asList(uuidList.split(",")), response);
    }


}
