package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.CashCashRequest;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2GetOrdStatusRequest;
import com.yqg.service.externalChannel.request.Cash2GetRepayInfoRequest;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wanghuaizhou on 2018/3/8.
 */
@CashCashRequest
@RestController
@RequestMapping("/external/cash2")
public class Cash2OrderController {

      @Autowired
      Cash2OrderService cash2OrderService;

      @Autowired
      private Cash2Config cash2Config;

      /***
       * 订单状态拉取
       * @param request
       * @return
       */
      @RequestMapping(value = "/getOrdStatus", method = RequestMethod.POST)
      public Cash2Response getOrdStatus(@RequestBody Cash2ApiParam request) {

            return this.cash2OrderService.getOrdStatus(request.getDecryptData(Cash2GetOrdStatusRequest.class,cash2Config));
      }

      /***
       * 订单审核结果拉取
       * @param request
       * @return
       */
      @RequestMapping(value = "/getOrdCheckResult", method = RequestMethod.POST)
      public Cash2Response getOrdCheckResult(@RequestBody Cash2ApiParam request) {

            return this.cash2OrderService.getOrdCheckResult(request.getDecryptData(Cash2GetOrdStatusRequest.class,cash2Config));
      }

      /***
       * 拉取还款计划接口
       * @param request
       * @return
       */
      @RequestMapping(value = "/getOrdRepayPlan", method = RequestMethod.POST)
      public Cash2Response getOrdRepayPlan(@RequestBody Cash2ApiParam request) {

            return this.cash2OrderService.getOrdRepayPlan(request.getDecryptData(Cash2GetOrdStatusRequest.class,cash2Config));
      }

      /***
       * 还款详情信息接口
       * @param request
       * @return
       */
      @RequestMapping(value = "/getRepayDetailInfo", method = RequestMethod.POST)
      public Cash2Response getRepayDetailInfo(@RequestBody Cash2ApiParam request) {

            return this.cash2OrderService.getRepayDetailInfo(request.getDecryptData(Cash2GetRepayInfoRequest.class,cash2Config));
      }


      /***
       * 降额确认接口
       * @param request
       * @return
       */
      @RequestMapping(value = "/confirmation", method = RequestMethod.POST)
      public Cash2Response confirmation(@RequestBody Cash2ApiParam request) {

            return this.cash2OrderService.confirmation(request.getDecryptData(Cash2GetOrdStatusRequest.class,cash2Config));
      }
}
