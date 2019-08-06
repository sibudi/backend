package com.yqg.controller.externalChannel;


import com.yqg.common.annotations.CashCashRequest;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2ExtralInfo;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.service.AdditionalInfoService;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/5
 * @Email zengxiangcai@yishufu.com
 * 扩展信息
 ****/

@Slf4j
@CashCashRequest
@RestController
@RequestMapping("/external/cash2")
public class Cash2AdditionalInfoController {

    @Autowired
    private AdditionalInfoService additionalInfoService;

    @Autowired
    private Cash2Config cash2Config;

    /****
     * 补充信息推送
     * @param request
     * @return
     */
    @RequestMapping(value = "/additional-info", method = RequestMethod.POST)
    public Cash2Response addAdditionalInfo(@RequestBody Cash2ApiParam request) {

        Cash2AdditionalInfoParam additionalInfo = request
            .getDecryptData(Cash2AdditionalInfoParam.class,cash2Config);

        try {
            additionalInfoService.addAdditionalInfo(additionalInfo);
        } catch (ServiceException e) {
            log.error("add additional info service exception", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.PARAM_TYPE_ERROR_1002)
                .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("add additional info exception", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001);
        }

        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(null);
    }

    /***
     * 订单确认后补充的信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/extral-info", method = RequestMethod.POST)
    public Cash2Response addExtralInfo(@RequestBody Cash2ApiParam request) {

        //当前type： PAYROLL/BANK_CARD_RECORD/WORK_PROOF/SIM
        Cash2ExtralInfo extralInfo = request
                .getDecryptData(Cash2ExtralInfo.class, cash2Config);
        try {
            return additionalInfoService.addExtralInfo(extralInfo);
        } catch (ServiceException e) {
            log.error("add extral info service exception", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.PARAM_TYPE_ERROR_1002)
                    .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("add extral info exception", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001);
        }
    }
}
