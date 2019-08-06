package com.yqg.controller.system;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.system.request.*;
import com.yqg.service.system.response.*;
import com.yqg.service.system.service.SysPaymentChannelService;
import com.yqg.service.system.service.SystemService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.util.List;


/**
 * Created by wanghuaizhou on 2017/11/24.
 */
@RestController
@Slf4j
@RequestMapping("/system")
public class SystemController {


    @Autowired
    private SystemService systemService;
    @Autowired
    private SysPaymentChannelService sysPaymentChannelService;
    @Autowired
    private RedisClient redisClient;

//    @ApiOperation("app更新接口")
//    @RequestMapping(value = "/isUpdate", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<SysAppVersionModel> isUpdate(HttpServletRequest request, @RequestBody SysAppVersionRequest sysAppVersionRequest) throws Exception {
//
//        //log.info("request body {}", JsonUtils.serialize(sysAppVersionRequest));
//        log.info("app更新接口");
//        return ResponseEntityBuilder.success( this.systemService.checkUpdate(sysAppVersionRequest));
//    }

    @ApiOperation("获取app H5 url集合")
    @RequestMapping(value = "/appH5UrlValueList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<SysAppH5Response>> getUrlList(HttpServletRequest request, @RequestBody BaseRequest baseRequest
    ) throws Exception {
        //log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("获取app H5 url集合");
        return ResponseEntityBuilder.success(this.systemService.getUrlList(redisClient));
    }

    @ApiOperation("所有的开户行列表")
    @RequestMapping(value = "/getBankInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<SysBankBasicInfoResponse>> getBankInfo(HttpServletRequest request, @RequestBody BaseRequest baseRequest
    ) throws ServiceException {
//        UserSessionUtil.filter(request,this.redisClient,baseRequest);
//        log.info("request body {}", JsonUtils.serialize(baseRequest));
        log.info("所有的开户行列表");
        return ResponseEntityBuilder.success(systemService.getBankInfo(baseRequest,redisClient));
    }

    @ApiOperation("获取支付通道列表")
    @RequestMapping(value = "/getPaymentChannelList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<SysPaymentChannelResponse>> getPaymentChannelList(HttpServletRequest request, @RequestBody SysPaymentChannelRequest channelRequest)
            throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,channelRequest);
//        log.info("request body {}", JsonUtils.serialize(channelRequest));
        log.info("获取支付通道列表");
        return ResponseEntityBuilder.success( sysPaymentChannelService.getSysPaymentChennelList(channelRequest));
    }

    @ApiOperation("是否开启APP上传，仅供iOS客户端使用")
    @RequestMapping(value = "/isUploadUserApps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<SysCheakAppsResponse> isUploadUserApps(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
            throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,baseRequest);
        log.info("是否开启APP上传，仅供iOS客户端使用");
        return ResponseEntityBuilder.success(this.systemService.isUploadUserApps(baseRequest));
    }

    @ApiOperation("通过父字典的DicCode查询子字典List")
    @RequestMapping(value = "/getDicItemListByDicCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> dicItemListByDicCode(@RequestBody DictionaryRequest request)
            throws Exception {
        return ResponseEntityBuilder.success(this.systemService.dicItemListByDicCode(request));
    }

    @ApiOperation("获取分享的内容")
    @RequestMapping(value = "/getShareData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<SysShareDataResponse> getShareData(HttpServletRequest httpRequest, @RequestBody BaseRequest baseRequest) throws Exception{
        log.info("获取分享的内容");
        return ResponseEntityBuilder.success(this.systemService.getShareData(baseRequest,1));
    }
    @ApiOperation("获取分享的二维码")
    @RequestMapping(value = "/getShareQRCode",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<SysShareDataResponse> getShareQRCode(HttpServletRequest httpRequest, @RequestBody BaseRequest baseRequest) throws Exception{
        log.info("获取分享的二维码");
        log.info(JsonUtils.serialize(baseRequest));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/logo.png");
        File logoFile = new File("logo.png");
        FileUtils.copyInputStreamToFile(stream, logoFile);
        return ResponseEntityBuilder.success(this.systemService.getShareQRCode(baseRequest,logoFile));
    }


    @ApiOperation("获取学校列表")
    @RequestMapping(value = "/getSchoolList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<SysSchoolListResponse> > getShareData(HttpServletRequest httpRequest, @RequestBody SysSchoolListRequest schoolListRequest) throws Exception{

        log.info("获取学校列表");
        return ResponseEntityBuilder.success(this.systemService.getSchoolNameWithKey(schoolListRequest.getNameStr()));
    }

}
