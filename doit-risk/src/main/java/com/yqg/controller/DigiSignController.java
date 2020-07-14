package com.yqg.controller;

import javax.ws.rs.core.MediaType;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.externalChannel.utils.CustomHttpResponse;
import com.yqg.service.signcontract.ContractSignService;
import com.yqg.service.signcontract.request.SignConfirmRequest;
import com.yqg.service.signcontract.response.SignInfoResponse;
import com.yqg.service.third.digSign.DigiSignService;
import com.yqg.service.third.digSign.reqeust.SignContractBulkRequest;
import com.yqg.service.third.digSign.response.DocumentStatusResponse;
import com.yqg.service.third.digSign.response.SignContractBulkResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/digisign")
public class DigiSignController {

    @Autowired
    private ContractSignService contractSignService;
    @Autowired
    private DigiSignService digiSignService;

    @RequestMapping(value = "/sign-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public SignInfoResponse getSignInfo(@RequestBody SignConfirmRequest request) throws Exception {
        try {
            SignInfoResponse response = contractSignService.getUserCurrentSignData(request.getUserUuid(), request.getOrderNo());
            return response;
        } catch (ServiceException e) {
            log.error("confirmSignContract error, param: "+ JsonUtils.serialize(request),e);
            throw e;
        } catch (Exception e1) {
            log.info("confirmation sign contract exception, orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        } 
    }


    @RequestMapping(value = "/bulk-sign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public SignContractBulkResponse signContractBulk(@RequestBody SignContractBulkRequest request) throws Exception {

        SignContractBulkResponse signContractBulkResponse = new SignContractBulkResponse();

        try {
            CustomHttpResponse response = digiSignService.signContractBulk(request.getOrderNo());
            signContractBulkResponse = JsonUtils.deserialize(response.getContent(), SignContractBulkResponse.class);
        } catch (Exception e1) {
            log.info("digisign-bulk-sign exception, orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }
        return signContractBulkResponse;
    }


    @RequestMapping(value = "/send-document", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public boolean sendDocument(@RequestBody SignConfirmRequest request) {
        
        boolean sendDocumentResponse = digiSignService.sendDocument(request.getOrderNo());
        return sendDocumentResponse;
    }


    @RequestMapping(value = "/check-document-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public DocumentStatusResponse checkDocumentStatus(@RequestBody SignConfirmRequest request) throws Exception {
        
        DocumentStatusResponse documentResponse = new DocumentStatusResponse();
        //DocumentStatusDetail documentDetailResponse = new DocumentStatusDetail();

        try {

            CustomHttpResponse response = digiSignService.checkDocumentStatus(request.getOrderNo());
            documentResponse = JsonUtils.deserialize(response.getContent(), DocumentStatusResponse.class);
            
            log.info("waiting to signed status: {}, waiting email: {}", documentResponse.getJSONFile().getStatus(), documentResponse.getJSONFile().getWaiting().get(0).getEmail());
        } catch (Exception e1) {

            log.info("check-document-status exception, orderNo: " + request.getOrderNo(), e1);
            throw new ServiceException(ExceptionEnum.SYSTEM_TIMEOUT);
        }

        return documentResponse;
    }
}