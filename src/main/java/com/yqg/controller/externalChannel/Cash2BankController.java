package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.CashCashRequest;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2BankStatusRequest;
import com.yqg.service.externalChannel.request.Cash2UserBankRequest;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.service.Cash2BankService;
import com.yqg.service.externalChannel.service.Cash2ResultPushService;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.service.UsrBankService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;


/*****
 * @Author zengxiangcai
 * Created at 2018/3/5
 * @Email zengxiangcai@yishufu.com
 * 银行卡信息
 ****/

@CashCashRequest
@RestController
@Slf4j
@RequestMapping("/external/cash2")
public class Cash2BankController {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UsrBankService usrBankService;

    @Autowired
    private Cash2ResultPushService cash2ResultPushService;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private Cash2BankService cash2BankService ;

    @Autowired
    private Cash2Config cash2Config;

    @ApiOperation("Cash2卡bin校验")
    @RequestMapping(value = "/checkCash2BankCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Cash2Response checkCash2BankCardBin(@RequestBody Cash2ApiParam request)
            throws ServiceException, ServiceExceptionSpec {
//        UserSessionUtil.filter(request, this.redisClient, userBankRequest);
//        log.info("request body{}", JsonUtils.serialize(userBankRequest));
        log.info("Cash2卡bin校验");
        Cash2UserBankRequest cash2UserBankRequest = request.getDecryptData(Cash2UserBankRequest.class,cash2Config);
        UsrBankRequest userBankRequest = new UsrBankRequest();
        userBankRequest.setThirdType(1);
        userBankRequest.setBankNumberNo(cash2UserBankRequest.getBankNumberNo());
        userBankRequest.setBankCardName(cash2UserBankRequest.getBankCardName());
        userBankRequest.setBankCode(cash2UserBankRequest.getBankCode());

        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderRelationByExternalOrderNo(cash2UserBankRequest.getOrderNo());
        userBankRequest.setOrderNo(externalOrderRelation.getOrderNo());
        userBankRequest.setUserUuid(externalOrderRelation.getUserUuid());
        usrBankService.bindBankCard(userBankRequest,redisClient);
        // 线程调用反馈给
        cash2ResultPushService.addTask(externalOrderRelation.getUserUuid());
        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(null);

    }

    @ApiOperation("Cash2获取用户银行卡列表")
    @RequestMapping(value = "/getCash2Banks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Cash2Response getCash2UserBankList(@RequestBody Cash2ApiParam request)
            throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, baseRequest);  UsrBankRequest
//        log.info("request body{}", JsonUtils.serialize(baseRequest));
        log.info("Cash2获取用户银行卡列表");
        Cash2UserBankRequest cash2UserBankRequest = request.getDecryptData(Cash2UserBankRequest.class,cash2Config);
//        return ResponseEntityBuilder.success(usrBankService.getUserBankList(baseRequest));
        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(usrBankService.getCash2UserBankList(cash2UserBankRequest));
    }



    @ApiOperation("拉取Cash2卡绑卡状态")
    @RequestMapping(value = "/getCash2BankCardStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Cash2Response getCash2BankCardStatus(@RequestBody Cash2ApiParam request)
            throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, userBankRequest);
//        log.info("request body{}", JsonUtils.serialize(userBankRequest));
        log.info("Cash2卡bin校验");
        Cash2UserBankRequest cash2UserBankRequest = request.getDecryptData(Cash2UserBankRequest.class,cash2Config);
        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1).withData(usrBankService.getCash2BankCardStatus(cash2UserBankRequest,redisClient));
    }

    @ApiOperation("放款失败后重新绑卡")
    @RequestMapping(value = "/bankcard-rebinding", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Cash2Response rebindBankCard(@RequestBody Cash2ApiParam request)
            throws ServiceException {
        Cash2UserBankRequest cash2UserBankRequest = request.getDecryptData(Cash2UserBankRequest.class,cash2Config);
        try {
            return cash2BankService.rebindBankCard(cash2UserBankRequest);
        } catch (ServiceException e) {
            log.error("rebind bank card server exception", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.PARAM_TYPE_ERROR_1002)
                    .withMessage(e.getMessage());
        } catch (Exception e) {
            log.error("apply loan error", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.PARAM_TYPE_ERROR_1002)
                    .withMessage("server error");
        }

    }


    @ApiOperation("放款失败后重新绑卡")
    @RequestMapping(value = "/bankcard-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Cash2Response getBankCardStatus(@RequestBody Cash2ApiParam request){
        Cash2BankStatusRequest req = request.getDecryptData(Cash2BankStatusRequest.class,cash2Config);
        try {
            return cash2BankService.getBankStatus(req);
        } catch (Exception e) {
            log.error("apply loan error", e);
            return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.PARAM_TYPE_ERROR_1002)
                    .withMessage("server error");
        }

    }





}
