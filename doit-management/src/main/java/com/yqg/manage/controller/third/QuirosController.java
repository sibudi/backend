package com.yqg.manage.controller.third;

import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.third.quiros.QuirosService;
import com.yqg.service.third.quiros.request.QuirosCallRequest;
import com.yqg.service.third.quiros.request.QuirosReportRequest;
import com.yqg.service.third.quiros.request.QuirosReportRequestByDocId;

import java.io.IOException;
import com.alibaba.druid.support.json.JSONUtils;
import okhttp3.OkHttpClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("/manage")
public class QuirosController {
    
    @Autowired
    private QuirosService quirosService;
    
    @RequestMapping(value = "/quirosClick2Call", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> postClick2Call(@RequestBody QuirosCallRequest request) throws ServiceExceptionSpec, IOException{
        String responseStr =  quirosService.actionCall2Click(request);
        return ResponseEntitySpecBuilder.success(responseStr);
    }
    
    @RequestMapping(value = "/quirosResultCallByDateRange", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> getResultCallByDateRange(QuirosReportRequest reportRequest) throws ServiceExceptionSpec, IOException{
        String responseStr = quirosService.actionResultCallByDateRange(reportRequest); 
        return ResponseEntitySpecBuilder.success(JSONUtils.parse(responseStr));
    }
    
    @RequestMapping(value = "/quirosResultCallById", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> getResultCallById(QuirosReportRequest reportRequest) throws ServiceExceptionSpec, IOException{
        String responseStr = quirosService.actionResultCallById(reportRequest); 
        return ResponseEntitySpecBuilder.success(JSONUtils.parse(responseStr));
    }

    @RequestMapping(value = "/getResultByDocID", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> getresultcallbyid(QuirosReportRequestByDocId QuirosReportRequestByDocId) throws ServiceExceptionSpec, IOException{
        String responseStr = quirosService.actionResultCallByDocId(QuirosReportRequestByDocId); 
        return ResponseEntitySpecBuilder.success(JSONUtils.parse(responseStr));
    }
}
