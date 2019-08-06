package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.externalChannel.request.Cash2ManualRequest;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Created by wanghuaizhou on 2019/1/17.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/cash-loan/v1")
public class CheetahManualController {

    @Autowired
    private CheetahOrderService cheetahOrderService;

    @ApiOperation("手动回调订单状态")
    @RequestMapping(value = "/ordStatusFeedbackManual", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec ordStatusFeedbackManual(@RequestBody Cash2ManualRequest cash2ManualRequest) throws Exception {
        log.info("手动回调订单状态");
        cheetahOrderService
                .ordStatusFeedbackManual(cash2ManualRequest);
        return ResponseEntitySpecBuilder.success();
    }
}
