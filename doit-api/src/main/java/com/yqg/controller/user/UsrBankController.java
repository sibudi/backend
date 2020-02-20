package com.yqg.controller.user;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.response.UsrBankResponse;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.service.user.service.UsrService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 用户银行
 * Created by luhong on 2017/11/25.
 */
@RestController
@Slf4j
@RequestMapping("/userBank")
public class UsrBankController {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UsrBankService usrBankService;
    @Autowired
    private UsrService usrService;

    @ApiOperation("卡bin校验")
    @RequestMapping(value = "/checkBankCardBin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> checkBankCardBin(HttpServletRequest request, @RequestBody UsrBankRequest userBankRequest)
            throws Exception, ServiceException, ServiceExceptionSpec {
//        UserSessionUtil.filter(request, this.redisClient, userBankRequest);
//        log.info("request body{}", JsonUtils.serialize(userBankRequest));
        log.info("卡bin校验");
        if(!usrService.isMobileValidated(userBankRequest.getUserUuid())){
                throw new ServiceException(ExceptionEnum.INVALID_ACTION);
        }
        usrBankService.bindBankCard(userBankRequest,redisClient);
        //绑卡后检查whatsapp
        usrService.addCheckIziWhatsAppTask(userBankRequest.getUserUuid());

        //保存用户设备指纹
        usrService.saveTongDunBlackBox(userBankRequest);

        return ResponseEntityBuilder.success();
    }

    @ApiOperation("获取用户银行卡列表")
    @RequestMapping(value = "/getUserBankList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<UsrBankResponse>> getUserBankList(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
            throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, baseRequest);
//        log.info("request body{}", JsonUtils.serialize(baseRequest));
        log.info("获取用户银行卡列表");
        return ResponseEntityBuilder.success(usrBankService.getUserBankList(baseRequest));
    }

    @ApiOperation("更换用户订单相关银行卡")
    @RequestMapping(value = "/changeOrderBankCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<UsrBankResponse>> changeOrderBankCard(HttpServletRequest request, @RequestBody UsrBankRequest userBankRequest)
            throws ServiceException {
//        UserSessionUtil.filter(request, this.redisClient, userBankRequest);
//        log.info("request body{}", JsonUtils.serialize(userBankRequest));
        log.info("更换用户订单相关银行卡");
        usrBankService.changeOrderBankCard(userBankRequest);
        return ResponseEntityBuilder.success();
    }

}
