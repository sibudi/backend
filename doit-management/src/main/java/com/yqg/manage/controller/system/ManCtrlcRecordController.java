package com.yqg.manage.controller.system;

import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.system.ManCtrlcRecordService;
import com.yqg.manage.service.system.request.ManCtrlcRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/**
 * 记录用户复制动作
 * Author: tonggen
 * Date: 2019/5/14
 * time: 10:56 AM
 */
@RestController
public class ManCtrlcRecordController {

    @Autowired
    private ManCtrlcRecordService manCtrlcRecordService;

    /**
     * 插入一条记录
     * @param request
     * @return
     * @throws ServiceExceptionSpec
     */
    @RequestMapping(value = "/manage/insertManCtrlcRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Integer> insertManCtrlcRecord(@RequestBody ManCtrlcRecordRequest request)
            throws ServiceExceptionSpec{

        return ResponseEntitySpecBuilder.success(manCtrlcRecordService.insertManCtrlcRecord(request));

    }

}
