package com.yqg.manage.controller.system;

import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.system.DataFixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/**
 * Author: tonggen
 * Date: 2019/5/30
 * time: 2:25 PM
 */
@RestController
@RequestMapping("/manage/dataFix/")
public class DataFixController {

    @Autowired
    private DataFixService dataFixService;

    /**
     * 修复质检语音文件下载
     * @return
     * @throws ServiceExceptionSpec
     */
    @RequestMapping(value = "/uploadVoiceCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<String> uploadVoiceCheck(@RequestParam("fileName") String fileName)
            throws ServiceExceptionSpec{

        return ResponseEntitySpecBuilder.success(dataFixService.uploadVoiceCheck(fileName));

    }


}
