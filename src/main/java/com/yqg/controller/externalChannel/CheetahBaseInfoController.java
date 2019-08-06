package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.CheetahRequest;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.service.externalChannel.request.CheetahBaseInfo;
import com.yqg.service.externalChannel.request.CheetahOrdInfoEditRequest;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.service.CheetahBaseInfoService;
import com.yqg.service.externalChannel.utils.CheetahResponseBuilder;
import com.yqg.service.externalChannel.utils.CheetahResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: tonggen
 * Date: 2018/12/27
 * time: 5:52 PM
 */
@Slf4j
@CheetahRequest
@RestController
@RequestMapping("/cash-loan/v1")
public class CheetahBaseInfoController {

    @Autowired
    private CheetahBaseInfoService cheetahBaseInfoService;

    /****
     * 基本信息推送
     * @param request
     * @return
     */
    @RequestMapping(value = "/orders/info", method = RequestMethod.POST)
    public CheetahResponse orderInfo(@RequestBody CheetahBaseInfo request) {

        //提取基础信息按照Do-It流程保存数据
        try {
            cheetahBaseInfoService.addBaseInfo(request);
        } catch (ServiceException e) {
            log.error("order info error server exception", e);
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.PARAM_ERROR_1001)
                    .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("order info error", e);
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.SERVER_INTERNAL_ERROR_3001);
        }

        return CheetahResponseBuilder.buildResponse(CheetahResponseCode.CODE_OK_0).withData(null);
    }

    /****
     * 订单驳回接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/orders/info-edit", method = RequestMethod.POST)
    public CheetahResponse infoEdit(@RequestBody CheetahOrdInfoEditRequest request) {

        try {
            return cheetahBaseInfoService.orderInfoEdit(request);

        } catch (ServiceException e) {
            log.error("order edit error server exception", e);
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.PARAM_ERROR_1001)
                    .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("order edit error", e);
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.SERVER_INTERNAL_ERROR_3001);
        }

    }

}
