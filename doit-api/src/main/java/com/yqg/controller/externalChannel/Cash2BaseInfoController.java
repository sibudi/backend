package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.CashCashRequest;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2BaseInfo;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.service.BaseInfoService;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/5
 * @Email zengxiangcai@yishufu.com
 * 基本信息
 ****/
@Slf4j
@CashCashRequest
@RestController
@RequestMapping("/external/cash2")
public class Cash2BaseInfoController {

    @Autowired
    private BaseInfoService baseInfoService;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private Cash2Config cash2Config;

    /****
     * 基本信息推送
     * @param request
     * @return
     */
    @RequestMapping(value = "/baseInfo", method = RequestMethod.POST)
    public Cash2Response test(@RequestBody Cash2ApiParam request) {

        Cash2BaseInfo baseInfo = request.getDecryptData(Cash2BaseInfo.class,cash2Config);
        //提取基础信息按照Do-It流程保存数据
        try {
            if (baseInfo.getIsReloan() == 0) {
                baseInfoService.addBaseInfo(baseInfo);
            } else {
                baseInfoService.reBorrowing(baseInfo);
            }

        } catch (ServiceException e) {
            log.error("apply loan error server exception", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.PARAM_TYPE_ERROR_1002)
                .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("apply loan error", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001);
        }

        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(null);
    }


    @ApiOperation("获取h5合同接口")
    @RequestMapping(value = "/getH5Contract", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Cash2Response getH5Contract(@RequestBody Cash2ApiParam request)
        throws ServiceException {
        log.info("获取h5合同接口");
//        Cash2H5ContractRequest cash2H5ContractRequest = request.getDecryptData(Cash2H5ContractRequest.class);
        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1)
            .withData(baseInfoService.getH5Contract(redisClient));
    }

}
