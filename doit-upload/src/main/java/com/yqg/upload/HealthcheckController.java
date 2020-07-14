package com.yqg.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;


/**
 * Created by arief.halim on 2020.06.11
 */
@RestController
@Slf4j
public class HealthcheckController {

    @RequestMapping(value = "/doit-upload/healthcheck", method = RequestMethod.HEAD)
    @ResponseBody
    public ResponseEntity<Object> healthcheck(){
        return ResponseEntityBuilder.success();
    }

}
