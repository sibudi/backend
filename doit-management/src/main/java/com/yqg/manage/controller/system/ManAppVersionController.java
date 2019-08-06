package com.yqg.manage.controller.system;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.system.ManAppVersionService;
import com.yqg.manage.service.system.request.ManAppVersionListRequest;
import com.yqg.manage.service.system.request.ManAppVersionRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "ManAppVersionController")
public class ManAppVersionController {

    @Autowired
    private ManAppVersionService manAppVersionService;

    @ApiOperation("添加app版本")
    @RequestMapping(value = "/manage/appVersionAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> addAppVersion(HttpServletRequest request, @RequestBody ManAppVersionRequest sysAppVersionRequest)
            throws Exception {
        this.manAppVersionService.addAppVersion(sysAppVersionRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("查询app版本配置列表")
    @RequestMapping(value = "/manage/appVersionList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> appVersionList(HttpServletRequest request, @RequestBody ManAppVersionListRequest listRequest)
            throws Exception {

        return ResponseEntitySpecBuilder.success(this.manAppVersionService.versionList(listRequest));
    }

    @ApiOperation("修改app版本配置")
    @RequestMapping(value = "/manage/appVersionEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> editVersion(HttpServletRequest request, @RequestBody ManAppVersionRequest sysAppVersionRequest)
            throws Exception {

        this.manAppVersionService.editVersion(sysAppVersionRequest);
        return ResponseEntitySpecBuilder.success();
    }
}
