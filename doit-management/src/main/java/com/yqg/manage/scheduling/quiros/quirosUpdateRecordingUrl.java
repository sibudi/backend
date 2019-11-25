package com.yqg.manage.scheduling.quiros;
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

@RestController
@RequestMapping("/manage")
public class quirosUpdateRecordingUrl {
    @Autowired
    private QuirosService quirosService;

    @RequestMapping(value = "/quirosUpdateRecordingUrl", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> quirosUpdateRecordingUrl() throws ServiceExceptionSpec, IOException{
        boolean result = quirosService.quirosUpdateRecordingUrl(); 
        return ResponseEntitySpecBuilder.success(result);
    }
}