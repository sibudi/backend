package com.yqg.controller.p2p;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.loan.request.RepayPlanRequest;
import com.yqg.service.order.OrdService;
import com.yqg.service.p2p.request.CheckOrderInfoRequest;
import com.yqg.service.p2p.request.CheckUserIsHaveInvestRequest;
import com.yqg.service.p2p.request.LoanQueryRequest;
import com.yqg.service.p2p.request.QueryUserInfoReqeust;
import com.yqg.service.p2p.response.CheckUserIsHaveInvestResponse;
import com.yqg.service.p2p.response.P2PResponse;
import com.yqg.service.p2p.response.P2PResponseBuiler;
import com.yqg.service.p2p.response.P2PResponseCode;
import com.yqg.service.p2p.service.P2PService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrUser;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by wanghuaizhou on 2018/9/6.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/p2p")
public class P2PController {

    @Autowired
    private P2PService p2PService;

    @Autowired
    private OrdService ordService;
    @Autowired
    private UsrService usrService;

    @ApiOperation("查看用户是否有在借款")
    @RequestMapping(value = "/checkUserIsHaveLoan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<CheckUserIsHaveInvestResponse> checkUserIsHaveLoan(HttpServletRequest request, @RequestBody CheckUserIsHaveInvestRequest checkUserIsHaveInvestRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.successForP2P(this.p2PService.checkUserIsHaveLoan(checkUserIsHaveInvestRequest));
    }

    @ApiOperation("通过借款人ID查询借款用户信息")
    @RequestMapping(value = "/queryUserInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> queryUserInfo(HttpServletRequest request, @RequestBody QueryUserInfoReqeust queryUserInfoReqeust)
            throws Exception {

        try {
            return ResponseEntitySpecBuilder.successForP2P(this.p2PService.queryUserInfoByUserId(queryUserInfoReqeust));
        }catch (ServiceException e) {
            log.error("queryUserInfo service exception", e);
            return  ResponseEntitySpecBuilder.faildForP2P(e.getMessage());
        } catch (Exception e) {
            log.error("queryUserInfo exception", e);
            return ResponseEntitySpecBuilder.faildForP2P();
        }
    }

    @ApiOperation("标的状态推送--回调")
    @RequestMapping(value = "/getOrderStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<P2PResponse> getOrderStatus(HttpServletRequest request, @RequestBody CheckOrderInfoRequest checkOrderInfoRequest)
            throws Exception {
        try {
            this.p2PService.handleP2PLoanStatus(checkOrderInfoRequest.getCreditorNo(),
                    checkOrderInfoRequest.getStatus(), null);
            return ResponseEntitySpecBuilder.successForP2P(P2PResponseBuiler.buildResponse(P2PResponseCode.CODE_OK_1));
        }catch (ServiceException e) {
            log.error("getOrderStatus service exception", e);
            return  ResponseEntitySpecBuilder.faildForP2P(e.getMessage());
        } catch (Exception e) {
            log.error("getOrderStatus exception", e);
            return ResponseEntitySpecBuilder.faildForP2P();
        }

    }

    @ApiOperation("推标")
    @RequestMapping(value = "/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<P2PResponse> test(HttpServletRequest request, @RequestBody CheckOrderInfoRequest checkOrderInfoRequest)
            throws Exception {
        try {
            // 推标
//            OrdOrder order = this.ordService.getOrderByOrderNo("011808221636013260");
//            UsrUser user = this.usrService.getUserByUuid("EA7F8CE9CF5B45CEA33BD9756AFD1DD3");
//            P2PResponse response = this.p2PService.sendOrderInfoToFinancial(order,user);
//            if (response.getCode() == 0){
//                return ResponseEntitySpecBuilder.successForP2P(response);
//            }else {
//                return ResponseEntitySpecBuilder.faildForP2P(response.getMessage());
//            }

//            // 查询标状态
//            OrdOrder order = this.ordService.getOrderByOrderNo("011808221636013260");
//            P2PResponse response = this.p2PService.checkOrderInfo(order);
//            if (response.getCode() == 0){
//                return ResponseEntitySpecBuilder.successForP2P(response);
//            }else {
//                return ResponseEntitySpecBuilder.faildForP2P(response.getMessage());
//            }

            // 查询用户是否在投
            UsrUser user = this.usrService.getUserByUuid("EA7F8CE9CF5B45CEA33BD9756AFD1DD3");
            P2PResponse response = this.p2PService.checkUserIsHaveInvest(user);
            Map<String,Object> data = (Map<String, Object>) response.getData();
            String isExit = data.get("isExist").toString();
            log.info("isExit"+isExit);
            return ResponseEntitySpecBuilder.successForP2P();
//            if (response.getCode() == 0){
//                return ResponseEntitySpecBuilder.successForP2P(response);
//            }else {
//                return ResponseEntitySpecBuilder.faildForP2P(response.getMessage());
//            }
        }catch (ServiceException e) {
            log.error("getOrderStatus service exception", e);
            return  ResponseEntitySpecBuilder.faildForP2P(e.getMessage());
        } catch (Exception e) {
            log.error("getOrderStatus exception", e);
            return ResponseEntitySpecBuilder.faildForP2P();
        }

    }

    @ApiOperation("通过手机号查询已经还款的订单")
    @RequestMapping(value = "/settled-loan-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getLoanListByMobile(@RequestBody LoanQueryRequest request) {
        try {
            return ResponseEntitySpecBuilder.successForP2P(this.p2PService.getLoanListByMobile(request.getMobileNumber()));
        } catch (Exception e) {
            log.error("getLoanListByMobile exception", e);
            return ResponseEntitySpecBuilder.faildForP2P();
        }
    }


    @ApiOperation("查询分期订单的还款计划")
    @RequestMapping(value = "/getRepayPlan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<RepayPlanRequest> getRepayPlan(HttpServletRequest request, @RequestBody CheckOrderInfoRequest checkOrderInfoRequest) {
        try {
            return ResponseEntitySpecBuilder.successForP2P(this.p2PService.getRepayPlan(checkOrderInfoRequest.getCreditorNo()));
        }catch (ServiceException e) {
            log.error("getRepayPlan exception", e);
            return ResponseEntitySpecBuilder.faildForP2P(e.getMessage());
        } catch (Exception e) {
            log.error("getRepayPlan exception", e);
            return ResponseEntitySpecBuilder.faildForP2P();
        }
    }
}
