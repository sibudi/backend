package com.yqg.manage.controller.mongo;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.mongo.OrderUserContactMongoService;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * @author Jacob
 */
@H5Request
@RestController
public class OrderUserContactMongoController {
    @Autowired
    private OrderUserContactMongoService orderUserContactMongoService;

    @ApiOperation("通过用户的uuid在Mongo中查询客户的通讯录")
    @RequestMapping(value = "/manage/orderUserContactMongoByOrderNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> orderUserContactMongoByOrderNo(@RequestBody OrderMongoRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.getOrderUserContactByOrderNo(request));
    }

    @ApiOperation("查询客户的通讯记录")
    @RequestMapping(value = "/manage/orderUserCallRecordMongoByUuid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> orderUserCallRecordMongoByUuid(@RequestBody OrderMongoRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.orderUserCallRecordMongoByUuid(request));
    }

    @ApiOperation("获得两个常用联系人(2018-08-23改成使用紧急联系人)")
    @RequestMapping(value = "/manage/frequentOrderUserCallRecordMongo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> frequentOrderUserCallRecordMongo(@RequestBody OrderMongoRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.frequentOrderUserCallRecordMongo(request));
    }

    @ApiOperation("获得家庭主妇收入来源")
    @RequestMapping(value = "/manage/getHouseWifiInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getHouseWifiInfo(@RequestBody OrderMongoRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.getHouseWifiInfo(request));
    }

//    @ApiOperation("获得两个常用联系人(提供给祥才使用的接口)")
//    @RequestMapping(value = "/manage/frequentOrderUserCallRecordMongo1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//    @ResponseBody
//    public ResponseEntitySpec<Object> frequentOrderUserCallRecordMongo1(@RequestBody OrderMongoRequest request) throws Exception {
//        return ResponseEntitySpecBuilder.success(this.orderUserContactMongoService.frequentOrderUserCallRecordMongo1(request));
//    }
}
