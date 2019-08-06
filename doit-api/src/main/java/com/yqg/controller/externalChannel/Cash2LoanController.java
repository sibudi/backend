package com.yqg.controller.externalChannel;


import com.yqg.common.annotations.CashCashRequest;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.*;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.service.Cash2LoanService;
import com.yqg.service.externalChannel.service.Cash2ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/5
 * @Email zengxiangcai@yishufu.com
 * 贷款信息
 *
 ****/

@CashCashRequest
@RestController
@RequestMapping("/external/cash2")
public class Cash2LoanController {

    @Autowired
    private Cash2LoanService loanService;

    @Autowired
    private Cash2Config cash2Config;

    @Autowired
    private Cash2ProductService cash2ProductService;

    /***
     * 检查是否可贷款
     * @param request
     * @return
     */
    @RequestMapping(value = "/loanable", method = RequestMethod.POST)
    public Cash2Response checkUserLoanable(@RequestBody Cash2ApiParam request) {

        return loanService.checkLoanable(request.getDecryptData(Cash2LoanableCheckParam.class,cash2Config));
    }


    /***
     * 贷款确认
     * @param request
     * @return
     */
    @RequestMapping(value = "/application-confirmation", method = RequestMethod.POST)
    public Cash2Response confirmLoanApplication(@RequestBody Cash2ApiParam request) throws Exception{

        return loanService
            .confirmApplication(request.getDecryptData(Cash2LoanConfirmationParam.class,cash2Config));
    }

    /***
     * 拉取借款金额和借款周期接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/getProductConfig", method = RequestMethod.POST)
    public Cash2Response getProductConfig(@RequestBody Cash2ApiParam request) throws Exception{

        return cash2ProductService
                .getProductConfig(request.getDecryptData(Cash2GetProductConfigRequest.class,cash2Config));
    }


    /***
     * 获取利息、管理费、还款总额等信息接口接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/getDetailProductInfo", method = RequestMethod.POST)
    public Cash2Response getDetailProductInfo(@RequestBody Cash2ApiParam request) throws Exception{

        return cash2ProductService
                .getDetailProductInfo(request.getDecryptData(Cash2GetDetailProductInfoRequest.class,cash2Config));
    }


//    /***
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/getDetailProductInfo", method = RequestMethod.POST)
//    public Cash2Response getDetailProductInfo(@RequestBody Cash2ApiParam request) throws Exception{
//
//        return cash2ProductService
//                .getDetailProductInfo(request.getDecryptData(Cash2GetDetailProductInfoRequest.class,cash2Config));
//    }
}
