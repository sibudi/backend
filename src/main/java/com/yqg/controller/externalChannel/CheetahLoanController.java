package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.CheetahRequest;
import com.yqg.service.externalChannel.request.CheetahBaseRequest;
import com.yqg.service.externalChannel.request.CheetahCheckLoanParam;
import com.yqg.service.externalChannel.request.CheetahGetOrdStatusRequest;
import com.yqg.service.externalChannel.request.CheetahLoanConfirmationParam;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.service.CheetahLoanService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by wanghuaizhou on 2018/12/26.
 */
@CheetahRequest
@RestController
@RequestMapping("/cash-loan/v1")
public class CheetahLoanController {

    @Autowired
    private CheetahLoanService cheetahLoanService;

    @Autowired
    private CheetahOrderService cheetahOrderService;

    /***
     * 检查是否可贷款
     * @return
     */
    @RequestMapping(value = "/users/pre-audit-status", method = RequestMethod.GET)
    public CheetahResponse checkUserLoanable(CheetahCheckLoanParam request)  throws Exception{

        return cheetahLoanService.checkLoanable(request);
    }

    /***
     * 贷款确认
     * @param request
     * @return
     */
    @RequestMapping(value = "/orders/create", method = RequestMethod.POST)
    public CheetahResponse confirmLoanApplication(@RequestBody CheetahLoanConfirmationParam request) throws Exception{

        return cheetahLoanService
                .confirmApplication(request);
    }

    /***
     * 拉取订单信息接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/orders/order-info", method = RequestMethod.GET)
    public CheetahResponse getOrdInfo( CheetahGetOrdStatusRequest request) throws Exception{

        return cheetahOrderService
                .getOrdStatus(request);
    }

    /***
     * 订单信息反馈接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/orders/repayment-info", method = RequestMethod.GET)
    public CheetahResponse getRepaymentInfo( CheetahGetOrdStatusRequest request) throws Exception{

        return cheetahOrderService
                .getRepaymentInfo(request);
    }


    /***
     * 金融产品信息接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public CheetahResponse getProductConfig( CheetahBaseRequest request) throws Exception{
        return cheetahOrderService
                .getProductConfig();
    }

}
