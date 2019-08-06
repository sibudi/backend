package com.yqg.manage.controller.collection;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.check.ManReceiverSchedulingService;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

/**
 * @program: microservice
 * @description: 质检语音管理
 * @author: 许金泉
 * @create: 2019-04-03 15:29
 **/
@H5Request
@RestController
@Api(tags = "当前催收排班")
@Slf4j
public class ReceiverSchedulingController {

    @Autowired
    private ManReceiverSchedulingService manReceiverSchedulingService;

    @ApiOperation("当前催收排班列表")
    @RequestMapping(value = "/manage/ReceiverSchedulingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> getReceiverSchedulingList(@RequestBody ReceiverSchedulingRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.manReceiverSchedulingService.getReceiverSchedulingList(request));
    }

    @ApiOperation("删除当前催收排班信息")
    @RequestMapping(value = "/manage/DeleteReceiverScheduling", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Boolean> deleteReceiverScheduling(@RequestBody List<String> uuidList) throws Exception {
        boolean result = this.manReceiverSchedulingService.deleteReceiverScheduling(uuidList) > 0;
        return ResponseEntitySpecBuilder.success(result);
    }

    @ApiOperation("添加催收排班信息")
    @RequestMapping(value = "/manage/AddReceiverScheduling", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Boolean> addReceiverScheduling(@RequestBody ReceiverSchedulingRequest request) throws Exception {
        boolean result = this.manReceiverSchedulingService.addOrUpdateReceiverScheduling(request) > 0;
        return ResponseEntitySpecBuilder.success(result);
    }

    @ApiOperation("修改催收排班信息")
    @RequestMapping(value = "/manage/UpdateReceiverScheduling", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Boolean> updateReceiverScheduling(@RequestBody ReceiverSchedulingRequest request) throws Exception {
        if (request.getId() == null) {
            throw new IllegalArgumentException("传入参数有误！");
        }
        boolean result = this.manReceiverSchedulingService.addOrUpdateReceiverScheduling(request) > 0;
        return ResponseEntitySpecBuilder.success(result);
    }

    @ApiOperation("下载催收排班模板")
    @RequestMapping(value = "/manage/DownloadReceiverSchedulingTemplateExcel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public void downloadReceiverSchedulingTemplateExcel(HttpServletResponse response) throws Exception {
         this.manReceiverSchedulingService.downloadReceiverSchedulingTemplateExcel(response);
    }

    @ApiOperation("导入Excel数据")
    @RequestMapping(value = "/manage/importReceiverSchedulingDataByExcel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Integer> importReceiverSchedulingDataByExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntitySpecBuilder.success(this.manReceiverSchedulingService.importReceiverSchedulingDataByExcel(file));
    }

}
