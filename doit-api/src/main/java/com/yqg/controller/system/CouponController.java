package com.yqg.controller.system;

import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.service.system.service.CouponService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * Created by wanghuaizhou on 2019/5/15.
 */
@RestController
@RequestMapping("/coupon")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    @ApiOperation("获取优惠券列表")
    @RequestMapping(value = "/getCouponList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> initHomeView(HttpServletRequest request, @RequestBody BaseRequest baseRequest)
            throws ServiceException {
        log.info("获取优惠券列表");
        // TODO 加解密
        return ResponseEntityBuilder.success(couponService.getCouponList(baseRequest));
    }

}
