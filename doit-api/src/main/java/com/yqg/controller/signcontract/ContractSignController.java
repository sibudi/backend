package com.yqg.controller.signcontract;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.signcontract.ContractSignService;
import com.yqg.service.signcontract.request.SignConfirmRequest;
import com.yqg.service.signcontract.response.SignInfoResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/***
 * 签约相关
 */
@RestController
@Slf4j
@RequestMapping(value = "/contract-signature")
public class ContractSignController {

    @Autowired
    private ContractSignService contractSignService;

    @ApiOperation("用户激活回调")
    @RequestMapping(value = "/user-activation-confirmation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> confirmUserActivation(@RequestBody SignConfirmRequest request) throws Exception {
        try {
            contractSignService.confirmUserActivation(request.getUserUuid(), request.getResponse());
        } catch (Exception e1) {
            log.info("user-activation-confirmation exception,orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
        return ResponseEntityBuilder.success();
    }
    @ApiOperation("签约激活回调")
    @RequestMapping(value = "/sign-confirmation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> confirmSignContract(@RequestBody SignConfirmRequest request) throws Exception {
        try {
            contractSignService.confirmSignContract(request.getOrderNo(), request.getResponse());
        } catch (ServiceException e){
            log.error("confirmSignContract error, param: "+ JsonUtils.serialize(request),e);
            throw e;
        }catch (Exception e1) {
            log.info("confirmation sign contract exception,orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("查询用户当前签约状态，待激活，待签约，待发送文档等")
    @RequestMapping(value = "/sign-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getSignInfo(@RequestBody SignConfirmRequest request) throws Exception {
        try {
            SignInfoResponse response = contractSignService.getUserCurrentSignData(request.getUserUuid(), request.getOrderNo());
            return ResponseEntityBuilder.success(response);
        } catch (ServiceException e){
            log.error("confirmSignContract error, param: "+ JsonUtils.serialize(request),e);
            throw e;
        }catch (Exception e1) {
            log.info("confirmation sign contract exception,orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
    }
}
