package com.yqg.manage.controller.collection;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.entity.collection.ManQualityCheckConfig;
import com.yqg.manage.service.collection.ManQualityCheckService;
import com.yqg.manage.service.collection.request.ManQualityConfigRequest;
import com.yqg.manage.service.collection.request.ManQualityRecordRequest;
import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import com.yqg.manage.service.collection.response.OutCollectionResponse;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;

/*****
 * @Author tonggen
 * created at ${date}
 * @email tonggen@yishufu.com
 ****/

@RestController
@RequestMapping("/manage/qualityCheck")
@H5Request
@Api(tags = "质检管理类")
public class ManQualityCheckController {

    @Autowired
    private ManQualityCheckService manQualityCheckService;

    @ApiOperation(value = "获取质检类配置，Author: tonggen", notes = "")
    @RequestMapping(value = "/listQualityCheckConfigs", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<ManQualityCheckConfig>>> listQualityCheckConfigs(
            @RequestBody ManQualityConfigRequest param) {
        return ResponseEntitySpecBuilder
                .success(manQualityCheckService.
                        listQualityCheckConfigs(param.getPageNo(),
                                param.getPageSize(), param.getType()));
    }

    @ApiOperation(value = "删除一个质检类配置，Author: tonggen", notes = "")
    @RequestMapping(value = "/deleteQualityCheckConfig", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> deleteQualityCheckConfig(
            @RequestBody ManQualityConfigRequest param) throws Exception{
        return ResponseEntitySpecBuilder
                .success(manQualityCheckService.
                        deleteQualityCheckConfig(param.getId()));
    }

    @ApiOperation(value = "新增或者更新质检配置，Author: tonggen", notes = "")
    @RequestMapping(value = "/insertOrUpdateCheckConfig", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> insertOrUpdateCheckConfig(
            @RequestBody ManQualityConfigRequest param) throws ServiceExceptionSpec {

        manQualityCheckService.
                insertOrUpdateCheckConfig(param);
        return ResponseEntitySpecBuilder.success();
    }

    /**
     * 质检列表（内）
     * @param param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "质检列表（内）")
    @RequestMapping(value = "/listQualityChecks", method = RequestMethod.POST)
    public ResponseEntitySpec<PageData<List<OutCollectionResponse>>> listQualityChecks(
            @RequestBody OverdueOrderRequest param) throws Exception {
        return ResponseEntitySpecBuilder
                .success(manQualityCheckService.listQualityChecks(param));
    }

    @ApiOperation(value = "保存质检记录，Author: tonggen", notes = "")
    @RequestMapping(value = "/insertQualityCheckRecord", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> insertQualityCheckRecord(
            @RequestBody ManQualityRecordRequest param) throws Exception{
        return ResponseEntitySpecBuilder
                .success(manQualityCheckService.
                        insertQualityCheckRecord(param));
    }

    @ApiOperation(value = "根据质检人员或者催收人员获得质检报表，Author: tonggen", notes = "")
    @RequestMapping(value = "/getCheckReport", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getCheckReport(@RequestParam String sessionId,
                                                     @RequestBody ManQualityRecordRequest param) throws Exception{
        return ResponseEntitySpecBuilder
                .success(manQualityCheckService.
                        getCheckReport(param.getOutsourceId(), param.getCheckerId(),
                                param.getStartTime(), param.getEndTime(), sessionId));
    }
}
