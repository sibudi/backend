package com.yqg.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.third.mobox.MoboxService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by wanghuaizhou on 2019/2/28.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/system")
public class MoboxController {

    @Autowired
    private MoboxService moboxService;

    @ApiOperation("接受同盾回调")
    @RequestMapping(value = "/getNotiCallback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<JSONObject> getNotiCallback(@RequestParam Map<String, String> params)
            throws Exception {
        this.moboxService.getNotiCallback(params);
        return ResponseEntitySpecBuilder.successForP2P();
    }

}
