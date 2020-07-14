package com.yqg.controller.system;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.order.entity.ExternalAnalytic;
import com.yqg.service.system.request.ExternalAnalyticRequest;
import com.yqg.service.system.service.ExternalAnalyticService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.ws.rs.core.MediaType;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Janhsen on 2019/11/22.
 */
@RestController
@Slf4j
public class ExternalAnalyticController {

    @Autowired
    private ExternalAnalyticService analyticService;

    @ApiOperation("add info from user action")
    @RequestMapping(value = "/externalAnalytics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> AddExternalAnalytic(@RequestBody ExternalAnalytic analyticRequest)
            throws ServiceException {
        log.info("invoke add AddExternalAnalytic");
        analyticRequest.setEventAttribute("");;
        analyticService.Insert(analyticRequest);
        return ResponseEntityBuilder.success();
    }
}
