package com.yqg.manage.controller.order;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.order.ManOrderUserDataService;
import com.yqg.manage.service.order.request.ManOrderUserRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "")
public class ManOrderUserDataController {
    @Autowired
    private ManOrderUserDataService manOrderUserDataService;

    @ApiOperation("根据订单uuid和类型查询订单用户信息")
    @RequestMapping(value = "/manage/orderUserDataMongo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getOrderUserDataMongo(@RequestBody ManOrderUserRequest orderListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderUserDataService.getOrderUserDataMongo(orderListSearchResquest));
    }

    @ApiOperation("根据订单uuid和类型查询订单用户信息")
    @RequestMapping(value = "/manage/orderUserDataSql", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getOrderUserDataSql(@RequestBody ManOrderUserRequest orderListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderUserDataService.getOrderUserDataSql(orderListSearchResquest));
    }

    @ApiOperation("获取活体识别分")
    @RequestMapping(value = "/manage/getPairVerifySimilarity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getPairVerifySimilarity(@RequestBody ManOrderUserRequest orderListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderUserDataService.getPairVerifySimilarity(orderListSearchResquest));
    }
}