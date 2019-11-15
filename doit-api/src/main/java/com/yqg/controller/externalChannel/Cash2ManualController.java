package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.externalChannel.request.Cash2ManualRequest;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Created by wanghuaizhou on 2019/1/16.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/cash2/manual")
public class Cash2ManualController {

    @Autowired
    Cash2OrderService cash2OrderService;

    @ApiOperation("手动回调订单状态")
    @RequestMapping(value = "/ordStatusFeedbackManual", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec ordStatusFeedbackManual(@RequestBody Cash2ManualRequest cash2ManualRequest) throws Exception {
        log.info("手动回调订单状态");
        this.cash2OrderService.ordStatusFeedbackManual(cash2ManualRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("手动回调审核状态")
    @RequestMapping(value = "/ordCheckResultFeedbackManual", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec ordCheckResultFeedbackManual(@RequestBody Cash2ManualRequest cash2ManualRequest) throws Exception {
        log.info("手动回调审核状态");
        this.cash2OrderService.ordCheckResultFeedbackManual(cash2ManualRequest);
        return ResponseEntitySpecBuilder.success();
    }

}
