package com.yqg.manage.controller.mongo;

import javax.ws.rs.core.MediaType;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.mongo.OrderUserContactMongoService;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * @author Jacob
 */
@H5Request
@RestController
public class OrderUserContactMongoController {
    @Autowired
    private OrderUserContactMongoService orderUserContactMongoService;

    @ApiOperation("获得两个常用联系人(2018-08-23改成使用紧急联系人)") //Obtain two frequently used contacts (2018-08-23 changed to use emergency contacts)
    @RequestMapping(value = "/manage/getOrderEmergencyContact", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getOrderEmergencyContact(@RequestBody OrderMongoRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.getOrderEmergencyContact(request));
    }

    @ApiOperation("获得家庭主妇收入来源")
    @RequestMapping(value = "/manage/getHouseWifiInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getHouseWifiInfo(@RequestBody OrderMongoRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.getHouseWifiInfo(request));
    }

}
