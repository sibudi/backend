package com.yqg.manage.controller.order;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.order.ManOrderHistoryService;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.order.entity.OrdHistory;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author alan
 */
@RestController
@H5Request
public class ManOrderHistoryController {
    @Autowired
    private ManOrderHistoryService manOrderHistoryService;

    @ApiOperation("orderSchedule")
    @RequestMapping(value = "/manage/orderSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<OrdHistory>> orderSchedule(@RequestBody ManOrderListSearchResquest orderListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderHistoryService.orderSchedule(orderListSearchResquest));
    }

}
