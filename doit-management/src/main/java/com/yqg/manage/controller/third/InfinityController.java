package com.yqg.manage.controller.third;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.enums.system.ThirdExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.management.entity.InfinityBillEntity;
import com.yqg.management.entity.InfinityExtnumberEntity;
import com.yqg.service.third.infinity.InfinityService;
import com.yqg.service.third.infinity.request.InfinityRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Features:
 * Created by lijianping on 19.4.1
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/manage/infinity")
public class InfinityController {

    @Autowired
    private InfinityService infinityService;


    @ApiOperation("1.Infinity登录接口，获取token")
    @RequestMapping(value = "/getToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<String> getToken(HttpServletRequest request, @RequestBody InfinityRequest baseRequest) throws Exception {

        log.info("Infinity登录接口，获取token");
        Map<String, String> map = new HashMap<>();

//        this.infinityService.getToken(map);
        return ResponseEntitySpecBuilder.success(this.infinityService.getToken());
    }

    @ApiOperation("2.获取分机列表，拿到分配的分机信息")
    @RequestMapping(value = "/getNumberInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<InfinityExtnumberEntity>> queryNumberInfo(HttpServletRequest request, @RequestBody InfinityRequest numberInfoReq) throws Exception {

        log.info("获取分机列表，拿到分配的分机信息");
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(numberInfoReq.getToken())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        if (StringUtils.isBlank(numberInfoReq.getStatus())) {
            map.put("status", "3");
        } else {
            map.put("status", numberInfoReq.getStatus());
        }
        map.put("token", numberInfoReq.getToken());
        return ResponseEntitySpecBuilder.success(this.infinityService.getExtNumberList(map));
    }


    @ApiOperation("3.获取分机实时状态，来判断是否可呼叫")
    @RequestMapping(value = "/getCallStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<InfinityExtnumberEntity>> getCallStatus(HttpServletRequest request, @RequestBody InfinityRequest req) throws Exception {
        log.info("获取分机实时状态，来判断是否可呼叫 ");

        if (StringUtils.isBlank(req.getToken())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        if (StringUtils.isBlank(req.getExtnumber())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        Map<String, String> map = new HashMap<>();
        map.put("token",this.infinityService.getToken());
        map.put("extnumber",req.getExtnumber());
        List<InfinityExtnumberEntity> list = this.infinityService.getCallStatus(map);
        if(list == null){
            throw new ServiceExceptionSpec(ThirdExceptionEnum.INFINITY_EXTNUMBER_NO);
        }
        return ResponseEntitySpecBuilder.success(list);
    }


    @ApiOperation("4.发起呼叫接口")
    @RequestMapping(value = "/makeCall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Map<String, String>> makeCall(HttpServletRequest request, @RequestBody InfinityRequest req) throws Exception {

        log.info("发起呼叫接口");
        if (StringUtils.isBlank(req.getExtnumber())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        if (StringUtils.isBlank(req.getDestnumber())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        if (StringUtils.isBlank(req.getUuid())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        return ResponseEntitySpecBuilder.success(this.infinityService.makeCall(req));
    }


    @ApiOperation("5.获取话单接口")
    @RequestMapping(value = "/getBill", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<InfinityBillEntity>> getBill(HttpServletRequest request, @RequestBody InfinityRequest req) throws Exception {
        log.info("获取话单接口");
        Map<String, String> map = new HashMap<>();
        map.put("token", this.infinityService.getToken());
        map.put("starttime", DateUtils.getTodayStart().toString());
        map.put("endtime", DateUtils.getTodayEnd().toString());
        if (!StringUtils.isBlank(req.getSyncflag())) {
            map.put("syncflag", req.getSyncflag());
        }
        return ResponseEntitySpecBuilder.success(this.infinityService.getBill(map));
    }


    @ApiOperation("6.获取录音文件接口")
    @RequestMapping(value = "/getRecodeFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Map<String, String>> getRecodeFile(HttpServletRequest request, @RequestBody InfinityRequest req) throws Exception {
        log.info("获取录音文件接口");

        if (StringUtils.isBlank(req.getFilename())) {
            throw new ServiceExceptionSpec(ThirdExceptionEnum.NO_PARAMETERS);
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", this.infinityService.getToken());
        map.put("filename", req.getFilename());
        return ResponseEntitySpecBuilder.success(this.infinityService.getRecodeFile(map));
    }


}

